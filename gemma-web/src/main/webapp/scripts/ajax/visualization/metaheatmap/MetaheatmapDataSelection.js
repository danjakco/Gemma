Ext.namespace('Gemma');

Gemma.MetaHeatmapDataSelection = Ext.extend(Ext.Panel, {

	constructor : function(searchCommand) {
		if(typeof searchCommand !== 'undefined'){
			Ext.apply(this, {
			param : searchCommand
				// get input from front-page search
			});
		}
		Gemma.MetaHeatmapDataSelection.superclass.constructor.call(this);
	},

	_selectedDatasetGroups : [],
	_selectedGeneGroups : [],
	geneGroupValueObjects: [],
	experimentGroupValueObjects:[],
	waitingForGeneSessionGroupBinding : false,
	waitingForDatasetSessionGroupBinding : false,
	geneGroupNames : [],
	datasetGroupNames : [],
	taxonId : null,

	prepareVisualization : function(target) {

		this.geneGroupNames = [];
		this.datasetGroupNames = [];
		this.geneGroupValueObjects = [];
		this.experimentGroupValueObjects = [];
		this._selectedGeneGroups = [];
		this._selectedDatasetGroups = [];
		// control variables used if asynchronous session group creation is
		// performed
		this.waitingForGeneSessionGroupBinding = false;
		this.waitingForDatasetSessionGroupBinding = false;

		var i;
		// populate list of selected groups
		for (i = 0; i < this._geneCombos.length; i++) {
			this._selectedGeneGroups.push(this._geneCombos[i].getGeneGroup());
		}

		for (i = 0; i < this._datasetCombos.length; i++) {
			this._selectedDatasetGroups.push(this._datasetCombos[i].getSelected());
		}

		var geneGroupsToBindToSession = [];

		for (i = 0; i < this._selectedGeneGroups.length; i++) {

			if (this._selectedGeneGroups[i] && this._selectedGeneGroups[i] !== null) {
				// if the group has a null value for id, then it
				// hasn't been
				// created as a group in the database nor session
				if (this._selectedGeneGroups[i].id === null || this._selectedGeneGroups[i].id === -1) {
					this._selectedGeneGroups[i].geneIds = this._selectedGeneGroups[i].memberIds;
					geneGroupsToBindToSession.push(this._selectedGeneGroups[i]);
				} else {
					this.geneGroupValueObjects.push(this._selectedGeneGroups[i]);
					this.geneGroupNames.push(this._selectedGeneGroups[i].name);
				}
			}
		}
		var j;
		if (geneGroupsToBindToSession.length !== 0) {
			this.waitingForGeneSessionGroupBinding = true;
			GeneSetController.addNonModificationBasedSessionBoundGroups(geneGroupsToBindToSession, function(geneSets) {
						// should be at least one geneset
						if (geneSets === null || geneSets.length === 0) {
							// TODO error message
							return;
						} else {
							for (j = 0; j < geneSets.length; j++) {
								this.geneGroupValueObjects.push(geneSets[j]);
								this.geneGroupNames.push(geneSets[j].name);
							}
						}
						this.waitingForGeneSessionGroupBinding = false;
						this.fireEvent('geneGroupsReadyForVisualization');
					}.createDelegate(this));
		}

		var datasetGroupsToBindToSession = [];
		for (i = 0; i < this._selectedDatasetGroups.length; i++) {
			if (this._selectedDatasetGroups[i] && this._selectedDatasetGroups[i] !== null) {
				// if the group has a null value for id, then it
				// hasn't been
				// created as a group in the database nor session
				if (this._selectedDatasetGroups[i].id === null || this._selectedDatasetGroups[i].id === -1 ) {
					this._selectedDatasetGroups[i].expressionExperimentIds = this._selectedDatasetGroups[i].memberIds;
					datasetGroupsToBindToSession.push(this._selectedDatasetGroups[i]);
				} else {
					this.experimentGroupValueObjects.push(this._selectedDatasetGroups[i]);
					this.datasetGroupNames.push(this._selectedDatasetGroups[i].name);
				}
			}
		}
		if (datasetGroupsToBindToSession.length !== 0) {
			this.waitingForDatasetSessionGroupBinding = true;
			ExpressionExperimentSetController.addNonModificationBasedSessionBoundGroups(datasetGroupsToBindToSession,
					function(datasetSets) {
						// should be at least one datasetSet
						if (datasetSets === null || datasetSets.length === 0) {
							// TODO error message
							return;
						} else {
							for (j = 0; j < datasetSets.length; j++) {
								this.experimentGroupValueObjects.push(datasetSets[j]);
								this.datasetGroupNames.push(datasetSets[j].name);
							}
						}
						this.waitingForDatasetSessionGroupBinding = false;
						this.fireEvent('datasetGroupsReadyForVisualization');
					}.createDelegate(this));
		}

		// if no asynchronous calls were made, run visualization right away
		// otherwise, doVisualization will be triggered by events
		if (!this.waitingForDatasetSessionGroupBinding && !this.waitingForGeneSessionGroupBinding) {
			this.doVisualization(null);
		}
	},
	doVisualization : function() {	
		
		if (typeof this.param === 'undefined') { // if not loading text from search interface (ex: when using a bookmarked link)
			var waitMsg = Ext.Msg.wait("", "Loading your visualization...");
		}
		
		this._selectedDatasetGroups = [];
		this._selectedGeneGroups = [];

		if (!this.taxonId || this.taxonId === null) {
			// DO SOMETHING!!
		}
		this.geneGroupValueObjects = this.geneGroupValueObjects;
		this.experimentGroupValueObjects = this.experimentGroupValueObjects;
		
		if(this.initexperimentGroupValueObjects){
			this.experimentGroupValueObjects = this.experimentGroupValueObjects.concat(this.initExperimentGroupResultValueObjects);
		}
		if(this.initexperimentGroupValueObjects){
			this.experimentGroupValueObjects = this.experimentGroupValueObjects.concat(this.initexperimentGroupValueObjects);
		}
		if(this.initGeneGroupValueObjects){
			this.geneGroupValueObjects = this.geneGroupValueObjects.concat(this.initGeneGroupValueObjects);
		}
		if(this.initGeneValueObjects){
			this.geneGroupValueObjects = this.geneGroupValueObjects.concat(this.initGeneValueObjects);
		}
		if(typeof this.initGeneSessionGroupQueries === 'undefined'){
			this.initGeneSessionGroupQueries = [];
		}else{
			this.geneSessionGroupQueries = this.initGeneSessionGroupQueries;
		}
		if(typeof this.initExperimentSessionGroupQueries === 'undefined'){
			this.initExperimentSessionGroupQueries = [];
		}else{
			this.experimentSessionGroupQueries = this.initExperimentSessionGroupQueries;
		}
		
//		DifferentialExpressionSearchController.differentialExpressionAnalysisVisualizationSearch(this.taxonId,
//				this.datasetReferences, this.geneReferences, this.initGeneSessionGroupQueries,
//				this.initExperimentSessionGroupQueries, function(data) {
		
		DifferentialExpressionSearchController.differentialExpressionAnalysisVisualizationSearch(this.taxonId,
				this.experimentGroupValueObjects, this.geneGroupValueObjects, this.initGeneSessionGroupQueries,
				this.initExperimentSessionGroupQueries, function(data) {

					//progressWindow.hide();
					if(waitMsg){
						waitMsg.hide();
					}
					
					// to trigger loadmask on search form to hide
					this.fireEvent('visualizationLoaded');
					
					data.taxonId = this.taxonId;
					
					var experimentCount = 0;
					var lastDatasetId = null;

					var i;
					var j;
					for (i = 0; i < data.resultSetValueObjects.length; i++) {
						for (j = 0; j < data.resultSetValueObjects[i].length; j++) {

							// get the number of experiments, they should be
							// grouped by datasetId
							if (data.resultSetValueObjects[i][j].datasetId != lastDatasetId) {
								experimentCount++;
							}
							lastDatasetId = data.resultSetValueObjects[i][j].datasetId;
						}
					}					

					// if no experiments were returned, don't show visualizer
					if (experimentCount === 0) {
						Ext.DomHelper.overwrite('meta-heatmap-div', {
							html : '<img src="/Gemma/images/icons/warning.png"/> Sorry, no data available for your search.'
						});
					} else {
						var title = '<b>Differential Expression Visualisation</b>' +
							' (Data available for ' +
							experimentCount +
							(((typeof this.param !== 'undefined') ? 
								(' of ' + this.param.datasetCount + ' experiments)') : " experiments)"));
							/*+" Threshold: " + this.pvalue)//we don't use this yet*/
						_metaVizApp = new Gemma.MetaHeatmapApp({
									_selectionController: this, // temp hack so we can re-run search from vizApp
									tbarTitle : title,
									visualizationData : data,
									initGeneSort: (this.initGeneSort)? this.initGeneSort:null,
									initExperimentSort: (this.initExperimentSort)? this.initExperimentSort:null,
									initFactorFilter: (this.initFactorFilter)? this.initFactorFilter:null,
									applyTo : 'meta-heatmap-div',
									//threshold : threshold, //we don't use this yet // should be renamed to p value threshold or something like that
									geneSessionGroupQueries :this.geneSessionGroupQueries,
									experimentSessionGroupQueries :this.experimentSessionGroupQueries,
									loadedFromURL: this.loadedFromURL
								});
						_metaVizApp.doLayout();
						_metaVizApp.refreshVisualization();
					}

				}.createDelegate(this));
	},
		/**
	 * Restore state from the URL (e.g., bookmarkable link)
	 * 
	 * @param {}
	 *            query
	 * @return {}
	 */
	initializeSearchFromURL : function(url) {
		
		alert("TODO initializeSearchFromURL");
		
		var param = Ext.urlDecode(url);
		var arrs; var i;
		
		/*if (param.p) { //we don't use this yet
			this.pvalue = param.p;
		}*/if (param.t) {
			this.taxonId = param.t;
		}
		if (param.gs) {
			this.initGeneSort = param.gs;
		}
		if (param.es) {
			this.initExperimentSort = param.es;
		}
		if (param.ff) {
			this.initFactorFilter = param.ff.split(',');
		}
		if (param.gq) {
			this.initGeneSessionGroupQueries = param.gq.split(',');
		}
		if (param.eq) {
			this.initExperimentSessionGroupQueries = param.gq.split(',');
		}
		/*if (param.g) {
			arrs = param.g.split(',');
			for(i = 0; i< arrs.length; i++){
				// make a reference object for each id
				arrs[i] = {
					id: arrs[i],
					type: 'databaseBackedGene'  // TODO get this from the reference java object!!!
				};
			}
			this.initGeneReferences = arrs;
		}
		if (param.e) {
			arrs = param.e.split(',');
			for(i = 0; i< arrs.length; i++){
				// make a reference object for each id
				arrs[i] = {
					id: arrs[i],
					type: 'databaseBackedExperiment'  // TODO get this from the reference java object!!!
				};
			}
			this.initExperimentReferences = arrs;
		}
		if (param.gg) {
			arrs = param.gg.split(',');var k = 0;
			for(i = 0; i< arrs.length; i++){
				// make a reference object for each id
				arrs[i] = {
					id: arrs[i],
					type: 'databaseBackedGroup' // TODO get this from the reference java object!!!
				};
			}
			this.initGeneGroupReferences = arrs;
		}
		if (param.eg) {
			arrs = param.eg.split(',');
			for(i = 0; i< arrs.length; i++){
				// make a reference object for each id
				arrs[i] = {
					id: arrs[i],
					type: 'databaseBackedGroup'  // TODO get this from the reference java object!!!
				};
			}
			this.initExperimentGroupReferences = arrs;
		}
		*/
	},
	initComponent : function() {

		this.on('geneGroupsReadyForVisualization', function() {
					if (!this.waitingForDatasetSessionGroupBinding) {
						this.doVisualization();
					}
				}, this);
		this.on('datasetGroupsReadyForVisualization', function() {
					if (!this.waitingForGeneSessionGroupBinding) {
						this.doVisualization();
					}
				}, this);
		
		// FOR TESTING !!!!!
		/*this.param2 = {
			
			geneNames : ["gene TEST", "gene TEST 2"],
			datasetNames : ["dataset TEST", "dataset TEST2"],
			taxonId : 2,
			pvalue : Gemma.DEFAULT_THRESHOLD
		};*/

		this.loadedFromURL = false;
		var queryStart = document.URL.indexOf("?");
		/*if (queryStart > -1) {
			this.initializeSearchFromURL(document.URL.substr(queryStart + 1));
			if((this.initGeneSessionGroupQueries || this.initGeneValueObjects || this.initGeneGroupValueObjects) &&
			   (this.initExperimentSessionGroupQueries || this.initExperimentValueObjects || this.initExperimentGroupValueObjects)){
				this.loadedFromURL = true;
			}
		}
		*/
		
		if (this.param && !this.loadedFromURL) { // if from search form
			if (this.param.experimentSetValueObjects) {
				this.experimentGroupValueObjects = this.param.experimentSetValueObjects;
			}
			if (this.param.geneSetValueObjects) {
				this.geneGroupValueObjects = this.param.geneSetValueObjects;
			}
			if (this.param.geneNames) {
				this.geneGroupNames = this.param.geneNames;
			}
			if (this.param.datasetNames) {
				this.datasetGroupNames = this.param.datasetNames;
			}
			if (this.param.datasetCount) {
				this.datasetsSelectedCount = this.param.datasetCount;
			}
			if (this.param.taxonId) {
				this.taxonId = this.param.taxonId;
			}
			if (this.param.taxonName) {
				this.taxonName = this.param.taxonName;
			}
			if (this.param.geneSessionGroupQueries) {
				this.geneSessionGroupQueries = this.param.geneSessionGroupQueries;
			}
			if (this.param.experimentSessionGroupQueries) {
				this.experimentSessionGroupQueries = this.param.experimentSessionGroupQueries;
			}
			/*if (this.param.pvalue) { //we don't use this yet
				this.pvalue = this.param.pvalue;
			}*/
			// need to know for bookmarking
			if (this.param.selectionsModified){
				this.selectionsModified = this.param.selectionsModified;
			}
		}

		Gemma.MetaHeatmapDataSelection.superclass.initComponent.apply(this, arguments);

		if(this.geneGroupValueObjects && this.experimentGroupValueObjects){
			this.doVisualization();
		}
	},
	onRender : function() {
		Gemma.MetaHeatmapDataSelection.superclass.onRender.apply(this, arguments);
		_sortPanel = this.ownerCt.sortPanel;
	}
});

Ext.reg('metaVizDataSelection', Gemma.MetaHeatmapDataSelection);
