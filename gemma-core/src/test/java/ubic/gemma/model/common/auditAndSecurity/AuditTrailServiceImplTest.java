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
package ubic.gemma.model.common.auditAndSecurity;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import ubic.gemma.model.common.auditAndSecurity.eventType.ArrayDesignGeneMappingEvent;
import ubic.gemma.model.common.auditAndSecurity.eventType.ArrayDesignGeneMappingEventImpl;
import ubic.gemma.model.common.auditAndSecurity.eventType.AuditEventType;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.testing.BaseSpringContextTest;

/**
 * @author pavlidis
 * @version $Id$
 */
public class AuditTrailServiceImplTest extends BaseSpringContextTest {

    ArrayDesign auditable;
    AuditTrailService auditTrailService;
    private int size;

    @Override
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        endTransaction();
        auditable = ArrayDesign.Factory.newInstance();
        auditable.setName( "testing audit " + RandomStringUtils.randomAlphanumeric( 32 ) );
        auditable.setShortName( RandomStringUtils.randomAlphanumeric( 8 ) );
        auditable = ( ArrayDesign ) this.persisterHelper.persist( auditable );
        auditTrailService = ( AuditTrailService ) this.getBean( "auditTrailService" );
        size = auditable.getAuditTrail().getEvents().size();
    }

    public final void testAddUpdateEventAuditableString() {
        AuditEvent ev = auditTrailService.addUpdateEvent( auditable, "nothing special, just testing" );
        assertNotNull( ev.getId() );
        assertEquals( size + 1, auditable.getAuditTrail().getEvents().size() );
    }

    public final void testAddUpdateEventAuditableAuditEventTypeString() {
        AuditEventType f = ArrayDesignGeneMappingEvent.Factory.newInstance();
        AuditEvent ev = auditTrailService.addUpdateEvent( auditable, f, "nothing special, just testing" );
        assertNotNull( ev.getId() );
        AuditTrail auditTrail = auditable.getAuditTrail();
        assertEquals( size + 1, auditTrail.getEvents().size() );
        assertEquals( ArrayDesignGeneMappingEventImpl.class, ( ( List<AuditEvent> ) auditTrail.getEvents() ).get( size )
                .getEventType().getClass() );
    }

}
