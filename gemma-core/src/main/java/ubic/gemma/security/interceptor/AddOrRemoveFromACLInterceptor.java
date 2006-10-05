/*
 * The Gemma project
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
package ubic.gemma.security.interceptor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.acl.basic.AbstractBasicAclEntry;
import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.BasicAclExtendedDao;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;
import org.acegisecurity.acl.basic.SimpleAclEntry;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import ubic.gemma.model.association.Relationship;
import ubic.gemma.model.common.Securable;
import ubic.gemma.model.common.description.DatabaseEntry;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.bioAssayData.BioAssayDataVector;
import ubic.gemma.model.expression.designElement.DesignElement;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.biosequence.BioSequence;
import ubic.gemma.model.genome.gene.GeneAlias;
import ubic.gemma.model.genome.gene.GeneProduct;
import ubic.gemma.persistence.CrudUtils;
import ubic.gemma.security.principal.UserDetailsServiceImpl;
import ubic.gemma.util.ReflectionUtil;

/**
 * Adds security controls to newly created objects, and removes them for objects that are deleted. Methods in this
 * interceptor are run for all new objects (to add security if needed) and when objects are deleted.
 * <p>
 * Implementation Note: For permissions modification to be triggered, the method name must match certain patterns, which
 * include "create" and "remove". Other methods that would require changes to permissions will not work without
 * modifying the source code.
 * 
 * @author keshav
 * @author pavlidis
 * @version $Id$
 * @see ubic.gemma.security.interceptor.AclPointcut
 * @spring.bean name="aclAdvice"
 * @spring.property name="crudUtils" ref="crudUtils"
 * @spring.property name="basicAclExtendedDao" ref="basicAclExtendedDao"
 */
public class AddOrRemoveFromACLInterceptor implements AfterReturningAdvice {

    CrudUtils crudUtils;

    public AddOrRemoveFromACLInterceptor() {
        this.crudUtils = new CrudUtils();
    }

    /**
     * Objects are grouped in a hierarchy. A default 'parent' is defined in the database. This must match an entry in
     * the ACL_OBJECT_IDENTITY table. In Gemma this is added as part of database initialization (see mysql-acegy-acl.sql
     * for MySQL version)
     */
    private static final String DEFAULT_PARENT = "globalDummyParent";

    /**
     * @see DEFAULT_PARENT
     */
    private static final String DEFAULT_PARENT_ID = "1";

    private static Log log = LogFactory.getLog( AddOrRemoveFromACLInterceptor.class.getName() );

    /**
     * For some types of objects, we don't put permissions on them directly, but on the containing object. Example:
     * reporter - we secure the arrayDesign, but not the reporter.
     */
    private static final Collection<Class> unsecuredClasses = new HashSet<Class>();

    /*
     * Classes to skip because they aren't secured. Either these are always "public" objects, or they are secured by
     * composition. In principle this shouldn't needed in most cases because the methods for the corresponding services
     * are not interccepted anyway.
     */
    static {
        unsecuredClasses.add( BioAssayDataVector.class );
        unsecuredClasses.add( DatabaseEntry.class );
        unsecuredClasses.add( BioSequence.class );
        unsecuredClasses.add( Relationship.class );
        unsecuredClasses.add( DesignElement.class );
        unsecuredClasses.add( Taxon.class );
        unsecuredClasses.add( Gene.class );
        unsecuredClasses.add( GeneProduct.class );
        unsecuredClasses.add( GeneAlias.class );
        unsecuredClasses.add( QuantitationType.class );
    }

    private BasicAclExtendedDao basicAclExtendedDao;

