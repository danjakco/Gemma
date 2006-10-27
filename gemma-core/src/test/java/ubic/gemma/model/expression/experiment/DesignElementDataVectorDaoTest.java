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
package ubic.gemma.model.expression.experiment;

import java.util.Collection;
import java.util.HashSet;

import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorDao; 
import ubic.gemma.testing.BaseSpringContextTest;

/**
 * @author keshav
 * @version $Id$
 */
public class DesignElementDataVectorDaoTest extends BaseSpringContextTest {

    private DesignElementDataVectorDao designElementDataVectorDao = null;

    /**
     * 
     *
     */
    public void testQueryByGeneSymbolAndSpecies() {
        designElementDataVectorDao = ( DesignElementDataVectorDao ) this.getBean( "designElementDataVectorDao" );
        Collection<ExpressionExperiment> expressionExperiments = new HashSet<ExpressionExperiment>();
        for ( long i = 0; i < 3; i++ ) {
            ExpressionExperiment ee = ExpressionExperiment.Factory.newInstance();
            ee.setId( i );
            ee.setName( "test_ee_" + i + " from DesignElementDataVectorDaoTest" );
            expressionExperiments.add( ee );
        }

        Collection objects = designElementDataVectorDao.queryByGeneSymbolAndSpecies( "GRIN1", "mouse",
                expressionExperiments );
        assertNotNull( objects );
    }
    
    public void testGetGenes() {
        designElementDataVectorDao = ( DesignElementDataVectorDao ) this.getBean( "designElementDataVectorDao" );
        DesignElementDataVector dedv = DesignElementDataVector.Factory.newInstance();
        dedv.setId( (long) 1 );
        Collection objects = designElementDataVectorDao.getGenes( dedv );
        assertNotNull( objects );
    }


    public void testGetGenesById() {
        designElementDataVectorDao = ( DesignElementDataVectorDao ) this.getBean( "designElementDataVectorDao" );
        DesignElementDataVector dedv = DesignElementDataVector.Factory.newInstance();
        dedv.setId( (long) 1 );
        Collection objects = designElementDataVectorDao.getGenesById( (long) 1 );
        assertNotNull( objects );
    }

}
