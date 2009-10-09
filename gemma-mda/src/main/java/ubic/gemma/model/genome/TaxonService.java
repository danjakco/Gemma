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
package ubic.gemma.model.genome;

/**
 * 
 */
public interface TaxonService {

    /**
     * 
     */
    public ubic.gemma.model.genome.Taxon find( ubic.gemma.model.genome.Taxon taxon );

    /**
     * 
     */
    public ubic.gemma.model.genome.Taxon findByCommonName( java.lang.String commonName );

    /**
     * 
     */
    public ubic.gemma.model.genome.Taxon findByAbbreviation( java.lang.String abbreviation );
    
    
    /**
     * 
     */
    public ubic.gemma.model.genome.Taxon findByScientificName( java.lang.String scientificName );

    /**
     * 
     */
    public ubic.gemma.model.genome.Taxon findOrCreate( ubic.gemma.model.genome.Taxon taxon );

    /**
     * 
     */
    public ubic.gemma.model.genome.Taxon load( java.lang.Long id );

    /**
     * 
     */
    public java.util.Collection loadAll();

    /**
     * 
     */
    public void remove( ubic.gemma.model.genome.Taxon taxon );

    /**
     * 
     */
    public void update( ubic.gemma.model.genome.Taxon taxon );

    /**
     * 
     */
    public java.util.Collection<Taxon> findChildTaxaByParent(Taxon parentTaxon);
    
}
