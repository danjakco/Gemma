/*
 * The Gemma project
 * 
 * Copyright (c) 2012 University of British Columbia
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

package ubic.gemma.persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

/**
 * Provide logging when an operation has failed and is being retried. This would not be needed if there was better
 * logging control over the default RetryContext.
 * 
 * @author paul
 * @version $Id$
 */
@Component
public class RetryLogger extends RetryListenerSupport {

    private static Log log = LogFactory.getLog( RetryLogger.class );

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.retry.listener.RetryListenerSupport#open(org.springframework.retry.RetryContext,
     * org.springframework.retry.RetryCallback)
     */
    @Override
    public <T> boolean open( RetryContext context, RetryCallback<T> callback ) {
        if ( context.getRetryCount() > 0 ) {
            log.warn( "Retry attempt: " + context.getRetryCount() + " [ Triggered by: "
                    + context.getLastThrowable().getMessage() + "]" );
        }
        return super.open( context, callback );
    }
}
