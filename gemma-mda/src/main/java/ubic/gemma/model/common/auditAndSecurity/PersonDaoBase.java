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
package ubic.gemma.model.common.auditAndSecurity;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>
 * Base Spring DAO Class: is able to create, update, remove, load, and find objects of type
 * <code>ubic.gemma.model.common.auditAndSecurity.Person</code>.
 * </p>
 * 
 * @see ubic.gemma.model.common.auditAndSecurity.Person
 */
public abstract class PersonDaoBase extends HibernateDaoSupport implements
        ubic.gemma.model.common.auditAndSecurity.PersonDao {

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#create(int, java.util.Collection)
     */

    public java.util.Collection create( final int transform, final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "Person.create - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            create( transform, ( ubic.gemma.model.common.auditAndSecurity.Person ) entityIterator
                                    .next() );
                        }
                        return null;
                    }
                } );
        return entities;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#create(int transform,
     *      ubic.gemma.model.common.auditAndSecurity.Person)
     */
    public Object create( final int transform, final ubic.gemma.model.common.auditAndSecurity.Person person ) {
        if ( person == null ) {
            throw new IllegalArgumentException( "Person.create - 'person' can not be null" );
        }
        this.getHibernateTemplate().save( person );
        return this.transformEntity( transform, person );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#create(java.util.Collection)
     */

    @SuppressWarnings( { "unchecked" })
    public java.util.Collection create( final java.util.Collection entities ) {
        return create( TRANSFORM_NONE, entities );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#create(ubic.gemma.model.common.auditAndSecurity.Person)
     */
    public Person create( ubic.gemma.model.common.auditAndSecurity.Person person ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Person ) this.create( TRANSFORM_NONE, person );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#find(int, java.lang.String,
     *      ubic.gemma.model.common.auditAndSecurity.Contact)
     */

    @SuppressWarnings( { "unchecked" })
    public Object find( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.common.auditAndSecurity.Contact contact ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( contact );
        argNames.add( "contact" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of 'ubic.gemma.model.common.auditAndSecurity.Contact"
                                + "' was found when executing query --> '" + queryString + "'" );
            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        result = transformEntity( transform, ( ubic.gemma.model.common.auditAndSecurity.Person ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#find(int, java.lang.String,
     *      ubic.gemma.model.common.auditAndSecurity.Person)
     */
    @SuppressWarnings( { "unchecked" })
    public Object find( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.common.auditAndSecurity.Person person ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( person );
        argNames.add( "person" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of 'ubic.gemma.model.common.auditAndSecurity.Person"
                                + "' was found when executing query --> '" + queryString + "'" );
            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        result = transformEntity( transform, ( ubic.gemma.model.common.auditAndSecurity.Person ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#find(int,
     *      ubic.gemma.model.common.auditAndSecurity.Contact)
     */

    @SuppressWarnings( { "unchecked" })
    public Object find( final int transform, final ubic.gemma.model.common.auditAndSecurity.Contact contact ) {
        return this.find( transform,
                "from ubic.gemma.model.common.auditAndSecurity.Person as person where person.contact = :contact",
                contact );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#find(int,
     *      ubic.gemma.model.common.auditAndSecurity.Person)
     */
    @SuppressWarnings( { "unchecked" })
    public Object find( final int transform, final ubic.gemma.model.common.auditAndSecurity.Person person ) {
        return this.find( transform,
                "from ubic.gemma.model.common.auditAndSecurity.Person as person where person.person = :person", person );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#find(java.lang.String,
     *      ubic.gemma.model.common.auditAndSecurity.Contact)
     */

    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.common.auditAndSecurity.Contact find( final java.lang.String queryString,
            final ubic.gemma.model.common.auditAndSecurity.Contact contact ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Contact ) this.find( TRANSFORM_NONE, queryString, contact );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#find(java.lang.String,
     *      ubic.gemma.model.common.auditAndSecurity.Person)
     */
    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.common.auditAndSecurity.Person find( final java.lang.String queryString,
            final ubic.gemma.model.common.auditAndSecurity.Person person ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Person ) this.find( TRANSFORM_NONE, queryString, person );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#find(ubic.gemma.model.common.auditAndSecurity.Contact)
     */

    public ubic.gemma.model.common.auditAndSecurity.Contact find(
            ubic.gemma.model.common.auditAndSecurity.Contact contact ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Contact ) this.find( TRANSFORM_NONE, contact );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#find(ubic.gemma.model.common.auditAndSecurity.Person)
     */
    public ubic.gemma.model.common.auditAndSecurity.Person find( ubic.gemma.model.common.auditAndSecurity.Person person ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Person ) this.find( TRANSFORM_NONE, person );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByEmail(int, java.lang.String)
     */

    @SuppressWarnings( { "unchecked" })
    public Object findByEmail( final int transform, final java.lang.String email ) {
        return this.findByEmail( transform, "from ContactImpl c where c.email = :email", email );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByEmail(int, java.lang.String, java.lang.String)
     */

    @SuppressWarnings( { "unchecked" })
    public Object findByEmail( final int transform, final java.lang.String queryString, final java.lang.String email ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( email );
        argNames.add( "email" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of 'ubic.gemma.model.common.auditAndSecurity.Contact"
                                + "' was found when executing query --> '" + queryString + "'" );
            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        result = transformEntity( transform, ( ubic.gemma.model.common.auditAndSecurity.Person ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByEmail(java.lang.String)
     */

    public ubic.gemma.model.common.auditAndSecurity.Contact findByEmail( java.lang.String email ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Contact ) this.findByEmail( TRANSFORM_NONE, email );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByEmail(java.lang.String, java.lang.String)
     */

    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.common.auditAndSecurity.Contact findByEmail( final java.lang.String queryString,
            final java.lang.String email ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Contact ) this.findByEmail( TRANSFORM_NONE, queryString,
                email );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByFirstAndLastName(int, java.lang.String,
     *      java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByFirstAndLastName( final int transform, final java.lang.String name,
            final java.lang.String secondName ) {
        return this
                .findByFirstAndLastName(
                        transform,
                        "from ubic.gemma.model.common.auditAndSecurity.Person as person where person.name = :name and person.secondName = :secondName",
                        name, secondName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByFirstAndLastName(int, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByFirstAndLastName( final int transform, final java.lang.String queryString,
            final java.lang.String name, final java.lang.String secondName ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( name );
        argNames.add( "name" );
        args.add( secondName );
        argNames.add( "secondName" );
        java.util.List results = this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() );
        transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByFirstAndLastName(java.lang.String,
     *      java.lang.String)
     */
    public java.util.Collection findByFirstAndLastName( java.lang.String name, java.lang.String secondName ) {
        return this.findByFirstAndLastName( TRANSFORM_NONE, name, secondName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByFirstAndLastName(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByFirstAndLastName( final java.lang.String queryString,
            final java.lang.String name, final java.lang.String secondName ) {
        return this.findByFirstAndLastName( TRANSFORM_NONE, queryString, name, secondName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByFullName(int, java.lang.String, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByFullName( final int transform, final java.lang.String name,
            final java.lang.String secondName ) {
        return this.findByFullName( transform,
                "from PersonImpl p where p.firstName=:firstName and p.lastName=:lastName and p.middleName=:middleName",
                name, secondName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByFullName(int, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByFullName( final int transform, final java.lang.String queryString,
            final java.lang.String name, final java.lang.String secondName ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( name );
        argNames.add( "name" );
        args.add( secondName );
        argNames.add( "secondName" );
        java.util.List results = this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() );
        transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByFullName(java.lang.String, java.lang.String)
     */
    public java.util.Collection findByFullName( java.lang.String name, java.lang.String secondName ) {
        return this.findByFullName( TRANSFORM_NONE, name, secondName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByFullName(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByFullName( final java.lang.String queryString, final java.lang.String name,
            final java.lang.String secondName ) {
        return this.findByFullName( TRANSFORM_NONE, queryString, name, secondName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByLastName(int, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByLastName( final int transform, final java.lang.String lastName ) {
        return this.findByLastName( transform,
                "from ubic.gemma.model.common.auditAndSecurity.Person as person where person.lastName = :lastName",
                lastName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByLastName(int, java.lang.String, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByLastName( final int transform, final java.lang.String queryString,
            final java.lang.String lastName ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( lastName );
        argNames.add( "lastName" );
        java.util.List results = this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() );
        transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByLastName(java.lang.String)
     */
    public java.util.Collection findByLastName( java.lang.String lastName ) {
        return this.findByLastName( TRANSFORM_NONE, lastName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findByLastName(java.lang.String, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByLastName( final java.lang.String queryString, final java.lang.String lastName ) {
        return this.findByLastName( TRANSFORM_NONE, queryString, lastName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findOrCreate(int, java.lang.String,
     *      ubic.gemma.model.common.auditAndSecurity.Contact)
     */

    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.common.auditAndSecurity.Contact contact ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( contact );
        argNames.add( "contact" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of 'ubic.gemma.model.common.auditAndSecurity.Contact"
                                + "' was found when executing query --> '" + queryString + "'" );
            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        result = transformEntity( transform, ( ubic.gemma.model.common.auditAndSecurity.Person ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findOrCreate(int, java.lang.String,
     *      ubic.gemma.model.common.auditAndSecurity.Person)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.common.auditAndSecurity.Person person ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( person );
        argNames.add( "person" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of 'ubic.gemma.model.common.auditAndSecurity.Person"
                                + "' was found when executing query --> '" + queryString + "'" );
            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        result = transformEntity( transform, ( ubic.gemma.model.common.auditAndSecurity.Person ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findOrCreate(int,
     *      ubic.gemma.model.common.auditAndSecurity.Contact)
     */

    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform, final ubic.gemma.model.common.auditAndSecurity.Contact contact ) {
        return this.findOrCreate( transform,
                "from ubic.gemma.model.common.auditAndSecurity.Person as person where person.contact = :contact",
                contact );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findOrCreate(int,
     *      ubic.gemma.model.common.auditAndSecurity.Person)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform, final ubic.gemma.model.common.auditAndSecurity.Person person ) {
        return this.findOrCreate( transform,
                "from ubic.gemma.model.common.auditAndSecurity.Person as person where person.person = :person", person );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findOrCreate(java.lang.String,
     *      ubic.gemma.model.common.auditAndSecurity.Contact)
     */

    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.common.auditAndSecurity.Contact findOrCreate( final java.lang.String queryString,
            final ubic.gemma.model.common.auditAndSecurity.Contact contact ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Contact ) this.findOrCreate( TRANSFORM_NONE, queryString,
                contact );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findOrCreate(java.lang.String,
     *      ubic.gemma.model.common.auditAndSecurity.Person)
     */
    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.common.auditAndSecurity.Person findOrCreate( final java.lang.String queryString,
            final ubic.gemma.model.common.auditAndSecurity.Person person ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Person ) this.findOrCreate( TRANSFORM_NONE, queryString,
                person );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findOrCreate(ubic.gemma.model.common.auditAndSecurity.Contact)
     */

    public ubic.gemma.model.common.auditAndSecurity.Contact findOrCreate(
            ubic.gemma.model.common.auditAndSecurity.Contact contact ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Contact ) this.findOrCreate( TRANSFORM_NONE, contact );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#findOrCreate(ubic.gemma.model.common.auditAndSecurity.Person)
     */
    public ubic.gemma.model.common.auditAndSecurity.Person findOrCreate(
            ubic.gemma.model.common.auditAndSecurity.Person person ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Person ) this.findOrCreate( TRANSFORM_NONE, person );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#load(int, java.lang.Long)
     */

    public Object load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "Person.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get(
                ubic.gemma.model.common.auditAndSecurity.PersonImpl.class, id );
        return transformEntity( transform, ( ubic.gemma.model.common.auditAndSecurity.Person ) entity );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#load(java.lang.Long)
     */

    public Person load( java.lang.Long id ) {
        return ( ubic.gemma.model.common.auditAndSecurity.Person ) this.load( TRANSFORM_NONE, id );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#loadAll()
     */

    @SuppressWarnings( { "unchecked" })
    public java.util.Collection loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#loadAll(int)
     */

    public java.util.Collection loadAll( final int transform ) {
        final java.util.Collection results = this.getHibernateTemplate().loadAll(
                ubic.gemma.model.common.auditAndSecurity.PersonImpl.class );
        this.transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#remove(java.lang.Long)
     */

    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "Person.remove - 'id' can not be null" );
        }
        ubic.gemma.model.common.auditAndSecurity.Person entity = ( ubic.gemma.model.common.auditAndSecurity.Person ) this
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
            throw new IllegalArgumentException( "Person.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#remove(ubic.gemma.model.common.auditAndSecurity.Person)
     */
    public void remove( ubic.gemma.model.common.auditAndSecurity.Person person ) {
        if ( person == null ) {
            throw new IllegalArgumentException( "Person.remove - 'person' can not be null" );
        }
        this.getHibernateTemplate().delete( person );
    }

    /**
     * @see ubic.gemma.model.common.SecurableDao#update(java.util.Collection)
     */

    public void update( final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "Person.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            update( ( ubic.gemma.model.common.auditAndSecurity.Person ) entityIterator.next() );
                        }
                        return null;
                    }
                } );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.PersonDao#update(ubic.gemma.model.common.auditAndSecurity.Person)
     */
    public void update( ubic.gemma.model.common.auditAndSecurity.Person person ) {
        if ( person == null ) {
            throw new IllegalArgumentException( "Person.update - 'person' can not be null" );
        }
        this.getHibernateTemplate().update( person );
    }

    /**
     * Transforms a collection of entities using the
     * {@link #transformEntity(int,ubic.gemma.model.common.auditAndSecurity.Person)} method. This method does not
     * instantiate a new collection.
     * <p/>
     * This method is to be used internally only.
     * 
     * @param transform one of the constants declared in <code>ubic.gemma.model.common.auditAndSecurity.PersonDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.common.auditAndSecurity.Person)
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
     * <code>ubic.gemma.model.common.auditAndSecurity.PersonDao</code>, please note that the {@link #TRANSFORM_NONE}
     * constant denotes no transformation, so the entity itself will be returned. If the integer argument value is
     * unknown {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in {@link ubic.gemma.model.common.auditAndSecurity.PersonDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected Object transformEntity( final int transform, final ubic.gemma.model.common.auditAndSecurity.Person entity ) {
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