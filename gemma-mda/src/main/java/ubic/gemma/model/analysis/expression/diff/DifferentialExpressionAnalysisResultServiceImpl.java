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

import ubic.gemma.model.analysis.expression.ExpressionAnalysisResultSet;
import ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisResult;

/**
 * @author keshav
 * @version $Id$
 * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisResultService
 */
public class DifferentialExpressionAnalysisResultServiceImpl extends
        ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisResultServiceBase {

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisResultServiceBase#handleGetExperimentalFactors(ubic.gemma.model.expression.analysis.expression.diff.DifferentialExpressionAnalysisResult)
     */
    @Override
    protected Collection handleGetExperimentalFactors(
            DifferentialExpressionAnalysisResult differentialExpressionAnalysisResult ) throws Exception {
        return this.getDifferentialExpressionAnalysisResultDao().getExperimentalFactors(
                differentialExpressionAnalysisResult );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisResultServiceBase#handleGetExperimentalFactors(java.util.Collection)
     */
    @Override
    protected Map handleGetExperimentalFactors( Collection differentialExpressionAnalysisResults ) throws Exception {
        return this.getDifferentialExpressionAnalysisResultDao().getExperimentalFactors(
                differentialExpressionAnalysisResults );
    }

    @Override
    protected void handleThaw( ExpressionAnalysisResultSet resultSet ) throws Exception {
        this.getExpressionAnalysisResultSetDao().thaw( resultSet );
    }
}