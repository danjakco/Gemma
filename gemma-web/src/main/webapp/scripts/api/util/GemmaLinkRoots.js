Ext.namespace( 'Gemma.LinkRoots' );

Gemma.LinkRoots = {
   expressionExperimentPage : "/Gemma/expressionExperiment/showExpressionExperiment.html?id=",
   expressionExperimentSetPage : "/Gemma/expressionExperimentSet/showExpressionExperimentSet.html?id=",
   geneSetPage : "/Gemma/geneSet/showGeneSet.html?id=",
   genePage : "/Gemma/gene/showGene.html?id=",
   genePageNCBI : "/Gemma/gene/showGene.html?ncbiid=",
   phenotypePage : "/Gemma/phenotypes.html?phenotypeUrlId="
};

(function() {
   Gemma.arrayDesignLink = function( ad ) {
      return "<a ext:qtip='" + ad.name + "' href=\"/Gemma/arrays/showArrayDesign.html?id=" + ad.id + "\">"
         + ad.shortName + "</a>";
   };
})();