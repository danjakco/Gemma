/*
 * The input for differential expression searches. This form has two main parts: a GeneChooserPanel, and the differential expression search parameters.
 * 
 * Differential expression search has one main setting, the threshold.
 * 
 * If scope=custom, a DatasetSearchField is shown.
 * 
 * @author keshav
 * @version $Id$
 */
Ext.Gemma.DiffExpressionSearchForm = function ( config ) {

	var thisPanel = this;
	
	/* establish default config options...
	 */
	var superConfig = {
		width : 550,
		frame : true,
		stateful : true,
		stateEvents : [ "beforesearch" ],
		stateId : "Ext.Gemma.DiffExpressionSearch", // share state with main page...
		defaults : { }
	};
	
	/* apply user-defined config options and call the superclass constructor...
	 */
	for ( property in config ) {
		superConfig[property] = config[property];
	}
	Ext.Gemma.DiffExpressionSearchForm.superclass.constructor.call( this, superConfig );
	
	
	/*
	 * Gene settings
	 */
	this.geneChooserPanel = new Ext.Gemma.GeneChooserPanel( { 
		showTaxon : true 
	} );
	this.geneChooserPanel.taxonCombo.on( "taxonchanged", function ( combo, taxon ) {
		this.taxonChanged( taxon );
	}, this );
	
	var queryFs = new Ext.form.FieldSet( {
		title : 'Query gene(s)',
		autoHeight : true 
	} );
	queryFs.add( this.geneChooserPanel );
	
	// Window shown when the user wants to see the experiments that are 'in play'.
	var activeDatasetsWindow = new Ext.Window({
			el : 'diffExpression-experiments',
			title : "Active datasets",
			modal : true,
			layout : 'fit',
			autoHeight : true,
			width : 600,
			closeAction:'hide',
			easing : 3, 
            buttons: [{ 
               text: 'Close',
               handler: function(){ 
                   activeDatasetsWindow.hide();  
               }
            }]
			
		});
 		
 	this.thresholdField = new Ext.form.NumberField( {
		allowBlank : false,
		allowDecimals : true,
		allowNegative : false,
		minValue : Ext.Gemma.DiffExpressionSearchForm.MIN_THRESHOLD,
		maxValue : 1,
		fieldLabel : 'Threshold',
		invalidText : "Minimum threshold is " + Ext.Gemma.DiffExpressionSearchForm.MIN_THRESHOLD,
		value : 0.01,
		width : 60
	} ); 
	Ext.Gemma.DiffExpressionSearchForm.addToolTip( this.thresholdField, 
		"Only genes with a qvalue less than this threshold are returned." );
 	
	// Field set for the bottom part of the form
	var analysisFs = new Ext.form.FieldSet( {  
		autoHeight : true,
	  	items : [ this.thresholdField] //
	} );
	
 	// Panel combining all of the above elements.
	var optionsPanel = new Ext.Panel({
		title : 'Analysis options',
		autoHeight : true,
		items : [ analysisFs]
	});
	
	this.optionsPanel = optionsPanel;
	
	var submitButton = new Ext.Button( {
		text : "Find diff expressed genes",
		handler : function() {
			thisPanel.doSearch.call( thisPanel );
		}
	} );
	
	
	/*
	 * Build the form
	 */
	this.add( queryFs );
	this.add( optionsPanel); 
	this.addButton( submitButton );

	Ext.Gemma.DiffExpressionSearchForm.searchForGene = function( geneId ) {
		geneChooserPanel.setGene.call( geneChooserPanel, geneId, thisPanel.doSearch.bind( thisPanel ) );
	};
};

Ext.Gemma.DiffExpressionSearchForm.addToolTip = function( component, html ) {
	component.on( "render", function() {
		component.gemmaTooltip = new Ext.ToolTip( {
			target : component.getEl(),
			html : html
		} );
	} );
};

/* other public methods...
 */
