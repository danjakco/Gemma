Ext.namespace('Gemma');

/**
 *
 *
 * @class
 */
Gemma.ObservableCoexpressionSearchResults = Ext.extend( Ext.util.Observable, {
    searchCommandUsed: {}, // type CoexpressionSearchCommand
    searchResults:{},
    cytoscapeSearchResults:{},
    cytoscapeResultsUpToDate: false,
    coexSearchTimeout: 420000,

    initComponent: function () {
        this.searchParameters.stringency =
            Gemma.CytoscapePanelUtil.restrictResultsStringency( this.searchParameters.displayStringency );

        Gemma.ObservableCoexpressionSearchResults.superclass.initComponent.call(this);
        this.addEvents('search-results-ready',
            'complete-search-results-ready',
            'search-error');
    },

    constructor: function (configs) {
        if (typeof configs !== 'undefined') {
            Ext.apply(this, configs);
        }
        Gemma.ObservableCoexpressionSearchResults.superclass.constructor.call(this);
    },

    getCoexpressionPairs: function () {
        return this.searchResults.knownGeneResults;
    },

    getNonQueryGeneTrimmedValue: function() {
        return this.cytoscapeSearchResults.nonQueryGeneTrimmedValue;
    },

    getCytoscapeCoexpressionPairs: function () {
        return this.cytoscapeSearchResults.knownGeneResults;
    },

    setCytoscapeCoexpressionPairs: function (coexPairs) {
        this.cytoscapeSearchResults.knownGeneResults = coexPairs;
    },

    getCytoscapeGeneSymbolsMatchingQuery: function (query) {
        var genePairs = this.cytoscapeSearchResults.knownGeneResults;
        var genesMatchingSearch = [];
        var queries = query.split(",");
        var i, j;
        for (j = 0; j < queries.length; j++) {

            queries[j] = queries[j].replace(/^\s+|\s+$/g, '');

            if (queries[j].length < 2) { // too short
                continue;
            }
            var queryRegEx = new RegExp(Ext.escapeRe(queries[j]), 'i');

            for (i = 0; i < genePairs.length; i++) {
                var foundGene = genePairs[i].foundGene;
                var queryGene = genePairs[i].queryGene;

                if (genesMatchingSearch.indexOf(foundGene.officialSymbol) !== 1) {
                    if (queryRegEx.test(foundGene.officialSymbol) ||
                        queryRegEx.test(foundGene.officialName)) {
                        genesMatchingSearch.push(foundGene.id);
                    }
                }

                if (genesMatchingSearch.indexOf(queryGene.officialSymbol) !== 1) {
                    if (queryRegEx.test(queryGene.officialSymbol) ||
                        queryRegEx.test(queryGene.officialName)) {
                        genesMatchingSearch.push(queryGene.id);
                    }
                }
            }
        }
        return genesMatchingSearch;
    },

    getResultsStringency: function () {
        return this.searchCommandUsed.stringency;
    },

    getQueryGeneIds: function () {
        return this.searchCommandUsed.geneIds;
    },

    getQueryGenes: function () {
        return this.searchResults.queryGenes;
    },

    getTaxonId: function () {
        return this.searchCommandUsed.taxonId;
    },

    searchForCytoscapeDataWithStringency : function (newStringency) {
        var resultsStringency = Gemma.CytoscapePanelUtil.restrictResultsStringency( newStringency );

        Ext.apply(
            this.coexpressionSearchData.cytoscapeCoexCommand, {
                stringency: resultsStringency,
                displayStringency: stringencyValue,
                queryGenesOnly: true
            });

        this.searchForCytoscapeData();
    },

    searchWithGeneIds: function (geneIds) {
        var coexpressionSearchCommand = {
            geneIds : geneIds,
            eeIds : this.searchCommandUsed.eeIds,
            stringency : 2,
            forceProbeLevelSearch : false,
            useMyDatasets : false,
            queryGenesOnly : false,
            taxonId : this.searchCommandUsed.taxonId,
            eeSetName : null,
            eeSetId : null
        };
        this.search(coexpressionSearchCommand);
    },

    search : function (searchCommand) {
        this.searchCommandUsed = searchCommand;
        var me = this;
        ExtCoexpressionSearchController.doBackgroundCoexSearch(
            searchCommand,
            {
                callback: function (taskId) {
                    var task = new Gemma.ObservableSubmittedTask({'taskId': taskId});
                    task.showTaskProgressWindow({});
                    Ext.getBody().unmask();
                    task.on('task-completed', function(results) {
                        me.searchResults = results;
                        me.cytoscapeResultsUpToDate = false;
                        me.fireEvent('aftersearch');
                        me.fireEvent('search-results-ready', results);
                    });
                    task.on('task-failed', function(error) {
                        me.fireEvent('aftersearch', error, true);
                    });
                    task.on('task-cancelling', function(error) {
                        me.fireEvent('aftersearch', error, true);
                    });
                },
                errorHandler: function (error) {
                    me.fireEvent('aftersearch', error);
                    Ext.Msg.alert(Gemma.HelpText.CommonWarnings.Timeout.title, Gemma.HelpText.CommonWarnings.Timeout.text);
                } // sometimes got triggered without timeout
            }
        );
    },

    /**
     *
     */
    searchForCytoscapeData: function () {
        var geneIdsSubset = Gemma.CytoscapePanelUtil.restrictQueryGenesForCytoscapeQuery( this );

        // Construct searchcommand
        // to grab coexpression info for found genes
        var coexpressionSearchCommand = {
            geneIds : geneIdsSubset,
            eeIds : this.searchCommandUsed.eeIds,
            stringency : this.searchCommandUsed.stringency,
            forceProbeLevelSearch : false,
            useMyDatasets : false,
            queryGenesOnly : true,
            taxonId : this.searchCommandUsed.taxonId,
            eeSetName : null,
            eeSetId : null
        };

        if (geneIdsSubset.length < 2) {
            // There is a bug where if you can get a gene back in results but if you search for it by itself there are no results(PPP2R1A human)
            this.cytoscapeSearchResults.knownGeneResults = [];
            this.fireEvent('complete-search-results-ready', this.cytoscapeSearchResults);
            return;
        }

        ExtCoexpressionSearchController.doSearchQuick2Complete (
            coexpressionSearchCommand, geneIdsSubset, {
                callback: function (results) {
                    this.cytoscapeSearchResults = results;
                    this.cytoscapeResultsUpToDate = true;
                    this.fireEvent('complete-search-results-ready', results, coexpressionSearchCommand);
                }.createDelegate(this),
                errorHandler: function (result) {
                    this.fireEvent('search-error', result);
                }.createDelegate(this),
                timeout: this.coexSearchTimeout
            });
    }
});