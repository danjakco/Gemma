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
//
// Attention: Generated code! Do not modify by hand!
// Generated by: SpringDao.vsl in andromda-spring-cartridge.
//
package ubic.gemma.model.expression.bioAssayData;

/**
 * @see ubic.gemma.model.expression.bioAssayData.DesignElementDataVector
 */
public interface DesignElementDataVectorDao extends ubic.gemma.model.expression.bioAssayData.DataVectorDao {
    /**
     * Loads an instance of ubic.gemma.model.expression.bioAssayData.DesignElementDataVector from the persistent store.
     */
    public ubic.gemma.model.expression.bioAssayData.DataVector load( java.lang.Long id );

    /**
     * <p>
     * Does the same thing as {@link #load(java.lang.Long)} with an additional flag called <code>transform</code>. If
     * this flag is set to <code>TRANSFORM_NONE</code> then the returned entity will <strong>NOT</strong> be
     * transformed. If this flag is any of the other constants defined in this class then the result <strong>WILL BE</strong>
     * passed through an operation which can optionally transform the entity (into a value object for example). By
     * default, transformation does not occur.
     * </p>
     * 
     * @param id the identifier of the entity to load.
     * @return either the entity or the object transformed from the entity.
     */
    public Object load( int transform, java.lang.Long id );

    /**
     * Loads all entities of type {@link ubic.gemma.model.expression.bioAssayData.DesignElementDataVector}.
     * 
     * @return the loaded entities.
     */
    public java.util.Collection loadAll();

    /**
     * <p>
     * Does the same thing as {@link #loadAll()} with an additional flag called <code>transform</code>. If this flag
     * is set to <code>TRANSFORM_NONE</code> then the returned entity will <strong>NOT</strong> be transformed. If
     * this flag is any of the other constants defined here then the result <strong>WILL BE</strong> passed through an
     * operation which can optionally transform the entity (into a value object for example). By default, transformation
     * does not occur.
     * </p>
     * 
     * @param transform the flag indicating what transformation to use.
     * @return the loaded entities.
     */
    public java.util.Collection loadAll( final int transform );

