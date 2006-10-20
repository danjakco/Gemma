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
package ubic.gemma.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author pavlidis
 * @version $Id$
 */
public abstract class BackgroundControllerJob extends BaseFormController implements Runnable {

    Log loadLog = LogFactory.getLog( this.getClass().getName() );

    protected Object command;
    protected SecurityContext securityContext;
    protected HttpSession session;

    /**
     * @param securityContext
     * @param command
     * @param jobDescription
     */
    protected void init( SecurityContext parentSecurityContext, HttpServletRequest request, Object commandObj ) {

        this.securityContext = parentSecurityContext;

        SecurityContextHolder.setContext( securityContext ); // so that acegi doesn't deny the thread permission
        this.command = commandObj;

        this.session = request.getSession();
    }

}
