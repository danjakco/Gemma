/*
 * The Gemma project
 * 
 * Copyright (c) 2005 Columbia University
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
package edu.columbia.gemma.loader.association;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.columbia.gemma.BaseDAOTestCase;
import edu.columbia.gemma.association.Gene2GOAssociationDao;
import edu.columbia.gemma.common.description.ExternalDatabase;
import edu.columbia.gemma.common.description.OntologyEntry;
import edu.columbia.gemma.common.description.OntologyEntryDao;
import edu.columbia.gemma.genome.Gene;
import edu.columbia.gemma.genome.GeneDao;
import edu.columbia.gemma.genome.Taxon;
import edu.columbia.gemma.genome.TaxonDao;
import edu.columbia.gemma.loader.expression.PersisterHelper;
import edu.columbia.gemma.loader.loaderutils.ParserAndLoaderTools;

/**
 * This test is more representative of integration testing than unit testing as it tests multiple both parsing and
 * loading.
 * <hr>
 * <p>
 * Copyright (c) 2004 - 2005 Columbia University
 * 
 * @author keshav
 * @version $Id$
 */
public class Gene2GOAssociationParserTest extends BaseDAOTestCase {
    protected static final Log log = LogFactory.getLog( Gene2GOAssociationParserTest.class );

    Gene2GOAssociationParserImpl gene2GOAssParser = null;

    Gene2GOAssociationLoaderImpl gene2GOAssLoader = null;

    Collection<Object> gene2GOCol = null;

    Map gene2GOMap = null;

    TaxonDao taxonDao = null;

    /**
     * Tests both the parser and the loader. This is more of an integration test, but since it's dependencies are
     * localized to the Gemma project it has been added to the test suite.
     * 
     * @throws NoSuchMethodException
     * @throws IOException
     * @throws ConfigurationException
     */
    @SuppressWarnings("unchecked")
    public void testParseAndLoad() throws NoSuchMethodException, IOException {
        log
                .info( "Testing class: Gene2GOAssocationParser method: public Method findParseLineMethod( Map g2GOMap, String name) throws NoSuchMethodException" );

        // String url = "ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/gene2go.gz";

        OntologyEntry oe = OntologyEntry.Factory.newInstance();
        oe.setAccession( "GO:xxxxx" );

        ExternalDatabase ed = ExternalDatabase.Factory.newInstance();
        ed.setName( "external testdb" );

        oe.setExternalDatabase( ed );

        Gene g = Gene.Factory.newInstance();

        g.setNcbiId( "1246500" );

        Taxon t = Taxon.Factory.newInstance();

        Collection<Taxon> taxa = taxonDao.loadAll();
        if ( taxa.size() == 0 ) {
            t.setCommonName( "Human" );
            g.setTaxon( t );
        } else
            g.setTaxon( taxa.iterator().next() );

        Object[] dependencies = new Object[2];

        dependencies[0] = oe;

        dependencies[1] = g;

        // TODO verify ftp
        // gene2GOCol = gene2GOAssParser.parseFromHttp( url, dependencies );

        InputStream is = this.getClass().getResourceAsStream( "/data/loader/association/gene2go.gz" );

        GZIPInputStream gZipIs = new GZIPInputStream( is );

        Method m = ParserAndLoaderTools.findParseLineMethod( gene2GOAssParser.getGene2GOAssociationMappings(),
                "gene2go" );

        gene2GOMap = gene2GOAssParser.parse( gZipIs, m );

        gene2GOCol = gene2GOAssParser.persist( gene2GOMap.values() );

        ParserAndLoaderTools.loadDatabase( gene2GOAssLoader, gene2GOCol );

    }

    /**
     * Configure parser and loader. Provide "tomcat-esque" functionality by injecting the parser and loader with their
     * dependencies.
     */
    protected void setUp() throws Exception {
        super.setUp();

        gene2GOAssParser = new Gene2GOAssociationParserImpl( "gene2go" );

        // constructor injection
        Gene2GOAssociationMappings g2GOMappings = new Gene2GOAssociationMappings( ( TaxonDao ) ctx.getBean( "taxonDao" ) );

        gene2GOAssParser.setGene2GOAssociationMappings( g2GOMappings );

        gene2GOAssParser.setPersisterHelper( ( PersisterHelper ) ctx.getBean( "persisterHelper" ) );

        gene2GOAssParser.setOntologyEntryDao( ( OntologyEntryDao ) ctx.getBean( "ontologyEntryDao" ) );

        gene2GOAssParser.setGeneDao( ( GeneDao ) ctx.getBean( "geneDao" ) );

        gene2GOMap = new HashMap();

        taxonDao = ( TaxonDao ) ctx.getBean( "taxonDao" );

        gene2GOAssLoader = new Gene2GOAssociationLoaderImpl();

        gene2GOAssLoader.setGene2GOAssociationDao( ( Gene2GOAssociationDao ) ctx.getBean( "gene2GOAssociationDao" ) );

    }

    /**
     * 
     */
    protected void tearDown() throws Exception {
        super.tearDown();

        gene2GOAssParser = null;

        gene2GOAssParser = null;

        gene2GOCol = null;

        gene2GOMap = null;

        taxonDao = null;

    }

}