    /**
     * Creates an instance of ubic.gemma.model.expression.bioAssayData.DesignElementDataVector and adds it to the
     * persistent store.
     */
    public ubic.gemma.model.expression.bioAssayData.DataVector create(
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * <p>
     * Does the same thing as {@link #create(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)} with an
     * additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then the
     * returned entity will <strong>NOT</strong> be transformed. If this flag is any of the other constants defined
     * here then the result <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entity (into a value object for example). By default, transformation does not occur.
     * </p>
     */
    public Object create( int transform,
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * Creates a new instance of ubic.gemma.model.expression.bioAssayData.DesignElementDataVector and adds from the
     * passed in <code>entities</code> collection
     * 
     * @param entities the collection of ubic.gemma.model.expression.bioAssayData.DesignElementDataVector instances to
     *        create.
     * @return the created instances.
     */
    public java.util.Collection create( java.util.Collection entities );

    /**
     * <p>
     * Does the same thing as {@link #create(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)} with an
     * additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then the
     * returned entity will <strong>NOT</strong> be transformed. If this flag is any of the other constants defined
     * here then the result <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection create( int transform, java.util.Collection entities );

    /**
     * Updates the <code>designElementDataVector</code> instance in the persistent store.
     */
    public void update( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * Updates all instances in the <code>entities</code> collection in the persistent store.
     */
    public void update( java.util.Collection entities );

    /**
     * Removes the instance of ubic.gemma.model.expression.bioAssayData.DesignElementDataVector from the persistent
     * store.
     */
    public void remove( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * Removes the instance of ubic.gemma.model.expression.bioAssayData.DesignElementDataVector having the given
     * <code>identifier</code> from the persistent store.
     */
    public void remove( java.lang.Long id );

    /**
     * Removes all entities in the given <code>entities<code> collection.
     */
    public void remove( java.util.Collection entities );

    /**
     * 
     */
    public ubic.gemma.model.expression.bioAssayData.DesignElementDataVector findOrCreate(
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * <p>
     * Does the same thing as {@link #findOrCreate(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)}
     * with an additional argument called <code>queryString</code>. This <code>queryString</code> argument allows
     * you to override the query string defined in
     * {@link #findOrCreate(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)}.
     * </p>
     */
    public ubic.gemma.model.expression.bioAssayData.DesignElementDataVector findOrCreate( String queryString,
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * <p>
     * Does the same thing as {@link #findOrCreate(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)}
     * with an additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code>
     * then finder results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other
     * constants defined here then finder results <strong>WILL BE</strong> passed through an operation which can
     * optionally transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public Object findOrCreate( int transform,
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * <p>
     * Does the same thing as
     * {@link #findOrCreate(boolean, ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in
     * {@link #findOrCreate(int, ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector)}.
     * </p>
     */
    public Object findOrCreate( int transform, String queryString,
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * 
     */
    public ubic.gemma.model.expression.bioAssayData.DesignElementDataVector find(
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * <p>
     * Does the same thing as {@link #find(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in
     * {@link #find(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)}.
     * </p>
     */
    public ubic.gemma.model.expression.bioAssayData.DesignElementDataVector find( String queryString,
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * <p>
     * Does the same thing as {@link #find(ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)} with an
     * additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then
     * finder results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other
     * constants defined here then finder results <strong>WILL BE</strong> passed through an operation which can
     * optionally transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public Object find( int transform,
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * <p>
     * Does the same thing as {@link #find(boolean, ubic.gemma.model.expression.bioAssayData.DesignElementDataVector)}
     * with an additional argument called <code>queryString</code>. This <code>queryString</code> argument allows
     * you to override the query string defined in
     * {@link #find(int, ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector)}.
     * </p>
     */
    public Object find( int transform, String queryString,
            ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * 
     */
    public java.util.Collection find( java.util.Collection quantitationTypes );

    /**
     * <p>
     * Does the same thing as {@link #find(java.util.Collection)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string
     * defined in {@link #find(java.util.Collection)}.
     * </p>
     */
    public java.util.Collection find( String queryString, java.util.Collection quantitationTypes );

    /**
     * <p>
     * Does the same thing as {@link #find(java.util.Collection)} with an additional flag called <code>transform</code>.
     * If this flag is set to <code>TRANSFORM_NONE</code> then finder results will <strong>NOT</strong> be
     * transformed during retrieval. If this flag is any of the other constants defined here then finder results
     * <strong>WILL BE</strong> passed through an operation which can optionally transform the entities (into value
     * objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection find( int transform, java.util.Collection quantitationTypes );

    /**
     * <p>
     * Does the same thing as {@link #find(boolean, java.util.Collection)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string
     * defined in {@link #find(int, java.util.Collection quantitationTypes)}.
     * </p>
     */
    public java.util.Collection find( int transform, String queryString, java.util.Collection quantitationTypes );

    /**
     * 
     */
    public java.util.Collection find( ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * <p>
     * Does the same thing as {@link #find(ubic.gemma.model.common.quantitationtype.QuantitationType)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #find(ubic.gemma.model.common.quantitationtype.QuantitationType)}.
     * </p>
     */
    public java.util.Collection find( String queryString,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * <p>
     * Does the same thing as {@link #find(ubic.gemma.model.common.quantitationtype.QuantitationType)} with an
     * additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then
     * finder results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other
     * constants defined here then finder results <strong>WILL BE</strong> passed through an operation which can
     * optionally transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection find( int transform,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * <p>
     * Does the same thing as {@link #find(boolean, ubic.gemma.model.common.quantitationtype.QuantitationType)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in
     * {@link #find(int, ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType)}.
     * </p>
     */
    public java.util.Collection find( int transform, String queryString,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * 
     */
    public java.util.Collection find( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * <p>
     * Does the same thing as
     * {@link #find(ubic.gemma.model.expression.arrayDesign.ArrayDesign, ubic.gemma.model.common.quantitationtype.QuantitationType)}
     * with an additional argument called <code>queryString</code>. This <code>queryString</code> argument allows
     * you to override the query string defined in
     * {@link #find(ubic.gemma.model.expression.arrayDesign.ArrayDesign, ubic.gemma.model.common.quantitationtype.QuantitationType)}.
     * </p>
     */
    public java.util.Collection find( String queryString,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * <p>
     * Does the same thing as
     * {@link #find(ubic.gemma.model.expression.arrayDesign.ArrayDesign, ubic.gemma.model.common.quantitationtype.QuantitationType)}
     * with an additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code>
     * then finder results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other
     * constants defined here then finder results <strong>WILL BE</strong> passed through an operation which can
     * optionally transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection find( int transform, ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * <p>
     * Does the same thing as
     * {@link #find(boolean, ubic.gemma.model.expression.arrayDesign.ArrayDesign, ubic.gemma.model.common.quantitationtype.QuantitationType)}
     * with an additional argument called <code>queryString</code>. This <code>queryString</code> argument allows
     * you to override the query string defined in
     * {@link #find(int, ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign, ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType)}.
     * </p>
     */
    public java.util.Collection find( int transform, String queryString,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * <p>
     * Thaws associations of the given DesignElementDataVector
     * </p>
     */
    public void thaw( ubic.gemma.model.expression.bioAssayData.DesignElementDataVector designElementDataVector );

    /**
     * 
     */
    public void thaw( java.util.Collection designElementDataVectors );

    /**
     * 
     */
    public java.lang.Integer countAll();

    /**
     * <p>
     * remove Design Element Data Vectors and Probe2ProbeCoexpression entries for a specified CompositeSequence.
     * </p>
     */
    public void removeDataForCompositeSequence(
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * <p>
     * Removes the DesignElementDataVectors and Probe2ProbeCoexpressions for a quantitation type, given a
     * QuantitationType (which always comes from a specific ExpressionExperiment)
     * </p>
     */
    public void removeDataForQuantitationType(
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * <p>
     * Given a collection of genes, a collection of expression experiments will return a collection of "preferred"
     * design element data vectors for the given genes in the given experiments. Note that these vectors should be
     * 'masked' for missing values before typical uses.
     * </p>
     */
    public java.util.Map getPreferredVectors( java.util.Collection ees, java.util.Collection genes );

}
