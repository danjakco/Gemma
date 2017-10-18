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
package ubic.gemma.core.loader.expression.geo.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a sample (GSM) in GEO. The channels correspond to BioMaterials; the sample itself corresponds to a
 * BioAssay in Gemma. Some fields are only relevant for SAGE.
 *
 * @author pavlidis
 */
@SuppressWarnings("unused")
public class GeoSample extends GeoData implements Comparable<GeoData> {

    private static final long serialVersionUID = -8820012224856178673L;
    private static final Log log = LogFactory.getLog( GeoSample.class.getName() );
    private final Collection<GeoReplication> replicates;
    private final Collection<GeoVariable> variables;
    // SAGE item
    private String anchor;
    private List<GeoChannel> channels;
    private String dataProcessing = "";
    private String description = "";
    private String hybProtocol = "";
    private boolean isGenePix = false;
    private String lastUpdateDate = "";
    private boolean mightNotHaveDataInFile = false;
    private Collection<GeoPlatform> platforms = null;
    private String scanProtocol = "";
    private Collection<String> seriesAppearsIn = new HashSet<>();
    private String supplementaryFile = "";
    private int tagCount;
    private int tagLength;
    /**
     * This is used to store the title for the sample as found in the GDS file, if it differs from the one in the GSE
     * file
     */
    private String titleInDataset = null;
    private String type = "DNA";
    private boolean warnedAboutGenePix = false;

    public GeoSample() {
        channels = new ArrayList<>();
        this.addChannel();
        contact = new GeoContact();
        platforms = new HashSet<>();
        replicates = new HashSet<>();
        variables = new HashSet<>();
    }

    public void addChannel() {
        GeoChannel newCh = new GeoChannel();
        newCh.setChannelNumber( channels.size() + 1 );
        this.channels.add( newCh );
    }

    public void addPlatform( GeoPlatform platform ) {
        if ( log.isDebugEnabled() )
            log.debug( this + " is on " + platform );

        if ( this.platforms.size() > 0 && !this.platforms.contains( platform ) ) {
            log.warn( "Multi-platform sample: " + this );
        }

        // special case that indicates might be MPSS.
        if ( "virtual".equals( platform.getDistribution() ) ) {
            this.setMightNotHaveDataInFile( true );
        }

        // Another special case - exon arrays, even gene-level ones, might not have the data.
        if ( GeoPlatform.isAffymetrixExonArray( platform.getGeoAccession() ) ) {
            this.setMightNotHaveDataInFile( true );
        }

        this.platforms.add( platform );
    }

    public void addReplication( GeoReplication replication ) {
        this.replicates.add( replication );
    }

    public void addSeriesAppearsIn( String value ) {
        this.getSeriesAppearsIn().add( value );
        if ( this.getSeriesAppearsIn().size() > 1 ) {
            if ( log.isDebugEnabled() )
                log.debug( this.getGeoAccession() + " appears in more than one series" );
        }
    }

    public void addToDataProcessing( String s ) {
        this.dataProcessing = this.dataProcessing + " " + s;
    }

    public void addToDescription( String s ) {
        this.description = this.description + " " + s;
        this.isGenePix = description.contains( "GenePix" );

        if ( isGenePix && !this.warnedAboutGenePix ) {
            log.warn( "GenePix data detected in " + this + ": Some unused quantitation types may be skipped" );
            warnedAboutGenePix = true;
        }

    }

    public void addToHybProtocol( String s ) {
        this.hybProtocol = this.hybProtocol + " " + s;
    }

    public void addToScanProtocol( String s ) {
        this.scanProtocol = this.scanProtocol + " " + s;
    }

    public void addVariable( GeoVariable variable ) {
        this.variables.add( variable );
    }

    /**
     * @return true if this sample appears in more than one GEO Series.
     */
    public boolean appearsInMultipleSeries() {
        return seriesAppearsIn.size() > 1;
    }

    @Override
    public int compareTo( GeoData o ) {
        return o.getGeoAccession().compareTo( this.getGeoAccession() );
    }

    /**
     * @return Returns the anchor. (SAGE)
     */
    public String getAnchor() {
        return this.anchor;
    }

    /**
     * @param anchor The anchor to set. (SAGE)
     */
    public void setAnchor( String anchor ) {
        this.anchor = anchor;
    }

    public GeoChannel getChannel( int i ) {
        if ( i <= 0 || i > channels.size() )
            throw new IllegalArgumentException(
                    "Invalid channel index " + i + ", only " + channels.size() + " channels available." );
        GeoChannel result = channels.get( i - 1 );

        if ( result.getChannelNumber() != i ) {
            throw new IllegalStateException(
                    "Channel number recorded in object was incorrect." + result.getChannelNumber() + " != " + i );
        }
        return result;
    }

    /**
     * @return Returns the channelCount.
     */
    public int getChannelCount() {
        return this.channels.size();
    }

    /**
     * @return Returns the channels.
     */
    public List<GeoChannel> getChannels() {
        return this.channels;
    }

    public void setChannels( List<GeoChannel> channelData ) {
        this.channels = channelData;
    }

    /**
     * @return Returns the dataProcessing.
     */
    public String getDataProcessing() {
        return this.dataProcessing;
    }

