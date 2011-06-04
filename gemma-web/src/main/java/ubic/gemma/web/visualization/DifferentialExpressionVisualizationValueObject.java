package ubic.gemma.web.visualization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DifferentialExpressionVisualizationValueObject {
    public class GeneScore {
        double score;
        int index;            
        public GeneScore( double score, int index ) {
            super();
            this.score = score;
            this.index = index;
        }
        public double getScore() {
            return score;
        }
        public void setScore( double score ) {
            this.score = score;
        }
        public int getIndex() {
            return index;
        }
        public void setIndex( int index ) {
            this.index = index;
        }            
    }

    public DifferentialExpressionVisualizationValueObject (int numberOfDatasetGroups, int[] geneGroupSizes) {
        int numberOfGeneGroups = geneGroupSizes.length;
        
        this.geneScores = new ArrayList<List<List<GeneScore>>>( numberOfDatasetGroups );                
        for (int i = 0; i < numberOfDatasetGroups; i++) {
            List<List<GeneScore>> subGeneScores = new ArrayList<List<GeneScore>>( numberOfGeneGroups );
            for (int j = 0; j < numberOfGeneGroups; j++) {                
                List<GeneScore> tempGeneScore = new ArrayList<GeneScore>( geneGroupSizes[j] );
                for (int geneIndex = 0; geneIndex < geneGroupSizes[j]; geneIndex ++) {
                    tempGeneScore.add( new GeneScore (0.0, geneIndex) );                    
                }                
                subGeneScores.add( tempGeneScore );
            }            
            this.geneScores.add( subGeneScores );
        }        
        
        this.resultSetValueObjects = new ArrayList<List<DifferentialExpressionAnalysisResultSetVisualizationValueObject>>( numberOfDatasetGroups );        
    }    
    
    //[datasetGroup][columnIndex]
    private List<List<DifferentialExpressionAnalysisResultSetVisualizationValueObject>> resultSetValueObjects;
    private List<List<String>> analysisLabels;        
    // geneGroup / geneIndex
    private List<List<String>> geneNames;
    private List<List<String>> geneFullNames;
    private List<List<Long>> geneIds;

    private List<String> geneGroupNames;
    private List<String> datasetGroupNames;
    
    //dsGroup geneGroup geneIndex // FOR NOW JUST ONE METRIC
    private List<List<List<GeneScore>>> geneScores;

    
    public void addToGeneScore (int datasetGroupIndex, int geneGroupIndex, int geneIndex, double score) {
       double currentScore = geneScores.get( datasetGroupIndex ).get(  geneGroupIndex ).get( geneIndex ).getScore();
       currentScore += score;
       geneScores.get( datasetGroupIndex ).get(  geneGroupIndex ).get( geneIndex ).setScore( currentScore );
    }
    
    public List<List<List<GeneScore>>> getGeneScores() {
        return geneScores;
    }

    public void setGeneScores( List<List<List<GeneScore>>> geneScores ) {
        this.geneScores = geneScores;
    }

    public List<List<Long>> getGeneIds() {
        return geneIds;
    }

    public void setGeneIds( List<List<Long>> geneIds ) {
        this.geneIds = geneIds;
    }

    public List<List<String>> getAnalysisLabels() {
        return analysisLabels;
    }

    public void setAnalysisLabels( List<List<String>> analysisLabels ) {
        this.analysisLabels = analysisLabels;
    }

    public List<List<String>> getGeneNames() {
        return geneNames;
    }

    public void setGeneNames( List<List<String>> geneNames ) {
        this.geneNames = geneNames;
    }

    public List<List<String>> getGeneFullNames() {
        return geneFullNames;
    }

    public void setGeneFullNames( List<List<String>> geneFullNames ) {
        this.geneFullNames = geneFullNames;
    }

    public List<String> getGeneGroupNames() {
        return geneGroupNames;
    }

    public void setGeneGroupNames( List<String> geneGroupNames ) {
        this.geneGroupNames = geneGroupNames;
    }

    public List<String> getDatasetGroupNames() {
        return datasetGroupNames;
    }

    public void setDatasetGroupNames( List<String> datasetGroupNames ) {
        this.datasetGroupNames = datasetGroupNames;
    }

    public List<List<DifferentialExpressionAnalysisResultSetVisualizationValueObject>> getResultSetValueObjects() {
        return resultSetValueObjects;
    }

    public void addDatasetGroup ( List<DifferentialExpressionAnalysisResultSetVisualizationValueObject> group ) {
        this.resultSetValueObjects.add( group );
        
    }
    
}
