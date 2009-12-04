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

import org.springframework.security.access.annotation.Secured;

import ubic.gemma.model.analysis.AnalysisResultSet;
import ubic.gemma.model.analysis.expression.ExpressionAnalysisResultSet;
import ubic.gemma.model.analysis.expression.ProbeAnalysisResult;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * @author kelsey
 * @version $Id$
 */
public interface DifferentialExpressionResultService {

    /**
     * Given a list of experiments and a threshold value finds all the probes that met the cut off in the given
     * experiments
     * 
     * @param experimentsAnalyzed
     * @param threshold
     * @return
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_MAP_READ" })
    public Map<ExpressionExperiment, Collection<ProbeAnalysisResult>> find(
            Collection<ExpressionExperiment> experimentsAnalyzed, double threshold, Integer limit );

    /**
     * Returns a map of a collection of {@link ProbeAnalysisResult}s keyed by {@link ExpressionExperiment}.
     * 
     * @param gene
     * @return Map<ExpressionExperiment, Collection<ProbeAnalysisResult>>
     */

    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_MAP_READ" })
    public Map<ExpressionExperiment, Collection<ProbeAnalysisResult>> find( ubic.gemma.model.genome.Gene gene );

    /**
     * Returns a map of a collection of {@link ProbeAnalysisResult}s keyed by {@link ExpressionExperiment}.
     * 
     * @param gene
     * @param experimentsAnalyzed
     * @return Map<ExpressionExperiment, Collection<ProbeAnalysisResult>>
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_MAP_READ" })
    public Map<ExpressionExperiment, Collection<ProbeAnalysisResult>> find( ubic.gemma.model.genome.Gene gene,
            Collection<ExpressionExperiment> experimentsAnalyzed );

    /**
     * Find differential expression for a gene in given data sets, exceeding a given significance level (using the
     * corrected pvalue field)
     * 
     * @param gene
     * @param experimentsAnalyzed
     * @param threshold
     * @return
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_MAP_READ" })
    public Map<ExpressionExperiment, Collection<ProbeAnalysisResult>> find( ubic.gemma.model.genome.Gene gene,
            Collection<ExpressionExperiment> experimentsAnalyzed, double threshold, Integer limit );

    /**
     * Given a list of result sets finds the diff expression results that met the given threshold
     * 
     * @param resultsAnalyzed
     * @param threshold
     * @return
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_MAP_READ" })
    public Map<ExpressionAnalysisResultSet, Collection<ProbeAnalysisResult>> findInResultSets(
            Collection<ExpressionAnalysisResultSet> resultsAnalyzed, double threshold, Integer limit );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY" })
    public Map<ProbeAnalysisResult, Collection<ExperimentalFactor>> getExperimentalFactors(
            Collection<ProbeAnalysisResult> differentialExpressionAnalysisResults );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    public Collection<ExperimentalFactor> getExperimentalFactors(
            ProbeAnalysisResult differentialExpressionAnalysisResult );

    public AnalysisResultSet loadAnalysisResult( Long analysisResultId );

    public void thaw( ProbeAnalysisResult result );

    public void thaw( Collection<ProbeAnalysisResult> results );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public void thaw( ExpressionAnalysisResultSet resultSet );

    /**
     * Does not thaw the collection of probes (just the factor information)
     * 
     * @param resultSet
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public void thawLite( ExpressionAnalysisResultSet resultSet );

    /**
     * Find differential expression for a gene, exceeding a given significance level (using the corrected pvalue field)
     * 
     * @param gene
     * @param threshold
     * @param limit
     * @return
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_MAP_READ" })
    Map<ExpressionExperiment, Collection<ProbeAnalysisResult>> find( ubic.gemma.model.genome.Gene gene,
            double threshold, Integer limit );

}
