/**
 * This form lets users create or edit a phenotype association. 
 * 
 * @author frances
 * @version $Id$
 */
Ext.namespace('Gemma.PhenotypeAssociationForm');

Gemma.PhenotypeAssociationForm.ACTION_CREATE = 'create';
Gemma.PhenotypeAssociationForm.ACTION_EDIT = 'edit';

Gemma.PhenotypeAssociationForm.Window = Ext.extend(Ext.Window, {
	layout: 'fit',
	modal: true,
	constrain: true,
	width: 740,
	height: 500,
	shadow: true,
	closeAction: 'hide',
	initComponent: function() {
		var formPanel = new Gemma.PhenotypeAssociationForm.Panel();
		formPanel.on({
			'hide' : function(thisFormPanel) {
				this.hide();
			},
 			scope: this
		});
		this.relayEvents(formPanel, ['phenotypeAssociationChanged']);			

		var showLogInWindow = function(action, data) {
			Gemma.AjaxLogin.showLoginWindowFn();
	
			Gemma.Application.currentUser.on("logIn",
				function(userName, isAdmin) {	
					Ext.getBody().unmask();

					formPanel.setData(action, data);
					this.show();
				},
				this,
				{
					single: true
				});
	    };
		
		Ext.apply(this, {
			listeners: {
				phenotypeAssociationChanged: function() {
					this.hide();
				},
				hide: function(thisWindow) {
					formPanel.resetForm();
				},
				scope: this
			},
			showWindow: function(action, data) {
				if (action === Gemma.PhenotypeAssociationForm.ACTION_CREATE) {
					this.setTitle('Add New Phenotype Association');
				} else if (action === Gemma.PhenotypeAssociationForm.ACTION_EDIT) {
					this.setTitle('Edit Phenotype Association');
				}

				SignupController.loginCheck({
	                callback: function(result){
	                	if (result.loggedIn){
							formPanel.setData(action, data);
							this.show();
	                	}
	                	else{
	                		showLogInWindow.call(this, action, data); 
	                	}
	                }.createDelegate(this)
	            });
			},
			items: [
				formPanel			
			]});
		this.superclass().initComponent.call(this);
	}
});

