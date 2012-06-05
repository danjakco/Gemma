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
package ubic.gemma.apps;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;

import ubic.gemma.analysis.preprocess.ProcessedExpressionDataVectorCreateService;
import ubic.gemma.analysis.preprocess.SampleCoexpressionMatrixService;
import ubic.gemma.analysis.preprocess.TwoChannelMissingValues;
import ubic.gemma.expression.experiment.service.ExpressionExperimentService;
import ubic.gemma.loader.expression.arrayExpress.ArrayExpressLoadService;
import ubic.gemma.loader.expression.geo.GeoDomainObjectGenerator;
import ubic.gemma.loader.expression.geo.service.GeoService;
import ubic.gemma.model.common.Describable;
import ubic.gemma.model.common.description.DatabaseEntry;
import ubic.gemma.model.common.description.ExternalDatabase;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.arrayDesign.ArrayDesignService;
import ubic.gemma.model.expression.arrayDesign.TechnologyType;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.util.AbstractSpringAwareCLI;

/**
 * Simple command line to load expression experiments, either singly or in batches defined on the command line or in a
 * file.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class LoadExpressionDataCli extends AbstractSpringAwareCLI {

    private enum Formats {
        AE, GEO
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {
        LoadExpressionDataCli p = new LoadExpressionDataCli();
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            Exception ex = p.doWork( args );
            if ( ex != null ) {
                ex.printStackTrace();
            }
            watch.stop();
            log.info( watch.getTime() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    // Command line Options
    protected String accessionFile = null;
    protected String accessions = null;
    protected boolean platformOnly = false;
    protected boolean doMatching = true;
    protected boolean force = false;
    protected String fileFormat = Formats.GEO.toString();
    protected String adName = "none";
    protected boolean aggressive;

    // Service Beans
    private ExpressionExperimentService eeService;
    private ArrayDesignService adService;
    private ProcessedExpressionDataVectorCreateService processedExpressionDataVectorCreateService;
    private TwoChannelMissingValues tcmv;

    private boolean splitByPlatform = false;
    private boolean allowSuperSeriesLoad = false;
    private boolean allowSubSeriesLoad = false;
    private boolean suppressPostProcessing = false;

    @Autowired
    private SampleCoexpressionMatrixService sampleCoexpressionMatrixService;

    @Override
    public String getShortDesc() {
        return "Load data from GEO or ArrayExpress";
    }

    /**
     * Do missing value and processed vector creation steps.
     * 
     * @param ees
     */
    private void postProcess( Collection<ExpressionExperiment> ees ) {
        log.info( "Postprocessing ..." );
        for ( ExpressionExperiment ee : ees ) {

            postProcess( ee );
        }
    }

    /**
     * @param ee
     */
    private void postProcess( ExpressionExperiment ee ) {
        Collection<ArrayDesign> arrayDesignsUsed = eeService.getArrayDesignsUsed( ee );
        if ( arrayDesignsUsed.size() > 1 ) {
            log.warn( "Skipping postprocessing because experiment uses "
                    + "multiple array types. Please check valid entry and run postprocessing separately." );
        }

        ArrayDesign arrayDesignUsed = arrayDesignsUsed.iterator().next();
        processForMissingValues( ee, arrayDesignUsed );

        processedExpressionDataVectorCreateService.computeProcessedExpressionData( ee );

        sampleCoexpressionMatrixService.findOrCreate( ee );
    }

    /**
     * @param ee
     * @return
     */
    private boolean processForMissingValues( ExpressionExperiment ee, ArrayDesign design ) {

        boolean wasProcessed = false;

        TechnologyType tt = design.getTechnologyType();
        if ( tt == TechnologyType.TWOCOLOR || tt == TechnologyType.DUALMODE ) {
            log.info( ee + " uses a two-color array design, processing for missing values ..." );
            ee = eeService.thawLite( ee );
            tcmv.computeMissingValues( ee );
            wasProcessed = true;
        }

        return wasProcessed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.util.AbstractCLI#buildOptions()
     */
    @SuppressWarnings("static-access")
    @Override
    protected void buildOptions() {
        Option fileOption = OptionBuilder.hasArg().withArgName( "Input file" )
                .withDescription( "Optional path to file with list of experiment accessions to load" )
                .withLongOpt( "file" ).create( 'f' );

        addOption( fileOption );

        Option accessionOption = OptionBuilder.hasArg().withArgName( "Accession(s)" )
                .withDescription( "Optional comma-delimited list of accessions (GSE or GDS) to load" )
                .withLongOpt( "acc" ).create( 'e' );
        addOption( accessionOption );

        Option platformOnlyOption = OptionBuilder.withArgName( "Platforms only" )
                .withDescription( "Load platforms (array designs) only" ).withLongOpt( "platforms" ).create( 'y' );
        addOption( platformOnlyOption );

        Option noBioAssayMatching = OptionBuilder.withDescription( "Do not try to match samples across platforms" )
                .withLongOpt( "nomatch" ).create( 'n' );

        addOption( noBioAssayMatching );

        Option splitByPlatformOption = OptionBuilder.withDescription(
                "Force data from each platform into a separate experiment. This implies '-nomatch'" ).create(
                "splitByPlatform" );
        addOption( splitByPlatformOption );

        Option forceOption = OptionBuilder.withDescription( "Reload data set if it already exists in system" )
                .withLongOpt( "force" ).create( "force" );
        addOption( forceOption );
        Option aggressiveQtRemoval = OptionBuilder
                .withDescription( "Aggressively remove all unneeded quantitation types" ).withLongOpt( "aggressive" )
                .create( "aggressive" );

        addOption( aggressiveQtRemoval );

        Option fileFormatOpt = OptionBuilder
                .hasArg()
                .withArgName( "File Format" )
                .withDescription(
                        "Either AE or GEO; defaults to GEO (using batch file does not work with Array Express)" )
                .withLongOpt( "format" ).create( 'm' );

        addOption( fileFormatOpt );

        Option arrayDesign = OptionBuilder.hasArg().withArgName( "array design name" )
                .withDescription( "Specify the name or short name of the platform the experiment uses (AE only)" )
                .withLongOpt( "array" ).create( 'a' );

        addOption( arrayDesign );

        addOption( OptionBuilder.withDescription( "Suppress postprocessing steps" ).create( "nopost" ) );

        /*
         * add 'allowsub/super' series option;
         */
        addOption( OptionBuilder.withDescription( "Allow sub/super series to be loaded" ).create( "allowsuper" ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.util.AbstractCLI#doWork(java.lang.String[])
     */
    @Override
    protected Exception doWork( String[] args ) {
        Exception err = processCommandLine( "Expression Data loader", args );
        if ( err != null ) {
            return err;
        }
        try {

            GeoService geoService = this.getBean( GeoService.class );
            geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGenerator() );

            ArrayExpressLoadService aeService = this.getBean( ArrayExpressLoadService.class );

            if ( accessions == null && accessionFile == null ) {
                return new IllegalArgumentException(
                        "You must specific either a file or accessions on the command line" );
            }

            Boolean aeFlag = false;
            ArrayDesign ad;
            if ( StringUtils.equalsIgnoreCase( Formats.AE.toString(), fileFormat ) ) {

                if ( platformOnly )
                    return new IllegalArgumentException( "Loading 'platform only' not supported for Array Express. " );

                if ( accessionFile != null )
                    return new IllegalArgumentException(
                            "Batch loading via text file not supported for Array Express file formats. " );

                ad = adService.findByShortName( this.adName );
                if ( ad == null ) ad = adService.findByName( this.adName );

                if ( ad == null ) {
                    return new IllegalArgumentException( "Array Design Specified was not valid: " + adName
                            + " Either name is incorrect that Array Design is not in Gemma:" );
                }
                aeFlag = true;
            } else if ( !StringUtils.equalsIgnoreCase( Formats.GEO.toString(), fileFormat ) ) {
                return new IllegalArgumentException( "File format '" + fileFormat + "' is not understood" );
            }

            if ( accessions != null ) {
                log.info( "Got accession(s) from command line " + accessions );
                String[] accsToRun = StringUtils.split( accessions, ',' );

                for ( String accession : accsToRun ) {

                    accession = StringUtils.strip( accession );

                    if ( StringUtils.isBlank( accession ) ) {
                        continue;
                    }

                    if ( aeFlag ) {
                        processAEAccession( aeService, accession );

                    } else if ( platformOnly ) {
                        Collection<?> designs = geoService.fetchAndLoad( accession, true, true, false, false, true,
                                true );
                        for ( Object object : designs ) {
                            assert object instanceof ArrayDesign;
                            successObjects.add( ( ( Describable ) object ).getName()
                                    + " ("
                                    + ( ( ArrayDesign ) object ).getExternalReferences().iterator().next()
                                            .getAccession() + ")" );
                        }
                    } else {
                        processAccession( geoService, accession );
                    }
                }

            }

            if ( accessionFile != null ) {
                log.info( "Loading accessions from " + accessionFile );
                InputStream is = new FileInputStream( accessionFile );
                BufferedReader br = new BufferedReader( new InputStreamReader( is ) );

                String accession = null;
                while ( ( accession = br.readLine() ) != null ) {

                    if ( StringUtils.isBlank( accession ) ) {
                        continue;
                    }

                    processAccession( geoService, accession );

                }
            }
            summarizeProcessing();
        } catch ( Exception e ) {
            log.error( e );
            return e;
        }
        return null;
    }

    protected void processAccession( GeoService geoService, String accession ) {
        try {

            if ( force ) {
                removeIfExists( accession );
            }

            Collection<ExpressionExperiment> ees = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                    accession, false, doMatching, this.aggressive, this.splitByPlatform, this.allowSuperSeriesLoad,
                    this.allowSubSeriesLoad );

            if ( !suppressPostProcessing ) {
                postProcess( ees );
            }

            for ( Object object : ees ) {
                assert object instanceof ExpressionExperiment;
                successObjects.add( ( ( Describable ) object ).getName() + " ("
                        + ( ( ExpressionExperiment ) object ).getAccession().getAccession() + ")" );
            }
        } catch ( Exception e ) {
            errorObjects.add( accession + ": " + e.getMessage() );
            log.error( "**** Exception while processing " + accession + ": " + e.getMessage() + " ********" );
            log.error( e, e );
        }
    }

    /**
     * @param aeService
     * @param accession
     */
    protected void processAEAccession( ArrayExpressLoadService aeService, String accession ) {

        try {
            ExpressionExperiment aeExperiment = aeService.load( accession, adName, false );
            successObjects.add( ( ( Describable ) aeExperiment ).getName() + " (" + ( aeExperiment ).getShortName()
                    + ")" );

        } catch ( Exception e ) {
            errorObjects.add( accession + ": " + e.getMessage() );
            log.error( "**** Exception while processing " + accession + ": " + e.getMessage() + " ********" );
            log.error( e, e );

        }
    }

    @Override
    protected void processOptions() {
        super.processOptions();
        if ( hasOption( 'f' ) ) {
            accessionFile = getOptionValue( 'f' );
        }

        if ( hasOption( 'e' ) ) {
            accessions = getOptionValue( 'e' );
        }

        if ( hasOption( 'y' ) ) {
            platformOnly = true;
        }

        if ( hasOption( 'n' ) ) {
            doMatching = false;
        }

        if ( hasOption( "force" ) ) {
            force = true;
        }

        this.allowSubSeriesLoad = hasOption( "allowsuper" );
        this.allowSuperSeriesLoad = hasOption( "allowsuper" );

        if ( hasOption( "aggressive" ) ) {
            this.aggressive = true;
        }

        if ( hasOption( 'm' ) ) {
            this.fileFormat = getOptionValue( 'm' );
        }

        if ( hasOption( 'a' ) ) {
            this.adName = getOptionValue( 'a' );
        }

        if ( hasOption( "splitByPlatform" ) ) {
            this.splitByPlatform = true;
            this.doMatching = false; // defensive
        } else {
            this.splitByPlatform = false;
            this.doMatching = true;
        }

        this.suppressPostProcessing = hasOption( "nopost" );

        this.eeService = getBean( ExpressionExperimentService.class );
        this.adService = getBean( ArrayDesignService.class );
        this.processedExpressionDataVectorCreateService = getBean( ProcessedExpressionDataVectorCreateService.class );
        this.tcmv = this.getBean( TwoChannelMissingValues.class );
        this.sampleCoexpressionMatrixService = getBean( SampleCoexpressionMatrixService.class );
    }

    /**
     * Delete previous version of the experiment.
     * 
     * @param accession
     */
    protected void removeIfExists( String accession ) {
        DatabaseEntry acDbe = DatabaseEntry.Factory.newInstance();
        acDbe.setAccession( accession );
        ExternalDatabase geo = ExternalDatabase.Factory.newInstance();
        geo.setName( "GEO" );
        acDbe.setExternalDatabase( geo );
        Collection<ExpressionExperiment> existing = eeService.findByAccession( acDbe );

        if ( !existing.isEmpty() ) {
            log.info( "Deleting existing version of " + accession );
            for ( ExpressionExperiment expressionExperiment : existing ) {
                eeService.delete( expressionExperiment );
            }
        }
    }

}
