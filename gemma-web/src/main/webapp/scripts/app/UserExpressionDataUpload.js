Ext.namespace('Gemma');
Ext.form.Field.prototype.msgTarget = 'side';
Ext.BLANK_IMAGE_URL = "/Gemma/images/s.gif";

Gemma.DatasetUploadTool = Ext.extend(Ext.util.Observable, {

			validateForm : function() {
				if (Ext.getCmp('main-form').getForm().isValid()) {
					Ext.getCmp('validate-data-button').enable();
				} else {
					Ext.getCmp('validate-data-button').disable();
				}
			},

			getUploadForm : function() {
				return new Gemma.FileUploadForm({
							title : 'Upload your data file',
							id : 'upload-form',
							style : 'margin : 5px',
							allowBlank : false
						});
			},

			commandObject : {},
			uploadTaskId : 0,

			agree : function() {
				if (Ext.getCmp('agree').getValue()) {
					this.validateForm();
				}
			},

			validate : function() {
				if (Ext.getCmp('main-form').getForm().isValid()) {
					/*
					 * Lock the form.
					 */
					// Ext.getCmp('main-form').disable();
					// Ext.getCmp('upload-form').disable();
					/*
					 * Assemble the command object.
					 */
					this.commandObject.shortName = Ext.getCmp('shortName').getValue();
					this.commandObject.name = Ext.getCmp('name').getValue();
					this.commandObject.description = Ext.getCmp('description').getValue();
					this.commandObject.isRatio = Ext.getCmp('qtype-isratio').getValue();
					if (Ext.getCmp('qtype-islogged').getValue()) {
						this.commandObject.scale = 'LOG2';
					} else {
						this.commandObject.scale = 'LINEAR';
					}

					this.commandObject.taxonId = Ext.getCmp('taxon-combo').getTaxon().data.id;
					this.commandObject.quantitationTypeName = "Value"; // Ext.getCmp('qtype-name').getValue();
					this.commandObject.quantitationTypeDescription = ""; // Ext.getCmp('qtype-description').getValue();

					/*
					 * Send the data to the server, but don't load it. If everything looks okay, show it to the user for
					 * confirmation. If not, tell them what to fix.
					 */

					ExpressionDataFileUploadController.validate(this.commandObject, {
								callback : this.onStartValidation.createDelegate(this)
							});

				} else {
					Ext.Msg.alert("Form is not ready for data validation",
							"The form isn't filled in properly yet. Please review the marked items.");
				}
			},

			onDoneLoading : function(eeId) {
				Ext.getCmp('uploadform').enable();
				Ext.getCmp('main-form').enable();
				var w = new Ext.Window({
							modal : true,
							width : 400,
							closable : false,
							title : "Data loading finished",
							html : "Congratulations! You data was successfully loaded and assigned internal Gemma id "
									+ eeId
									+ ". Click 'ok' to view the details of your new data set, or 'Load another'.",
							buttons : [{
								text : "OK",
								handler : function() {
									w.hide();
									w.destroy();
									window.location = "/Gemma/expressionExperiment/showExpressionExperiment.html?id="
											+ eeId;

								}
							}, {
								text : "Load another",
								handler : function() {
									Ext.getCmp('main-form').getForm().reset();
									Ext.getCmp('upload-form').reset();
									w.hide();
									w.destroy();
								},
								scope : this
							}]
						});

				w.show();
			},

			onStartSubmission : function(taskId) {
				var p = new Gemma.ProgressWidget({
							taskId : taskId
						});

				var w = new Ext.Window({
							modal : true,
							closable : false,
							width : 500,
							items : [p]
						});

				p.on('done', function(payload) {
							this.onDoneLoading(payload);
							w.hide('submit-data-button');
							w.destroy();
							p.destroy();
						}.createDelegate(this));

				p.on('fail', function(message) {
							w.hide('submit-data-button');
							w.destroy();
							p.destroy();
						}.createDelegate(this));

				w.show('submit-data-button');
				p.startProgress();
			},

			onStartValidation : function(taskId) {
				var p = new Gemma.ProgressWidget({
							taskId : taskId
						});

				var w = new Ext.Window({
							modal : true,
							closable : false,
							width : 400,
							items : [p]
						});

				p.on('done', function(payload) {
							this.onValidated(payload);
							w.hide('validate-data-button');
							w.destroy();
							p.destroy();
						}.createDelegate(this));

				p.on('fail', function(message) {
							w.hide('validate-data-button');
							w.destroy();
							p.destroy();
						}.createDelegate(this));

				w.show('validate-data-button');
				p.startProgress();
			},

			onValidated : function(result) {
				// console.log(result);
				if (result.valid) {

					/*
					 * Show some kind of summary - how many rows etc, any mismatches with probes.
					 */
					Ext.Msg.alert("Your data look valid", "Your data matrix had " + result.numRows + " rows, "
									+ result.numColumns + " columns, " + result.numberMatchingProbes
									+ " probes on the array design matched your data, "
									+ result.numberOfNonMatchingProbes + " didn't."
									+ " If this all looks correct to you, click 'submit' to continue", function() {
								/*
								 * We disable the form so the user can't modify it. (problem: mask gets in the way of
								 * alerts?)
								 */
								// Ext.getCmp('main-form').disable();
								// Ext.getCmp('upload-form').disable();
								Ext.getCmp('submit-data-button').enable();

							});

				} else {

					/*
					 * Display a summary of the problems
					 */

					var summary = "";

					if (!result.shortNameIsUnique) {
						summary = "The short name you selected already has been used;";
					}

					if (!result.dataFileIsValidFormat && result.dataFileFormatProblemMessage) {
						summary = summary + result.dataFileFormatProblemMessage + "; ";
					}

					if (!result.arrayDesignMatchesDataFile && result.arrayDesignMismatchProblemMessage) {
						summary = summary + result.arrayDesignMismatchProblemMessage + "; ";
					}

					if (!result.quantitationTypeIsValid && result.quantitationTypeProblemMessage) {
						summary = summary + result.quantitationTypeProblemMessage + "; ";
					}

					Ext.Msg.alert("Data not valid", summary);

					/*
					 * re-enable the form.
					 */
					Ext.getCmp('main-form').enable();
					Ext.getCmp('upload-form').enable();

				}
			},

			submitDataset : function() {

				/*
				 * Submit to the server. Start a progress bar. On the server, we have to do the following steps: 1)
				 * validate everything 2) do the conversion 3) forward the user to the new data set page. Make sure they
				 * get instructions on how to
				 */

				ExpressionDataFileUploadController.load(this.commandObject, {
							callback : this.onStartSubmission.createDelegate(this)
						});

			}
		});

