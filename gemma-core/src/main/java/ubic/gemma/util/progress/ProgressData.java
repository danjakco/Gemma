/* Copyright (c) 2006 University of British Columbia
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

package ubic.gemma.util.progress;

import java.io.Serializable;
import java.net.URL;


/**
 * <hr>
 * <p>
 * Copyright (c) 2006 UBC Pavlab
 * 
 * @author klc
 * @version $Id$
 */

public class ProgressData implements Serializable {

    private static final long serialVersionUID = -4303625064082352461L;

    private int percent = 0;
    private String description = "Default";
    private boolean done = false;
    private String forwardingURL;

    /**
     * @param per int value of percent
     * @param descrip string a description of the progress
     * @param finished
     */
    public ProgressData( int per, String descrip, boolean finished ) {
        percent = per;
        description = descrip;
        done = finished;
    }

    /**
     * @param percent int
     * @param description String
     */
    public ProgressData( int per, String descrip ) {
        this( per, descrip, false );
    }

    //dwr doesn't work right without blank constructor
    public ProgressData() {
                
    }
    
    /**
     * @param per int value of percent
     * @param descrip string a description of the progress
     * @param finished
     * @param forwardingURL the URL that will be forwarded to when the the progress bar is finished
     */
    public ProgressData( int per, String descrip, boolean finished, String forwardingURL ) {
        this(per,descrip,finished);
        this.forwardingURL = forwardingURL;
    }
    
    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone( boolean done ) {
        this.done = done;
    }

    /**
     * @param description string a description of the progress
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    public int getPercent() {
        return percent;
    }

    /**
     * @param per int value of percent
     */
    public void setPercent( int percent ) {
        this.percent = percent;
    }

    /**
     * @return the forwardingURL
     */
    public String getForwardingURL() {
        return forwardingURL;
    }

    /**
     * @param forwardingURL the forwardingURL to set
     */
    public void setForwardingURL( String forwardingURL ) {
        this.forwardingURL = forwardingURL;
    }

}
