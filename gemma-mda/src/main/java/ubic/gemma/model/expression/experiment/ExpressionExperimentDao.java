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
package ubic.gemma.model.expression.experiment;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ubic.gemma.model.common.auditAndSecurity.AuditEvent;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.bioAssayData.ProcessedExpressionDataVector;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.persistence.BrowsingDao;

/**
 * @see ExpressionExperiment
 */
public interface ExpressionExperimentDao extends BioAssaySetDao<ExpressionExperiment>,
        BrowsingDao<ExpressionExperiment> {

    /**
     * 
     */
    public Integer countAll();

    /**
     * 
     */
    public ExpressionExperiment find( ExpressionExperiment expressionExperiment );

    /**
     * There can be more than one if the experiment was split up.
     */
    public Collection<ExpressionExperiment> findByAccession( ubic.gemma.model.common.description.DatabaseEntry accession );

    /**
     * Finds all the EE's that reference the given bibliographicReference id
     */
    public Collection<ExpressionExperiment> findByBibliographicReference( Long bibRefID );

    /**
     * 
     */
    public ExpressionExperiment findByBioMaterial( ubic.gemma.model.expression.biomaterial.BioMaterial bm );

    /**
     * 
     */
    public Collection<ExpressionExperiment> findByBioMaterials( Collection<BioMaterial> bioMaterials );

    /**
     * Returns a collection of expression experiments that detected the given gene at a level greater than the given
     * rank (percentile)
     */
    public Collection<ExpressionExperiment> findByExpressedGene( ubic.gemma.model.genome.Gene gene, Double rank );

    /**
     * 
     */
    public ExpressionExperiment findByFactorValue( FactorValue factorValue );

    /**
     * 
     */
    public Collection<ExpressionExperiment> findByFactorValues( Collection<FactorValue> factorValues );

    /**
     * <p>
     * returns a collection of expression experiments that have an AD that assays for the given gene
     * </p>
     */
    public Collection<ExpressionExperiment> findByGene( ubic.gemma.model.genome.Gene gene );

    /**
     * 
     */
    public ExpressionExperiment findByName( String name );

    /**
     * 
     */
    public Collection<ExpressionExperiment> findByParentTaxon( ubic.gemma.model.genome.Taxon taxon );

    public ExpressionExperiment findByQuantitationType( QuantitationType quantitationType );

    /**
     * 
     */
    public ExpressionExperiment findByShortName( String shortName );

    /**
     * 
     */
    public Collection<ExpressionExperiment> findByTaxon( ubic.gemma.model.genome.Taxon taxon );

    /**
     * 
     */
    public ExpressionExperiment findOrCreate( ExpressionExperiment expressionExperiment );

    /**
     * Get the map of ids to number of terms associated with each expression experiment.
     */
    public Map<Long, Integer> getAnnotationCounts( Collection<Long> ids );

    /**
     * @param ids
     * @return
     * @deprecated not used.
     */
    @Deprecated
    public Map<Long, Map<Long, Collection<AuditEvent>>> getArrayDesignAuditEvents( Collection<Long> ids );

    public Collection<ArrayDesign> getArrayDesignsUsed( BioAssaySet expressionExperiment );

    /**
     * Gets the AuditEvents of the specified expression experiment ids. This returns a map of id -> AuditEvent. If the
     * events do not exist, the map entry will point to null.
     */
    public Map<Long, Collection<AuditEvent>> getAuditEvents( Collection<Long> ids );

    /**
     * 
     */
    public Integer getBioAssayCountById( long id );

    /**
     * Retrieve the BioAssayDimensions for the study.
     * 
     * @param expressionExperiment
     * @return
     */
    public Collection<BioAssayDimension> getBioAssayDimensions( ExpressionExperiment expressionExperiment );

    /**
     * 
     */
    public Integer getBioMaterialCount( ExpressionExperiment expressionExperiment );

    /**
     * 
     */
    public Integer getDesignElementDataVectorCountById( long id );

    /**
     * 
     */
    public Collection<DesignElementDataVector> getDesignElementDataVectors(
            Collection<CompositeSequence> designElements,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * 
     */
    public Collection<DesignElementDataVector> getDesignElementDataVectors(
            Collection<QuantitationType> quantitationTypes );

    /**
     * 
     */
    public Map<Long, Date> getLastArrayDesignUpdate( Collection<ExpressionExperiment> expressionExperiments );

    /**
     * Gets the last audit event for the AD's associated with the given EE.
     */
    public Date getLastArrayDesignUpdate( ExpressionExperiment ee );

    /**
     * <p>
     * Returns the missing-value-masked preferred quantitation type for the experiment, if it exists, or null otherwise.
     * </p>
     */
    public ubic.gemma.model.common.quantitationtype.QuantitationType getMaskedPreferredQuantitationType(
            ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Function to get a count of expression experiments, grouped by Taxon
     * </p>
     */
    public Map<Taxon, Long> getPerTaxonCount();

    /**
     * Get map of ids to how many factor values the experiment has, counting only factor values which are associated
     * with biomaterials.
     */
    public Map<Long, Integer> getPopulatedFactorCounts( Collection<Long> ids );

    /**
     * Get map of ids to how many factor values the experiment has, counting only factor values which are associated
     * with biomaterials and not counting batch factors
     */
    public Map<Long, Integer> getPopulatedFactorCountsExcludeBatch( Collection<Long> ids );

    /**
     * @param ee
     * @return
     */
    public Collection<ProcessedExpressionDataVector> getProcessedDataVectors( ExpressionExperiment ee );

    /**
     * 
     */
    public Integer getProcessedExpressionVectorCount( ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Function to get a count of an expressionExperiment's designelementdatavectors, grouped by quantitation type.
     * </p>
     */
    public Map<QuantitationType, Integer> getQuantitationTypeCountById( Long Id );

    /**
     * 
     */
    public Collection<QuantitationType> getQuantitationTypes( ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Get the quantitation types for the expression experiment, for the array design specified. This is really only
     * useful for expression experiments that use more than one array design.
     * </p>
     */
    public Collection<QuantitationType> getQuantitationTypes( ExpressionExperiment expressionExperiment,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign );

    /**
     * 
     */
    public Map<ExpressionExperiment, Collection<AuditEvent>> getSampleRemovalEvents(
            Collection<ExpressionExperiment> expressionExperiments );

    /**
     * 
     */
    public Collection<DesignElementDataVector> getSamplingOfVectors(
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType, Integer limit );

    /**
     * <p>
     * Return any ExpressionExperimentSubSets the given Experiment might have.
     * </p>
     */
    public Collection<ExpressionExperimentSubSet> getSubSets( ExpressionExperiment expressionExperiment );

    /**
     * Gets the taxon for the given expressionExperiment
     */
    public ubic.gemma.model.genome.Taxon getTaxon( Long ExpressionExperimentID );

    /**
     * 
     */
    public Collection<ExpressionExperimentValueObject> loadValueObjects( Collection<Long> ids );

    /**
     * 
     */
    public ExpressionExperiment thaw( ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Thaws the BioAssays associated with the given ExpressionExperiment and their associations, but not the
     * DesignElementDataVectors.
     * </p>
     */
    public ExpressionExperiment thawBioAssays( ExpressionExperiment expressionExperiment );
    
    public ExpressionExperiment thawBioAssaysLiter( ExpressionExperiment expressionExperiment );

    /**
     * Return up to Math.abs(limit) experiments that were most recently updated (limit >0) or least recently updated
     * (limit < 0).
     * 
     * @param idsOfFetched
     * @param limit
     * @return
     */
    public List<ExpressionExperiment> findByUpdatedLimit( Collection<Long> ids, Integer limit );

    public Collection<ExpressionExperiment> loadLackingFactors();

    public Collection<ExpressionExperiment> loadLackingTags();

    public Collection<ExpressionExperiment> findByAccession( String accession );

    public List<ExpressionExperiment> browseSpecificIds( Integer start, Integer limit, String orderField,
            boolean descending, Collection<Long> ids );

    public List<ExpressionExperiment> browseSpecificIds( Integer start, Integer limit, Collection<Long> ids );

    public List<ExpressionExperiment> loadAllOrdered( String orderField, boolean descending );

    public List<ExpressionExperiment> loadMultipleOrdered( String orderField, boolean descending, Collection<Long> ids );

    public List<ExpressionExperiment> loadAllTaxonOrdered( String orderField, boolean descending, Taxon taxon );

    public List<ExpressionExperiment> loadAllTaxon( Taxon taxon );

    /**
     * Includes ones which are untroubled and which don't have a troubled ArrayDesign.
     * 
     * @param ids
     * @return
     */
    public Collection<Long> getUntroubled( Collection<Long> ids );

    public List<ExpressionExperiment> findByUpdatedLimit( int limit );

    /**
     * @param taxon
     * @param limit
     * @return
     */
    public List<ExpressionExperiment> findByTaxon( Taxon taxon, int limit );

    /**
     * @param firstPass
     * @return
     */
    public Map<ArrayDesign, Collection<Long>> getArrayDesignsUsed( Collection<Long> eeids );

}
