package ubic.gemma.model.expression.arrayDesign;

import org.springframework.beans.factory.InitializingBean;
import ubic.gemma.model.common.auditAndSecurity.AuditEvent;
import ubic.gemma.model.common.auditAndSecurity.curation.CuratableDao;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.biosequence.BioSequence;
import ubic.gemma.model.genome.sequenceAnalysis.BlatResult;

import java.util.Collection;
import java.util.Map;

/**
 * Created by tesarst on 13/03/17.
 */
public interface ArrayDesignDao extends InitializingBean, CuratableDao<ArrayDesign> {
    ArrayDesign find( ArrayDesign entity );

    Map<Taxon, Long> getPerTaxonCount();

    void addProbes( ArrayDesign arrayDesign, Collection<CompositeSequence> newProbes );

    ArrayDesign find( String queryString, ArrayDesign entity );

    Collection<ArrayDesign> findByManufacturer( String queryString );

    Collection<ArrayDesign> findByTaxon( Taxon taxon );

    Map<CompositeSequence, BioSequence> getBioSequences( ArrayDesign arrayDesign );

    Map<CompositeSequence, Collection<BlatResult>> loadAlignments( ArrayDesign arrayDesign );

    int numExperiments( ArrayDesign arrayDesign );

    ArrayDesign thawLite( ArrayDesign arrayDesign );

    Collection<ArrayDesign> thawLite( Collection<ArrayDesign> arrayDesigns );

    Collection<CompositeSequence> compositeSequenceWithoutBioSequences( ArrayDesign arrayDesign );

    Collection<CompositeSequence> compositeSequenceWithoutBlatResults( ArrayDesign arrayDesign );

    Collection<CompositeSequence> compositeSequenceWithoutGenes( ArrayDesign arrayDesign );

    void deleteAlignmentData( ArrayDesign arrayDesign );

    void deleteGeneProductAssociations( ArrayDesign arrayDesign );

    Collection<ArrayDesign> findByAlternateName( String queryString );

    Collection<BioAssay> getAllAssociatedBioAssays( Long id );

    Map<Long, Collection<AuditEvent>> getAuditEvents( Collection<Long> ids );

    Collection<ExpressionExperiment> getExpressionExperiments( ArrayDesign arrayDesign );

    Collection<Taxon> getTaxa( Long id );

    Taxon getTaxon( Long id );

    Map<Long, Boolean> isMerged( Collection<Long> ids );

    Map<Long, Boolean> isMergee( Collection<Long> ids );

    Map<Long, Boolean> isSubsumed( Collection<Long> ids );

    Map<Long, Boolean> isSubsumer( Collection<Long> ids );

    Collection<ArrayDesignValueObject> loadAllValueObjects();

    Collection<ArrayDesignValueObject> loadValueObjects( Collection<Long> ids );

    Collection<CompositeSequence> loadCompositeSequences( Long id );

    long numAllCompositeSequenceWithBioSequences();

    long numAllCompositeSequenceWithBioSequences( Collection<Long> ids );

    long numAllCompositeSequenceWithBlatResults();

    long numAllCompositeSequenceWithBlatResults( Collection<Long> ids );

    long numAllCompositeSequenceWithGenes();

    long numAllCompositeSequenceWithGenes( Collection<Long> ids );

    long numAllGenes();

    long numAllGenes( Collection<Long> ids );

    long numBioSequences( ArrayDesign arrayDesign );

    long numBlatResults( ArrayDesign arrayDesign );

    long numCompositeSequences( Long id );

    long numCompositeSequenceWithBioSequences( ArrayDesign arrayDesign );

    long numCompositeSequenceWithBlatResults( ArrayDesign arrayDesign );

    long numCompositeSequenceWithGenes( ArrayDesign arrayDesign );

    long numGenes( ArrayDesign arrayDesign );

    void removeBiologicalCharacteristics( ArrayDesign arrayDesign );

    ArrayDesign thaw( ArrayDesign arrayDesign );

    Boolean updateSubsumingStatus( ArrayDesign candidateSubsumer, ArrayDesign candidateSubsumee );
}