Ext.extend( Ext.Gemma.DiffExpressionSearchForm, Ext.FormPanel, {

	applyState : function( state, config ) {
		if ( state ) {
			this.dsc = state;
		}
	},

	getState : function() {
		return this.getDiffExpressionSearchCommand();
	},

	initComponent : function() {
        Ext.Gemma.DiffExpressionSearchForm.superclass.initComponent.call(this);
        
        this.addEvents(
            'beforesearch',
            'aftersearch'
        );
    },
    
    render : function ( container, position ) {
		Ext.Gemma.DiffExpressionSearchForm.superclass.render.apply(this, arguments);
    	
    	if ( ! this.loadMask ) {
			this.createLoadMask();
		}
		
		// initialize from state
		if ( this.dsc ) {
			this.initializeFromDiffExpressionSearchCommand( this.dsc );
		}
		
    },
	
	createLoadMask : function () {
		this.loadMask = new Ext.LoadMask( this.getEl() );
	},

	getDiffExpressionSearchCommand : function () {
		var dsc = {
			geneIds : this.geneChooserPanel.getGeneIds(),
			taxonId : this.geneChooserPanel.getTaxonId(),
			threshold : this.thresholdField.getValue()
		};
		
		return dsc;
	},
	
	initializeFromDiffExpressionSearchCommand : function ( dsc, doSearch ) {
		/* make the form look like it has the right values;
		 * this will happen asynchronously...
		 */
		if ( dsc.taxonId ) {
			this.geneChooserPanel.taxonCombo.setValue( dsc.taxonId );
		}
		if ( dsc.geneIds.length > 1 ) {
			this.geneChooserPanel.loadGenes( dsc.geneIds );
		} else {
			this.geneChooserPanel.setGene( dsc.geneIds[0] );
		}
		if ( dsc.threshold ) {
			this.thresholdField.setValue( dsc.threshold );
		}
		
		/* perform the search with the specified values...
		 */
		if ( doSearch ) {
			this.doSearch( dsc );
		}
	},
	
	getBookmarkableLink : function ( dsc ) {
		if ( ! dsc ) {
			dsc = this.getDiffExpressionSearchCommand();
		}
		var queryStart = document.URL.indexOf( "?" );
		var url = queryStart > -1 ? document.URL.substr( 0, queryStart ) : document.URL;
		url += String.format( "?g={0}&s={1}", dsc.geneIds.join( "," ), dsc.stringency );
		if ( dsc.queryGenesOnly ) {
			url += "&q";
		}
		if ( dsc.eeIds ) {
			url += String.format( "&ees={0}", dsc.eeIds.join( "," ) );
		} else {
			url += String.format( "&a={0}", dsc.cannedAnalysisId );
		}
		
		if (dsc.eeQuery) {
			url += "&eeq=" + dsc.eeQuery;
		}
		
		return url;
	},

	doSearch : function ( dsc ) {
		if ( ! dsc ) {
			dsc = this.getDiffExpressionSearchCommand();
		}
		this.clearError();
		var msg = this.validateSearch( dsc );
		if ( msg.length === 0 ) {
			if ( this.fireEvent('beforesearch', this, dsc ) !== false ) {
				this.loadMask.show();
				var errorHandler = this.handleError.createDelegate(this, [], true);
				DifferentialExpressionSearchController.getDiffExpressionForGenes( dsc, {callback : this.returnFromSearch.bind( this ), errorHandler : errorHandler} );
			}
		} else {
			this.handleError(msg);
		}
	},
	
	handleError : function( msg, e ) {
		Ext.DomHelper.overwrite("diffExpression-messages", {tag : 'img', src:'/Gemma/images/icons/warning.png' }); 
		Ext.DomHelper.append("diffExpression-messages", {tag : 'span', html : "&nbsp;&nbsp;"  + msg });  
		this.loadMask.hide();
	},
	
	clearError : function () {
		Ext.DomHelper.overwrite("diffExpression-messages", "");
	},
	
	validateSearch : function ( dsc ) {
		if ( dsc.geneIds.length < 1 ) {
			return "We couldn't figure out which gene you want to query. Please use the search functionality to find genes.";
		} else if ( dsc.threshold < Ext.Gemma.DiffExpressionSearchForm.MIN_THRESHOLD ) {
			return "Minimum threshold is " + Ext.Gemma.DiffExpressionSearchForm.MIN_THRESHOLD;
		} else if ( dsc.threshold > Ext.Gemma.DiffExpressionSearchForm.MAX_THRESHOLD ) {
			return "Maximum threshold is " + Ext.Gemma.DiffExpressionSearchForm.MAX_THRESHOLD;
		} else {
			return "";
		}
	},
	
	returnFromSearch : function ( result ) {
		this.loadMask.hide();
		this.fireEvent( 'aftersearch', this, result );
	},
	
	taxonChanged : function ( taxon ) {
		this.geneChooserPanel.taxonChanged( taxon );
	},
	
	getActiveEeIds : function() {
		return this.eeIds;
	}
	
} );

Ext.Gemma.DiffExpressionSearchForm.MIN_STRINGENCY = 0.0;
Ext.Gemma.DiffExpressionSearchForm.MAX_STRINGENCY = 1.0;