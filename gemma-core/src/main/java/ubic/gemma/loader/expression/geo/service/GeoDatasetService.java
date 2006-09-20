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
package ubic.gemma.loader.expression.geo.service;

import java.util.Collection;

import ubic.gemma.loader.expression.geo.GeoDomainObjectGenerator;
import ubic.gemma.loader.expression.geo.model.GeoSeries;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * Non-interactive fetching, processing and persisting of GEO data.
 * 
 * @author pavlidis
 * @version $Id$
 * @spring.bean id="geoDatasetService"
 */
public class GeoDatasetService extends AbstractGeoService {

    /**
     * Given a GEO GSE or GDS:
     * <ol>
     * <li>Download and parse GDS files and GSE file needed</li>
     * <li>Convert the GDS and GSE into a ExpressionExperiment (or just the ArrayDesigns)
     * <li>Load the resulting ExpressionExperiment and/or ArrayDesigns into Gemma</li>
     * </ol>
     * 
     * @param geoDataSetAccession
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object fetchAndLoad( String geoAccession ) {
        this.geoConverter.clear();
        this.geoDomainObjectGenerator = new GeoDomainObjectGenerator();
        geoDomainObjectGenerator.setProcessPlatformsOnly( this.loadPlatformOnly );

        if ( this.loadPlatformOnly ) {
            Collection<?> platforms = geoDomainObjectGenerator.generate( geoAccession );
            Collection<Object> arrayDesigns = geoConverter.convert( platforms );
            return persisterHelper.persist( arrayDesigns );
        }

        Collection<?> results = geoDomainObjectGenerator.generate( geoAccession );

        if ( results == null || results.size() == 0 ) {
            throw new RuntimeException( "Could not get domain objects for " + geoAccession );
        }

        assert results.iterator().next() instanceof GeoSeries : "Got a "
                + results.iterator().next().getClass().getName() + " instead of a " + GeoSeries.class.getName();

        GeoSeries series = ( GeoSeries ) results.iterator().next();

        log.info( "Generated GEO domain objects for " + geoAccession );

        ExpressionExperiment result = ( ExpressionExperiment ) geoConverter.convert( series );

        for ( BioAssay bioAssay : result.getBioAssays() ) {
            ArrayDesign arrayDesign = bioAssay.getArrayDesignUsed();
            checkArrayDesign( arrayDesign );
        }

        log.info( "Converted " + series.getGeoAccession() );
        assert persisterHelper != null;
        Object persistedResult = persisterHelper.persist( result );
        this.geoConverter.clear();
        return persistedResult;
    }

    /**
     * @param arrayDesigns
     */
    private void checkArrayDesign( ArrayDesign arrayDesign ) {
        ArrayDesign existing = arrayDesignService.find( arrayDesign );
        if ( existing != null ) {
            log.info( arrayDesign + " already exists in the system" );
        }
    }

}
