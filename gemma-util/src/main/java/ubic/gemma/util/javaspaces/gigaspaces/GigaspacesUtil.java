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
package ubic.gemma.util.javaspaces.gigaspaces;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.j_spaces.core.client.FinderException;
import com.j_spaces.core.client.SpaceFinder;

/**
 * @author keshav
 * @version $Id$
 */
public class GigaspacesUtil implements ApplicationContextAware {

    private static Log log = LogFactory.getLog( GigaspacesUtil.class );

    private ApplicationContext applicationContext = null;

    /**
     * Determines if the (@link ApplicationContext) contains gigaspaces beans.
     */
    private boolean contextContainsGigaspaces() {

        return applicationContext.containsBean( "gigaspacesTemplate" );

    }

    /**
     * Checks if space is running at specified url.
     * 
     * @param ctx
     * @return boolean
     */
    public static boolean isSpaceRunning( String url ) {

        boolean running = true;
        try {
            SpaceFinder.find( url );
        } catch ( FinderException e ) {
            running = false;
            log.error( "Error finding space at: " + url + "." );
            // e.printStackTrace();
        } finally {
            return running;
        }
    }

    /**
     * Add the gigaspaces contexts to the other spring contexts.
     * 
     * @param paths
     */
    public static void addGigaspacesContextToPaths( List<String> paths ) {
        paths.add( "classpath*:ubic/gemma/gigaspaces-expressionExperiment.xml" );
    }

    /**
     * First checks if the space is running at url. If space is running, adds the gigaspaces beans to the context if
     * they do not exist. If the space is not running, returns the original context.
     * 
     * @param url
     * @return ApplicatonContext
     */
    public ApplicationContext addGigaspacesToApplicationContext( String url ) {

        if ( !isSpaceRunning( url ) ) {
            log.error( "Cannot add Gigaspaces to application context. Space not started at " + url
                    + ". Returning context without gigaspaces beans." );

            return applicationContext;

        }

        if ( !contextContainsGigaspaces() ) {

            GenericWebApplicationContext genericCtx = new GenericWebApplicationContext();
            XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader( genericCtx );
            xmlReader.loadBeanDefinitions( new ClassPathResource( "ubic/gemma/gigaspaces-expressionExperiment.xml" ) );
            // PropertiesBeanDefinitionReader propReader = new PropertiesBeanDefinitionReader( genericCtx );
            // propReader.loadBeanDefinitions(new ClassPathResource("otherBeans.properties"));

            genericCtx.setParent( applicationContext );

            genericCtx.refresh();

            return genericCtx;

        }

        else {
            log.info( "Application context unchanged. Gigaspaces beans already exist." );
        }

        return applicationContext;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException {
        this.applicationContext = applicationContext;

    }

}
