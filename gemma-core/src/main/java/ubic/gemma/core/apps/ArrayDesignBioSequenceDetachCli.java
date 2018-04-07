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
package ubic.gemma.core.apps;

import java.util.HashSet;
import java.util.Map;

import org.apache.commons.cli.Option;

import ubic.gemma.core.util.AbstractCLIContextCLI;
import ubic.gemma.model.common.auditAndSecurity.eventType.ArrayDesignSequenceRemoveEvent;
import ubic.gemma.model.common.auditAndSecurity.eventType.AuditEventType;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.genome.biosequence.BioSequence;
import ubic.gemma.persistence.service.genome.biosequence.BioSequenceService;

/**
 * Remove all associations that this array design has with BioSequences. This is needed for cases where the original
 * import has associated the probes with the wrong sequences. A common case is for GEO data sets where the actual
 * oligonucleotide is not given. Instead the submitter provides Genbank accessions, which are misleading. This method
 * can be used to clear those until the "right" sequences can be identified and filled in. Note that this does not
 * remove the BioSequences, it just nulls the BiologicalCharacteristics of the CompositeSequences.
 *
 * @author pavlidis
 */
public class ArrayDesignBioSequenceDetachCli extends ArrayDesignSequenceManipulatingCli {

    public static void main( String[] args ) {
        ArrayDesignBioSequenceDetachCli p = new ArrayDesignBioSequenceDetachCli();
        AbstractCLIContextCLI.tryDoWorkNoExit( p, args );
    }

    @Override
    public String getCommandName() {
        return "detachSequences";
    }

    @Override
    protected void buildOptions() {
        super.buildOptions();

        Option fileOption = Option.builder( "delete" )
                .desc( "Delete sequences instead of detaching them - use with care" ).build();

        this.addOption( fileOption );
    }

    @Override
    protected Exception doWork( String[] args ) {
        Exception err = this.processCommandLine( args );
        if ( err != null )
            return err;

        BioSequenceService bioSequenceService = this.getBean( BioSequenceService.class );

        for ( ArrayDesign arrayDesign : this.arrayDesignsToProcess ) {

            if ( this.hasOption( "delete" ) ) {
                Map<CompositeSequence, BioSequence> bioSequences = this.getArrayDesignService().getBioSequences( arrayDesign );
                this.getArrayDesignService().removeBiologicalCharacteristics( arrayDesign );
                bioSequenceService.remove( new HashSet<>( bioSequences.values() ) );
                this.audit( arrayDesign, "Deleted " + bioSequences.size() + " associated sequences from the system" );
            } else {

                this.getArrayDesignService().removeBiologicalCharacteristics( arrayDesign );
                this.audit( arrayDesign, "Removed sequence associations with CLI" );
            }
        }
        return null;
    }

    @Override
    public String getShortDesc() {
        return "Remove all associations that a platform has with sequences, for cases where imported data had wrong associations. "
                + "Also can be used to delete sequences associated with a platform (use very carefully as sequences can be shared by platforms)";
    }

    private void audit( ArrayDesign arrayDesign, String message ) {
        super.getArrayDesignReportService().generateArrayDesignReport( arrayDesign.getId() );
        AuditEventType eventType = ArrayDesignSequenceRemoveEvent.Factory.newInstance();
        auditTrailService.addUpdateEvent( arrayDesign, eventType, message );
    }

}
