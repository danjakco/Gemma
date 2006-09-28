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
package ubic.gemma.loader.genome.gene.ncbi;

import ubic.gemma.util.ConfigUtils;
import ubic.gemma.util.NetDatasourceUtil;

/**
 * @author pavlidis
 * @version $Id$
 */
public class NCBIUtil extends NetDatasourceUtil {

    public void init() {
        this.setHost( ConfigUtils.getString( "ncbi.host" ) );
    }

}
