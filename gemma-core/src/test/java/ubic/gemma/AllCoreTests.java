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
package ubic.gemma;

import ubic.gemma.analysis.preprocess.AllPreProcessTests;
import ubic.gemma.model.expression.arrayDesign.ArrayDesignDaoImplTest;
import ubic.gemma.externalDb.ExternalDatabaseTest;
import ubic.gemma.model.genome.QtlDaoImplTest;
import ubic.gemma.model.genome.gene.CandidateGeneListDaoImplTest;
import ubic.gemma.loader.association.Gene2GOAssociationParserTest;
import ubic.gemma.loader.description.OntologyEntryLoaderIntegrationTest;
import ubic.gemma.loader.entrez.pubmed.AllPubMedTests;
import ubic.gemma.loader.expression.arrayDesign.AllArrayDesignTests;
import ubic.gemma.loader.expression.arrayExpress.DataFileFetcherTest;
import ubic.gemma.loader.expression.geo.AllGeoTests;
import ubic.gemma.loader.expression.mage.AllMageTests;
import ubic.gemma.loader.expression.smd.AllSmdTests;
import ubic.gemma.model.common.description.BibliographicReferenceDaoImplTest;
import ubic.gemma.model.common.description.DatabaseEntryDaoImplTest;
import ubic.gemma.security.SecurityIntegrationTest;
import ubic.gemma.security.interceptor.AuditInterceptorTest;
import ubic.gemma.security.interceptor.PersistAclInterceptorTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for gemma-core
 * 
 * @author pavlidis
 * @version $Id$
 */
public class AllCoreTests {

    public static Test suite() {
        TestSuite suite = new TestSuite( "Tests for gemma-core" );

        suite.addTest( AllPubMedTests.suite() );
        suite.addTest( AllGeoTests.suite() );
        suite.addTest( AllMageTests.suite() );

        suite.addTest( AllSmdTests.suite() );

        suite.addTest( AllArrayDesignTests.suite() );
        suite.addTest( AllPreProcessTests.suite() );

        suite.addTestSuite( AuditInterceptorTest.class );
        suite.addTestSuite( PersistAclInterceptorTest.class );
        suite.addTestSuite( SecurityIntegrationTest.class );

        suite.addTestSuite( ArrayDesignDaoImplTest.class );
        suite.addTestSuite( ExternalDatabaseTest.class );
        suite.addTestSuite( CandidateGeneListDaoImplTest.class );
        suite.addTestSuite( QtlDaoImplTest.class );

        suite.addTestSuite( BibliographicReferenceDaoImplTest.class );
        suite.addTestSuite( DatabaseEntryDaoImplTest.class );

        suite.addTestSuite( Gene2GOAssociationParserTest.class );
        suite.addTestSuite( OntologyEntryLoaderIntegrationTest.class );
        suite.addTestSuite( DataFileFetcherTest.class );

        return suite;
    }

}
