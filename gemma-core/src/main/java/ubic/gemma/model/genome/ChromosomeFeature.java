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
package ubic.gemma.model.genome;

import ubic.gemma.model.common.Describable;

/**
 * Some part of a chromosome
 */
public abstract class ChromosomeFeature extends Describable {

    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long serialVersionUID = -8627296370886752496L;

    private String ncbiId;
    private String previousNcbiId;

    private CytogeneticLocation cytogeneticLocation;

    private PhysicalLocation physicalLocation;

    /**
     * No-arg constructor added to satisfy javabean contract
     *
     * @author Paul
     */
    public ChromosomeFeature() {
    }

    public CytogeneticLocation getCytogeneticLocation() {
        return this.cytogeneticLocation;
    }

    public void setCytogeneticLocation( CytogeneticLocation cytogeneticLocation ) {
        this.cytogeneticLocation = cytogeneticLocation;
    }

    /**
     * The NCBI id for this feature.
     *
     * @deprecated use NcbiGi or NcbiGeneId.
     */
    @Deprecated
    public String getNcbiId() {
        return this.ncbiId;
    }

    public void setNcbiId( String ncbiId ) {
        this.ncbiId = ncbiId;
    }

    public PhysicalLocation getPhysicalLocation() {
        return this.physicalLocation;
    }

    public void setPhysicalLocation( PhysicalLocation physicalLocation ) {
        this.physicalLocation = physicalLocation;
    }

    /**
     * The last-used NCBI id for this feature, according to the history information provided by NCBI. This may be empty.
     */
    public String getPreviousNcbiId() {
        return this.previousNcbiId;
    }

    public void setPreviousNcbiId( String previousNcbiId ) {
        this.previousNcbiId = previousNcbiId;
    }

}