Ext.onReady(function() {
	Ext.QuickTips.init();

	var q = Ext.QuickTips;

	Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

	tool = new Gemma.DatasetUploadTool();

	var uploadForm = tool.getUploadForm();

	var taxonCombo = new Gemma.TaxonCombo({
				id : 'taxon-combo',
				fieldLabel : "Experiment taxon",
				emptyText : "RNA Taxon",
				allowBlank : false,
				isDisplayTaxonSpecies : true
			});
	
	var taxonArrayCombo = new Gemma.TaxonCombo({
				id : 'taxonArray-combo',
				fieldLabel : "Array taxon",	
				emptyText : "Array Taxon ",
				allowBlank : false,
				isDisplayTaxonSpecies : true
	});

	var arrayDesignCombo = new Gemma.ArrayDesignCombo({
				minHeight : 80,
				bodyStyle : 'padding: 10px',
				allowBlank : false,
				width : 500
			});

	/*
	 * Note we have two separate forms: one to do the file upload, and then a second form that pulls everything
	 * together.
	 */

	var form = new Ext.Panel({
		renderTo : 'form',
		width : 600,
		autoHeight : true,
		frame : true,
		title : "Enter expression experiment details",
		items : [{
			xtype : 'panel',
			collapsible : true,
			title : 'Instructions',
			collapsed : false,
			frame : false,
			border : true,
			html : '<ul class="plainList" ><li>Complete all sections of the form, upload your data file (compress it first to speed things up)'
					+ ', and click "Validate data"; once validation is complete you will be able to click "Submit data".</li>'
					+ '<li>Most of the descriptive text you enter can be modified later. '
					+ 'The taxon, array design and the data themselves cannot easily be altered after submission.</li>'
					+ '<li>For help with the file data file format, see '
					+ '<a target="_blank" href="/Gemma/static/expressionExperiment/upload_help.html">this page</a>.</li> '
					+ '<li>The probe identifiers in your file must match those in the array design on record.</li>'
					+ '<li>If you used more than one array type in your study, there may be a "combined" array that will take care of your case. If not, let us know.</li>'
					+ '<li>Problems? Questions? Please <a href="mailto:gemma@ubic.ca">contact us</a></li></ul>'
		}, {

			xtype : 'form',
			id : 'main-form',
			autoHeight : true,
			items : [{
						xtype : 'fieldset',
						title : 'The basics',
						autoHeight : true,
						style : 'margin : 5px',
						bodyStyle : "padding : 10px",
						items : [{
									xtype : 'textfield',
									id : 'shortName',
									fieldLabel : 'Short name',
									emptyText : 'For example, "jones-liver". Must be unique, no spaces please. ',
									width : 400,
									allowBlank : false,
									maxLength : 100,
									minLength : 4,
									validator : function(value) {
										if (value.match(/\s/)) {
											return "Short name must not contain blanks.";
										}
										return true;

									}.createDelegate(this)
								}, {
									xtype : 'textfield',
									fieldLabel : 'Name',
									id : 'name',
									emptyText : 'Enter a longer descriptive name here',
									width : 400,
									maxLength : 255,
									allowBlank : false
								}, {
									xtype : 'textarea',
									id : 'description',
									height : 70,
									width : 400,
									fieldLabel : 'Description',
									allowBlank : true,
									maxLength : 5000,
									emptyText : 'Optionally enter a brief abstract describing your experiment'
								}, taxonCombo]
					}, {
						xtype : 'fieldset',
						style : 'margin : 5px',
						height : 'auto',
						title : "Select the array design you used",

						layout : 'table',
						layoutConfig : {
							columns : 1
						},

						items : [arrayDesignCombo,{
								//create form layout for taxon array combo cell otherwise field label not displayed
								xtype : 'panel',
								layout : 'form',
								items : [taxonArrayCombo]						
							}, {
							id : 'array-design-info-area',
							xtype : 'panel',
							html : "<div style='width:400px,height:100px;overflow :auto;margin: 4px 0px 4px 0px;border:1px #CCC solid ;'"
									+ " id='array-design-info-inner-html'>"
									+ "<p style='color:grey;'>Array design details will be displayed here</p></div>"
								// ,width : 500,
								// style : 'overflow :scroll;margin: 4px 0px 4px 0px;border:solid 1px;',
								// height : 100,
								// readOnly : true
						}, {
							xtype : 'label',
							html : "Don't see your array design listed? Please see "
									+ "<a target='_blank' href='/Gemma/arrays/showAllArrayDesigns.html'>the array design list</a>"
									+ " for more information, "
									+ "or <a href='mailto:gemma@ubic.ca'>let us know</a> about your array design."
						}]
						// fixme add link to report.
					}, new Gemma.QuantitationTypePanel({
								id : 'quantitation-type-panel',
								style : 'margin : 5px'
							}), {
						xtype : 'fieldset',
						id : 'availability-form',
						title : 'Security/availability information',
						labelWidth : 200,
						autoHeight : true,
						items : [{
									xtype : 'numberfield',
									enableKeyEvents : true,
									minLength : 7,
									maxLength : 9,
									allowNegative : false,
									id : 'pubmedid',
									allowDecimals : false,
									fieldLabel : 'Pubmed ID',
									boxLabel : "If provided, your data will be made publicly viewable"
								}, {
									xtype : 'checkbox',
									id : 'public',
									boxLabel : "If checked, your data will immediately be viewable by anybody",
									fieldLabel : "Make my data publicly available"
								}, {
									xtype : 'checkbox',
									id : 'agree',
									enabled : false,
									handler : tool.agree,
									scope : tool,
									fieldLabel : "I have read the '<a target=\'_blank\' href='/Gemma/static/termsAndConditions.html'>terms and conditions</a>'"
								}]
					}]

		}, uploadForm],
		buttons : [{
					id : 'validate-data-button',
					value : 'Validate data',
					handler : tool.validate,
					scope : tool,
					text : "Validate data",
					disabled : false
				}, {
					id : 'submit-data-button',
					value : 'Submit dataset',
					handler : tool.submitDataset,
					scope : tool,
					text : "Submit dataset",
					disabled : true
				}]
	});

	Ext.getCmp('pubmedid').on('keyup', function(e, a) {
				if (!Ext.getCmp('pubmedid').getValue()) {
					Ext.getCmp('public').setValue(false);
					Ext.getCmp('public').enable();
				} else if (Ext.getCmp('pubmedid').isValid()) {
					Ext.getCmp('public').setValue(true);
					Ext.getCmp('public').disable();
					tool.commandObject.pubMedId = Ext.getCmp('pubmedid').getValue()
				} else {
					Ext.getCmp('public').setValue(false);
					Ext.getCmp('public').enable();
				}
			});

	arrayDesignCombo.on('select', function(combo, arrayDesign) {
		Ext.DomHelper
				.overwrite(
						'array-design-info-inner-html',
						"<div style='padding:5px;'><a target='_blank' style='text-decoration:underline' href=\"/Gemma/arrays/showArrayDesign.html?id="
								+ arrayDesign.data.id
								+ "\">"
								+ arrayDesign.data.shortName
								+ " details</a> (opens in new window)<p>Description: "
								+ arrayDesign.data.description
								+ "</p></div>");
		tool.commandObject.arrayDesignIds = [arrayDesign.data.id];
	});

	
	
	taxonCombo.on('select', function(combo, taxon) {		
		//if taxon combo changes should update array taxon combo with the same taxon
		taxonArrayCombo.setTaxon(taxon.data);
		arrayDesignCombo.taxonChanged(taxon.data);
	}.createDelegate(this));

	taxonCombo.on('ready', function(taxon) {
		var task = new Ext.util.DelayedTask(arrayDesignCombo.taxonChanged, arrayDesignCombo, [taxon]);
		task.delay(500);
	}.createDelegate(this));
	
	taxonArrayCombo.on('select', function(combo, taxon) {
		arrayDesignCombo.taxonChanged(taxon.data);
	}.createDelegate(this));

    taxonArrayCombo.on('ready', function(taxon) {
		var task = new Ext.util.DelayedTask(arrayDesignCombo.taxonChanged, arrayDesignCombo, [taxon]);
		task.delay(500);
	}.createDelegate(this));

	uploadForm.on('start', function(result) {
				Ext.getCmp('submit-data-button').disable();
			}.createDelegate(this));

	uploadForm.on('finish', function(result) {
				/*
				 * Get the file information and put it in the value object.
				 */
				if (result.success) {
					if (Ext.getCmp('main-form').getForm().isValid()) {
						Ext.getCmp('validate-data-button').enable();
					}
					tool.commandObject.serverFilePath = result.localFile;
					tool.commandObject.originalFileName = result.originalFile;
				}
			});

});
