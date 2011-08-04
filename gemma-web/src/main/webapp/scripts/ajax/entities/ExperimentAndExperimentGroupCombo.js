/*
 * The Gemma project
 * 
 * Copyright (c) 2008 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 */

Ext.namespace('Gemma');

Gemma.ExperimentAndExperimentGroupCombo = Ext.extend(Ext.form.ComboBox, {			

	name : 'experimentAndExperimentGroupCombo',
	displayField : 'name',
	width : 160,
	listWidth : 450, // ridiculously large so IE displays it properly
	lazyInit: false, //true to not initialize the list for this combo until the field is focused (defaults to true)
	triggerAction: 'all', //run the query specified by the allQuery config option when the trigger is clicked
	allQuery: '', // loading of auto gen and user's sets handled in Controller when query = ''
	
	loadingText : 'Searching...',

	emptyText : "Search experiments by keyword",
	listEmptyText : 'Enter text to search for experiments',
	minChars : 3,
	selectOnFocus : false,
	autoSelect: false,
	forceSelection: true,
	typeAhead: false,
	taxonId:null,
	
	lastQuery: null, // used for query queue fix
	
	mode : 'remote',
	queryDelay : 800, // default = 500
	listeners: {
                specialkey: function(formField, e){  // needed for query queue fix
                    // e.HOME, e.END, e.PAGE_UP, e.PAGE_DOWN,
                    // e.TAB, e.ESC, arrow keys: e.LEFT, e.RIGHT, e.UP, e.DOWN
                    if ( e.getKey() === e.TAB || e.getKey() === e.RIGHT  || e.getKey() === e.DOWN ) {
                        this.expand();
                    }else if (e.getKey() === e.ENTER ) {
                        this.doQuery(this.lastQuery);
                    }else if (e.getKey() === e.ESC ) {
                        this.collapse();
                    }
                },
        		beforequery: function(qe){
            		delete qe.combo.lastQuery;
        		}
    },

	// overwrite ComboBox onLoad function to get rid of query text being selected after a search returns
	// (this was interfering with the query queue fix)
	// only change made is commented out line
    onLoad : function(){
        if(!this.hasFocus){
            return;
        }
        if(this.store.getCount() > 0 || this.listEmptyText){
            this.expand();
            this.restrictHeight();
            if(this.lastQuery == this.allQuery){
                if(this.editable){
                  //  this.el.dom.select();
                }

                if(this.autoSelect !== false && !this.selectByValue(this.value, true)){
                    this.select(0, true);
                }
            }else{
                if(this.autoSelect !== false){
                    this.selectNext();
                }
                if(this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE){
                    this.taTask.delay(this.typeAheadDelay);
                }
            }
        }else{
            this.collapse();
        }

    }, // end onLoad overwrite
	
	/**
	 * Parameters for AJAX call.
	 * 
	 * @param {}
	 *            query
	 * @return {}
	 */
	getParams : function(query) {
		return [query, this.taxonId];
	},
	
	initComponent: function(){
	
		var eeTpl = new Ext.XTemplate('<div style="font-size:11px;background-color:#ECF4FF" class="x-combo-list-item" '+
					'ext:qtip="{name}: {description} ({taxonName})"><b>{name}</b>: {description} <span style="color:grey">({taxonName})</span></div>');
		var modifiedSessionTpl  = new Ext.XTemplate('	<div style="font-size:11px;background-color:#FFFFFF" class="x-combo-list-item" ext:qtip="{name}:'+
					' {description} ({size}) ({taxonName})"><b>{name}</b>:  <span style="color:red">Unsaved</span> {description} ({size}) <span style="color:grey">({taxonName})</span></div>');
		var freeTxtTpl = new Ext.XTemplate('<div style="font-size:11px;background-color:#FFFFE3" class="x-combo-list-item" ext:qtip="{name}: {description} ({size}) ({taxonName})">'+
					'<b>{name}</b>: {description} ({size}) <span style="color:grey">({taxonName})</span></div>');
		var userOwnedDbSetTpl  = new Ext.XTemplate('<div style="font-size:11px;background-color:#FFECEC" class="x-combo-list-item" ext:qtip="{name}: {description} ({size}) ({taxonName})">'+
					'<b>{name}</b>: {description} ({size}) <span style="color:grey">({taxonName})</span></div>');
		var dbSetTpl  = new Ext.XTemplate('<div style="font-size:11px;background-color:#EBE3F6" class="x-combo-list-item" ext:qtip="{name}: {description} ({size}) ({taxonName})">'+
					'<b>{name}</b>: {description} ({size}) <span style="color:grey">({taxonName})</span></div>');
		var sessionSetTpl = dbSetTpl;
		var defaultTpl = dbSetTpl;
		Ext.apply(this, {
			// format fields to show in combo, only show size in brackets if the entry is a group
			tpl: new Ext.XTemplate('<tpl for=".">' +
				'{[ this.renderItem(values) ]}' +
			'</tpl>',{
				renderItem: function(values){
					if (values.resultValueObject instanceof ExpressionExperimentValueObject) {
						return eeTpl.apply(values);
					}else if (values.resultValueObject instanceof DatabaseBackedExpressionExperimentSetValueObject) {
						if (values.userOwned) {
							return userOwnedDbSetTpl.apply(values)
						} else {
							return dbSetTpl.apply(values);
						}
					}else if (values.resultValueObject instanceof FreeTextExpressionExperimentResultsValueObject) {
						return freeTxtTpl.apply(values);
					}else if (values.resultValueObject instanceof SessionBoundExpressionExperimentSetValueObject) {
						if (values.resultValueObject.modified) {
							return modifiedSessionTpl.apply(values);
						}else {
							return sessionSetTpl.apply(values);
						}
					}
					return defaultTpl.apply(values);
				}
			}),
			store: {
				reader: new Ext.data.ListRangeReader({}, Ext.data.Record.create([{
					name: "name",
					type: "string"
				}, {
					name: "description",
					type: "string"
				}, {
					name: "isGroup",
					type: "boolean"
				}, {
					name: "size",
					type: "int"
				}, {
					name: "taxonId",
					type: "int",
					defaultValue: "-1"
				}, {
					name: "taxonName",
					type: "string",
					defaultValue: ""
				}, {
					name: "memberIds",
					defaultValue: []
				}, {
					name: "resultValueObject"
				}])),
				proxy: new Ext.data.DWRProxy(ExpressionExperimentController.searchExperimentsAndExperimentGroups),
				autoLoad: false
			}
		});
		
		Gemma.ExperimentAndExperimentGroupCombo.superclass.initComponent.call(this);
		
		this.on('select', this.setExpressionExperimentGroup, this);
		
		
		/***** start of query queue fix *****/
		// this makes sure that when older searches return AFTER newer searches, the newer results aren't bumped
		// this needs the lastQuery property to be initialised as null
		this.getStore().on('beforeload', function(store, options){
			this.records = this.store.getRange();
		}, this);
		
		this.getStore().on('load', function(store, records, options){
			var query = (options.params) ? options.params[0] : null;
			if ((query === null && this.lastQuery !== null) || (query !== '' && query !== this.lastQuery)) {
				store.removeAll();
				store.add(this.records);
				if (this.records === null || this.records.length === 0) {
					this.doQuery(this.lastQuery);
				}
			}
			else if(this.store && typeof this.store ==! 'undefined'){
				this.records = this.store.getRange();
			}
		}, this);
		/***** end of query queue fix *****/
		
		this.on('focus', function(field){
			// if the text field is blank, show the automatically generated groups (like 'All human', 'All rat' etc)
			if (this.getValue() === '') {
				
				/*
				// passing in taxon instead of taxonId breaks this call
				ExpressionExperimentController.searchExperimentsAndExperimentGroups("", this.taxonId, function(records){
					this.getStore().loadData(records);
				}.createDelegate(this));
				*/
				this.doQuery('',true);
				this.lastQuery = null; // needed for query queue fix
			}
		}, this);
	},

	reset : function() {
		Gemma.ExperimentAndExperimentGroupCombo.superclass.reset.call(this);
		delete this.selectedExpressionExperimentGroup;
		this.lastQuery = null;

		if (this.tooltip) {
			this.tooltip.destroy();
		}
	},

	getExpressionExperimentGroup : function() {
		if (this.getRawValue() === ''){
			return null;
		}
		return this.selectedExpressionExperimentGroup;
	},
	
	getSelected : function() {
		return this.getExpressionExperimentGroup();
	},

	setExpressionExperimentGroup : function(combo, expressionExperimentGroup, index) {
		//this.reset();
		this.selectedExpressionExperimentGroup = expressionExperimentGroup.data;
		this.lastQuery = null;

	},
	setTaxonId: function(id){
		this.taxonId = id;	
	}
});