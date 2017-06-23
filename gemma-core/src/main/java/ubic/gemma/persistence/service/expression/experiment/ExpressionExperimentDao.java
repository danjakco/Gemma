package ubic.gemma.persistence.service.expression.experiment;

import org.springframework.beans.factory.InitializingBean;
import ubic.gemma.model.common.auditAndSecurity.AuditEvent;
import ubic.gemma.model.common.auditAndSecurity.Contact;
import ubic.gemma.model.common.description.DatabaseEntry;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.bioAssayData.ProcessedExpressionDataVector;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.experiment.*;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.persistence.service.common.auditAndSecurity.curation.CuratableDao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by tesarst on 13/03/17.
 *
 * @author tesarst
 */
public interface ExpressionExperimentDao
        extends InitializingBean, CuratableDao<ExpressionExperiment, ExpressionExperimentValueObject> {

    Collection<ExpressionExperiment> findByInvestigator( Contact investigator );

    ExpressionExperiment thaw( ExpressionExperiment expressionExperiment );

    ExpressionExperiment thawBioAssays( ExpressionExperiment expressionExperiment );

    ExpressionExperiment thawBioAssaysLiter( ExpressionExperiment expressionExperiment );

    Collection<ExpressionExperimentValueObject> listFilter( int offset, int limit, String orderBy, boolean asc,
            DatabaseEntry accession );

    List<ExpressionExperiment> browse( Integer start, Integer limit );

    List<ExpressionExperiment> browse( Integer start, Integer limit, String orderField, boolean descending );

    List<ExpressionExperiment> browseSpecificIds( Integer start, Integer limit, Collection<Long> ids );

    List<ExpressionExperiment> browseSpecificIds( Integer start, Integer limit, String orderField, boolean descending,
            Collection<Long> ids );

    Collection<ExpressionExperiment> findByAccession( DatabaseEntry accession );

    Collection<ExpressionExperiment> findByAccession( String accession );

    List<ExpressionExperiment> findByTaxon( Taxon taxon, Integer limit );

    List<ExpressionExperiment> findByUpdatedLimit( Collection<Long> ids, Integer limit );

    List<ExpressionExperiment> findByUpdatedLimit( Integer limit );

    Collection<ArrayDesign> getArrayDesignsUsed( BioAssaySet bas );

    Map<ArrayDesign, Collection<Long>> getArrayDesignsUsed( Collection<Long> eeids );

    Collection<BioAssayDimension> getBioAssayDimensions( ExpressionExperiment expressionExperiment );

    Collection<ExpressionExperiment> getExperimentsWithOutliers();

    Collection<ProcessedExpressionDataVector> getProcessedDataVectors( ExpressionExperiment expressionExperiment );

    List<ExpressionExperimentValueObject> loadAllValueObjectsOrdered( String orderField, boolean descending );

    List<ExpressionExperimentValueObject> loadAllValueObjectsTaxon( Taxon taxon );

    Collection<ExpressionExperimentValueObject> loadValueObjectsOrdered( String orderField, boolean descending,
            Collection<Long> ids );

    List<ExpressionExperimentValueObject> loadAllValueObjectsTaxonOrdered( String orderField, boolean descending,
            Taxon taxon );

    Collection<ExpressionExperiment> loadLackingFactors();

    Collection<ExpressionExperiment> loadLackingTags();

    ExpressionExperimentValueObject loadValueObject( Long eeId );

    Collection<ExpressionExperimentValueObject> loadValueObjects( Collection<Long> ids, boolean maintainOrder );

    Collection<ExpressionExperiment> findByBibliographicReference( Long bibRefID );

    ExpressionExperiment findByBioAssay( BioAssay ba );

    ExpressionExperiment findByBioMaterial( BioMaterial bm );

    Collection<ExpressionExperiment> findByBioMaterials( Collection<BioMaterial> bms );

    Collection<ExpressionExperiment> findByExpressedGene( Gene gene, Double rank );

    ExpressionExperiment findByFactor( ExperimentalFactor ef );

    ExpressionExperiment findByFactorValue( FactorValue fv );

    ExpressionExperiment findByFactorValue( Long factorValueId );

    Collection<ExpressionExperiment> findByFactorValues( Collection<FactorValue> fvs );

    Collection<ExpressionExperiment> findByGene( Gene gene );

    Collection<ExpressionExperiment> findByParentTaxon( Taxon taxon );

    ExpressionExperiment findByQuantitationType( QuantitationType quantitationType );

    Collection<ExpressionExperiment> findByTaxon( Taxon taxon );

    Map<Long, Integer> getAnnotationCounts( Collection<Long> ids );

    @Deprecated
    Map<Long, Map<Long, Collection<AuditEvent>>> getArrayDesignAuditEvents( Collection<Long> ids );

    Map<Long, Collection<AuditEvent>> getAuditEvents( Collection<Long> ids );

    Integer getBioAssayCountById( long Id );

    Integer getBioMaterialCount( ExpressionExperiment expressionExperiment );

    Integer getDesignElementDataVectorCountById( long Id );

    Collection<DesignElementDataVector> getDesignElementDataVectors( Collection<CompositeSequence> designElements,
            QuantitationType quantitationType );

    Collection<DesignElementDataVector> getDesignElementDataVectors( Collection<QuantitationType> quantitationTypes );

    Map<Long, Date> getLastArrayDesignUpdate( Collection<ExpressionExperiment> expressionExperiments );

    Date getLastArrayDesignUpdate( ExpressionExperiment ee );

    QuantitationType getMaskedPreferredQuantitationType( ExpressionExperiment ee );

    Map<Taxon, Long> getPerTaxonCount();

    Map<Long, Integer> getPopulatedFactorCounts( Collection<Long> ids );

    Map<Long, Integer> getPopulatedFactorCountsExcludeBatch( Collection<Long> ids );

    Map<QuantitationType, Integer> getQuantitationTypeCountById( Long id );

    Collection<QuantitationType> getQuantitationTypes( ExpressionExperiment expressionExperiment );

    Collection<QuantitationType> getQuantitationTypes( ExpressionExperiment expressionExperiment,
            ArrayDesign arrayDesign );

    Map<ExpressionExperiment, Collection<AuditEvent>> getSampleRemovalEvents(
            Collection<ExpressionExperiment> expressionExperiments );

    Collection<DesignElementDataVector> getSamplingOfVectors( QuantitationType quantitationType, Integer limit );

    Collection<ExpressionExperimentSubSet> getSubSets( ExpressionExperiment expressionExperiment );

    Taxon getTaxon( BioAssaySet ee );

    <T extends BioAssaySet> Map<T, Taxon> getTaxa( Collection<T> bioAssaySets );
}
