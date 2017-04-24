/*
 * The Gemma project
 * 
 * Copyright (c) 2007 University of British Columbia
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ubic.gemma.analysis.expression.diff.DifferentialExpressionAnalyzerServiceImpl.AnalysisType;
import ubic.gemma.analysis.preprocess.batcheffects.BatchInfoPopulationServiceImpl;
import ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.experiment.*;

import java.util.Collection;
import java.util.HashSet;

/**
 * A differential expression analysis tool that executes the appropriate analysis based on the number of experimental
 * factors and factor values, as well as the block design.
 * Implementations of the selected analyses; t-test, one way anova, and two way anova with and without interactions are
 * based on the details of the paper written by P. Pavlidis, Methods 31 (2003) 282-289.
 * See http://www.bioinformatics.ubc.ca/pavlidis/lab/docs/reprints/anova-methods.pdf.
 *
 * @author keshav
 */
@Component
public class AnalysisSelectionAndExecutionServiceImpl implements AnalysisSelectionAndExecutionService {

    private static final Log log = LogFactory.getLog( AnalysisSelectionAndExecutionServiceImpl.class );

    /*
     * We are context-aware so we can get prototype beans.
     */
    private ApplicationContext applicationContext;

    @Override
    public Collection<DifferentialExpressionAnalysis> analyze( ExpressionExperiment expressionExperiment,
            DifferentialExpressionAnalysisConfig config ) {
        AnalysisType analyzer = determineAnalysis( expressionExperiment, config );

        if ( analyzer == null ) {
            throw new RuntimeException( "Could not locate an appropriate analyzer" );
        }

        return this.applicationContext.getBean( DiffExAnalyzer.class )
                .run( expressionExperiment, config );
    }

    @Override
    public DifferentialExpressionAnalysis analyze( ExpressionExperimentSubSet subset,
            DifferentialExpressionAnalysisConfig config ) {
        AnalysisType analyzer = determineAnalysis( subset, config );

        if ( analyzer == null ) {
            throw new RuntimeException( "Could not locate an appropriate analyzer" );
        }

        return this.applicationContext.getBean( DiffExAnalyzer.class )
                .run( subset, config );
    }

    @Override
    public AnalysisType determineAnalysis( BioAssaySet bioAssaySet, Collection<ExperimentalFactor> experimentalFactors,
            ExperimentalFactor subsetFactor, boolean includeInteractionsIfPossible ) {

        Collection<ExperimentalFactor> efsToUse = getFactorsToUse( bioAssaySet, experimentalFactors );

        if ( subsetFactor != null ) {
            /*
             * Note that the interaction term might still get used (if selected), we just don't decide here.
             */
            return AnalysisType.GENERICLM;
        }

        assert !efsToUse.isEmpty();

        /*
         * If any of the factors are continuous, just use a generic glm.
         */
        for ( ExperimentalFactor ef : efsToUse ) {
            if ( ef.getType().equals( FactorType.CONTINUOUS ) ) {
                return AnalysisType.GENERICLM;
            }
        }

        if ( efsToUse.size() == 1 ) {

            ExperimentalFactor experimentalFactor = efsToUse.iterator().next();
            Collection<FactorValue> factorValues = experimentalFactor.getFactorValues();

            /*
             * Check that there is more than one value in at least one group
             */
            boolean ok = DifferentialExpressionAnalysisUtil.checkValidForLm( bioAssaySet, experimentalFactor );

            if ( !ok ) {
                return null;
            }

            if ( factorValues.isEmpty() )
                throw new IllegalArgumentException(
                        "Collection of factor values is either null or 0. Cannot execute differential expression analysis." );
            if ( factorValues.size() == 1 ) {
                // one sample t-test.
                return AnalysisType.OSTTEST;
            } else if ( factorValues.size() == 2 ) {
                /*
                 * Return t-test analyzer. This can be taken care of by the one way anova, but keeping it separate for
                 * clarity.
                 */
                return AnalysisType.TTEST;
            } else {

                /*
                 * Return one way anova analyzer. NOTE: This can take care of the t-test as well, since a one-way anova
                 * with two groups is just a t-test
                 */
                return AnalysisType.OWA;
            }

        } else if ( efsToUse.size() == 2 ) {
            /*
             * Candidate for ANOVA
             */
            boolean okForInteraction = true;
            Collection<QuantitationType> qts = getQts( bioAssaySet );
            for ( ExperimentalFactor f : efsToUse ) {
                Collection<FactorValue> factorValues = f.getFactorValues();
                if ( factorValues.size() == 1 ) {

                    boolean useIntercept = false;
                    // check for a ratiometric quantitation type.
                    for ( QuantitationType qt : qts ) {
                        if ( qt.getIsPreferred() && qt.getIsRatio() ) {
                            // use ANOVA but treat the intercept as a factor.
                            useIntercept = true;
                            break;
                        }
                    }

                    if ( useIntercept ) {
                        return AnalysisType.GENERICLM;
                    }

                    return null;
                }
                if ( BatchInfoPopulationServiceImpl.isBatchFactor( f ) ) {
                    log.info( "One of the two factors is 'batch', not using it for an interaction" );
                    okForInteraction = false;
                }

            }
            /* Check for block design and execute two way ANOVA (with or without interactions). */
            if ( !includeInteractionsIfPossible || !DifferentialExpressionAnalysisUtil
                    .blockComplete( bioAssaySet, experimentalFactors ) || !okForInteraction ) {
                return AnalysisType.TWO_WAY_ANOVA_NO_INTERACTION; // NO interactions
            }
            return AnalysisType.TWO_WAY_ANOVA_WITH_INTERACTION;
        } else {
            /*
             * Upstream we bail if there are too many factors.
             */
            return AnalysisType.GENERICLM;
        }
    }

