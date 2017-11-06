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
package ubic.gemma.core.search;

import ubic.gemma.model.common.Identifiable;
import ubic.gemma.persistence.util.EntityUtils;
import ubic.gemma.persistence.util.ReflectionUtil;

/**
 * @author paul
 */
public class SearchResult implements Comparable<SearchResult>, Identifiable {

    boolean indexSearch;

    private Class<?> resultClass;

    private Long objectId;

    private String highlightedText;

    private Double score = 0.0;

    private Object resultObject;

    public SearchResult( Object searchResult ) {
        if ( searchResult == null )
            throw new IllegalArgumentException( "Search result cannot be null" );
        this.resultObject = searchResult;
        this.resultClass = ReflectionUtil.getBaseForImpl( searchResult.getClass() );
        this.objectId = EntityUtils.getId( resultObject );
    }

    public SearchResult( Object searchResult, double score ) {
        this( searchResult );
        this.score = score;
    }

    public SearchResult( Object searchResult, double score, String matchingText ) {
        this( searchResult );
        this.score = score;
        this.highlightedText = matchingText;
    }

    @Override
    public int compareTo( SearchResult o ) {
        return -this.score.compareTo( o.getScore() );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        final SearchResult other = ( SearchResult ) obj;
        if ( objectId == null ) {
            if ( other.objectId != null )
                return false;
        } else if ( !objectId.equals( other.objectId ) )
            return false;
        if ( resultClass == null ) {
            if ( other.resultClass != null )
                return false;
        } else if ( !resultClass.getName().equals( other.resultClass.getName() ) )
            return false;
        return true;
    }

    public String getHighlightedText() {
        return highlightedText;
    }

    public void setHighlightedText( String highlightedText ) {
        this.highlightedText = highlightedText;
    }

    /**
     * @return the id for the underlying result entity.
     */
    @Override
    public Long getId() {
        return objectId;
    }

    public Class<?> getResultClass() {
        return resultClass;
    }

    public Object getResultObject() {
        return this.resultObject;
    }

    /**
     * @param resultObject if null, the resultObject is reset to null, but the class and id information will not be
     *                     overwritten.
     */
    public void setResultObject( Object resultObject ) {
        this.resultObject = resultObject;
        if ( resultObject != null ) {
            this.resultClass = ReflectionUtil.getBaseForImpl( resultObject.getClass() );
            this.objectId = EntityUtils.getId( resultObject );
        }
    }

    public Double getScore() {
        return score;
    }

    public void setScore( Double score ) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( objectId == null ) ? 0 : objectId.hashCode() );
        result = prime * result + ( ( resultClass == null ) ? 0 : resultClass.getName().hashCode() );
        return result;
    }

    /**
     * @return Was this a result obtained from a Lucene index search?
     */
    public boolean isIndexSearchResult() {
        return indexSearch;
    }

    public void setIndexSearchResult( boolean isIndexSearchResult ) {
        this.indexSearch = isIndexSearchResult;
    }

    @Override
    public String toString() {
        return this.getResultObject() + " matched in: " + ( this.highlightedText != null ?
                "'" + this.highlightedText + "'" :
                "(?)" );
    }
}
