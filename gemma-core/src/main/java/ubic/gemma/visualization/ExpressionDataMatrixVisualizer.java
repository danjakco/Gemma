/*
 * The Gemma project
 * 
 * Copyright (c) 2006 Columbia University
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

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix2DNamed;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.gui.ColorMap;
import ubic.basecode.gui.ColorMatrix;
import ubic.basecode.gui.JMatrixDisplay;
import ubic.basecode.io.ByteArrayConverter;
import ubic.gemma.datastructure.matrix.ExpressionDataMatrix;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.designElement.DesignElement;

/**
 * A value object to visualize the ExpressionDataMatrix
 * 
 * @author keshav
 * @version $Id$
 */
public class ExpressionDataMatrixVisualizer implements MatrixVisualizer, Serializable {
    private Log log = LogFactory.getLog( this.getClass() );

    private static final long serialVersionUID = -5075323948059345296L;

    // private ExpressionDataMatrix expressionDataMatrix = null;

    // private String outfile = "visualization.png";

    private ColorMatrix colorMatrix = null;

    private List<String> rowLabels = null;

    private List<String> colLabels = null;

    private boolean suppressVisualizations;

    private Color[] colorMap = ColorMap.REDGREEN_COLORMAP;

    // /*
    // * (non-Javadoc)
    // *
    // * @see ubic.gemma.visualization.MatrixVisualizer#createVisualization()
    // */
    // public void createVisualization() {
    // createVisualization( this.expressionDataMatrix );
    // }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#createVisualization(ubic.gemma.visualization.MatrixVisualizationData)
     */
    public void createVisualization( ExpressionDataMatrix expressionDataMatrix ) {

        if ( expressionDataMatrix == null || expressionDataMatrix.getDesignElements() == null ) {
            throw new IllegalArgumentException( "ExpressionDataMatrix apparently has no data" );
        }

        Collection<DesignElement> deCol = expressionDataMatrix.getDesignElements();

        ByteArrayConverter byteArrayConverter = new ByteArrayConverter();
        double[][] data = new double[deCol.size()][];
        int i = 0;
        for ( DesignElement designElement : deCol ) {
            Collection<DesignElementDataVector> vectors = ( ( CompositeSequence ) designElement )
                    .getDesignElementDataVectors();
            Iterator iter = vectors.iterator();
            DesignElementDataVector vector = ( DesignElementDataVector ) iter.next();

            data[i] = byteArrayConverter.byteArrayToDoubles( vector.getData() );

            if ( rowLabels == null ) {
                log.debug( "Setting row names" );
                rowLabels = new ArrayList<String>();
            }
            // log.debug( designElement.getName() );
            rowLabels.add( designElement.getName() );
            i++;
        }

        if ( colLabels == null ) {
            log.warn( "Column labels not set.  Using defaults" );
            colLabels = new ArrayList<String>();
            for ( int j = 0; j < data[0].length; j++ ) {
                colLabels.add( String.valueOf( j ) );
            }
        }
        createVisualization( data );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#createVisualization(double[][])
     */
    public void createVisualization( double[][] data ) {
        assert rowLabels != null && colLabels != null : "Labels not set";

        DoubleMatrixNamed matrix = new DenseDoubleMatrix2DNamed( data );
        matrix.setRowNames( rowLabels );
        matrix.setColumnNames( colLabels );
        colorMatrix = new ColorMatrix( matrix );
    }

    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see ubic.gemma.visualization.MatrixVisualizer#getOutfile()
    // */
    // public String getOutfile() {
    // return outfile;
    // }

    // /*
    // * (non-Javadoc)
    // *
    // * @see ubic.gemma.visualization.MatrixVisualizer#setOutfile(java.lang.String)
    // */
    // public void setOutfile( String outfile ) {
    // this.outfile = outfile;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#saveImage(java.io.File)
     */
    public void saveImage( File outFile ) throws IOException {
        this.saveImage( outFile.getAbsolutePath() );
    }

    /**
     * @param outfile
     * @throws IOException
     */
    private void saveImage( String outfile ) throws IOException {
        // if ( outfile != null ) this.outfile = outfile;

        JMatrixDisplay display = new JMatrixDisplay( colorMatrix );

        display.setCellSize( new Dimension( 16, 16 ) );
        display.saveImage( outfile );
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see ubic.gemma.visualization.MatrixVisualizer#getExpressionDataMatrix()
    // */
    // public ExpressionDataMatrix getExpressionDataMatrix() {
    // return expressionDataMatrix;
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // ubic.gemma.visualization.MatrixVisualizer#setExpressionDataMatrix(ubic.gemma.visualization.ExpressionDataMatrix)
    // */
    // public void setExpressionDataMatrix( ExpressionDataMatrix expressionDataMatrix ) {
    // this.expressionDataMatrix = expressionDataMatrix;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#isSuppressVisualizations()
     */
    public boolean isSuppressVisualizations() {
        return suppressVisualizations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#setSuppressVisualizations(boolean)
     */
    public void setSuppressVisualizations( boolean suppressVisualizations ) {
        this.suppressVisualizations = suppressVisualizations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#getRowLabels()
     */
    public List<String> getRowLabels() {
        return rowLabels;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#setRowLabels(java.util.List)
     */
    public void setRowLabels( List<String> rowLabels ) {
        this.rowLabels = rowLabels;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#getColLabels()
     */
    public List<String> getColLabels() {
        return colLabels;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#setColLabels(java.util.List)
     */
    public void setColLabels( List<String> colNames ) {
        this.colLabels = colNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#getColorMap()
     */
    public Color[] getColorMap() {
        return colorMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.visualization.MatrixVisualizer#setColorMap(null[])
     */
    public void setColorMap( Color[] colorMap ) {
        this.colorMap = colorMap;
    }

    /**
     * Draw the dynamic image and write to stream
     * 
     * @param stream
     * @return String
     * @throws IOException
     */
    public String drawDynamicImage( OutputStream stream ) throws IOException {
        // TODO move me to another implementation of MatrixVisualizer
        log.warn( "drawing dynamic image" );

        ExpressionDataMatrixProducerImpl producer = new ExpressionDataMatrixProducerImpl();
        producer.setColorMatrix( this.colorMatrix );

        String type = producer.createDynamicImage( stream, true, true );

        log.debug( "returning content type " + type );

        return type;
    }

    /**
     * @return ColorMatrix
     */
    public ColorMatrix getColorMatrix() {
        return colorMatrix;
    }
}