    /**
     * Creates the acl_permission object and the acl_object_identity object.
     * 
     * @param method - method called to trigger this action
     * @param object - represents the domain object.
     * @param recipient
     * @param permission
     */
    public void addPermission( Object object ) {

        AbstractBasicAclEntry simpleAclEntry = getAclEntry( object );

        /* By default we assign the object to have the default global parent. */
        simpleAclEntry.setAclObjectParentIdentity( new NamedEntityObjectIdentity( DEFAULT_PARENT, DEFAULT_PARENT_ID ) );

        try {
            basicAclExtendedDao.create( simpleAclEntry );
            if ( log.isDebugEnabled() ) {
                log.debug( "Added permission " + getAuthority() + " for recipient "
                        + UserDetailsServiceImpl.getCurrentUsername() + " on " + object );
            }
        } catch ( DataIntegrityViolationException ignored ) {

            // This happens in two situations:
            // 1. When a 'findOrCreate' resulted in just a 'find'.
            // 2. When a create was called, but some associated object was already in the system.
            //              
            // Either way, we can ignore it.
            //              

            // if ( method.getName().equals( "findOrCreate" ) ) {
            // do nothing. This happens when the object already exists and has permissions assigned (for example,
            // findOrCreate resulted in a 'find')
            // } else {
            // something else must be wrong
            // log.fatal( method.getName() + " on " + getAuthority() + " for recipient " + getUsername() + " on " +
            // object, ignored );
            // throw ( ignored );
            // }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang.Object, java.lang.reflect.Method,
     *      java.lang.Object[], java.lang.Object)
     */
    @SuppressWarnings( { "unused", "unchecked" })
    public void afterReturning( Object retValue, Method m, Object[] args, Object target ) throws Throwable {

        assert args != null;
        assert args.length == 1;
        Object persistentObject = getPersistentObject( retValue, m, args );

        Session sess = crudUtils.getSessionFactory().openSession();

        try {
            Hibernate.initialize( persistentObject );
        } catch ( HibernateException e ) {
            // this can result in a second objet being created if the object is already in a session that has not been
            // flushed.
            persistentObject = sess.merge( persistentObject );
        }

        if ( persistentObject == null ) return;
        if ( Collection.class.isAssignableFrom( persistentObject.getClass() ) ) {
            for ( Object o : ( Collection<Object> ) persistentObject ) {
                if ( !Securable.class.isAssignableFrom( persistentObject.getClass() ) ) {
                    return; // they will all be the same type.
                }
                processObject( m, o );
            }
        } else { // note that check for securable is in the pointcut.
            processObject( m, persistentObject );
        }

        sess.close();
    }

    /**
     * Delete acl permissions for an object.
     * 
     * @param object
     * @throws IllegalArgumentException
     * @throws DataAccessException
     */
    public void deletePermission( Object object ) throws DataAccessException, IllegalArgumentException {
        try {
            basicAclExtendedDao.delete( makeObjectIdentity( object ) );
        } catch ( org.springframework.dao.DataRetrievalFailureException e ) {
            // this happens during tests where we have flushed during a transaction.
            log.warn( "Could not delete aclObjectIdentity for " + object );
        }
        if ( log.isDebugEnabled() ) {
            log.debug( "Deleted object " + object + " ACL permissions for recipient "
                    + UserDetailsServiceImpl.getCurrentUsername() );
        }
    }

    /**
     * @param basicAclExtendedDao
     */
    public void setBasicAclExtendedDao( BasicAclExtendedDao basicAclExtendedDao ) {
        this.basicAclExtendedDao = basicAclExtendedDao;
    }

    /**
     * @param retValue
     * @param m
     * @param args
     * @return
     */
    private Object getPersistentObject( Object retValue, Method m, Object[] args ) {
        if ( CrudUtils.methodIsDelete( m ) || CrudUtils.methodIsUpdate( m ) ) {
            return args[0];
        }
        // assert retValue != null : "Null return value from method " + m.getName();
        return retValue;
    }

    /**
     * @param m
     * @param object
     */
    private void processAssociations( Method m, Object object ) throws IllegalAccessException,
            InvocationTargetException {

        EntityPersister persister = crudUtils.getEntityPersister( object );
        if ( persister == null ) {
            // FIXME this happens when the object is a proxy.
            log.error( "No EntityPersister found for " + object.getClass().getName() );
            return;
        }
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();
        String[] propertyNames = persister.getPropertyNames();

        for ( int j = 0; j < propertyNames.length; j++ ) {
            CascadeStyle cs = cascadeStyles[j];
            if ( !crudUtils.needCascade( m, cs ) ) {
                // log.debug( "Not processing association " + propertyNames[j] + ", Cascade=" + cs );
                continue;
            }

            PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor( object.getClass(), propertyNames[j] );

            Object associatedObject = ReflectionUtil.getProperty( object, descriptor );

            if ( associatedObject == null ) continue;

            Class<?> propertyType = descriptor.getPropertyType();

            if ( Securable.class.isAssignableFrom( propertyType ) ) {

                if ( log.isDebugEnabled() ) log.debug( "Processing ACL for " + propertyNames[j] + ", Cascade=" + cs );
                processObject( m, associatedObject );
            } else if ( Collection.class.isAssignableFrom( propertyType ) ) {

                /*
                 * This block commented out because of lazy-load problems.
                 */
                // Collection associatedObjects = ( Collection ) associatedObject;
                // for ( Object object2 : associatedObjects ) {
                // if ( Securable.class.isAssignableFrom( object2.getClass() ) ) {
                // if ( log.isDebugEnabled() ) {
                // log.debug( "Processing ACL for member " + object2 + " of collection " + propertyNames[j]
                // + ", Cascade=" + cs );
                // }
                // processObject( m, object2 );
                // }
                // }
            }
        }
    }

    /**
     * @param m method that was called. This is used to determine what action to take.
     * @param object. If null, no action is taken.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void processObject( Method m, Object object ) throws IllegalAccessException, InvocationTargetException {

        if ( object == null ) return;

        assert m != null;

        if ( !Securable.class.isAssignableFrom( object.getClass() ) || unsecuredClassesContains( object.getClass() ) ) {
            if ( log.isDebugEnabled() ) {
                log.debug( object.getClass().getName() + " is not a secured object, skipping permissions processing." );
            }
            return;
        }

        // if ( log.isDebugEnabled() ) {
        // log.debug( "Processing permissions for: " + object.getClass().getName() + " for method " + m.getName() );
        // }
        if ( CrudUtils.methodIsCreate( m ) ) {
            addPermission( object );
            processAssociations( m, object );
        } else if ( CrudUtils.methodIsDelete( m ) ) {
            deletePermission( object );
            processAssociations( m, object );
        } else {
            // nothing to do.
        }
    }

    /**
     * @param class1
     * @return
     */
    private boolean unsecuredClassesContains( Class<? extends Object> c ) {
        for ( Class<? extends Object> clazz : unsecuredClasses ) {
            if ( clazz.isAssignableFrom( c ) ) return true;
        }
        return false;
    }

    /**
     * @param object
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static AbstractBasicAclEntry getAclEntry( Object object ) {
        SimpleAclEntry simpleAclEntry = new SimpleAclEntry();
        simpleAclEntry.setAclObjectIdentity( makeObjectIdentity( object ) );
        simpleAclEntry.setMask( getAuthority() );
        simpleAclEntry.setRecipient( UserDetailsServiceImpl.getCurrentUsername() );
        return simpleAclEntry;
    }

    /**
     * @param object
     */
    private static boolean checkValidPrimaryKey( Object object ) {
        Class clazz = object.getClass();
        try {
            String methodName = "getId";
            Method m = clazz.getMethod( methodName, new Class[] {} );
            Object result = m.invoke( object, new Object[] {} );
            if ( result == null ) {
                return false;
            }
        } catch ( NoSuchMethodException nsme ) {
            throw new IllegalArgumentException( "Object of class '" + clazz
                    + "' does not provide the required getId() method: " + object );
        } catch ( IllegalArgumentException e ) {
            throw new RuntimeException( e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        }
        return true;
    }

    /**
     * Forms the object identity to be inserted in acl_object_identity table.
     * 
     * @param object
     * @return object identity.
     */
    private static AclObjectIdentity makeObjectIdentity( Object object ) {
        assert checkValidPrimaryKey( object ) : "No valid primary key for object " + object;
        try {
            return new NamedEntityObjectIdentity( object );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * For the current principal (user), return the permissions mask. If the current principal has role "admin", they
     * are granted ADMINISTRATION authority. If they are role "user", they are granted READ_WRITE authority.
     * 
     * @return
     */
    protected static Integer getAuthority() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        GrantedAuthority[] ga = auth.getAuthorities();
        for ( int i = 0; i < ga.length; i++ ) {
            if ( ga[i].equals( "admin" ) ) {
                // if ( log.isDebugEnabled() ) log.debug( "Granting ADMINISTRATION privileges" );
                return SimpleAclEntry.ADMINISTRATION;
            }
        }
        // if ( log.isDebugEnabled() ) log.debug( "Granting READ_WRITE privileges" );
        return SimpleAclEntry.READ_WRITE;
    }

    /**
     * @param crudUtils the crudUtils to set
     */
    public void setCrudUtils( CrudUtils crudUtils ) {
        this.crudUtils = crudUtils;
    }

}
