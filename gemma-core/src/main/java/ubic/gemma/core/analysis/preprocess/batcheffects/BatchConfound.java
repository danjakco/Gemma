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
package ubic.gemma.core.analysis.preprocess.batcheffects;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import ubic.basecode.math.KruskalWallis;
import ubic.gemma.core.analysis.preprocess.svd.SVDServiceHelperImpl;
import ubic.gemma.core.analysis.util.ExperimentalDesignUtils;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.FactorValue;

import java.util.*;

/**
 * Test if an experimental design is confounded with batches.
 *
 * @author paul
 */
public class BatchConfound {

    private static final Log log = LogFactory.getLog( BatchConfound.class.getName() );

    public static Collection<BatchConfoundValueObject> test( ExpressionExperiment ee ) {
        Map<ExperimentalFactor, Map<Long, Double>> bioMaterialFactorMap = getBioMaterialFactorMap( ee );
        return factorBatchConfoundTest( ee, bioMaterialFactorMap );
    }

    private static Map<ExperimentalFactor, Map<Long, Double>> getBioMaterialFactorMap( ExpressionExperiment ee ) {
        Map<ExperimentalFactor, Map<Long, Double>> bioMaterialFactorMap = new HashMap<>();

        for ( BioAssay bioAssay : ee.getBioAssays() ) {
            BioMaterial bm = bioAssay.getSampleUsed();
            SVDServiceHelperImpl.populateBMFMap( bioMaterialFactorMap, bm );
        }
        return bioMaterialFactorMap;
    }

    private static Collection<BatchConfoundValueObject> factorBatchConfoundTest( ExpressionExperiment ee,
            Map<ExperimentalFactor, Map<Long, Double>> bioMaterialFactorMap ) throws IllegalArgumentException {

        Map<Long, Long> batchMembership = new HashMap<>();
        ExperimentalFactor batchFactor = null;
        Map<Long, Integer> batchIndexes = new HashMap<>();
        for ( ExperimentalFactor ef : bioMaterialFactorMap.keySet() ) {
            if ( ExperimentalDesignUtils.isBatch( ef ) ) {
                batchFactor = ef;

                Map<Long, Double> bmToFv = bioMaterialFactorMap.get( batchFactor );

                if ( bmToFv == null ) {
                    log.warn( "No biomaterial --> factor value map for batch factor: " + batchFactor );
                    continue;
                }

                int index = 0;
                for ( FactorValue fv : batchFactor.getFactorValues() ) {
                    batchIndexes.put( fv.getId(), index++ );
                }

                for ( Long bmId : bmToFv.keySet() ) {
                    batchMembership.put( bmId, bmToFv.get( bmId ).longValue() );
                }
                break;
            }
        }

        Set<BatchConfoundValueObject> result = new HashSet<>();
        if ( batchFactor == null ) {
            return result;
        }

        /*
         * Compare other factors to batches to look for confounds.
         */

        for ( ExperimentalFactor ef : bioMaterialFactorMap.keySet() ) {

            if ( ef.equals( batchFactor ) )
                continue;

            Map<Long, Double> bmToFv = bioMaterialFactorMap.get( ef );
            int numBioMaterials = bmToFv.keySet().size();

            assert numBioMaterials > 0 : "No biomaterials for " + ef;

            double p = Double.NaN;
            double chiSquare;
            int df;

            int numBatches = batchFactor.getFactorValues().size();
            if ( ExperimentalDesignUtils.isContinuous( ef ) ) {

                DoubleArrayList factorValues = new DoubleArrayList( numBioMaterials );
                factorValues.setSize( numBioMaterials );

                IntArrayList batches = new IntArrayList( numBioMaterials );
                batches.setSize( numBioMaterials );

                int j = 0;
                for ( Long bmId : bmToFv.keySet() ) {

                    assert factorValues.size() > 0 : "Biomaterial to factorValue is empty for " + ef;

                    factorValues.set( j, bmToFv.get( bmId ) );
                    long batch = batchMembership.get( bmId );
                    batches.set( j, batchIndexes.get( batch ) );
                    j++;
                }

                p = KruskalWallis.test( factorValues, batches );
                df = KruskalWallis.dof( factorValues, batches );
                chiSquare = KruskalWallis.kwStatistic( factorValues, batches );

                log.debug( "KWallis\t" + ee.getId() + "\t" + ee.getShortName() + "\t" + ef.getId() + "\t" + ef.getName()
                        + "\t" + String.format( "%.2f", chiSquare ) + "\t" + df + "\t" + String.format( "%.2g", p )
                        + "\t" + numBatches );
            } else {

                Map<Long, Integer> factorValueIndexes = new HashMap<>();
                int index = 0;
                for ( FactorValue fv : ef.getFactorValues() ) {
                    factorValueIndexes.put( fv.getId(), index++ );
                }
                Map<Long, Long> factorValueMembership = new HashMap<>();

                for ( Long bmId : bmToFv.keySet() ) {
                    factorValueMembership.put( bmId, bmToFv.get( bmId ).longValue() );
                }

                long[][] counts = new long[numBatches][ef.getFactorValues().size()];

                for ( int i = 0; i < batchIndexes.size(); i++ ) {
                    for ( int j = 0; j < factorValueIndexes.size(); j++ ) {
                        counts[i][j] = 0;
                    }
                }

                for ( Long bm : bmToFv.keySet() ) {
                    long fv = factorValueMembership.get( bm );
                    Long batch = batchMembership.get( bm );
                    if ( batch == null ) {
                        log.warn( "No batch membership for : " + bm );
                        continue;
                    }
                    int batchIndex = batchIndexes.get( batch );
                    int factorIndex = factorValueIndexes.get( fv );
                    counts[batchIndex][factorIndex]++;
                }

                ChiSquareTest cst = new ChiSquareTest();

                try {
                    chiSquare = cst.chiSquare( counts );
                } catch ( IllegalArgumentException e ) {
                    log.warn( "IllegalArgumentException exception computing ChiSq for : " + ef + "; Error was: " + e
                            .getMessage() );
                    chiSquare = Double.NaN;
                }

                df = ( counts.length - 1 ) * ( counts[0].length - 1 );
                ChiSquaredDistribution distribution = new ChiSquaredDistribution( df );

                if ( !Double.isNaN( chiSquare ) ) {
                    p = 1.0 - distribution.cumulativeProbability( chiSquare );
                }

                log.debug( "ChiSq\t" + ee.getId() + "\t" + ee.getShortName() + "\t" + ef.getId() + "\t" + ef.getName()
                        + "\t" + String.format( "%.2f", chiSquare ) + "\t" + df + "\t" + String.format( "%.2g", p )
                        + "\t" + numBatches );
            }

            BatchConfoundValueObject summary = new BatchConfoundValueObject( ee, ef, chiSquare, df, p, numBatches );

            result.add( summary );
        }
        return result;
    }
}
