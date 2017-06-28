/*
 * The Gemma project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.gemma.persistence.service.common.auditAndSecurity;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubic.gemma.model.common.auditAndSecurity.Person;

/**
 * @author pavlidis
 * @author keshav
 * @version $Id$
 * @see PersonService
 */
@Service
public class PersonServiceImpl extends PersonServiceBase {

    @Override
    @Transactional(readOnly = true)
    public Person load( Long id ) {
        return this.getPersonDao().load( id );
    }

    @Override
    @Transactional
    public void update( Person p ) {
        this.getPersonDao().update( p );

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * PersonServiceBase#handleCreate(ubic.gemma.model.common.auditAndSecurity
     * .Person)
     */
    @Override
    protected Person handleCreate( Person person ) {
        return this.getPersonDao().create( person );
    }

    @Override
    protected Collection<Person> handleFindByFullName( String name, String lastName ) {
        return this.getPersonDao().findByFullName( name, lastName );
    }

    /*
     * (non-Javadoc)
     * 
     * @see PersonServiceBase#handleExpfindByName(java.lang.String,
     * java.lang.String, java.lang.String)
     */

    /**
     * @see PersonService#findOrCreate(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    protected Person handleFindOrCreate( Person person ) {
        return this.getPersonDao().findOrCreate( person );
    }

    /*
     * (non-Javadoc)
     * 
     * @see PersonServiceBase#handleLoadAll()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Collection<Person> handleLoadAll() {
        return ( Collection<Person> ) this.getPersonDao().loadAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * PersonServiceBase#handleRemove(ubic.gemma.model.common.auditAndSecurity
     * .Person)
     */
    @Override
    protected void handleRemove( Person person ) {
        this.getPersonDao().remove( person );
    }

    /**
     * @see PersonService#removePerson(ubic.gemma.model.common.auditAndSecurity.Person)
     */
    protected void handleRemovePerson( ubic.gemma.model.common.auditAndSecurity.Person person ) {
        this.getPersonDao().remove( person );
    }

}