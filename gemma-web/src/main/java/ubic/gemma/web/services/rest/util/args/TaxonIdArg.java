package ubic.gemma.web.services.rest.util.args;

import ubic.gemma.model.genome.TaxonValueObject;
import ubic.gemma.persistence.service.genome.taxon.TaxonService;
import ubic.gemma.model.genome.Taxon;

/**
 * Created by tesarst on 16/05/17.
 * Long argument type for taxon API, referencing the Taxon ID.
 */
public class TaxonIdArg extends TaxonArg<Long> {

    /**
     * @param l intentionally primitive type, so the value property can never be null.
     */
    TaxonIdArg( long l ) {
        this.value = l;
        this.nullCause = "The identifier was recognised to be an ID, but no taxon with this ID exists.";
    }

    @Override
    public Taxon getPersistentObject( TaxonService service ) {
        return service.load( this.value );
    }
}