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
package ubic.gemma.core.loader.expression.geo.fetcher;

import ubic.gemma.persistence.util.Settings;

/**
 * Retrieve GEO GDS files from the NCBI FTP server.
 *
 * @author pavlidis
 */
public class DatasetFetcher extends GeoFetcher {

    public DatasetFetcher() {
        super();

    }

    @Override
    protected String formRemoteFilePath( String identifier ) {
        return remoteBaseDir + identifier + SOFT_GZ;
    }

    @Override
    protected void initConfig() {
        this.localBasePath = Settings.getString( "geo.local.datafile.basepath" );
        this.remoteBaseDir = Settings.getString( "geo.remote.datasetDir" );
    }

}
