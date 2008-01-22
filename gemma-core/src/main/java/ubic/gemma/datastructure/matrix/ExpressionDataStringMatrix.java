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
package ubic.gemma.datastructure.matrix;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.StringMatrix2DNamed;
import ubic.basecode.io.ByteArrayConverter;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.designElement.DesignElement;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * @author pavlidis
 * @version $Id$
 */
public class ExpressionDataStringMatrix extends BaseExpressionDataMatrix {

    private static final long serialVersionUID = 1L;

    private static Log log = LogFactory.getLog( ExpressionDataStringMatrix.class.getName() );

    private StringMatrix2DNamed matrix;

    public ExpressionDataStringMatrix( ExpressionExperiment expressionExperiment, QuantitationType quantitationType ) {

    }

    public ExpressionDataStringMatrix( ExpressionExperiment expressionExperiment,
            Collection<DesignElement> designElements, QuantitationType quantitationType ) {

    }

    public ExpressionDataStringMatrix( Collection<DesignElementDataVector> dataVectors,
            QuantitationType quantitationType ) {

    }

    public ExpressionDataStringMatrix( Collection<DesignElementDataVector> vectors ) {
        init();
        selectVectors( vectors );
        vectorsToMatrix( vectors );
    }

    public String get( DesignElement designElement, BioAssay bioAssay ) {
        int i = this.rowElementMap.get( designElement );
        int j = this.columnAssayMap.get( bioAssay );
        return ( String ) this.matrix.get( i, j );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.datastructure.matrix.ExpressionDataMatrix#get(java.util.List, java.util.List)
     */
    public String[][] get( List designElements, List bioAssays ) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.datastructure.matrix.ExpressionDataMatrix#getColumn(ubic.gemma.model.expression.bioAssay.BioAssay)
     */
    public String[] getColumn( BioAssay bioAssay ) {
        int index = this.columnAssayMap.get( bioAssay );
        return this.getColumn( index );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.datastructure.matrix.ExpressionDataMatrix#getColumn(java.lang.Integer)
     */
    public String[] getColumn( Integer index ) {
        return this.matrix.getColumn( index );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.datastructure.matrix.ExpressionDataMatrix#getColumns(java.util.List)
     */
    public String[][] getColumns( List bioAssays ) {
        String[][] res = new String[bioAssays.size()][];
        for ( int i = 0; i < bioAssays.size(); i++ ) {
            res[i] = this.getColumn( ( BioAssay ) bioAssays.get( i ) );
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.datastructure.matrix.ExpressionDataMatrix#getMatrix()
     */
    public String[][] getMatrix() {
        String[][] res = new String[rows()][];
        for ( int i = 0; i < rows(); i++ ) {
            res[i] = ( String[] ) this.matrix.getRow( i );
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.datastructure.matrix.ExpressionDataMatrix#getRow(ubic.gemma.model.expression.designElement.DesignElement)
     */
    public String[] getRow( DesignElement designElement ) {
        return ( String[] ) this.matrix.getRow( this.getRowIndex( designElement ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.datastructure.matrix.ExpressionDataMatrix#getRows(java.util.List)
     */
    public String[][] getRows( List designElements ) {
        String[][] res = new String[rows()][];
        for ( int i = 0; i < designElements.size(); i++ ) {
            res[i] = ( String[] ) this.matrix.getRow( this.getRowIndex( ( DesignElement ) designElements.get( i ) ) );
        }
        return res;
    }

    @Override
    protected void vectorsToMatrix( Collection<DesignElementDataVector> vectors ) {
        if ( vectors == null || vectors.size() == 0 ) {
            throw new IllegalArgumentException( "No vectors!" );
        }

        int maxSize = setUpColumnElements();

        this.matrix = createMatrix( vectors, maxSize );
    }

    private StringMatrix2DNamed createMatrix( Collection<DesignElementDataVector> vectors, int maxSize ) {

        int numRows = this.rowDesignElementMapByInteger.keySet().size();

        StringMatrix2DNamed<Integer, Integer> matrix = new StringMatrix2DNamed<Integer, Integer>( numRows, maxSize );

        for ( int j = 0; j < matrix.columns(); j++ ) {
            matrix.addColumnName( j );
        }

        // initialize the matrix to "";
        for ( int i = 0; i < matrix.rows(); i++ ) {
            for ( int j = 0; j < matrix.columns(); j++ ) {
                matrix.setQuick( i, j, "" );
            }
        }

        ByteArrayConverter bac = new ByteArrayConverter();
        for ( DesignElementDataVector vector : vectors ) {

            DesignElement designElement = vector.getDesignElement();
            assert designElement != null : "No designelement for " + vector;

            Integer rowIndex = this.rowElementMap.get( designElement );
            assert rowIndex != null;

            matrix.addRowName( rowIndex );

            byte[] bytes = vector.getData();
            String[] vals = bac.byteArrayToStrings( bytes );

            BioAssayDimension dimension = vector.getBioAssayDimension();
            Collection<BioAssay> bioAssays = dimension.getBioAssays();
            assert bioAssays.size() == vals.length : "Expected " + vals.length + " got " + bioAssays.size();

            Iterator it = bioAssays.iterator();

            for ( int j = 0; j < bioAssays.size(); j++ ) {

                BioAssay bioAssay = ( BioAssay ) it.next();
                Integer column = this.columnAssayMap.get( bioAssay );

                assert column != null;

                matrix.setQuick( rowIndex, column, vals[j] );
            }

        }

        log.debug( "Created a " + matrix.rows() + " x " + matrix.columns() + " matrix" );
        return matrix;
    }

    public int columns() {
        return matrix.columns();
    }

    public int rows() {
        return matrix.rows();
    }

    public void set( int row, int column, Object value ) {
        matrix.setQuick( row, column, value );
    }

    public Object get( int row, int column ) {
        return matrix.get( row, column );
    }

    public Object[] getRow( Integer index ) {
        return matrix.getRow( index );
    }

}
