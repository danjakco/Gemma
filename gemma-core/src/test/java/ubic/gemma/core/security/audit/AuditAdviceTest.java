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
package ubic.gemma.core.security.audit;

import gemma.gsec.authentication.UserDetailsImpl;
import gemma.gsec.authentication.UserManager;
import gemma.gsec.authentication.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import ubic.gemma.persistence.service.common.auditAndSecurity.AuditEventService;
import ubic.gemma.persistence.service.common.auditAndSecurity.AuditTrailService;
import ubic.gemma.persistence.service.expression.experiment.ExpressionExperimentService;
import ubic.gemma.model.common.Auditable;
import ubic.gemma.model.common.auditAndSecurity.*;
import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.biomaterial.Treatment;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.FactorValue;
import ubic.gemma.core.testing.BaseSpringContextTest;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Test of adding audit events when objects are created, updated or deleted.
 * Note: this test used to use genes, but we removed auditability from genes.
 *
 * @author pavlidis
 */
public class AuditAdviceTest extends BaseSpringContextTest {

    @Autowired
    private UserManager userManager;

    @Autowired
    private AuditTrailService auditTrailService;

    @Autowired
    private AuditEventService auditEventService;

    @Autowired
    private ExpressionExperimentService expressionExperimentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void testAuditCreateAndDeleteExpressionExperiment() {
        ExpressionExperiment ee = this.getTestPersistentCompleteExpressionExperiment( true );

        Collection<Long> trailIds = new HashSet<>();
        Collection<Long> eventIds = new HashSet<>();

        checkEEAuditTrails( ee, trailIds, eventIds );

        assertEquals( 1, ee.getAuditTrail().getEvents().size() );

        ee = expressionExperimentService.load( ee.getId() ); // so not thawed, which tests lazy issue in the advice.

        ee.setShortName( randomName() );

        expressionExperimentService.update( ee );

        ee = expressionExperimentService.thawLite( ee );

        // make sure we added an update event on the ee
        assertEquals( 2, auditEventService.getEvents( ee ).size() );

        // check that we haven't added an update event to the design -- only a create.

        assertEquals( 1, auditEventService.getEvents( ee.getExperimentalDesign() ).size() );

        expressionExperimentService.remove( ee );

        checkDeletedTrails( trailIds, eventIds );

    }

    @Test
    public void testCascadingCreateOnUpdate() throws Exception {
        ExpressionExperiment ee = this.getTestPersistentCompleteExpressionExperiment( false );

        ee = this.expressionExperimentService.load( ee.getId() );
        ee = expressionExperimentService.thawLite( ee );

        // should have create only
        assertEquals( 1, ee.getAuditTrail().getEvents().size() );

        BioAssay ba = BioAssay.Factory.newInstance();
        String name = RandomStringUtils.randomAlphabetic( 20 );
        ba.setName( name );
        ba.setArrayDesignUsed( ee.getBioAssays().iterator().next().getArrayDesignUsed() );
        ee.getBioAssays().add( ba );

        this.expressionExperimentService.update( ee );
        assertNotNull( ee.getAuditTrail() );

        // should have create and 1 updates
        assertEquals( 2, ee.getAuditTrail().getEvents().size() );

        Session session = sessionFactory.openSession();
        session.update( ee );

        for ( BioAssay bioa : ee.getBioAssays() ) {
            assertNotNull( bioa.getAuditTrail() );
            Collection<AuditEvent> events = bioa.getAuditTrail().getEvents();
            assertEquals( 1, events.size() );
            for ( AuditEvent e : events ) {
                assertNotNull( e.getId() );
                assertNotNull( e.getAction() );
            }
        }

        session.close();

        this.expressionExperimentService.update( ee );
        this.expressionExperimentService.update( ee );
        this.expressionExperimentService.update( ee );

        assertEquals( 5, ee.getAuditTrail().getEvents().size() );

        /*
         * Check we didn't get any extra events added to children.
         */
        for ( BioAssay prod : ee.getBioAssays() ) {
            assertEquals( 1, prod.getAuditTrail().getEvents().size() );
        }
    }

    /**
     * Test of simple case.
     */
    @Test
    public void testCascadingCreateWithAssociatedAuditable() throws Exception {
        ExpressionExperiment ee = this.getTestPersistentCompleteExpressionExperiment( false );

        ee = this.expressionExperimentService.load( ee.getId() );
        ee = expressionExperimentService.thawLite( ee );

        assertEquals( 16, ee.getBioAssays().size() );

        assertNotNull( ee.getBioAssays().iterator().next().getId() );

        assertEquals( 1, ee.getAuditTrail().getEvents().size() );

        for ( BioAssay prod : ee.getBioAssays() ) {
            assertNotNull( prod.getAuditTrail() );

            Collection<AuditEvent> events = this.auditTrailService.getEvents( prod );
            assertEquals( 1, events.size() );
            for ( AuditEvent e : events ) {
                assertNotNull( e.getId() );
                assertEquals( AuditAction.CREATE, e.getAction() );
            }
        }

    }

