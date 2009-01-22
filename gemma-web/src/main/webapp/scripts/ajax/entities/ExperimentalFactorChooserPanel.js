Ext.namespace('Gemma');

/**
 * User interface for selecting experimental factors.
 * 
 * @class Gemma.ExperimentalFactorChooserPanel
 * @extends Ext.Window
 * 
 * @author keshav
 * @version $Id$
 */
Gemma.SHOW_ALL_FACTORS = "Show all datasets";
Gemma.HIDE_FACTORS = "Hide datasets with only 1 factor";

Gemma.ExperimentalFactorChooserPanel = Ext.extend(Ext.Window, {
	id : 'factor-chooser',
	layout : 'fit',
	width : 800,
	height : 250,
	closeAction : 'hide',
	constrainHeader : true,

	title : "View/Choose the factors to analyze in each experiment",
	eeFactorsMap : null,

	reset : function(eeSet) {
		if (this.currentEEset != null && eeSet != this.currentEEset) {
			// console.log("reset " + eeSet);
			this.eeFactorsMap = null;
		}
		this.currentEEset = eeSet;
	},

	onCommit : function() {

		if (!this.efGrid) {
			return;
		}
		this.eeFactorsMap = [];

		var eeFactorSource = this.efGrid.getSource();
		// var store = this.efGrid.getStore();

		for (var experimentName in eeFactorSource) {
			var factorName = eeFactorSource[experimentName];
			if (typeof factorName != 'string') {
				continue;
			}

			// locate the experiment in the data
			for (var i in this.data) {
				var rec = this.data[i];
				if (!rec.expressionExperiment) {
					continue;
				}
				var eeInfo = rec.expressionExperiment;

				var eeName = eeInfo.name;

				if (eeName != experimentName) {
					continue;
				}

				var eeId = eeInfo.id;
				var efInfo = rec.experimentalFactors;

				// locate the experimental factor.
				for (var j in efInfo) {
					var ef = efInfo[j];
					if (!ef.name) {
						continue;
					}
					var efName = ef.name;
					if (efName == factorName) {
						var efId = ef.id;
						this.eeFactorsMap.push({
									efId : efId,
									eeId : eeId
								});
						break;
					}
				}

			}

		}

		this.fireEvent("factors-chosen", this.eeFactorsMap);
		this.hide();
	},

	onHelp : function() {

		Ext.Msg
				.alert(
						"Help for factor choose",
						"The meta-analysis can only use one factor per study. Experiments that have more"
								+ " than one factor will be shown here (or view all experiments)."
								+ " Click on the factor field to get a menu for choosing among multiple possibilities. Use the 'hinting' "
								+ "button to choose the type of factor most useful to you, to save manual work. For more help see <a target='_blank' "
								+ "href='http://www.chibi.ubc.ca/faculty/pavlidis/wiki/display/gemma/Dataset+chooser#Datasetchooser-TheGemmaexperimentalfactorchooser'>this page</a>");

	},

	/*
	 * initialize this panel by adding 'things' to it, like the data-store, columns, buttons (and events for buttons),
	 * etc.
	 */
	initComponent : function() {

		Ext.apply(this, {
					tbar : new Ext.Toolbar({
								items : [{
											pressed : true,
											enableToggle : true,
											text : Gemma.SHOW_ALL_FACTORS,
											tooltip : "Click to show/hide all experiments",
											id : 'single-factor-toggle',
											cls : 'x-btn-text-icon details',
											toggleHandler : this.toggleFactors.createDelegate(this)
										}, {
											xtype : 'tbspacer'
										}, {
											xtype : 'tbspacer'
										}, {
											text : 'Hinting',
											tooltip : 'Provide heuristics for selecting factors',
											handler : this.factorHinting.createDelegate(this)
										}]
							}),
					buttons : [{
								id : 'done-selecting-button',
								text : "Done",
								handler : this.onCommit.createDelegate(this),
								scope : this
							}, {
								id : 'help-selecting-button',
								text : "Help",
								handler : this.onHelp.createDelegate(this),
								scope : this
							}]
				});

		Gemma.ExperimentalFactorChooserPanel.superclass.initComponent.call(this);

		this.addEvents("factors-chosen");
	},

	/**
	 * Show the experiments and associated factors.
	 * 
	 * @param {}
	 *            config
	 */
	show : function(eeIds) {
		Gemma.ExperimentalFactorChooserPanel.superclass.show.call(this);
		this.populateFactors(eeIds);
	},

	/**
	 * Show a window with radio buttons to choose between OrganismPart, DiseaseState .. possibly others
	 */
	factorHinting : function(btn) {
		var w = new Ext.Window({
					modal : true,
					title : "Select type of factor to favor",
					closeAction : 'close',
					stateful : false,
					resizable : false,
					columns : 1,
					autoHeight : true,
					width : 300,
					items : [{
								xtype : 'radiogroup',
								stateful : false,
								id : 'factor-hinting-radiogroup',
								style : 'padding:8px;',
								items : [{
											/*
											 * FIXME: cookie doesn't work, so I check 'any' by default.
											 */
											stateful : true,
											id : 'factor-hinting-button',
											boxLabel : 'OrganismPart',
											stateEvents : ['check'],
											stateId : 'organism-part-hint',
											name : 'rb-hint',
											inputValue : 1
										}, {
											stateful : true,
											boxLabel : 'DiseaseState',
											stateId : 'disease-state-hint',
											stateEvents : ['check'],
											name : 'rb-hint',
											inputValue : 2
										}, {
											stateful : true,
											boxLabel : 'Any',
											stateId : 'any-factor-hint',
											stateEvents : ['check'],
											name : 'rb-hint',
											checked : true,
											inputValue : 3
										}]
							}],
					buttons : [{
								text : 'OK',
								handler : function() {
									this.applyHintingFilter();
									w.close();
								},
								scope : this
							}, {
								text : 'Cancel',
								handler : function() {
									w.close();
								}
							}]

				});

		w.show();
	},

	applyHintingFilter : function() {
		var choice = Ext.getCmp('factor-hinting-button').getGroupValue();
		if (choice == '1') {
			// organism part
			this.efGrid.getStore().filterBy(this.organismPartFilter, this);
		} else if (choice == '2') {
			this.efGrid.getStore().filterBy(this.diseaseStateFilter, this);
		} else if (choice == '3') {
			// no filtering
		} else {
			// no filtering
		}

	},

	toggleFactors : function(btn, pressed) {
		var buttonText = btn.getText();
		if (buttonText == Gemma.SHOW_ALL_FACTORS) {
			this.efGrid.getStore().clearFilter();
			btn.setText(Gemma.HIDE_FACTORS);
		} else {
			this.efGrid.getStore().filterBy(this.filter, this, 0);
			btn.setText(Gemma.SHOW_ALL_FACTORS);
		}
	},

	/**
	 * Set the factor for a row to be the one matching a given pattern, if possible.
	 * 
	 * @param {}
	 *            r - the PropertyStore record
	 * @param {}
	 *            id
	 * @param {}
	 *            regex
	 * @return {Boolean}
	 */
	filterByFactorNamePattern : function(r, id, regex) {
		editor = this.efGrid.customEditors[id];

		/*
		 * Locate the matching factor, if any, and set the value in the store. No filtering is actually done here.
		 */
		editor.field.store.each(function(record) {
					if (record.get('name').match(regex)) {
						r.set('value', record.get('name'));
						return false; // break the iteration.
					}
					return true; // keep iterating.
				});

		// honor the other filter
		if (Ext.getCmp('single-factor-toggle').pressed && editor.field.store.getTotalCount() > 1) {
			return true;
		}
		return false;
	},

	organismPartFilter : function(r, id) {
		return this.filterByFactorNamePattern(r, id, "^OrganismPart");
	},

	diseaseStateFilter : function(r, id) {
		return this.filterByFactorNamePattern(r, id, "^DiseaseState");
	},

	filter : function(r, id) {

		editor = this.efGrid.customEditors[id];

		/*
		 * IF there are multiple factors, show it.
		 */
		if (editor.field.store.getTotalCount() > 1) {
			return true;
		}

		return false;

	},

	/**
	 * Get factors and then pass the results to the callback.
	 * 
	 * @param {}
	 *            eeIds
	 */
	populateFactors : function(eeIds) {
		if (!this.efGrid) {
			Ext.apply(this, {
						loadMask : new Ext.LoadMask(this.getEl(), {
									msg : "Loading factors ..."
								})
					});
			this.loadMask.show();
		} else {
			this.efGrid.loadMask.show();
		}
		DifferentialExpressionSearchController.getFactors(eeIds, {
					callback : this.returnFromGetFactors.createDelegate(this)
				});
	},

	/**
	 * When returning from getting the factors, load the data.
	 * 
	 * @param {}
	 *            result
	 */
	returnFromGetFactors : function(results) {
		this.data = results;
		var dataFromServer = {
			data : results
		};
		if (results.size() > 0) {
			if (this.efGrid) {
				this.remove(this.efGrid, true);
			} else {
				this.loadMask.hide();
			}
			this.efGrid = new Gemma.ExpressionExperimentExperimentalFactorGrid(dataFromServer);
			this.add(this.efGrid);
			this.doLayout();
			this.efGrid.getStore().filterBy(this.filter, this, 0);

		}
	}

});