    @Override
    public AnalysisType determineAnalysis( BioAssaySet bioAssaySet, DifferentialExpressionAnalysisConfig config ) {

        if ( config.getFactorsToInclude().isEmpty() ) {
            throw new IllegalArgumentException( "Must provide at least one factor" );
        }

        if ( config.getAnalysisType() == null ) {
            AnalysisType type = determineAnalysis( bioAssaySet, config.getFactorsToInclude(), config.getSubsetFactor(),
                    true );
            if ( type.equals( AnalysisType.TWO_WAY_ANOVA_NO_INTERACTION ) ) {
                /*
                 * Ensure the config does not have interactions.
                 */
                log.info(
                        "Any interaction term will be dropped from the configuration as it cannot be analyzed (no replicates? block incomplete?)" );
                config.getInteractionsToInclude().clear();
                // config.setAnalysisType( type ); // don't need this side-effect.
            }
            return type;
        }

        if ( config.getSubsetFactor() != null ) {
            /*
             * Basically have to make the subsets, and then validate the choice of model on each of those. The following
             * just assumes that we're going to do something very simple.
             */
            return AnalysisType.GENERICLM;
        }

        switch ( config.getAnalysisType() ) {
            case OWA:
                if ( config.getFactorsToInclude().size() != 1 ) {
                    throw new IllegalArgumentException( "Cannot run One-way ANOVA on more than one factor" );
                }
                return AnalysisType.OWA;
            case TWO_WAY_ANOVA_WITH_INTERACTION:
                validateFactorsForTwoWayANOVA( config.getFactorsToInclude() );
                if ( !DifferentialExpressionAnalysisUtil.blockComplete( bioAssaySet, config.getFactorsToInclude() ) ) {
                    throw new IllegalArgumentException(
                            "Experimental design must be block complete to run Two-way ANOVA with interactions" );
                }
                return AnalysisType.TWO_WAY_ANOVA_WITH_INTERACTION;
            case TWO_WAY_ANOVA_NO_INTERACTION:
                // NO interactions.
                validateFactorsForTwoWayANOVA( config.getFactorsToInclude() );
                return AnalysisType.TWO_WAY_ANOVA_NO_INTERACTION;
            case TTEST:
                if ( config.getFactorsToInclude().size() != 1 ) {
                    throw new IllegalArgumentException( "Cannot run t-test on more than one factor " );
                }
                for ( ExperimentalFactor experimentalFactor : config.getFactorsToInclude() ) {
                    if ( experimentalFactor.getFactorValues().size() < 2 ) {
                        throw new IllegalArgumentException(
                                "Need at least two levels per factor to run two-sample t-test" );
                    }
                }
                return AnalysisType.TTEST;
            case OSTTEST:
                // one sample t-test.
                if ( config.getFactorsToInclude().size() != 1 ) {
                    throw new IllegalArgumentException( "Cannot run t-test on more than one factor " );
                }
                for ( ExperimentalFactor experimentalFactor : config.getFactorsToInclude() ) {
                    if ( experimentalFactor.getFactorValues().size() != 1 ) {
                        throw new IllegalArgumentException(
                                "Need only one level for the factor for one-sample t-test" );
                    }
                }
                return AnalysisType.OSTTEST;
            case GENERICLM:
                return AnalysisType.GENERICLM;
            default:
                throw new IllegalArgumentException(
                        "Analyses of that type (" + config.getAnalysisType() + ")are not yet supported" );
        }

    }

