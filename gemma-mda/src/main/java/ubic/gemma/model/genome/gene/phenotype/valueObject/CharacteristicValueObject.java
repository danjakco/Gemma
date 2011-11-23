/*
 * The Gemma project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.gemma.model.genome.gene.phenotype.valueObject;

/**
 * CharacteristicValueObject containing a category to a value
 * 
 * @author ??
 * @version $Id$
 */
public class CharacteristicValueObject implements Comparable<CharacteristicValueObject> {

    private Long id = null;
    /** id used by url*/
    private String urlId = "";

    private String category = "";
    private String categoryUri = "";

    private String value = "";
    private String valueUri = "";

    /** number of occurence in all genes */
    private Long occurence = null;

    /** what Ontology uses this term */
    private String ontologyUsed = null;

    private boolean alreadyPresentOnGene = false;

    /** child term from a root */
    private boolean child = false;
    /** root of a query */
    private boolean root = false;

    public CharacteristicValueObject( String value, String category ) {
        this.category = category;
        this.value = value;
    }

    public CharacteristicValueObject( String value, String category, String valueUri, String categoryUri ) {
        super();
        this.category = category;
        this.categoryUri = categoryUri;
        this.value = value;
        this.valueUri = valueUri;
        if ( valueUri != null && !valueUri.equals( "" ) && valueUri.indexOf( "#" ) > 0 ) {
            this.urlId = valueUri.substring( valueUri.indexOf( "#" ) + 1, valueUri.length() );
        }
    }

    public String getCategoryUri() {
        return this.categoryUri;
    }

    public void setCategoryUri( String categoryUri ) {
        this.categoryUri = categoryUri;
    }

    public String getValueUri() {
        return this.valueUri;
    }

    public void setValueUri( String valueUri ) {
        this.valueUri = valueUri;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory( String category ) {
        this.category = category;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue( String value ) {
        this.value = value.toLowerCase();
    }

    public Long getOccurence() {
        return this.occurence;
    }

    public void setOccurence( Long occurence ) {
        this.occurence = occurence;
    }

    public boolean isChild() {
        return this.child;
    }

    public void setChild( boolean child ) {
        this.child = child;
    }

    public boolean isRoot() {
        return this.root;
    }

    public void setRoot( boolean root ) {
        this.root = root;
    }

    public String getOntologyUsed() {
        return this.ontologyUsed;
    }

    public void setOntologyUsed( String ontologyUsed ) {
        this.ontologyUsed = ontologyUsed;
    }

    public boolean getAlreadyPresentOnGene() {
        return this.alreadyPresentOnGene;
    }

    public void setAlreadyPresentOnGene( boolean alreadyPresentOnGene ) {
        this.alreadyPresentOnGene = alreadyPresentOnGene;
    }

    public Long getId() {
        return this.id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getUrlId() {
        return this.urlId;
    }

    public void setUrlId( String urlId ) {
        this.urlId = urlId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( this.category == null ) ? 0 : this.category.hashCode() );
        result = prime * result + ( ( this.categoryUri == null ) ? 0 : this.categoryUri.hashCode() );
        result = prime * result + ( ( this.value == null ) ? 0 : this.value.hashCode() );
        result = prime * result + ( ( this.valueUri == null ) ? 0 : this.valueUri.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        CharacteristicValueObject other = ( CharacteristicValueObject ) obj;
        if ( this.valueUri == null ) {
            if ( other.valueUri != null ) return false;
        } else if ( !this.valueUri.equals( other.valueUri ) ) return false;
        return true;
    }

    @Override
    public int compareTo( CharacteristicValueObject o ) {

        if ( this.category.equalsIgnoreCase( o.category ) ) {
            return this.value.compareToIgnoreCase( o.value );
        }
        return this.category.compareTo( o.category );
    }

}
