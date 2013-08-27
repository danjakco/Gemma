/*
 * The Gemma project
 * 
 * Copyright (c) 2013 University of British Columbia
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
package ubic.gemma.loader.association.phenotype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import ubic.basecode.ontology.model.OntologyTerm;
import ubic.basecode.ontology.ncbo.AnnotatorResponse;
import ubic.gemma.model.genome.gene.phenotype.valueObject.CharacteristicValueObject;
import ubic.gemma.model.genome.gene.phenotype.valueObject.EvidenceSourceValueObject;
import ubic.gemma.model.genome.gene.phenotype.valueObject.GenericEvidenceValueObject;

public class OmimDatabaseImporter extends ExternalDatabaseEvidenceImporterAbstractCLI {

    // name of the external database
    protected static final String OMIM = "OMIM";

    // ***********************************************************************************
    // reading OMIM static files
    public static final String OMIM_FILES_PATH = RESOURCE_PATH + OMIM + File.separator;
    // manual static file mapping
    public static final String MANUAL_MAPPING_OMIM = OMIM_FILES_PATH + "ManualDescriptionMapping.tsv";
    // when we find this description ingore it
    public static final String DESCRIPTION_TO_IGNORE = OMIM_FILES_PATH + "DescriptionToIgnore.tsv";

    // ********************************************************************************
    // the OMIM files to download
    public static final String OMIM_URL_PATH = "ftp://grcf.jhmi.edu/OMIM/";
    public static final String OMIM_FILE_MORBID = "morbidmap";
    public static final String OMIM_FILE_MIM = "mim2gene.txt";
    // ********************************************************************************

    // data structure that we will be using with OMIM data
    private HashMap<String, ArrayList<GenericEvidenceValueObject>> omimIDGeneToEvidence = new HashMap<String, ArrayList<GenericEvidenceValueObject>>();

    public static void main( String[] args ) throws Exception {

        OmimDatabaseImporter importEvidence = new OmimDatabaseImporter( args );

        // creates the folder where to place the file web downloaded files and final output files
        importEvidence.createWriteFolderWithDate( OMIM );

        // download the OMIM File called morbid
        String morbidmap = importEvidence.downloadFileFromWeb( OMIM_URL_PATH, OMIM_FILE_MORBID );
        // download the OMIM File called mim2gene
        String mim2gene = importEvidence.downloadFileFromWeb( OMIM_URL_PATH, OMIM_FILE_MIM );

        // find the OMIM and Mesh terms
        importEvidence.findOmimAndMeshMappingUsingOntologyFile();

        importEvidence.processOmimFiles( morbidmap, mim2gene );
    }

    public OmimDatabaseImporter( String[] args ) throws Exception {
        super( args );
    }

    // process all OMIm files to get the data out and manipulates it
    private void processOmimFiles( String morbidmap, String mim2gene ) throws IOException {

        // special resultsToIgnore, results returned that are too general
        parseResultsToIgnore();
        // omim description we want to ignore, when we find those description, we know we are not interested in them
        HashSet<String> descriptionToIgnore = parseDescriptionToIgnore();

        // mapping from OMIM id to valueURI, those are the ones we know are good, because they were checked manually
        // (OMIM_ID --->
        HashMap<String, Collection<String>> omimIdToPhenotype = parseFileOmimDescriptionToPhenotype();

        // mapping find using mim2gene file, Omim id ---> Gene NCBI
        HashMap<String, String> omimIdToGeneNCBI = parseFileOmimIdToGeneNCBI( mim2gene );

        String line = null;

        BufferedReader br = new BufferedReader( new FileReader( morbidmap ) );

        int lineNumber = 1;

        // parse the morbid OMIM file
        while ( ( line = br.readLine() ) != null ) {

            String[] tokens = line.split( "\\|" );

            int pos = tokens[0].lastIndexOf( "," );

            // if there is a database link
            if ( pos != -1 ) {

                // phenotypes found
                Collection<String> phenotypesUri = new HashSet<String>();
                // this represents the case used to find the result
                String conditionUsed = "";

                // OMIM description find in file, the annotator use description
                String description = tokens[0].substring( 0, pos ).trim();

                // evidence code we will use
                String evidenceCode = "TAS";
                // the OMIM id, (also is the database link)
                String omimId = tokens[0].substring( pos + 1, tokens[0].length() ).trim().split( " " )[0];
                // OMOM gene id
                String omimGeneId = tokens[2];

                // search term that will be used by the annotator we run it 2 times
                // this search term is not mofified
                String searchTerm = description;
                // the searchTermWithOutKeywords is a modified version of the searchTerm
                String searchTermWithOutKeywords = removeSpecificKeywords( searchTerm );
                // omimGeneid ---> ncbi id
                String ncbiGeneId = omimIdToGeneNCBI.get( omimGeneId );

                // is the omimGeneId found in the other file
                if ( ncbiGeneId != null ) {

                    String geneSymbol = this.geneService.findByNCBIId( new Integer( ncbiGeneId ) ).getOfficialSymbol();

                    // if there is no omim id given we cannot do anything with this line (happens often)
                    if ( !isInteger( omimId ) || Integer.parseInt( omimId ) < 100 ) {
                        continue;
                    }

                    log.info( "teating line: " + lineNumber++ );

                    // Case 0: we want to exclude this omim descriptipn
                    if ( descriptionToIgnore.contains( description ) ) {
                        conditionUsed = "Case 0: Ignored Descripton";
                    }

                    // we go in order of importance to treat the line

                    // Case 1: lets use the Omim id to find the mapping phenotype, if it exists and not obsolote
                    else if ( omimIdToPhenotypeMapping.get( omimId ) != null ) {
                        phenotypesUri = omimIdToPhenotypeMapping.get( omimId );
                        conditionUsed = "Case 1: Found with OMIM ID";
                    }

                    // Case 2: use the static manual annotation file
                    else if ( omimIdToPhenotype.get( omimId ) != null ) {
                        phenotypesUri = omimIdToPhenotype.get( omimId );
                        conditionUsed = "Case 2: Found with Description, Manual Mapping";
                    }

                    // if Case 1 and 2 fail, then lets use the annotator to try finding an answer
                    else {

                        // search with the annotator and filter result to take out obsolete terms given
                        Collection<AnnotatorResponse> ontologyTermsNormal = removeNotExistAndObsolete( anoClient
                                .findTerm( searchTerm ) );

                        // same thing, but with the searchTermWithOutKeywords
                        Collection<AnnotatorResponse> ontologyTermsWithOutKeywords = new HashSet<AnnotatorResponse>();

                        // the results are ordered by importance, check first result to see if a correct mapping was
                        // found, we are only interested in exacts matches and synonim at this point
                        AnnotatorResponse annotatorResponseFirstNormal = null;

                        if ( !ontologyTermsNormal.isEmpty() ) {
                            annotatorResponseFirstNormal = ontologyTermsNormal.iterator().next();
                        }

                        if ( annotatorResponseFirstNormal != null
                                && this.ontologyService.getTerm( annotatorResponseFirstNormal.getValueUri() ) != null
                                && !this.ontologyService.getTerm( annotatorResponseFirstNormal.getValueUri() )
                                        .isTermObsolete() ) {

                            if ( annotatorResponseFirstNormal.getOntologyUsed().equalsIgnoreCase( "DOID" ) ) {

                                if ( annotatorResponseFirstNormal.isExactMatch() ) {
                                    phenotypesUri.add( annotatorResponseFirstNormal.getValueUri() );
                                    conditionUsed = "Case 4a: Found Exact With Disease Annotator";

                                } else if ( annotatorResponseFirstNormal.isSynonym() ) {
                                    phenotypesUri.add( annotatorResponseFirstNormal.getValueUri() );
                                    conditionUsed = "Case 5a: Found Synonym With Disease Annotator Synonym";
                                }
                            } else if ( annotatorResponseFirstNormal.getOntologyUsed().equalsIgnoreCase( "HP" ) ) {

                                if ( annotatorResponseFirstNormal.isExactMatch() ) {
                                    phenotypesUri.add( annotatorResponseFirstNormal.getValueUri() );
                                    conditionUsed = "Case 6a: Found Exact With HP Annotator";

                                } else if ( annotatorResponseFirstNormal.isSynonym() ) {
                                    phenotypesUri.add( annotatorResponseFirstNormal.getValueUri() );
                                    conditionUsed = "Case 7a: Found Synonym With HP Annotator Synonym";
                                }
                            }
                        }

                        if ( conditionUsed.isEmpty() ) {

                            ontologyTermsWithOutKeywords = removeNotExistAndObsolete( anoClient
                                    .findTerm( searchTermWithOutKeywords ) );

                            AnnotatorResponse annotatorResponsFirst = null;

                            if ( !ontologyTermsWithOutKeywords.isEmpty() ) {
                                annotatorResponsFirst = ontologyTermsWithOutKeywords.iterator().next();
                            }

                            if ( annotatorResponsFirst != null
                                    && this.ontologyService.getTerm( annotatorResponsFirst.getValueUri() ) != null
                                    && !this.ontologyService.getTerm( annotatorResponsFirst.getValueUri() )
                                            .isTermObsolete() ) {

                                if ( annotatorResponsFirst.getOntologyUsed().equalsIgnoreCase( "DOID" ) ) {

                                    if ( annotatorResponsFirst.isExactMatch() ) {
                                        phenotypesUri.add( annotatorResponsFirst.getValueUri() );
                                        conditionUsed = "Case 4b: Found Exact With Disease Annotator (keywords taken out)";

                                    } else if ( annotatorResponsFirst.isSynonym() ) {
                                        phenotypesUri.add( annotatorResponsFirst.getValueUri() );
                                        conditionUsed = "Case 5b: Found Synonym With Disease Annotator Synonym (keywords taken out)";
                                    }
                                } else if ( annotatorResponsFirst.getOntologyUsed().equalsIgnoreCase( "HP" ) ) {

                                    if ( annotatorResponsFirst.isExactMatch() ) {
                                        phenotypesUri.add( annotatorResponsFirst.getValueUri() );
                                        conditionUsed = "Case 6b: Found Exact With HP Annotator (without keyword)";

                                    } else if ( annotatorResponsFirst.isSynonym() ) {
                                        phenotypesUri.add( annotatorResponsFirst.getValueUri() );
                                        conditionUsed = "Case 7b: Found Synonym With HP Annotator Synonym (keywords taken out)";
                                    }
                                }
                            }
                        }

                        // no exact match or synonym were found in DOID or HP, check if other mapping were found

                        if ( conditionUsed.isEmpty() ) {

                            if ( ontologyTermsNormal.size() > 0 || ontologyTermsWithOutKeywords.size() > 0 ) {

                                // keep all that was found by the annotator
                                HashSet<String> mappingFoundNotExactValueUri = new HashSet<String>();
                                HashSet<String> mappingFoundNotExactValue = new HashSet<String>();

                                for ( AnnotatorResponse annotatorResponse : ontologyTermsNormal ) {
                                    mappingFoundNotExactValue.add( annotatorResponse.getValue() );
                                    mappingFoundNotExactValueUri.add( annotatorResponse.getValueUri() );
                                }
                                for ( AnnotatorResponse annotatorResponse : ontologyTermsWithOutKeywords ) {
                                    mappingFoundNotExactValue.add( annotatorResponse.getValue() );
                                    mappingFoundNotExactValueUri.add( annotatorResponse.getValueUri() );
                                }

                                HashSet<String> mappingFoundNotExactValue2 = new HashSet<String>();
                                String resultsIgnored = "";

                                // take out the excluded results
                                for ( String phenoValueUri : mappingFoundNotExactValueUri ) {

                                    if ( resultsToIgnore.contains( phenoValueUri ) ) {

                                        resultsIgnored = resultsIgnored + phenoValueUri + "; ";
                                    } else {
                                        mappingFoundNotExactValue2.add( phenoValueUri );
                                    }
                                }

                                if ( mappingFoundNotExactValue2.isEmpty() ) {
                                    conditionUsed = "Case 10: Exclude Results\t";
                                } else {

                                    conditionUsed = "Case 8: Found Mappings, No Match Detected";

                                    for ( String mappingF : mappingFoundNotExactValueUri ) {
                                        phenotypesUri.add( mappingF );
                                    }
                                }

                            } else {
                                conditionUsed = "Case 9: No Mapping Found";
                            }
                        }

                    }

                    // ingored
                    if ( conditionUsed.indexOf( "Case 0:" ) != -1 ) {
                        outNotFoundBuffer.add( description + "\t" + conditionUsed + "\n" );
                    }

                    // Case 1 and Case are the only cases that we want to import
                    else if ( conditionUsed.indexOf( "Case 1:" ) != -1 || conditionUsed.indexOf( "Case 2:" ) != -1 ) {

                        SortedSet<CharacteristicValueObject> phenotypes = new TreeSet<CharacteristicValueObject>();

                        for ( String valueUri : phenotypesUri ) {

                            OntologyTerm ontologyTerm = this.diseaseOntologyService.getTerm( valueUri );
                            if ( ontologyTerm == null ) {
                                ontologyTerm = this.humanPhenotypeOntologyService.getTerm( valueUri );
                            }

                            // ontologyTerm can never be null or obsolete we checked before
                            CharacteristicValueObject c = new CharacteristicValueObject( ontologyTerm.getLabel(),
                                    valueUri );
                            phenotypes.add( c );

                        }

                        if ( !phenotypes.isEmpty() ) {

                            EvidenceSourceValueObject evidenceSource = new EvidenceSourceValueObject( omimId, null );
                            evidenceSource.setExternalUrl( conditionUsed );

                            GenericEvidenceValueObject e = new GenericEvidenceValueObject( new Integer( ncbiGeneId ),
                                    phenotypes, description, evidenceCode, false, evidenceSource );

                            e.setGeneOfficialSymbol( geneSymbol );

                            String key = omimId + ncbiGeneId;

                            ArrayList<GenericEvidenceValueObject> evidences = new ArrayList<GenericEvidenceValueObject>();

                            if ( omimIDGeneToEvidence.get( key ) == null ) {
                                evidences.add( e );
                            } else {
                                evidences = omimIDGeneToEvidence.get( key );
                                evidences.add( e );
                            }

                            omimIDGeneToEvidence.put( key, evidences );
                        }
                    }

                    else if ( conditionUsed.indexOf( "Case 4" ) != -1 || conditionUsed.indexOf( "Case 5" ) != -1
                            || conditionUsed.indexOf( "Case 6" ) != -1 || conditionUsed.indexOf( "Case 7" ) != -1
                            || conditionUsed.indexOf( "Case 8:" ) != -1 || conditionUsed.indexOf( "Case 3:" ) != -1 ) {

                        String phenotypeValueUri = "";
                        String phenotypeValue = "";

                        for ( String valueUri : phenotypesUri ) {
                            OntologyTerm o = this.diseaseOntologyService.getTerm( valueUri );
                            if ( o == null ) {
                                o = this.humanPhenotypeOntologyService.getTerm( valueUri );
                            }

                            phenotypeValueUri = phenotypeValueUri + o.getUri() + ";";
                            phenotypeValue = phenotypeValue + o.getLabel() + ";";

                        }

                        if ( !phenotypeValueUri.isEmpty() ) {
                            String lineToWrite = omimId + "\t" + phenotypeValueUri + "\t" + phenotypeValue + "\t"
                                    + description + "\t" + conditionUsed + "\n";

                            outMappingFoundBuffer.add( lineToWrite );
                        }

                    }

                    else if ( conditionUsed.indexOf( "Case 10:" ) != -1 ) {

                        String lineToWrite = description + "\t" + conditionUsed + "\n";

                        outNotFoundBuffer.add( lineToWrite );
                    } else {
                        String lineToWrite = description + "\t" + conditionUsed + "\n";

                        outNotFoundBuffer.add( lineToWrite );
                    }
                }
            }
        }

        if ( !errorMessages.isEmpty() ) {

            log.info( "here is the error messages :\n" );

            for ( String err : errorMessages ) {

                log.error( err );
            }
        }

        // write final output found
        for ( String lineMappinpFound : outMappingFoundBuffer ) {
            outMappingFound.write( lineMappinpFound );
        }
        for ( String lineNotFound : outNotFoundBuffer ) {
            outNotFound.write( lineNotFound );
        }

        // headers of the final file
        outFinalResults
                .write( "GeneSymbol\tGeneId\tEvidenceCode\tComments\tDatabaseLink\tPhenotypes\tExtraInfo\tExtraInfo\tisNegative\tExternalDatabase\n" );

        // case 0 and 1 at the end, combine same
        for ( ArrayList<GenericEvidenceValueObject> evidences : omimIDGeneToEvidence.values() ) {

            String geneSymbol = "";
            String ncbiGeneId = "";
            String evidenceCode = "";
            String description = "";
            HashSet<String> descriptions = new HashSet<String>();
            String databaseLink = "";
            String allValueUri = "";
            String phenotypeValue = "";
            String conditionUsed = "";
            HashSet<CharacteristicValueObject> phenoSet = new HashSet<CharacteristicValueObject>();

            for ( GenericEvidenceValueObject g : evidences ) {

                geneSymbol = g.getGeneOfficialSymbol();
                ncbiGeneId = g.getGeneNCBI().toString();
                evidenceCode = g.getEvidenceCode();
                descriptions.add( g.getDescription() );
                databaseLink = g.getEvidenceSource().getAccession();
                // just used this field to hide some information...
                conditionUsed = g.getEvidenceSource().getExternalUrl();

                for ( CharacteristicValueObject c : g.getPhenotypes() ) {

                    phenoSet.add( c );
                }
            }

            for ( CharacteristicValueObject phe : phenoSet ) {
                allValueUri = allValueUri + phe.getValueUri() + ";";
                phenotypeValue = phenotypeValue + phe.getValue() + ";";
            }
            for ( String desc : descriptions ) {
                description = description + desc + "; ";
            }

            if ( !description.isEmpty() ) {
                description = description.substring( 0, description.length() - 2 );
            }

            String evidenceLine = geneSymbol + "\t" + ncbiGeneId + "\t" + evidenceCode + "\t" + description + "\t"
                    + databaseLink + "\t" + allValueUri + "\t" + phenotypeValue + "\t" + conditionUsed + "\t" + ""
                    + "\t" + OMIM + "\n";

            outFinalResults.write( evidenceLine );
        }

        writeBuffersAndCloseFiles();

        br.close();
    }

    private HashSet<String> parseDescriptionToIgnore() throws IOException {

        HashSet<String> descriptionToIgnore = new HashSet<String>();

        BufferedReader br = new BufferedReader( new InputStreamReader(
                OmimDatabaseImporter.class.getResourceAsStream( DESCRIPTION_TO_IGNORE ) ) );

        String line = "";

        while ( ( line = br.readLine() ) != null ) {

            line = removeSmallNumberAndTxt( line );

            descriptionToIgnore.add( removeCha( line ).trim() );
        }

        return descriptionToIgnore;
    }

    private HashMap<String, Collection<String>> parseFileOmimDescriptionToPhenotype() throws IOException {

        HashMap<String, Collection<String>> omimIdToPhenotype = new HashMap<String, Collection<String>>();

        BufferedReader br = new BufferedReader( new InputStreamReader(
                OmimDatabaseImporter.class.getResourceAsStream( MANUAL_MAPPING_OMIM ) ) );

        String line = br.readLine();

        String[] tokens = null;

        while ( ( line = br.readLine() ) != null ) {

            Collection<String> col = new HashSet<String>();

            tokens = line.split( "\t" );

            String omimIdStaticFile = removeCha( tokens[0] );
            String valueUriStaticFile = removeCha( tokens[1] ).replaceAll( ";", "" );

            if ( !isObsoleteOrNotExist( valueUriStaticFile ) ) {

                if ( omimIdToPhenotype.get( omimIdStaticFile ) == null ) {

                    col.add( valueUriStaticFile );
                } else {
                    col = omimIdToPhenotype.get( omimIdStaticFile );
                    col.add( valueUriStaticFile );
                }
                omimIdToPhenotype.put( omimIdStaticFile, col );
            } else {
                errorMessages.add( "MANUAL MAPPING FILE TERM OBSOLETE OR NOT EXISTANT: " + line );
            }
        }

        return omimIdToPhenotype;
    }

    private HashMap<String, String> parseFileOmimIdToGeneNCBI( String mim2gene ) throws IOException {

        String line = null;
        HashMap<String, String> omimIdToGeneNCBI = new HashMap<String, String>();

        BufferedReader br = new BufferedReader( new FileReader( mim2gene ) );

        while ( ( line = br.readLine() ) != null ) {

            String[] tokens = line.split( "\t" );

            if ( !tokens[2].trim().equals( "-" ) && !tokens[3].trim().equals( "-" ) && !tokens[2].trim().equals( "" )
                    && !tokens[3].trim().equals( "" ) ) {
                omimIdToGeneNCBI.put( tokens[0], tokens[2] );
            }
        }
        br.close();
        return omimIdToGeneNCBI;

    }

}