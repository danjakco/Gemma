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
package edu.columbia.gemma.web.controller.common.auditAndSecurity;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.columbia.gemma.common.auditAndSecurity.User;
import edu.columbia.gemma.common.auditAndSecurity.UserExistsException;
import edu.columbia.gemma.common.auditAndSecurity.UserRoleService;
import edu.columbia.gemma.util.RequestUtil;
import edu.columbia.gemma.util.StringUtil;
import edu.columbia.gemma.web.Constants;
import edu.columbia.gemma.web.controller.BaseFormController;

/**
 * Controller to signup new users. Based on code from Appfuse.
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * @author pavlidis
 * @author keshav
 * @version $Id$
 * @spring.bean id="signupController" name="/signup.html"
 * @spring.property name="formView" value="signup"
 * @spring.property name="validator" ref="userValidator"
 * @spring.property name="successView" value="redirect:mainMenu.html"
 * @spring.property name="commandName" value="user"
 * @spring.property name="commandClass" value="edu.columbia.gemma.common.auditAndSecurity.User"
 * @spring.property name="userService" ref="userService"
 * @spring.property name="userRoleService" ref="userRoleService"
 * @spring.property name="mailEngine" ref="mailEngine"
 * @spring.property name="message" ref="mailMessage"
 * @spring.property name="templateName" value="accountCreated.vm"
 */
public class SignupController extends BaseFormController {
    private UserRoleService userRoleService;

    @Override
    public ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response, Object command,
            BindException errors ) throws Exception {

        log.debug( "entering 'onSubmit' method..." );

        User user = ( User ) command;
        Locale locale = request.getLocale();

        // TODO we need to get direct access to the ServletContext out of the controller. It is making the
        // controller impossible to test!!
        String algorithm;
        if ( request.getServletPath() != "/test.signup.html" )
            algorithm = ( String ) getConfiguration().get( Constants.ENC_ALGORITHM );
        else {
            algorithm = "SHA";
        }
        // TODO when testing, you would never get to this point without my check above. Again, we need to
        // get the ServletContext access out of the controller.
        if ( algorithm == null ) { // should only happen for test case
            log.debug( "assuming testcase, setting algorithm to 'SHA'" );
            algorithm = "SHA";
        }

        user.setPassword( StringUtil.encodePassword( user.getPassword(), algorithm ) );

        user.setEnabled( true );

        // Set the default user role on this new user
        this.getUserService().addRole( user, userRoleService.getRole( Constants.USER_ROLE ) );

        try {
            this.getUserService().saveUser( user );
        } catch ( UserExistsException e ) {
            log.warn( e.getMessage() );

            errors.rejectValue( "userName", "errors.existing.user",
                    new Object[] { user.getUserName(), user.getEmail() }, "duplicate user" );

            // redisplay the unencrypted passwords
            user.setPassword( user.getConfirmPassword() );
            return showForm( request, response, errors );
        }

        // Set cookies for auto-magical login ;-)
        String loginCookie = this.getUserService().createLoginCookie( user.getUserName() );
        RequestUtil.setCookie( response, Constants.LOGIN_COOKIE, loginCookie, request.getContextPath() );

        saveMessage( request, getText( "user.registered", user.getUserName(), locale ) );

        request.getSession().setAttribute( Constants.REGISTERED, Boolean.TRUE );

        // Send user an e-mail
        log.debug( "Sending user '" + user.getUserName() + "' an account information e-mail" );
        message.setSubject( getText( "signup.email.subject", locale ) );
        sendEmail( user, getText( "signup.email.message", locale ), RequestUtil.getAppURL( request ) );

        return new ModelAndView( getSuccessView() );
    }

    /**
     * @param roleManager The roleManager to set.
     */
    public void setUserRoleService( UserRoleService userRoleService ) {
        this.userRoleService = userRoleService;
    }

    @Override
    @SuppressWarnings("unused")
    protected Object formBackingObject( HttpServletRequest request ) throws Exception {
        return User.Factory.newInstance();
    }

}