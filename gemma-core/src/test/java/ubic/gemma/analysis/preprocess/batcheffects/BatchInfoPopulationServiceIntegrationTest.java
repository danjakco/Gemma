/*
 * The Gemma project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.gemma.analysis.preprocess.batcheffects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ubic.gemma.loader.expression.geo.AbstractGeoServiceTest;
import ubic.gemma.loader.expression.geo.GeoDomainObjectGeneratorLocal;
import ubic.gemma.loader.expression.geo.service.GeoService;
import ubic.gemma.loader.util.AlreadyExistsInSystemException;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.model.expression.experiment.FactorValue;

/**
 * Test fetching and loading the batch information from raw files.
 * 
 * @author paul
 * @version $Id$
 */
public class BatchInfoPopulationServiceIntegrationTest extends AbstractGeoServiceTest {
    @Autowired
    protected GeoService geoService;

    @Autowired
    private BatchInfoPopulationService batchInfoPopulationService;

    @Autowired
    ExpressionExperimentService eeService;

    @Test
    public void testLoad() throws Exception {

        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT ) );
        ExpressionExperiment newee;
        try {
            Collection<?> results = geoService.fetchAndLoad( "GSE26903", false, true, false, false );
            newee = ( ExpressionExperiment ) results.iterator().next();

        } catch ( AlreadyExistsInSystemException e ) {
            newee = ( ExpressionExperiment ) e.getData();
        }

        assertNotNull( newee );
        newee = eeService.thawLite( newee );

        assertTrue( batchInfoPopulationService.fillBatchInformation( newee, true ) );
    }

    /**
     * Another Affymetrix format - GCOS
     * 
     * @throws Exception
     */
    @Test
    public void testLoadCommandConsoleFormat() throws Exception {

        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT ) );
        ExpressionExperiment newee;
        try {
            Collection<?> results = geoService.fetchAndLoad( "GSE20219", false, true, false, false );
            newee = ( ExpressionExperiment ) results.iterator().next();

        } catch ( AlreadyExistsInSystemException e ) {
            newee = ( ExpressionExperiment ) e.getData();
        }

        assertNotNull( newee );
        newee = eeService.thawLite( newee );

        assertTrue( batchInfoPopulationService.fillBatchInformation( newee, true ) );

        newee = eeService.thawLite( newee );

        for ( ExperimentalFactor ef : newee.getExperimentalDesign().getExperimentalFactors() ) {
            for ( FactorValue fv : ef.getFactorValues() ) {
                assertTrue( fv.getValue().startsWith( "Batch_0" ) ); // Batch_01, Batch_02 etc.
            }
        }

    }
}
