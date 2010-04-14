Ext.namespace('Gemma');
Ext.BLANK_IMAGE_URL = '/Gemma/images/default/s.gif';

/**
 * Provides functionallity for creating and managing Gene groups inside of Gemma.
 * 
 * @author klc
 * @version $Id$
 */
Ext.onReady(function() {

			Ext.QuickTips.init();
			Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

			/*
			 * The GUI
			 */

			var geneGroupImporter = new Gemma.GeneGroupImporter({
						renderTo : 'genesetCreation-div',
						width : "100%"
					});

		});

// Displays the current users Gene Groups
Gemma.GeneGroupPanel = Ext.extend(Ext.grid.EditorGridPanel, {

	loadMask : true,
	stateful : false,
	selModel : new Ext.grid.RowSelectionModel({
		singleSelect : true,
		listeners : {
			'selectionChange' : {
				fn : function(selmod) {

					var sel = selmod.getSelected();

					if (!sel) {
						return;
					}
					var selectedGeneGroupId = sel.data.id;
					var genePanel = Ext.getCmp('gene-chooser-panel');

					// Need to store these for retrevial later else they might
					// end up being for the row that just got selected (Love
					// asyncronous languages!)
					var currentGroupSize = genePanel.currentGroupSize;
					var modifiedGroupSize = genePanel.getGeneIds().size();

					// Figure out if we need to ask them to save their changes
					// before navigating away from the group they began
					// editing...
					if (genePanel.currentGroupId) { // This is only relevant the
						// 1st time something is
						// selected
						if (currentGroupSize != modifiedGroupSize) {
							var gids = genePanel.getGeneIds();
							var groupId = genePanel.currentGroupId;

							var confirmSaveChanges = function(btn) {

								if (btn == 'yes') {

									GeneSetController.updateGeneGroup(groupId, gids, {
												callback : function(d) {
													refreshGeneGroupData();
												},
												errorHandler : function(e) {
													Ext.Msg.alert('Sorry', e);
												}
											});

								}
							};

							Ext.Msg.show({
								title : 'Save changes?',
								msg : 'The gene group currently being viewed has been modified.  Would you like to save the changes before navigating away?',
								buttons : Ext.Msg.YESNO,
								fn : confirmSaveChanges,
								animEl : 'elId',
								icon : Ext.MessageBox.QUESTION
							});

						}
					}
					GeneSetController.getGenesInGroup(selectedGeneGroupId, function(geneValueObjs) {

								// If no genes in gene set, enable taxon
								// selction
								genePanel.currentGroupId = selectedGeneGroupId;

								if (!geneValueObjs || geneValueObjs.size() === 0) {
									genePanel.getTopToolbar().taxonCombo.reset();
									genePanel.getTopToolbar().geneCombo.reset();
									genePanel.getTopToolbar().taxonCombo.setDisabled(false);
									genePanel.fireEvent("taxonchanged", null);
									genePanel.loadGenes([]);
									genePanel.currentGroupSize = 0;
									return;
								}

								genePanel.currentGroupSize = geneValueObjs.size();

								var geneIds = [];
								var taxonId = geneValueObjs[0].taxonId;
								for (var i = 0; i < geneValueObjs.length; i++) {
									if (taxonId != geneValueObjs[0].taxonId) {
										Ext.Msg
												.alert(
														'Sorry.  Gene groups do not support mixed taxa. Please remove this gene group',
														response);
									}
									geneIds.push(geneValueObjs[i].id);
								}

								var groupTaxon = {
									id : taxonId,
									commonName : geneValueObjs[0].taxonName
								};
								genePanel.getTopToolbar().taxonCombo.setTaxon(groupTaxon);
								genePanel.getTopToolbar().geneCombo.setTaxon(groupTaxon);
								genePanel.getTopToolbar().taxonCombo.setDisabled(true);
								genePanel.loadGenes(geneIds);
							});
				}
			}
		}
	}),
	store : new Ext.data.Store({
				autoLoad : false,
				name : "geneGroupData-store",
				proxy : new Ext.data.DWRProxy(GeneSetController.getUsersGeneGroups),
				reader : new Ext.data.ListRangeReader({}, Ext.data.Record.create([{
									name : "id",
									type : "int"
								}, {
									name : "name",
									type : "string"
								}, {
									name : "description",
									type : "string"
								}, {
									name : "owner"
								}, {
									name : "isPublic",
									type : "boolean"
								}, {
									name : "isShared",
									type : "boolean"
								}, {
									name : "size",
									type : "int"
								}])),

				listeners : {
					"exception" : function(proxy, type, action, options, response, arg) {
						Ext.Msg.alert('Sorry', response);
					}
				}
			}),
	columns : [{
				header : 'Name',
				dataIndex : 'name',
				editable : false,
				groupable : false,
				sortable : true
			}, {
				header : 'Description',
				dataIndex : 'description',
				editable : true,
				groupable : false,
				sortable : true,
				editor : new Ext.form.TextField()
			}, {
				header : 'Owner',
				tooltip : 'Who owns the data',
				dataIndex : 'owner',
				groupable : true,
				sortable : true,
				renderer : function(value, metaData, record, rowIndex, colIndex, store) {
					return value.authority ? value.authority : value;
				},
				editor : new Ext.form.ComboBox({
							typeAhead : true,
							displayField : "authority",
							triggerAction : 'all',
							lazyRender : true,
							store : new Ext.data.Store({
										proxy : new Ext.data.DWRProxy(SecurityController.getAvailablePrincipalSids),
										reader : new Ext.data.ListRangeReader({}, Ext.data.Record.create([{
															name : "authority"
														}, {
															name : "principal",
															type : "boolean"
														}])),
										listeners : {
											"exception" : function(proxy, type, action, options, response, arg) {
												Ext.Msg.alert('Sorry', response);
											}
										}
									})
						})
			}, {
				header : 'size',
				sortable : true,
				dataIndex : 'size',
				editable : false,
				groupable : false,
				tooltip : 'number of genes in group'
			}, {
				header : 'Flags',
				sortable : true,
				renderer : function(value, metadata, record, rowIndex, colIndex, store) {

					// Not sure why record.get("publik") returns null and
					// record.data is an empty string but record.json has my
					// data. Data is in GeneSetValueObject and all other
					// information is there on the server. Looking at record
					// object in debugger i found my variables
					// encouded as json. Not sure why....

					var result = Gemma.SecurityManager.getSecurityLink("ubic.gemma.model.genome.gene.GeneSetImpl",
							record.json.id, record.json.publik, record.json.shared);
					return result;

				},
				tooltip : 'Click to edit permissions'
			}

	]
});

