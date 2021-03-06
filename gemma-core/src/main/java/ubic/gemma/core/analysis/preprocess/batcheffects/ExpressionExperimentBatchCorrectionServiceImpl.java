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

import cern.colt.matrix.DoubleMatrix2D;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrixImpl;
import ubic.basecode.math.MatrixStats;
import ubic.basecode.util.FileTools;
import ubic.gemma.core.analysis.util.ExperimentalDesignUtils;
import ubic.gemma.core.datastructure.matrix.ExpressionDataDoubleMatrix;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.common.quantitationtype.ScaleType;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.bioAssayData.ProcessedExpressionDataVector;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.FactorValue;
import ubic.gemma.persistence.service.expression.bioAssayData.ProcessedExpressionDataVectorService;
import ubic.gemma.persistence.service.expression.experiment.ExpressionExperimentService;

import java.util.*;

/**
 * Methods for correcting batch effects.
 *
 * @author paul
 */
@Component
public class ExpressionExperimentBatchCorrectionServiceImpl implements ExpressionExperimentBatchCorrectionService {

    private static final String QT_DESCRIPTION_SUFFIX_FOR_BATCH_CORRECTED = "(batch-corrected)";
    private static final Log log = LogFactory.getLog( ExpressionExperimentBatchCorrectionServiceImpl.class );
    @Autowired
    private ExpressionExperimentService expressionExperimentService;

    @Autowired
    private ProcessedExpressionDataVectorService processedExpressionDataVectorService;

    @Override
    public boolean checkCorrectability( ExpressionExperiment ee, boolean force ) {

        for ( QuantitationType qt : expressionExperimentService.getQuantitationTypes( ee ) ) {
            if ( qt.getIsBatchCorrected() && !force ) { // this avoids batch correcting when we don't need to, but might be the wrong place for this check
                ExpressionExperimentBatchCorrectionServiceImpl.log
                        .warn( "Experiment already has a batch-corrected quantitation type (use 'force' to override): " + ee + ": " + qt );
                return false;
            }
        }

        ExperimentalFactor batch = this.getBatchFactor( ee );
        if ( batch == null ) {
            ExpressionExperimentBatchCorrectionServiceImpl.log.warn( "No batch factor found: " + ee );
            return false;
        }

        String bConf = expressionExperimentService.getBatchConfound( ee );
        if ( bConf != null ) { // we used to let force override this, but that behavior is undesirable: if there is a confound, we don't batch correct
            ExpressionExperimentBatchCorrectionServiceImpl.log
                    .warn( "Experiment cannot be batch corrected due to a confound: " + bConf );
            return false;
        }

        /*
         * Make sure we have at least two samples per batch. This generally won't happen if batches were defined by
         * Gemma.
         */
        Map<Long, Integer> batches = new HashMap<>();
        Set<BioMaterial> seen = new HashSet<>();
        for ( BioAssay ba : ee.getBioAssays() ) {
            BioMaterial bm = ba.getSampleUsed();
            if ( seen.contains( bm ) )
                continue;
            seen.add( bm );
            for ( FactorValue fv : bm.getFactorValues() ) {
                if ( fv.getExperimentalFactor().equals( batch ) ) {
                    Long batchId = fv.getId();
                    if ( !batches.containsKey( batchId ) )
                        batches.put( batchId, 0 );
                    batches.put( batchId, batches.get( batchId ) + 1 );
                }
            }
        }
        /*
         * consider merging batches. - we already do this when we create the batch factor, so in general batches should
         * always have at least 2 samples
         */
        for ( Long batchId : batches.keySet() ) {
            if ( batches.get( batchId ) < 2 ) {
                ExpressionExperimentBatchCorrectionServiceImpl.log
                        .info( "Batch with only one sample detected, correction not possible: " + ee + ", batchId="
                                + batchId );
                return false;
            }
        }

        return true;

    }

    @Override
    public ExpressionDataDoubleMatrix comBat( ExpressionDataDoubleMatrix originalDataMatrix ) {

        ExpressionExperiment ee = originalDataMatrix.getExpressionExperiment();

        ee = expressionExperimentService.thawLite( ee );

        /*
         * is there a batch to use?
         */
        ExperimentalFactor batch = this.getBatchFactor( ee );
        if ( batch == null ) {
            ExpressionExperimentBatchCorrectionServiceImpl.log.warn( "No batch factor found" );
            return null;
        }

        ObjectMatrix<BioMaterial, ExperimentalFactor, Object> design = this.getDesign( ee, originalDataMatrix );

        return this.doComBat( ee, originalDataMatrix, design );

    }

