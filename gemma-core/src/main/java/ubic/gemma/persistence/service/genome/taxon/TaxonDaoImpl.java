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
package ubic.gemma.persistence.service.genome.taxon;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.*;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ubic.gemma.model.common.description.ExternalDatabase;
import ubic.gemma.model.common.description.ExternalDatabaseValueObject;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.TaxonValueObject;
import ubic.gemma.persistence.service.VoEnabledDao;
import ubic.gemma.persistence.util.BusinessKey;
import ubic.gemma.persistence.util.ObjectFilter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author pavlidis
 * @see Taxon
 */
@Repository
public class TaxonDaoImpl extends VoEnabledDao<Taxon, TaxonValueObject> implements TaxonDao {

    @Autowired
    public TaxonDaoImpl( SessionFactory sessionFactory ) {
        super( Taxon.class, sessionFactory );
    }

    @Override
    public Taxon find( Taxon taxon ) {

        BusinessKey.checkValidKey( taxon );

        Criteria queryObject = this.getSessionFactory().getCurrentSession().createCriteria( Taxon.class )
                .setReadOnly( true );
        queryObject.setReadOnly( true );
        queryObject.setFlushMode( FlushMode.MANUAL );
        BusinessKey.addRestrictions( queryObject, taxon );

        List<?> results = queryObject.list();
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of '" + taxon.getClass().getName()
                                + "' was found when executing query" );
            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        return ( Taxon ) result;

    }

    @Override
    public Taxon findOrCreate( Taxon taxon ) {
        Taxon existingTaxon = find( taxon );
        if ( existingTaxon != null ) {
            if ( log.isDebugEnabled() )
                log.debug( "Found existing taxon: " + taxon );
            return existingTaxon;
        }

        if ( StringUtils.isBlank( taxon.getCommonName() ) && StringUtils.isBlank( taxon.getScientificName() ) ) {
            throw new IllegalArgumentException( "Cannot create a taxon without names: " + taxon );
        }

        log.warn( "Creating new taxon: " + taxon );
        return super.create( taxon );
    }

    @Override
    public Taxon findByAbbreviation( final String abbreviation ) {
        return this.findOneByStringProperty( "abbreviation", abbreviation );
    }

    @Override
    public Taxon findByCommonName( final String commonName ) {
        return this.findOneByStringProperty( "commonName", commonName );
    }

    @Override
    public Taxon findByScientificName( final String scientificName ) {
        return this.findOneByStringProperty( "scientificName", scientificName );
    }

    @Override
    public Taxon findByNcbiId( Long ncbiId ) {
        return ( Taxon ) this.findByProperty( "ncbiId", ncbiId );
    }

    @Override
    public Collection<Taxon> findTaxonUsedInEvidence() {
        //noinspection unchecked
        return this.getSessionFactory().getCurrentSession().createQuery(
                "select distinct taxon from Gene as g join g.phenotypeAssociations as evidence join g.taxon as taxon" )
                .list();
    }

    @Override
    public Collection<Taxon> findChildTaxaByParent( Taxon parentTaxon ) {
        //noinspection unchecked
        return this.getSessionFactory().getCurrentSession()
                .createQuery( "from Taxon as taxon where taxon.parentTaxon = :parentTaxon" )
                .setParameter( "parentTaxon", parentTaxon ).list();
    }

    @Override
    public void thaw( final Taxon taxon ) {
        this.getSessionFactory().getCurrentSession().doWork( new Work() {
            @Override
            public void execute( Connection connection ) throws SQLException {
                getSession().buildLockRequest( LockOptions.NONE ).lock( taxon );
                Hibernate.initialize( taxon.getParentTaxon() );
                Hibernate.initialize( taxon.getExternalDatabase() );
                getSession().evict( taxon );
            }
        } );
    }

    @Override
    public TaxonValueObject loadValueObject( Taxon entity ) {
        thaw( entity );
        return new TaxonValueObject( entity );
    }

    @Override
    public Collection<TaxonValueObject> loadValueObjects( Collection<Taxon> entities ) {
        Collection<TaxonValueObject> vos = new LinkedHashSet<>();
        for ( Taxon e : entities ) {
            vos.add( this.loadValueObject( e ) );
        }
        return vos;
    }

    @Override
    public Collection<TaxonValueObject> loadValueObjectsPreFilter( int offset, int limit, String orderBy, boolean asc,
            ArrayList<ObjectFilter[]> filter ) {
        // Compose query
        Query query = this.getLoadValueObjectsQueryString( filter, orderBy, !asc );

        query.setCacheable( true );
        if ( limit > 0 )
            query.setMaxResults( limit );
        query.setFirstResult( offset );

        //noinspection unchecked
        List<Object[]> list = query.list();
        List<TaxonValueObject> vos = new ArrayList<>( list.size() );

        for ( Object[] row : list ) {
            TaxonValueObject vo = new TaxonValueObject( ( Taxon ) row[1] );
            if ( row[2] != null ) {
                vo.setExternalDatabase( new ExternalDatabaseValueObject( ( ExternalDatabase ) row[2] ) );
            }
            if ( row[3] != null ) {
                vo.setParentTaxon( new TaxonValueObject( ( Taxon ) row[3] ) );
                if ( row[4] != null ) {
                    vo.getParentTaxon().setParentTaxon( new TaxonValueObject( ( Taxon ) row[4] ) );
                }
            }
            vos.add( vo );
        }

        return vos;
    }

    /**
     * @param filters         see {@link this#formRestrictionClause(ArrayList)} filters argument for
     *                        description.
     * @param orderByProperty the property to order by.
     * @param orderDesc       whether the ordering is ascending or descending.
     * @return a hibernate Query object ready to be used for TaxonVO retrieval.
     */
    private Query getLoadValueObjectsQueryString( ArrayList<ObjectFilter[]> filters, String orderByProperty,
            boolean orderDesc ) {

        //noinspection JpaQlInspection // the constants for aliases is messing with the inspector
        String queryString = "select " + ObjectFilter.DAO_TAXON_ALIAS + ".id as id, " // 0
                + ObjectFilter.DAO_TAXON_ALIAS + ", " // 1
                + "ED, "  // 2
                + "PT, "  // 3
                + "PTT "  // 4
                + "from Taxon as " + ObjectFilter.DAO_TAXON_ALIAS + " " // taxon
                + "left join " + ObjectFilter.DAO_TAXON_ALIAS + ".externalDatabase as ED " // external db
                + "left join " + ObjectFilter.DAO_TAXON_ALIAS + ".parentTaxon as PT " // parent taxon
                + "left join " + ObjectFilter.DAO_TAXON_ALIAS + ".parentTaxon.parentTaxon as PTT " // parent p taxon
                + "where " + ObjectFilter.DAO_TAXON_ALIAS + ".id is not null "; // needed to use formRestrictionCause()

        queryString += formRestrictionClause( filters, false );
        queryString += "group by " + ObjectFilter.DAO_TAXON_ALIAS + ".id ";
        queryString += formOrderByProperty( orderByProperty, orderDesc );

        Query query = this.getSessionFactory().getCurrentSession().createQuery( queryString );

        addRestrictionParameters( query, filters );

        return query;
    }
}