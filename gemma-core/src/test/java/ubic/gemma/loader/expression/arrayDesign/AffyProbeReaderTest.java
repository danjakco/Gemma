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

package ubic.gemma.loader.expression.arrayDesign;

import java.io.InputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.designElement.Reporter;

/**
 * @author pavlidis
 * @version $Id$
 */
public class AffyProbeReaderTest extends TestCase {
    protected static final Log log = LogFactory.getLog( AffyProbeReaderTest.class );
    AffyProbeReader apr;
    InputStream is;

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        apr = new AffyProbeReader();
        apr.setSequenceField( 5 );
        is = AffyProbeReaderTest.class.getResourceAsStream( "/data/loader/affymetrix-probes-test.txt" );

    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        apr = null;
        if ( is != null ) is.close();
    }

    public final void testReadInputStreamNew() throws Exception {
        is = AffyProbeReaderTest.class.getResourceAsStream( "/data/loader/affymetrix-newprobes-example.txt" );
        apr.setSequenceField( 4 );
        apr.parse( is );

        String expectedValue = "AGCTCAGGTGGCCCCAGTTCAATCT"; // 4
        CompositeSequence cs = apr.get( "1000_at" );

        assertTrue( "CompositeSequence was null", cs != null );

        boolean foundIt = false;
        for ( Iterator<Reporter> iter = cs.getComponentReporters().iterator(); iter.hasNext(); ) {
            Reporter element = iter.next();
            log.info( element.getName() );
            if ( element.getName().equals( "1000_at:617:349" ) ) {
                String actualValue = element.getImmobilizedCharacteristic().getSequence();

                assertEquals( expectedValue, actualValue );
                foundIt = true;
                break;
            }
        }
        assertTrue( "Didn't find the probe ", foundIt );
    }

    /*
     * Class under test for Map read(InputStream)
     */
    public final void testReadExonArrayInputStream() throws Exception {


        is = AffyProbeReaderTest.class.getResourceAsStream( "/data/loader/expression/arrayDesign/HuExSampleProbe.txt" );

        assertTrue( "InputStream was null", is != null );

        apr.parse( is );

        String expectedValue = "CGGTGCTGGGTCAGGGATCGACTGA";
        CompositeSequence cs = apr.get( "2315108" );

        assertTrue( "CompositeSequence was null", cs != null );

        boolean foundIt = false;
        for ( Iterator<Reporter> iter = cs.getComponentReporters().iterator(); iter.hasNext(); ) {
            Reporter element = iter.next();
            log.info( element.getName() );
            if ( element.getName().equals( "2315108:814:817" ) ) {
                String actualValue = element.getImmobilizedCharacteristic().getSequence();

                assertEquals( expectedValue, actualValue );
                foundIt = true;
                break;
            }
        }
        assertTrue( "Didn't find the probe ", foundIt );
    }

    /*
     * Class under test for Map read(InputStream)
     */
    public final void testReadInputStream() throws Exception {

        assertTrue( "InputStream was null", is != null );

        apr.parse( is );

        String expectedValue = "GCCCCCGTGAGGATGTCACTCAGAT"; // 10
        CompositeSequence cs = apr.get( "1004_at" );

        assertTrue( "CompositeSequence was null", cs != null );

        boolean foundIt = false;
        for ( Iterator<Reporter> iter = cs.getComponentReporters().iterator(); iter.hasNext(); ) {
            Reporter element = iter.next();
            log.info( element.getName() );
            if ( element.getName().equals( "1004_at#2:557:275" ) ) {
                String actualValue = element.getImmobilizedCharacteristic().getSequence();

                assertEquals( expectedValue, actualValue );
                foundIt = true;
                break;
            }
        }
        assertTrue( "Didn't find the probe ", foundIt );
    }

}
