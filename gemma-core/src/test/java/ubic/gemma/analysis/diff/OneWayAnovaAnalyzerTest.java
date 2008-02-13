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
package ubic.gemma.analysis.diff;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.model.expression.analysis.ExpressionAnalysis;
import ubic.gemma.model.expression.analysis.ExpressionAnalysisResultSet;

/**
 * Tests the one way anova analyzer.
 * 
 * @author keshav
 * @version $Id$
 */
public class OneWayAnovaAnalyzerTest extends BaseAnalyzerConfigurationTest {

    /**
     * The following has been confirmed with the results from the R console:
     * <p>
     * data (for one design element): 0.515, 0.0918, 0.478, 0.63, 0.0521, 0.4033, 0.2055, 0.1582
     * <p>
     * factor: "pcp", "no pcp", "pcp", "no pcp", "no pcp", "pcp", "no pcp", "pcp"
     * <p>
     * resulting p-value: 0.393
     * <p>
     * (Note: Because there are only two factor values ("pcp", "no pcp") this is really just a t-test but this was
     * tested out on the R console the same one way anova call used in the {@link OneWayAnovaAnalyzer}).
     */

    private Log log = LogFactory.getLog( this.getClass() );

    OneWayAnovaAnalyzer analyzer = null;

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.analysis.diff.BaseAnalyzerConfigurationTest#onSetUpInTransaction()
     */
    @Override
    public void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        analyzer = ( OneWayAnovaAnalyzer ) this.getBean( "oneWayAnovaAnalyzer" );
    }

    /**
     * Tests the OneWayAnova method.
     */
    @SuppressWarnings("unchecked")
    public void testOneWayAnova() throws Exception {

        if ( !connected ) {
            log.warn( "Could not establish R connection.  Skipping test ..." );
            return;
        }
        log.debug( "Testing OneWayAnova method in " + OneWayAnovaAnalyzer.class.getName() );

        super.configureTestDataForOneWayAnova();

        configureMocks();

        ExpressionAnalysis expressionAnalysis = analyzer.oneWayAnova( expressionExperiment );

        Collection<ExpressionAnalysisResultSet> resultSets = expressionAnalysis.getResultSets();
        ExpressionAnalysisResultSet resultSet = resultSets.iterator().next();
        int numResults = resultSet.getResults().size();

        assertEquals( numResults, NUM_DESIGN_ELEMENTS );
        logResults( resultSet );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.analysis.diff.BaseAnalyzerConfigurationTest#configureMocks()
     */
    @Override
    protected void configureMocks() throws Exception {

        configureMockAnalysisServiceHelper( 1 );
        analyzer.setAnalysisHelperService( analysisHelperService );

    }

}
