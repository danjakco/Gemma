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
package ubic.gemma.analysis.expression.diff;

import ubic.basecode.util.RClient;
import ubic.basecode.util.RConnectionFactory;
import ubic.gemma.analysis.service.ExpressionDataMatrixService;

/**
 * An abstract analyzer to be extended by analyzers which will make use of R.
 * 
 * @spring.property name="expressionDataMatrixService" ref="expressionDataMatrixService"
 * @author keshav
 * @version $Id$
 */
public abstract class AbstractAnalyzer {

    protected RClient rc = null;

    protected ExpressionDataMatrixService expressionDataMatrixService = null;

    /**
     * 
     *
     */
    public void connectToR() {
        rc = RConnectionFactory.getRConnection();
    }

    /**
     * @param expressionDataMatrixService
     */
    public void setExpressionDataMatrixService( ExpressionDataMatrixService expressionDataMatrixService ) {
        this.expressionDataMatrixService = expressionDataMatrixService;
    }

}
