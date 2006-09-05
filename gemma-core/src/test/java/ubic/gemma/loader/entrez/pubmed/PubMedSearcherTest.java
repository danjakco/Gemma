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
package ubic.gemma.loader.entrez.pubmed;

import ubic.gemma.apps.AbstractCLITestCase;
import ubic.gemma.util.ConfigUtils;

/**
 * @author pavlidis
 * @version $Id$
 */
public class PubMedSearcherTest extends AbstractCLITestCase {
    PubMedSearcher p = new PubMedSearcher();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link ubic.gemma.loader.entrez.pubmed.PubMedSearcher#main(java.lang.String[])}.
     */
    public final void testMain() throws Exception {
        try {
            Exception result = p.doWork( new String[] { "-v", "3", "-u", ConfigUtils.getString( "gemma.regular.user" ),
                    "-p", ConfigUtils.getString( "gemma.regular.password" ), "-testing", "hippocampus", "diazepam",
                    "juvenile" } );
            if ( result != null ) {
                if ( result instanceof java.net.UnknownHostException ) {
                    log.warn( "Test skipped because of UnknownHostException" );
                    return;
                }
                fail( result.getMessage() );
            }
        } catch ( Exception e ) {
            if ( e instanceof java.net.UnknownHostException ) {
                log.warn( "Test skipped because of UnknownHostException" );
                return;
            }
            throw e;
        }
    }
}
