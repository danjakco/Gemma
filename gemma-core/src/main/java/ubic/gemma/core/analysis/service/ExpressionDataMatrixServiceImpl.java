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
package ubic.gemma.core.analysis.service;

import cern.colt.list.DoubleArrayList;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.math.DescriptiveWithMissing;
import ubic.gemma.core.analysis.preprocess.filter.ExpressionExperimentFilter;
import ubic.gemma.core.analysis.preprocess.filter.FilterConfig;
import ubic.gemma.core.datastructure.matrix.ExpressionDataDoubleMatrix;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.bioAssayData.ProcessedExpressionDataVector;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.persistence.service.expression.arrayDesign.ArrayDesignService;
import ubic.gemma.persistence.service.expression.bioAssayData.ProcessedExpressionDataVectorDao;
import ubic.gemma.persistence.service.expression.bioAssayData.ProcessedExpressionDataVectorService;
import ubic.gemma.persistence.service.expression.experiment.ExpressionExperimentService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Tools for easily getting data matrices for analysis in a consistent way.
 *
 * @author keshav
 */
@Component
public class ExpressionDataMatrixServiceImpl implements ExpressionDataMatrixService {

    @Autowired
    private ExpressionExperimentService expressionExperimentService;

    @Autowired
    private ProcessedExpressionDataVectorService processedExpressionDataVectorService;

    @Autowired
    private ArrayDesignService arrayDesignService;

    @Override
    public ExpressionDataDoubleMatrix getFilteredMatrix( ExpressionExperiment ee, FilterConfig filterConfig ) {
        Collection<ProcessedExpressionDataVector> dataVectors = processedExpressionDataVectorService
                .getProcessedDataVectors( ee );
        return this.getFilteredMatrix( ee, filterConfig, dataVectors );
    }

    @Override
    public ExpressionDataDoubleMatrix getFilteredMatrix( ExpressionExperiment ee, FilterConfig filterConfig,
            Collection<ProcessedExpressionDataVector> dataVectors ) {
        Collection<ArrayDesign> arrayDesignsUsed = expressionExperimentService.getArrayDesignsUsed( ee );
        return this.getFilteredMatrix( filterConfig, dataVectors, arrayDesignsUsed );
    }

    @Override
    public ExpressionDataDoubleMatrix getFilteredMatrix( String arrayDesignName, FilterConfig filterConfig,
            Collection<ProcessedExpressionDataVector> dataVectors ) {
        ArrayDesign ad = arrayDesignService.findByShortName( arrayDesignName );
        if ( ad == null ) {
            throw new IllegalArgumentException( "No platform named '" + arrayDesignName + "'" );
        }
        Collection<ArrayDesign> arrayDesignsUsed = new HashSet<>();
        arrayDesignsUsed.add( ad );
        return this.getFilteredMatrix( filterConfig, dataVectors, arrayDesignsUsed );
    }

    @Override
    public ExpressionDataDoubleMatrix getProcessedExpressionDataMatrix( ExpressionExperiment ee ) {
        Collection<ProcessedExpressionDataVector> dataVectors = this.processedExpressionDataVectorService
                .getProcessedDataVectors( ee );
        if ( dataVectors.isEmpty() ) {
            throw new IllegalArgumentException(
                    "There are no ProcessedExpressionDataVectors for " + ee + ", they must be created first" );
        }
        this.processedExpressionDataVectorService.thaw( dataVectors );
        return new ExpressionDataDoubleMatrix( dataVectors );
    }

    @Override
    public DoubleMatrix<Gene, ExpressionExperiment> getRankMatrix( Collection<Gene> genes,
            Collection<ExpressionExperiment> ees, ProcessedExpressionDataVectorDao.RankMethod method ) {

        DoubleMatrix<Gene, ExpressionExperiment> matrix = new DenseDoubleMatrix<>( genes.size(), ees.size() );

        Map<ExpressionExperiment, Map<Gene, Collection<Double>>> ranks = processedExpressionDataVectorService
                .getRanks( ees, genes, method );

        matrix.setRowNames( new ArrayList<>( genes ) );
        matrix.setColumnNames( new ArrayList<>( ees ) );
        for ( int i = 0; i < matrix.rows(); i++ ) {
            for ( int j = 0; j < matrix.columns(); j++ ) {
                matrix.setByKeys( matrix.getRowName( i ), matrix.getColName( j ), Double.NaN );
            }
        }

        for ( Gene g : matrix.getRowNames() ) {
            for ( ExpressionExperiment e : matrix.getColNames() ) {
                if ( ranks.containsKey( e ) ) {
                    Collection<Double> r = ranks.get( e ).get( g );

                    if ( r == null ) {
                        continue;
                    }

                    Double[] ar = r.toArray( new Double[r.size()] );

                    // compute median of collection.
                    double[] dar = ArrayUtils.toPrimitive( ar );
                    double medianRank = DescriptiveWithMissing.median( new DoubleArrayList( dar ) );
                    matrix.setByKeys( g, e, medianRank );

                }
            }
        }

        return matrix;
    }

    private ExpressionDataDoubleMatrix getFilteredMatrix( FilterConfig filterConfig,
            Collection<ProcessedExpressionDataVector> dataVectors, Collection<ArrayDesign> arrayDesignsUsed ) {
        if ( dataVectors == null || dataVectors.isEmpty() )
            throw new IllegalArgumentException( "Vectors must be provided" );
        ExpressionExperimentFilter filter = new ExpressionExperimentFilter( arrayDesignsUsed, filterConfig );
        this.processedExpressionDataVectorService.thaw( dataVectors );
        return filter.getFilteredMatrix( dataVectors );
    }

}
