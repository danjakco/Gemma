/*
 * The Gemma project
 * 
 * Copyright (c) 2008 University of British Columbia
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
package ubic.gemma.model.expression.bioAssayData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.util.ConfigUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * Configures the cache for data vectors.
 * <p>
 * Implementation note: This uses ehCache. I have decided to make one cache per expression experiment. The reason for
 * this is that having complex keys for cached Elements based on expression experiment AND gene makes it difficult to
 * invalidate the cache when an expression experiment's data changes. The drawback is that there are potentially
 * hundreds of caches; I don't know if there are any performance considerations there.
 * 
 * @author paul
 * @version $Id$
 */
public class ProcessedDataVectorCache {

    private static final String PROBE2PROBE_COEXPRESSION_CACHE_NAME = "Probe2ProbeCache";
    private static final int PROBE2PROBE_COEXPRESSION_CACHE_DEFAULT_MAX_ELEMENTS = 100000;
    private static final int PROBE2PROBE_COEXPRESSION_CACHE_DEFAULT_TIME_TO_LIVE = 10000;
    private static final int PROBE2PROBE_COEXPRESSION_CACHE_DEFAULT_TIME_TO_IDLE = 10000;
    private static final boolean PROBE2PROBE_COEXPRESSION_CACHE_DEFAULT_ETERNAL = true;
    private static final boolean PROBE2PROBE_COEXPRESSION_CACHE_DEFAULT_OVERFLOW_TO_DISK = true;

    /**
     * We retain references to the caches separately from the CacheManager. This _could_ create leaks of caches if the
     * cache manager needs to recreate a cache for some reason. Something to keep in mind.
     */
    private static final Map<ExpressionExperiment, Cache> caches = new HashMap<ExpressionExperiment, Cache>();

    private static String getCacheName( ExpressionExperiment e ) {
        return PROBE2PROBE_COEXPRESSION_CACHE_NAME + "_" + e.getShortName() + "_" + e.getId();
    }

    /**
     * Remove all elements from the cache for the given expression experiment, if the cache exists.
     * 
     * @param e the expression experiment - specific cache to be cleared.
     */
    public static void clearCache( ExpressionExperiment e ) {
        CacheManager manager = CacheManager.getInstance();
        Cache cache = manager.getCache( getCacheName( e ) );
        if ( cache != null ) cache.removeAll();
    }

    /**
     * 
     */
    public static void clearAllCaches() {
        for ( ExpressionExperiment e : caches.keySet() ) {
            clearCache( e );
        }
    }

    /**
     * @return
     */
    public static Collection<Cache> getAllCaches() {
        return caches.values();
    }

    /**
     * Get the coexpression cache for a particular experiment
     * 
     * @param e
     * @return
     */
    public static Cache getCache( ExpressionExperiment e ) {
        if ( !caches.containsKey( e ) ) {
            initializeCache( e );
        }
        return caches.get( e );
    }

    /**
     * Initialize the vector cache; if it already exists it will not be recreated.
     * 
     * @return
     */
    private static void initializeCache( ExpressionExperiment e ) {

        if ( caches.containsKey( e ) ) {
            return;
        }

        int maxElements = ConfigUtils.getInt( "gemma.cache.probe2probe.maxelements",
                PROBE2PROBE_COEXPRESSION_CACHE_DEFAULT_MAX_ELEMENTS );
        int timeToLive = ConfigUtils.getInt( "gemma.cache.probe2probe.timetolive",
                PROBE2PROBE_COEXPRESSION_CACHE_DEFAULT_TIME_TO_LIVE );
        int timeToIdle = ConfigUtils.getInt( "gemma.cache.probe2probe.timetoidle",
                PROBE2PROBE_COEXPRESSION_CACHE_DEFAULT_TIME_TO_IDLE );

        boolean overFlowToDisk = ConfigUtils.getBoolean( "gemma.cache.probe2probe.usedisk",
                PROBE2PROBE_COEXPRESSION_CACHE_DEFAULT_OVERFLOW_TO_DISK );

        boolean eternal = ConfigUtils.getBoolean( "gemma.cache.probe2probe.eternal",
                PROBE2PROBE_COEXPRESSION_CACHE_DEFAULT_ETERNAL );

        String cacheName = getCacheName( e );

        /*
         * Create a cache for the probe data.s
         */
        CacheManager manager = CacheManager.getInstance();

        if ( !manager.cacheExists( cacheName ) ) {

            manager.addCache( new Cache( cacheName, maxElements, overFlowToDisk, eternal, timeToLive, timeToIdle ) );
        }

        caches.put( e, manager.getCache( cacheName ) );

    }

}
