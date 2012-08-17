/*
 * The Gemma project
 * 
 * Copyright (c) 2007 University of British Columbia
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
package ubic.gemma.model.genome.gene.phenotype.valueObject;

import java.util.SortedSet;

import ubic.gemma.model.analysis.expression.diff.GeneDifferentialExpressionMetaAnalysisResult;
import ubic.gemma.model.association.phenotype.DifferentialExpressionEvidence;

/**
 * TODO Document Me
 * 
 * @author stgeorgn
 * @version $Id$
 */
public class DiffExpressionEvidenceValueObject extends EvidenceValueObject {

    // TODO need to populate this as an valueObject...
    private GeneDifferentialExpressionMetaAnalysisResult differentialExpressionAnalysisResult = null;

    public DiffExpressionEvidenceValueObject( Integer geneNCBI, SortedSet<CharacteristicValueObject> phenotypes,
            String description, String evidenceCode, boolean isNegativeEvidence,
            EvidenceSourceValueObject evidenceSource,
            GeneDifferentialExpressionMetaAnalysisResult differentialExpressionAnalysisResult ) {
        super( geneNCBI, phenotypes, description, evidenceCode, isNegativeEvidence, evidenceSource );
        this.differentialExpressionAnalysisResult = differentialExpressionAnalysisResult;
    }

    /** Entity to Value Object */
    public DiffExpressionEvidenceValueObject( DifferentialExpressionEvidence differentialExpressionEvidence ) {
        super( differentialExpressionEvidence );

        this.differentialExpressionAnalysisResult = differentialExpressionEvidence
                .getGeneDifferentialExpressionMetaAnalysisResult();
    }

    public DiffExpressionEvidenceValueObject() {
        super();
    }

}