// Shows the genes that are currently in a given group and allows genes to be
// added or removed from the selected group
Gemma.GeneGroupImporter = Ext.extend(Ext.Panel, {

			initComponent : function() {

				this.geneChooserPanel = new Gemma.GeneGrid({
							region : 'east',
							id : 'gene-chooser-panel'
						});

				this.geneGroupPanel = new Gemma.GeneGroupPanel({
							id : 'gene-group-panel',
							region : 'center'
						});

				Ext.apply(this.geneChooserPanel.getTopToolbar().taxonCombo, {
							stateId : "",
							stateful : false,
							stateEvents : []
						});

				Ext.apply(this, {
							layout : 'border',
							// width : "100%",
							height : 400,
							title : "Gene Group Manager",
							tbar : {
								items : [{
									tooltip : "Create New Group",
									icon : "/Gemma/images/icons/group_add.png",
									id : 'geneimportgroup-save-btn',
									handler : function(b, e) {

										Ext.Msg.prompt('New Group', 'Please enter the group name:',
												function(btn, text) {
													if (btn == 'ok') {

														GeneSetController.createGeneGroup(text, [], {
																	callback : function(groupId) {

																		var genePanel = Ext
																				.getCmp('gene-chooser-panel');
																		genePanel.currentGroupId = null;
																		genePanel.loadGenes([]);
																		refreshGeneGroupData(groupId);
																		genePanel.getTopToolbar().geneCombo.focus();

																	},
																	errorHandler : function(e) {
																		Ext.Msg.alert('Sorry', e);
																	}
																});
													}
												});

									}
								}, {
									tooltip : "Save changes",
									icon : "/Gemma/images/icons/database_save.png",
									id : 'manager-data-panel-save-btn',
									handler : function(b, e) {

										var rec = Ext.getCmp("gene-group-panel").getSelectionModel().getSelected();
										if (rec) {

											// update description field if necessary
											// console.log(rec.data.description);

											// Update the genes incase they changed also. Can
											// only update the genes for 1 list.
											var loadMask = new Ext.LoadMask(Ext.getCmp("gene-group-panel").getEl(), {
														msg : "Saving changes..."
													});
											loadMask.show();

											var geneIds = Ext.getCmp("gene-chooser-panel").getGeneIds();

											GeneSetController.updateGeneGroup(rec.data.id, rec.data.description,
													geneIds, {
														callback : function(d) {
															// so that system doesn't ask to save again
															Ext.getCmp('gene-chooser-panel').currentGroupId = null;
															refreshGeneGroupData();
															loadMask.hide()
														},
														errorHandler : function(e) {
															Ext.Msg.alert('Sorry', e);
														}
													});

										}

									}
								}, {
									tooltip : "Refresh from the database",
									icon : "/Gemma/images/icons/arrow_refresh_small.png",
									handler : function() {
										refreshGeneGroupData();
									}
								}, {
									tooltip : "Show/hide public data",
									id : "geneGroupData-show-public",
									enableToggle : true,
									icon : "/Gemma/images/icons/world_add.png",
									handler : function() {
										refreshGeneGroupData();
									}
								}, {
									icon : "/Gemma/images/icons/group_delete.png",
									tooltip : "Delete a group",

									handler : function() {

										var sel = Ext.getCmp('gene-group-panel').getSelectionModel().getSelected();
										var groupId = sel.get("id");
										var groupName = sel.get("name");

										var processResult = function(btn) {

											if (btn == 'yes') {

												var loadMask = new Ext.LoadMask(Ext.getCmp("gene-group-panel").getEl(),
														{
															msg : "Saving changes..."
														});
												loadMask.show();

												GeneSetController.deleteGeneGroup(groupId, {
															callback : function() {
																refreshGeneGroupData();

																Ext.getCmp('gene-chooser-panel').currentGroupId = null;
																Ext.getCmp('gene-group-panel').getSelectionModel()
																		.selectFirstRow();
																loadMask.hide();
															},
															errorHandler : function(e) {
																Ext.Msg.alert('Sorry', e);
															}
														});
											}
										};

										Ext.Msg.show({
													title : 'Are you sure?',
													msg : 'The group "' + groupName + " with id " + groupId
															+ '" will be permanently deleted. This cannot be undone.',
													buttons : Ext.Msg.YESNO,
													fn : processResult,
													animEl : 'elId',
													icon : Ext.MessageBox.QUESTION
												});

									}
								}]
							},// toolbar
							items : [this.geneGroupPanel, this.geneChooserPanel]
						});

				Gemma.GeneGroupImporter.superclass.initComponent.call(this);

				refreshGeneGroupData();

			}
		});

// TODO This code shoudl be encorperated into the widget so it can be reused and
// not have to exist as a static entity outside of the widget's scope
refreshGeneGroupData = function(groupId) {
	var showPrivateOnly = !Ext.getCmp("geneGroupData-show-public").pressed;
	Ext.getCmp('gene-group-panel').getStore().load({
				params : [showPrivateOnly], 
				callback : function(){	//for selecting the desired row
					console.log(groupId);
					if (!groupId){
						return;
					}
					
					var groupPanel = Ext.getCmp('gene-group-panel');
					var row = groupPanel.getStore().findExact(id,groupId);
					groupPanel.getSelectionModel().selectRow(row, false);
				
				}
			});
};