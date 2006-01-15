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
package edu.columbia.gemma.loader.entrez.pubmed;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import edu.columbia.gemma.common.description.BibliographicReference;

/**
 * Class that can retrieve pubmed records (in XML format) via HTTP. The url used is configured via a resource.
 * <hr>
 * <p>
 * 
 * 
 * @author pavlidis
 * @version $Id$
 * @spring.bean id="pubMedXmlFetcher"
 */
public class PubMedXMLFetcher {

    protected static final Log log = LogFactory.getLog( PubMedXMLFetcher.class );
    private String uri;

    public PubMedXMLFetcher() throws ConfigurationException {
        Configuration config = new PropertiesConfiguration( "Gemma.properties" );
        String baseURL = ( String ) config.getProperty( "entrez.efetch.baseurl" );
        String db = ( String ) config.getProperty( "entrez.efetch.pubmed.db" );
        String idtag = ( String ) config.getProperty( "entrez.efetch.pubmed.idtag" );
        String retmode = ( String ) config.getProperty( "entrez.efetch.pubmed.retmode" );
        String rettype = ( String ) config.getProperty( "entrez.efetch.pubmed.rettype" );
        uri = baseURL + "&" + db + "&" + retmode + "&" + rettype + "&" + idtag;
    }

    /**
     * For an integer pubmed id
     * 
     * @param pubMedId
     * @return BibliographicReference representing the publication
     * @throws IOException
     */
    public BibliographicReference retrieveByHTTP( int pubMedId ) throws IOException, SAXException,
            ParserConfigurationException {
        URL toBeGotten = new URL( uri + pubMedId );
        log.info( "Fetching " + toBeGotten );
        PubMedXMLParser pmxp = new PubMedXMLParser();
        return pmxp.parse( toBeGotten.openStream() );
    }
}
