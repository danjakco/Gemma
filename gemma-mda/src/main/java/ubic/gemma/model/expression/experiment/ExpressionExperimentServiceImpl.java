/*
 * The Gemma project.
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
package ubic.gemma.model.expression.experiment;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.model.common.auditAndSecurity.Contact;
import ubic.gemma.model.common.description.DatabaseEntry;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.bioAssay.BioAssay;

/**
 * @author pavlidis
 * @author keshav
 * @version $Id$
 * @see ubic.gemma.model.expression.experiment.ExpressionExperimentService
 */
public class ExpressionExperimentServiceImpl extends
        ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase {
    private Log log = LogFactory.getLog( this.getClass() );

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleFindByInvestigator(ubic.gemma.model.common.auditAndSecurity.Contact)
     */
    @Override
    protected Collection handleFindByInvestigator( Contact investigator ) throws Exception {
        return this.getExpressionExperimentDao().findByInvestigator( investigator );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleFindByAccession(ubic.gemma.model.common.description.DatabaseEntry)
     */
    @Override
    protected ExpressionExperiment handleFindByAccession( DatabaseEntry accession ) throws Exception {
        return this.getExpressionExperimentDao().findByAccession( accession );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleFindByName(java.lang.String)
     */
    @Override
    protected ExpressionExperiment handleFindByName( String name ) throws Exception {
        return this.getExpressionExperimentDao().findByName( name );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleFindById(java.lang.Long)
     */
    @Override
    protected ExpressionExperiment handleFindById( Long id ) throws Exception {
        return this.getExpressionExperimentDao().findById( id );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleFindOrCreate(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    @Override
    protected ExpressionExperiment handleFindOrCreate( ExpressionExperiment expressionExperiment ) throws Exception {
        return this.getExpressionExperimentDao().findOrCreate( expressionExperiment );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleUpdate(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    @Override
    protected void handleUpdate( ExpressionExperiment expressionExperiment ) throws Exception {

        this.getExpressionExperimentDao().update( expressionExperiment );
        loadLazyHibernateCollections( expressionExperiment );// TODO use lazy=false instead
    }

    /**
     * Collections with lazy=true are initially not filled with values from the database. An operation such as size()
     * will load the collection.
     * 
     * @param expressionExperiment
     */
    private void loadLazyHibernateCollections( ExpressionExperiment expressionExperiment ) {

        Collection<BioAssay> bioassays = expressionExperiment.getBioAssays();
        for ( BioAssay bioassay : bioassays ) {
            bioassay.getArrayDesignsUsed().size();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleGetAllExpressionExperiments()
     */
    @Override
    protected Collection handleLoadAll() throws Exception {
        return this.getExpressionExperimentDao().loadAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleDelete(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    @Override
    protected void handleDelete( ExpressionExperiment expressionExperiment ) throws Exception {
        this.getExpressionExperimentDao().remove( expressionExperiment );

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleRead(java.lang.Long)
     */
    @Override
    protected ExpressionExperiment handleRead( Long id ) throws Exception {
        return ( ExpressionExperiment ) this.getExpressionExperimentDao().load( id );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleCreate(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    @Override
    protected ExpressionExperiment handleCreate( ExpressionExperiment expressionExperiment ) throws Exception {
        return ( ExpressionExperiment ) this.getExpressionExperimentDao().create( expressionExperiment );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleFind(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    @Override
    protected ExpressionExperiment handleFind( ExpressionExperiment expressionExperiment ) throws Exception {
        return this.getExpressionExperimentDao().find( expressionExperiment );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentServiceBase#handleGetArrayDesignsUsed(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    @Override
    protected Collection<ArrayDesign> handleGetArrayDesignsUsed( ExpressionExperiment expressionExperiment ) {
        Collection<ArrayDesign> result = new HashSet<ArrayDesign>();
        for ( BioAssay ba : expressionExperiment.getBioAssays() ) {
            result.addAll( ba.getArrayDesignsUsed() );
        }
        return result;
    }
}
