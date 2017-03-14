/*
 * The Gemma project.
 * 
 * Copyright (c) 2006 University of British Columbia
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
package ubic.gemma.expression.experiment.service;

import gemma.gsec.SecurityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubic.basecode.ontology.model.OntologyResource;
import ubic.gemma.analysis.preprocess.batcheffects.BatchConfound;
import ubic.gemma.analysis.preprocess.batcheffects.BatchConfoundValueObject;
import ubic.gemma.analysis.preprocess.batcheffects.BatchEffectDetails;
import ubic.gemma.analysis.preprocess.batcheffects.BatchInfoPopulationServiceImpl;
import ubic.gemma.analysis.preprocess.svd.SVDService;
import ubic.gemma.analysis.preprocess.svd.SVDValueObject;
import ubic.gemma.model.analysis.expression.ExpressionExperimentSet;
import ubic.gemma.model.analysis.expression.ExpressionExperimentSetDao;
import ubic.gemma.model.analysis.expression.coexpression.SampleCoexpressionAnalysisDao;
import ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis;
import ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao;
import ubic.gemma.model.analysis.expression.pca.PrincipalComponentAnalysis;
import ubic.gemma.model.analysis.expression.pca.PrincipalComponentAnalysisDao;
import ubic.gemma.model.common.auditAndSecurity.AuditEvent;
import ubic.gemma.model.common.auditAndSecurity.AuditEventDao;
import ubic.gemma.model.common.auditAndSecurity.Contact;
import ubic.gemma.model.common.auditAndSecurity.eventType.AuditEventType;
import ubic.gemma.model.common.auditAndSecurity.eventType.LinkAnalysisEvent;
import ubic.gemma.model.common.auditAndSecurity.eventType.MissingValueAnalysisEvent;
import ubic.gemma.model.common.auditAndSecurity.eventType.ProcessedVectorComputationEvent;
import ubic.gemma.model.common.description.AnnotationValueObject;
import ubic.gemma.model.common.description.BibliographicReference;
import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.model.common.description.VocabCharacteristic;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.common.quantitationtype.QuantitationTypeService;
import ubic.gemma.model.common.search.SearchSettingsImpl;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.bioAssayData.*;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.experiment.*;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.ontology.OntologyService;
import ubic.gemma.search.SearchResult;
import ubic.gemma.search.SearchService;

import java.util.*;

/**
 * @author pavlidis
 * @author keshav
 * @see ubic.gemma.expression.experiment.service.ExpressionExperimentService
 */
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@Service
@Transactional
public class ExpressionExperimentServiceImpl implements ExpressionExperimentService {

    private static final double BATCH_CONFOUND_THRESHOLD = 0.01;
    private static final Log log = LogFactory.getLog( ExpressionExperimentServiceImpl.class.getName() );

    @Autowired
    private AuditEventDao auditEventDao;

    @Autowired
    private BioAssayDimensionDao bioAssayDimensionDao;

    @Autowired
    private DifferentialExpressionAnalysisDao differentialExpressionAnalysisDao;

    @Autowired
    private ExpressionExperimentDao expressionExperimentDao;

    @Autowired
    private ExpressionExperimentSetDao expressionExperimentSetDao;

    @Autowired
    private ExpressionExperimentSubSetService expressionExperimentSubSetService;

    @Autowired
    private ExperimentalFactorDao experimentalFactorDao;

    @Autowired
    private FactorValueDao factorValueDao;

    @Autowired
    private RawExpressionDataVectorDao rawExpressionDataVectorDao;

    @Autowired
    private OntologyService ontologyService;

    @Autowired
    private PrincipalComponentAnalysisDao principalComponentAnalysisDao;

    @Autowired
    private ProcessedExpressionDataVectorDao processedVectorDao;

    @Autowired
    private QuantitationTypeService quantitationTypeDao;

    @Autowired
    private SampleCoexpressionAnalysisDao sampleCoexpressionAnalysisDao;

    @Autowired
    private SearchService searchService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SVDService svdService;

    @Autowired
    private RawExpressionDataVectorDao vectorDao;

