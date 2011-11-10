/**
 * @version $Id$
 * @author AZ
 */
Ext.namespace('Gemma.Metaheatmap');

/**
 * MetaHeatmap Application Consist of 3 main panels:
 * <pre>
 * - side labels area
 * - top labels area 
 * - main area 
 * </pre>
 * It is controlled by window that allows sorting/filtering and choosing data.
 * 
 * genes - Metaheatmap.Genes is shared between control panel and visualization panel.
 *         It takes care of sorting/filtering of the genes.
 * geneSelection
 * experimentSelection
 * 
 * 
 *  ----------------------------------------
 * 				Toolbar	
 *  ------  --------------------  ----------
 *         | TopLabelArea  ^  	|			|
 *          ---------------|----			|
 *  ----- 				   |	|	Sort	|
 * |Side |	boxHeatmap     |	|  Filter	|
 * |Label|				   |	|	Panel	|
 * |Area |				   |	|			|
 * |	 |				   |	|			|
 * |	 |				   |	|			|
 * |	 |				   |	|			|
 * |<-----VisualizationPanel--->|			|
 * |	 |						|			|
 * ------ ---------------------- -----------
 *
 * 
 * If 'savedState' object is passed, the application is initialized using values passed in it.
 * The state captures:  sort/filter + search queries/sets used to retrieve genes and experiments.
 * Due to URL length limitation this will not always work for bookmarkable links. We can store 'savedState' in database in the future to work around this limitation.
 */		

/* TODO 
 * + getApplicationState()  -- not tested
 * + setApplicationState()  -- not implemented
 * + loadApplicationState() -- not implemented
 * + saveApplicationState() -- not implemented
 * 
 * + getBookmarkableLink() 		  -- replace with saveApplicationState()
 * + showBookmarkableLinkWindow() -- not tested
 * + getDownloadLink()  		  -- remove 
 * 
 */

Gemma.Metaheatmap.defaultConditionZoom = 10;
Gemma.Metaheatmap.defaultGeneZoom = 10;

