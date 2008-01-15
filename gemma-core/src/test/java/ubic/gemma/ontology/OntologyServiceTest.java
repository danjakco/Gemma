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
package ubic.gemma.ontology;

import java.util.Collection;

import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.testing.BaseSpringContextTest;

/**
 * @author paul
 */
public class OntologyServiceTest extends BaseSpringContextTest {
    boolean mgodisabled = false;

    @Override
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        MgedOntologyService mgo = ( MgedOntologyService ) this.getBean( "mgedOntologyService" );
        if ( !mgo.isEnabled() ) {
            mgodisabled = true;
            return;
        }
        while ( !mgo.isOntologyLoaded() ) {
            Thread.sleep( 1000 );
            log.info( "Waiting for Ontology to load" );
        }
    }

    public void testListAvailableOntologies() {
        if ( mgodisabled ) {
            log.warn( "MGED Ontology not loaded, skipping test" );
            return;
        }
        Collection<Ontology> name = OntologyService.listAvailableOntologies();
        assertTrue( name.size() > 0 );
    }

    public final void testFindExactMatch() throws Exception {
        if ( mgodisabled ) {
            log.warn( "MGED Ontology not loaded, skipping test" );
            return;
        }
        OntologyService os = ( OntologyService ) this.getBean( "ontologyService" );
        Collection<Characteristic> name = os.findExactTerm( "male",
                "http://mged.sourceforge.net/ontologies/MGEDOntology.owl#Sex" );
        assertEquals( 1, name.size() );
    }

}
