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
package ubic.gemma.visualization;

import java.util.Collection;
import java.util.LinkedHashSet;

import ubic.basecode.graphics.MatrixDisplay;
import ubic.basecode.io.ByteArrayConverter;
import ubic.gemma.datastructure.matrix.ExpressionDataDoubleMatrix;
import ubic.gemma.datastructure.matrix.ExpressionDataMatrix;
import ubic.gemma.model.common.quantitationtype.PrimitiveType;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.designElement.DesignElement;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.testing.BaseSpringContextTest;

/**
 * Test class that sets up an {@link ExpressionDataMatrix} and tests the functionality of the
 * ExpressionDataMatrixVisualizationService.
 * <p>
 * The values used are actual values from the Gene Expression Omnibus (GEO). That is, we have obtained information from
 * GSE994. The probe sets used are 218120_s_at and 121_at, and the samples used are GSM15697 and GSM15744. Specifically,
 * we the Gemma objects that correspond to the GEO objects are:
 * <p>
 * DesignElement 1 = 218120_s_at, DesignElement 2 = 121_at
 * <p>
 * BioAssay 1 = "Current Smoker 73", BioAssay 2 = "Former Smoker 34"
 * <p>
 * BioMaterial 1 = "GSM15697", BioMaterial 2 = "GSM15744"
 * <p>
 * BioAssayDimension = "GSM15697, GSM15744" (the names of all the biomaterials).
 * 
 * @author keshav
 * @version $Id$
 */
public class ExpressionDataMatrixVisualizationServiceTest extends BaseSpringContextTest {

    ExpressionDataMatrix expressionDataMatrix = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();

        ByteArrayConverter bac = new ByteArrayConverter();

        ExpressionExperiment ee = ExpressionExperiment.Factory.newInstance();

        QuantitationType qt = QuantitationType.Factory.newInstance();
        qt.setName( "VALUE" );
        qt.setRepresentation( PrimitiveType.DOUBLE );

        BioAssayDimension bioAssayDimension = BioAssayDimension.Factory.newInstance();
        bioAssayDimension.setName( "GSM15697, GSM15744" );

        /* set up the bioassays */
        Collection<BioAssay> assays = new LinkedHashSet<BioAssay>();

        BioAssay assay1 = BioAssay.Factory.newInstance();
        assay1.setName( "Current Smoker 73" );

        /* set up the biomaterials */
        Collection<BioMaterial> samplesUsed1 = new LinkedHashSet<BioMaterial>();
        BioMaterial sample1 = BioMaterial.Factory.newInstance();
        sample1.setName( "GSM15697" );
        samplesUsed1.add( sample1 );

        assay1.setSamplesUsed( samplesUsed1 );

        assays.add( assay1 );

        BioAssay assay2 = BioAssay.Factory.newInstance();
        assay2.setName( "Former Smoker 34" );

        Collection<BioMaterial> samplesUsed2 = new LinkedHashSet<BioMaterial>();
        BioMaterial sample2 = BioMaterial.Factory.newInstance();
        sample2.setName( "GSM15744" );
        samplesUsed2.add( sample2 );

        assay2.setSamplesUsed( samplesUsed2 );

        assays.add( assay2 );

        bioAssayDimension.setBioAssays( assays );

        /* set up the design element data vectors */
        Collection<DesignElementDataVector> vectors1 = new LinkedHashSet<DesignElementDataVector>();
        DesignElementDataVector vector1 = DesignElementDataVector.Factory.newInstance();
        double[] ddata1 = { 74.9, 101.7 };
        byte[] bdata1 = bac.doubleArrayToBytes( ddata1 );
        vector1.setData( bdata1 );
        vector1.setQuantitationType( qt );
        vector1.setBioAssayDimension( bioAssayDimension );
        vectors1.add( vector1 );

        Collection<DesignElementDataVector> vectors2 = new LinkedHashSet<DesignElementDataVector>();
        DesignElementDataVector vector2 = DesignElementDataVector.Factory.newInstance();
        double[] ddata2 = { 404.6, 318.7 };
        byte[] bdata2 = bac.doubleArrayToBytes( ddata2 );
        vector2.setData( bdata2 );
        vector2.setQuantitationType( qt );
        vector2.setBioAssayDimension( bioAssayDimension );
        vectors2.add( vector2 );

        /* set up the design elements */
        Collection<DesignElement> designElements = new LinkedHashSet<DesignElement>();

        DesignElement de1 = CompositeSequence.Factory.newInstance();
        de1.setName( "218120_s_at" );
        vector1.setDesignElement( de1 ); // set this de on the vector
        // de1.setDesignElementDataVectors( vectors1 );

        DesignElement de2 = CompositeSequence.Factory.newInstance();
        de2.setName( "121_at" );
        vector2.setDesignElement( de2 ); // set this de on the vector
        // de2.setDesignElementDataVectors( vectors2 );

        designElements.add( de1 );
        designElements.add( de2 );

        Collection<DesignElementDataVector> eeVectors = new LinkedHashSet<DesignElementDataVector>();
        eeVectors.add( vector1 );
        eeVectors.add( vector2 );

        /* set the vectors on the expression experiment */
        ee.setDesignElementDataVectors( eeVectors );

        expressionDataMatrix = new ExpressionDataDoubleMatrix( eeVectors );
    }

    /**
     * Tests normalization of the matrix.
     */
    public void testNormalizeExpressionDataDoubleMatrixByRowMean() {

        ExpressionDataMatrixVisualizationService expressionDataMatrixVisualizationService = ( ExpressionDataMatrixVisualizationService ) this
                .getBean( "expressionDataMatrixVisualizationService" );

        ExpressionDataMatrix normalizedExpressonDataMatrix = expressionDataMatrixVisualizationService
                .standardizeExpressionDataDoubleMatrix( expressionDataMatrix, null );

        assertEquals( normalizedExpressonDataMatrix.columns(), expressionDataMatrix.columns() );

    }

    /**
     * Tests creating the heatmap.
     */
    public void testCreateHeatMap() {

        ExpressionDataMatrixVisualizationService expressionDataMatrixVisualizationService = ( ExpressionDataMatrixVisualizationService ) this
                .getBean( "expressionDataMatrixVisualizationService" );

        MatrixDisplay display = expressionDataMatrixVisualizationService.createHeatMap( expressionDataMatrix );

        assertNotNull( display );

    }
}
