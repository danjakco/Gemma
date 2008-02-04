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
package ubic.gemma.web.controller.expression.experiment;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.web.servlet.ModelAndView;

import ubic.gemma.analysis.stats.ExpressionDataSampleCorrelation;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.util.ConfigUtils;
import ubic.gemma.web.controller.BaseMultiActionController;

/**
 * @spring.bean id="expressionExperimentQCController"
 * @spring.property name="methodNameResolver" ref="expressionExperimentQCActions"
 * @spring.property ref="expressionExperimentService" name="expressionExperimentService"
 * @author paul
 * @version $Id$
 */
public class ExpressionExperimentQCController extends BaseMultiActionController {

    private static final int HISTOGRAM_IMAGE_SIZE = 200;
    private ExpressionExperimentService expressionExperimentService;

    public void setExpressionExperimentService( ExpressionExperimentService expressionExperimentService ) {
        this.expressionExperimentService = expressionExperimentService;
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public ModelAndView showCorrMat( HttpServletRequest request, HttpServletResponse response ) throws Exception {
        String id = request.getParameter( "id" );
        String size = request.getParameter( "size" ); // okay if null
        String contrast = request.getParameter( "contr" ); // okay if null, default is 'hi'

        if ( id == null ) {
            log.warn( "No id!" );
            return null;
        }

        String contrVal = "hi";
        if ( StringUtils.isNotBlank( contrast ) ) {
            contrVal = contrast;
        }

        Long idl;
        try {
            idl = Long.parseLong( id );
        } catch ( NumberFormatException e ) {
            log.warn( "Invalid id: " + id );
            return null;
        }
        assert idl != null;

        ExpressionExperiment ee = expressionExperimentService.load( idl );
        if ( ee == null ) {
            log.warn( "No such experiment with id " + idl );
            return null;
        }

        writeCorrMatImage( response, ee, size, contrVal );

        return null; // nothing to return;
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public ModelAndView showProbeCorrDist( HttpServletRequest request, HttpServletResponse response ) throws Exception {
        String id = request.getParameter( "id" );

        if ( id == null ) {
            log.warn( "No id!" );
            return null;
        }
        Long idl;
        try {
            idl = Long.parseLong( id );
        } catch ( NumberFormatException e ) {
            log.warn( "Invalid id: " + id );
            return null;
        }
        assert idl != null;

        ExpressionExperiment ee = expressionExperimentService.load( idl );
        if ( ee == null ) {
            log.warn( "No such experiment with id " + idl );
            return null;
        }

        writeProbeCorrHistImage( response, ee );
        return null; // nothing to return;
    }

    /**
     * @param ee
     * @param size 'large' or 'small'.
     * @param contrast
     * @return
     */
    private File locateCorrMatImageFile( ExpressionExperiment ee, String size, String contrast ) {
        // locate the image.
        String shortName = ee.getShortName();
        String analysisStoragePath = ConfigUtils.getAnalysisStoragePath() + File.separatorChar
                + ExpressionDataSampleCorrelation.CORRMAT_DIR_NAME;

        String suffix;
        if ( contrast.equalsIgnoreCase( "hi" ) ) {
            suffix = ExpressionDataSampleCorrelation.SMALL_HIGHCONTRAST;
        } else {
            suffix = ExpressionDataSampleCorrelation.SMALL_LOWCONTRAST;
        }

        if ( size != null && size.equals( "large" ) ) {
            if ( contrast.equalsIgnoreCase( "hi" ) ) {
                suffix = ExpressionDataSampleCorrelation.LARGE_HIGHCONTRAST;
            } else {
                suffix = ExpressionDataSampleCorrelation.LARGE_LOWCONTRAST;
            }
        }

        File f = new File( analysisStoragePath + File.separatorChar + shortName + "_corrmat" + suffix );
        return f;
    }

    /**
     * @param ee
     * @return
     */
    private File locateProbeCorrFile( ExpressionExperiment ee ) {
        // locate the image.
        String shortName = ee.getShortName();
        String analysisStoragePath = ConfigUtils.getAnalysisStoragePath();

        String suffix = ".correlDist.txt";
        File f = new File( analysisStoragePath + File.separatorChar + shortName + suffix );
        return f;
    }

    /**
     * @param response
     * @param ee
     * @param size
     */
    private void writeCorrMatImage( HttpServletResponse response, ExpressionExperiment ee, String size, String contrast ) {
        File f = locateCorrMatImageFile( ee, size, contrast );

        writeImage( response, f );
    }

    /**
     * Write an image from a file to the user's browser FIXME move this.
     * 
     * @param response
     * @param f
     */
    private void writeImage( HttpServletResponse response, File f ) {
        if ( !f.canRead() ) {
            log.warn( "Could not locate the correlation matrix image" );
            return;
        }

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream( f );
            out = response.getOutputStream();
            response.setContentType( "image/png" );
            byte[] buf = new byte[1024];
            int len;
            while ( ( len = in.read( buf ) ) > 0 ) {
                out.write( buf, 0, len );
            }
            in.close();
        } catch ( IOException e ) {
            log.error( "While writing image", e );
        } finally {
            if ( out != null ) {
                try {
                    out.close();
                } catch ( IOException e ) {
                    log.warn( "Problems closing output stream.  Issues were: " + e.toString() );
                }
            }
        }
    }

    /**
     * @param response
     * @param ee
     */
    private void writeProbeCorrHistImage( HttpServletResponse response, ExpressionExperiment ee ) throws IOException {
        XYSeries series = getCorrelHist( ee );

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries( series );
        JFreeChart chart = ChartFactory.createXYLineChart( "", "Correlation", "Frequency", xySeriesCollection,
                PlotOrientation.VERTICAL, false, false, false );

        OutputStream out = null;
        try {
            out = response.getOutputStream();
            ChartRenderingInfo info = new ChartRenderingInfo();
            chart.setBackgroundPaint( Color.white );
            ChartUtilities.writeChartAsPNG( out, chart, ( int ) ( HISTOGRAM_IMAGE_SIZE * 1.4 ), HISTOGRAM_IMAGE_SIZE,
                    info );
        } catch ( IOException e ) {
            log.error( "While writing image", e );
        } finally {
            if ( out != null ) {
                try {
                    out.close();
                } catch ( IOException e ) {
                    log.warn( "Problems closing output stream.  Issues were: " + e.toString() );
                }
            }
        }
    }

    /**
     * @param ee
     * @return JFreeChart XYSeries representing the histogram.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private XYSeries getCorrelHist( ExpressionExperiment ee ) throws FileNotFoundException, IOException {
        File f = this.locateProbeCorrFile( ee );

        XYSeries series = new XYSeries( ee.getId(), true, true );
        BufferedReader in = new BufferedReader( new FileReader( f ) );
        while ( in.ready() ) {
            String line = in.readLine().trim();
            if ( line.startsWith( "#" ) ) continue;
            String[] split = StringUtils.split( line );
            if ( split.length < 2 ) continue;
            try {
                double x = Double.parseDouble( split[0] );
                double y = Double.parseDouble( split[1] );
                series.add( x, y );
            } catch ( NumberFormatException e ) {
                // line wasn't useable.. no big deal. Heading is included.
            }
        }
        return series;
    }
}
