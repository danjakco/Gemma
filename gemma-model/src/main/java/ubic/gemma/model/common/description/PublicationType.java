/*
 * The Gemma project.
 * 
 * Copyright (c) 2006-2012 University of British Columbia
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
package ubic.gemma.model.common.description;

/**
 * @deprecated as far as I know, not used (we save it for pubmed refs, but we don't read it?). if we need it we can
 *             store it as a component of the bibliographic reference.
 */
@Deprecated
public abstract class PublicationType implements java.io.Serializable {

    /**
     * Constructs new instances of {@link PublicationType}.
     */
    public static final class Factory {
        /**
         * Constructs a new instance of {@link PublicationType}.
         */
        public static PublicationType newInstance() {
            return new PublicationTypeImpl();
        }

        /**
         * Constructs a new instance of {@link PublicationType}, taking all possible properties (except the
         * identifier(s))as arguments.
         */
        public static PublicationType newInstance( String type ) {
            final PublicationType entity = new PublicationTypeImpl();
            entity.setType( type );
            return entity;
        }
    }

    private String type;

    private Long id;

    /**
     * No-arg constructor added to satisfy javabean contract
     * 
     * @author Paul
     */
    public PublicationType() {
    }

    /**
     * Returns <code>true</code> if the argument is an PublicationType instance and all identifiers for this entity
     * equal the identifiers of the argument entity. Returns <code>false</code> otherwise.
     */
    @Override
    public boolean equals( Object object ) {
        if ( this == object ) {
            return true;
        }
        if ( !( object instanceof PublicationType ) ) {
            return false;
        }
        final PublicationType that = ( PublicationType ) object;
        if ( this.id == null || that.getId() == null || !this.id.equals( that.getId() ) ) {
            return false;
        }
        return true;
    }

    /**
     * 
     */
    public Long getId() {
        return this.id;
    }

    /**
     * 
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns a hash code based on this entity's identifiers.
     */
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 29 * hashCode + ( id == null ? 0 : id.hashCode() );

        return hashCode;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public void setType( String type ) {
        this.type = type;
    }

}