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
package ubic.gemma.model.analysis.expression.coexpression;

import java.util.Collection;

import ubic.gemma.model.analysis.AnalysisDao;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * @see ubic.gemma.model.analysis.expression.coexpression.CoexpressionAnalysis
 */
public interface CoexpressionAnalysisDao extends AnalysisDao<CoexpressionAnalysis> {

    /**
     * @param expressionExperiment
     * @return
     */
    public CoexpCorrelationDistribution getCoexpCorrelationDistribution( ExpressionExperiment expressionExperiment );

    /**
     * @param idsToFilter
     * @return
     */
    public Collection<Long> getExperimentsWithAnalysis( Collection<Long> idsToFilter );

    /**
     * @param ee
     * @return
     */
  public  Boolean hasCoexpCorrelationDistribution( ExpressionExperiment ee );

}
