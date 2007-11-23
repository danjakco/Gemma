package ubic.gemma.web.controller.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.model.common.description.CharacteristicService;
import ubic.gemma.model.common.description.VocabCharacteristic;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.biomaterial.BioMaterialService;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.model.expression.experiment.FactorValue;
import ubic.gemma.model.expression.experiment.FactorValueService;
import ubic.gemma.ontology.OntologyService;
import ubic.gemma.util.GemmaLinkUtils;
import ubic.gemma.web.controller.BaseFormController;
import ubic.gemma.web.controller.expression.experiment.AnnotationValueObject;

/**
 * @author luke
 * @spring.bean id="characteristicBrowserController"
 * @spring.property name="formView" value="characteristics"
 * @spring.property name="characteristicService" ref="characteristicService"
 * @spring.property name="expressionExperimentService" ref="expressionExperimentService"
 * @spring.property name="bioMaterialService" ref="bioMaterialService"
 * @spring.property name="factorValueService" ref="factorValueService"
 * @spring.property name="ontologyService" ref="ontologyService"
 */
public class CharacteristicBrowserController extends BaseFormController {

    private static Log log = LogFactory.getLog( CharacteristicBrowserController.class.getName() );

    CharacteristicService characteristicService;
    ExpressionExperimentService expressionExperimentService;
    BioMaterialService bioMaterialService;
    FactorValueService factorValueService;
    OntologyService ontologyService;
    
    @Override
    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
            throws Exception {
        return new ModelAndView( this.getFormView() );
    }
    
    public Collection<AnnotationValueObject> findCharacteristics( String valuePrefix ) {
        Collection<AnnotationValueObject> results = new HashSet<AnnotationValueObject>();
        Collection chars = characteristicService.findByValue( valuePrefix );
        Map charToParent = characteristicService.getParents( chars );
        for ( Object o : chars ) {
            Characteristic c = (Characteristic)o;
            AnnotationValueObject avo = new AnnotationValueObject();
            avo.setId( c.getId() );
            avo.setClassName( c.getCategory() );
            avo.setTermName( c.getValue() );
            if ( c instanceof VocabCharacteristic ) {
                VocabCharacteristic vc = (VocabCharacteristic)c;
                avo.setClassUri( vc.getCategoryUri() );
                avo.setTermUri( vc.getValueUri() );
            }
            Object parent = charToParent.get( c );
            populateParentInformation( avo, charToParent.get( c ) );
            results.add( avo );
        }
        return results;
    }
    
    public void removeCharacteristics( Collection<Characteristic> chars ) {
        Map charToParent = characteristicService.getParents( chars );
        for ( Characteristic c : chars ) {
            removeFromParent( c, charToParent.get( c ) );
            characteristicService.delete( c );
        }
    }
    
    public void updateCharacteristics( Collection<Characteristic> chars ) {
        Map charToParent = characteristicService.getParents( chars );
        for ( Characteristic cFromClient : chars ) {
            Characteristic cFromDatabase = characteristicService.load( cFromClient.getId() );
            VocabCharacteristic vcFromClient =
                ( cFromClient instanceof VocabCharacteristic ) ? (VocabCharacteristic)cFromClient : null;
            VocabCharacteristic vcFromDatabase =
                ( cFromDatabase instanceof VocabCharacteristic ) ? (VocabCharacteristic)cFromDatabase : null;
            
            /* if one of the characteristics is a VocabCharacteristic and the other is not, we have
             * to change the characteristic in the database so that it matches the one from the client;
             * since we can't change the class of the object, we have to delete the old characteristic
             * and make a new one of the appropriate class.
             */
            if ( vcFromClient != null && vcFromDatabase == null ) {
                vcFromDatabase = (VocabCharacteristic)characteristicService.create(
                    VocabCharacteristic.Factory.newInstance(
                        null,
                        null,
                        cFromDatabase.getValue(),
                        cFromDatabase.getCategory(),
                        cFromDatabase.getEvidenceCode(),
                        cFromDatabase.getName(),
                        cFromDatabase.getDescription(),
                        null,
                        cFromDatabase.getAuditTrail()
                    ) );
                Object parent = charToParent.get( cFromDatabase );
                removeFromParent( cFromDatabase, parent );
                characteristicService.delete( cFromDatabase );
                addToParent( vcFromDatabase, parent );
                cFromDatabase = vcFromDatabase;
            } else if ( vcFromClient == null && vcFromDatabase != null ) {
                cFromDatabase = characteristicService.create(
                    Characteristic.Factory.newInstance(
                        vcFromDatabase.getValue(),
                        vcFromDatabase.getCategory(),
                        vcFromDatabase.getEvidenceCode(),
                        vcFromDatabase.getName(),
                        vcFromDatabase.getDescription(),
                        vcFromDatabase.getAuditTrail()
                    )
                );
                Object parent = charToParent.get( vcFromDatabase );
                removeFromParent( vcFromDatabase, parent );
                characteristicService.delete( vcFromDatabase );
                addToParent( cFromDatabase, parent );
            }
            
            /* at this point, cFromDatabase points to the class-corrected characteristic in the
             * database that must be updated with the information coming from the client; at the
             * moment, the only things that the client can change are the category, value and
             * associated URIs.  TODO allow changing of the evidence code.
             */
            cFromDatabase.setValue( cFromClient.getValue() );
            cFromDatabase.setCategory( cFromClient.getCategory() );
            if ( cFromDatabase instanceof VocabCharacteristic ) {
                vcFromDatabase = (VocabCharacteristic)cFromDatabase;
                vcFromDatabase.setValueUri( vcFromClient.getValueUri() );
                vcFromDatabase.setCategoryUri( vcFromClient.getCategoryUri() );
            }
            characteristicService.update( cFromDatabase );
        }
    }
    
