/*
 * The Gemma project.
 * 
 * Copyright (c) 2006-2012 University of British Columbia
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
package ubic.gemma.model.association.coexpression;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * 
 */
public abstract class RatGeneCoExpression extends Gene2GeneCoexpression {

    /**
     * 
     */
    private static final long serialVersionUID = -151363121627382446L;

    /**
     * Constructs new instances of {@link RatGeneCoExpression}.
     */
    public static final class Factory {

        /**
         * @param effect
         * @param firstGene
         * @param secondGene
         * @return
         */
        public static RatGeneCoExpression newInstance( Double effect, Long firstGene, Long secondGene ) {
            final RatGeneCoExpression entity = new RatGeneCoExpressionImpl();
            assert effect != null && firstGene != null && secondGene != null;
            try {
                FieldUtils.writeField( entity, "firstGene", firstGene, true );
                FieldUtils.writeField( entity, "secondGene", secondGene, true );
                FieldUtils.writeField( entity, "positiveCorrelation", effect > 0, true );
                FieldUtils.writeField( entity, "numDataSetsSupporting", 1, true );

            } catch ( IllegalAccessException e ) {
                System.err.println( e );
            }

            return entity;
        }
    }

}