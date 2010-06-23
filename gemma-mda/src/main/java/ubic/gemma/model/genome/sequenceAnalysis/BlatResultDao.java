/*
 * The Gemma project.
 * 
 * Copyright (c) 2006-2007 University of British Columbia
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
package ubic.gemma.model.genome.sequenceAnalysis;

import java.util.Collection;

import ubic.gemma.persistence.BaseDao;

/**
 * @see ubic.gemma.model.genome.sequenceAnalysis.BlatResult
 * @author Gemma
 * @version $Id$
 */
public interface BlatResultDao extends BaseDao<BlatResult> {

    public BlatResult thaw( BlatResult blatResult );

    public Collection<BlatResult> thaw( Collection<BlatResult> blatResults );

    /**
     * 
     */
    public BlatResult find( BlatResult blatResult );

    /**
     * Find BLAT results for the given sequence
     */
    public java.util.Collection<BlatResult> findByBioSequence(
            ubic.gemma.model.genome.biosequence.BioSequence bioSequence );

    /**
     * 
     */
    public ubic.gemma.model.genome.sequenceAnalysis.BlatResult findOrCreate(
            ubic.gemma.model.genome.sequenceAnalysis.BlatResult blatResult );

}
