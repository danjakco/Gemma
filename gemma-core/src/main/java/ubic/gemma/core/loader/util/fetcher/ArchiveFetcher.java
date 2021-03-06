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
package ubic.gemma.core.loader.util.fetcher;

/**
 * Interface defining a class that downloads archives and unpacks them.
 *
 * @author pavlidis
 */
public interface ArchiveFetcher extends Fetcher {

    /**
     * Should the downloaded archive be deleted after unpacking?
     *
     * @param doDelete whether to delete
     */
    @SuppressWarnings("unused")
    // Possible external use
    void setDeleteAfterUnpack( boolean doDelete );

}
