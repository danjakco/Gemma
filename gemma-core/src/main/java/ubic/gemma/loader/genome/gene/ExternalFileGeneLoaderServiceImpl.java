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
package ubic.gemma.loader.genome.gene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ubic.gemma.genome.gene.service.GeneService;
import ubic.gemma.genome.taxon.service.TaxonService;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.gene.GeneProduct;
import ubic.gemma.model.genome.gene.GeneProductType;
import ubic.gemma.persistence.Persister;

/**
 * Class to provide functionality to load genes from a tab delimited file. Typical usage is for non model organisms that
 * do not have genes in NCBI. Supports loading genes against a non species taxon such as a family e.g Salmonids. File
 * format is : Optional header which should be appended with a # to indicate not to process this line Then a line
 * containing 3 fields which should be 'Gene Symbol' 'Gene Name' 'UniProt id' (last is optional) separated by tabs. The
 * Class reads the file and looping through each line creates a gene (NCBI id is null) and one associated gene product.
 * The gene is populated with gene symbol, gene name, gene official name (gene symbol) and a description indicating that
 * this gene has been loaded from a text file. Then gene is associated with a gene product bearing the same name as the
 * gene symbol and persisted.
 * 
 * @author ldonnison
 * @version $Id$
 */
@Component
public class ExternalFileGeneLoaderServiceImpl implements ExternalFileGeneLoaderService {
    private static Log log = LogFactory.getLog( ExternalFileGeneLoaderServiceImpl.class.getName() );

    @Autowired
    private Persister persisterHelper;

    @Autowired
    private TaxonService taxonService;

    @Autowired
    private GeneService geneService;

    /* (non-Javadoc)
     * @see ubic.gemma.loader.genome.gene.ExternalFileGeneLoaderService#createGene(java.lang.String[], ubic.gemma.model.genome.Taxon)
     */
    @Override
    public Gene createGene( String[] fields, Taxon taxon ) {
        String geneSymbol = fields[0];
        String geneName = fields[1];
        String uniProt = "";
        if ( fields.length > 2 ) uniProt = fields[2];
        Gene gene = null;
        // need at least the gene symbol and gene name
        if ( !StringUtils.isBlank( geneSymbol ) && !StringUtils.isBlank( geneName ) ) {
            log.debug( "Creating gene " + geneSymbol );
            gene = geneService.findByOfficialSymbol( geneSymbol, taxon );
            if ( gene != null ) return null; // no need to create it.
            gene = Gene.Factory.newInstance();
            gene.setName( geneSymbol );
            gene.setOfficialSymbol( geneSymbol );
            gene.setOfficialName( StringUtils.lowerCase( geneName ) );
            gene.setDescription( "Imported from external annotation file" );
            gene.setTaxon( taxon );
            gene.setProducts( createGeneProducts( gene ) );
        } else {
            log.warn( "Line does not contain valid gene information; GeneSymbol is: " + geneSymbol + "GeneName is: "
                    + geneName + " Uni Prot id is  " + uniProt );
        }
        return gene;
    }

    /* (non-Javadoc)
     * @see ubic.gemma.loader.genome.gene.ExternalFileGeneLoaderService#createGeneProducts(ubic.gemma.model.genome.Gene)
     */
    @Override
    public Collection<GeneProduct> createGeneProducts( Gene gene ) {
        Collection<GeneProduct> geneProducts = new HashSet<GeneProduct>();
        GeneProduct geneProduct = GeneProduct.Factory.newInstance();
        geneProduct.setType( GeneProductType.RNA );
        geneProduct.setGene( gene );
        geneProduct.setName( gene.getName() );
        geneProduct.setDescription( "Gene product placeholder" );
        geneProducts.add( geneProduct );
        return geneProducts;
    }

