/*
 * The Gemma project
 * 
 * Copyright (c) 2008 University of British Columbia
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
package ubic.gemma.loader.expression.geo.service;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;

import ubic.gemma.loader.expression.geo.AbstractGeoServiceTest;
import ubic.gemma.loader.expression.geo.GeoDomainObjectGeneratorLocal;
import ubic.gemma.loader.util.AlreadyExistsInSystemException;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * @author paul
 * @version $Id$
 */
public class GeoSuperSeriesLoadIntegrationTest extends AbstractGeoServiceTest {

    @Autowired
    protected GeoDatasetService geoService;

    @SuppressWarnings("unchecked")
    @Test
    @AfterTransaction
    public void testFetchAndLoadSuperSeries() throws Exception {
        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT
                + "GSE11897SuperSeriesShort" ) );
        try {
            Collection<ExpressionExperiment> results = geoService
                    .fetchAndLoad( "GSE11897", false, true, false, false, true );
            assertEquals( 1, results.size() );
        } catch ( AlreadyExistsInSystemException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
