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
package ubic.gemma.model.association;

/**
 * <p>
 * Base Spring DAO Class: is able to create, update, remove, load, and find objects of type
 * <code>ubic.gemma.model.association.Gene2GOAssociation</code>.
 * </p>
 * 
 * @see ubic.gemma.model.association.Gene2GOAssociation
 */
public abstract class Gene2GOAssociationDaoBase extends
        ubic.gemma.model.association.Gene2OntologyEntryAssociationDaoImpl implements
        ubic.gemma.model.association.Gene2GOAssociationDao {

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#create(int, java.util.Collection)
     */
    public java.util.Collection create( final int transform, final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "Gene2GOAssociation.create - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession( new org.springframework.orm.hibernate3.HibernateCallback() {
            public Object doInHibernate( org.hibernate.Session session ) throws org.hibernate.HibernateException {
                for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                    create( transform, ( ubic.gemma.model.association.Gene2GOAssociation ) entityIterator.next() );
                }
                return null;
            }
        }  );
        return entities;
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#create(int transform,
     *      ubic.gemma.model.association.Gene2GOAssociation)
     */
    public Object create( final int transform, final ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        if ( gene2GOAssociation == null ) {
            throw new IllegalArgumentException( "Gene2GOAssociation.create - 'gene2GOAssociation' can not be null" );
        }
        this.getHibernateTemplate().save( gene2GOAssociation );
        return this.transformEntity( transform, gene2GOAssociation );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#create(java.util.Collection)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection create( final java.util.Collection entities ) {
        return create( TRANSFORM_NONE, entities );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#create(ubic.gemma.model.association.Gene2GOAssociation)
     */
    public ubic.gemma.model.association.Relationship create(
            ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        return ( ubic.gemma.model.association.Gene2GOAssociation ) this.create( TRANSFORM_NONE, gene2GOAssociation );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#find(int, java.lang.String,
     *      ubic.gemma.model.association.Gene2GOAssociation)
     */
    @SuppressWarnings( { "unchecked" })
    public Object find( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( gene2GOAssociation );
        argNames.add( "gene2GOAssociation" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of 'ubic.gemma.model.association.Gene2GOAssociation"
                                + "' was found when executing query --> '" + queryString + "'" );
            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        result = transformEntity( transform, ( ubic.gemma.model.association.Gene2GOAssociation ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#find(int,
     *      ubic.gemma.model.association.Gene2GOAssociation)
     */
    @SuppressWarnings( { "unchecked" })
    public Object find( final int transform, final ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        return this
                .find(
                        transform,
                        "from ubic.gemma.model.association.Gene2GOAssociation as gene2GOAssociation where gene2GOAssociation.gene2GOAssociation = :gene2GOAssociation",
                        gene2GOAssociation );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#find(java.lang.String,
     *      ubic.gemma.model.association.Gene2GOAssociation)
     */
    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.association.Gene2GOAssociation find( final java.lang.String queryString,
            final ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        return ( ubic.gemma.model.association.Gene2GOAssociation ) this.find( TRANSFORM_NONE, queryString,
                gene2GOAssociation );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#find(ubic.gemma.model.association.Gene2GOAssociation)
     */
    public ubic.gemma.model.association.Gene2GOAssociation find(
            ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        return ( ubic.gemma.model.association.Gene2GOAssociation ) this.find( TRANSFORM_NONE, gene2GOAssociation );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#findAssociationByGene(ubic.gemma.model.genome.Gene)
     */
    public java.util.Collection findAssociationByGene( final ubic.gemma.model.genome.Gene gene ) {
        try {
            return this.handleFindAssociationByGene( gene );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.association.Gene2GOAssociationDao.findAssociationByGene(ubic.gemma.model.genome.Gene gene)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#findByGene(ubic.gemma.model.genome.Gene)
     */
    public java.util.Collection findByGene( final ubic.gemma.model.genome.Gene gene ) {
        try {
            return this.handleFindByGene( gene );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.association.Gene2GOAssociationDao.findByGene(ubic.gemma.model.genome.Gene gene)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#findByGoTerm(java.lang.String,
     *      ubic.gemma.model.genome.Taxon)
     */
    public java.util.Collection findByGoTerm( final java.lang.String goId, final ubic.gemma.model.genome.Taxon taxon ) {
        try {
            return this.handleFindByGoTerm( goId, taxon );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.association.Gene2GOAssociationDao.findByGoTerm(java.lang.String goId, ubic.gemma.model.genome.Taxon taxon)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#findByGOTerm(java.util.Collection,
     *      ubic.gemma.model.genome.Taxon)
     */
    public java.util.Collection findByGOTerm( final java.util.Collection goTerms,
            final ubic.gemma.model.genome.Taxon taxon ) {
        try {
            return this.handleFindByGOTerm( goTerms, taxon );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.association.Gene2GOAssociationDao.findByGOTerm(java.util.Collection goTerms, ubic.gemma.model.genome.Taxon taxon)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#findOrCreate(int, java.lang.String,
     *      ubic.gemma.model.association.Gene2GOAssociation)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( gene2GOAssociation );
        argNames.add( "gene2GOAssociation" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of 'ubic.gemma.model.association.Gene2GOAssociation"
                                + "' was found when executing query --> '" + queryString + "'" );
            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        result = transformEntity( transform, ( ubic.gemma.model.association.Gene2GOAssociation ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#findOrCreate(int,
     *      ubic.gemma.model.association.Gene2GOAssociation)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform,
            final ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        return this
                .findOrCreate(
                        transform,
                        "from ubic.gemma.model.association.Gene2GOAssociation as gene2GOAssociation where gene2GOAssociation.gene2GOAssociation = :gene2GOAssociation",
                        gene2GOAssociation );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#findOrCreate(java.lang.String,
     *      ubic.gemma.model.association.Gene2GOAssociation)
     */
    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.association.Gene2GOAssociation findOrCreate( final java.lang.String queryString,
            final ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        return ( ubic.gemma.model.association.Gene2GOAssociation ) this.findOrCreate( TRANSFORM_NONE, queryString,
                gene2GOAssociation );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#findOrCreate(ubic.gemma.model.association.Gene2GOAssociation)
     */
    public ubic.gemma.model.association.Gene2GOAssociation findOrCreate(
            ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        return ( ubic.gemma.model.association.Gene2GOAssociation ) this.findOrCreate( TRANSFORM_NONE,
                gene2GOAssociation );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#load(int, java.lang.Long)
     */
    @Override
    public Object load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "Gene2GOAssociation.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get(
                ubic.gemma.model.association.Gene2GOAssociationImpl.class, id );
        return transformEntity( transform, ( ubic.gemma.model.association.Gene2GOAssociation ) entity );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#load(java.lang.Long)
     */
    @Override
    public ubic.gemma.model.association.Relationship load( java.lang.Long id ) {
        return ( ubic.gemma.model.association.Gene2GOAssociation ) this.load( TRANSFORM_NONE, id );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#loadAll()
     */
    @Override
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#loadAll(int)
     */
    @Override
    public java.util.Collection loadAll( final int transform ) {
        final java.util.Collection results = this.getHibernateTemplate().loadAll(
                ubic.gemma.model.association.Gene2GOAssociationImpl.class );
        this.transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#remove(java.lang.Long)
     */
    @Override
    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "Gene2GOAssociation.remove - 'id' can not be null" );
        }
        ubic.gemma.model.association.Gene2GOAssociation entity = ( ubic.gemma.model.association.Gene2GOAssociation ) this
                .load( id );
        if ( entity != null ) {
            this.remove( entity );
        }
    }

    /**
     * @see ubic.gemma.model.association.RelationshipDao#remove(java.util.Collection)
     */
    @Override
    public void remove( java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "Gene2GOAssociation.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#remove(ubic.gemma.model.association.Gene2GOAssociation)
     */
    public void remove( ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        if ( gene2GOAssociation == null ) {
            throw new IllegalArgumentException( "Gene2GOAssociation.remove - 'gene2GOAssociation' can not be null" );
        }
        this.getHibernateTemplate().delete( gene2GOAssociation );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#removeAll()
     */
    public void removeAll() {
        try {
            this.handleRemoveAll();
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.association.Gene2GOAssociationDao.removeAll()' --> " + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.association.RelationshipDao#update(java.util.Collection)
     */
    @Override
    public void update( final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "Gene2GOAssociation.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession( new org.springframework.orm.hibernate3.HibernateCallback() {
            public Object doInHibernate( org.hibernate.Session session ) throws org.hibernate.HibernateException {
                for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                    update( ( ubic.gemma.model.association.Gene2GOAssociation ) entityIterator.next() );
                }
                return null;
            }
        } );
    }

    /**
     * @see ubic.gemma.model.association.Gene2GOAssociationDao#update(ubic.gemma.model.association.Gene2GOAssociation)
     */
    public void update( ubic.gemma.model.association.Gene2GOAssociation gene2GOAssociation ) {
        if ( gene2GOAssociation == null ) {
            throw new IllegalArgumentException( "Gene2GOAssociation.update - 'gene2GOAssociation' can not be null" );
        }
        this.getHibernateTemplate().update( gene2GOAssociation );
    }

    /**
     * Performs the core logic for {@link #findAssociationByGene(ubic.gemma.model.genome.Gene)}
     */
    protected abstract java.util.Collection handleFindAssociationByGene( ubic.gemma.model.genome.Gene gene )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #findByGene(ubic.gemma.model.genome.Gene)}
     */
    protected abstract java.util.Collection handleFindByGene( ubic.gemma.model.genome.Gene gene )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #findByGoTerm(java.lang.String, ubic.gemma.model.genome.Taxon)}
     */
    protected abstract java.util.Collection handleFindByGoTerm( java.lang.String goId,
            ubic.gemma.model.genome.Taxon taxon ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #findByGOTerm(java.util.Collection, ubic.gemma.model.genome.Taxon)}
     */
    protected abstract java.util.Collection handleFindByGOTerm( java.util.Collection goTerms,
            ubic.gemma.model.genome.Taxon taxon ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #removeAll()}
     */
    protected abstract void handleRemoveAll() throws java.lang.Exception;

    /**
     * Transforms a collection of entities using the
     * {@link #transformEntity(int,ubic.gemma.model.association.Gene2GOAssociation)} method. This method does not
     * instantiate a new collection.
     * <p/>
     * This method is to be used internally only.
     * 
     * @param transform one of the constants declared in <code>ubic.gemma.model.association.Gene2GOAssociationDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.association.Gene2GOAssociation)
     */
    @Override
    protected void transformEntities( final int transform, final java.util.Collection entities ) {
        switch ( transform ) {
            case TRANSFORM_NONE: // fall-through
            default:
                // do nothing;
        }
    }

    /**
     * Allows transformation of entities into value objects (or something else for that matter), when the
     * <code>transform</code> flag is set to one of the constants defined in
     * <code>ubic.gemma.model.association.Gene2GOAssociationDao</code>, please note that the {@link #TRANSFORM_NONE}
     * constant denotes no transformation, so the entity itself will be returned. If the integer argument value is
     * unknown {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in {@link ubic.gemma.model.association.Gene2GOAssociationDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected Object transformEntity( final int transform, final ubic.gemma.model.association.Gene2GOAssociation entity ) {
        Object target = null;
        if ( entity != null ) {
            switch ( transform ) {
                case TRANSFORM_NONE: // fall-through
                default:
                    target = entity;
            }
        }
        return target;
    }

}