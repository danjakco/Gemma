/*
 * The Gemma project
 * 
 * Copyright (c) 2010 University of British Columbia
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
package ubic.gemma.model.expression;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentSubSet;
import ubic.gemma.model.expression.experiment.ExpressionExperimentSubSetService;
import ubic.gemma.testing.BaseSpringContextTest;

/**
 * @author paul
 * @version $Id$
 */
public class ExpressionExperimentSubSetServiceTest extends BaseSpringContextTest {

    @Autowired
    private ExpressionExperimentSubSetService expressionExperimentSubSetService;

    @Test
    public final void testFind() throws Exception {

        ExpressionExperiment ee = super.testHelper.getTestPersistentBasicExpressionExperiment();

        ExpressionExperimentSubSet subset = ExpressionExperimentSubSet.Factory.newInstance();

        subset.setSourceExperiment( ee );
        subset.getBioAssays().addAll( ee.getBioAssays() );
        subset.setName( "foo" );

        ExpressionExperimentSubSet persisted = expressionExperimentSubSetService.create( subset );

        assertNotNull( persisted );
        ExpressionExperimentSubSet hit = expressionExperimentSubSetService.find( persisted );

        assertEquals( persisted, hit );

    }

}