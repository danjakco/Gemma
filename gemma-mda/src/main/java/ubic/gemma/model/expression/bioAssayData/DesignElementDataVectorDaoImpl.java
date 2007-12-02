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
package ubic.gemma.model.expression.bioAssayData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.orm.hibernate3.HibernateTemplate;

import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.biosequence.BioSequence;
import ubic.gemma.util.BusinessKey;

/**
 * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVector
 * @author pavlidis
 * @version $Id$
 */
public class DesignElementDataVectorDaoImpl extends
        ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDaoBase {

    private static Log log = LogFactory.getLog( DesignElementDataVectorDaoImpl.class.getName() );

    /*
     * (non-Javadoc)
     */
    @Override
    public DesignElementDataVector find( DesignElementDataVector designElementDataVector ) {
        try {
            BusinessKey.checkKey( designElementDataVector );

            Criteria queryObject = super.getSession( false ).createCriteria( DesignElementDataVector.class );

            queryObject.createCriteria( "designElement" ).add(
                    Restrictions.eq( "name", designElementDataVector.getDesignElement().getName() ) ).createCriteria(
                    "arrayDesign" ).add(
                    Restrictions.eq( "name", designElementDataVector.getDesignElement().getArrayDesign().getName() ) );

            queryObject.createCriteria( "quantitationType" ).add(
                    Restrictions.eq( "name", designElementDataVector.getQuantitationType().getName() ) );

            queryObject.createCriteria( "expressionExperiment" ).add(
                    Restrictions.eq( "name", designElementDataVector.getExpressionExperiment().getName() ) );

            java.util.List results = queryObject.list();
            Object result = null;
            if ( results != null ) {
                if ( results.size() > 1 ) {
                    throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                            "More than one instance of '" + DesignElementDataVector.class.getName()
                                    + "' was found when executing query" );

                } else if ( results.size() == 1 ) {
                    result = results.iterator().next();
                }
            }
            return ( DesignElementDataVector ) result;
        } catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

    @Override
    public Collection find( ArrayDesign arrayDesign, QuantitationType quantitationType ) {
        final String queryString = "select dedv from DesignElementDataVectorImpl dedv where dedv.designElement in (:desEls) and dedv.quantitationType = :quantitationType ";
        try {
            org.hibernate.Query queryObject = super.getSession( false ).createQuery( queryString );
            queryObject.setParameter( "quantitationType", quantitationType );
            queryObject.setParameterList( "desEls", arrayDesign.getCompositeSequences() );
            return queryObject.list();
        } catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public DesignElementDataVector findOrCreate( DesignElementDataVector designElementDataVector ) {

        DesignElementDataVector existing = find( designElementDataVector );
        if ( existing != null ) {
            if ( log.isDebugEnabled() ) log.debug( "Found existing designElementDataVector: " + existing );
            return existing;
        }
        if ( log.isDebugEnabled() ) log.debug( "Creating new designElementDataVector: " + designElementDataVector );
        return ( DesignElementDataVector ) create( designElementDataVector );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDaoBase#handleQueryByGeneSymbolAndSpecies(java.lang.String,
     *      java.lang.String)
     */
    @Override
    protected Collection handleQueryByGeneSymbolAndSpecies( String geneOfficialSymbol, String species,
            Collection expressionExperiments ) throws Exception {

        final String queryString = "from DesignElementDataVectorImpl as d " // get DesignElementDataVectorImpl
                + "inner join d.designElement as de " // where de.name='probe_5'";
                + "inner join de.biologicalCharacteristic as bs " // where bs.name='test_bs'";
                + "inner join bs.bioSequence2GeneProduct as b2g "// where b2g.score=1.5";
                + "inner join b2g.geneProduct as gp inner join gp.gene as g "
                + "inner join g.taxon as t where g.officialSymbol = :geneOfficialSymbol and t.commonName = :species "
                + "and d.expressionExperiment in (:expressionExperiments)";

        try {
            org.hibernate.Query queryObject = super.getSession( false ).createQuery( queryString );
            queryObject.setParameter( "geneOfficialSymbol", geneOfficialSymbol );
            queryObject.setParameter( "species", species );
            queryObject.setParameterList( "expressionExperiments", expressionExperiments );
            java.util.List results = queryObject.list();

            if ( results != null ) {
                log.debug( "size: " + results.size() );
                for ( Object obj : results ) {
                    log.debug( obj );
                }
            }

            return results;
        } catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

    /**
     * Gets all the genes that are related to the DesignElementDataVector identified by the given ID.
     * 
     * @param id
     * @return Collection
     */
    @Override
    protected Collection handleGetGenesById( long id ) throws Exception {

        final String queryString = "select distinct geneProduct.gene from BioSequence2GeneProductImpl as bs2gp inner join bs2gp.geneProduct as geneProduct, CompositeSequenceImpl cs, "
                + " DesignElementDataVectorImpl dedv inner join dedv.designElement de"
                + " where de.id=cs.id and cs.biologicalCharacteristic=bs2gp.bioSequence and dedv.id = :id ";

        try {
            org.hibernate.Query queryObject = super.getSession( false ).createQuery( queryString );
            queryObject.setLong( "id", id );
            return queryObject.list();

        } catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

    /**
     * Gets all the genes that are related to the DesignElementDataVector.
     * 
     * @param designElementDataVector
     * @return Collection
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Collection handleGetGenes( DesignElementDataVector dedv ) throws Exception {
        return this.handleGetGenesById( dedv.getId() );
    }

    /**
     * @param ees
     * @param genes
     * @return
     */
    @Override
    protected Map handleGetVectors( Collection ees, Collection genes ) throws Exception {
        Map<DesignElementDataVector, Collection<Gene>> dedv2genes = new HashMap<DesignElementDataVector, Collection<Gene>>();

        StopWatch watch = new StopWatch();
        watch.start();

        Map<CompositeSequence, Collection<Gene>> cs2gene = getCs2GeneMap( genes );
        watch.stop();

        if ( cs2gene.keySet().size() == 0 ) {
            log.warn( "No composite sequences found for genes" );
            return dedv2genes;
        }

        log.info( "Got " + cs2gene.keySet().size() + " composite sequences for " + genes.size() + " genes in "
                + watch.getTime() + "ms" );
        watch.reset();

        // Second, get designElementDataVectors for each compositeSequence and then fill the dedv2genes
        watch.start();
        final String queryString;
        if ( ees == null || ees.size() == 0 ) {
            queryString = "select distinct dedv, dedv.designElement from DesignElementDataVectorImpl dedv"
                    + " where dedv.designElement in ( :cs ) and dedv.quantitationType.isPreferred = true";
        } else {
            queryString = "select distinct dedv, dedv.designElement from DesignElementDataVectorImpl dedv"
                    + " where dedv.designElement in (:cs ) and dedv.quantitationType.isPreferred = true"
                    + " and dedv.expressionExperiment in ( :ees )";
        }
        org.hibernate.Query queryObject = super.getSession( false ).createQuery( queryString );

        try {

            if ( ees != null && ees.size() > 0 ) {
                queryObject.setParameterList( "ees", ees );
            }
            queryObject.setParameterList( "cs", cs2gene.keySet() );

            ScrollableResults results = queryObject.scroll( ScrollMode.FORWARD_ONLY );
            log.info( "Query done in " + watch.getTime() + "ms" );

            while ( results.next() ) {
                DesignElementDataVector dedv = ( DesignElementDataVector ) results.get( 0 );
                CompositeSequence cs = ( CompositeSequence ) results.get( 1 );
                Collection<Gene> associatedGenes = cs2gene.get( cs );
                if ( !dedv2genes.containsKey( dedv ) ) {
                    dedv2genes.put( dedv, associatedGenes );
                } else {
                    Collection<Gene> mappedGenes = dedv2genes.get( dedv );
                    mappedGenes.addAll( associatedGenes );
                }
            }
            results.close();

        } catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }

        watch.stop();
        log.info( "Got " + dedv2genes.keySet().size() + " DEDV for " + cs2gene.keySet().size()
                + " composite sequences in " + watch.getTime() + "ms" );

        return dedv2genes;
    }

    /**
     * @param genes
     * @return
     */
    private Map<CompositeSequence, Collection<Gene>> getCs2GeneMap( Collection genes ) {

        // first get the composite sequences - FIXME could be done with GENE2CS native query
        final String csQueryString = "select distinct cs, gene from GeneImpl as gene"
                + " inner join gene.products gp, BlatAssociationImpl ba, CompositeSequenceImpl cs "
                + " where ba.bioSequence=cs.biologicalCharacteristic and ba.geneProduct = gp and  gene in (:genes)";

        Map<CompositeSequence, Collection<Gene>> cs2gene = new HashMap<CompositeSequence, Collection<Gene>>();
        try {
            org.hibernate.Query queryObject = super.getSession( false ).createQuery( csQueryString );
            queryObject.setParameterList( "genes", genes );
            ScrollableResults results = queryObject.scroll( ScrollMode.FORWARD_ONLY );
            while ( results.next() ) {
                CompositeSequence cs = ( CompositeSequence ) results.get( 0 );
                Gene g = ( Gene ) results.get( 1 );
                if ( !cs2gene.containsKey( cs ) ) {
                    cs2gene.put( cs, new HashSet<Gene>() );
                }

                cs2gene.get( cs ).add( g );
            }
            results.close();
        } catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
        return cs2gene;
    }

    @Override
    protected void handleThaw( final DesignElementDataVector designElementDataVector ) throws Exception {
        HibernateTemplate templ = this.getHibernateTemplate();
        templ.execute( new org.springframework.orm.hibernate3.HibernateCallback() {
            public Object doInHibernate( org.hibernate.Session session ) throws org.hibernate.HibernateException {
                thaw( session, designElementDataVector );
                return null;
            }
        }, true );

    }

    @Override
    protected void handleThaw( final Collection designElementDataVectors ) throws Exception {

        // String query = "from DesignElementDataVectorImpl d "
        // + "inner join fetch d.designElement inner join fetch d.bioAssayDimension dim "
        // + "inner join fetch dim.bioAssays ba " + "inner join fetch ba.arrayDesignUsed "
        // + "inner join fetch ba.samplesUsed " + "inner join fetch ba.derivedDataFiles where d in (:dedvs) ";

        HibernateTemplate templ = this.getHibernateTemplate();
        templ.execute( new org.springframework.orm.hibernate3.HibernateCallback() {
            public Object doInHibernate( org.hibernate.Session session ) throws org.hibernate.HibernateException {
                int count = 0;
                for ( Object object : designElementDataVectors ) {
                    DesignElementDataVector designElementDataVector = ( DesignElementDataVector ) object;
                    try {
                        thaw( session, designElementDataVector );
                    } catch ( org.hibernate.NonUniqueObjectException e ) {
                        // no problem. Ignore it. This happens in tests.
                    }
                //    session.clear();
                    if ( ++count % 10000 == 0 ) {
                        log.info( "Thawed " + count + " vectors" );
                    }
                }
                session.clear();
                if ( count  > 10000 )    log.info( "Done, thawed " + count + " vectors" );
                return null;
            }

        }, true );

    }

    /**
     * Thaw a single vector.
     * 
     * @param session
     * @param designElementDataVector
     */
    private void thaw( org.hibernate.Session session, DesignElementDataVector designElementDataVector ) {
        session.update( designElementDataVector );

        // thaw the design element.
        BioSequence seq = ( ( CompositeSequence ) designElementDataVector.getDesignElement() )
                .getBiologicalCharacteristic();
        if ( seq != null ) {
            session.update( seq );
            seq.hashCode();
        }

        ArrayDesign arrayDesign = ( ( CompositeSequence ) designElementDataVector.getDesignElement() ).getArrayDesign();
        session.update( arrayDesign ); // lock with LockMode.NONE or READ yields 'dirty collection reference' error.
        Hibernate.initialize( arrayDesign );
        arrayDesign.hashCode();

        // thaw the bioassays.
        for ( BioAssay ba : designElementDataVector.getBioAssayDimension().getBioAssays() ) {
            session.update( ba );
            ba.setArrayDesignUsed( ( ArrayDesign ) session.merge( ba.getArrayDesignUsed() ) );
            ba.getArrayDesignUsed().hashCode();
            ba.getSamplesUsed().size();
            // for ( BioMaterial bm : ba.getSamplesUsed() ) {
            // bm.getName();
            // bm.getBioAssaysUsedIn().size();
            // }
            ba.getDerivedDataFiles().size();
        }
    }

    @Override
    protected Integer handleCountAll() throws Exception {
        final String query = "select count(*) from DesignElementDataVectorImpl";
        try {
            org.hibernate.Query queryObject = super.getSession( false ).createQuery( query );

            return ( Integer ) queryObject.iterate().next();
        } catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDaoBase#handleGetGenes(java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Map handleGetGenes( Collection dataVectors ) throws Exception {
        HibernateTemplate templ = this.getHibernateTemplate();
        // implementation details in callback class
        GetGeneCallbackHandler callback = new GetGeneCallbackHandler( dataVectors );
        Map<DesignElementDataVector, Collection<Gene>> geneMap = ( Map ) templ.execute( callback, true );
        return geneMap;
    }

    /**
     * Private helper class that allows a designElementDataVector collection to be used as an argument to a
     * HibernateCallback
     * 
     * @author jsantos
     */
    private class GetGeneCallbackHandler implements org.springframework.orm.hibernate3.HibernateCallback {
        private Collection<DesignElementDataVector> dataVectors;

        public GetGeneCallbackHandler( Collection<DesignElementDataVector> dataVectors ) {
            this.dataVectors = dataVectors;
        }

        /**
         * @param dataVectors the dataVectors to set
         */
        public void setDataVectors( Collection<DesignElementDataVector> dataVectors ) {
            this.dataVectors = dataVectors;
        }

        /**
         * Callback method for HibernateTemplate
         */
        public Object doInHibernate( org.hibernate.Session session ) throws org.hibernate.HibernateException {
            /*
             * Algorithm: for each batch designElementDataVectors, do a query to get the associated genes. The results
             * will then be pushed into a map, associating the designElementDataVector with a collection of Genes.
             * Return the map.
             */
            int MAX_COUNTER = 500;
            Map<DesignElementDataVector, Collection<Gene>> geneMap = new HashMap<DesignElementDataVector, Collection<Gene>>();

            ArrayList<DesignElementDataVector> batch = new ArrayList<DesignElementDataVector>();
            // note similar query in handleGetGenesById()
            final String queryString = "select distinct dedv, geneProduct.gene from BlatAssociationImpl as bs2gp inner join bs2gp.geneProduct geneProduct, CompositeSequenceImpl cs, "
                    + " DesignElementDataVectorImpl dedv inner join dedv.designElement de"
                    + " where de.id=cs.id and cs.biologicalCharacteristic=bs2gp.bioSequence and dedv in (:ids) ";

            Iterator<DesignElementDataVector> iter = dataVectors.iterator();
            int counter = 0;
            // get up to the next N entries
            while ( iter.hasNext() ) {

                counter = 0;
                batch.clear();
                while ( ( counter < MAX_COUNTER ) && iter.hasNext() ) {
                    batch.add( iter.next() );
                    counter++;
                }

                org.hibernate.Query queryObject = session.createQuery( queryString );
                queryObject.setParameterList( "ids", batch );
                // get results and push into hashmap.

                ScrollableResults results = queryObject.scroll( ScrollMode.FORWARD_ONLY );
                while ( results.next() ) {
                    DesignElementDataVector dedv = ( DesignElementDataVector ) results.get( 0 );
                    Gene g = ( Gene ) results.get( 1 );
                    // if the key exists, push into collection
                    // if the key does not exist, create and put hashset into the map
                    if ( geneMap.containsKey( dedv ) ) {
                        if ( !geneMap.get( dedv ).add( g ) ) {
                            if ( log.isDebugEnabled() ) log.debug( "Failed to add " + g.getName() + "; Duplicate" );
                        }
                    } else {
                        Collection<Gene> genes = new HashSet<Gene>();
                        genes.add( g );
                        geneMap.put( dedv, genes );
                    }
                }
                results.close();
            }
            return geneMap;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDaoBase#handleRemoveDataForCompositeSequence(ubic.gemma.model.expression.designElement.CompositeSequence)
     */
    @Override
    protected void handleRemoveDataForCompositeSequence( final CompositeSequence compositeSequence ) throws Exception {

        String[] probeCoexpTypes = new String[] { "Mouse", "Human", "Rat", "Other" };

        for ( String type : probeCoexpTypes ) {

            final String dedvRemovalQuery = "from DesignElementDataVectorImpl dedv where dedv.designElement = :cs";

            final String ppcRemoveFirstQuery = "from " + type
                    + "ProbeCoExpressionImpl as p where p.firstVector.designElement = :cs)";
            final String ppcRemoveSecondQuery = "from " + type
                    + "ProbeCoExpressionImpl as p where p.secondVector.designElement = :cs)";

            HibernateTemplate templ = this.getHibernateTemplate();
            templ.execute( new org.springframework.orm.hibernate3.HibernateCallback() {
                public Object doInHibernate( org.hibernate.Session session ) throws org.hibernate.HibernateException {
                    assert session != null;

                    org.hibernate.Query queryObject = session.createQuery( ppcRemoveFirstQuery );
                    queryObject.setParameter( "cs", compositeSequence );

                    Collection os = queryObject.list();
                    for ( Object obj : os ) {
                        session.delete( obj );
                    }

                    queryObject = session.createQuery( ppcRemoveSecondQuery );
                    queryObject.setParameter( "cs", compositeSequence );
                    os = queryObject.list();
                    for ( Object obj : os ) {
                        session.delete( obj );
                    }

                    queryObject = session.createQuery( dedvRemovalQuery );
                    queryObject.setParameter( "cs", compositeSequence );
                    os = queryObject.list();
                    for ( Object obj : os ) {
                        session.delete( obj );
                    }

                    return null;
                }
            }, true );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDaoBase#handleRemoveDataFromQuantitationType(ubic.gemma.model.expression.experiment.ExpressionExperiment,
     *      ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    @Override
    protected void handleRemoveDataFromQuantitationType( final ExpressionExperiment expressionExperiment,
            final QuantitationType quantitationType ) throws Exception {
        final String dedvRemovalQuery = "delete from DesignElementDataVectorImpl as dedv where dedv.expressionExperiment = :ee and dedv.quantitationType = :qt";

        HibernateTemplate templ = this.getHibernateTemplate();
        templ.execute( new org.springframework.orm.hibernate3.HibernateCallback() {
            public Object doInHibernate( org.hibernate.Session session ) throws org.hibernate.HibernateException {

                org.hibernate.Query queryObject = session.createQuery( dedvRemovalQuery );
                queryObject.setParameter( "ee", expressionExperiment );
                queryObject.setParameter( "qt", quantitationType );
                int modified = queryObject.executeUpdate();
                log.info( "Deleted " + modified + " data vector elements" );

                return null;
            }
        } );

    }

    // protected Map handleGetGene2DedvsMap(Collection genes, QuantitationType qt) {
    // Map<Gene, Collection<DesignElementDataVector>> gene2dedvs = new HashMap<Gene,
    // Collection<DesignElementDataVector>>();
    // StringBuffer geneIdList = new StringBuffer();
    // for (Object obj : genes) {
    // if (obj instanceof Gene) {
    // Gene gene = (Gene) obj;
    // geneIdList.append( gene.getId() );
    // geneIdList.append( ',' );
    // }
    // }
    // geneIdList.deleteCharAt( geneIdList.length() - 1 );
    // String queryString = "SELECT "
    // }

    @Override
    protected Map handleGetDedv2GenesMap( Collection dedvs, QuantitationType qt ) throws Exception {
        Map<DesignElementDataVector, Collection<Gene>> dedv2genes = new HashMap<DesignElementDataVector, Collection<Gene>>();
        Map<Long, DesignElementDataVector> dedvMap = new HashMap<Long, DesignElementDataVector>();

        StringBuffer dedvIdList = new StringBuffer();

        for ( Object object : dedvs ) {
            if ( object instanceof DesignElementDataVector ) {
                DesignElementDataVector dedv = ( DesignElementDataVector ) object;
                dedvIdList.append( dedv.getId() );
                dedvIdList.append( ',' );
                dedvMap.put( dedv.getId(), dedv );
            }
        }

        dedvIdList.deleteCharAt( dedvIdList.length() - 1 );

        // Native query - faster? Fetches all data for that QT and throws away unneeded portion
        String queryString = "SELECT DESIGN_ELEMENT_DATA_VECTOR.ID as dedvId, DESIGN_ELEMENT_FK as csId, GENE as geneId, CHROMOSOME_FEATURE.ID as featureID, "
                + " CHROMOSOME_FEATURE.OFFICIAL_NAME as officialName, CHROMOSOME_FEATURE.OFFICIAL_SYMBOL as officialSymbol FROM DESIGN_ELEMENT_DATA_VECTOR, GENE2CS, CHROMOSOME_FEATURE WHERE "
                + " QUANTITATION_TYPE_FK = "
                + qt.getId()
                + " AND GENE2CS.CS=DESIGN_ELEMENT_FK AND GENE2CS.GENE=CHROMOSOME_FEATURE.ID AND DESIGN_ELEMENT_DATA_VECTOR.ID in ("
                + dedvIdList + ")";

        Session session = getSessionFactory().openSession();
        org.hibernate.SQLQuery queryObject = session.createSQLQuery( queryString );

        queryObject.addScalar( "dedvId", new LongType() );
        queryObject.addScalar( "geneId", new LongType() );
        queryObject.addScalar( "featureID", new LongType() );
        queryObject.addScalar( "officialName", new StringType() );
        queryObject.addScalar( "officialSymbol", new StringType() );

        ScrollableResults scroll = queryObject.scroll( ScrollMode.FORWARD_ONLY );

        while ( scroll.next() ) {

            // get the data returned from the query
            Long dedvId = scroll.getLong( 0 );
            Long geneId = scroll.getLong( 1 );
            Long featureId = scroll.getLong( 2 );
            String officialName = scroll.getString( 3 );
            String officialSymbol = scroll.getString( 4 );

            // Create the objects we want to put in the hashmap
            Gene gene = Gene.Factory.newInstance();
            gene.setOfficialName( officialName );
            gene.setOfficialSymbol( officialSymbol );
            gene.setId( geneId );

            DesignElementDataVector dedv = dedvMap.get( dedvId );

            // Test to see if we can just add or if we have to make a collection
            // TODO: this might be problomatic due to the == operator and the hash function for DEDV
            if ( dedv2genes.containsKey( dedv ) ) {
                dedv2genes.get( dedv ).add( gene );
            } else {
                Collection<Gene> genes = new HashSet<Gene>();
                genes.add( gene );
                dedv2genes.put( dedv, genes );
            }

        }
        session.clear();
        session.close();
        return dedv2genes;
    }
}
