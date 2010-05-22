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
package ubic.gemma.model.expression.arrayDesign;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.access.annotation.Secured;

import ubic.gemma.model.common.auditAndSecurity.AuditEvent;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.genome.Taxon;

/**
 * @version $Id$
 */
public interface ArrayDesignService {

    /**
     * @return all compositeSequences for the given arrayDesign that do not have any bioSequence associations.
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public java.util.Collection<CompositeSequence> compositeSequenceWithoutBioSequences( ArrayDesign arrayDesign );

    /**
     * @return all compositeSequences for the given arrayDesign that do not have BLAT result associations.
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public java.util.Collection<CompositeSequence> compositeSequenceWithoutBlatResults( ArrayDesign arrayDesign );

    /**
     * @return all compositeSequences for the given arrayDesign that do not have gene associations.
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public java.util.Collection<CompositeSequence> compositeSequenceWithoutGenes( ArrayDesign arrayDesign );

    /**
     * @return global count of compositeSequences in the system.
     */
    public Integer countAll();

    /**
     * 
     */
    @Secured( { "GROUP_USER" })
    public ArrayDesign create( ArrayDesign arrayDesign );

    /**
     * delete sequence alignment results associated with the bioSequences for this array design.
     */
    @Secured( { "GROUP_USER", "ACL_SECURABLE_EDIT" })
    public void deleteAlignmentData( ArrayDesign arrayDesign );

    /**
     * deletes the gene product associations on the specified array design F
     */
    @Secured( { "GROUP_USER", "ACL_SECURABLE_EDIT" })
    public void deleteGeneProductAssociations( ArrayDesign arrayDesign );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_READ" })
    public ArrayDesign find( ArrayDesign arrayDesign );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    public java.util.Collection<ArrayDesign> findByAlternateName( String queryString );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_READ" })
    public ArrayDesign findByName( String name );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_READ" })
    public ArrayDesign findByShortName( String shortName );

    /**
     * Find by the primary taxon.
     * 
     * @param taxon
     * @return
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    public Collection<ArrayDesign> findByTaxon( Taxon taxon );

    /**
     * 
     */
    @Secured( { "GROUP_USER", "AFTER_ACL_READ" })
    public ArrayDesign findOrCreate( ArrayDesign arrayDesign );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    public java.util.Collection<BioAssay> getAllAssociatedBioAssays( Long id );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public Integer getCompositeSequenceCount( ArrayDesign arrayDesign );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    public java.util.Collection<ExpressionExperiment> getExpressionExperiments( ArrayDesign arrayDesign );

    /**
     * <p>
     * Gets the AuditEvents of the latest annotation file event for the specified array design ids. This returns a map
     * of id -> AuditEvent. If the events do not exist, the map entry will point to null.
     * </p>
     */
    public java.util.Map<Long, AuditEvent> getLastAnnotationFile( java.util.Collection<Long> ids );

    /**
     * <p>
     * Gets the AuditEvents of the latest gene mapping for the specified array design ids. This returns a map of id ->
     * AuditEvent. If the events do not exist, the map entry will point to null.
     * </p>
     */
    public java.util.Map<Long, AuditEvent> getLastGeneMapping( java.util.Collection<Long> ids );

    /**
     * 
     */
    public java.util.Map<Long, AuditEvent> getLastRepeatAnalysis( java.util.Collection<Long> ids );

    /**
     * <p>
     * Gets the AuditEvents of the latest sequence analyses for the specified array design ids. This returns a map of id
     * -> AuditEvent. If the events do not exist, the map entry will point to null.
     * </p>
     */
    public java.util.Map<Long, AuditEvent> getLastSequenceAnalysis( java.util.Collection<Long> ids );

    /**
     * <p>
     * Gets the AuditEvents of the latest sequence update for the specified array design ids. This returns a map of id
     * -> AuditEvent. If the events do not exist, the map entry will point to null.
     * </p>
     */
    public java.util.Map<Long, AuditEvent> getLastSequenceUpdate( java.util.Collection<Long> ids );

    /**
     * 
     */
    public java.util.Map<Long, AuditEvent> getLastTroubleEvent( java.util.Collection<Long> ids );

    /**
     * 
     */
    public java.util.Map<Long, AuditEvent> getLastValidationEvent( java.util.Collection<Long> ids );

    /**
     * @return a map of taxon -> count of how many array designs there are for that taxon. Taxa with no arrays are
     *         excluded.
     */
    public Map<Taxon, Integer> getPerTaxonCount();

    /**
     * 
     */
    public Integer getReporterCount( ArrayDesign arrayDesign );

    /**
     * Return the taxa for the array design. This can be multiple, or zero if the array is not processed.
     * 
     * @param id The id of the array design
     * @return The Set of Taxons for array design.
     */
    public java.util.Collection<Taxon> getTaxa( Long id );

    /**
     * Return the taxon for the array design. This can be misleading if the array uses multiple taxa: this method will
     * return the first one found.
     * 
     * @param id The id of the array design
     * @return The taxon
     * @deprecated Use arrayDesign.getPrimaryTaxon if you want a single representative taxon for the array. Otherwise
     *             use getTaxa to get the comprehensive list.
     */
    @Deprecated
    public Taxon getTaxon( Long id );

    /**
     * 
     */
    public java.util.Map<Long, Boolean> isMerged( java.util.Collection<Long> ids );

    /**
     * 
     */
    public java.util.Map<Long, Boolean> isMergee( java.util.Collection<Long> ids );

    /**
     * 
     */
    public java.util.Map<Long, Boolean> isSubsumed( java.util.Collection<Long> ids );

