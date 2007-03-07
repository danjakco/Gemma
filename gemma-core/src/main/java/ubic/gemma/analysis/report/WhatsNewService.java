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
package ubic.gemma.analysis.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import ubic.basecode.util.FileTools;
import ubic.gemma.datastructure.AuditableObject;
import ubic.gemma.model.common.auditAndSecurity.AuditEventService;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.arrayDesign.ArrayDesignService;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.model.expression.experiment.ExpressionExperimentValueObject;
import ubic.gemma.util.ConfigUtils;

/**
 * Service to collect data on object that are new in the system.
 * 
 * @spring.bean id="whatsNewService"
 * @spring.property name="auditEventService" ref="auditEventService"
 * @spring.property name="expressionExperimentService" ref="expressionExperimentService"
 * @spring.property name="arrayDesignService" ref="arrayDesignService"
 * @author pavlidis
 * @version $Id$
 */
public class WhatsNewService {
    
    private String WHATS_NEW_FILE = "WhatsNew";
    private String WHATS_NEW_DIR = "WhatsNew";
    private String HOME_DIR = ConfigUtils.getString( "gemma.appdata.home" );
    
    AuditEventService auditEventService;
    ExpressionExperimentService expressionExperimentService = null;
    ArrayDesignService arrayDesignService = null;

    /**
     * @param arrayDesignService the arrayDesignService to set
     */
    public void setArrayDesignService( ArrayDesignService arrayDesignService ) {
        this.arrayDesignService = arrayDesignService;
    }

    /**
     * @param expressionExperimentService the expressionExperimentService to set
     */
    public void setExpressionExperimentService( ExpressionExperimentService expressionExperimentService ) {
        this.expressionExperimentService = expressionExperimentService;
    }

    public void setAuditEventService( AuditEventService auditEventService ) {
        this.auditEventService = auditEventService;
    }

    /**
     * @param date
     * @return representing the updated or new objects.
     */
    @SuppressWarnings("unchecked")
    public WhatsNew getReport( Date date ) {
        WhatsNew wn = new WhatsNew( date );
        wn.setUpdatedObjects( auditEventService.getUpdatedSinceDate( date ) );
        wn.setNewObjects( auditEventService.getNewSinceDate( date ) );
        return wn;
    }

    /**
     * save the report from the date specified. This will be the report that will be 
     * used by the WhatsNew box.
     * @param date
     */
    public void saveReport( Date date) {
        WhatsNew wn = getReport(date);
        initDirectories( true );
        saveFile(wn);
    }
    
    /**
     * save the report from last week. This will be the report that will be 
     * used by the WhatsNew box.
     * @param date
     */
    public void generateWeeklyReport( ) {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        date = DateUtils.addDays( date, -7 );
        saveReport(date);
    }
    
    /**
     * Retrieve the latest WhatsNew report.
     * @return WhatsNew the latest WhatsNew report cache.
     */
    public WhatsNew retrieveReport () {
        WhatsNew wn = new WhatsNew();

        try {
            File newObjects = new File( HOME_DIR + "/" + WHATS_NEW_DIR + "/" + WHATS_NEW_FILE + ".new");
            File updatedObjects = new File( HOME_DIR + "/" + WHATS_NEW_DIR + "/" + WHATS_NEW_FILE + ".updated");
            if ( !newObjects.exists() && !updatedObjects.exists()) {
                return null;
            }
            
            // load up all new objects
            if (newObjects.exists()) {
                FileInputStream fis = new FileInputStream(newObjects );
                ObjectInputStream ois = new ObjectInputStream( fis );
                Collection<AuditableObject> aos = ( Collection ) ois.readObject();   
                ois.close();
                fis.close();
                
                Collection<Long> newEeIds = new ArrayList<Long>();
                for ( AuditableObject object : aos ) {
                    if (object.type.equalsIgnoreCase( "ArrayDesign" )) {
                        wn.addNewObjects( arrayDesignService.load( object.id ) );
                    }
                    if (object.type.equalsIgnoreCase( "ExpressionExperiment" )) {
                        newEeIds.add( object.id );
                    }
                    if (object.date != null) {
                        wn.setDate(object.date );
                    }
                }
                if (newEeIds.size() > 0) {
                    wn.addNewObjects( expressionExperimentService.load( newEeIds ) );
                }

            }
            
            // load up all updated objects
            if (updatedObjects.exists()) {
                FileInputStream fis = new FileInputStream(updatedObjects );
                ObjectInputStream ois = new ObjectInputStream( fis );
                Collection<AuditableObject> aos = ( Collection ) ois.readObject();   
                ois.close();
                fis.close();
                
                Collection<Long> updatedEeIds = new ArrayList<Long>();
                for ( AuditableObject object : aos ) {
                    if (object.type.equalsIgnoreCase( "ArrayDesign" )) {
                        ArrayDesign ad = arrayDesignService.load( object.id );
                        wn.addUpdatedObjects( ad );
                    }
                    if (object.type.equalsIgnoreCase( "ExpressionExperiment" )) {
                        updatedEeIds.add( object.id );
                    }
                    if (object.date != null) {
                        wn.setDate(object.date );
                    }
                }
                if (updatedEeIds.size() > 0) {
                    wn.addUpdatedObjects( expressionExperimentService.load( updatedEeIds ) );
                }
            }
        } catch ( Throwable e ) {
            return null;
        }

        return wn;
    }
    
