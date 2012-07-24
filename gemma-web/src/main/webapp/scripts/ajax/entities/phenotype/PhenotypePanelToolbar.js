/**
 * This is the toolbar for the phenotype panel.
 * 
 * @author frances
 * @version $Id$
 */
Ext.namespace('Gemma');

Gemma.PhenotypePanelToolbar = Ext.extend(Ext.Toolbar, {
    initComponent: function() {
    	var DEFAULT_FILTERS_TITLE = 'Filters';
    	var MY_ANNOTATIONS_ONLY_TITLE = 'My annotations only';

		var currentFilters = {
			taxonId: '-1',
			showOnlyEditable: false
		};			

    	var loggedIn = false;

    	if (!Gemma.isRunningOutsideOfGemma()) {
			var loggedInDom = Ext.getDom('loggedIn');
			loggedIn = (loggedInDom && loggedInDom.value === "true");

			Gemma.Application.currentUser.on("logIn", function() {
				loggedIn = true;
			});

			Gemma.Application.currentUser.on("logOut", function() {
				loggedIn = false;
			});
    	}

		var showOnlyEditableCheckbox = new Ext.form.Checkbox({
			checked: false, // should not be checked initially.
			fieldLabel: MY_ANNOTATIONS_ONLY_TITLE,
			listeners: {
				check: function(thisCheckbox, checked) {
					if (checked) {
						if (Gemma.isRunningOutsideOfGemma()) {
							Ext.Msg.alert(Gemma.HelpText.WidgetDefaults.PhenotypePanel.filterMyAnnotationsOutsideOfGemmaTitle,
								Gemma.HelpText.WidgetDefaults.PhenotypePanel.filterMyAnnotationsOutsideOfGemmaText);
						
							thisCheckbox.setValue(false);
						} else {
							SignupController.loginCheck({
				                callback: function(result) {
				                	if (!result.loggedIn) {
										Gemma.AjaxLogin.showLoginWindowFn();
								
										thisCheckbox.setValue(false);
										
										Gemma.Application.currentUser.on("logIn",
											function(userName, isAdmin) {	
												Ext.getBody().unmask();
											},
											this,
											{
												single: true
											});
					                }
				                }
				            });
						}
					}
				}
			}
		});

		var taxonCombo = new Gemma.TaxonCombo({
			isDisplayTaxonWithEvidence : true,
			fieldLabel: 'Taxon',
			// don't remember taxon value if user navigates away then comes back
			stateId : null, 
			emptyText : "Filter by taxon",
			allTaxa : true, // want an 'All taxa' option
			value: currentFilters.taxonId,
			// We must have this. Otherwise, the menu closes when combo box's item is selected.			
			getListParent: function() {
			    return this.el.up('.x-menu');
			}
		});
		taxonCombo.getStore().on('doneLoading', function() {
			// Initialize it.
			taxonCombo.setValue(taxonCombo.value);
		}, this);				

		var menu = new Ext.menu.Menu({
    		listeners: {
    			hide: function(thisMenu) {
					showOnlyEditableCheckbox.suspendEvents();
					showOnlyEditableCheckbox.setValue(currentFilters.showOnlyEditable);
					showOnlyEditableCheckbox.resumeEvents();

					taxonCombo.setValue(currentFilters.taxonId);
    			}
    		},
    		items: [
	            {
	            	xtype: 'form',
					autoHeight: true,
					width: 400,
					labelWidth: 120,
					items: [
						showOnlyEditableCheckbox,
						taxonCombo
					],
					buttonAlign: 'right',
					buttons: [
						{
						    text: 'Apply',
						    formBind: true,
						    handler: function() {
								this.fireEvent('filterApplied', {
									showOnlyEditable: showOnlyEditableCheckbox.getValue(),
									taxonId: taxonCombo.getValue()
								});
								
								updateFiltersStatus();
								
						    	menu.hide();
						    },
							scope: this
						}
					]		
				}
    		]
    	});
    	
		var filterButton = new Ext.Button({
			text: DEFAULT_FILTERS_TITLE,
			menu: menu
		});
		
		var updateFiltersStatus = function() {
			currentFilters.taxonId = taxonCombo.getValue();
			currentFilters.showOnlyEditable = showOnlyEditableCheckbox.getValue();
			
			var filtersApplied = '';
			if (showOnlyEditableCheckbox.getValue()) {
				filtersApplied += '<b>' + MY_ANNOTATIONS_ONLY_TITLE + '</b>';
			}
			if (taxonCombo.getValue() !== '-1') {
				if (filtersApplied !== '') {
					filtersApplied += ' + ';
				}
				filtersApplied += '<b>' + taxonCombo.getTaxon().data.commonName + '</b>';
			}
			filterButton.setText((filtersApplied === '') ?
				DEFAULT_FILTERS_TITLE :
				DEFAULT_FILTERS_TITLE + ': ' + filtersApplied);
		};		

		Ext.apply(this, {
			setShowOnlyEditableCheckbox: function(status) {
				showOnlyEditableCheckbox.suspendEvents();
				showOnlyEditableCheckbox.setValue(status);
				showOnlyEditableCheckbox.resumeEvents();
				
				updateFiltersStatus();
			},
			items: [			
				filterButton
            ]
		});

		Gemma.PhenotypePanelToolbar.superclass.initComponent.call(this);
    }
});
