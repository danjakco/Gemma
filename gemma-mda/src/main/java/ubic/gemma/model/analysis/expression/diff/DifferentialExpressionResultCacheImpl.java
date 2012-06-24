/*
 * The Gemma project
 * 
 * Copyright (c) 2012 University of British Columbia
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

package ubic.gemma.model.analysis.expression.diff;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.NonstopConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.stereotype.Component;

import ubic.gemma.util.ConfigUtils;

/**
 * Cache for data from differential expression result queries.
 * 
 * @author Paul
 * @version $Id$
 */
@Component
public class DifferentialExpressionResultCacheImpl implements DifferentialExpressionResultCache, InitializingBean {

    private static final String CACHE_NAME_BASE = "DiffExResultCache";
    private static final int CACHE_DEFAULT_MAX_ELEMENTS = 1000000;
    private static final int CACHE_DEFAULT_TIME_TO_LIVE = 10000;
    private static final int CACHE_DEFAULT_TIME_TO_IDLE = 10000;
    private static final boolean CACHE_DEFAULT_ETERNAL = true;
    private static final boolean CACHE_DEFAULT_OVERFLOW_TO_DISK = false;

    @Autowired
    private EhCacheManagerFactoryBean cacheManagerFactory;

    private Boolean enabled = true;

    private Cache cache;

    /*
     * (non-Javadoc)
     * 
     * @see
     * ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultCache#addToCache(ubic.gemma.model.analysis
     * .expression.diff.DifferentialExpressionResultDaoImpl.DiffExprGeneSearchResult)
     */
    @Override
    public void addToCache( DiffExprGeneSearchResult diffExForCache ) {
        Long r = diffExForCache.getResultSetId();
        Long g = diffExForCache.getGeneId();

        cache.put( new Element( new CacheKey( r, g ), diffExForCache ) );

    }

    @Override
    public void addToCache( Collection<DiffExprGeneSearchResult> diffExForCache ) {
        for ( DiffExprGeneSearchResult d : diffExForCache ) {
            addToCache( d );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        CacheManager cacheManager = cacheManagerFactory.getObject();
        int maxElements = ConfigUtils.getInt( "gemma.cache.diffex.maxelements", CACHE_DEFAULT_MAX_ELEMENTS );
        int timeToLive = ConfigUtils.getInt( "gemma.cache.diffex.timetolive", CACHE_DEFAULT_TIME_TO_LIVE );
        int timeToIdle = ConfigUtils.getInt( "gemma.cache.diffex.timetoidle", CACHE_DEFAULT_TIME_TO_IDLE );

        boolean eternal = ConfigUtils.getBoolean( "gemma.cache.diffex.eternal", CACHE_DEFAULT_ETERNAL )
                && timeToLive == 0;
        boolean terracottaEnabled = ConfigUtils.getBoolean( "gemma.cache.clustered", true );
        boolean overFlowToDisk = ConfigUtils.getBoolean( "gemma.cache.diffex.usedisk", CACHE_DEFAULT_OVERFLOW_TO_DISK );
        boolean diskPersistent = ConfigUtils.getBoolean( "gemma.cache.diskpersistent", false ) && !terracottaEnabled;

        if ( !cacheManager.cacheExists( CACHE_NAME_BASE ) ) {
            /*
             * See TerracottaConfiguration.
             */
            int diskExpiryThreadIntervalSeconds = 600;
            int maxElementsOnDisk = 10000;
            boolean terracottaCoherentReads = false;
            boolean clearOnFlush = false;

            if ( terracottaEnabled ) {

                CacheConfiguration config = new CacheConfiguration( CACHE_NAME_BASE, maxElements );
                config.setStatistics( false );
                config.setMemoryStoreEvictionPolicy( MemoryStoreEvictionPolicy.LRU.toString() );
                config.setOverflowToDisk( false );
                config.setEternal( eternal );
                config.setTimeToIdleSeconds( timeToIdle );
                config.setMaxElementsOnDisk( maxElementsOnDisk );
                config.addTerracotta( new TerracottaConfiguration() );
                config.getTerracottaConfiguration().setCoherentReads( terracottaCoherentReads );
                config.clearOnFlush( clearOnFlush );
                config.setTimeToLiveSeconds( timeToLive );
                config.getTerracottaConfiguration().setClustered( true );
                config.getTerracottaConfiguration().setValueMode( "SERIALIZATION" );
                config.getTerracottaConfiguration().addNonstop( new NonstopConfiguration() );
                this.cache = new Cache( config );

            } else {
                this.cache = new Cache( CACHE_NAME_BASE, maxElements, MemoryStoreEvictionPolicy.LRU, overFlowToDisk,
                        null, eternal, timeToLive, timeToIdle, diskPersistent, diskExpiryThreadIntervalSeconds, null );
            }
            cacheManager.addCache( cache );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultCache#clearCache()
     */
    @Override
    public void clearCache() {
        cache.removeAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultCache#clearCache(java.lang.Long)
     */
    @Override
    public void clearCache( Long resultSetId ) {
        for ( Object o : cache.getKeys() ) {
            CacheKey k = ( CacheKey ) o;
            if ( k.resultSetId.equals( resultSetId ) ) {
                cache.remove( k );
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultCache#get(ubic.gemma.model.analysis.expression
     * .diff.ExpressionAnalysisResultSet, ubic.gemma.model.genome.Gene)
     */
    @Override
    public DiffExprGeneSearchResult get( Long resultSet, Long g ) {
        assert cache != null;
        Element element = cache.get( new CacheKey( resultSet, g ) );
        if ( element == null ) return null;
        return ( DiffExprGeneSearchResult ) element.getValue();
    }

    @Override
    public Collection<DiffExprGeneSearchResult> get( Long resultSet, Collection<Long> genes ) {
        assert cache != null;
        Collection<DiffExprGeneSearchResult> results = new HashSet<DiffExprGeneSearchResult>();
        for ( Long g : genes ) {
            Element element = cache.get( new CacheKey( resultSet, g ) );
            if ( element != null ) {
                results.add( ( DiffExprGeneSearchResult ) element.getValue() );
            }
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultCache#isEnabled()
     */
    @Override
    public Boolean isEnabled() {
        return enabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.analysis.expression.diff.DifferentialExpressionResultCache#setEnabled(java.lang.Boolean)
     */
    @Override
    public void setEnabled( Boolean enabled ) {
        this.enabled = enabled;
    }

}

class CacheKey implements Serializable {

    private static final long serialVersionUID = 1453661277282349121L;
    Long resultSetId;
    Long geneId;

    CacheKey( Long resultSetId, Long geneId ) {
        this.resultSetId = resultSetId;
        this.geneId = geneId;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        CacheKey other = ( CacheKey ) obj;
        if ( resultSetId == null ) {
            if ( other.resultSetId != null ) return false;
        } else if ( !resultSetId.equals( other.resultSetId ) ) return false;
        if ( geneId == null ) {
            if ( other.geneId != null ) return false;
        } else if ( !geneId.equals( other.geneId ) ) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( resultSetId == null ) ? 0 : resultSetId.hashCode() );
        result = prime * result + ( ( geneId == null ) ? 0 : geneId.hashCode() );
        return result;
    }

}