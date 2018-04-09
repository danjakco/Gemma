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
package ubic.gemma.persistence.service.common.description;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import ubic.gemma.model.common.description.LocalFile;
import ubic.gemma.persistence.service.AbstractDao;
import ubic.gemma.persistence.util.BusinessKey;

import java.util.List;

/**
 * @author pavlidis
 */
@Repository
public class LocalFileDaoImpl extends AbstractDao<LocalFile> implements LocalFileDao {

    @Autowired
    public LocalFileDaoImpl( SessionFactory sessionFactory ) {
        super( LocalFile.class, sessionFactory );
    }

    @Override
    public LocalFile find( LocalFile localFile ) {

        BusinessKey.checkValidKey( localFile );

        HibernateTemplate t = new HibernateTemplate( this.getSessionFactory() );
        t.setFlushMode( HibernateAccessor.FLUSH_COMMIT );
        List<?> results;
        if ( localFile.getRemoteURL() == null ) {
            results = t.findByNamedParam( "from LocalFile where localURL=:u and remoteURL is null ", "u",
                    localFile.getLocalURL() );
        } else if ( localFile.getLocalURL() == null ) {
            results = t.findByNamedParam( "from LocalFile where localURL is null and remoteURL=:r", "r",
                    localFile.getRemoteURL() );
        } else {
            results = t
                    .findByNamedParam( "from LocalFile where localURL=:u and remoteURL=:r", new String[] { "u", "r" },
                            new Object[] { localFile.getLocalURL(), localFile.getRemoteURL() } );
        }

        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                        "More than one instance of '" + LocalFile.class.getName()
                                + "' was found when executing query" );

            } else if ( results.size() == 1 ) {
                result = results.get( 0 );
            }
        }
        return ( LocalFile ) result;

    }

    @Override
    public LocalFile findOrCreate( ubic.gemma.model.common.description.LocalFile localFile ) {
        if ( localFile == null )
            throw new IllegalArgumentException();
        LocalFile existingLocalFile = this.find( localFile );
        if ( existingLocalFile != null ) {
            if ( AbstractDao.log.isDebugEnabled() )
                AbstractDao.log.debug( "Found existing localFile: " + existingLocalFile.getLocalURL() );
            return existingLocalFile;
        }
        if ( AbstractDao.log.isDebugEnabled() )
            AbstractDao.log.debug( "Creating new localFile: " + localFile.getLocalURL() );
        return this.create( localFile );
    }

}