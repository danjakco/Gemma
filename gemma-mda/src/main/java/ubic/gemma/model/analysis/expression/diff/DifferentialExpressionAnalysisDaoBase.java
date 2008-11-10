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
package ubic.gemma.model.analysis.expression.diff;

/**
 * <p>
 * Base Spring DAO Class: is able to create, update, remove, load, and find objects of type
 * <code>ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis</code>.
 * </p>
 * 
 * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis
 */
public abstract class DifferentialExpressionAnalysisDaoBase extends
        ubic.gemma.model.analysis.expression.ExpressionAnalysisDaoImpl<DifferentialExpressionAnalysis> implements
        ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao {

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#load(int, java.lang.Long)
     */

    public DifferentialExpressionAnalysis load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "DifferentialExpressionAnalysis.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get(
                ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisImpl.class, id );
        return transformEntity( transform,
                ( ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis ) entity );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#load(java.lang.Long)
     */

    public DifferentialExpressionAnalysis load( java.lang.Long id ) {
        return ( ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis ) this.load( TRANSFORM_NONE,
                id );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#loadAll()
     */

    @SuppressWarnings( { "unchecked" })
    public java.util.Collection loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#loadAll(int)
     */

    public java.util.Collection loadAll( final int transform ) {
        final java.util.Collection results = this.getHibernateTemplate().loadAll(
                ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisImpl.class );
        this.transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#create(ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis)
     */
    public DifferentialExpressionAnalysis create(
            ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis differentialExpressionAnalysis ) {
        return ( ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis ) this.create(
                TRANSFORM_NONE, differentialExpressionAnalysis );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#create(int transform,
     *      ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis)
     */
    public Object create(
            final int transform,
            final ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis differentialExpressionAnalysis ) {
        if ( differentialExpressionAnalysis == null ) {
            throw new IllegalArgumentException(
                    "DifferentialExpressionAnalysis.create - 'differentialExpressionAnalysis' can not be null" );
        }
        this.getHibernateTemplate().save( differentialExpressionAnalysis );
        return this.transformEntity( transform, differentialExpressionAnalysis );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#create(java.util.Collection)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection create( final java.util.Collection entities ) {
        return create( TRANSFORM_NONE, entities );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#create(int,
     *      java.util.Collection)
     */
    public java.util.Collection create( final int transform, final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "DifferentialExpressionAnalysis.create - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            create(
                                    transform,
                                    ( ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis ) entityIterator
                                            .next() );
                        }
                        return null;
                    }
                } );
        return entities;
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#update(ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis)
     */
    public void update(
            ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis differentialExpressionAnalysis ) {
        if ( differentialExpressionAnalysis == null ) {
            throw new IllegalArgumentException(
                    "DifferentialExpressionAnalysis.update - 'differentialExpressionAnalysis' can not be null" );
        }
        this.getHibernateTemplate().update( differentialExpressionAnalysis );
    }

    /**
     * @see ubic.gemma.model.common.SecurableDao#update(java.util.Collection)
     */

    public void update( final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "DifferentialExpressionAnalysis.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            update( ( ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis ) entityIterator
                                    .next() );
                        }
                        return null;
                    }
                } );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#remove(ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis)
     */
    public void remove(
            ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis differentialExpressionAnalysis ) {
        if ( differentialExpressionAnalysis == null ) {
            throw new IllegalArgumentException(
                    "DifferentialExpressionAnalysis.remove - 'differentialExpressionAnalysis' can not be null" );
        }
        this.getHibernateTemplate().delete( differentialExpressionAnalysis );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#remove(java.lang.Long)
     */

    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "DifferentialExpressionAnalysis.remove - 'id' can not be null" );
        }
        ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis entity = ( ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis ) this
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
            throw new IllegalArgumentException( "DifferentialExpressionAnalysis.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#findByName(java.lang.String)
     */

    public java.util.Collection findByName( java.lang.String name ) {
        return this.findByName( TRANSFORM_NONE, name );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#findByName(java.lang.String,
     *      java.lang.String)
     */

    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByName( final java.lang.String queryString, final java.lang.String name ) {
        return this.findByName( TRANSFORM_NONE, queryString, name );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#findByName(int,
     *      java.lang.String)
     */

    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByName( final int transform, final java.lang.String name ) {
        return this.findByName( transform, "select a from AnalysisImpl as a where a.name like :name", name );
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#findByName(int,
     *      java.lang.String, java.lang.String)
     */

    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findByName( final int transform, final java.lang.String queryString,
            final java.lang.String name ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( name );
        argNames.add( "name" );
        java.util.List results = this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() );
        transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#thaw(java.util.Collection)
     */
    public void thaw( final java.util.Collection expressionAnalyses ) {
        try {
            this.handleThaw( expressionAnalyses );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao.thaw(java.util.Collection expressionAnalyses)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #thaw(java.util.Collection)}
     */
    protected abstract void handleThaw( java.util.Collection expressionAnalyses ) throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#findExperimentsWithAnalyses(ubic.gemma.model.genome.Gene)
     */
    public java.util.Collection findExperimentsWithAnalyses( final ubic.gemma.model.genome.Gene gene ) {
        try {
            return this.handleFindExperimentsWithAnalyses( gene );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao.findExperimentsWithAnalyses(ubic.gemma.model.genome.Gene gene)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #findExperimentsWithAnalyses(ubic.gemma.model.genome.Gene)}
     */
    protected abstract java.util.Collection handleFindExperimentsWithAnalyses( ubic.gemma.model.genome.Gene gene )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#thaw(ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis)
     */
    public void thaw(
            final ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis differentialExpressionAnalysis ) {
        try {
            this.handleThaw( differentialExpressionAnalysis );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao.thaw(ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis differentialExpressionAnalysis)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for
     * {@link #thaw(ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis)}
     */
    protected abstract void handleThaw(
            ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis differentialExpressionAnalysis )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#find(ubic.gemma.model.genome.Gene,
     *      ubic.gemma.model.analysis.expression.ExpressionAnalysisResultSet, double)
     */
    public java.util.Collection find( final ubic.gemma.model.genome.Gene gene,
            final ubic.gemma.model.analysis.expression.ExpressionAnalysisResultSet resultSet, final double threshold ) {
        try {
            return this.handleFind( gene, resultSet, threshold );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao.find(ubic.gemma.model.genome.Gene gene, ubic.gemma.model.analysis.expression.ExpressionAnalysisResultSet resultSet, double threshold)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for
     * {@link #find(ubic.gemma.model.genome.Gene, ubic.gemma.model.analysis.expression.ExpressionAnalysisResultSet, double)}
     */
    protected abstract java.util.Collection handleFind( ubic.gemma.model.genome.Gene gene,
            ubic.gemma.model.analysis.expression.ExpressionAnalysisResultSet resultSet, double threshold )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#getResultSets(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    public java.util.Collection getResultSets(
            final ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment ) {
        try {
            return this.handleGetResultSets( expressionExperiment );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao.getResultSets(ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #getResultSets(ubic.gemma.model.expression.experiment.ExpressionExperiment)}
     */
    protected abstract java.util.Collection handleGetResultSets(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao#findByInvestigationIds(java.util.Collection)
     */
    public java.util.Map findByInvestigationIds( final java.util.Collection investigationIds ) {
        try {
            return this.handleFindByInvestigationIds( investigationIds );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao.findByInvestigationIds(java.util.Collection investigationIds)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #findByInvestigationIds(java.util.Collection)}
     */
    protected abstract java.util.Map handleFindByInvestigationIds( java.util.Collection investigationIds )
            throws java.lang.Exception;

    /**
     * Allows transformation of entities into value objects (or something else for that matter), when the
     * <code>transform</code> flag is set to one of the constants defined in
     * <code>ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao</code>, please note that the
     * {@link #TRANSFORM_NONE} constant denotes no transformation, so the entity itself will be returned. If the integer
     * argument value is unknown {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in
     *        {@link ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected DifferentialExpressionAnalysis transformEntity( final int transform,
            final ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis entity ) {
        Object target = null;
        if ( entity != null ) {
            switch ( transform ) {
                case TRANSFORM_NONE: // fall-through
                default:
                    target = entity;
            }
        }
        return ( DifferentialExpressionAnalysis ) target;
    }

    /**
     * Transforms a collection of entities using the
     * {@link #transformEntity(int,ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis)} method.
     * This method does not instantiate a new collection.
     * <p/>
     * This method is to be used internally only.
     * 
     * @param transform one of the constants declared in
     *        <code>ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis)
     */

    protected void transformEntities( final int transform, final java.util.Collection entities ) {
        switch ( transform ) {
            case TRANSFORM_NONE: // fall-through
            default:
                // do nothing;
        }
    }

}