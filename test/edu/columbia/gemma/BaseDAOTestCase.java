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
package edu.columbia.gemma;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;

import edu.columbia.gemma.util.SpringContextUtil;

/**
 * Base class for running DAO tests. Based on code from Appfuse.
 * 
 * @author mraible
 * @author pavlidis
 * @version $Id$
 */
public class BaseDAOTestCase extends TestCase {
    protected final Log log = LogFactory.getLog( getClass() );
    protected final static BeanFactory ctx = SpringContextUtil.getApplicationContext( true );
    protected ResourceBundle rb;

    public BaseDAOTestCase() {
        // Since a ResourceBundle is not required for each class, just
        // do a simple check to see if one exists
        String className = this.getClass().getName();

        try {
            rb = ResourceBundle.getBundle( className ); // will look for <className>.properties
        } catch ( MissingResourceException mre ) {
            // log.warn("No resource bundle found for: " + className);
        }
    }

    /**
     * Utility method to populate a javabean-style object with values from a Properties file
     * 
     * @param obj
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected Object populate( Object obj ) throws Exception {
        // loop through all the beans methods and set its properties from
        // its .properties file
        Map map = new HashMap();

        for ( Enumeration keys = rb.getKeys(); keys.hasMoreElements(); ) {
            String key = ( String ) keys.nextElement();
            map.put( key, rb.getString( key ) );
        }

        BeanUtils.copyProperties( obj, map );

        return obj;
    }
}