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
//
// Attention: Generated code! Do not modify by hand!
// Generated by: SpringService.vsl in andromda-spring-cartridge.
//
package ubic.gemma.model.expression.bioAssayData;

/**
 * @author Paul
 * @version $Id$
 */
public interface BioAssayDimensionService {

    /**
     * 
     */
    public ubic.gemma.model.expression.bioAssayData.BioAssayDimension findOrCreate(
            ubic.gemma.model.expression.bioAssayData.BioAssayDimension bioAssayDimension );

    /**
     * 
     */
    public ubic.gemma.model.expression.bioAssayData.BioAssayDimension create(
            ubic.gemma.model.expression.bioAssayData.BioAssayDimension bioAssayDimension );

    /**
     * 
     */
    public void remove( ubic.gemma.model.expression.bioAssayData.BioAssayDimension bioAssayDimension );

    /**
     * 
     */
    public ubic.gemma.model.expression.bioAssayData.BioAssayDimension load( java.lang.Long id );

    /**
     * 
     */
    public void update( ubic.gemma.model.expression.bioAssayData.BioAssayDimension bioAssayDimension );

}
