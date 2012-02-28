/*

 * The Gemma project
 * 
 * Copyright (c) 2010 University of British Columbia
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
package ubic.gemma.genome.gene;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;


/**
 * Represents a Gene group gene set.
 * 
 * @author kelsey
 * @version $Id$
 */
public class GeneSetValueObject implements Serializable {

    private static final long serialVersionUID = 6212231006289412683L;

    private boolean currentUserHasWritePermission = false;
    private boolean currentUserIsOwner = false;
    private String description;
    private Collection<Long> geneIds = new HashSet<Long>();
    private Long id;
    private String name;
    private boolean publik;
    private boolean shared;
    private Integer size;
    private String taxonName;
    private Long taxonId;
    
    /**
     * default constructor to satisfy java bean contract
     */
    public GeneSetValueObject() {
        super();
    }
  
    public String getTaxonName() {
        return this.taxonName;
    }    

    public void setTaxonName( String taxonName ) {
        this.taxonName = taxonName;        
    }

    public Long getTaxonId() {
        return this.taxonId;
    }    

    public void setTaxonId( Long taxonId ) {
        this.taxonId = taxonId;        
    }
    
    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return
     */
    public Collection<Long> getGeneIds() {
        return geneIds;
    }

    /**
     * @return
     */
    public Long getId() {
        return id;
    }
        

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * returns the number of members in the group
     * 
     * @return
     */
    public Integer getSize() {
        return this.size;
    }

    /**
     * @return the currentUserHasWritePermission
     */
    public boolean isCurrentUserHasWritePermission() {
        return currentUserHasWritePermission;
    }

    public boolean isPublik() {
        return this.publik;
    }

    public boolean isShared() {
        return this.shared;
    }

    /**
     * @param currentUserHasWritePermission the currentUserHasWritePermission to set
     */
    public void setCurrentUserHasWritePermission( boolean currentUserHasWritePermission ) {
        this.currentUserHasWritePermission = currentUserHasWritePermission;
    }

    /**
     * @param description
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * @param geneMembers
     */
    public void setGeneIds( Collection<Long> geneMembers ) {
        this.geneIds = geneMembers;
    }

    /**
     * @param id
     */
    public void setId( Long id ) {
        this.id = id;
    }

    /**
     * @param name
     */
    public void setName( String name ) {
        this.name = name;
    }

    public void setPublik( boolean isPublic ) {
        this.publik = isPublic;
    }

    public void setShared( boolean isShared ) {
        this.shared = isShared;
    }

    /**
     * @param size
     */
    public void setSize( Integer size ) {
        this.size = size;
    }

    public boolean equals( GeneSetValueObject ervo ) {
        if(ervo.getClass().equals( this.getClass() ) && ervo.getId().equals( this.getId() )){
            return true;
        }
       return false;
    }

    /**
     * @param currentUserIsOwner the currentUserIsOwner to set
     */
    public void setCurrentUserIsOwner( boolean currentUserIsOwner ) {
        this.currentUserIsOwner = currentUserIsOwner;
    }

    /**
     * @return the currentUserIsOwner
     */
    public boolean isCurrentUserIsOwner() {
        return currentUserIsOwner;
    }
}
