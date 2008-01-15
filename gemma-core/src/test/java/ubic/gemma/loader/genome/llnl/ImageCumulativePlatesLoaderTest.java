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
package ubic.gemma.loader.genome.llnl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import ubic.gemma.loader.genome.llnl.ImageCumulativePlatesLoader;
import ubic.gemma.model.common.description.ExternalDatabaseService;
import ubic.gemma.model.genome.biosequence.BioSequence;
import ubic.gemma.model.genome.biosequence.BioSequenceService;
import ubic.gemma.testing.BaseSpringContextTest;

/**
 * @author pavlidis
 * @version $Id$
 */
public class ImageCumulativePlatesLoaderTest extends BaseSpringContextTest {

    InputStream is;
    ImageCumulativePlatesLoader loader;
    private Collection<BioSequence> biosequences = new ArrayList<BioSequence>();

    @Override
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        is = this.getClass().getResourceAsStream( "/data/loader/genome/cumulative.plates.test.txt" );
    }

    public void testLoadInputStream() throws Exception {
        ImageCumulativePlatesParser parser = new ImageCumulativePlatesParser();
        parser.parse( is );
        this.biosequences = parser.getResults();
        loader = new ImageCumulativePlatesLoader();
        loader.setPersisterHelper( persisterHelper );
        loader.setBioSequenceService( ( BioSequenceService ) this.getBean( "bioSequenceService" ) );
        loader.setExternalDatabaseService( ( ExternalDatabaseService ) this.getBean( "externalDatabaseService" ) );
        int actualValue = loader.load( is );
        is.close();
        assertEquals( 418, actualValue );
    }

    @Override
    protected void onTearDownInTransaction() throws Exception {
        is.close();
        BioSequenceService bss = ( BioSequenceService ) this.getBean( "bioSequenceService" );
        for ( BioSequence bs : biosequences ) {
            BioSequence found = bss.find( bs );
            if ( found == null ) continue;
            log.info( "Deleting " + found );
            bss.remove( found );
        }
    }

}
