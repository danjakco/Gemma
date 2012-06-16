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

import org.springframework.security.access.annotation.Secured;

/**
 * @author kelsey
 * @version $Id$
 */
public interface BlatAssociationService {

    @Secured({ "GROUP_USER" })
    public BlatAssociation create( BlatAssociation blatAssociation );

    public java.util.Collection<BlatAssociation> find( ubic.gemma.model.genome.biosequence.BioSequence bioSequence );

    public java.util.Collection<BlatAssociation> find( ubic.gemma.model.genome.Gene gene );

    public void thaw( Collection<BlatAssociation> blatAssociations );

    public void thaw( BlatAssociation blatAssociation );

    @Secured({ "GROUP_USER" })
    public void update( ubic.gemma.model.genome.sequenceAnalysis.BlatAssociation blatAssociation );

    @Secured({ "GROUP_ADMIN" })
    public void remove( BlatAssociation blatAssociation );

}