    private void removeFromParent( Characteristic c, Object parent ) {
        if ( parent instanceof ExpressionExperiment ) {
            ExpressionExperiment ee = (ExpressionExperiment)parent;
            ee.getCharacteristics().remove( c );
            expressionExperimentService.update( ee );
        } else if ( parent instanceof BioMaterial ) {
            BioMaterial bm = (BioMaterial)parent;
            bm.getCharacteristics().remove( c );
            bioMaterialService.update( bm );
        } else if ( parent instanceof FactorValue ) {
            FactorValue fv = (FactorValue)parent;
            fv.getCharacteristics().remove( c );
            factorValueService.update( fv );
        }
    }
    
    private void addToParent( Characteristic c, Object parent ) {
        if ( parent instanceof ExpressionExperiment ) {
            ExpressionExperiment ee = (ExpressionExperiment)parent;
            ee.getCharacteristics().add( c );
            expressionExperimentService.update( ee );
        } else if ( parent instanceof BioMaterial ) {
            BioMaterial bm = (BioMaterial)parent;
            bm.getCharacteristics().add( c );
            bioMaterialService.update( bm );
        } else if ( parent instanceof FactorValue ) {
            FactorValue fv = (FactorValue)parent;
            fv.getCharacteristics().add( c );
            factorValueService.update( fv );
        }
    }

    private void populateParentInformation( AnnotationValueObject avo, Object parent ) {
        if ( parent == null )
            return;
        if ( parent instanceof ExpressionExperiment ) {
            ExpressionExperiment ee = (ExpressionExperiment)parent;
            avo.setParentDescription( String.format( "ExpressionExperiment: %s", ee.getName() ) );
            avo.setParentLink( GemmaLinkUtils.getExpressionExperimentLink( ee.getId(), avo.getParentDescription(), avo.getParentDescription() ) );
        } else if ( parent instanceof BioMaterial ) {
            BioMaterial bm = (BioMaterial)parent;
            avo.setParentDescription( String.format( "BioMaterial: %s", bm.getDescription() ) );
            avo.setParentLink( GemmaLinkUtils.getBioMaterialLink( bm.getId(), avo.getParentDescription(), avo.getParentDescription() ) );
        } else if ( parent instanceof FactorValue ) {
            FactorValue fv = (FactorValue)parent;
            avo.setParentDescription( String.format( "FactorValue: %s : %s", fv.getExperimentalFactor().getName(), fv.getValue() ) );
            avo.setParentLink( GemmaLinkUtils.getExperimentalDesignLink( fv.getExperimentalFactor().getExperimentalDesign().getId(), avo.getParentDescription(), avo.getParentDescription() ) );
        }
    }

    /**
     * @param characteristicService the characteristicService to set
     */
    public void setCharacteristicService( CharacteristicService characteristicService ) {
        this.characteristicService = characteristicService;
    }

    /**
     * @param ontologyService the ontologyService to set
     */
    public void setOntologyService( OntologyService ontologyService ) {
        this.ontologyService = ontologyService;
    }

    /**
     * @param bioMaterialService the bioMaterialService to set
     */
    public void setBioMaterialService( BioMaterialService bioMaterialService ) {
        this.bioMaterialService = bioMaterialService;
    }

    /**
     * @param expressionExperimentService the expressionExperimentService to set
     */
    public void setExpressionExperimentService( ExpressionExperimentService expressionExperimentService ) {
        this.expressionExperimentService = expressionExperimentService;
    }

    /**
     * @param factorValueService the factorValueService to set
     */
    public void setFactorValueService( FactorValueService factorValueService ) {
        this.factorValueService = factorValueService;
    }
    
}