    /* (non-Javadoc)
     * @see ubic.gemma.loader.genome.gene.ExternalFileGeneLoaderService#load(java.lang.String, java.lang.String)
     */
    @Override
    public int load( String geneFile, String taxonName ) throws Exception {
        String line = null;
        int linesSkipped = 0;
        log.info( "Starting loading gene file " + geneFile + " for taxon " + taxonName );
        BufferedReader bufferedReaderGene = readFile( geneFile );
        Taxon taxon = validateTaxon( taxonName );
        log.info( "Taxon and file validation passed for " + geneFile + " for taxon " + taxonName );
        int loadedGeneCount = 0;
        while ( ( line = bufferedReaderGene.readLine() ) != null ) {
            String[] lineContents = readLine( line );
            if ( lineContents != null ) {
                Gene gene = createGene( lineContents, taxon );
                if ( gene != null ) {
                    persisterHelper.persistOrUpdate( gene );
                    loadedGeneCount++;
                } else {
                    linesSkipped++;
                }
            }
        }
        updateTaxonWithGenesLoaded( taxon );
        log.info( "Finished loading gene file " + geneFile + " for taxon " + taxon + ". " + " Genes loaded: "
                + loadedGeneCount + " ,Lines skipped: " + linesSkipped );
        return loadedGeneCount;
    }

    /* (non-Javadoc)
     * @see ubic.gemma.loader.genome.gene.ExternalFileGeneLoaderService#updateTaxonWithGenesLoaded(ubic.gemma.model.genome.Taxon)
     */
    @Override
    public void updateTaxonWithGenesLoaded( Taxon taxon ) throws Exception {
        Collection<Taxon> childTaxa = taxonService.findChildTaxaByParent( taxon );
        // if this taxon has children flag not to use their genes
        if ( childTaxa != null && !childTaxa.isEmpty() ) {
            for ( Taxon childTaxon : childTaxa ) {
                if ( childTaxon != null && childTaxon.getIsGenesUsable() ) {
                    childTaxon.setIsGenesUsable( false );
                    taxonService.update( childTaxon );
                    log.warn( "Child taxa" + childTaxon + " genes have been loaded parent taxa should superseed" );
                }
            }
        }
        // set taxon flag indicating that use these taxons genes
        if ( !taxon.getIsGenesUsable() ) {
            taxon.setIsGenesUsable( true );
            taxonService.update( taxon );
            log.info( "Updating taxon genes loaded to true for taxon " + taxon );
        }

    }

    /* (non-Javadoc)
     * @see ubic.gemma.loader.genome.gene.ExternalFileGeneLoaderService#validateTaxon(java.lang.String)
     */
    @Override
    public Taxon validateTaxon( String taxonName ) throws IllegalArgumentException {
        Taxon taxon = taxonService.findByCommonName( taxonName );
        if ( taxon == null ) {
            throw new IllegalArgumentException( "No taxon with common name " + taxonName + " found" );
        }
        return taxon;
    }

    /**
     * Creates a bufferedReader for gene file.
     * 
     * @param geneFile GeneFile including full path
     * @return BufferedReader The bufferedReader for gene file.
     * @throws IOException File can not be opened for reading such as does not exist.
     */
    private BufferedReader readFile( String geneFile ) throws IOException {
        File f = new File( geneFile );
        if ( !f.canRead() ) {
            throw new IOException( "Cannot read from " + geneFile );
        }
        BufferedReader b = new BufferedReader( new FileReader( geneFile ) );
        log.info( "File " + geneFile + " read successfully" );
        return b;

    }

    /**
     * Read a gene file line, splitting the line into 3 strings.
     * 
     * @param line A line from the gene file
     * @return Array of strings representing a line in a gene file.
     * @throws IOException Thrown if file is not readable
     */
    private String[] readLine( String line ) throws IOException {
        if ( StringUtils.isBlank( line ) ) {
            return null;
        }
        if ( line.startsWith( "#" ) ) {
            return null;
        }

        String[] fields = StringUtils.splitPreserveAllTokens( line, '\t' );
        if ( fields.length < 2 ) {
            throw new IOException( "Illegal format, expected at least 2 columns, got " + fields.length );
        }
        return fields;

    }

}