    @Override
    public ExpressionDataDoubleMatrix comBat( ExpressionExperiment ee ) {
        /*
         * is there a batch to use?
         */
        ExperimentalFactor batch = this.getBatchFactor( ee );
        if ( batch == null ) {
            ExpressionExperimentBatchCorrectionServiceImpl.log.warn( "No batch factor found" );
            return null;
        }

        /*
         * Extract data
         */
        Collection<ProcessedExpressionDataVector> vectos = processedExpressionDataVectorService
                .getProcessedDataVectors( ee );
        processedExpressionDataVectorService.thaw( vectos );
        ExpressionDataDoubleMatrix mat = new ExpressionDataDoubleMatrix( vectos );

        return this.comBat( mat );

    }

    @Override
    public ExperimentalFactor getBatchFactor( ExpressionExperiment ee ) {

        ExperimentalFactor batch = null;
        for ( ExperimentalFactor ef : ee.getExperimentalDesign().getExperimentalFactors() ) {
            if ( ExperimentalDesignUtils.isBatch( ef ) ) {
                batch = ef;
                break;
            }
        }
        return batch;
    }

    /**
     * I really don't want ComBat to know about our expression APIs, so I redo the design without the ExperimentalFactor
     * type. But this is a bit stupid and causes other problems.
     */
    private ObjectMatrix<BioMaterial, String, Object> convertFactorValuesToStrings(
            ObjectMatrix<BioMaterial, ExperimentalFactor, Object> design ) {

        ObjectMatrix<BioMaterial, String, Object> designU = new ObjectMatrixImpl<>( design.rows(), design.columns() );
        designU.setRowNames( design.getRowNames() );
        List<String> colNames = new ArrayList<>();
        for ( int i = 0; i < design.rows(); i++ ) {
            for ( int j = 0; j < design.columns(); j++ ) {
                designU.set( i, j, design.get( i, j ) );
                if ( i == 0 ) {
                    // WARNING we _can_ have duplicates.
                    colNames.add( design.getColName( j ).getName() );
                }
            }
        }
        designU.setColumnNames( colNames );
        return designU;
    }

    private ExpressionDataDoubleMatrix doComBat( ExpressionExperiment ee, ExpressionDataDoubleMatrix originalDataMatrix,
            ObjectMatrix<BioMaterial, ExperimentalFactor, Object> design ) {
        ObjectMatrix<BioMaterial, String, Object> designU = this.convertFactorValuesToStrings( design );
        DoubleMatrix<CompositeSequence, BioMaterial> matrix = originalDataMatrix.getMatrix();

        designU = this.orderMatrix( matrix, designU );

        ScaleType scale = originalDataMatrix.getQuantitationTypes().iterator().next().getScale();

        boolean transformed = false;
        if ( !( scale.equals( ScaleType.LOG2 ) || scale.equals( ScaleType.LOG10 ) || scale
                .equals( ScaleType.LOGBASEUNKNOWN ) || scale.equals( ScaleType.LN ) ) ) {
            ExpressionExperimentBatchCorrectionServiceImpl.log.info( " *** COMBAT: LOG TRANSFORMING ***" );
            transformed = true;
            MatrixStats.logTransform( matrix );
        }

        /*
         * Process
         */
        ComBat<CompositeSequence, BioMaterial> comBat = new ComBat<>( matrix, designU );

        DoubleMatrix2D results = comBat.run( true ); // false: NONPARAMETRIC

        // note these plots always reflect the parametric setup.
        comBat.plot( ee.getId() + "." + FileTools.cleanForFileName( ee.getShortName() ) ); // TEMPORARY?

        /*
         * Postprocess. Results is a raw matrix/
         */
        DoubleMatrix<CompositeSequence, BioMaterial> correctedDataMatrix = new DenseDoubleMatrix<>( results.toArray() );
        correctedDataMatrix.setRowNames( matrix.getRowNames() );
        correctedDataMatrix.setColumnNames( matrix.getColNames() );

        if ( transformed ) {
            MatrixStats.unLogTransform( correctedDataMatrix );
        }

        ExpressionDataDoubleMatrix correctedExpressionDataMatrix = new ExpressionDataDoubleMatrix( originalDataMatrix,
                correctedDataMatrix );

        assert correctedExpressionDataMatrix.getQuantitationTypes().size() == 1;
        /*
         * It is easier if we make a new quantitationtype.
         */
        QuantitationType oldQt = correctedExpressionDataMatrix.getQuantitationTypes().iterator().next();
        QuantitationType newQt = this.makeNewQuantitationType( oldQt );
        correctedExpressionDataMatrix.getQuantitationTypes().clear();
        correctedExpressionDataMatrix.getQuantitationTypes().add( newQt );

        // Sanity check...
        for ( int i = 0; i < correctedExpressionDataMatrix.columns(); i++ ) {
            assert correctedExpressionDataMatrix.getBioMaterialForColumn( i )
                    .equals( originalDataMatrix.getBioMaterialForColumn( i ) );
        }

        return correctedExpressionDataMatrix;
    }

