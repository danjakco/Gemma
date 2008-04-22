/*
 * The Gemma project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.gemma.analysis.service;

import java.util.Collection;
import java.util.Map;

import ubic.gemma.analysis.preprocess.ExpressionDataMatrixBuilder;
import ubic.gemma.analysis.preprocess.filter.ExpressionExperimentFilter;
import ubic.gemma.analysis.preprocess.filter.FilterConfig;
import ubic.gemma.datastructure.matrix.ExpressionDataDoubleMatrix;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.arrayDesign.TechnologyType;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorService;
import ubic.gemma.model.expression.bioAssayData.DoubleVectorValueObject;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.model.genome.Gene;

/**
 * Tools for easily getting data matrices for analysis in a consistent way.
 * 
 * @spring.bean id="analysisHelperService"
 * @spring.property name="expressionExperimentService" ref="expressionExperimentService"
 * @spring.property name="vectorService" ref="designElementDataVectorService"
 * @author keshav
 * @version $Id$
 */
public class AnalysisHelperService {

    ExpressionExperimentService expressionExperimentService;

    DesignElementDataVectorService vectorService;

    /**
     * Provide a filtered expression data matrix.
     * 
     * @param ee
     * @param filterConfig
     * @param dataVectors
     * @return
     */
    @SuppressWarnings("unchecked")
    public ExpressionDataDoubleMatrix getFilteredMatrix( ExpressionExperiment ee, FilterConfig filterConfig,
            Collection<DesignElementDataVector> dataVectors ) {
        Collection<ArrayDesign> arrayDesignsUsed = expressionExperimentService.getArrayDesignsUsed( ee );
        ExpressionExperimentFilter filter = new ExpressionExperimentFilter( ee, arrayDesignsUsed, filterConfig );
        ExpressionDataDoubleMatrix eeDoubleMatrix = filter.getFilteredMatrix( dataVectors );
        return eeDoubleMatrix;
    }

    /**
     * Provide a filtered expression data matrix.
     * 
     * @param ee
     * @param filterConfig
     * @return
     */
    @SuppressWarnings("unchecked")
    public ExpressionDataDoubleMatrix getFilteredMatrix( ExpressionExperiment ee, FilterConfig filterConfig ) {
        Collection<DesignElementDataVector> dataVectors;
        if ( isTwoColor( ee ) ) {
            dataVectors = this.getPreferredAndMissingValueVectors( ee );
        } else {
            dataVectors = this.getPreferredVectors( ee );
        }
        return this.getFilteredMatrix( ee, filterConfig, dataVectors );
    }

    /**
     * Determine if the expression experiment uses two-color arrays. TODO: this duplicates code found elsewhere.
     * 
     * @param ee
     * @return
     * @throws UnsupportedOperationException if the ee uses both two color and one-color technologies.
     */
    @SuppressWarnings("unchecked")
    private boolean isTwoColor( ExpressionExperiment ee ) {
        Boolean answer = null;
        Collection<ArrayDesign> arrayDesignsUsed = expressionExperimentService.getArrayDesignsUsed( ee );
        for ( ArrayDesign arrayDesign : arrayDesignsUsed ) {
            TechnologyType techType = arrayDesign.getTechnologyType();
            boolean isTwoC = techType.equals( TechnologyType.TWOCOLOR ) || techType.equals( TechnologyType.DUALMODE );
            if ( answer != null && !answer.equals( isTwoC ) ) {
                throw new UnsupportedOperationException(
                        "Gemma cannot handle experiments that mix one- and two-color arrays" );
            }
            answer = isTwoC;
        }
        return answer;
    }

    /**
     * @param ee
     * @return matrix of preferred data, with all missing values masked.
     */
    public ExpressionDataDoubleMatrix getMaskedPreferredDataMatrix( ExpressionExperiment ee ) {
        Collection<DesignElementDataVector> dataVectors = getPreferredAndMissingValueVectors( ee );
        ExpressionDataMatrixBuilder builder = new ExpressionDataMatrixBuilder( dataVectors );
        return builder.getMaskedPreferredData();
    }

    /**
     * @param ee
     * @param genes
     * @return map of vectors to genes.
     */
    @SuppressWarnings("unchecked")
    public Map<DoubleVectorValueObject, Collection<Gene>> getMaskedPreferredDataVectors(
            Collection<ExpressionExperiment> ees, Collection<Gene> genes ) {
        return this.vectorService.getMaskedPreferredDataArrays( ees, genes );
    }

    /**
     * NOTE that we don't normally use the 'absent/present' (missing value) data for one-color arrays (e.g. Affymetrix).
     * Thus this method should typically be called only for two-channel (ratiometric) arrays.
     * 
     * @param ee
     * @return all data vectors that are "preferred" or "absent/present" types for the given ee.
     */
    @SuppressWarnings("unchecked")
    public Collection<DesignElementDataVector> getPreferredAndMissingValueVectors( ExpressionExperiment ee ) {
        Collection<QuantitationType> qts = ExpressionDataMatrixBuilder.getPreferredQuantitationTypes( ee );
        qts.addAll( ExpressionDataMatrixBuilder.getMissingValueQuantitationTypes( ee ) );
        Collection<DesignElementDataVector> dataVectors = expressionExperimentService.getDesignElementDataVectors( qts );
        vectorService.thaw( dataVectors );
        return dataVectors;
    }

    /**
     * @param ee
     * @return all data vectors that are "preferred" type for the given ee.
     */
    @SuppressWarnings("unchecked")
    public Collection<DesignElementDataVector> getPreferredVectors( ExpressionExperiment ee ) {
        Collection<QuantitationType> qts = ExpressionDataMatrixBuilder.getPreferredQuantitationTypes( ee );
        Collection<DesignElementDataVector> dataVectors = expressionExperimentService.getDesignElementDataVectors( qts );
        vectorService.thaw( dataVectors );
        return dataVectors;
    }

    /**
     * If you are preprocessing a data set, you might want to use this (e.g., computing missing values). If you are
     * analyzing a data set (links, etc), you should use getPreferredAndMissingValueVectors.
     * 
     * @param ee
     * @return vectors for the experiment that are needed for analysis, in general. This includes background and raw
     *         channel data needed to compute missing values.
     * @see getPreferredAndMissingValueVectors
     */
    @SuppressWarnings("unchecked")
    public Collection<DesignElementDataVector> getUsefulVectors( ExpressionExperiment ee ) {
        Collection<QuantitationType> qts = ExpressionDataMatrixBuilder.getUsefulQuantitationTypes( ee );
        if ( qts.size() == 0 ) throw new IllegalArgumentException( "No usable quantitation type in " + ee );
        Collection<DesignElementDataVector> dataVectors = expressionExperimentService.getDesignElementDataVectors( qts );
        vectorService.thaw( dataVectors );
        return dataVectors;
    }

    public void setExpressionExperimentService( ExpressionExperimentService expressionExperimentService ) {
        this.expressionExperimentService = expressionExperimentService;
    }

    public void setVectorService( DesignElementDataVectorService vectorService ) {
        this.vectorService = vectorService;
    }

}
