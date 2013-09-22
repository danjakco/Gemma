/*
 * The Gemma project.
 * 
 * Copyright (c) 2006-2012 University of British Columbia
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

import ubic.gemma.model.analysis.SingleExperimentAnalysisImpl;

/**
 * A coexpression analysis at the level of probes
 */
public abstract class ProbeCoexpressionAnalysis extends SingleExperimentAnalysisImpl {

    /**
     * Constructs new instances of {@link ProbeCoexpressionAnalysis}.
     */
    public static final class Factory {
        /**
         * Constructs a new instance of {@link ProbeCoexpressionAnalysis}.
         */
        public static ProbeCoexpressionAnalysis newInstance() {
            return new ProbeCoexpressionAnalysisImpl();
        }

    }

    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long serialVersionUID = 1210363957474375117L;
    private Integer numberOfLinks;

    private Collection<CoexpressionProbe> probesUsed = new java.util.HashSet<>();

    /**
     * No-arg constructor added to satisfy javabean contract
     * 
     * @author Paul
     */
    public ProbeCoexpressionAnalysis() {
    }

    /**
     * The number of links which were stored for this analysis.
     */
    public Integer getNumberOfLinks() {
        return this.numberOfLinks;
    }

    /**
     * Probes that were not filtered based on initial filtering criteria. Not all probes used have links retained.
     */
    public Collection<CoexpressionProbe> getProbesUsed() {
        return this.probesUsed;
    }

    public void setNumberOfLinks( Integer numberOfLinks ) {
        this.numberOfLinks = numberOfLinks;
    }

    public void setProbesUsed( Collection<CoexpressionProbe> probesUsed ) {
        this.probesUsed = probesUsed;
    }

}