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
package ubic.gemma.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author pavlidis
 * @version $Id$
 */
public class SettingsTest {

    /**
     * This has to exist in Gemma.properties for this test to work.
     */
    private static final String TEST_VARIABLE = "testProperty";

    /**
     * This tests whether the 'include' is working.
     */
    @Test
    public final void testInclude() {
        String actualResult = Settings.getString( TEST_VARIABLE );
        assertFalse( actualResult.contains( "$" ) );
    }

    @Test
    public void testProp1() {
        Settings.getString( "geo.remote.platformDir" );
    }

    @Test
    public void testProp2() {
        Settings.getString( "gemma.compass.dir" );
    }

}