    /**
     * FIXME this test occasionally fails on bamboo.
     */
    @Test
    public void testSimpleAuditCreateUpdateUser() throws Exception {

        String USERNAME = RandomStringUtils.randomAlphabetic( RANDOM_STRING_LENGTH );

        String encodedPassword = passwordEncoder.encodePassword( USERNAME, USERNAME );
        UserDetailsImpl u = new UserDetailsImpl( encodedPassword, USERNAME, true, null, null, null, new Date() );
        userManager.createUser( u );

        User user = ( User ) userService.findByUserName( USERNAME );

        List<AuditEvent> events = auditTrailService.getEvents( user );

        assertEquals( "Should have just one event, a 'create'", 1, events.size() );
        assertEquals( AuditAction.CREATE, events.get( 0 ).getAction() );

        assertNotNull( events.get( 0 ).getId() );

        user.setEmail( RandomStringUtils.randomNumeric( 10 ) ); // change something.
        userService.update( user );

        events = auditTrailService.getEvents( user );

        int sizeAfterFirstUpdate = events.size();

        assertEquals( "Should have a 'create' and an 'update'", 2, sizeAfterFirstUpdate );

        // debugging...
        if ( !events.get( 0 ).getAction().equals( AuditAction.CREATE ) ) {
            log.info( "First event wasn't 'C', dumping trail" );
            for ( AuditEvent ae : events ) {
                log.info( ae );
            }
        }

        assertEquals( AuditAction.CREATE, events.get( 0 ).getAction() );
        assertEquals( AuditAction.UPDATE, events.get( sizeAfterFirstUpdate - 1 ).getAction() );

        // third time.
        user.setEmail( RandomStringUtils.randomNumeric( 10 ) ); // change something.
        userService.update( user );

        events = auditTrailService.getEvents( user );

        assertEquals( 3, user.getAuditTrail().getEvents().size() );
        assertEquals( AuditAction.UPDATE, events.get( 2 ).getAction() );

        // cleanup
        userManager.deleteUser( USERNAME );
    }

    @Test
    public void testSimpleAuditFindOrCreate() {
        ExpressionExperiment ee = ExpressionExperiment.Factory.newInstance();
        ee.setDescription( "From test" );
        ee.setName( RandomStringUtils.randomAlphabetic( 20 ) );
        ee = expressionExperimentService.findOrCreate( ee );

        assertNotNull( ee.getAuditTrail() );
        assertEquals( 1, ee.getAuditTrail().getEvents().size() );
        assertNotNull( ee.getCurationDetails() );
        assertNotNull( ee.getCurationDetails().getId() );
        assertNotNull( ee.getCurationDetails().getLastUpdated() );
        assertNotNull( ee.getAuditTrail().getCreationEvent().getId() );
    }

