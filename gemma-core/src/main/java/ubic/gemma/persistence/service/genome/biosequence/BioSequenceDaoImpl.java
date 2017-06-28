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
package ubic.gemma.persistence.service.genome.biosequence;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ubic.gemma.model.common.description.DatabaseEntry;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.biosequence.BioSequence;
import ubic.gemma.model.genome.sequenceAnalysis.BioSequenceValueObject;
import ubic.gemma.persistence.util.BusinessKey;
import ubic.gemma.persistence.util.EntityUtils;

import java.util.*;

/**
 * @author pavlidis
 * @see ubic.gemma.model.genome.biosequence.BioSequence
 */
@Repository
public class BioSequenceDaoImpl extends BioSequenceDaoBase {

    @Autowired
    public BioSequenceDaoImpl( SessionFactory sessionFactory ) {
        super( sessionFactory );
    }

    @SuppressWarnings("unchecked")
    @Override
    public BioSequence find( BioSequence bioSequence ) {

        BusinessKey.checkValidKey( bioSequence );

        Criteria queryObject = BusinessKey.createQueryObject( getSessionFactory().getCurrentSession(), bioSequence );
        queryObject.setReadOnly( true );
        queryObject.setFlushMode( FlushMode.MANUAL );
        /*
         * this initially matches on name and taxon only.
         */
        java.util.List<?> results = queryObject.list();
        Object result = null;
        if ( results != null ) {
            if ( results.size() > 1 ) {
                debug( bioSequence, results );

                // Try to find the best match. See BusinessKey for more
                // explanation of why this is needed.
                BioSequence match = null;
                for ( BioSequence res : ( Collection<BioSequence> ) results ) {
                    if ( res.equals( bioSequence ) ) {
                        if ( match != null ) {
                            log.warn( "More than one sequence in the database matches " + bioSequence
                                    + ", returning arbitrary match: " + match );
                            break;
                        }
                        match = res;
                    }
                }

                return match;

            } else if ( results.size() == 1 ) {
                result = results.iterator().next();
            }
        }
        return ( BioSequence ) result;
    }

