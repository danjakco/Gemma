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
package ubic.gemma.model.expression.experiment;

import java.util.Collection;

import org.springframework.security.access.annotation.Secured;

/**
 * @author paul
 */
public interface ExperimentalFactorService {

    String BATCH_FACTOR_NAME_PREFIX = "Batch_";

    String BATCH_FACTOR_CATEGORY_URI = "http://www.ebi.ac.uk/efo/EFO_0005067"; // block aka batch

    String BATCH_FACTOR_CATEGORY_NAME = "block";

    String BATCH_FACTOR_NAME = "batch";
    String FACTOR_VALUE_RNAME_PREFIX = "fv_";

    /**
     * Delete the factor, its associated factor values and all differential expression analyses in which it is used.
     */
    @Secured({ "GROUP_USER", "ACL_SECURABLE_EDIT" })
    void delete( ExperimentalFactor experimentalFactor );

    @Secured({ "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_READ" })
    ExperimentalFactor find( ExperimentalFactor experimentalFactor );

    @Secured({ "GROUP_USER", "AFTER_ACL_READ" })
    ExperimentalFactor findOrCreate( ExperimentalFactor experimentalFactor );

    @Secured({ "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_READ" })
    ExperimentalFactor load( java.lang.Long id );

    @Secured({ "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    Collection<ExperimentalFactor> load( Collection<Long> ids );

    @Secured({ "IS_AUTHENTICATED_ANONYMOUSLY", "AFTER_ACL_COLLECTION_READ" })
    Collection<ExperimentalFactor> loadAll();

    @Secured({ "GROUP_USER", "ACL_SECURABLE_EDIT" })
    void update( ExperimentalFactor experimentalFactor );

}