    /**
     * @param dataProcessing The dataProcessing to set.
     */
    public void setDataProcessing( String dataProcessing ) {
        this.dataProcessing = dataProcessing;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription( String description ) {
        this.description = description;
        this.isGenePix = description.contains( "GenePix" );
        if ( isGenePix && !this.warnedAboutGenePix ) {
            log.warn(
                    "GenePix data detected: Some unused quantitation types may be skipped (further warnings skipped)" );
            warnedAboutGenePix = true;
        }
    }

    /**
     * @return Returns the hybProtocol.
     */
    public String getHybProtocol() {
        return this.hybProtocol;
    }

    /**
     * @param hybProtocol The hybProtocol to set.
     */
    public void setHybProtocol( String hybProtocol ) {
        this.hybProtocol = hybProtocol;
    }

    /**
     * @return String
     */
    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate( String lastUpdateDate ) {
        this.lastUpdateDate = lastUpdateDate;
    }

    /**
     * Given a column number (count starts from zero) get the name of the corresponding quantitation type for this
     * sample.
     *
     * @param n column number
     * @return column name.
     */
    public String getNthQuantitationType( int n ) {
        if ( n < 0 || n > getColumnNames().size() - 1 ) {
            return null; // This can happen if not every sample has the same quantitation types (happens in rare
            // cases)
        }
        return getColumnNames().get( n );
    }

    /**
     * @return organism name. This is obtained from the 'channels'.
     * @throws IllegalArgumentException if there are two different organisms. This is kind of temporary, it's not nice.
     */
    public String getOrganism() {
        String org = null;
        for ( GeoChannel c : getChannels() ) {
            String o = c.getOrganism();
            if ( org != null && o != null && !org.equals( o ) ) {
                throw new IllegalArgumentException(
                        "Sample has two different organisms; One channel taxon is " + org + " other is " + o
                                + " Check that is expected for sample " + this.getGeoAccession() );

            }
            org = o;
        }
        return org;

    }

    public Collection<GeoPlatform> getPlatforms() {
        return this.platforms;
    }

    /**
     * @return Returns the replicates.
     */
    public Collection<GeoReplication> getReplicates() {
        return this.replicates;
    }

    /**
     * @return Returns the scanProtocol.
     */
    public String getScanProtocol() {
        return this.scanProtocol;
    }

    /**
     * @param scanProtocol The scanProtocol to set.
     */
    public void setScanProtocol( String scanProtocol ) {
        this.scanProtocol = scanProtocol;
    }

    public Collection<String> getSeriesAppearsIn() {
        return seriesAppearsIn;
    }

    public void setSeriesAppearsIn( Collection<String> otherSeriesAppearsIn ) {
        this.seriesAppearsIn = otherSeriesAppearsIn;
    }

    /**
     * @return String
     */
    public String getSupplementaryFile() {
        return supplementaryFile;
    }

    public void setSupplementaryFile( String supplementaryFile ) {
        this.supplementaryFile = supplementaryFile;
    }

    /**
     * @return Returns the tagCount. (SAGE)
     */
    public int getTagCount() {
        return this.tagCount;
    }

    /**
     * @param tagCount The tagCount to set. (SAGE)
     */
    public void setTagCount( int tagCount ) {
        this.tagCount = tagCount;
    }

    /**
     * @return Returns the tagLength. (SAGE)
     */
    public int getTagLength() {
        return this.tagLength;
    }

    /**
     * @param tagLength The tagLength to set. (SAGE)
     */
    public void setTagLength( int tagLength ) {
        this.tagLength = tagLength;
    }

    public String getTitleInDataset() {
        return titleInDataset;
    }

    public void setTitleInDataset( String titleInDataset ) {
        this.titleInDataset = titleInDataset;
    }

    /**
     * Returns the sample type (ie. DNA, RNA, etc.)
     *
     * @return String
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets the sample type (ie. DNA, RNA, etc.)
     *
     * @param type new type
     */
    public void setType( String type ) {
        this.type = type;
    }

    /**
     * @return Returns the variables.
     */
    public Collection<GeoVariable> getVariables() {
        return this.variables;
    }

    /**
     * @return true if the data uses a platform that, generally, we can use the data from. Will be false for MPSS, SAGE
     * and Exon array data.
     */
    public boolean hasUsableData() {
        if ( platforms == null || platforms.isEmpty() ) {
            throw new IllegalStateException( "Don't call until platforms has been set" );
        }
        for ( GeoPlatform p : platforms ) {
            if ( !p.useDataFromGeo() ) {
                return false;
            }
        }
        return true;
    }

    public boolean isGenePix() {
        return isGenePix;
    }

    /**
     * @return true if the data might be separate, as for some RNA-seq studies.
     */
    public boolean isMightNotHaveDataInFile() {
        return mightNotHaveDataInFile;
    }

    public void setMightNotHaveDataInFile( boolean mightNotHaveDataInFile ) {
        this.mightNotHaveDataInFile = mightNotHaveDataInFile;
    }

    @Override
    public String toString() {
        return super.toString() + ( this.getPlatforms().size() > 0 ?
                " on " + ( this.getPlatforms().size() == 1 ?
                        this.getPlatforms().iterator().next() :
                        ( this.getPlatforms().size() + " platforms" ) ) :
                "" );
    }

    public enum LibraryStrategy {
        OTHER, RNASEQ
    }

}
