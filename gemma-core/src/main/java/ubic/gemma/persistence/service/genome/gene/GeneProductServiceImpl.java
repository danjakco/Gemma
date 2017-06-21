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
package ubic.gemma.persistence.service.genome.gene;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubic.gemma.model.common.description.DatabaseEntry;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.gene.GeneProduct;
import ubic.gemma.model.genome.sequenceAnalysis.AnnotationAssociation;
import ubic.gemma.model.genome.sequenceAnalysis.BlatAssociation;
import ubic.gemma.persistence.service.genome.biosequence.BioSequenceDao;
import ubic.gemma.persistence.service.genome.sequenceAnalysis.AnnotationAssociationDao;
import ubic.gemma.persistence.service.genome.sequenceAnalysis.BlatAssociationDao;

import java.util.Collection;
import java.util.HashSet;

/**
 * @see GeneProductService
 */
@Service
public class GeneProductServiceImpl extends GeneProductServiceBase {

    @Autowired
    public GeneProductServiceImpl( AnnotationAssociationDao annotationAssociationDao, BioSequenceDao bioSequenceDao,
            BlatAssociationDao blatAssociationDao, GeneProductDao geneProductDao ) {
        super( annotationAssociationDao, bioSequenceDao, blatAssociationDao, geneProductDao );
    }

    @Override
    @Transactional(readOnly = true)
    public GeneProduct findByGi( String ncbiGi ) {
        return this.geneProductDao.findByNcbiId( ncbiGi );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<GeneProduct> findByName( String name, Taxon taxon ) {
        return this.geneProductDao.findByName( name, taxon );
    }

    @Override
    @Transactional
    public void remove( Collection<GeneProduct> toRemove ) {
        Collection<BlatAssociation> associations = this.blatAssociationDao.find( toRemove );
        if ( !associations.isEmpty() ) {
            log.info( "Removing " + associations.size() + " blat associations involving up to " + toRemove.size()
                    + " products." );
            this.blatAssociationDao.remove( associations );
        }

        Collection<AnnotationAssociation> annotationAssociations = this.annotationAssociationDao.find( toRemove );
        if ( !annotationAssociations.isEmpty() ) {
            log.info(
                    "Removing " + annotationAssociations.size() + " annotationAssociations involving up to " + toRemove
                            .size() + " products." );
            this.annotationAssociationDao.remove( annotationAssociations );
        }

        // might need to add referenceAssociations also.

        // remove associations to database entries that are still associated with sequences.
        for ( GeneProduct gp : toRemove ) {
            this.thaw( gp );
            Collection<DatabaseEntry> accessions = gp.getAccessions();
            Collection<DatabaseEntry> toRelease = new HashSet<DatabaseEntry>();
            for ( DatabaseEntry de : accessions ) {
                if ( this.bioSequenceDao.findByAccession( de ) != null ) {
                    toRelease.add( de );
                }
            }
            gp.getAccessions().removeAll( toRelease );
            this.geneProductDao.remove( gp );

        }

    }

    @Override
    protected Collection<Gene> handleGetGenesByName( String search ) {
        return this.geneProductDao.getGenesByName( search );
    }

    @Override
    protected Collection<Gene> handleGetGenesByNcbiId( String search ) {
        return this.geneProductDao.getGenesByNcbiId( search );
    }

}