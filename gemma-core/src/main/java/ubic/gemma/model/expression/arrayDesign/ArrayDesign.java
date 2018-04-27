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

package ubic.gemma.model.expression.arrayDesign;

import ubic.gemma.model.common.AbstractAuditable;
import ubic.gemma.model.common.auditAndSecurity.curation.Curatable;
import ubic.gemma.model.common.auditAndSecurity.curation.CurationDetails;

import java.util.Collection;

/**
 * Represents an assembly of design elements that are assayed all at once.
 *
 * @author Paul
 */
public class ArrayDesign extends AbstractAuditable implements gemma.gsec.model.SecuredNotChild, Curatable {

    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long serialVersionUID = -7566439134502613470L;
    private Integer advertisedNumberOfDesignElements;
    private String shortName;
    private ubic.gemma.model.expression.arrayDesign.TechnologyType technologyType;
    private ubic.gemma.model.genome.Taxon primaryTaxon;
    private Collection<ubic.gemma.model.common.description.DatabaseEntry> externalReferences = new java.util.HashSet<>();
    private Collection<ubic.gemma.model.expression.designElement.CompositeSequence> compositeSequences = new java.util.HashSet<>();
    private ubic.gemma.model.expression.arrayDesign.ArrayDesign mergedInto;
    private ubic.gemma.model.expression.arrayDesign.ArrayDesign subsumingArrayDesign;
    private Collection<ubic.gemma.model.expression.arrayDesign.ArrayDesign> subsumedArrayDesigns = new java.util.HashSet<>();
    private Collection<ubic.gemma.model.expression.arrayDesign.ArrayDesign> mergees = new java.util.HashSet<>();
    private ubic.gemma.model.common.auditAndSecurity.Contact designProvider;
    private Collection<ubic.gemma.model.expression.arrayDesign.AlternateName> alternateNames = new java.util.HashSet<>();
    private CurationDetails curationDetails;

    /**
     * No-arg constructor added to satisfy javabean contract
     */
    public ArrayDesign() {
    }

    /**
     * @return The number of design elements, according to the manufactuerer or determined at the time the array design
     *         was
     *         entered into the system. The actual number of design elements can only be determined by looking at the
     *         associated
     *         Collection of DesignElements.
     */
    public Integer getAdvertisedNumberOfDesignElements() {
        return this.advertisedNumberOfDesignElements;
    }

    public void setAdvertisedNumberOfDesignElements( Integer advertisedNumberOfDesignElements ) {
        this.advertisedNumberOfDesignElements = advertisedNumberOfDesignElements;
    }

    public Collection<ubic.gemma.model.expression.arrayDesign.AlternateName> getAlternateNames() {
        return this.alternateNames;
    }

    public void setAlternateNames( Collection<ubic.gemma.model.expression.arrayDesign.AlternateName> alternateNames ) {
        this.alternateNames = alternateNames;
    }

    public Collection<ubic.gemma.model.expression.designElement.CompositeSequence> getCompositeSequences() {
        return this.compositeSequences;
    }

    public void setCompositeSequences(
            Collection<ubic.gemma.model.expression.designElement.CompositeSequence> compositeSequences ) {
        this.compositeSequences = compositeSequences;
    }

    public ubic.gemma.model.common.auditAndSecurity.Contact getDesignProvider() {
        return this.designProvider;
    }

    public void setDesignProvider( ubic.gemma.model.common.auditAndSecurity.Contact designProvider ) {
        this.designProvider = designProvider;
    }

    /**
     * @return Accessions for this array design in other databases, e.g., GEO, ArrayExpression.
     */
    public Collection<ubic.gemma.model.common.description.DatabaseEntry> getExternalReferences() {
        return this.externalReferences;
    }

    public void setExternalReferences(
            Collection<ubic.gemma.model.common.description.DatabaseEntry> externalReferences ) {
        this.externalReferences = externalReferences;
    }