    private void initDirectories( boolean deleteFiles ) {
        // check to see if the home directory exists. If it doesn't, create it.
        // check to see if the reports directory exists. If it doesn't, create it.
        FileTools.createDir( HOME_DIR );
        FileTools.createDir( HOME_DIR + "/" + WHATS_NEW_DIR );
        File f = new File( HOME_DIR + "/" + WHATS_NEW_DIR );
        Collection<File> files = new ArrayList<File>();
        File[] fileArray = f.listFiles();
        for ( File file : fileArray ) {
            files.add( file );
        }
        // clear out all files
        if ( deleteFiles ) {
            FileTools.deleteFiles( files );
        }
    }
    
    private boolean saveFile( WhatsNew wn ) {
        try {
            // remove file first
            File newOutput = new File( HOME_DIR + "/" + WHATS_NEW_DIR + "/" + WHATS_NEW_FILE + ".new" );
            File updatedOutput = new File( HOME_DIR + "/" + WHATS_NEW_DIR + "/" + WHATS_NEW_FILE + ".updated" );
            if ( newOutput.exists() ) {
                newOutput.delete();
            }
            if ( updatedOutput.exists() ) {
                updatedOutput.delete();
            }
            Calendar c = Calendar.getInstance();
            Date date = c.getTime();
            
            Collection<ArrayDesign> ads = wn.getNewArrayDesigns();
            Collection<ExpressionExperiment> ees = wn.getNewExpressionExperiments();
            // save the IDs for new Auditables
            Collection<AuditableObject> newObjects = new ArrayList<AuditableObject>();
            for ( ArrayDesign ad : ads ) {
                AuditableObject ao = new AuditableObject();
                ao.date = date;
                ao.type = "ArrayDesign";
                ao.id = ad.getId();
                newObjects.add( ao );
            }
            for ( ExpressionExperiment ee : ees ) {
                AuditableObject ao = new AuditableObject();
                ao.date = date;
                ao.type = "ExpressionExperiment";
                ao.id = ee.getId();
                newObjects.add( ao );
            }
            
            // save the ids for updated Auditables
            ads = wn.getUpdatedArrayDesigns();
            ees = wn.getUpdatedExpressionExperiments();
            // save the IDs for new Auditables
            Collection<AuditableObject> updatedObjects = new ArrayList<AuditableObject>();
            for ( ArrayDesign ad : ads ) {
                AuditableObject ao = new AuditableObject();
                ao.date = date;
                ao.type = "ArrayDesign";
                ao.id = ad.getId();
                updatedObjects.add( ao );
            }
            for ( ExpressionExperiment ee : ees ) {
                AuditableObject ao = new AuditableObject();
                ao.date = date;
                ao.type = "ExpressionExperiment";
                ao.id = ee.getId();
                updatedObjects.add( ao );
            }
            // save information into a map
            //Map<String,Collection> auditableSave = new HashMap<String,Collection>();
            //auditableSave.put( "newAuditables", newObjects );
            //auditableSave.put( "updatedAuditables", updatedObjects );
            FileOutputStream fos = new FileOutputStream( newOutput );
            ObjectOutputStream oos = new ObjectOutputStream( fos );
            oos.writeObject( newObjects );            
            oos.flush();
            oos.close();
            
            fos = new FileOutputStream( updatedOutput );
            oos = new ObjectOutputStream( fos );
            oos.writeObject( updatedObjects );            
            oos.flush();
            oos.close();
        } catch ( Throwable e ) {
            return false;
        }

        return true;
    }
    

}
