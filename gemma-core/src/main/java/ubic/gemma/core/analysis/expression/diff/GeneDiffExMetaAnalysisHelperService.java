/*
 * The Gemma project
 * 
 * Copyright (c) 2012 University of British Columbia
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

package ubic.gemma.core.analysis.expression.diff;

import java.util.Collection;

import ubic.gemma.model.analysis.expression.diff.GeneDifferentialExpressionMetaAnalysis;
import ubic.gemma.model.analysis.expression.diff.GeneDifferentialExpressionMetaAnalysisDetailValueObject;
import ubic.gemma.model.analysis.expression.diff.GeneDifferentialExpressionMetaAnalysisSummaryValueObject;

/**
 * TODO Document Me
 * 
 * @author frances
 *
 */
public interface GeneDiffExMetaAnalysisHelperService {
    /**
     * Convert the given meta-analysis into detail value object.
     * 
     * @param metaAnalysis
     * @return the converted detail value object
     */
    public GeneDifferentialExpressionMetaAnalysisDetailValueObject convertToValueObject(
            GeneDifferentialExpressionMetaAnalysis metaAnalysis );

    /**
     * Find meta-analysis by the given id.
     * 
     * @param metaAnalysisId
     * @return detail meta-analysis value object
     */
    public GeneDifferentialExpressionMetaAnalysisDetailValueObject findDetailMetaAnalysisById( long metaAnalysisId );

    /**
     * Load all meta-analyses.
     * 
     * @return a collection of summary value objects
     */
    public Collection<GeneDifferentialExpressionMetaAnalysisSummaryValueObject> loadAllMetaAnalyses();
}