Gemma.Metaheatmap.Application = Ext.extend ( Ext.Panel, {
	
	initComponent : function () {

		this.conditions = this.visualizationData.conditions;
		this.genes	    = this.visualizationData.genes;
		this.cells 		= {};		
		this.cells.cellData  = this.visualizationData.cellData;

		// Add convenience cell retrieval function to cellData object. TODO: refactor out?
		this.cells.getCell = function (gene, condition) {
			var geneToCellMap = this.cellData[condition.id];			
			if (typeof geneToCellMap != 'undefined') {
				var cellValueObj = geneToCellMap[gene.id];
				if (typeof cellValueObj != 'undefined') {
					return cellValueObj;			
				}
			}			
			return null;
		};

//TODO		
//		var visualizerState = {
//				filterSorter : { genes : null,
//					   	   		 conditions : null},
//				zoom   : { genes      : null,
//					       conditions : null}			
//		};
//		if ( savedState ) {
//			visualizerState = savedState;
//		}
		
		var filters = [];
						
		var sortByExperimentGroupFn = this.createSortByPropertyFunction_('experimentGroupName');
		var sortBySpecificityFn = this.createSortByPropertyFunction_('miniPieValue');
		var sortByFactorCategoryFn = this.createSortByPropertyFunction_ ('factorCategory');
		var sortByContrastFactorValueFn = this.createSortByPropertyFunction_ ('contrastFactorValue');
		var sortByDatasetNameFn = this.createSortByPropertyFunction_ ('datasetShortName');

		var sortByPvaluesFn = this.createSortByPropertyFunction_('inverseSumPvalue');

		var sortByGeneNameFn = this.createSortByPropertyFunction_ ('name'); 
		var sortGeneByGroupFn = this.createSortByPropertyFunction_ ('groupName');
						
		var sortFactorTree = [{'sortFn' : sortByFactorCategoryFn, 'groupBy' : 'factorCategory'},
		                      {'sortFn' : sortByContrastFactorValueFn, 'groupBy' : 'contrastFactorValue'},
		                      {'sortFn' : sortByContrastFactorValueFn, 'groupBy' : null}];		
		
				
		var geneSortPreset1 = [{'sortFn' : sortGeneByGroupFn , 'groupBy' : 'groupName'},
		                       {'sortFn' : sortByGeneNameFn, 'groupBy' : null}];
		
		var geneSortPreset2 = [{'sortFn' : sortGeneByGroupFn , 'groupBy' : 'groupName'},
		                       {'sortFn' : sortByPvaluesFn, 'groupBy' : null}];
		
		var conditionSortPreset1 = [{'sortFn' : sortByExperimentGroupFn , 'groupBy' : 'experimentGroupName'},
		                            {'sortFn' : sortByDatasetNameFn , 'groupBy' : 'datasetShortName'},
		                            {'sortFn' : sortByContrastFactorValueFn, 'groupBy' : null}];

		var conditionSortPreset2 = [{'sortFn' : sortByFactorCategoryFn , 'groupBy' : 'factorCategory'},
		                            {'sortFn' : sortBySpecificityFn, 'groupBy' : null}]; 

		var conditionSortPreset3 = [{'sortFn' : sortByFactorCategoryFn , 'groupBy' : 'factorCategory'},
		                            {'sortFn' : sortByPvaluesFn, 'groupBy' : null}]; 
		
		this.genePresets = [
		                    {'name' : 'sort alphabetically', 'sort' : geneSortPreset1, 'filter' : []},
		                   	{'name' : 'sort by average p-value', 'sort' : geneSortPreset2, 'filter' : []}
		                   ];
		
		this.genePresetNames = [];
		var i;
		for ( i = 0; i < this.genePresets.length; i++) {
			this.genePresetNames.push ([this.genePresets[i]['name'], i]);
		}
		
		this.conditionPresets = [            
		                  {'name' : 'sort by experiment', 'sort' : conditionSortPreset1, 'filter' : []},         
		                  {'name' : 'sort by specificity (group by factor)', 'sort' : conditionSortPreset2, 'filter' : []},
		                  {'name' : 'sort by average p-value (group by factor)', 'sort' : conditionSortPreset3, 'filter' : []}
		                ];		
		
		this.conditionPresetNames = [];		
		for ( i = 0; i < this.conditionPresets.length; i++) {
			this.conditionPresetNames.push ([this.conditionPresets[i]['name'], i]);
		}		
		
		this.geneTree 	   = new Gemma.Metaheatmap.SortedFilteredTree (this.genes, geneSortPreset1, []); 			
		this.conditionTree = new Gemma.Metaheatmap.SortedFilteredTree (this.conditions, conditionSortPreset1, []);		
		this.factorTree    = new Gemma.Metaheatmap.SortedFilteredTree (this.conditions, sortFactorTree, []);
		
		Ext.apply ( this, {
			layout : 'border',
			width  : Ext.getBody().getViewSize().width - 40, // eventually it'll be a viewport
			height : Ext.getBody().getViewSize().height - 20,
			tbar  :	[ {
						xtype: 'label',
						ref : 'titleLabel',
						text: ""
					  },
			      	  '->',
			      	  {	xtype : 'button',
			      		text  : '<b>Color Legend</b>',
			      		enableToggle : true,
			      		tooltip : 'Show/hide the color legend',
			      		toggleHandler : function (btn,pressed) {
			      		  if (pressed) {
			      			  this.visualizationPanel.isLegendShown = true;

			      			  if (this.visualizationPanel.variableWidthCol.boxHeatmap.isShowPvalue) {			      				  
			      				  this.visualizationPanel.variableWidthCol.colorLegendFoldChange.hide();
			      				  this.visualizationPanel.variableWidthCol.colorLegendPvalue.show();			      				  
			      			  } else {
			      				  this.visualizationPanel.variableWidthCol.colorLegendFoldChange.show();
			      				  this.visualizationPanel.variableWidthCol.colorLegendPvalue.hide();			      				  
			      			  }
			      		  } else {
			      			  this.visualizationPanel.isLegendShown = false;
		      				  this.visualizationPanel.variableWidthCol.colorLegendFoldChange.hide();
		      				  this.visualizationPanel.variableWidthCol.colorLegendPvalue.hide();			      				  
			      		  }
			      	  	}, scope : this
			      	  },
			      	  '-',
			      	  {	xtype : 'button',
			      		text : '<b>Bookmarkable Link</b>',
			      		tooltip:'Get a link to re-run this search',
			      		disabled: true,
			      		handler : function() {
			      		  this.showBookmarkableLinkWindow();
			      	  	},
			      	  	scope : this
			      	  },
			      	  '-',
			      	  { xtype : 'button',
			      		text : '<b>Save selected genes</b>',
			      		icon : '/Gemma/images/download.gif',
			      		cls : 'x-btn-text-icon',
			      		tooltip:'Save your selection as a list of genes.',		      		
			      		handler : function() {
			      		  var geneSetGrid = new Gemma.GeneMembersSaveGrid({
						  	genes: this.visualizationPanel.getSelectedGenes(),
						  	frame: false
						  });
						  this.getEl().mask();
			      		  var popup = new Ext.Window ({ closable : false,
														layout : 'fit',
														width : 450,
														height : 500,
														items : geneSetGrid });	
														
							geneSetGrid.on('doneModification', function() {
										this.getEl().unmask();
										popup.hide();
									}, this);
			      		  popup.show();
			      	  	},
			      	  	scope: this		      	  
			      	  },'-',{
					  	xtype: 'button',
						text: '<b>Download</b>',
						icon: '/Gemma/images/download.gif',
					  	menu: new Ext.menu.Menu({
							items: [{
								text: 'As text',
								icon: '/Gemma/images/icons/page_white_text.png',
								tooltip: 'Download a formatted text version of your search results',
								handler: function(){
				      		  		var textWindow = new Gemma.Metaheatmap.DownloadWindow({ geneTree : this.geneTree,
										conditionTree : this.conditionTree,
										cells : this.cells,
										isPvalue : this.visualizationPanel.variableWidthCol.boxHeatmap.isShowPvalue});
				      		  		textWindow.convertToText ();
				      		  		textWindow.show();
								},
								scope: this
							}, {
								text: 'As image',
								icon: '/Gemma/images/icons/picture.png',
								tooltip: 'Download heatmap image',
								handler: function(){
									this.visualizationPanel.downloadImage();
								},
								scope: this
							}]
						})}, '-',
			      	 { xtype 	: 'button',
			      		icon	: '/Gemma/images/icons/question_blue.png',
			      		cls 	: 'x-btn-icon',
			      		tooltip : 'Click here for documentation on how to use this visualizer.',
			      		handler : function() {
			      		  window.open('http://www.chibi.ubc.ca/faculty/pavlidis/wiki/download/attachments/26706691/differentialExpressionVisualizationImageMap-howTo3.png'); 
			      	  	},
			      	  	scope	: this		      	 
			        }
			],			
			items :	[
			       	 {	ref     : 'visualizationPanel',
			       		xtype   : 'Metaheatmap.VisualizationPanel',
			       		conditionTree	  : this.conditionTree,
			       		geneTree		  : this.geneTree,
			       		cells			  : this.cells,
			       		geneControls 	  : this.geneControls,
			       		conditionControls : this.conditionControls,			       					       		
			       		region  : 'center',
						autoScroll : true
			       	 },
			       	 {	ref		: 'controlPanel',
				       		xtype   : 'Metaheatmap.ControlPanel',
				       		conditionTree	  : this.conditionTree,
				       		geneTree		  : this.geneTree,
				       		geneControls 	  : this.geneControls,
				       		conditionControls : this.conditionControls,
				       		sortedTree		  : this.factorTree,
							collapsible       : true,
							floatable: false,
							animFloat: false,
							title: 'Sort & Filter',
							border:true,
				       		region  : 'east',
				       		width   : 300
				     }
				    ]
		});

		Gemma.Metaheatmap.Application.superclass.initComponent.apply (this, arguments);
	},
	
	onRender : function() {
		Gemma.Metaheatmap.Application.superclass.onRender.apply (this, arguments);
		
		this.controlPanel.on('applySortGroupFilter', function (geneSort, geneFilter, conditionSort, conditionFilter) {
			this.geneTree 	   = new Gemma.Metaheatmap.SortedFilteredTree (this.genes, geneSort, geneFilter); 			
			this.conditionTree = new Gemma.Metaheatmap.SortedFilteredTree (this.conditions, conditionSort, conditionFilter);		
			
			this.visualizationPanel.setConditionTree (this.conditionTree);
			this.visualizationPanel.setGeneTree (this.geneTree);	
			
			this.topToolbar.titleLabel.setText(this.conditionTree.numFiltered + " conditions and " + this.geneTree.numFiltered + " genes  are filtered.");
						
			this.visualizationPanel.redraw();			
		}, this );
		
	},
		
	createSortByPropertyFunction_ : function ( property ) {
		return function ( a, b ) {
			if (typeof a[property] == "number") {
				return (a[property] - b[property]);
			} else {
				return ((a[property] < b[property]) ? -1 : ((a[property] > b[property]) ? 1 : 0));
			}
		};
	},				
	
	refreshVisualization : function() {
		this.visualizationPanel.redraw();
	},
			
	getApplicationState : function() {
		var state = {};
		// Get gene group ids.
		// If there are any session-bound groups, get query that made them.
		state.geneGroupIds = [];
		state.geneIds = [];
		var i, ref, k = 0;
		for (i = 0; i < this.metaheatmapData.geneGroupReferences.length; i++) {
			ref = this.metaheatmapData.geneGroupReferences[i];
			if (typeof ref.type !== 'undefined') {
				if (ref.type === 'databaseBackedGene') {
					state.geneIds.push(ref.id);
				} else if (ref.type.toLowerCase().indexOf('session') === -1 && ref.type.toLowerCase().indexOf('group') !== -1) {
					state.geneGroupIds.push (ref.id);
				} else {
					this.usingSessionGroup = true;
				}
			}
		}
		if (this.experimentSessionGroupQueries) {
			state.experimentSessionGroupQueries = this.experimentSessionGroupQueries;
		}
		if (this.geneSessionGroupQueries) {
			state.geneSessionGroupQueries = this.geneSessionGroupQueries;
		}
		
		// Get experiment group ids.
		// If there are any session-bound groups, get queries that made them.
		state.eeGroupIds = [];
		state.eeIds = [];
		for (i = 0; i < this.metaheatmapData.datasetGroupReferences.length;i++) {
			ref = this.metaheatmapData.datasetGroupReferences[i];
			if (typeof ref.type !== 'undefined') {
				if(ref.type === 'databaseBackedExperiment'){
					state.eeIds.push(ref.id);
				} else if (ref.type.toLowerCase().indexOf('session') === -1 && ref.type.toLowerCase().indexOf('group') !== -1) {
					state.eeGroupIds.push(ref.id);
				} else {
					this.usingSessionGroup = true;
				}
			}
		}
		
		// Gene sort state.
		state.geneSort = this.toolPanel_._sortPanel._geneSort.getValue();
		
		// Experiment sort state.
		state.eeSort = this.toolPanel_._sortPanel._experimentSort.getValue();
		if(state.eeSort === '--'){// TODO make this less fragile
			state.eeSort = null;
		}
		
		// filters
		var toFilter = [];
		var children = this.tree.getRootNode().childNodes;
		for (i = 0; i < children.length; i++) {
			if (!children[i].attributes.checked) {
				toFilter.push(children[i].id);
			}
		}
		state.factorFilters = toFilter;	
		state.taxonId = this.metaheatmapData.taxonId;
		return state;
	},
		
	/** TODO
	 * Create a URL that can be used to query the system.
	 * 
	 * @param state should have format:
	 * 	state.geneIds = array of gene ids that occur singly (not in a group): [7,8,9]
	 *  state.geneGroupIds = array of db-backed gene group ids: [10,11,12]
	 *  ^same for experiments^
	 *  state.geneSort
	 *  state.eeSort
	 *  state.filters = list of filters applied, values listed should be filtered OUT (note this 
	 *  	is the opposite heuristic as in viz) (done to minimize url length)
	 *  state.taxonId
	 * @return url string or null if error or nothing to link to
	 */
	getBookmarkableLink : function () {
		var state = this.getApplicationState();
		return Gemma.Metaheatmap.Utils.getBookmarkableLink (state);
	},
	
	// TODO
	showBookmarkableLinkWindow : function() {		
		url = this.getBookmarkableLink();

		var warning = (this.selectionsModified)? "Please note: you have unsaved modifications in one or more of your"+
							" experiment and/or gene groups. <b>These changes will not be saved in this link.</b>"+
							" In order to keep your modifications, please log in and save your unsaved groups.<br><br>":"";
									
		if (url === null && warning === ""){
			url= "Error creating your link.";
		}
		var win = new Ext.Window({
			closeAction : 'close',
			title		: "Bookmark or sharable link",
			html		: '<b>Use this link to re-run your search:</b><br> <a target="_blank" href="'+url+'">'+url+'</a>',
			width		: 650,
			padding		: 10
		});
		win.show();
	},
	
	// TODO
	getDownloadLink : function() {
		// TODO: Refactor!
		// That was a quick way to reuse bookmarkable link logic. We should take shared code out into a separate function.
		var url = this.getBookmarkableLink();
		if (url !== null) {
			url = url.replace('metaheatmap.html','downloadText/downloadMetaheatmapData.html');
		}
		return url;
	},		
	
	// TODO			
	getApplicationStateFromURL : function (url) {
	}
		
});