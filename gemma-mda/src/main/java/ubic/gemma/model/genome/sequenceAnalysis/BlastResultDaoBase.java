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
package ubic.gemma.model.genome.sequenceAnalysis;

/**
 * <p>
 * Base Spring DAO Class: is able to create, update, remove, load, and find objects of type
 * <code>ubic.gemma.model.genome.sequenceAnalysis.BlastResult</code>.
 * </p>
 * 
 * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResult
 */
public abstract class BlastResultDaoBase extends
        ubic.gemma.model.genome.sequenceAnalysis.SequenceSimilaritySearchResultDaoImpl implements
        ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao {

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#create(int, java.util.Collection)
     */
    public java.util.Collection create( final int transform, final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "BlastResult.create - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            create( transform, ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) entityIterator
                                    .next() );
                        }
                        return null;
                    }
                } );
        return entities;
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#create(int transform,
     *      ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    public Object create( final int transform, final ubic.gemma.model.genome.sequenceAnalysis.BlastResult blastResult ) {
        if ( blastResult == null ) {
            throw new IllegalArgumentException( "BlastResult.create - 'blastResult' can not be null" );
        }
        this.getHibernateTemplate().save( blastResult );
        return this.transformEntity( transform, blastResult );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#create(java.util.Collection)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection create( final java.util.Collection entities ) {
        return create( TRANSFORM_NONE, entities );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#create(ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    public ubic.gemma.model.genome.sequenceAnalysis.SequenceSimilaritySearchResult create(
            ubic.gemma.model.genome.sequenceAnalysis.BlastResult blastResult ) {
        return ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) this.create( TRANSFORM_NONE, blastResult );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#find(int, java.lang.String,
     *      ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    @SuppressWarnings( { "unchecked" })
    public Object find( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.genome.sequenceAnalysis.BlastResult toFind ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( toFind );
        argNames.add( "toFind" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of 'ubic.gemma.model.genome.sequenceAnalysis.BlastResult"
                                + "' was found when executing query --> '" + queryString + "'" );
            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        result = transformEntity( transform, ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#find(int,
     *      ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    @SuppressWarnings( { "unchecked" })
    public Object find( final int transform, final ubic.gemma.model.genome.sequenceAnalysis.BlastResult toFind ) {
        return this
                .find(
                        transform,
                        "from ubic.gemma.model.genome.sequenceAnalysis.BlastResult as blastResult where blastResult.toFind = :toFind",
                        toFind );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#find(java.lang.String,
     *      ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.genome.sequenceAnalysis.BlastResult find( final java.lang.String queryString,
            final ubic.gemma.model.genome.sequenceAnalysis.BlastResult toFind ) {
        return ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) this.find( TRANSFORM_NONE, queryString, toFind );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#find(ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    public ubic.gemma.model.genome.sequenceAnalysis.BlastResult find(
            ubic.gemma.model.genome.sequenceAnalysis.BlastResult toFind ) {
        return ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) this.find( TRANSFORM_NONE, toFind );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#findOrCreate(int, java.lang.String,
     *      ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.genome.sequenceAnalysis.BlastResult toFindOrCreate ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( toFindOrCreate );
        argNames.add( "toFindOrCreate" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of 'ubic.gemma.model.genome.sequenceAnalysis.BlastResult"
                                + "' was found when executing query --> '" + queryString + "'" );
            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        result = transformEntity( transform, ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#findOrCreate(int,
     *      ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform,
            final ubic.gemma.model.genome.sequenceAnalysis.BlastResult toFindOrCreate ) {
        return this
                .findOrCreate(
                        transform,
                        "from ubic.gemma.model.genome.sequenceAnalysis.BlastResult as blastResult where blastResult.toFindOrCreate = :toFindOrCreate",
                        toFindOrCreate );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#findOrCreate(java.lang.String,
     *      ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.genome.sequenceAnalysis.BlastResult findOrCreate( final java.lang.String queryString,
            final ubic.gemma.model.genome.sequenceAnalysis.BlastResult toFindOrCreate ) {
        return ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) this.findOrCreate( TRANSFORM_NONE, queryString,
                toFindOrCreate );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#findOrCreate(ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    public ubic.gemma.model.genome.sequenceAnalysis.BlastResult findOrCreate(
            ubic.gemma.model.genome.sequenceAnalysis.BlastResult toFindOrCreate ) {
        return ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) this.findOrCreate( TRANSFORM_NONE,
                toFindOrCreate );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#load(int, java.lang.Long)
     */
    @Override
    public Object load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "BlastResult.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get(
                ubic.gemma.model.genome.sequenceAnalysis.BlastResultImpl.class, id );
        return transformEntity( transform, ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) entity );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#load(java.lang.Long)
     */
    @Override
    public ubic.gemma.model.genome.sequenceAnalysis.SequenceSimilaritySearchResult load( java.lang.Long id ) {
        return ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) this.load( TRANSFORM_NONE, id );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#loadAll()
     */
    @Override
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#loadAll(int)
     */
    @Override
    public java.util.Collection loadAll( final int transform ) {
        final java.util.Collection results = this.getHibernateTemplate().loadAll(
                ubic.gemma.model.genome.sequenceAnalysis.BlastResultImpl.class );
        this.transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#remove(java.lang.Long)
     */
    @Override
    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "BlastResult.remove - 'id' can not be null" );
        }
        ubic.gemma.model.genome.sequenceAnalysis.BlastResult entity = ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) this
                .load( id );
        if ( entity != null ) {
            this.remove( entity );
        }
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.SequenceSimilaritySearchResultDao#remove(java.util.Collection)
     */
    @Override
    public void remove( java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "BlastResult.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#remove(ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    public void remove( ubic.gemma.model.genome.sequenceAnalysis.BlastResult blastResult ) {
        if ( blastResult == null ) {
            throw new IllegalArgumentException( "BlastResult.remove - 'blastResult' can not be null" );
        }
        this.getHibernateTemplate().delete( blastResult );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.SequenceSimilaritySearchResultDao#update(java.util.Collection)
     */
    @Override
    public void update( final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "BlastResult.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            update( ( ubic.gemma.model.genome.sequenceAnalysis.BlastResult ) entityIterator.next() );
                        }
                        return null;
                    }
                } );
    }

    /**
     * @see ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao#update(ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
     */
    public void update( ubic.gemma.model.genome.sequenceAnalysis.BlastResult blastResult ) {
        if ( blastResult == null ) {
            throw new IllegalArgumentException( "BlastResult.update - 'blastResult' can not be null" );
        }
        this.getHibernateTemplate().update( blastResult );
    }

    /**
     * Transforms a collection of entities using the
     * {@link #transformEntity(int,ubic.gemma.model.genome.sequenceAnalysis.BlastResult)} method. This method does not
     * instantiate a new collection.
     * <p/>
     * This method is to be used internally only.
     * 
     * @param transform one of the constants declared in
     *        <code>ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.genome.sequenceAnalysis.BlastResult)
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
     * <code>ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao</code>, please note that the
     * {@link #TRANSFORM_NONE} constant denotes no transformation, so the entity itself will be returned. If the integer
     * argument value is unknown {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in {@link ubic.gemma.model.genome.sequenceAnalysis.BlastResultDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected Object transformEntity( final int transform,
            final ubic.gemma.model.genome.sequenceAnalysis.BlastResult entity ) {
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