/*
 * The Gemma project
 * 
 * Copyright (c) 2006 University of British Columbia
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
package ubic.gemma.web.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.TaxonService;

/**
 * Used to convert Taxon from and into strings for display in forms.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class TaxonPropertyEditor extends PropertyEditorSupport {

    private static Log log = LogFactory.getLog( TaxonPropertyEditor.class.getName() );

    private TaxonService taxonService;

    public TaxonPropertyEditor( TaxonService taxonService ) {
        this.taxonService = taxonService;
    }

    public String getAsText() {
        if ( this.getValue() == null || ( ( Taxon ) this.getValue() ).getId() == null ) {
            return "---";
        }
        return ( ( Taxon ) this.getValue() ).getScientificName();
    }

    public void setAsText( String text ) throws IllegalArgumentException {
        if ( log.isDebugEnabled() ) log.debug( "Transforming " + text + " to a taxon..." );
        Object ad = taxonService.findByScientificName( text );
        this.setValue( ad ); // okay to be null
    }
}
