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
package ubic.gemma.model.common.protocol;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>
 * Base Spring DAO Class: is able to create, update, remove, load, and find objects of type
 * <code>ubic.gemma.model.common.protocol.HardwareApplication</code>.
 * </p>
 * 
 * @see ubic.gemma.model.common.protocol.HardwareApplication
 */
public abstract class HardwareApplicationDaoBase extends HibernateDaoSupport implements
        ubic.gemma.model.common.protocol.HardwareApplicationDao {

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#create(int, java.util.Collection)
     */

    public java.util.Collection create( final int transform, final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "HardwareApplication.create - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            create( transform, ( ubic.gemma.model.common.protocol.HardwareApplication ) entityIterator
                                    .next() );
                        }
                        return null;
                    }
                } );
        return entities;
    }

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#create(int transform,
     *      ubic.gemma.model.common.protocol.HardwareApplication)
     */
    public Object create( final int transform,
            final ubic.gemma.model.common.protocol.HardwareApplication hardwareApplication ) {
        if ( hardwareApplication == null ) {
            throw new IllegalArgumentException( "HardwareApplication.create - 'hardwareApplication' can not be null" );
        }
        this.getHibernateTemplate().save( hardwareApplication );
        return this.transformEntity( transform, hardwareApplication );
    }

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#create(java.util.Collection)
     */

    @SuppressWarnings( { "unchecked" })
    public java.util.Collection create( final java.util.Collection entities ) {
        return create( TRANSFORM_NONE, entities );
    }

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#create(ubic.gemma.model.common.protocol.HardwareApplication)
     */
    public HardwareApplication create( ubic.gemma.model.common.protocol.HardwareApplication hardwareApplication ) {
        return ( ubic.gemma.model.common.protocol.HardwareApplication ) this.create( TRANSFORM_NONE,
                hardwareApplication );
    }

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#load(int, java.lang.Long)
     */

    public Object load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "HardwareApplication.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get(
                ubic.gemma.model.common.protocol.HardwareApplicationImpl.class, id );
        return transformEntity( transform, ( ubic.gemma.model.common.protocol.HardwareApplication ) entity );
    }

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#load(java.lang.Long)
     */

    public HardwareApplication load( java.lang.Long id ) {
        return ( ubic.gemma.model.common.protocol.HardwareApplication ) this.load( TRANSFORM_NONE, id );
    }

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#loadAll()
     */

    @SuppressWarnings( { "unchecked" })
    public java.util.Collection loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#loadAll(int)
     */

    public java.util.Collection loadAll( final int transform ) {
        final java.util.Collection results = this.getHibernateTemplate().loadAll(
                ubic.gemma.model.common.protocol.HardwareApplicationImpl.class );
        this.transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#remove(java.lang.Long)
     */

    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "HardwareApplication.remove - 'id' can not be null" );
        }
        ubic.gemma.model.common.protocol.HardwareApplication entity = ( ubic.gemma.model.common.protocol.HardwareApplication ) this
                .load( id );
        if ( entity != null ) {
            this.remove( entity );
        }
    }

    /**
     * @see ubic.gemma.model.common.SecurableDao#remove(java.util.Collection)
     */

    public void remove( java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "HardwareApplication.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#remove(ubic.gemma.model.common.protocol.HardwareApplication)
     */
    public void remove( ubic.gemma.model.common.protocol.HardwareApplication hardwareApplication ) {
        if ( hardwareApplication == null ) {
            throw new IllegalArgumentException( "HardwareApplication.remove - 'hardwareApplication' can not be null" );
        }
        this.getHibernateTemplate().delete( hardwareApplication );
    }

    /**
     * @see ubic.gemma.model.common.SecurableDao#update(java.util.Collection)
     */

    public void update( final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "HardwareApplication.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            update( ( ubic.gemma.model.common.protocol.HardwareApplication ) entityIterator.next() );
                        }
                        return null;
                    }
                } );
    }

    /**
     * @see ubic.gemma.model.common.protocol.HardwareApplicationDao#update(ubic.gemma.model.common.protocol.HardwareApplication)
     */
    public void update( ubic.gemma.model.common.protocol.HardwareApplication hardwareApplication ) {
        if ( hardwareApplication == null ) {
            throw new IllegalArgumentException( "HardwareApplication.update - 'hardwareApplication' can not be null" );
        }
        this.getHibernateTemplate().update( hardwareApplication );
    }

    /**
     * Transforms a collection of entities using the
     * {@link #transformEntity(int,ubic.gemma.model.common.protocol.HardwareApplication)} method. This method does not
     * instantiate a new collection.
     * <p/>
     * This method is to be used internally only.
     * 
     * @param transform one of the constants declared in
     *        <code>ubic.gemma.model.common.protocol.HardwareApplicationDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.common.protocol.HardwareApplication)
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
     * <code>ubic.gemma.model.common.protocol.HardwareApplicationDao</code>, please note that the
     * {@link #TRANSFORM_NONE} constant denotes no transformation, so the entity itself will be returned. If the integer
     * argument value is unknown {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in {@link ubic.gemma.model.common.protocol.HardwareApplicationDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected Object transformEntity( final int transform,
            final ubic.gemma.model.common.protocol.HardwareApplication entity ) {
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