    @Override
    public BioSequence findByAccession( DatabaseEntry databaseEntry ) {
        BusinessKey.checkValidKey( databaseEntry );

        String queryString;
        List<BioSequence> results;
        if ( databaseEntry.getId() != null ) {
            queryString = "select b from BioSequenceImpl b inner join fetch b.sequenceDatabaseEntry d inner join fetch d.externalDatabase e  where d=:dbe";
            //noinspection unchecked
            results = this.getSessionFactory().getCurrentSession().createQuery( queryString )
                    .setParameter( "dbe", databaseEntry ).list();
        } else {
            queryString = "select b from BioSequenceImpl b inner join fetch b.sequenceDatabaseEntry d "
                    + "inner join fetch d.externalDatabase e where d.accession = :acc and e.name = :dbName";
            //noinspection unchecked
            results = this.getSessionFactory().getCurrentSession().createQuery( queryString )
                    .setParameter( "acc", databaseEntry.getAccession() )
                    .setParameter( "dbName", databaseEntry.getExternalDatabase().getName() ).list();
        }

        if ( results.size() > 1 ) {
            debug( null, results );
            log.warn( "More than one instance of '" + BioSequence.class.getName()
                    + "' was found when executing query for accession=" + databaseEntry.getAccession() );

            // favor the one with name matching the accession.
            for ( Object object : results ) {
                BioSequence bs = ( BioSequence ) object;
                if ( bs.getName().equals( databaseEntry.getAccession() ) ) {
                    return bs;
                }
            }

            log.error( "No biosequence really matches " + databaseEntry.getAccession() );
            return null;

        } else if ( results.size() == 1 ) {
            return results.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public BioSequence findOrCreate( BioSequence bioSequence ) {
        BioSequence existingBioSequence = this.find( bioSequence );
        if ( existingBioSequence != null ) {
            return existingBioSequence;
        }
        if ( log.isDebugEnabled() )
            log.debug( "Creating new: " + bioSequence );
        return create( bioSequence );
    }

    @Override
    protected Map<Gene, Collection<BioSequence>> handleFindByGenes( Collection<Gene> genes ) {
        if ( genes == null || genes.isEmpty() )
            return new HashMap<Gene, Collection<BioSequence>>();

        Map<Gene, Collection<BioSequence>> results = new HashMap<Gene, Collection<BioSequence>>();

        int batchSize = 500;

        if ( genes.size() <= batchSize ) {
            findByGenesBatch( genes, results );
            return results;
        }

        Collection<Gene> batch = new HashSet<Gene>();

        for ( Gene gene : genes ) {
            batch.add( gene );
            if ( batch.size() == batchSize ) {
                findByGenesBatch( genes, results );
                batch.clear();
            }
        }

        if ( !batch.isEmpty() ) {
            findByGenesBatch( genes, results );
        }

        return results;
    }

    @Override
    protected Collection<BioSequence> handleFindByName( String name ) {
        return this.findByProperty( "name", name );
    }

    @Override
    protected Collection<Gene> handleGetGenesByAccession( String search ) {
        //noinspection unchecked
        return this.getSessionFactory().getCurrentSession().createQuery(
                "select distinct gene from Gene as gene inner join gene.products gp,  BioSequence2GeneProduct as bs2gp"
                        + " inner join bs2gp.bioSequence bs "
                        + "inner join bs.sequenceDatabaseEntry de where gp=bs2gp.geneProduct "
                        + " and de.accession = :search " ).setParameter( "search", search ).list();
    }

    @Override
    protected Collection<Gene> handleGetGenesByName( String search ) {
        try {
            //noinspection unchecked
            return this.getSessionFactory().getCurrentSession().createQuery(
                    "select distinct gene from Gene as gene inner join gene.products gp,  BioSequence2GeneProduct as bs2gp where gp=bs2gp.geneProduct "
                            + " and bs2gp.bioSequence.name like :search " ).setString( "search", search ).list();

        } catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

    @Override
    protected BioSequence handleThaw( final BioSequence bioSequence ) {
        if ( bioSequence == null )
            return null;
        if ( bioSequence.getId() == null )
            return bioSequence;

        List<?> res = this.getHibernateTemplate().findByNamedParam( "select b from BioSequenceImpl b "
                        + " left join fetch b.taxon tax left join fetch tax.externalDatabase left join fetch tax.parentTaxon pt "
                        + " left join fetch pt.externalDatabase "
                        + " left join fetch b.sequenceDatabaseEntry s left join fetch s.externalDatabase"
                        + " left join fetch b.bioSequence2GeneProduct bs2gp "
                        + " left join fetch bs2gp.geneProduct gp left join fetch gp.gene g"
                        + " left join fetch g.aliases left join fetch g.accessions  where b.id=:bid", "bid",
                bioSequence.getId() );

        return ( BioSequence ) res.iterator().next();
    }

    @Override
    protected Collection<BioSequence> handleThaw( final Collection<BioSequence> bioSequences ) {
        if ( bioSequences.isEmpty() )
            return new HashSet<BioSequence>();

        Collection<BioSequence> result = new HashSet<BioSequence>();
        Collection<BioSequence> batch = new HashSet<BioSequence>();

        for ( BioSequence g : bioSequences ) {
            batch.add( g );
            if ( batch.size() == 100 ) {
                result.addAll( doThawBatch( batch ) );
                batch.clear();
            }
        }

        if ( !batch.isEmpty() ) {
            result.addAll( doThawBatch( batch ) );
        }

        return result;
    }

    private Collection<? extends BioSequence> doThawBatch( Collection<BioSequence> batch ) {
        //noinspection unchecked
        return this.getHibernateTemplate().findByNamedParam( "select b from BioSequenceImpl b "
                        + " left join fetch b.taxon tax left join fetch tax.externalDatabase left join fetch tax.parentTaxon left join fetch b.sequenceDatabaseEntry s "
                        + " left join fetch s.externalDatabase" + " left join fetch b.bioSequence2GeneProduct bs2gp "
                        + " left join fetch bs2gp.geneProduct gp left join fetch gp.gene g"
                        + " left join fetch g.aliases left join fetch g.accessions  where b.id in (:bids)", "bids",
                EntityUtils.getIds( batch ) );
    }

    private void debug( BioSequence query, List<?> results ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "\nMultiple BioSequences found matching query:\n" );

        if ( query != null ) {
            sb.append( "\tQuery: ID=" ).append( query.getId() ).append( " Name=" ).append( query.getName() );
            if ( StringUtils.isNotBlank( query.getSequence() ) )
                sb.append( " Sequence=" ).append( StringUtils.abbreviate( query.getSequence(), 10 ) );
            if ( query.getSequenceDatabaseEntry() != null )
                sb.append( " acc=" ).append( query.getSequenceDatabaseEntry().getAccession() );
            sb.append( "\n" );
        }

        for ( Object object : results ) {
            BioSequence entity = ( BioSequence ) object;
            sb.append( "\tMatch: ID=" ).append( entity.getId() ).append( " Name=" ).append( entity.getName() );
            if ( StringUtils.isNotBlank( entity.getSequence() ) )
                sb.append( " Sequence=" ).append( StringUtils.abbreviate( entity.getSequence(), 10 ) );
            if ( entity.getSequenceDatabaseEntry() != null )
                sb.append( " acc=" ).append( entity.getSequenceDatabaseEntry().getAccession() );
            sb.append( "\n" );
        }
        if ( log.isDebugEnabled() )
            log.debug( sb.toString() );
    }

    private void findByGenesBatch( Collection<Gene> genes, Map<Gene, Collection<BioSequence>> results ) {
        //noinspection unchecked
        List<Object[]> qr = this.getSessionFactory().getCurrentSession().createQuery(
                "select distinct gene,bs from Gene gene inner join fetch gene.products ggp,"
                        + " BioSequenceImpl bs inner join bs.bioSequence2GeneProduct bs2gp inner join bs2gp.geneProduct bsgp"
                        + " where ggp=bsgp and gene in (:genes)" ).setParameterList( "genes", genes ).list();
        for ( Object[] oa : qr ) {
            Gene g = ( Gene ) oa[0];
            BioSequence b = ( BioSequence ) oa[1];
            if ( !results.containsKey( g ) ) {
                results.put( g, new HashSet<BioSequence>() );
            }
            results.get( g ).add( b );
        }
    }

    @Override
    public BioSequenceValueObject loadValueObject( BioSequence entity ) {
        return BioSequenceValueObject.fromEntity( entity );
    }

    @Override
    public Collection<BioSequenceValueObject> loadValueObjects( Collection<BioSequence> entities ) {
        Collection<BioSequenceValueObject> vos = new LinkedHashSet<BioSequenceValueObject>();
        for ( BioSequence e : entities ) {
            vos.add( this.loadValueObject( e ) );
        }
        return vos;
    }
}