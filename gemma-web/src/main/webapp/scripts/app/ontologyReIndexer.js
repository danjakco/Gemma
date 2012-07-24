function handleFailure(data, e) {
	Ext.DomHelper.overwrite("taskId", "");
	Ext.DomHelper.overwrite("messages", {
				tag : 'img',
				src : '/Gemma/images/icons/warning.png'
			});
	Ext.DomHelper.append("messages", {
				tag : 'span',
				html : "&nbsp;There was an error: " + data
			});
}

function handleSuccess(data) {
	try {
		taskId = data;
		Ext.DomHelper.overwrite("messages", "");
		var p = new progressbar({
					taskId : taskId
				});

		p.createIndeterminateProgressBar();
		p.on('fail', handleFailure);
		p.on('cancel', reset);
		p.startProgress();
	} catch (e) {
		handleFailure(data, e);
		return;
	}
}

function reinitializeOntologyIndices(event) {

	var delegate = handleSuccess.createDelegate(this, [], true);
	var errorHandler = handleFailure.createDelegate(this, [], true);

	var callParams = [];
	callParams.push({
				callback : delegate,
				errorHandler : errorHandler
			});

	// this should return quickly, with the task id.
	Ext.DomHelper.overwrite("messages", {
				tag : 'img',
				src : '/Gemma/images/default/tree/loading.gif'
			});
	Ext.DomHelper.append("messages", "&nbsp;Submitting job...");
	AnnotationController.reinitializeOntologyIndices.apply(this, callParams);

}

var reinitializeOntologyIndicesForm = function() {

	Ext.form.Field.prototype.msgTarget = 'side';
	var simple = new Ext.FormPanel({
				border : false
			});

	simple.add(new Ext.Button({
				text : "Reinitialize Ontology Indices",
				handler : function(event) {
					Ext.Msg.show({
						title : Gemma.HelpText.CommonWarnings.ReIndexing.title,
						msg : String.format(Gemma.HelpText.CommonWarnings.ReIndexing.text,'ontology'),
						buttons : Ext.Msg.YESNO,
						fn : function(btn, text) {
							if (btn == 'yes') {
								reinitializeOntologyIndices(event);
							}
						},
						scope : this,
						icon : Ext.MessageBox.WARNING
					});
				},
				scope: this
			}));
	simple.render('reinitializeOntologyIndices-form');
};

Ext.onReady(function() {
			reinitializeOntologyIndicesForm();
		});