/*
 * The Gemma project
 * 
 * Copyright (c) 2006 Columbia University
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
package ubic.gemma.web.controller.coexpressionSearch;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/**
 * @author luke
 */
public class CoexpressionSearchCommand {

    private Collection<Long> geneIds;

    private Collection<Long> eeIds;

    private Long eeSetId;

    private String eeSetName;

    private Integer stringency;

    private boolean queryGenesOnly;

    /*
     * we're storing the actual ee ids in the command object; the query string is only here so we can use this object to
     * store the state of the search form between visits...
     */
    private String eeQuery;

    /*
     * as eeQuery above, the taxon is only here so we can use this object to store the entire state of the form...
     */
    private Long taxonId;

    public Collection<Long> getGeneIds() {
        return geneIds;
    }

    public void setGeneIds( Collection<Long> geneIds ) {
        this.geneIds = geneIds;
    }

    public Collection<Long> getEeIds() {
        return eeIds;
    }

    public void setEeIds( Collection<Long> eeIds ) {
        this.eeIds = eeIds;
    }

    public Integer getStringency() {
        return stringency;
    }

    public void setStringency( Integer stringency ) {
        this.stringency = stringency;
    }

    public String getEeQuery() {
        return eeQuery;
    }

    public void setEeQuery( String eeQuery ) {
        this.eeQuery = eeQuery;
    }

    public Long getTaxonId() {
        return taxonId;
    }

    public void setTaxonId( Long taxonId ) {
        this.taxonId = taxonId;
    }

    public boolean getQueryGenesOnly() {
        return queryGenesOnly;
    }

    public void setQueryGenesOnly( boolean queryGenesOnly ) {
        this.queryGenesOnly = queryGenesOnly;
    }

    @Override
    public String toString() {
        return "GeneIds=" + StringUtils.join( getGeneIds(), "," ) + " Analysis=" + this.getEeSetId()
                + " QueryGenesOnly=" + this.getQueryGenesOnly() + " taxon=" + getTaxonId() + " eeQuery=" + getEeQuery()
                + " Stringency=" + stringency + " ees=" + StringUtils.join( getEeIds(), "," );
    }

    public Long getEeSetId() {
        return eeSetId;
    }

    public void setEeSetId( Long eeSetId ) {
        this.eeSetId = eeSetId;
    }

    public String getEeSetName() {
        return eeSetName;
    }

    public void setEeSetName( String eeSetName ) {
        this.eeSetName = eeSetName;
    }

}
