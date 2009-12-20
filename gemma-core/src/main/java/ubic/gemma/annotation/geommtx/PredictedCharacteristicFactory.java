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
package ubic.gemma.annotation.geommtx;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ubic.GEOMMTx.LabelLoader;
import ubic.basecode.ontology.model.OntologyTerm;
import ubic.gemma.model.association.GOEvidenceCode;
import ubic.gemma.model.common.description.VocabCharacteristic;
import ubic.gemma.ontology.OntologyService;
import ubic.gemma.ontology.providers.MgedOntologyService;
import ubic.gemma.util.ConfigUtils;

/**
 * @author leon, paul
 * @version $Id$
 */
@Service
public class PredictedCharacteristicFactory implements InitializingBean {

    private Map<String, String> labels;

    /**
     * Special case
     */
    private OntologyTerm fmaMolecule;

    @Autowired
    private OntologyService ontologyService;

    protected static Log log = LogFactory.getLog( PredictedCharacteristicFactory.class );

    /**
     * @param URI
     * @return
     */
    public VocabCharacteristic getCharacteristic( String URI ) {
        checkReady();

        VocabCharacteristic c = VocabCharacteristic.Factory.newInstance();
        c.setValueUri( URI );
        c.setValue( labels.get( URI ) );

        String category = getCategory( URI );

        c.setCategory( category );
        c.setCategoryUri( MgedOntologyService.MGED_ONTO_BASE_URL + "#" + category );

        c.setEvidenceCode( GOEvidenceCode.IEA );

        return c;
    }

    private void checkReady() {
        if ( labels == null || labels.isEmpty() ) {
            throw new IllegalStateException( "Sorry, not usable" );
        }
    }

    public String getLabel( String uri ) {
        checkReady();

        return labels.get( uri );
    }

    public boolean hasLabel( String uri ) {
        checkReady();

        return labels.containsKey( uri );
    }

    /**
     * Infer the category
     * 
     * @param URI
     * @return
     */
    public String getCategory( String URI ) {
        String category = null;

        if ( URI.contains( "/owl/FMA#" ) ) {
            OntologyTerm term = ontologyService.getTerm( URI );

            boolean direct = false; // get all parents
            Collection<OntologyTerm> parents = term.getParents( direct );

            // test if its a Biological macromolecule in FMA
            if ( parents.contains( fmaMolecule ) ) {
                log.info( "URI is biological macromolecule in FMA" );
                category = "Compound";
            } else {
                category = "OrganismPart";
            }
        } else if ( URI.contains( "BIRNLex-Anatomy" ) ) {
            category = "OrganismPart";
        } else if ( URI.contains( "/owl/DOID#" ) ) {
            category = "DiseaseState";
        } else {
            log.debug( "Could not infer category for : " + URI );
        }
        return category;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        boolean activated = ConfigUtils.getBoolean( ExpressionExperimentAnnotator.MMTX_ACTIVATION_PROPERTY_KEY );

        if ( !activated ) {
            log.warn( "Automated tagger disabled; to turn on set "
                    + ExpressionExperimentAnnotator.MMTX_ACTIVATION_PROPERTY_KEY
                    + "=true in your Gemma.properties file" );
            return;
        }

        // term for Biological macromolecule in FMA (FMAID=63887)
        fmaMolecule = ontologyService.getTerm( "http://purl.org/obo/owl/FMA#FMA_63887" );
        labels = LabelLoader.readLabels();
    }
}
