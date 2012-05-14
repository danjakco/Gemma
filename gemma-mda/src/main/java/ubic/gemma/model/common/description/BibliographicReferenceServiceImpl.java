/*
 * The Gemma project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.gemma.model.common.description;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ubic.gemma.model.association.phenotype.PhenotypeAssociation;
import ubic.gemma.model.association.phenotype.service.PhenotypeAssociationService;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentValueObject;
import ubic.gemma.model.genome.gene.phenotype.valueObject.BibliographicPhenotypesValueObject;
//import ubic.gemma.search.SearchService;
//import ubic.gemma.search.SearchSettings;

/**
 * Implementation of BibliographicReferenceService.
 * 
 * @author keshav
 * @version $Id$
 * @see ubic.gemma.model.common.description.BibliographicReferenceService
 */
@Service
public class BibliographicReferenceServiceImpl extends
        ubic.gemma.model.common.description.BibliographicReferenceServiceBase {

    private static final String PUB_MED_DATABASE_NAME = "PubMed";
    
    @Autowired
    private PhenotypeAssociationService phenotypeAssociationService;
    //@Autowired
    //private SearchService searchService;

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.common.description.BibliographicReferenceServiceBase#handleAddDocument(byte[],
     * ubic.gemma.model.common.description.BibliographicReference)
     */
    @Override
    protected void handleAddPDF( LocalFile pdfFile, BibliographicReference bibliographicReference ) throws Exception {
        bibliographicReference.setFullTextPdf( pdfFile );
        this.getBibliographicReferenceDao().update( bibliographicReference );

    }

    /**
     * @see ubic.gemma.model.common.description.BibliographicReferenceService#saveBibliographicReference(ubic.gemma.model.common.description.BibliographicReference)
     */
    @Override
    protected BibliographicReference handleCreate(
            ubic.gemma.model.common.description.BibliographicReference bibliographicReference )
            throws java.lang.Exception {
        return getBibliographicReferenceDao().create( bibliographicReference );
    }

    /**
     * Check to see if the reference already exists
     * 
     * @see ubic.gemma.model.common.description.BibliographicReferenceService#alreadyExists(ubic.gemma.model.common.description.BibliographicReference)
     */
    @Override
    protected BibliographicReference handleFind(
            ubic.gemma.model.common.description.BibliographicReference bibliographicReference )
            throws java.lang.Exception {

        return getBibliographicReferenceDao().find( bibliographicReference );
    }

    /**
     * @see ubic.gemma.model.common.description.BibliographicReferenceService#findByExternalId(java.lang.String)
     */
    @Override
    protected ubic.gemma.model.common.description.BibliographicReference handleFindByExternalId( java.lang.String id )
            throws java.lang.Exception {

        return this.getBibliographicReferenceDao().findByExternalId( id, PUB_MED_DATABASE_NAME );

    }

    /**
     * @see ubic.gemma.model.common.description.BibliographicReferenceService#findByExternalId(java.lang.String,
     *      java.lang.String)
     */
    @Override
    protected ubic.gemma.model.common.description.BibliographicReference handleFindByExternalId( java.lang.String id,
            java.lang.String databaseName ) throws java.lang.Exception {

        return this.getBibliographicReferenceDao().findByExternalId( id, databaseName );
    }

    

    @Override
    protected BibliographicReference handleFindOrCreate( BibliographicReference bibliographicReference )
            throws Exception {
        return this.getBibliographicReferenceDao().findOrCreate( bibliographicReference );
    }

    @Override
    protected Map<ExpressionExperiment, BibliographicReference> handleGetAllExperimentLinkedReferences()
            throws Exception {
        return this.getBibliographicReferenceDao().getAllExperimentLinkedReferences();
    }

    @Override
    protected Collection handleGetRelatedExperiments( BibliographicReference bibliographicReference ) throws Exception {
        return this.getBibliographicReferenceDao().getRelatedExperiments( bibliographicReference );
    }

    @Override
    protected BibliographicReference handleLoad( Long id ) throws Exception {
        return this.getBibliographicReferenceDao().load( id );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ubic.gemma.model.common.description.BibliographicReferenceServiceBase#handleLoadBibliographicReference(java.lang
     * .Long)
     */
    protected BibliographicReference handleLoadBibliographicReference( Long id ) throws Exception {
        return getBibliographicReferenceDao().load( id );
    }

    @Override
    protected Collection<BibliographicReference> handleLoadMultiple( Collection<Long> ids ) throws Exception {
        return this.getBibliographicReferenceDao().load( ids );
    }

    @Override
    protected Collection<BibliographicReferenceValueObject> handleLoadMultipleValueObjects( Collection<Long> ids ) throws Exception {
        Collection<BibliographicReference> bibRefs = this.getBibliographicReferenceDao().load( ids );
        if(bibRefs.isEmpty()){
            return new ArrayList<BibliographicReferenceValueObject>();
        }
        Map<Long, BibliographicReferenceValueObject> idTobibRefVO =  new HashMap<Long, BibliographicReferenceValueObject>();
        
        for(BibliographicReference bibref : bibRefs){
            BibliographicReferenceValueObject vo = new BibliographicReferenceValueObject( bibref );
            idTobibRefVO.put( bibref.getId(), vo );
        }
        
        populateRelatedExperiments( bibRefs, idTobibRefVO );
        populateBibliographicPhenotypes( idTobibRefVO );
        
        return idTobibRefVO.values();
    }

    /**
     * @param bibRefs
     * @param idTobibRefVO
     */
    private void populateRelatedExperiments( Collection<BibliographicReference> bibRefs,
            Map<Long, BibliographicReferenceValueObject> idTobibRefVO ) {
        Map<BibliographicReference, Collection<ExpressionExperiment>> relatedExperiments = this
                .getRelatedExperiments( bibRefs );
        for ( BibliographicReference bibref : bibRefs ) {
            BibliographicReferenceValueObject vo = idTobibRefVO.get( bibref.getId() );
            if ( relatedExperiments.containsKey( bibref ) ) {
                vo.setExperiments( ExpressionExperimentValueObject.convert2ValueObjects( relatedExperiments
                        .get( bibref ) ) );
            }
        }
    }

    /**
     * @param bibRefs
     * @param idTobibRefVO
     */
    private void populateBibliographicPhenotypes( Map<Long, BibliographicReferenceValueObject> idTobibRefVO ) {

        for(BibliographicReferenceValueObject vo : idTobibRefVO.values()){
            Collection<PhenotypeAssociation> phenotypeAssociations = this.phenotypeAssociationService
                    .findPhenotypesForBibliographicReference( vo.getPubAccession() );

            Collection<BibliographicPhenotypesValueObject> bibliographicPhenotypesValueObjects = BibliographicPhenotypesValueObject
                    .phenotypeAssociations2BibliographicPhenotypesValueObjects( phenotypeAssociations );
            vo.setBibliographicPhenotypes( bibliographicPhenotypesValueObjects );
        }
    }

    @Override
    protected void handleRemove( BibliographicReference bibliographicReference ) throws Exception {
        this.getBibliographicReferenceDao().remove( bibliographicReference );
    }

    /**
     * @see ubic.gemma.model.common.description.BibliographicReferenceService#saveBibliographicReference(ubic.gemma.model.common.description.BibliographicReference)
     */
    @Override
    protected void handleUpdate( ubic.gemma.model.common.description.BibliographicReference BibliographicReference )
            throws java.lang.Exception {
        getBibliographicReferenceDao().update( BibliographicReference );
    }

    /**
     * 
     */
    public BibliographicReference findByExternalId( DatabaseEntry accession ) {
        return this.getBibliographicReferenceDao().findByExternalId( accession );
    }

    /*
     * (non-Javadoc)
     * 
     * @seeubic.gemma.model.common.description.BibliographicReferenceService#thaw(ubic.gemma.model.common.description.
     * BibliographicReference)
     */
    @Override
    public BibliographicReference thaw( BibliographicReference bibliographicReference ) {
        return this.getBibliographicReferenceDao().thaw( bibliographicReference );
    }

    /*
     * (non-Javadoc)
     * 
     * 
     * /* (non-Javadoc)
     * 
     * @see ubic.gemma.model.common.description.BibliographicReferenceService#thaw(java.util.Collection)
     */
    @Override
    public Collection<BibliographicReference> thaw( Collection<BibliographicReference> bibliographicReferences ) {
        return this.getBibliographicReferenceDao().thaw( bibliographicReferences );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.common.description.BibliographicReferenceService#browse(java.lang.Integer,
     * java.lang.Integer)
     */
    @Override
    public List<BibliographicReference> browse( Integer start, Integer limit ) {
        return this.getBibliographicReferenceDao().browse( start, limit );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.common.description.BibliographicReferenceService#browse(java.lang.Integer,
     * java.lang.Integer, java.lang.String, boolean)
     */
    @Override
    public List<BibliographicReference> browse( Integer start, Integer limit, String orderField, boolean descending ) {
        return this.getBibliographicReferenceDao().browse( start, limit, orderField, descending );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.common.description.BibliographicReferenceService#count()
     */
    @Override
    public Integer count() {
        return this.getBibliographicReferenceDao().count();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ubic.gemma.model.common.description.BibliographicReferenceService#getRelatedExperiments(java.util.Collection)
     */
    @Override
    public Map<BibliographicReference, Collection<ExpressionExperiment>> getRelatedExperiments(
            Collection<BibliographicReference> records ) {
        return this.getBibliographicReferenceDao().getRelatedExperiments( records );

    }
    
    /*@Override 
    public List<BibliographicReferenceValueObject> search(String query){
        List<BibliographicReference> resultEntities = ( List<BibliographicReference> ) searchService.search( SearchSettings.bibliographicReferenceSearch( query ), BibliographicReference.class );
        List<BibliographicReferenceValueObject> results = BibliographicReferenceValueObject.convert2ValueObjects( resultEntities );
        return results;
        
    }*/

}