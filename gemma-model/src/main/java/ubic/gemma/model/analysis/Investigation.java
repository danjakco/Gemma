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

package ubic.gemma.model.analysis;

import java.util.Collection;
import java.util.HashSet;

import ubic.gemma.model.common.Auditable;
import ubic.gemma.model.common.auditAndSecurity.Contact;
import ubic.gemma.model.common.description.BibliographicReference;
import ubic.gemma.model.common.description.Characteristic;

/**
 * An abstract concept of a scientific study
 */
public abstract class Investigation extends Auditable implements gemma.gsec.model.SecuredNotChild {

    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long serialVersionUID = -122213115714441341L;

    private Collection<Characteristic> characteristics = new HashSet<>();
    private Contact owner;

    private BibliographicReference primaryPublication;

    private Collection<BibliographicReference> otherRelevantPublications = new HashSet<>();

    private Collection<Contact> investigators = new HashSet<>();

    /**
     * No-arg constructor added to satisfy javabean contract
     * 
     * @author Paul
     */
    public Investigation() {
    }

    /**
     * <p>
     * Annotations that describe the experiment as a whole, for example "tumor" or "brain".
     * </p>
     */
    public Collection<Characteristic> getCharacteristics() {
        return this.characteristics;
    }

    /**
     * <p>
     * Other contacts who are investigators on this experiment
     * </p>
     */
    public Collection<ubic.gemma.model.common.auditAndSecurity.Contact> getInvestigators() {
        return this.investigators;
    }

    /**
     * <p>
     * A collection of other publications that are directly relevant to this investigation (e.g., use the same data but
     * are not the primary publication for the investigation).
     * </p>
     */
    public Collection<BibliographicReference> getOtherRelevantPublications() {
        return this.otherRelevantPublications;
    }

    /**
     * The contact who owns this investigation. For publicly acquired data, this is the data submitter or provider.
     */
    public Contact getOwner() {
        return this.owner;
    }

    /**
     * The primary citable publication for this investigation.
     */
    public BibliographicReference getPrimaryPublication() {
        return this.primaryPublication;
    }

    public void setCharacteristics( Collection<Characteristic> characteristics ) {
        this.characteristics = characteristics;
    }

    public void setInvestigators( Collection<Contact> investigators ) {
        this.investigators = investigators;
    }

    public void setOtherRelevantPublications( Collection<BibliographicReference> otherRelevantPublications ) {
        this.otherRelevantPublications = otherRelevantPublications;
    }

    public void setOwner( Contact owner ) {
        this.owner = owner;
    }

    public void setPrimaryPublication( BibliographicReference primaryPublication ) {
        this.primaryPublication = primaryPublication;
    }

}