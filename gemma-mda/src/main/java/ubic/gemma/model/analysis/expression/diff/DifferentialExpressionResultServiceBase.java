/*
 * The Gemma project.
 * 
 * Copyright (c) 2006-2007 University of British Columbia
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
package ubic.gemma.model.analysis.expression.diff;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import ubic.gemma.model.expression.experiment.ExperimentalFactor;

/**
 * Spring Service base class for
 * <code>ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultService</code>, provides access to all
 * services and entities referenced by this service.
 * 
 * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultService
 * @version $Id$
 */
public abstract class DifferentialExpressionResultServiceBase implements DifferentialExpressionResultService {

    @Autowired
    private DifferentialExpressionResultDao differentialExpressionAnalysisResultDao;

    @Autowired
    private ExpressionAnalysisResultSetDao expressionAnalysisResultSetDao;

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultService#getExperimentalFactors(java.util.Collection)
     */
    @Override
    public Map<DifferentialExpressionAnalysisResult, Collection<ExperimentalFactor>> getExperimentalFactors(
            final Collection<DifferentialExpressionAnalysisResult> differentialExpressionAnalysisResults ) {
        return this.handleGetExperimentalFactors( differentialExpressionAnalysisResults );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultService#getExperimentalFactors(ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisResult)
     */
    @Override
    public Collection<ExperimentalFactor> getExperimentalFactors(
            final DifferentialExpressionAnalysisResult differentialExpressionAnalysisResult ) {
        return this.handleGetExperimentalFactors( differentialExpressionAnalysisResult );
    }

    /**
     * Sets the reference to <code>differentialExpressionAnalysisResult</code>'s DAO.
     */
    public void setDifferentialExpressionResultDao(
            ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultDao differentialExpressionAnalysisResultDao ) {
        this.differentialExpressionAnalysisResultDao = differentialExpressionAnalysisResultDao;
    }

    /**
     * Sets the reference to <code>expressionAnalysisResultSet</code>'s DAO.
     */
    public void setExpressionAnalysisResultSetDao(
            ubic.gemma.model.analysis.expression.diff.ExpressionAnalysisResultSetDao expressionAnalysisResultSetDao ) {
        this.expressionAnalysisResultSetDao = expressionAnalysisResultSetDao;
    }

    /**
     * @see diff.DifferentialExpressionResultService#thaw(ExpressionAnalysisResultSet)
     */
    @Override
    public void thaw( final ExpressionAnalysisResultSet resultSet ) {
        this.handleThaw( resultSet );

    }

    /**
     * Gets the reference to <code>differentialExpressionAnalysisResult</code>'s DAO.
     */
    protected DifferentialExpressionResultDao getDifferentialExpressionResultDao() {
        return this.differentialExpressionAnalysisResultDao;
    }

    /**
     * Gets the reference to <code>expressionAnalysisResultSet</code>'s DAO.
     */
    protected ExpressionAnalysisResultSetDao getExpressionAnalysisResultSetDao() {
        return this.expressionAnalysisResultSetDao;
    }

    /**
     * Performs the core logic for {@link #getExperimentalFactors(java.util.Collection)}
     */
    protected abstract Map<DifferentialExpressionAnalysisResult, Collection<ExperimentalFactor>> handleGetExperimentalFactors(
            Collection<DifferentialExpressionAnalysisResult> differentialExpressionAnalysisResults );

    /**
     * Performs the core logic for {@link #getExperimentalFactors(diff.DifferentialExpressionAnalysisResult)}
     */
    protected abstract java.util.Collection<ExperimentalFactor> handleGetExperimentalFactors(
            DifferentialExpressionAnalysisResult differentialExpressionAnalysisResult );

    /**
     * Performs the core logic for {@link #thaw(ExpressionAnalysisResultSet)}
     */
    protected abstract void handleThaw( ExpressionAnalysisResultSet resultSet );

}