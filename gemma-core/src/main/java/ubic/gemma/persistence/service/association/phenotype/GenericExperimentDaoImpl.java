/*
 * The Gemma project
 * 
 * Copyright (c) 2009 University of British Columbia
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
package ubic.gemma.persistence.service.association.phenotype;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ubic.gemma.model.association.phenotype.GenericExperiment;
import ubic.gemma.persistence.service.AbstractDao;

import java.util.Collection;

/**
 * @author ?
 */
@Repository
public class GenericExperimentDaoImpl extends AbstractDao<GenericExperiment> implements GenericExperimentDao {

    @Autowired
    public GenericExperimentDaoImpl( SessionFactory sessionFactory ) {
        super( GenericExperiment.class, sessionFactory );
    }

    /**
     * Find all Investigations for a specific pubmed
     */
    @Override
    public Collection<GenericExperiment> findByPubmedID( String pubmed ) {

        Criteria genericExperiment = this.getSessionFactory().getCurrentSession()
                .createCriteria( GenericExperiment.class );
        genericExperiment.setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY )
                .createCriteria( "primaryPublication" ).createCriteria( "pubAccession" )
                .add( Restrictions.like( "accession", pubmed ) );

        //noinspection unchecked
        return genericExperiment.list();
    }

}
