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
 * <code>ubic.gemma.model.expression.bioAssayData.DesignElementDataVector</code>.
 * </p>
 * 
 * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVector
 */
public abstract class DesignElementDataVectorDaoBase extends ubic.gemma.model.expression.bioAssayData.DataVectorDaoImpl
        implements ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao {

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#countAll()
     */
    public java.lang.Integer countAll() {
        try {
            return this.handleCountAll();
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao.countAll()' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#create(int, java.util.Collection)
     */
    public java.util.Collection<DesignElementDataVector> create( final int transform,
            final java.util.Collection<DesignElementDataVector> entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "DesignElementDataVector.create - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator<DesignElementDataVector> entityIterator = entities.iterator(); entityIterator
                                .hasNext(); ) {
                            create( transform, entityIterator.next() );
                        }
                        return null;
                    }
                } );
        return entities;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#create(int transform,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    public Object create( final int transform,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        if ( designElementDataVector == null ) {
            throw new IllegalArgumentException(
                    "DesignElementDataVector.create - 'designElementDataVector' can not be null" );
        }
        this.getHibernateTemplate().save( designElementDataVector );
        return this.transformEntity( transform, designElementDataVector );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#create(java.util.Collection)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection create( final java.util.Collection entities ) {
        return create( TRANSFORM_NONE, entities );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#create(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    public ubic.gemma.model.expression.bioAssayData.DataVector create(
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) this.create( TRANSFORM_NONE,
                designElementDataVector );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(int, java.lang.String,
     *      java.util.Collection)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection find( final int transform, final java.lang.String queryString,
            final java.util.Collection quantitationTypes ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( quantitationTypes );
        argNames.add( "quantitationTypes" );
        java.util.List results = this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() );
        transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(int, java.lang.String,
     *      ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection find( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( quantitationType );
        argNames.add( "quantitationType" );
        java.util.List results = this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() );
        transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(int, java.lang.String,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign,
     *      ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection find( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign,
            final ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( arrayDesign );
        argNames.add( "arrayDesign" );
        args.add( quantitationType );
        argNames.add( "quantitationType" );
        java.util.List results = this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() );
        transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(int, java.lang.String,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    @SuppressWarnings( { "unchecked" })
    public Object find( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( designElementDataVector );
        argNames.add( "designElementDataVector" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.expression.bioAssayData.DesignElementDataVector"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform,
                ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(int, java.util.Collection)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection find( final int transform, final java.util.Collection quantitationTypes ) {
        return this
                .find(
                        transform,
                        "from DesignElementDataVectorImpl d fetch all properties where d.quantitationType in (:quantitationTypes)",
                        quantitationTypes );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(int,
     *      ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection find( final int transform,
            final ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) {
        return this.find( transform,
                "from DesignElementDataVectorImpl d fetch all properties where d.quantitationType=:quantitationType",
                quantitationType );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(int,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign,
     *      ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection find( final int transform,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign,
            final ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) {
        return this
                .find(
                        transform,
                        "select dedv from DesignElementDataVectorImpl dedv inner join dedv.designElement de inner join dedv.quantitationType qtype where de.arrayDesign = :arrayDesign and qtype = :quantitationType ",
                        arrayDesign, quantitationType );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(int,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    @SuppressWarnings( { "unchecked" })
    public Object find( final int transform,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        return this
                .find(
                        transform,
                        "from ubic.gemma.model.expression.bioAssayData.DesignElementDataVector as designElementDataVector where designElementDataVector.designElementDataVector = :designElementDataVector",
                        designElementDataVector );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(java.lang.String,
     *      java.util.Collection)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection find( final java.lang.String queryString, final java.util.Collection quantitationTypes ) {
        return this.find( TRANSFORM_NONE, queryString, quantitationTypes );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(java.lang.String,
     *      ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection find( final java.lang.String queryString,
            final ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) {
        return this.find( TRANSFORM_NONE, queryString, quantitationType );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(java.lang.String,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign,
     *      ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection find( final java.lang.String queryString,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign,
            final ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) {
        return this.find( TRANSFORM_NONE, queryString, arrayDesign, quantitationType );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(java.lang.String,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.expression.bioAssayData.DesignElementDataVector find( final java.lang.String queryString,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) this.find( TRANSFORM_NONE,
                queryString, designElementDataVector );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(java.util.Collection)
     */
    public java.util.Collection find( java.util.Collection quantitationTypes ) {
        return this.find( TRANSFORM_NONE, quantitationTypes );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    public java.util.Collection find( ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) {
        return this.find( TRANSFORM_NONE, quantitationType );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(ubic.gemma.model.expression.arrayDesign.ArrayDesign,
     *      ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    public java.util.Collection find( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) {
        return this.find( TRANSFORM_NONE, arrayDesign, quantitationType );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#find(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    public ubic.gemma.model.expression.bioAssayData.DesignElementDataVector find(
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) this.find( TRANSFORM_NONE,
                designElementDataVector );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#findOrCreate(int, java.lang.String,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( designElementDataVector );
        argNames.add( "designElementDataVector" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.expression.bioAssayData.DesignElementDataVector"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform,
                ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#findOrCreate(int,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        return this
                .findOrCreate(
                        transform,
                        "from ubic.gemma.model.expression.bioAssayData.DesignElementDataVector as designElementDataVector where designElementDataVector.designElementDataVector = :designElementDataVector",
                        designElementDataVector );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#findOrCreate(java.lang.String,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    @SuppressWarnings( { "unchecked" })
    public ubic.gemma.model.expression.bioAssayData.DesignElementDataVector findOrCreate(
            final java.lang.String queryString,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) this.findOrCreate( TRANSFORM_NONE,
                queryString, designElementDataVector );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#findOrCreate(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    public ubic.gemma.model.expression.bioAssayData.DesignElementDataVector findOrCreate(
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) this.findOrCreate( TRANSFORM_NONE,
                designElementDataVector );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#getPreferredVectors(java.util.Collection,
     *      java.util.Collection)
     */
    public java.util.Map getPreferredVectors( final java.util.Collection ees, final java.util.Collection genes ) {
        try {
            return this.handleGetPreferredVectors( ees, genes );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao.getPreferredVectors(java.util.Collection ees, java.util.Collection genes)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#load(int, java.lang.Long)
     */
    @Override
    public Object load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "DesignElementDataVector.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get(
                ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorImpl.class, id );
        return transformEntity( transform, ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) entity );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#load(java.lang.Long)
     */
    @Override
    public ubic.gemma.model.expression.bioAssayData.DataVector load( java.lang.Long id ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) this.load( TRANSFORM_NONE, id );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#loadAll()
     */
    @Override
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#loadAll(int)
     */
    @Override
    public java.util.Collection<DesignElementDataVector> loadAll( final int transform ) {
        final java.util.Collection<DesignElementDataVector> results = this.getHibernateTemplate().loadAll(
                ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorImpl.class );
        this.transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#remove(java.lang.Long)
     */
    @Override
    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "DesignElementDataVector.remove - 'id' can not be null" );
        }
        ubic.gemma.model.expression.bioAssayData.DesignElementDataVector entity = ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) this
                .load( id );
        if ( entity != null ) {
            this.remove( entity );
        }
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#remove(java.util.Collection)
     */
    @Override
    public void remove( java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "DesignElementDataVector.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#remove(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    public void remove( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        if ( designElementDataVector == null ) {
            throw new IllegalArgumentException(
                    "DesignElementDataVector.remove - 'designElementDataVector' can not be null" );
        }
        this.getHibernateTemplate().delete( designElementDataVector );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#removeDataForCompositeSequence(ubic.gemma.model.expression.designElement.CompositeSequence)
     */
    public void removeDataForCompositeSequence(
            final ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence ) {
        try {
            this.handleRemoveDataForCompositeSequence( compositeSequence );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao.removeDataForCompositeSequence(ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#removeDataForQuantitationType(ubic.gemma.model.common.quantitationtype.QuantitationType)
     */
    public void removeDataForQuantitationType(
            final ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) {
        try {
            this.handleRemoveDataForQuantitationType( quantitationType );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao.removeDataForQuantitationType(ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#thaw(java.util.Collection)
     */
    public void thaw( final java.util.Collection designElementDataVectors ) {
        try {
            this.handleThaw( designElementDataVectors );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao.thaw(java.util.Collection designElementDataVectors)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#thaw(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    public void thaw( final ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        try {
            this.handleThaw( designElementDataVector );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao.thaw(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DataVectorDao#update(java.util.Collection)
     */
    @Override
    public void update( final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "DesignElementDataVector.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            update( ( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector ) entityIterator
                                    .next() );
                        }
                        return null;
                    }
                } );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao#update(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
     */
    public void update( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector ) {
        if ( designElementDataVector == null ) {
            throw new IllegalArgumentException(
                    "DesignElementDataVector.update - 'designElementDataVector' can not be null" );
        }
        this.getHibernateTemplate().update( designElementDataVector );
    }

    /**
     * Performs the core logic for {@link #countAll()}
     */
    protected abstract java.lang.Integer handleCountAll() throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #getPreferredVectors(java.util.Collection, java.util.Collection)}
     */
    protected abstract java.util.Map handleGetPreferredVectors( java.util.Collection ees, java.util.Collection genes )
            throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #removeDataForCompositeSequence(ubic.gemma.model.expression.designElement.CompositeSequence)}
     */
    protected abstract void handleRemoveDataForCompositeSequence(
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence ) throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #removeDataForQuantitationType(ubic.gemma.model.common.quantitationtype.QuantitationType)}
     */
    protected abstract void handleRemoveDataForQuantitationType(
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #thaw(java.util.Collection)}
     */
    protected abstract void handleThaw( java.util.Collection designElementDataVectors ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #thaw(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)}
     */
    protected abstract void handleThaw(
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector )
            throws java.lang.Exception;

    /**
     * Transforms a collection of entities using the
     * {@link #transformEntity(int,ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)} method. This
     * method does not instantiate a new collection.
     * <p/>
     * This method is to be used internally only.
     * 
     * @param transform one of the constants declared in
     *        <code>ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)
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
     * <code>ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao</code>, please note that the
     * {@link #TRANSFORM_NONE} constant denotes no transformation, so the entity itself will be returned. If the integer
     * argument value is unknown {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in
     *        {@link ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected Object transformEntity( final int transform,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDataVector entity ) {
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