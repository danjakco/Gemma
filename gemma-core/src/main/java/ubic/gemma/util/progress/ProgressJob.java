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

import java.util.Observer;

import ubic.gemma.model.common.auditAndSecurity.JobInfo;

/**
 * <hr>
 * All progressJobs must implement the following functionality. ProgressJobs are used by the client to provide hooks for
 * providing feedback to a user for long running processes
 * <p>
 * Copyright (c) 2006 UBC Pavlab
 * 
 * @author klc
 * @version $Id$
 */
public interface ProgressJob {

    /**
     * @return Returns the pData.
     */
    public abstract ProgressData getProgressData();

    /**
     * @param data The pData to set.
     */
    public abstract void setProgressData( ProgressData data );

    /**
     * @return Returns the runningStatus.
     */
    public abstract boolean isRunningStatus();

    /**
     * @param runningStatus The runningStatus to set.
     */
    public abstract void setRunningStatus( boolean runningStatus );

  
    public abstract String getUser();

    /**
     * Updates the current progress of the job. Simple increments the progress percent by 1.
     */
    public abstract void updateProgress();

    /**
     * Upates the current progress of the job to the desired percent. doesn't change anything else.
     * 
     * @param newPercent
     */
    public abstract void updateProgress( int newPercent );

    /**
     * Updates the progress job by a complete progressData. In case a few things need to be updated
     * 
     * @param pd
     */
    public abstract void updateProgress( ProgressData pd );

    public abstract void addObserver( Observer O );
    
    public abstract Long getId();
    
    public abstract void done();
    
    public abstract int getPhase();
    
    public abstract void setPhase(int phase);
    
    public abstract void setDescription(String description);
    
    public abstract String getDescription();
    
    public abstract JobInfo getJobInfo();
    
    public abstract String getTrackingId();
    public abstract void   setTrackingId(String trackingId);
    
    

}