    public ubic.gemma.model.expression.arrayDesign.ArrayDesign getMergedInto() {
        return this.mergedInto;
    }

    public void setMergedInto( ubic.gemma.model.expression.arrayDesign.ArrayDesign mergedInto ) {
        this.mergedInto = mergedInto;
    }

    public Collection<ubic.gemma.model.expression.arrayDesign.ArrayDesign> getMergees() {
        return this.mergees;
    }

    public void setMergees( Collection<ubic.gemma.model.expression.arrayDesign.ArrayDesign> mergees ) {
        this.mergees = mergees;
    }

    /**
     * @return The taxon the array design is for. This could be a non-specific taxon ("salmonid"). It may not match the
     *         sequences on the array exactly. For example, a mouse array might have some non-mouse sequences as
     *         controls, but
     *         the primary taxon is still mouse.
     */
    public ubic.gemma.model.genome.Taxon getPrimaryTaxon() {
        return this.primaryTaxon;
    }

    public void setPrimaryTaxon( ubic.gemma.model.genome.Taxon primaryTaxon ) {
        this.primaryTaxon = primaryTaxon;
    }

    /**
     * @return A brief unique (but optional) human-readable name for the expression experiment. For example in the past
     *         we often
     *         used names like "HG-U95A".
     */
    public String getShortName() {
        return this.shortName;
    }

    public void setShortName( String shortName ) {
        this.shortName = shortName;
    }

    /**
     * @return Array designs that this array design "covers". For example, the HG-U133_Plus_2 array includes all the
     *         elements
     *         that are on the HG-U133A and HG-U133B, so they are subsumed by the HG-U133_Plus_2.
     */
    public Collection<ubic.gemma.model.expression.arrayDesign.ArrayDesign> getSubsumedArrayDesigns() {
        return this.subsumedArrayDesigns;
    }

    public void setSubsumedArrayDesigns(
            Collection<ubic.gemma.model.expression.arrayDesign.ArrayDesign> subsumedArrayDesigns ) {
        this.subsumedArrayDesigns = subsumedArrayDesigns;
    }

    /**
     * @return An array design that subsumes this one (contains DesignElements that are equivalent to the ones on this
     *         arraydesign).
     */
    public ubic.gemma.model.expression.arrayDesign.ArrayDesign getSubsumingArrayDesign() {
        return this.subsumingArrayDesign;
    }

    public void setSubsumingArrayDesign( ubic.gemma.model.expression.arrayDesign.ArrayDesign subsumingArrayDesign ) {
        this.subsumingArrayDesign = subsumingArrayDesign;
    }

    public ubic.gemma.model.expression.arrayDesign.TechnologyType getTechnologyType() {
        return this.technologyType;
    }

    public void setTechnologyType( ubic.gemma.model.expression.arrayDesign.TechnologyType technologyType ) {
        this.technologyType = technologyType;
    }

    @Override
    public boolean equals( Object object ) {
        if ( !( object instanceof ArrayDesign ) )
            return false;
        ArrayDesign that = ( ArrayDesign ) object;
        if ( this.getId() != null && that.getId() != null )
            return this.getId().equals( that.getId() );

        return this.getName() != null && that.getName() != null && this.getName().equals( that.getName() );

    }

    @Override
    public int hashCode() {
        if ( this.getId() != null )
            return 29 * getId().hashCode();
        if ( this.getName() != null )
            return 29 * getName().hashCode();
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + ( this.getShortName() == null ? "" : " (" + this.getShortName() + ")" );
    }

    @Override
    public CurationDetails getCurationDetails() {
        return this.curationDetails;
    }

    @Override
    public void setCurationDetails( CurationDetails curationDetails ) {
        this.curationDetails = curationDetails;
    }

    public static final class Factory {

        public static ubic.gemma.model.expression.arrayDesign.ArrayDesign newInstance() {
            return new ubic.gemma.model.expression.arrayDesign.ArrayDesign();
        }

    }

}