    /**
     * 
     */
    public java.util.Map<Long, Boolean> isSubsumer( java.util.Collection<Long> ids );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_READ" })
    public ArrayDesign load( long id );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    public java.util.Collection<ArrayDesign> loadAll();

    /**
     * <p>
     * loads all Array designs as value objects.
     * </p>
     */
    public java.util.Collection<ArrayDesignValueObject> loadAllValueObjects();

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    public java.util.Collection<CompositeSequence> loadCompositeSequences( ArrayDesign arrayDesign );

    /**
     * Does a 'thaw' of an arrayDesign given an id. Returns the thawed arrayDesign.
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_READ" })
    public ArrayDesign loadFully( Long id );

    /**
     * <p>
     * Given a collection of ID (longs) will return a collection of ArrayDesigns
     * </p>
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    public java.util.Collection<ArrayDesign> loadMultiple( java.util.Collection<Long> ids );

    /**
     * <p>
     * loads the Value Objects for the Array Designs specified by the input ids.
     * </p>
     */
    public java.util.Collection<ArrayDesignValueObject> loadValueObjects( java.util.Collection<Long> ids );

    /**
     * <p>
     * Function to return a count of all compositeSequences with bioSequence associations
     * </p>
     */
    public long numAllCompositeSequenceWithBioSequences();

    /**
     * <p>
     * Function to return the count of all composite sequences with biosequences, given a list of array design Ids
     * </p>
     */
    public long numAllCompositeSequenceWithBioSequences( java.util.Collection<Long> ids );

    /**
     * <p>
     * Function to return all composite sequences with blat results
     * </p>
     */
    public long numAllCompositeSequenceWithBlatResults();

    /**
     * <p>
     * Function to return the count of all composite sequences with blat results, given a list of array design Ids
     * </p>
     */
    public long numAllCompositeSequenceWithBlatResults( java.util.Collection<Long> ids );

    /**
     * <p>
     * Function to return a count of all composite sequences with associated genes.
     * </p>
     */
    public long numAllCompositeSequenceWithGenes();

    /**
     * <p>
     * Function to return the count of all composite sequences with genes, given a list of array design Ids
     * </p>
     */
    public long numAllCompositeSequenceWithGenes( java.util.Collection<Long> ids );

    /**
     * <p>
     * Returns a count of the number of genes associated with all arrayDesigns
     * </p>
     */
    public long numAllGenes();

    /**
     * <p>
     * Returns the number of unique Genes associated with the collection of ArrayDesign ids.
     * </p>
     */
    public long numAllGenes( java.util.Collection<Long> ids );

    /**
     * <p>
     * returns the number of bioSequences associated with this ArrayDesign id
     * </p>
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public long numBioSequences( ArrayDesign arrayDesign );

    /**
     * <p>
     * returns the number of BlatResults (BioSequence2GeneProduct) entries associated with this ArrayDesign id.
     * </p>
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public long numBlatResults( ArrayDesign arrayDesign );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public long numCompositeSequenceWithBioSequences( ArrayDesign arrayDesign );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public long numCompositeSequenceWithBlatResults( ArrayDesign arrayDesign );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public long numCompositeSequenceWithGenes( ArrayDesign arrayDesign );

    /**
     * <p>
     * function to get the number of composite sequences that are aligned to a predicted gene
     * </p>
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public long numCompositeSequenceWithPredictedGenes( ArrayDesign arrayDesign );

    /**
     * <p>
     * function to get the number of composite sequences that are aligned to a probe-mapped region.
     * </p>
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public long numCompositeSequenceWithProbeAlignedRegion( ArrayDesign arrayDesign );

    /**
     * Returns the number of unique Genes associated with this ArrayDesign id
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public long numGenes( ArrayDesign arrayDesign );

    /**
     * 
     */
    @Secured( { "GROUP_USER", "ACL_SECURABLE_EDIT" })
    public void remove( ArrayDesign arrayDesign );

    /**
     * Remove all associations that this array design has with BioSequences. This is needed for cases where the original
     * import has associated the probes with the wrong sequences. A common case is for GEO data sets where the actual
     * oligonucleotide is not given. Instead the submitter provides Genbank accessions, which are misleading. This
     * method can be used to clear those until the "right" sequences can be identified and filled in. Note that this
     * does not delete the BioSequences, it just nulls the BiologicalCharacteristics of the CompositeSequences.
     */
    @Secured( { "GROUP_USER", "ACL_SECURABLE_EDIT" })
    public void removeBiologicalCharacteristics( ArrayDesign arrayDesign );

    /**
     * 
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public ArrayDesign thaw( ArrayDesign arrayDesign );

    /**
     * Perform a less intensive thaw of an array design.
     * 
     * @deprecated This does the same thing as 'thaw'.
     */
    @Deprecated
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "ACL_SECURABLE_READ" })
    public ArrayDesign thawLite( ArrayDesign arrayDesign );

    /**
     * 
     */
    @Secured( { "GROUP_USER", "ACL_SECURABLE_EDIT" })
    public void update( ArrayDesign arrayDesign );

    /**
     * Test whether the candidateSubsumer subsumes the candidateSubsumee. If so, the array designs are updated to
     * reflect this fact. The boolean value returned indicates whether there was indeed a subsuming relationship found.
     */
    @Secured( { "GROUP_USER", "ACL_SECURABLE_EDIT" })
    public Boolean updateSubsumingStatus( ArrayDesign candidateSubsumer, ArrayDesign candidateSubsumee );

    /**
     * @param searchString
     * @return
     */
    @Secured( { "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    public Collection<ArrayDesign> findByManufacturer( String searchString );

}
