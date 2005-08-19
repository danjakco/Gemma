package edu.columbia.gemma.expression.arrayDesign;

import java.util.Collection;
import java.util.HashSet;

import org.easymock.MockControl;
import org.springframework.beans.factory.BeanFactory;

import edu.columbia.gemma.BaseServiceTestCase;
import edu.columbia.gemma.expression.designElement.CompositeSequence;
import edu.columbia.gemma.expression.designElement.CompositeSequenceService;
import edu.columbia.gemma.expression.designElement.DesignElement;
import edu.columbia.gemma.security.ui.ManualAuthenticationProcessing;
import edu.columbia.gemma.util.SpringContextUtil;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$ TODO this test class contains
 *          lots of tests for acegi security. I have commented out many of the other tests for now, but will add them
 *          back in soon.
 */
public class ArrayDesignServiceImplTest extends BaseServiceTestCase {

    private ArrayDesignServiceImpl arrayDesignService = new ArrayDesignServiceImpl();
    private ArrayDesignDao arrayDesignDaoMock = null;
    private MockControl control;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        control = MockControl.createControl( ArrayDesignDao.class );
        arrayDesignDaoMock = ( ArrayDesignDao ) control.getMock();
        arrayDesignService.setArrayDesignDao( arrayDesignDaoMock );
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Uses mock objects to unit test service layer method getAllArrayDesigns().
     * 
     * @throws Exception TODO add security to the method, then add it back to list of tests.
     */
    // public void testGetAllArrayDesigns() throws Exception {
    // // to implement this test, the mock dao has to save several objects.
    // Collection m = new HashSet();
    //
    // for ( int i = 0; i < 5; i++ ) {
    // ArrayDesign tad = ArrayDesign.Factory.newInstance();
    //
    // tad.setName( "Foo" + i );
    // if ( !m.add( tad ) ) throw new IllegalStateException( "Couldn't add to the collection - check equals" );
    // }
    //
    // // set the behavior for the DAO: get all array designs will retrive the collection.
    // arrayDesignDaoMock.getAllArrayDesigns();
    // control.setReturnValue( m );
    //
    // control.replay(); // switch from record mode to replay
    // arrayDesignService.getAllArrayDesigns();
    // control.verify(); // verify that expectations were met
    // }
    // /**
    // * @throws Exception TODO add security to this test, then add it back to list of tests.
    // */
    // public void testSaveArrayDesign() throws Exception {
    // ArrayDesign tad = ArrayDesign.Factory.newInstance();
    //
    // tad.setName( "Foo" );
    //
    // // The expected behavior
    // arrayDesignDaoMock.create( tad );
    // control.setReturnValue( tad );
    //
    // control.replay(); // switch from record mode to replay
    // arrayDesignService.saveArrayDesign( tad );
    // control.verify(); // verify that expectations were met.
    // }
    /**
     * Test removing an arrayDesign without having the correct authorization privileges. You should get an
     * unsuccessfulAuthentication. Does not use mock objects because I need the data from the database.
     * 
     * @throws Exception
     */
    // public void testRemoveArrayDesignWithoutAuthorizationWithoutMock() throws Exception {
    // BeanFactory ctx = SpringContextUtil.getApplicationContext();
    //
    // ManualAuthenticationProcessing manAuthentication = ( ManualAuthenticationProcessing ) ctx
    // .getBean( "manualAuthenticationProcessing" );
    //
    // manAuthentication.validateRequest( "KiranKeshav", "7Champin" );
    //
    // ArrayDesignService ads = ( ArrayDesignService ) ctx.getBean( "arrayDesignService" );
    //
    // Collection<ArrayDesign> col = ads.getAllArrayDesigns();
    // if ( col.size() == 0 )
    // log.info( "There are no arrayDesigns in database" );
    //
    // else {
    // Iterator iter = col.iterator();
    // ArrayDesign ad = ( ArrayDesign ) iter.next();
    //
    // ads.removeArrayDesign( ad );
    // }
    //
    // // TODO add a more appropriate assertion (ie. specific to acegi security) and then uncomment this test.
    // assertNull( null, null );
    // }
    /**
     * Test removing an arrayDesign with correct authorization. The security interceptor should be called on this
     * method, as should the PersistAclInterceptorBackend. Does not use mock objects because I need to remove an element
     * from a live database.
     * 
     * @throws Exception
     */
    // public void testRemoveArrayDesignWithoutMock() throws Exception {
    // BeanFactory ctx = SpringContextUtil.getApplicationContext();
    //
    // ManualAuthenticationProcessing manAuthentication = ( ManualAuthenticationProcessing ) ctx
    // .getBean( "manualAuthenticationProcessing" );
    //
    // manAuthentication.validateRequest( "pavlab", "pavlab" );
    //
    // ArrayDesignService ads = ( ArrayDesignService ) ctx.getBean( "arrayDesignService" );
    //
    // Collection<ArrayDesign> col = ads.getAllArrayDesigns();
    // if ( col.size() == 0 )
    // log.info( "There are no arrayDesigns in database" );
    //
    // else {
    // Iterator iter = col.iterator();
    // ArrayDesign ad = ( ArrayDesign ) iter.next();
    //
    // ads.removeArrayDesign( ad );
    // }
    // }
    /**
     * Testing the collections framework. Mock objects not used.
     * 
     * @throws
     */
    // public void testCollectionsWithArrayDesignsWithoutMock() throws Exception {
    // BeanFactory ctx = SpringContextUtil.getApplicationContext();
    //
    // ManualAuthenticationProcessing manAuthentication = ( ManualAuthenticationProcessing ) ctx
    // .getBean( "manualAuthenticationProcessing" );
    //
    // manAuthentication.validateRequest( "pavlab", "pavlab" );
    //
    // ArrayDesignService ads = ( ArrayDesignService ) ctx.getBean( "arrayDesignService" );
    //
    // /* Collection comparison */
    //
    // Collection<ArrayDesign> col = ( Collection ) ads.getAllArrayDesigns();
    // log.info( "Interface Collection" );
    // for ( ArrayDesign ad : col ) {
    // log.info( ad );
    // }
    //
    // // not ordered, no duplicates
    // HashSet<ArrayDesign> hs = new HashSet( ads.getAllArrayDesigns() );
    // log.info( "Interface: Set, Implementation: HashSet" );
    // for ( ArrayDesign ad : hs ) {
    // log.info( ad );
    // }
    // // sorted, contains duplicates
    // // TreeSet<ArrayDesign> ts = new TreeSet(ads.getAllArrayDesigns());
    // // log.info("Interface: SortedSet, Implementation: TreeSet");
    // // for ( ArrayDesign ad : ts ) {
    // // log.info(ad);
    // // }
    //
    // // ordered (based on insertion).
    // LinkedList<ArrayDesign> ll = new LinkedList( ads.getAllArrayDesigns() );
    // log.info( "Interface: List, Implementation: LinkedList" );
    // for ( ArrayDesign ad : ll ) {
    // log.info( ad );
    // }
    // }
    /**
     * Save an array design with username KiranKeshav
     * 
     * @throws Exception
     */
//    public void testSaveArrayDesignWithoutMock() throws Exception {
//        BeanFactory ctx = SpringContextUtil.getApplicationContext();
//
//        ManualAuthenticationProcessing manAuthentication = ( ManualAuthenticationProcessing ) ctx
//                .getBean( "manualAuthenticationProcessing" );
//
//        manAuthentication.validateRequest( "KiranKeshav", "keshav" );
//
//        ArrayDesignService ads = ( ArrayDesignService ) ctx.getBean( "arrayDesignService" );
//
//        ArrayDesign arrayDesign = ArrayDesign.Factory.newInstance();
//        arrayDesign.setName( "AD Foo" );
//        arrayDesign.setDescription( "a test ArrayDesign" );
//
//        CompositeSequence cs1 = CompositeSequence.Factory.newInstance();
//        cs1.setName( "DE Bar1" );
//
//        CompositeSequence cs2 = CompositeSequence.Factory.newInstance();
//        cs2.setName( "DE Bar2" );
//
//        Collection<DesignElement> col = new HashSet();
//        col.add( cs1 );
//        col.add( cs2 );
//        // Note this sequence. Remember, inverse="true" if using this. If you do not make
//        // an explicit call to cs1(2).setArrayDesign(arrayDesign), then inverse="false" must be set.
//        cs1.setArrayDesign( arrayDesign );
//        cs2.setArrayDesign( arrayDesign );
//        arrayDesign.setDesignElements( col );
//
//        ads.saveArrayDesign( arrayDesign );
//    }

    /**
     * Tests getting all design elements given authorization on an array design. Mock objects not used because I need
     * the objects from the database.
     * 
     * @throws Exception
     */
    public void testGetAllDesignElementsFromArrayDesignsWithoutMock() throws Exception {
        BeanFactory ctx = SpringContextUtil.getApplicationContext();

        ManualAuthenticationProcessing manAuthentication = ( ManualAuthenticationProcessing ) ctx
                .getBean( "manualAuthenticationProcessing" );

        manAuthentication.validateRequest( "pavlab", "pavlab" );

        ArrayDesignService ads = ( ArrayDesignService ) ctx.getBean( "arrayDesignService" );
        CompositeSequenceService css = ( CompositeSequenceService ) ctx.getBean( "compositeSequenceService" );

        Collection<CompositeSequence> col = css.getAllCompositeSequences();
        log.info( col.size() );
        for ( CompositeSequence cs : col ) {
            log.debug( cs );
        }

    }
}