Gemma.PhenotypeAssociationForm.Panel = Ext.extend(Ext.FormPanel, {
    initComponent: function() {
		var hasError = true;

		// They are null when this form is used for creating evidence.
		var evidenceId = null;		
		var lastUpdated = null;

		var geneSearchComboBox = new Gemma.PhenotypeAssociationForm.GeneSearchComboBox({
			listeners: {
				blur: function(combo) {
					phenotypesSearchPanel.setCurrentGeneNcbiId(combo.getValue() == '' ? null : combo.getValue());
					this.validateForm(false);
				},
				select: function(combo, record, index) {
					this.validateForm(false);
				},
				scope: this
			}
    	});

		var phenotypesSearchPanel = new Gemma.PhenotypeAssociationForm.PhenotypesSearchPanel({
			listeners: {
				blur: function() {
					this.validateForm(false);
				},
				select: function(combo, record, index) {
					this.validateForm(false);
				},
				phenotypeFieldAdded: function() {
//					this.hideErrorPanel();
					this.validateForm(false);
				},
				phenotypeFieldCleared: function() {
//					this.hideErrorPanel();
					this.validateForm(false);
				},
				phenotypeFieldRemoved: function() {
					this.validateForm(false);
				},
				scope: this
			}
		});
    	
    	var literaturePanel = new Gemma.PhenotypeAssociationForm.LiteraturePanel({
			pudMedIdValidator: function() {
				return !hasError;
			},
			listeners: {
				blur: function(numberField) {
					var pubMedId = numberField.getValue();
			
					if (pubMedId === "") {
						this.hideErrorPanel();					
					} else if (pubMedId <= 0) {
						this.showPubMedIdError();
					}
				},
				keypress: function(numberField, event) {
					this.hideErrorPanel();
				},
				load: function(store, records, options) {
			    	if (store.getTotalCount() > 0) {
						this.hideErrorPanel();
						this.validateForm(false);
			    	} else {
						this.showPubMedIdError();
			    	}
				},
				scope: this
			}
    	});
    	
		var errorPanel = new Gemma.PhenotypeAssociationForm.ErrorPanel({
		    region: 'north'	
		});   	

		var evidenceCodeComboBox = new Ext.form.ComboBox({
			hiddenName: 'evidenceCode',
			valueField: 'evidenceCode',
			allowBlank: false,
			mode: 'local',
			store: new Ext.data.ArrayStore({
				fields:  ['evidenceCode', 'evidenceCodeDisplayText'],
				data: [['EXP', Gemma.EvidenceCodes.expText],
						['IC', Gemma.EvidenceCodes.icText],
						['TAS', Gemma.EvidenceCodes.tasText]]
			}),
			value: 'IC',
			forceSelection: true,				
			displayField:'evidenceCodeDisplayText',
			width: 200,
			typeAhead: true,
			triggerAction: 'all',
			selectOnFocus:true,
			listeners: {
				'blur': function(comboBox) { 
					this.validateForm(false);
				},
				scope: this
			},
		    initComponent: function() {
				var originalEvidenceCode = null;

				Ext.apply(this, {
					reset: function() {
						this.superclass().reset.call(this);
					    this.setValue('IC');
					    this.clearInvalid();
					}
				});
				this.superclass().initComponent.call(this);
		    }
		});
    	
		var evidenceTypeComboBox = new Gemma.PhenotypeAssociationForm.EvidenceTypeComboBox({
			listeners: {
				blur: function(comboBox) { 
					this.validateForm(false);
				},
				select: function(combo, record, index) {
					switch (record.data.evidenceClassName) {
						case 'LiteratureEvidenceValueObject':
		//								experimentalFieldSet.hide();
		//								externalDatabaseFieldSet.hide();
							literaturePanel.show();
							break;
					}
				},
				scope: this
			}
		});

		var descriptionTextArea = new Ext.form.TextArea({
			name: 'description',
			fieldLabel: 'Note',
	//		width: 590
			anchor: '96%'
		});

		var isPublicCheckbox = new Ext.form.Checkbox({
			name: "isPublic",
			fieldLabel: "Public",
			value: false
		});

    	Ext.apply(this, {
			url: '/Gemma/processPhenotypeAssociationCreateForm.html',
			layout: 'border',    	
			monitorValid : true,

			items: [
				errorPanel,
				{
					xtype: 'panel',
				    region: 'center',
					layout: 'form',
				    border: false,
					autoScroll: true,
					width: 600,
						defaults: {
							blankText: 'This field is required'
						},
						bodyStyle: 'padding: 5px;',
						items: [
							{
								xtype: 'compositefield',
							    fieldLabel: 'Gene',
							    autoWidth: true,
							    items: [
									geneSearchComboBox,	    
									geneSearchComboBox.getGeneSelectedLabel()
							    ]
							},
							phenotypesSearchPanel,							
							evidenceTypeComboBox,
				//			experimentalFieldSet,
				//			externalDatabaseFieldSet,
							literaturePanel,
							descriptionTextArea,
							{
								xtype: 'compositefield',
								fieldLabel: "Evidence Code",
							    autoWidth: true,
							    items: [
							    	evidenceCodeComboBox,
									{
										xtype: 'displayfield',
										value: '<a class="helpLink" href="javascript: void(0)" onclick="showHelpTip(event, ' +
												'\''+
												'<b>Inferred from Experiment</b><br />An experimental assay has been located in the cited reference, whose results indicate a gene association (or non-association) to a phenotype.<br /><br /><b>Inferred by Curator</b><br />The association between the gene and phenotype is not supported by any direct evidence, but can be reasonably inferred by a curator. This includes annotations from animal models or cell cultures.<br /><br /><b>Traceable Author Statement</b><br />The gene-to-phenotype association is stated in a review paper or a website (external database) with a reference to the original publication.' +
												'\'); return false">' +
												'<img src="/Gemma/images/help.png" /> </a>',
										margins: '4 0 0 0'			
									}
							    ]
							},
							isPublicCheckbox
						]
			}],
			setData: function(action, data) {
				phenotypesSearchPanel.selectPhenotypes(data.phenotypes, data.gene);
				geneSearchComboBox.selectGene(data.gene);
				
				evidenceId = data.evidenceId;
				
				// if we are editing evidence
				if (evidenceId != null) {
					lastUpdated = data.lastUpdated;
			
					evidenceTypeComboBox.selectEvidenceType(data.evidenceClassName);
					
					switch (data.evidenceClassName) {
						case 'LiteratureEvidenceValueObject':
//							literaturePanel.show();
							literaturePanel.setPubMedId(data.pubMedId);
						break;
					}
					
					descriptionTextArea.setValue(data.description);
					evidenceCodeComboBox.setValue(data.evidenceCode);
					isPublicCheckbox.setValue(data.isPublic);
				}
				
				if (this.hidden) {
					this.show();
				}
			},
			submitForm: function() {
			    if (this.getForm().isValid()) {
			    	var isCreating = (evidenceId == null);
			    	
			        this.getForm().submit({
			            url: '/Gemma/processPhenotypeAssociationForm.html',
			            params: {
			            	evidenceId: evidenceId,
			            	lastUpdated: lastUpdated
			            },
			            waitMsg: (isCreating ? 'Adding' : 'Editing') + ' Phenotype Association ...',
			            success: function(form, action) {
			            	Ext.Msg.alert('Phenotype association ' + (isCreating ? 'added' : 'updated'), 'Phenotype association has been ' + (isCreating ? 'added' : 'updated') + '.');
							this.fireEvent('phenotypeAssociationChanged');			
			            },
			            failure: function(form, action) {
			            	var title = 'Cannot ' + (evidenceId == null ? 'add' : 'edit') + ' phenotype association';

					        switch (action.failureType) {
					            case Ext.form.Action.CLIENT_INVALID:
					                Ext.Msg.alert(title, 'Some fields are still invalid.');
					                break;
					            case Ext.form.Action.CONNECT_FAILURE:
					            	// This failure can happen when there is some unexpected exception thrown in the server side.
					                Ext.Msg.alert(title, 'Server communication failure: ' + action.response.status + ' - ' + action.response.statusText);
					                break;
					            case Ext.form.Action.SERVER_INVALID:
									var validateEvidenceValueObject = action.result;
									Ext.Msg.alert(title, Gemma.convertToEvidenceError(validateEvidenceValueObject).errorMessage,
										function() {
											if (validateEvidenceValueObject.userNotLoggedIn) {
												Gemma.AjaxLogin.showLoginWindowFn();
											}
										}
									);
									
									break;
					    	}
			            },
			            scope: this
			        });
			    }
			},
			showPubMedIdError: function() {
				errorPanel.showError(Gemma.HelpText.WidgetDefaults.PhenotypeAssociationForm.ErrorMessage.pubmedIdInvalid);
			},
			hideErrorPanel: function() {
				errorPanel.hide();
			},
			validateForm: function(shouldSubmitAfterValidating) {
				// Validate the form only when there are no previous errors
				if (evidenceTypeComboBox.getValue() === 'LiteratureEvidenceValueObject') {
					if (geneSearchComboBox.getValue() !== '' && evidenceCodeComboBox.getValue() !== '' && literaturePanel.getPubMedId() !== '') {
						var phenotypeValueUris = phenotypesSearchPanel.validatePhenotypes();
		
						if (phenotypeValueUris != null && phenotypeValueUris.length > 0) {
							var prevGeneValue = geneSearchComboBox.getValue();
		
							PhenotypeController.validatePhenotypeAssociation(
								geneSearchComboBox.getValue(), phenotypeValueUris, evidenceTypeComboBox.getValue(),  
								literaturePanel.getPubMedId(), descriptionTextArea.getValue(), evidenceCodeComboBox.getValue(), 
								isPublicCheckbox.getValue(), evidenceId, lastUpdated, function(validateEvidenceValueObject) {
			
		
								// Because using the controller to validate takes time, fields such as gene value could be changed (e.g. by clicking the Reset button). 
								// Thus, we should show error ONLY when a sample test field has not been changed after the controller call. I picked Gene as a sample.  
								if (prevGeneValue === geneSearchComboBox.getValue()) {
									var hasWarning = false;
		
									if (validateEvidenceValueObject == null) {
										hasError = false;
										errorPanel.hide();
									} else {
										var errorCode = Gemma.convertToEvidenceError(validateEvidenceValueObject); 
										hasWarning = errorCode.isWarning;
										hasError = !hasWarning; 
										if (hasWarning) {
											errorPanel.showWarning(errorCode.errorMessage);
										} else {
											errorPanel.showError(errorCode.errorMessage);
										}
		
									}						
		
									if (shouldSubmitAfterValidating) {
										if (!hasError) {
											if (hasWarning) {
												Ext.MessageBox.confirm('Confirm',
													'<b>' + errorPanel.getErrorMessage() + '</b><br />' +
														'Are you sure you want to ' + (evidenceId == null ? 'add' : 'edit') + ' phenotype association?',
													function(button) {
														if (button === 'yes') {
															this.submitForm();
														}
													},
													this);
											} else {
												this.submitForm();
											}
										}
									}
								}							
							}.createDelegate(this));
							
						}
		
					}
				}
			},
			resetForm: function() {
				this.getForm().reset();
			    this.hideErrorPanel();
			    
				geneSearchComboBox.reset();
				phenotypesSearchPanel.reset();
				evidenceTypeComboBox.reset();	
			    literaturePanel.reset();
				descriptionTextArea.reset();
				evidenceCodeComboBox.reset();
				isPublicCheckbox.reset();
				
				this.validateForm(false);
			},	
			buttonAlign: 'right',
			buttons: [
				{
				    text: 'Cancel',
				    handler: function() {
				    	this.hide();
				    },
					scope: this
				},
				{
					text: 'Reset',
					handler: function() {
						this.resetForm();
					},
					scope: this
				},
				{
				    text: 'OK',
				    formBind: true,
				    handler: function() {
						if (evidenceTypeComboBox.getValue() === 'LiteratureEvidenceValueObject') {
							this.validateForm(true);
						}
				    },
					scope: this
				}
			]		
		});
		
		this.superclass().initComponent.call(this);
    }
});