    @Override
    public DiffExAnalyzer getAnalyzer() {
        return this.applicationContext.getBean( DiffExAnalyzer.class );
    }

    @Override
    public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Collection<ExperimentalFactor> getFactorsToUse( BioAssaySet bioAssaySet,
            Collection<ExperimentalFactor> experimentalFactors ) {
        Collection<ExperimentalFactor> efsToUse = new HashSet<>();

        ExperimentalDesign design;

        if ( bioAssaySet instanceof ExpressionExperiment ) {
            design = ( ( ExpressionExperiment ) bioAssaySet ).getExperimentalDesign();
        } else if ( bioAssaySet instanceof ExpressionExperimentSubSet ) {
            design = ( ( ExpressionExperimentSubSet ) bioAssaySet ).getSourceExperiment().getExperimentalDesign();
        } else {
            throw new UnsupportedOperationException( "Cannot deal with a " + bioAssaySet.getClass() );
        }

        if ( experimentalFactors == null || experimentalFactors.isEmpty() ) {
            efsToUse.addAll( design.getExperimentalFactors() );
            if ( efsToUse.isEmpty() ) {
                throw new IllegalStateException(
                        "No factors given, nor in the experiment's design. Cannot execute differential expression analysis." );
            }
        } else {
            efsToUse = experimentalFactors;
            if ( efsToUse.isEmpty() ) {
                throw new IllegalArgumentException(
                        "No experimental factors.  Cannot execute differential expression analysis." );
            }

            // sanity check...
            for ( ExperimentalFactor experimentalFactor : efsToUse ) {
                if ( !experimentalFactor.getExperimentalDesign().getId().equals( design.getId() ) ) {
                    throw new IllegalArgumentException( "Factors must come from the experiment provided" );
                }
            }
        }
        return efsToUse;
    }

    private Collection<QuantitationType> getQts( BioAssaySet bioAssaySet ) {
        if ( bioAssaySet instanceof ExpressionExperiment ) {
            return ( ( ExpressionExperiment ) bioAssaySet ).getQuantitationTypes();
        } else if ( bioAssaySet instanceof ExpressionExperimentSubSet ) {
            return ( ( ExpressionExperimentSubSet ) bioAssaySet ).getSourceExperiment().getQuantitationTypes();
        } else {
            throw new UnsupportedOperationException( "Cannot deal with a " + bioAssaySet.getClass() );
        }
    }

    private void validateFactorsForTwoWayANOVA( Collection<ExperimentalFactor> factors ) {
        if ( factors.size() != 2 ) {
            throw new IllegalArgumentException( "Need exactly two factors to run two-way ANOVA" );
        }
        for ( ExperimentalFactor experimentalFactor : factors ) {
            if ( experimentalFactor.getFactorValues().size() < 2 ) {
                throw new IllegalArgumentException( "Need at least two levels per factor to run ANOVA" );
            }
        }
    }

}
