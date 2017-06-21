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
package ubic.gemma.model.common.auditAndSecurity.eventType;

/**
 * <p>
 * Event used when a sample is removed from an experiment (typically due to QC concerns)
 * </p>
 */
public class SampleRemovalEvent extends
        ubic.gemma.model.common.auditAndSecurity.eventType.ExpressionExperimentAnalysisEvent {

    /**
     * Constructs new instances of {@link ubic.gemma.model.common.auditAndSecurity.eventType.SampleRemovalEvent}.
     */
    public static final class Factory {
        /**
         * Constructs a new instance of {@link ubic.gemma.model.common.auditAndSecurity.eventType.SampleRemovalEvent}.
         */
        public static ubic.gemma.model.common.auditAndSecurity.eventType.SampleRemovalEvent newInstance() {
            return new ubic.gemma.model.common.auditAndSecurity.eventType.SampleRemovalEvent();
        }

    }

    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long serialVersionUID = 6508357999103110838L;

    /**
     * No-arg constructor added to satisfy javabean contract
     * 
     * @author Paul
     */
    public SampleRemovalEvent() {
    }

}