/*
 * The Gemma project
 * 
 * Copyright (c) 2011 University of British Columbia
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
package ubic.gemma.web.services.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.NotFoundException;

import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentDao;
import ubic.gemma.model.expression.experiment.FactorValue;

/**
 * Simple web service to return sample annotations for curated dataset.
 * 
 * @author anton
 *
 */
@Component
@Path("/eedesign")
public class EEDesignWebService {
        
    @Autowired
    private ExpressionExperimentDao expressionExperimentDao;
     
    @GET
    @Path("/findByShortName/{shortName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Map<String,String>> getAnnotations( @PathParam("shortName") String shortName, @Context HttpServletResponse servletResponse ) {
        
        Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>();
        
        ExpressionExperiment experiment = this.expressionExperimentDao.findByShortName( shortName );        
        if (experiment == null) throw new NotFoundException("Dataset not found.");

        for (BioAssay bioAssay : experiment.getBioAssays()) {
            
            String accession = bioAssay.getAccession().getAccession();
            
            for (BioMaterial bioMaterial : bioAssay.getSamplesUsed()) {                    
            
                Map<String,String> annotations = new HashMap<String,String>();

                for (FactorValue factorValue : bioMaterial.getFactorValues()) {                    
                    if (factorValue.getExperimentalFactor().getName().equals( "batch" )) {
                        // skip batch
                    } else {
                        annotations.put( factorValue.getExperimentalFactor().getName(), getFactorValueString( factorValue ) );                        
                    }                
                }
                result.put( accession, annotations );                
            }                
        }
        
        return result;
    }
        
    private String getFactorValueString( FactorValue fv ) {
        if ( fv == null ) return "null";

        if ( fv.getCharacteristics() != null && fv.getCharacteristics().size() > 0 ) {
            String fvString = "";
            for (Characteristic c : fv.getCharacteristics()) {
                fvString += c.getValue() + " ";                
            }
            return fvString;
        } else if ( fv.getMeasurement() != null ) {
            return fv.getMeasurement().getValue();
        } else if ( fv.getValue() != null && !fv.getValue().isEmpty() ) {
            return fv.getValue();
        } else
            return "absent ";
    }

}
