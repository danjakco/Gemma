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

import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.designElement.DesignElement;

/**
 * @author pavlidis
 * @version $Id$
 */
public class ExpressionDataDoubleMatrixUtil {

    private static final double LOGARITHM_BASE = 2.0;

    /**
     * Subtract two conformant matrices. The rows and columns do not have to be in the same order, but they do have to
     * have the same row and column keys. The result is stored in a.
     * 
     * @param a
     * @param b
     */
    public static void subtractMatrices( ExpressionDataDoubleMatrix a, ExpressionDataDoubleMatrix b ) {
        checkConformant( a, b );
        int columns = a.columns();
        for ( DesignElement el : a.getRowElements() ) {
            for ( int i = 0; i < columns; i++ ) {
                BioMaterial bm = a.getBioMaterialForColumn( i );
                double valA = a.get( el, bm );
                double valB = b.get( el, bm );
                a.set( el, bm, valA - valB );
            }
        }
    }

    /**
     * Log-transform the values in the matrix (base 2). Non-positive values (which have no logarithm defined) are
     * entered as NaN.
     * 
     * @param matrix
     */
    public static void logTransformMatrix( ExpressionDataDoubleMatrix matrix ) {
        int columns = matrix.columns();
        double log2 = Math.log( LOGARITHM_BASE );
        for ( DesignElement el : matrix.getRowElements() ) {
            for ( int i = 0; i < columns; i++ ) {
                BioMaterial bm = matrix.getBioMaterialForColumn( i );
                double valA = matrix.get( el, bm );
                if ( valA <= 0 ) {
                    matrix.set( el, bm, Double.NaN );
                } else {
                    matrix.set( el, bm, Math.log( valA ) / log2 );
                }
            }
        }

    }

    /**
     * Add two conformant matrices. The rows and columns do not have to be in the same order, but they do have to have
     * the same row and column keys. The result is stored in a.
     * 
     * @param a
     * @param b
     */
    public static void addMatrices( ExpressionDataDoubleMatrix a, ExpressionDataDoubleMatrix b ) {
        checkConformant( a, b );
        int columns = a.columns();
        for ( DesignElement el : a.getRowElements() ) {
            for ( int i = 0; i < columns; i++ ) {
                BioMaterial bm = a.getBioMaterialForColumn( i );
                double valA = a.get( el, bm );
                double valB = b.get( el, bm );
                a.set( el, bm, valA + valB );
            }
        }
    }

    private static void checkConformant( ExpressionDataMatrix a, ExpressionDataMatrix b ) {
        if ( a.rows() != b.rows() ) throw new IllegalArgumentException( "Unequal row counts" );
        if ( a.columns() != b.columns() )
            throw new IllegalArgumentException( "Unequal column counts: " + a.columns() + " != " + b.columns() );
    }

    /**
     * Divide all values by the dividend
     * 
     * @param matrix
     * @param dividend
     * @throws IllegalArgumentException if dividend == 0.
     */
    public static void scalarDivideMatrix( ExpressionDataDoubleMatrix matrix, double dividend ) {
        if ( dividend == 0 ) throw new IllegalArgumentException( "Can't divide by zero" );
        int columns = matrix.columns();
        for ( DesignElement el : matrix.getRowElements() ) {
            for ( int i = 0; i < columns; i++ ) {
                BioMaterial bm = matrix.getBioMaterialForColumn( i );
                double valA = matrix.get( el, bm );
                matrix.set( el, bm, valA / dividend );

            }
        }
    }

    /**
     * Use the mask matrix to turn some values in a conformant matrix to NaN. The rows and columns do not have to be in
     * the same order, but they do have to have the same row and column keys. The result is stored in a.
     * 
     * @param matrix
     * @param mask
     */
    public static void maskMatrix( ExpressionDataDoubleMatrix matrix, ExpressionDataBooleanMatrix mask ) {
        checkConformant( matrix, mask );
        int columns = matrix.columns();
        for ( DesignElement el : matrix.getRowElements() ) {
            for ( int i = 0; i < columns; i++ ) {
                BioMaterial bm = matrix.getBioMaterialForColumn( i );
                boolean present = mask.get( el, bm );
                if ( !present ) {
                    matrix.set( el, bm, Double.NaN );
                }

            }
        }
    }

}
