/*
 * The Gemma project
 * 
 * Copyright (c) 2012 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.gemma.analysis.expression.diff;

import java.util.Collection;

import ubic.gemma.analysis.service.ExpressionDataMatrixService;
import ubic.gemma.datastructure.matrix.ExpressionDataDoubleMatrix;
import ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * @author paul
 * @version $Id$
 */
public interface DiffExAnalyzer {

    /**
     * Peform an analysis where the factors are determined (or guessed) automatically. If this cannot be unambiguously
     * determined, an exception will be thrown. (Default behaviour might be just to use all factors)
     * 
     * @param expressionExperiment
     * @return ExpressionAnalysis
     */
    public abstract Collection<DifferentialExpressionAnalysis> run( ExpressionExperiment expressionExperiment );

    /**
     * Perform an analysis using the specified factor(s)
     * 
     * @param expressionExperiment
     * @param factors If you care about the order the factors are included in the model, use a List
     * @return
     */
    public abstract Collection<DifferentialExpressionAnalysis> run( ExpressionExperiment expressionExperiment,
            Collection<ExperimentalFactor> factors );

    /**
     * @param expressionExperiment
     * @param config
     * @return
     */
    public abstract Collection<DifferentialExpressionAnalysis> run( ExpressionExperiment expressionExperiment,
            DifferentialExpressionAnalysisConfig config );

    /**
     * Perform an analysis using the specified factor(s), introduced in the order given.
     * 
     * @param expressionExperiment
     * @param experimentalFactors
     * @return
     */
    public abstract Collection<DifferentialExpressionAnalysis> run( ExpressionExperiment expressionExperiment,
            ExperimentalFactor... experimentalFactors );

    /***
     * Allows entry of modified data matrices into the workflow
     * 
     * @param expressionExperiment
     * @param dmatrix
     * @param config
     * @return
     */
    public abstract Collection<DifferentialExpressionAnalysis> run( ExpressionExperiment expressionExperiment,
            ExpressionDataDoubleMatrix dmatrix, DifferentialExpressionAnalysisConfig config );

    public abstract ExpressionDataDoubleMatrix regressionResiduals( ExpressionDataDoubleMatrix matrix,
            DifferentialExpressionAnalysisConfig config, boolean retainScale );

    public abstract ExperimentalFactor determineInterceptFactor( Collection<ExperimentalFactor> factors,
            QuantitationType quantitationType );

    // this is needed so we can alter this in tests
    abstract void setExpressionDataMatrixService( ExpressionDataMatrixService expressionDataMatrixService );

}