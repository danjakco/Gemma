/*
 * The Gemma project
 *
 * Copyright (c) 2013 University of British Columbia
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
package ubic.gemma.core.loader.association.phenotype;

import org.apache.commons.cli.Option;
import ubic.gemma.core.apps.GemmaCLI.CommandGroup;
import ubic.gemma.core.association.phenotype.PhenotypeAssociationManagerService;
import ubic.gemma.core.util.AbstractCLI;
import ubic.gemma.core.util.AbstractCLIContextCLI;
import ubic.gemma.model.association.phenotype.PhenotypeAssociation;
import ubic.gemma.model.genome.gene.phenotype.valueObject.EvidenceValueObject;

import java.util.Collection;

/**
 * When we need to remove all evidence from an external database, usually to reimport them after
 *
 * @author nicolas
 */
public class DeleteEvidenceCLI extends AbstractCLIContextCLI {

    private String externalDatabaseName = "";
    private PhenotypeAssociationManagerService phenotypeAssociationService = null;

    public static void main( String[] args ) {

        DeleteEvidenceCLI deleteEvidenceImporterCLI = new DeleteEvidenceCLI();

        try {
            Exception ex;

            String[] argsToTake;

            argsToTake = args;

            ex = deleteEvidenceImporterCLI.doWork( argsToTake );

            if ( ex != null ) {
                ex.printStackTrace();
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public CommandGroup getCommandGroup() {
        return CommandGroup.PHENOTYPES;
    }

    @Override
    public String getCommandName() {
        return "deletePhenotypes";
    }

    @Override
    public String getShortDesc() {
        return "Use to remove all evidence from an external database, usually to reimport them after";
    }

    @Override
    protected void buildOptions() {
        @SuppressWarnings("static-access")
        Option databaseOption = Option.builder( "d" ).desc( "External database name (e.g. 'GWAS_Catalog', 'DGA' etc.)" ).hasArg()
                .argName( "name" ).required().build();
        this.addOption( databaseOption );

    }

    @Override
    protected Exception doWork( String[] args ) {

        Exception err = this.processCommandLine( args );

        if ( err != null )
            return err;

        try {
            this.loadServices();
        } catch ( Exception e ) {
            AbstractCLI.log.info( e.getMessage() );
        }

        Integer limit = 1000;

        AbstractCLI.log.info( "Loading " + limit + " evidences to delete ..." );

        Collection<EvidenceValueObject<? extends PhenotypeAssociation>> evidenceToDelete = this.phenotypeAssociationService
                .loadEvidenceWithExternalDatabaseName( externalDatabaseName, limit, 0 );
        int i = 0;

        while ( evidenceToDelete.size() > 0 ) {
            for ( EvidenceValueObject<?> e : evidenceToDelete ) {
                this.phenotypeAssociationService.remove( e.getId() );
             //   AbstractCLI.log.info( i++ );
                i++;
            }
            
            // WTF?
            evidenceToDelete = this.phenotypeAssociationService
                    .loadEvidenceWithExternalDatabaseName( externalDatabaseName, limit, 0 );
        }
      //  System.exit( -1 ); // ???
        return null;
    }

    @Override
    protected void processOptions() {
        super.processOptions();
        this.externalDatabaseName = this.getOptionValue( 'd' );
    }

    private synchronized void loadServices() {
        this.phenotypeAssociationService = this.getBean( PhenotypeAssociationManagerService.class );
    }

}
