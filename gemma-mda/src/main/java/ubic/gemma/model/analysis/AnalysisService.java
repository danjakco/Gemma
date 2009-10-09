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
package ubic.gemma.model.analysis;

import java.util.Collection;

/**
 * <p>
 * Provides basic services for dealing with analysis
 * </p>
 */
public interface AnalysisService<T extends Analysis> {

    /**
     * 
     */
    public void delete( java.lang.Long idToDelete );

    /**
     * <p>
     * deletes the given analysis from the system
     * </p>
     */
    public void delete( T toDelete );

    /**
     * <p>
     * find all the analyses that involved the given investigation
     * </p>
     */
    public java.util.Collection findByInvestigation( ubic.gemma.model.analysis.Investigation investigation );

    /**
     * <p>
     * Given a collection of investigations returns a Map of Analysis --> collection of Investigations
     * </p>
     * <p>
     * The collection of investigations returned by the map will include all the investigations for the analysis key iff
     * one of the investigations for that analysis was in the given collection started with
     * </p>
     */
    public java.util.Map findByInvestigations( java.util.Collection investigations );

    /**
     */
    public Collection findByName( java.lang.String name );

    /**
     * 
     */
    public java.util.Collection<T> findByTaxon( ubic.gemma.model.genome.Taxon taxon );

    
    /**
     * 
     */
    public java.util.Collection<T> findByParentTaxon( ubic.gemma.model.genome.Taxon taxon );


    /**
     * <p>
     * An analysis is uniquely determined by its set of investigations. Only returns an analyis if the collection of
     * investigations given exacly matches other wise returns null
     * </p>
     */
    public T findByUniqueInvestigations( java.util.Collection investigations );

    /**
     * <p>
     * Returns the analysis with the specified ID
     * </p>
     */
    public T load( java.lang.Long id );

    /**
     * <p>
     * Returns all of the analysis objects
     * </p>
     */
    public java.util.Collection loadAll();

}
