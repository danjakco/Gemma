/*
 * The gemma project
 * 
 * Copyright (c) 2014 University of British Columbia
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

package ubic.gemma.model.analysis.expression.coexpression;

import ubic.gemma.model.genome.Gene;

/**
 * TODO Document Me
 * 
 * @author Paul
 *
 */
public class RatCoexpressionSupportDetailsImpl extends SupportDetails {

    private static final long serialVersionUID = 3067914341922800267L;

    public RatCoexpressionSupportDetailsImpl( Gene firstGene, Gene secondGene, Boolean isPositive ) {
        super( firstGene, secondGene, isPositive );
    }
    public RatCoexpressionSupportDetailsImpl( Long firstGene, Long secondGene, Boolean isPositive ) {
        super( firstGene, secondGene, isPositive );
    }
    public RatCoexpressionSupportDetailsImpl() {
        this( (Long)null, null, null );

    }
}