    /**
     * Torture test. Passes fine with a single thread.
     */
    @Test
    public void testAuditFindOrCreateConcurrentTorture() throws Exception {
        int numThreads = 14; // too high and we run out of connections, which is not what we're testing.
        final int numExperimentsPerThread = 5;
        final int numUpdates = 10;
        final Random random = new Random();
        final AtomicInteger c = new AtomicInteger( 0 );
        final AtomicBoolean failed = new AtomicBoolean( false );
        Collection<Thread> threads = new HashSet<>();
        for ( int i = 0; i < numThreads; i++ ) {

            Thread.sleep( random.nextInt( 100 ) );

            Thread k = new Thread( new Runnable() {
                @Override
                public void run() {
                    try {
                        for ( int j = 0; j < numExperimentsPerThread; j++ ) {
                            log.debug( "Starting experiment " + j );
                            ExpressionExperiment ee = ExpressionExperiment.Factory.newInstance();
                            ee.setDescription( "From test" );
                            ee.setShortName( RandomStringUtils.randomAlphabetic( 20 ) );
                            ee.setName( RandomStringUtils.randomAlphabetic( 20 ) );
                            ee = expressionExperimentService.findOrCreate( ee );

                            assertNotNull( ee.getAuditTrail() );
                            assertEquals( 1, ee.getAuditTrail().getEvents().size() );
                            assertNotNull( ee.getCurationDetails() );
                            assertNotNull( ee.getCurationDetails().getId() );
                            assertNotNull( ee.getCurationDetails().getLastUpdated() );
                            assertNotNull( ee.getAuditTrail().getCreationEvent().getId() );

                            for ( int q = 0; q < numUpdates; q++ ) {
                                Thread.sleep( random.nextInt( 5 ) );
                                log.debug( "Update: experiment " + j );
                                expressionExperimentService.update( ee );
                                c.incrementAndGet();
                            }
                            log.debug( "Done with experiment " + j );
                        }
                    } catch ( Exception e ) {
                        failed.set( true );
                        log.error( "!!!!!!!!!!!!!!!!!!!!!! FAILED: " + e.getMessage() );
                        log.debug( e, e );
                        throw new RuntimeException( e );
                    }
                    log.debug( "Thread done." );
                }
            } );
            threads.add( k );

            k.start();
        }

        int waits = 0;
        int maxWaits = 20;
        int expectedEventCount = numThreads * numExperimentsPerThread * numUpdates;
        while ( c.get() < expectedEventCount && !failed.get() ) {
            Thread.sleep( 1000 );
            log.info( "Waiting ..." );
            if ( ++waits > maxWaits ) {
                for ( Thread t : threads ) {
                    if ( t.isAlive() )
                        t.interrupt();
                }
                fail( "Multithreaded failure: timed out." );
            }
        }

        log.debug( " &&&&& DONE &&&&&" );

        for ( Thread thread : threads ) {
            if ( thread.isAlive() )
                thread.interrupt();
        }

        if ( failed.get() || c.get() != expectedEventCount ) {
            fail( "Multithreaded loading failure: check logs for failure to recover from deadlock?" );
        } else {
            log.info( "TORTURE TEST PASSED!" );
        }

    }

    private void checkAuditTrail( Auditable c, Collection<Long> trailIds, Collection<Long> eventIds ) {

        AuditTrail auditTrail = c.getAuditTrail();
        assertNotNull( "No audit trail for " + c, auditTrail );

        trailIds.add( auditTrail.getId() );

        assertTrue( "Trail but no events for " + c, auditTrail.getEvents().size() > 0 );

        for ( AuditEvent ae : auditTrail.getEvents() ) {
            eventIds.add( ae.getId() );
        }

    }

    private boolean checkDeletedAuditTrail( Long atid ) {
        return this.simpleJdbcTemplate
                .queryForObject( "SELECT COUNT(*) FROM AUDIT_TRAIL WHERE ID = ?", Integer.class, atid ) == 0;
    }

    private boolean checkDeletedEvent( Long i ) {
        return this.simpleJdbcTemplate
                .queryForObject( "SELECT COUNT(*) FROM AUDIT_EVENT WHERE ID = ?", Integer.class, i ) == 0;
    }

    private void checkDeletedTrails( Collection<Long> trailIds, Collection<Long> eventIds ) {

        for ( Long id : trailIds ) {
            assertTrue( checkDeletedAuditTrail( id ) );
        }

        for ( Long id : eventIds ) {
            assertTrue( checkDeletedEvent( id ) );
        }

    }

    private void checkEEAuditTrails( ExpressionExperiment ee, Collection<Long> trailIds, Collection<Long> eventIds ) {
        checkAuditTrail( ee, trailIds, eventIds );

        for ( BioAssay ba : ee.getBioAssays() ) {
            checkAuditTrail( ba, trailIds, eventIds );
            BioMaterial bm = ba.getSampleUsed();
            checkAuditTrail( bm, trailIds, eventIds );
            for ( Characteristic c : bm.getCharacteristics() ) {
                checkAuditTrail( c, trailIds, eventIds );
            }

            for ( Treatment t : bm.getTreatments() ) {
                checkAuditTrail( t, trailIds, eventIds );
                checkAuditTrail( t.getAction(), trailIds, eventIds );
                // for ( CompoundMeasurement cm : t.getCompoundMeasurements() ) {
                // checkAuditTrail( cm.getCompound().getCompoundIndices(), trailIds, eventIds );
                // }
            }

        }

        Collection<ExperimentalFactor> experimentalFactors = ee.getExperimentalDesign().getExperimentalFactors();
        assertTrue( experimentalFactors.size() > 0 );

        for ( ExperimentalFactor ef : experimentalFactors ) {
            checkAuditTrail( ef, trailIds, eventIds );
            for ( FactorValue fv : ef.getFactorValues() ) {
                for ( Characteristic c : fv.getCharacteristics() ) {
                    checkAuditTrail( c, trailIds, eventIds );
                }
            }
        }

    }

}
