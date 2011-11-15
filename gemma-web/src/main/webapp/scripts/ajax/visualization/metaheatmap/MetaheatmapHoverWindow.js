Ext.namespace('Gemma.Metaheatmap');

	  // a window for displaying details as elements of the image are
	  // hovered over

Gemma.Metaheatmap.HoverWindow = Ext.extend ( Ext.Window, {
				  
	//height : 200,
	width  : 350,
	closable   : false,
	shadow 	   : false,
	border 	   : false,
	bodyBorder : false,
	//hidden	   : true,
	//	bodyStyle  : 'padding: 7px',

	isDocked : false, // 
	
	tplWriteMode : 'overwrite',

	initComponent : function () {
		Gemma.Metaheatmap.HoverWindow.superclass.initComponent.apply ( this, arguments );

		this.tpl = this.initTemplate_();		
	},		
	
	initTemplate_ : function () {
		return new Ext.XTemplate (
				'<span style="font-size: 12px ">',
				'<tpl for=".">',
				'<tpl if="type==\'condition\'">',   //Experiment
				'<b>Experiment</b>: {datasetShortName}, {datasetName}<br><br>',
				'<b>Factor</b>:{factorCategory} - {factorDescription}<br><br> ',
				'<b>Specificity</b>: {specificityPercent}% of probes were differentially expressed under this condition ({numberDiffExpressedProbes} out of {numberOfProbesOnArray})<br><br> ',
				'</tpl>',
				'<tpl if="type==\'minipie\'">',     //minipie
				'{percentProbesDiffExpressed} of probes are differentially expressed.<br><br>',
				'({numberOfProbesDiffExpressed} of {numberOfProbesTotal}) Click for details.',
				'</tpl>',
				'<tpl if="type==\'gene\'">',		//gene
				'<b>Gene</b>: {geneSymbol} {geneFullName}<br>',
				'</tpl>',
				'<tpl if="type==\'contrastCell\'">',  //contrast
				'<b>Gene</b>: {geneSymbol} {geneFullName}<br><br> ',
				'<b>Experiment</b>: {datasetShortName}, {datasetName}<br><br>',
				'<b>Factor</b>:{factorCategory} - {factorDescription}<br><br> ', 
				'<b>Log2 fold change</b>: {foldChange:sciNotation}<br>', 
				'<b>pValue</b>: {contrastPvalue:sciNotation} <br>',
				'</tpl>',
				'<tpl if="type==\'cell\'">',		 //cell
				'<b>Gene</b>: {geneSymbol} {geneFullName}<br><br> ',
				'<b>Experiment</b>: {datasetShortName}, {datasetName}<br><br>',
				'<b>Factor</b>:{factorCategory} - {factorDescription}<br><br> ',
				'<b>p-value</b>: {pvalue:sciNotation}<br><br>',
				'<b>log fold change</b>: {foldChange:sciNotation}',
				'</tpl>', '</tpl></span>');
	},

	onRender: function() {
		Gemma.Metaheatmap.HoverWindow.superclass.onRender.apply ( this, arguments );
	}
	
});

Ext.reg('Metaheatmap.HoverWindow',Gemma.Metaheatmap.HoverWindow);
