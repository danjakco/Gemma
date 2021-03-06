/*
 * The Gemma project
 *
 * Copyright (c) 2007 University of British Columbia
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
package ubic.gemma.model.common.search;

import org.apache.commons.lang3.StringUtils;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.genome.Taxon;

/**
 * @author paul
 */
public class SearchSettingsImpl extends SearchSettings {

    private static final long serialVersionUID = -8856730658411678433L;
    private boolean doHighlighting = false;

    public SearchSettingsImpl() {
    }

    /**
     * NOTE the query is trim()'ed, no need to do that later.
     *
     * @param query query
     */
    @SuppressWarnings({ "unused", "WeakerAccess" }) // Possible external use
    public SearchSettingsImpl( String query ) {
        this.setQuery( query.trim() );
    }

    /**
     * Convenience method to get pre-configured settings.
     *
     * @param  query query
     * @return       search settings
     */
    public static SearchSettings arrayDesignSearch( String query ) {
        SearchSettingsImpl s = new SearchSettingsImpl( query );
        s.noSearches();
        s.setSearchPlatforms( true );
        return s;
    }

    /**
     * Convenience method to get pre-configured settings.
     *
     * @param  query query
     * @return       search settings
     */
    public static SearchSettings bibliographicReferenceSearch( String query ) {
        SearchSettings s = new SearchSettingsImpl( query );
        s.noSearches();
        s.setSearchBibrefs( true );
        return s;
    }

    /**
     * Convenience method to get pre-configured settings.
     *
     * @param  query       query
     * @param  arrayDesign the array design to limit the search to
     * @return             search settings
     */
    public static SearchSettings compositeSequenceSearch( String query, ArrayDesign arrayDesign ) {
        SearchSettings s = new SearchSettingsImpl( query );
        s.noSearches();
        s.setSearchProbes( true );
        s.setPlatformConstraint( arrayDesign );
        return s;
    }

    /**
     * Convenience method to get pre-configured settings.
     *
     * @param  query query
     * @return       search settings
     */
    public static SearchSettings expressionExperimentSearch( String query ) {
        SearchSettingsImpl s = new SearchSettingsImpl( query );
        s.setSearchGenes( false );
        s.noSearches();
        s.setSearchExperiments( true );
        return s;
    }

    /**
     * Convenience method to get pre-configured settings.
     *
     * @param  query query
     * @param taxon if you want to filter by taxon (can be null)
     * @return       search settings
     */
    public static SearchSettings expressionExperimentSearch( String query, Taxon taxon ) {
        SearchSettingsImpl s = new SearchSettingsImpl( query );
        s.setSearchGenes( false );
        s.noSearches();
        s.setSearchExperiments( true );
        s.setTaxon( taxon );
        return s;
    }

    /**
     * Convenience method to get pre-configured settings.
     *
     * @param  query query
     * @param  taxon the taxon to limit the search to (can be null)
     * @return       search settings
     */
    public static SearchSettings geneSearch( String query, Taxon taxon ) {
        SearchSettings s = new SearchSettingsImpl( query );
        s.noSearches();
        s.setSearchGenes( true );
        s.setTaxon( taxon );
        return s;
    }

    public boolean getDoHighlighting() {
        return this.doHighlighting;
    }

    /**
     * Set to false to reduce overhead when highlighting isn't needed.
     *
     * @param doHighlighting do highlighting
     */
    public void setDoHighlighting( boolean doHighlighting ) {
        this.doHighlighting = doHighlighting;
    }

    /**
     * Reset all search criteria to false.
     */
    @Override
    public void noSearches() {
        this.setSearchPlatforms( false );
        this.setSearchBibrefs( false );
        this.setSearchBioSequences( false );
        this.setSearchGenes( false );
        this.setUseGo( false );
        this.setSearchPhenotypes( false );
        this.setSearchExperiments( false );
        this.setSearchProbes( false );
        this.setSearchGeneSets( false );
        this.setSearchExperimentSets( false );
    }

    @Override
    public String toString() {
        String tax = this.getTaxon() != null ? " [" + this.getTaxon().getCommonName() + "]" : "";
        if ( !StringUtils.isBlank( this.getTermUri() ) ) {
            return this.getTermUri() + tax;
        }
        return this.getQuery() + tax;
    }

}
