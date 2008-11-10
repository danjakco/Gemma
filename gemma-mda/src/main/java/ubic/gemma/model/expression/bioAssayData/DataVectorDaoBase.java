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
package ubic.gemma.model.expression.bioAssayData;

/**
 * <p>
 * Base Spring DAO Class: is able to create, update, remove, load, and find objects of type
 * <code>ubic.gemma.model.expression.bioAssayData.DataVector</code>.
 * </p>
 * 
 * @see ubic.gemma.model.expression.bioAssayData.DataVector
 */
public abstract class DataVectorDaoBase extends org.springframework.orm.hibernate3.support.HibernateDaoSupport
        implements ubic.gemma.model.expression.bioAssayData.DataVectorDao {

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#load(int, java.lang.Long)
     */
    public Object load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "DataVector.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get(
                ubic.gemma.model.expression.bioAssayData.DataVectorImpl.class, id );
        return transformEntity( transform, ( ubic.gemma.model.expression.bioAssayData.DataVector ) entity );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#load(java.lang.Long)
     */
    public ubic.gemma.model.expression.bioAssayData.DataVector load( java.lang.Long id ) {
        return ( ubic.gemma.model.expression.bioAssayData.DataVector ) this.load( TRANSFORM_NONE, id );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#loadAll()
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#loadAll(int)
     */
    public java.util.Collection loadAll( final int transform ) {
        final java.util.Collection results = this.getHibernateTemplate().loadAll(
                ubic.gemma.model.expression.bioAssayData.DataVectorImpl.class );
        this.transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#remove(java.lang.Long)
     */
    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "DataVector.remove - 'id' can not be null" );
        }
        ubic.gemma.model.expression.bioAssayData.DataVector entity = this.load( id );
        if ( entity != null ) {
            this.remove( entity );
        }
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#remove(java.util.Collection)
     */
    public void remove( java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "DataVector.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#remove(ubic.gemma.model.expression.bioAssayData.DataVector)
     */
    public void remove( ubic.gemma.model.expression.bioAssayData.DataVector dataVector ) {
        if ( dataVector == null ) {
            throw new IllegalArgumentException( "DataVector.remove - 'dataVector' can not be null" );
        }
        this.getHibernateTemplate().delete( dataVector );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#update(java.util.Collection)
     */
    public void update( final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "DataVector.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            update( ( ubic.gemma.model.expression.bioAssayData.DataVector ) entityIterator.next() );
                        }
                        return null;
                    }
                } );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#update(ubic.gemma.model.expression.bioAssayData.DataVector)
     */
    public void update( ubic.gemma.model.expression.bioAssayData.DataVector dataVector ) {
        if ( dataVector == null ) {
            throw new IllegalArgumentException( "DataVector.update - 'dataVector' can not be null" );
        }
        this.getHibernateTemplate().update( dataVector );
    }

    /**
     * Transforms a collection of entities using the
     * {@link #transformEntity(int,ubic.gemma.model.expression.bioAssayData.DataVector)} method. This method does not
     * instantiate a new collection.
     * <p/>
     * This method is to be used internally only.
     * 
     * @param transform one of the constants declared in
     *        <code>ubic.gemma.model.expression.bioAssayData.DataVectorDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.expression.bioAssayData.DataVector)
     */
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
     * <code>ubic.gemma.model.expression.bioAssayData.DataVectorDao</code>, please note that the {@link #TRANSFORM_NONE}
     * constant denotes no transformation, so the entity itself will be returned. If the integer argument value is
     * unknown {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in {@link ubic.gemma.model.expression.bioAssayData.DataVectorDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected Object transformEntity( final int transform,
            final ubic.gemma.model.expression.bioAssayData.DataVector entity ) {
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