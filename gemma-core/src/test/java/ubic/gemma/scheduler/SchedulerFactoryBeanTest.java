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
package ubic.gemma.scheduler;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;

import ubic.gemma.testing.BaseSpringContextTest;

/**
 * Indexing has been scheduled to run declaratively by Quartz. This class tests the declarative configuration.
 * 
 * @author keshav
 * @version $Id$
 */
public class SchedulerFactoryBeanTest extends BaseSpringContextTest {
    private Log log = LogFactory.getLog( this.getClass() );

    /**
     * Tests the indexAll method.
     */
    public void testIndexAll() {
        log.info( "Running test" + this.getClass() );
        StdScheduler scheduler = ( StdScheduler ) this.getBean( "schedulerFactoryBean" );

        try {
            String[] names = scheduler.getTriggerNames( null );
            for ( String name : names ) {
                log.info( name );
            }
            Trigger newTrigger = scheduler.getTrigger( "indexTrigger", null );
            newTrigger.setStartTime( new Date() );
            scheduler.rescheduleJob( "indexTrigger", null, newTrigger );
            scheduler.start();
        } catch ( SchedulerException e ) {
            e.printStackTrace();
        }

    }

}
