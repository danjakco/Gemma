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
package edu.columbia.gemma.security;

import java.util.Collection;
import java.util.HashSet;

import org.acegisecurity.AccessDeniedException;

import edu.columbia.gemma.BaseTransactionalSpringContextTest;
import edu.columbia.gemma.common.auditAndSecurity.AuditTrail;
import edu.columbia.gemma.common.auditAndSecurity.AuditTrailService;
import edu.columbia.gemma.common.auditAndSecurity.Contact;
import edu.columbia.gemma.common.auditAndSecurity.User;
import edu.columbia.gemma.common.auditAndSecurity.UserDao;
import edu.columbia.gemma.common.auditAndSecurity.UserService;
import edu.columbia.gemma.expression.arrayDesign.ArrayDesign;
import edu.columbia.gemma.expression.arrayDesign.ArrayDesignService;
import edu.columbia.gemma.expression.designElement.CompositeSequence;
import edu.columbia.gemma.expression.designElement.CompositeSequenceService;
import edu.columbia.gemma.security.ui.ManualAuthenticationProcessing;
import edu.columbia.gemma.util.StringUtil;
import edu.columbia.gemma.web.Constants;

/**
 * Use this to test acegi functionality.
 * 
 * @author pavlidis
 * @author keshav
 * @version $Id$
 */
public class SecurityIntegrationTest extends BaseTransactionalSpringContextTest {

    private static String testUserName = "testUser";
    private static String testPassword = "toast";

    private ManualAuthenticationProcessing manualAuthenticationProcessing;
    private ArrayDesignService arrayDesignService;
    private AuditTrailService auditTrailService;
    private CompositeSequenceService compositeSequenceService;
    private UserDao userDao;
    private UserService userService;

    /**
     * @param userService The userService to set.
     */
    public void setUserService( UserService userService ) {
        this.userService = userService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.columbia.gemma.BaseDependencyInjectionSpringContextTest#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception {

        super.onSetUpInTransaction(); // so we have authority to add a user.
        User testUser = User.Factory.newInstance();
        testUser.setEmail( "foo@bar" );
        testUser.setFirstName( "Foo" );
        testUser.setLastName( "Bar" );
        testUser.setMiddleName( "" );
        testUser.setEnabled( Boolean.TRUE );
        testUser.setUserName( testUserName );
        testUser.setPassword( StringUtil.encodePassword( testPassword, "SHA" ) );
        testUser.setConfirmPassword( testPassword );
        testUser.setPasswordHint( "I am an idiot" );
        testUser = ( User ) userDao.create( testUser );
        userService.addRole( testUser, Constants.USER_ROLE );

        if ( !manualAuthenticationProcessing.validateRequest( testUserName, testPassword ) ) {
            throw new RuntimeException( "Failed to authenticate" );
        }

    }

    /**
     * Test removing an arrayDesign without having the correct authorization privileges. You should get an
     * unsuccessfulAuthentication.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testRemoveArrayDesignWithoutAuthorizationWithoutMock() throws Exception {
        ArrayDesign ad = ArrayDesign.Factory.newInstance();
        ad.setName( "deleteme" );
        ad = ( ArrayDesign ) persisterHelper.persist( ad );

        arrayDesignService.remove( ad );
    }

    /**
     * Test removing an arrayDesign with correct authorization. The security interceptor should be called on this
     * method, as should the AddOrRemoveFromACLInterceptor.
     * 
     * @throws Exception
     */
    public void testRemoveArrayDesignWithoutMock() throws Exception {

        ArrayDesign ad = arrayDesignService.findArrayDesignByName( "AD Foo" );
        if ( ad == null )
            log.info( "ArrayDesign does not exist, skipping test" ); // FIXME, should add it!

        else {
            try {
                arrayDesignService.remove( ad );
                fail( "Should have gotten an AccessDeniedException" );
            } catch ( AccessDeniedException okay ) {
                // 
            }
        }
    }

    /**
     * Save an array design.
     * 
     * @throws Exception
     */
    public void testSaveArrayDesignWithoutMock() throws Exception {

        ArrayDesign arrayDesign = ArrayDesign.Factory.newInstance();
        arrayDesign.setName( "AD Foo" );
        arrayDesign.setDescription( "a test ArrayDesign" );

        AuditTrail at = AuditTrail.Factory.newInstance();
        at = auditTrailService.create( at );
        arrayDesign.setAuditTrail( at );

        Contact c = Contact.Factory.newInstance();
        c.setName( "\' Design Provider Name\'" );
        at = AuditTrail.Factory.newInstance();
        at = auditTrailService.create( at );
        c.setAuditTrail( at );

        arrayDesign.setDesignProvider( c );

        CompositeSequence cs1 = CompositeSequence.Factory.newInstance();
        cs1.setName( "DE Bar1" );

        CompositeSequence cs2 = CompositeSequence.Factory.newInstance();
        cs2.setName( "DE Bar2" );

        Collection<CompositeSequence> col = new HashSet<CompositeSequence>();
        col.add( cs1 );
        col.add( cs2 );

        /*
         * Note this sequence. Remember, inverse="true" if using this. If you do not make an explicit call to
         * cs1(2).setArrayDesign(arrayDesign), then inverse="false" must be set.
         */
        cs1.setArrayDesign( arrayDesign );
        cs2.setArrayDesign( arrayDesign );
        arrayDesign.setCompositeSequences( col );

        arrayDesignService.findOrCreate( arrayDesign );

    }

    /**
     * Tests getting all design elements given authorization on an array design (ie. tests getting the 'owned objects'
     * given the authorization on the owner). This test was used to test the Acegi Security functionality.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testGetAllDesignElementsFromArrayDesignsWithoutMock() throws Exception {

        Collection<CompositeSequence> col = compositeSequenceService.getAllCompositeSequences();
        for ( CompositeSequence cs : col ) {
            log.debug( cs );
        }

        if ( col.size() == 0 ) {
            fail( "User not authorized for to access at least one of the objects in the graph" );
        }
    }

    /**
     * @param arrayDesignService The arrayDesignService to set.
     */
    public void setArrayDesignService( ArrayDesignService arrayDesignService ) {
        this.arrayDesignService = arrayDesignService;
    }

    /**
     * @param auditTrailService The auditTrailService to set.
     */
    public void setAuditTrailService( AuditTrailService auditTrailService ) {
        this.auditTrailService = auditTrailService;
    }

    /**
     * @param compositeSequenceService The compositeSequenceService to set.
     */
    public void setCompositeSequenceService( CompositeSequenceService compositeSequenceService ) {
        this.compositeSequenceService = compositeSequenceService;
    }

    /**
     * @param manualAuthenticationProcessing The manualAuthenticationProcessing to set.
     */
    public void setManualAuthenticationProcessing( ManualAuthenticationProcessing manualAuthenticationProcessing ) {
        this.manualAuthenticationProcessing = manualAuthenticationProcessing;
    }

    /**
     * @param userDao The userDao to set.
     */
    public void setUserDao( UserDao userDao ) {
        this.userDao = userDao;
    }
}