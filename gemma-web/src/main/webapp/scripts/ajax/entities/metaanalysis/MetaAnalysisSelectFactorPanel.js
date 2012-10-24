/**
 * Panel for selecting factors of experiments  
 * 
 * @author frances
 * @version $Id$
 */
Ext.namespace('Gemma');

Gemma.MetaAnalysisSelectFactorPanel = Ext.extend(Gemma.WizardTabPanelItemPanel, {
	nextButtonText: 'Run meta-analysis',
	initComponent: function() {
		var experimentSelectedCount = 0;
		
		var nextButton = this.createNextButton();
		nextButton.disable();

		// Assume that if this.metaAnalysis is not null, result sets are shown
		// for viewing only. So, editing is not allowed. 
		var generateExperimentComponents = function(experimentDetails) {
			var radioGroup = new Ext.form.RadioGroup({
			    items: []
			});
			
			var experimentTitle = '<b>' + experimentDetails.accession + ' ' + experimentDetails.name + '</b>';

			var experimentTitleComponent;
			if (this.metaAnalysis) {
				experimentTitleComponent = new Ext.form.DisplayField({
					style: 'margin: 10px 0 0 20px;', // DisplayField instead of Label is used. Otherwise, top margin is not honored. 
					html: experimentTitle
				});
			} else {
				experimentTitleComponent = new Ext.form.Checkbox({
	        		style: 'margin: 10px 0 0 10px;',
					boxLabel: experimentTitle,
					listeners: {
						check: function(checkbox, checked) {
							if (checked) {
								experimentSelectedCount++;
	
								if (radioGroup.getValue() == null) {
									for (var i = 0; i < radioGroup.items.length; i++) {
										if (!radioGroup.items[i].disabled) {
											radioGroup.items[i].setValue(true);
											break;
										}
									}
								}
							} else {
								experimentSelectedCount--;
								
								radioGroup.reset();
							}
							
							nextButton.setDisabled(experimentSelectedCount < 2);
						}
					}
        		});
			}
        	
        	var experimentResultSetsPanel = new Ext.Panel({
				bodyStyle: 'background-color: transparent; padding: 0 0 20px 40px;',				
        		border: false
        	});
        	
			var totalResultSetCount = 0;
        	
			if (experimentDetails.differentialExpressionAnalyses.length == 0) {
				experimentResultSetsPanel.add(new Ext.form.Label({
					style: 'font-style: italic; ',
					disabled: true,
					html: 'No differential expression analysis available' + '<br />'
				}));
			} else {
				var analysesSummaryTree = new Gemma.DifferentialExpressionAnalysesSummaryTree({
			    	experimentDetails: experimentDetails,
			    	editable: false,
			    	style: 'padding-bottom: 20px;'
			    });
			    
				var generateResultSetComponent = function(text, marginLeft, notSuitableForAnalysisMessage, inputValue, shouldResultSetSelected) {
					// When users click on any icons following the text for each result set, radio buttons should not
					// be selected. Assume this text ends right before the first html tag span. indexOfFirstSpan is
					// used to store the start index of the first html tag span. Radio button will be created using
					// this text only. However, if a radio button will be disabled, this radio button will use both
					// this text and html code for all icons because these icons should look disabled.
					var indexOfFirstSpan = text.indexOf('<span');
					var radio = new Ext.form.Radio({
						checked: shouldResultSetSelected,
						boxLabel: (notSuitableForAnalysisMessage ?
									text + ' <i>' + notSuitableForAnalysisMessage + '</i>' :
									text.substring(0, indexOfFirstSpan)),
						name: (this.metaAnalysis ?
							// Meta-analysis id should be used because another window may have the same set
							// of radio buttons for the same experiment.
							this.metaAnalysis.id + '-' + experimentDetails.id :
							experimentDetails.id),
						style: 'margin-left: ' + marginLeft + 'px;',
						disabled: notSuitableForAnalysisMessage != null,
						inputValue: inputValue,
						listeners: {
							check: function(radio, checked) {
								if (checked) {
									if (experimentTitleComponent.isXType(Ext.form.Checkbox)) {									
										experimentTitleComponent.setValue(true);
									}
								}
							}
						}
					});
					var items = [ radio ];
					if (!notSuitableForAnalysisMessage) {
						// Put all icons in another component placed after the radio button.
						items.push({
							xtype: 'displayfield',
							value: text.substring(indexOfFirstSpan), // text only and without any icons
							style: 'margin-top: -5px;'
						});
					}
					return {
						border: false,
						layout: 'hbox',
						getRadio: function() { return radio; },
					    items: items
					};
			    }.createDelegate(this);
			    
			    var checkSuitableForAnalysis = function(attributes) {
			    	var notSuitableForAnalysisMessage = null;
					if (attributes.numberOfFactors > 1) {
						notSuitableForAnalysisMessage = '(Not suitable - Analysis used more than 1 factor)';
					} else if (attributes.numberOfFactorValues == null || attributes.numberOfFactorValues > 2) {
						notSuitableForAnalysisMessage = '(Not suitable - Analysis based on more than 2 groups)';
					}
					
					return notSuitableForAnalysisMessage;
			    };
			    
				// Sort the tree's child nodes.
				analysesSummaryTree.root.childNodes.sort(function(group1, group2) {
					var strippedText1 = Ext.util.Format.stripTags(group1.text);
					var strippedText2 = Ext.util.Format.stripTags(group2.text);
					
					return (strippedText1 < strippedText2 ?
								-1 :
								strippedText1 > strippedText2 ?
									1 :
									0);
				}); 

				var checkResultSetAvailability = function(analysisId, resultSetId) {
					var shouldResultSetCreated = true;
					var shouldResultSetSelected = false;
					if (this.metaAnalysis) {
						for (var i = 0; i < this.metaAnalysis.includedResultSetDetails.length; i++) {
							var currIncludedResultSetDetails = this.metaAnalysis.includedResultSetDetails[i];
							
							if (currIncludedResultSetDetails.experimentId == experimentDetails.id) {
								shouldResultSetCreated = (currIncludedResultSetDetails.analysisId === analysisId);
								shouldResultSetSelected = (currIncludedResultSetDetails.resultSetId === resultSetId);

								break;
							}
						}
					}
					return {
						shouldResultSetCreated: shouldResultSetCreated,
						shouldResultSetSelected: shouldResultSetSelected
					};
				}.createDelegate(this);
				
				Ext.each(analysesSummaryTree.root.childNodes, function(resultSetParent, unusedI) {
					if (resultSetParent.childNodes.length > 0) {
						var label = new Ext.form.Label({
							html: resultSetParent.text + '<br />'
						});
						experimentResultSetsPanel.add(label);
						
						var currResultSetCount = 0;
						Ext.each(resultSetParent.childNodes, function(resultSet, unusedJ) {
							radioAvailability = checkResultSetAvailability(resultSet.attributes.analysisId, resultSet.attributes.resultSetId);							
							if (radioAvailability.shouldResultSetCreated) {
								var notSuitableForAnalysisMessage = checkSuitableForAnalysis(resultSet.attributes); 
									
								if (notSuitableForAnalysisMessage == null) {
									currResultSetCount++;
									totalResultSetCount++;
								}
								var resultSetComponent = generateResultSetComponent(resultSet.text, 15, notSuitableForAnalysisMessage,
															resultSet.attributes.resultSetId, radioAvailability.shouldResultSetSelected);							
								radioGroup.items.push(resultSetComponent.getRadio());
								experimentResultSetsPanel.add(resultSetComponent);
							}
						},
						this); // scope

						label.setDisabled(currResultSetCount === 0);
					} else {
						radioAvailability = checkResultSetAvailability(resultSetParent.attributes.analysisId, resultSetParent.attributes.resultSetId);							
						if (radioAvailability.shouldResultSetCreated) {
							var notSuitableForAnalysisMessage = checkSuitableForAnalysis(resultSetParent.attributes); 
								
							if (notSuitableForAnalysisMessage == null) {
								totalResultSetCount++;
							}
							var resultSetComponent = generateResultSetComponent(resultSetParent.text, 0, notSuitableForAnalysisMessage,
														resultSetParent.attributes.resultSetId, radioAvailability.shouldResultSetSelected);
							radioGroup.items.push(resultSetComponent.getRadio());						
							experimentResultSetsPanel.add(resultSetComponent);
						}
					}
				},
				this); // scope

				experimentResultSetsPanel.on('afterlayout', function() {
						analysesSummaryTree.drawPieCharts();    
					}, analysesSummaryTree, {
						single: true,
						delay: 100
					});
			} 
			
			if (totalResultSetCount === 0) {
				experimentTitleComponent.setDisabled(true);
			}
			
			return { 
				hasEnabledRadioButtons: (totalResultSetCount > 0),
				experimentTitleComponent: experimentTitleComponent,
				experimentResultSetsPanel: experimentResultSetsPanel
			}
		}.createDelegate(this);
		
		var showExperiments = function(expressionExperimentIds) {
			this.maskWindow();

			analyzableExperimentsPanel.removeAll();
			nonAnalyzableExperimentsPanel.removeAll();
			
			nextButton.setDisabled(true);

			ExpressionExperimentController.loadExpressionExperiments(expressionExperimentIds, function(experiments) {
				var nonAnalyzableExperimentComponents = [];
				
				var addExperimentComponentsToPanel = function(experimentComponents, containerPanel, componentIndex) {
					var panel = new Ext.Panel({
						border: false,
						bodyStyle: (componentIndex % 2 === 0 ?
							'background-color: #FAFAFA;' :
							'background-color: #FFFFFF;')
					});
					panel.add(experimentComponents.experimentTitleComponent);
					panel.add(experimentComponents.experimentResultSetsPanel);
					containerPanel.add(panel);
				};
				
				var i;
				var analyzableExperimentsPanelIndex = 0;

				for (i = 0; i < experiments.length; i++) {
					var experimentComponents = generateExperimentComponents(experiments[i]);
					
					if (experimentComponents.hasEnabledRadioButtons) {
						addExperimentComponentsToPanel(experimentComponents, analyzableExperimentsPanel, analyzableExperimentsPanelIndex);
						analyzableExperimentsPanelIndex++;
					} else {
						nonAnalyzableExperimentComponents.push(experimentComponents);
					}
				}
				
				
				for (var j = 0; j < nonAnalyzableExperimentComponents.length; j++) {
					addExperimentComponentsToPanel(nonAnalyzableExperimentComponents[j], nonAnalyzableExperimentsPanel, j);
				}
				
				
				this.doLayout();
				
				this.unmaskWindow();					
				
			}.createDelegate(this));
		}.createDelegate(this);
		
		var analyzableExperimentsPanel = new Ext.Panel({
			header: (!this.metaAnalysis),
			title: 'Analyzable experiments',
            region: 'center',
			autoScroll: true,
       	 	border: false
		});
		var nonAnalyzableExperimentsPanel = new Ext.Panel({
            title: 'Non-analyzable experiments',
            region: 'south',
			autoScroll: true,
			border: false,

            split: true,
            height: 200
		});

		var setDisabledChildComponentsVisible = function(container, visible) {
			if (container.items && container.items.length > 0) {
				Ext.each(container.items.items, function(item, index) {
					if (item) {
						if (item.items && item.items.length > 0) {
							setDisabledChildComponentsVisible(item, visible);
						} else if (item.disabled &&
								   (item instanceof Ext.form.Checkbox || item instanceof Ext.form.Label)) {
							item.setVisible(visible);
						}
					}
				});
			}
		};
		
		var findSelectedResultSetIds = function(resultSetIds, container) {
			if (container.items && container.items.length > 0) {
				Ext.each(container.items.items, function(item, index) {
					if (item) {
						if (item.items && item.items.length > 0) {
							findSelectedResultSetIds(resultSetIds, item);
						} else if (item instanceof Ext.form.Radio && item.getValue()) {
							resultSetIds.push(item.inputValue);
						}
					}
				});
			}
		};

		var	setPanelReadOnly = function(panel, isReadOnly) {
			panel.items.each(function(item)  {
				if (isReadOnly) {
					item.body.mask();
				} else {
					item.body.unmask();
				}
			});
		}
		
		var buttonPanel = new Ext.Panel({
		 	region: 'south',
		 	border: false,
		 	height: 40,
		 	padding: '10px 0 0 10px',
		 	items: [
		 		nextButton
			]
		 }); 

		var thisPanelItems;		 
		if (this.metaAnalysis) {
			var expressionExperimentIds = [];
			
			Ext.each(this.metaAnalysis.includedResultSetDetails, function(includedResultSetDetail, index) {
				expressionExperimentIds.push(includedResultSetDetail.experimentId);
			});
			
			showExperiments(expressionExperimentIds);
			
			thisPanelItems = [
				analyzableExperimentsPanel
			];
		} else {
			thisPanelItems = [{
					region: 'center',
					layout: 'border',
			 	 	items: [
					 	analyzableExperimentsPanel,
					 	nonAnalyzableExperimentsPanel
					]
				},
				buttonPanel 
			];
		}
		
		this.on({
			afterrender: function() {
				if (this.metaAnalysis) {
					// Defer the call. Otherwise, this panel cannot be set read-only.
					Ext.defer(
						function() {
							this.setPanelReadOnly();
						},
						1000,
						this);
				}
			}
		});		


		Ext.apply(this, {
			height: 600,
			layout: 'border',
			title: (this.metaAnalysis ? 'Selected' : 'Select') + ' factors',			
			getSelectedResultSetIds: function() {
				var selectedResultSetIds = [];

				findSelectedResultSetIds(selectedResultSetIds, this);

				return selectedResultSetIds;
			},
			items: thisPanelItems,
			setSelectedExperimentIds: function(expressionExperimentIds) {
				showExperiments(expressionExperimentIds);
			},
			setPanelReadOnly: function(msg, msgCls) {
				if (analyzableExperimentsPanel.header) {
					analyzableExperimentsPanel.header.mask(msg, msgCls);
				}
				
				setPanelReadOnly(analyzableExperimentsPanel, true);
				
				if (!this.metaAnalysis) {				
					buttonPanel.body.mask();
					setPanelReadOnly(nonAnalyzableExperimentsPanel, true);
				}
			},
			unsetPanelReadOnly: function() {
				analyzableExperimentsPanel.header.unmask();
				buttonPanel.body.unmask();
				
				setPanelReadOnly(analyzableExperimentsPanel, false);
				setPanelReadOnly(nonAnalyzableExperimentsPanel, false);
			}			
		});
		

		if (!this.metaAnalysis) {
			Ext.apply(this, {
				tbar: [{
					xtype: 'checkbox',
					boxLabel: 'Hide non-analyzable experiments and factors',
					listeners: {
						check: function(checkbox, checked) {
							nonAnalyzableExperimentsPanel.setVisible(!checked);
							setDisabledChildComponentsVisible(this, !checked);
							this.doLayout();
						},
						scope: this
					}
				}]
			});
		}

		Gemma.MetaAnalysisSelectFactorPanel.superclass.initComponent.call(this);
	}
});