    /**
     * Extract sample information, format into something ComBat can use. Which covariates should we use??
     */
    private ObjectMatrix<BioMaterial, ExperimentalFactor, Object> getDesign( ExpressionExperiment ee,
            ExpressionDataDoubleMatrix mat ) {

        Collection<ExperimentalFactor> experimentalFactors = ee.getExperimentalDesign().getExperimentalFactors();

        List<ExperimentalFactor> factors = new ArrayList<>( experimentalFactors );

        List<BioMaterial> orderedSamples = ExperimentalDesignUtils.getOrderedSamples( mat, factors );

        return ExperimentalDesignUtils.sampleInfoMatrix( factors, orderedSamples,
                ExperimentalDesignUtils.getBaselineConditions( orderedSamples, factors ) );
    }

    private QuantitationType makeNewQuantitationType( QuantitationType oldQt ) {
        QuantitationType newQt = QuantitationType.Factory.newInstance();
        newQt.setIsBatchCorrected( true );
        newQt.setDescription( oldQt.getDescription() );
        newQt.setIsBackground( oldQt.getIsBackground() );
        newQt.setIsBackgroundSubtracted( oldQt.getIsBackgroundSubtracted() );
        newQt.setGeneralType( oldQt.getGeneralType() );
        newQt.setIsMaskedPreferred( oldQt.getIsMaskedPreferred() );
        newQt.setIsPreferred( oldQt.getIsPreferred() );
        newQt.setIsRatio( oldQt.getIsRatio() );
        newQt.setScale( oldQt.getScale() );
        newQt.setIsNormalized( oldQt.getIsNormalized() );
        newQt.setRepresentation( oldQt.getRepresentation() );
        newQt.setName( oldQt.getName() );
        newQt.setType( oldQt.getType() );
        newQt.setIsRecomputedFromRawData( oldQt.getIsRecomputedFromRawData() );

        if ( !newQt.getDescription().toLowerCase().contains(
                ExpressionExperimentBatchCorrectionServiceImpl.QT_DESCRIPTION_SUFFIX_FOR_BATCH_CORRECTED ) ) {
            newQt.setDescription( newQt.getDescription() + " "
                    + ExpressionExperimentBatchCorrectionServiceImpl.QT_DESCRIPTION_SUFFIX_FOR_BATCH_CORRECTED );
        }
        return newQt;
    }

    /**
     * Reorder the design matrix so its rows are in the same order as the columns of the data matrix.
     *
     * @return updated designU
     */
    private ObjectMatrix<BioMaterial, String, Object> orderMatrix( DoubleMatrix<CompositeSequence, BioMaterial> matrix,
            ObjectMatrix<BioMaterial, String, Object> designU ) {

        ObjectMatrix<BioMaterial, String, Object> result = new ObjectMatrixImpl<>( designU.rows(), designU.columns() );

        List<BioMaterial> rowNames = matrix.getColNames();

        for ( int j = 0; j < designU.columns(); j++ ) {
            for ( int i = 0; i < designU.rows(); i++ ) {
                result.set( i, j, designU.get( designU.getRowIndexByName( rowNames.get( i ) ), j ) );
            }
        }
        result.setRowNames( matrix.getColNames() );
        result.setColumnNames( designU.getColNames() );
        return result;

    }

}
