/*
 * The Gemma project
 * 
 * Copyright (c) 2005 Columbia University
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
package edu.columbia.gemma.web.taglib;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 * @jsp.tag name="date" body-content="empty"
 */
public class DateFormatTag extends BodyTagSupport {

    private Date date;

    /**
     * @param d
     * @jsp.attribute description="The date to be formatted" required="true" rtexprvalue="true"
     */
    public void setDate( Date d ) {
        this.date = d;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {

        String formattedDate = "";
        if ( date != null ) {
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy" );
            formattedDate = sdf.format( date );
        }
        try {
            pageContext.getOut().print( formattedDate );
        } catch ( Exception ex ) {
            throw new JspException( "DateFormatTag: " + ex.getMessage() );
        }

        return SKIP_BODY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
     */
    @SuppressWarnings("unused")
    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

}