    @Override
    @Transactional
    public ExpressionExperiment addVectors( ExpressionExperiment ee, ArrayDesign ad,
            Collection<RawExpressionDataVector> newVectors ) {

        // ee = this.load( ee.getId() );
        Collection<BioAssayDimension> bads = new HashSet<>();
        Collection<QuantitationType> qts = new HashSet<>();
        for ( RawExpressionDataVector vec : newVectors ) {
            bads.add( vec.getBioAssayDimension() );
            qts.add( vec.getQuantitationType() );
        }

        if ( bads.size() > 1 ) {
            throw new IllegalArgumentException( "Vectors must share a common bioassaydimension" );
        }

        if ( qts.size() > 1 ) {
            throw new UnsupportedOperationException(
                    "Can only replace with one type of vector (only one quantitation type)" );
        }

        BioAssayDimension bad = bads.iterator().next();

        bad = this.bioAssayDimensionDao.findOrCreate( bad );
        assert bad.getBioAssays().size() > 0;

        QuantitationType newQt = qts.iterator().next();

        if ( newQt.getId() == null ) {
            newQt = this.quantitationTypeDao.create( newQt );
        } else {
            log.warn( "Quantitation type already had an ID...:" + newQt );
        }

        for ( RawExpressionDataVector vec : newVectors ) {
            vec.setBioAssayDimension( bad );
            vec.setQuantitationType( newQt );
        }

        ee = rawExpressionDataVectorDao.addVectors( ee.getId(), newVectors );

        ArrayDesign vectorAd = newVectors.iterator().next().getDesignElement().getArrayDesign();

        if ( ad == null ) {
            for ( BioAssay ba : ee.getBioAssays() ) {
                if ( !vectorAd.equals( ba.getArrayDesignUsed() ) ) {
                    throw new IllegalArgumentException( "Vectors must use the array design as the bioassays" );
                }
            }
        } else if ( !vectorAd.equals( ad ) ) {
            throw new IllegalArgumentException( "Vectors must use the array design indicated" );
        }

        for ( BioAssay ba : ee.getBioAssays() ) {
            ba.setArrayDesignUsed( ad );
        }

        // this is a denormalization; easy to forget to update this.
        ee.getQuantitationTypes().add( newQt );

        // this.update( ee ); // is this even necessary? should flush.

        log.info( ee.getRawExpressionDataVectors().size() + " vectors for experiment" );

        return ee;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpressionExperiment> browse( Integer start, Integer limit ) {
        return this.expressionExperimentDao.browse( start, limit );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpressionExperiment> browse( Integer start, Integer limit, String orderField, boolean descending ) {
        return this.expressionExperimentDao.browse( start, limit, orderField, descending );
    }

    @Override
    @Transactional(readOnly = true)
    public Integer count() {
        return this.expressionExperimentDao.countAll();
    }

    /**
     * @see ExpressionExperimentService#countAll()
     */
    @Override
    @Transactional(readOnly = true)
    public java.lang.Integer countAll() {
        return this.expressionExperimentDao.countAll();
    }

    /**
     * @see ExpressionExperimentService#create(ExpressionExperiment)
     */
    @Override
    @Transactional
    public ExpressionExperiment create( final ExpressionExperiment expressionExperiment ) {
        return this.expressionExperimentDao.create( expressionExperiment );
    }

    /**
     * @see ExpressionExperimentService#delete(ExpressionExperiment)
     */
    @Override
    @Transactional
    public void delete( final ExpressionExperiment expressionExperiment ) {
        if ( expressionExperiment == null || expressionExperiment.getId() == null ) {
            throw new IllegalArgumentException( "Experiment is null or had null id" );
        }

        if ( securityService.isEditable( expressionExperiment ) ) {
            this.handleDelete( expressionExperiment );
        } else {
            throw new SecurityException(
                    "Error performing 'ExpressionExperimentService.delete(ExpressionExperiment expressionExperiment)' --> "
                            + " You do not have permission to edit this experiment." );
        }
    }

    /**
     * returns ids of search results
     *
     * @return collection of ids or an empty collection
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<Long> filter( String searchString ) {

        Map<Class<?>, List<SearchResult>> searchResultsMap = searchService
                .search( SearchSettingsImpl.expressionExperimentSearch( searchString ) );

        assert searchResultsMap != null;

        Collection<SearchResult> searchResults = searchResultsMap.get( ExpressionExperiment.class );

        Collection<Long> ids = new ArrayList<>( searchResults.size() );

        for ( SearchResult s : searchResults ) {
            ids.add( s.getId() );
        }

        return ids;
    }

    /**
     * @see ExpressionExperimentService#find(ExpressionExperiment)
     */
    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment find( final ExpressionExperiment expressionExperiment ) {
        return this.expressionExperimentDao.find( expressionExperiment );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByAccession( String accession ) {
        return this.expressionExperimentDao.findByAccession( accession );
    }

    /**
     * @see ExpressionExperimentService#findByAccession(ubic.gemma.model.common.description.DatabaseEntry)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByAccession(
            final ubic.gemma.model.common.description.DatabaseEntry accession ) {
        return this.expressionExperimentDao.findByAccession( accession );
    }

    /**
     * @see ExpressionExperimentService#findByBibliographicReference(ubic.gemma.model.common.description.BibliographicReference)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByBibliographicReference( final BibliographicReference bibRef ) {
        return this.expressionExperimentDao.findByBibliographicReference( bibRef.getId() );
    }

    /**
     * @see ExpressionExperimentService#findByBioAssay(ubic.gemma.model.expression.bioAssay.BioAssay)
     */
    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment findByBioAssay( final ubic.gemma.model.expression.bioAssay.BioAssay ba ) {
        return this.expressionExperimentDao.findByBioAssay( ba );
    }

    /**
     * @see ExpressionExperimentService#findByBioMaterial(ubic.gemma.model.expression.biomaterial.BioMaterial)
     */
    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment findByBioMaterial( final ubic.gemma.model.expression.biomaterial.BioMaterial bm ) {
        return this.expressionExperimentDao.findByBioMaterial( bm );
    }

    /**
     * @see ExpressionExperimentService#findByBioMaterials(Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByBioMaterials( final Collection<BioMaterial> bioMaterials ) {
        return this.expressionExperimentDao.findByBioMaterials( bioMaterials );
    }

    /**
     * @see ExpressionExperimentService#findByExpressedGene(ubic.gemma.model.genome.Gene, double)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByExpressedGene( final ubic.gemma.model.genome.Gene gene,
            final double rank ) {
        return this.expressionExperimentDao.findByExpressedGene( gene, rank );
    }

    /**
     * @see ExpressionExperimentService#findByFactor(ExperimentalFactor)
     */
    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment findByFactor( final ExperimentalFactor factor ) {
        return this.expressionExperimentDao.findByFactor( factor );
    }

    /**
     * @see ExpressionExperimentService#findByFactorValue(FactorValue)
     */
    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment findByFactorValue( final FactorValue factorValue ) {
        return this.expressionExperimentDao.findByFactorValue( factorValue );
    }

    /**
     * @see ExpressionExperimentService#findByFactorValue(FactorValue)
     */
    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment findByFactorValue( final Long factorValueId ) {
        return this.expressionExperimentDao.findByFactorValue( factorValueId );
    }

    /**
     * @see ExpressionExperimentService#findByFactorValues(Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByFactorValues( final Collection<FactorValue> factorValues ) {
        return this.expressionExperimentDao.findByFactorValues( factorValues );
    }

    /**
     * @see ExpressionExperimentService#findByGene(ubic.gemma.model.genome.Gene)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByGene( final ubic.gemma.model.genome.Gene gene ) {
        return this.expressionExperimentDao.findByGene( gene );
    }

    /**
     * @see ExpressionExperimentService#findByInvestigator(ubic.gemma.model.common.auditAndSecurity.Contact)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByInvestigator( final Contact investigator ) {
        return this.expressionExperimentDao.findByInvestigator( investigator );
    }

    /**
     * @see ExpressionExperimentService#findByName(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByName( final java.lang.String name ) {
        return this.expressionExperimentDao.findByName( name );
    }

    /**
     * @see ExpressionExperimentService#findByParentTaxon(ubic.gemma.model.genome.Taxon)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByParentTaxon( final ubic.gemma.model.genome.Taxon taxon ) {
        return this.expressionExperimentDao.findByParentTaxon( taxon );
    }

    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment findByQuantitationType( QuantitationType type ) {
        return this.expressionExperimentDao.findByQuantitationType( type );
    }

    /**
     * @see ExpressionExperimentService#findByShortName(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment findByShortName( final java.lang.String shortName ) {
        return this.expressionExperimentDao.findByShortName( shortName );
    }

    /**
     * @see ExpressionExperimentService#findByTaxon(ubic.gemma.model.genome.Taxon)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> findByTaxon( final ubic.gemma.model.genome.Taxon taxon ) {
        return this.expressionExperimentDao.findByTaxon( taxon );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpressionExperiment> findByUpdatedLimit( Integer limit ) {
        return this.expressionExperimentDao.findByUpdatedLimit( limit );
    }

    /**
     * @see ExpressionExperimentService#findOrCreate(ExpressionExperiment)
     */
    @Override
    @Transactional
    public ExpressionExperiment findOrCreate( final ExpressionExperiment expressionExperiment ) {
        return this.expressionExperimentDao.findOrCreate( expressionExperiment );
    }

    /**
     * @see ExpressionExperimentService#getAnnotationCounts(Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> getAnnotationCounts( final Collection<Long> ids ) {
        return this.expressionExperimentDao.getAnnotationCounts( ids );
    }

    /**
     * Get the terms associated this expression experiment.
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<AnnotationValueObject> getAnnotations( Long eeId ) {
        ExpressionExperiment expressionExperiment = load( eeId );
        Collection<AnnotationValueObject> annotations = new ArrayList<>();
        for ( Characteristic c : expressionExperiment.getCharacteristics() ) {
            AnnotationValueObject annotationValue = new AnnotationValueObject();
            annotationValue.setId( c.getId() );
            annotationValue.setClassName( c.getCategory() );
            annotationValue.setTermName( c.getValue() );
            annotationValue.setEvidenceCode( c.getEvidenceCode().toString() );
            if ( c instanceof VocabCharacteristic ) {
                VocabCharacteristic vc = ( VocabCharacteristic ) c;
                annotationValue.setClassUri( vc.getCategoryUri() );
                String className = getLabelFromUri( vc.getCategoryUri() );
                if ( className != null )
                    annotationValue.setClassName( className );
                annotationValue.setTermUri( vc.getValueUri() );
                String termName = getLabelFromUri( vc.getValueUri() );
                if ( termName != null )
                    annotationValue.setTermName( termName );
                annotationValue.setObjectClass( VocabCharacteristic.class.getSimpleName() );
            } else {
                annotationValue.setObjectClass( Characteristic.class.getSimpleName() );
            }
            annotations.add( annotationValue );
        }
        return annotations;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ArrayDesign> getArrayDesignsUsed( final BioAssaySet expressionExperiment ) {
        return this.expressionExperimentDao.getArrayDesignsUsed( expressionExperiment );
    }

    @Transactional(readOnly = true)
    private AuditEventDao getAuditEventDao() {
        return auditEventDao;
    }

    /**
     * @return String msg describing confound if it is present, null otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public String getBatchConfound( ExpressionExperiment ee ) {
        Collection<BatchConfoundValueObject> confounds;
        try {
            confounds = BatchConfound.test( ee );
        } catch ( Exception e ) {
            return null;
        }
        String result = null;

        for ( BatchConfoundValueObject c : confounds ) {
            if ( c.getP() < BATCH_CONFOUND_THRESHOLD ) {
                String factorName = c.getEf().getName();
                result = "Factor: " + factorName + " may be confounded with batches; p=" + String
                        .format( "%.2g", c.getP() ) + "<br />";
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public BatchEffectDetails getBatchEffect( ExpressionExperiment ee ) {

        BatchEffectDetails details = new BatchEffectDetails();

        details.setDataWasBatchCorrected( false );
        for ( QuantitationType qt : this.expressionExperimentDao.getQuantitationTypes( ee ) ) {
            if ( qt.getIsMaskedPreferred() && qt.getIsBatchCorrected() ) {
                details.setDataWasBatchCorrected( true );
                details.setHasBatchInformation( true );
            }

        }

        for ( ExperimentalFactor ef : ee.getExperimentalDesign().getExperimentalFactors() ) {
            if ( BatchInfoPopulationServiceImpl.isBatchFactor( ef ) ) {
                details.setHasBatchInformation( true );
                SVDValueObject svd = svdService.getSvdFactorAnalysis( ee.getId() );
                if ( svd == null )
                    break;
                double minp = 1.0;

                for ( Integer component : svd.getFactorPvals().keySet() ) {
                    Map<Long, Double> cmpEffects = svd.getFactorPvals().get( component );

                    Double pval = cmpEffects.get( ef.getId() );
                    if ( pval != null && pval < minp ) {
                        details.setPvalue( pval );
                        details.setComponent( component + 1 );
                        details.setComponentVarianceProportion( svd.getVariances()[component] );
                        minp = pval;
                    }

                }
                return details;
            }
        }
        return details;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BioAssayDimension> getBioAssayDimensions( ExpressionExperiment expressionExperiment ) {
        Collection<BioAssayDimension> bioAssayDimensions = this.expressionExperimentDao
                .getBioAssayDimensions( expressionExperiment );
        Collection<BioAssayDimension> thawedbioAssayDimensions = new HashSet<>();
        for ( BioAssayDimension bioAssayDimension : bioAssayDimensions ) {
            thawedbioAssayDimensions.add( this.bioAssayDimensionDao.thaw( bioAssayDimension ) );
        }
        return thawedbioAssayDimensions;
    }

    /**
     * @see ExpressionExperimentService#getBioMaterialCount(ExpressionExperiment)
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getBioMaterialCount( final ExpressionExperiment expressionExperiment ) {
        return this.expressionExperimentDao.getBioMaterialCount( expressionExperiment );
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getDesignElementDataVectorCountById( final Long id ) {
        return this.expressionExperimentDao.getDesignElementDataVectorCountById( id );
    }

    /**
     * @see ExpressionExperimentService#getDesignElementDataVectors(Collection, ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<DesignElementDataVector> getDesignElementDataVectors(
            final Collection<CompositeSequence> designElements,
            final ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) {
        return this.expressionExperimentDao.getDesignElementDataVectors( designElements, quantitationType );
    }

    /**
     * @see ExpressionExperimentService#getDesignElementDataVectors(Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<DesignElementDataVector> getDesignElementDataVectors(
            final Collection<QuantitationType> quantitationTypes ) {
        return this.expressionExperimentDao.getDesignElementDataVectors( quantitationTypes );
    }

    private DifferentialExpressionAnalysisDao getDifferentialExpressionAnalysisDao() {
        return differentialExpressionAnalysisDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> getExperimentsWithOutliers() {
        return this.expressionExperimentDao.getExperimentsWithOutliers();
    }

    private ExpressionExperimentSetDao getExpressionExperimentSetDao() {
        return expressionExperimentSetDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Date> getLastArrayDesignUpdate( final Collection<ExpressionExperiment> expressionExperiments ) {
        return this.expressionExperimentDao.getLastArrayDesignUpdate( expressionExperiments );
    }

    @Override
    @Transactional(readOnly = true)
    public Date getLastArrayDesignUpdate( final ExpressionExperiment ee ) {
        return this.expressionExperimentDao.getLastArrayDesignUpdate( ee );
    }

    /**
     * @see ExpressionExperimentService#getLastLinkAnalysis(Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, AuditEvent> getLastLinkAnalysis( final Collection<Long> ids ) {
        return getLastEvent( this.loadMultiple( ids ), LinkAnalysisEvent.Factory.newInstance() );
    }

    /**
     * @see ExpressionExperimentService#getLastMissingValueAnalysis(Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, AuditEvent> getLastMissingValueAnalysis( final Collection<Long> ids ) {
        return getLastEvent( this.loadMultiple( ids ), MissingValueAnalysisEvent.Factory.newInstance() );
    }

    /**
     * @see ExpressionExperimentService#getLastProcessedDataUpdate(Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, AuditEvent> getLastProcessedDataUpdate( final Collection<Long> ids ) {
        return getLastEvent( this.loadMultiple( ids ), ProcessedVectorComputationEvent.Factory.newInstance() );
    }

    /**
     * @return a map of the expression experiment ids to the last audit event for the given audit event type the map
     * can contain nulls if the specified auditEventType isn't found for a given expression experiment id
     */
    private Map<Long, AuditEvent> getLastEvent( Collection<ExpressionExperiment> ees, AuditEventType type ) {

        Map<Long, AuditEvent> lastEventMap = new HashMap<>();
        AuditEvent last;
        for ( ExpressionExperiment experiment : ees ) {
            last = this.getAuditEventDao().getLastEvent( experiment, type.getClass() );
            lastEventMap.put( experiment.getId(), last );
        }
        return lastEventMap;
    }

    /**
     * @see ExpressionExperimentService#getPerTaxonCount()
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Taxon, Long> getPerTaxonCount() {
        return this.expressionExperimentDao.getPerTaxonCount();
    }

    /**
     * @see ExpressionExperimentService#getPopulatedFactorCounts(Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> getPopulatedFactorCounts( final Collection<Long> ids ) {
        return this.expressionExperimentDao.getPopulatedFactorCounts( ids );
    }

    /**
     * @see ExpressionExperimentService#getPopulatedFactorCountsExcludeBatch(Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> getPopulatedFactorCountsExcludeBatch( final Collection<Long> ids ) {
        return this.expressionExperimentDao.getPopulatedFactorCountsExcludeBatch( ids );
    }

    /**
     * @see ExpressionExperimentService#getPreferredQuantitationType(ExpressionExperiment)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<QuantitationType> getPreferredQuantitationType( final ExpressionExperiment ee ) {
        Collection<QuantitationType> preferredQuantitationTypes = new HashSet<>();

        Collection<QuantitationType> quantitationTypes = this.getQuantitationTypes( ee );

        for ( QuantitationType qt : quantitationTypes ) {
            if ( qt.getIsPreferred() ) {
                preferredQuantitationTypes.add( qt );
            }
        }
        return preferredQuantitationTypes;
    }

    private PrincipalComponentAnalysisDao getPrincipalComponentAnalysisDao() {
        return principalComponentAnalysisDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ProcessedExpressionDataVector> getProcessedDataVectors( ExpressionExperiment ee ) {
        return this.expressionExperimentDao.getProcessedDataVectors( ee );
    }

    /**
     * @see ExpressionExperimentService#getQuantitationTypeCountById(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<QuantitationType, Integer> getQuantitationTypeCountById( final java.lang.Long Id ) {
        return this.expressionExperimentDao.getQuantitationTypeCountById( Id );
    }

    /**
     * @see ExpressionExperimentService#getQuantitationTypes(ExpressionExperiment)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<QuantitationType> getQuantitationTypes( final ExpressionExperiment expressionExperiment ) {
        return this.expressionExperimentDao.getQuantitationTypes( expressionExperiment );
    }

    /**
     * @see ExpressionExperimentService#getQuantitationTypes(ExpressionExperiment, ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<QuantitationType> getQuantitationTypes( final ExpressionExperiment expressionExperiment,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        return this.expressionExperimentDao.getQuantitationTypes( expressionExperiment, arrayDesign );
    }

    private SampleCoexpressionAnalysisDao getSampleCoexpressionAnalysisDao() {
        return sampleCoexpressionAnalysisDao;
    }

    /**
     * @see ExpressionExperimentService#getSampleRemovalEvents(Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<ExpressionExperiment, Collection<AuditEvent>> getSampleRemovalEvents(
            final Collection<ExpressionExperiment> expressionExperiments ) {
        return this.expressionExperimentDao.getSampleRemovalEvents( expressionExperiments );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<DesignElementDataVector> getSamplingOfVectors( final QuantitationType quantitationType,
            final Integer limit ) {
        return this.expressionExperimentDao.getSamplingOfVectors( quantitationType, limit );
    }

    /**
     * @see ExpressionExperimentService#getSubSets(ExpressionExperiment)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperimentSubSet> getSubSets( final ExpressionExperiment expressionExperiment ) {
        return this.expressionExperimentDao.getSubSets( expressionExperiment );
    }

    @Override
    @Transactional(readOnly = true)
    public Taxon getTaxon( final BioAssaySet bioAssaySet ) {
        return this.expressionExperimentDao.getTaxon( bioAssaySet );
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends BioAssaySet> Map<T, Taxon> getTaxa( Collection<T> bioAssaySets ) {
        return this.expressionExperimentDao.getTaxa( bioAssaySets );
    }

    /**
     * @see ExpressionExperimentService#load(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment load( final java.lang.Long id ) {
        return this.expressionExperimentDao.load( id );
    }

    /**
     * @see ExpressionExperimentService#loadAll()
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> loadAll() {
        return this.expressionExperimentDao.loadAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperimentValueObject> loadAllValueObjects() {
        return this.expressionExperimentDao.loadAllValueObjects();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpressionExperimentValueObject> loadAllValueObjectsOrdered( String orderField, boolean descending ) {
        return this.expressionExperimentDao.loadAllValueObjectsOrdered( orderField, descending );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpressionExperimentValueObject> loadAllValueObjectsTaxon( Taxon taxon ) {
        return this.expressionExperimentDao.loadAllValueObjectsTaxon( taxon );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpressionExperimentValueObject> loadAllValueObjectsTaxonOrdered( String orderField, boolean descending,
            Taxon taxon ) {
        return this.expressionExperimentDao.loadAllValueObjectsTaxonOrdered( orderField, descending, taxon );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> loadLackingFactors() {
        return this.expressionExperimentDao.loadLackingFactors();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> loadLackingTags() {
        return this.expressionExperimentDao.loadLackingTags();
    }

    /**
     * @see ExpressionExperimentService#loadMultiple(Collection)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> loadMultiple( final Collection<Long> ids ) {
        return this.expressionExperimentDao.load( ids );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> loadMyExpressionExperiments() {
        return loadAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> loadMySharedExpressionExperiments() {
        return loadAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperiment> loadUserOwnedExpressionExperiments() {
        return loadAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ExpressionExperimentValueObject loadValueObject( Long eeId ) {
        return this.expressionExperimentDao.loadValueObject( eeId );
    }

    /**
     * @see ExpressionExperimentService#loadValueObjects(Collection, boolean)
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExpressionExperimentValueObject> loadValueObjects( final Collection<Long> ids,
            boolean maintainOrder ) {
        return this.expressionExperimentDao.loadValueObjects( ids, maintainOrder );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpressionExperimentValueObject> loadValueObjectsOrdered( String orderField, boolean descending,
            Collection<Long> ids ) {
        return new ArrayList<>( this.expressionExperimentDao.loadValueObjectsOrdered( orderField, descending, ids ) );
    }

    @Override
    @Transactional
    public int removeData( ExpressionExperiment ee, QuantitationType qt ) {
        ExpressionExperiment eeToUpdate = this.load( ee.getId() );
        Collection<RawExpressionDataVector> vecsToRemove = new ArrayList<>();
        for ( RawExpressionDataVector oldvec : eeToUpdate.getRawExpressionDataVectors() ) {
            if ( oldvec.getQuantitationType().equals( qt ) ) {
                vecsToRemove.add( oldvec );
            }
        }

        if ( vecsToRemove.isEmpty() ) {
            throw new IllegalArgumentException( "No vectors to remove for quantitation type=" + qt );
        }

        eeToUpdate.getRawExpressionDataVectors().removeAll( vecsToRemove );
        log.info( "Removing unused quantitation type: " + qt );
        eeToUpdate.getQuantitationTypes().remove( qt );
        return vecsToRemove.size();
    }

    @Override
    @Transactional
    public ExpressionExperiment replaceVectors( ExpressionExperiment ee, ArrayDesign ad,
            Collection<RawExpressionDataVector> newVectors ) {

        if ( newVectors == null || newVectors.isEmpty() ) {
            throw new UnsupportedOperationException( "Only use this method for replacing vectors, not erasing them" );
        }

        // to attach to session correctly.
        ExpressionExperiment eeToUpdate = this.load( ee.getId() );

        // remove old vectors. FIXME are we sure we want to do this?
        Collection<QuantitationType> qtsToRemove = new HashSet<>();
        for ( RawExpressionDataVector oldvec : eeToUpdate.getRawExpressionDataVectors() ) {
            qtsToRemove.add( oldvec.getQuantitationType() );
        }
        vectorDao.remove( eeToUpdate.getRawExpressionDataVectors() );
        processedVectorDao.remove( eeToUpdate.getProcessedExpressionDataVectors() );
        eeToUpdate.getProcessedExpressionDataVectors().clear();
        eeToUpdate.getRawExpressionDataVectors().clear();

        for ( QuantitationType oldqt : qtsToRemove ) {
            log.info( "Removing unused quantitation type: " + oldqt );
            quantitationTypeDao.remove( oldqt );
        }

        return addVectors( eeToUpdate, ad, newVectors );
    }

    /**
     * Needed for tests.
     */
    public void setExpressionExperimentDao( ExpressionExperimentDao expressionExperimentDao ) {
        this.expressionExperimentDao = expressionExperimentDao;
    }

    /**
     * @see ExpressionExperimentService#thaw(ExpressionExperiment)
     */
    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment thaw( final ExpressionExperiment expressionExperiment ) {
        return this.expressionExperimentDao.thaw( expressionExperiment );
    }

    /**
     * @see ExpressionExperimentService#thawLite(ExpressionExperiment)
     */
    @Override
    @Transactional(readOnly = true)
    public ExpressionExperiment thawLite( final ExpressionExperiment expressionExperiment ) {
        return this.expressionExperimentDao.thawBioAssays( expressionExperiment );
    }

    /**
     * @see ExpressionExperimentService#thawLite(ExpressionExperiment)
     */
    @Override
    public ExpressionExperiment thawLiter( final ExpressionExperiment expressionExperiment ) {
        return this.expressionExperimentDao.thawBioAssaysLiter( expressionExperiment );
    }

    /**
     * @see ExpressionExperimentService#update(ExpressionExperiment)
     */
    @Override
    @Transactional
    public void update( final ExpressionExperiment expressionExperiment ) {
        this.expressionExperimentDao.update( expressionExperiment );
    }

    private void handleDelete( ExpressionExperiment ee ) {

        if ( ee == null ) {
            throw new IllegalArgumentException( "Experiment cannot be null" );
        }

        // Remove subsets
        Collection<ExpressionExperimentSubSet> subsets = getSubSets( ee );
        for ( ExpressionExperimentSubSet subset : subsets ) {
            expressionExperimentSubSetService.delete( subset );
        }

        // Remove differential expression analyses
        Collection<DifferentialExpressionAnalysis> diffAnalyses = this.getDifferentialExpressionAnalysisDao()
                .findByInvestigation( ee );
        for ( DifferentialExpressionAnalysis de : diffAnalyses ) {
            Long toDelete = de.getId();
            this.getDifferentialExpressionAnalysisDao().remove( toDelete );
        }

        // remove any sample coexpression matrices
        this.getSampleCoexpressionAnalysisDao().removeForExperiment( ee );

        // Remove PCA
        Collection<PrincipalComponentAnalysis> pcas = this.getPrincipalComponentAnalysisDao().findByExperiment( ee );
        for ( PrincipalComponentAnalysis pca : pcas ) {
            this.getPrincipalComponentAnalysisDao().remove( pca );
        }

        /*
         * FIXME: delete probecoexpression analysis; gene coexexpression will linger.
         */

        /*
         * Delete any expression experiment sets that only have this one ee in it. If possible remove this experiment
         * from other sets, and update them. IMPORTANT, this section assumes that we already checked for gene2gene
         * analyses!
         */
        Collection<ExpressionExperimentSet> sets = this.getExpressionExperimentSetDao().find( ee );
        for ( ExpressionExperimentSet eeset : sets ) {
            if ( eeset.getExperiments().size() == 1 && eeset.getExperiments().iterator().next().equals( ee ) ) {
                this.getExpressionExperimentSetDao().remove( eeset );
            } else {
                log.info( "Removing " + ee + " from " + eeset );
                eeset.getExperiments().remove( ee );
                this.getExpressionExperimentSetDao().update( eeset );

            }
        }

        this.expressionExperimentDao.remove( ee );
    }

    private String getLabelFromUri( String uri ) {
        OntologyResource resource = ontologyService.getResource( uri );
        if ( resource != null )
            return resource.getLabel();

        return null;
    }

    @Override
    @Transactional
    public ExperimentalFactor addFactor( ExpressionExperiment ee, ExperimentalFactor factor ) {
        ExpressionExperiment experiment = expressionExperimentDao.load( ee.getId() );
        factor.setExperimentalDesign( experiment.getExperimentalDesign() );
        factor.setSecurityOwner( experiment );
        factor = experimentalFactorDao.create( factor ); // to make sure we get acls.
        experiment.getExperimentalDesign().getExperimentalFactors().add( factor );
        expressionExperimentDao.update( experiment );
        return factor;
    }

    @Override
    @Transactional
    public FactorValue addFactorValue( ExpressionExperiment ee, FactorValue fv ) {
        assert fv.getExperimentalFactor() != null;
        ExpressionExperiment experiment = expressionExperimentDao.load( ee.getId() );
        fv.setSecurityOwner( experiment );
        Collection<ExperimentalFactor> efs = experiment.getExperimentalDesign().getExperimentalFactors();
        fv = this.factorValueDao.create( fv );
        for ( ExperimentalFactor ef : efs ) {
            if ( fv.getExperimentalFactor().equals( ef ) ) {
                ef.getFactorValues().add( fv );
                break;
            }
        }
        expressionExperimentDao.update( experiment );
        return fv;

    }

}