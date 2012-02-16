/*
 * The Gemma project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.gemma.model.expression.bioAssay;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.biomaterial.BioMaterial;

/**
 * @author pavlidis
 * @author keshav
 * @author joseph
 * @version $Id$
 * @see ubic.gemma.model.expression.bioAssay.BioAssayService
 */
@Service
public class BioAssayServiceImpl implements BioAssayService {

    @Autowired
    private ubic.gemma.model.expression.biomaterial.BioMaterialDao bioMaterialDao;

    @Autowired
    private ubic.gemma.model.expression.bioAssay.BioAssayDao bioAssayDao;

    /**
     * @see ubic.gemma.model.expression.bioAssay.BioAssayService#addBioMaterialAssociation(ubic.gemma.model.expression.bioAssay.BioAssay,
     *      ubic.gemma.model.expression.biomaterial.BioMaterial)
     */
    public void addBioMaterialAssociation( final BioAssay bioAssay,
            final ubic.gemma.model.expression.biomaterial.BioMaterial bioMaterial ) {
        try {
            this.handleAddBioMaterialAssociation( bioAssay, bioMaterial );
        } catch ( Throwable th ) {
            throw new BioAssayServiceException(
                    "Error performing 'BioAssayService.addBioMaterialAssociation(BioAssay bioAssay, ubic.gemma.model.expression.biomaterial.BioMaterial bioMaterial)' --> "
                            + th, th );
        }
    }

    /**
     * @see BioAssayService#countAll()
     */
    public java.lang.Integer countAll() {
        try {
            return this.handleCountAll();
        } catch ( Throwable th ) {
            throw new BioAssayServiceException( "Error performing 'BioAssayService.countAll()' --> " + th, th );
        }
    }

    /**
     * @see BioAssayService#findBioAssayDimensions(BioAssay)
     */
    public java.util.Collection<BioAssayDimension> findBioAssayDimensions( final BioAssay bioAssay ) {
        try {
            return this.handleFindBioAssayDimensions( bioAssay );
        } catch ( Throwable th ) {
            throw new BioAssayServiceException(
                    "Error performing 'BioAssayService.findBioAssayDimensions(BioAssay bioAssay)' --> " + th, th );
        }
    }

    /**
     * @see BioAssayService#findOrCreate(BioAssay)
     */
    public BioAssay findOrCreate( final BioAssay bioAssay ) {
        try {
            return this.handleFindOrCreate( bioAssay );
        } catch ( Throwable th ) {
            throw new BioAssayServiceException(
                    "Error performing 'BioAssayService.findOrCreate(BioAssay bioAssay)' --> " + th, th );
        }
    }

    /**
     * @see BioAssayService#load(java.lang.Long)
     */
    public BioAssay load( final java.lang.Long id ) {
        try {
            return this.handleLoad( id );
        } catch ( Throwable th ) {
            throw new BioAssayServiceException( "Error performing 'BioAssayService.load(java.lang.Long id)' --> " + th,
                    th );
        }
    }

    /**
     * @see BioAssayService#loadAll()
     */
    public java.util.Collection<BioAssay> loadAll() {
        try {
            return this.handleLoadAll();
        } catch ( Throwable th ) {
            throw new BioAssayServiceException( "Error performing 'BioAssayService.loadAll()' --> " + th, th );
        }
    }

    /**
     * @see BioAssayService#remove(BioAssay)
     */
    public void remove( final BioAssay bioAssay ) {
        try {
            this.handleRemove( bioAssay );
        } catch ( Throwable th ) {
            throw new BioAssayServiceException( "Error performing 'BioAssayService.remove(BioAssay bioAssay)' --> "
                    + th, th );
        }
    }

    /**
     * @see BioAssayService#removeBioMaterialAssociation(BioAssay, ubic.gemma.model.expression.biomaterial.BioMaterial)
     */
    public void removeBioMaterialAssociation( final BioAssay bioAssay,
            final ubic.gemma.model.expression.biomaterial.BioMaterial bioMaterial ) {
        try {
            this.handleRemoveBioMaterialAssociation( bioAssay, bioMaterial );
        } catch ( Throwable th ) {
            throw new BioAssayServiceException(
                    "Error performing 'BioAssayService.removeBioMaterialAssociation(BioAssay bioAssay, ubic.gemma.model.expression.biomaterial.BioMaterial bioMaterial)' --> "
                            + th, th );
        }
    }

    /**
     * Sets the reference to <code>bioAssay</code>'s DAO.
     */
    public void setBioAssayDao( BioAssayDao bioAssayDao ) {
        this.bioAssayDao = bioAssayDao;
    }

    /**
     * Sets the reference to <code>bioMaterialService</code>.
     */
    public void setBioMaterialDao( ubic.gemma.model.expression.biomaterial.BioMaterialDao bioMaterialDao ) {
        this.bioMaterialDao = bioMaterialDao;
    }

    /**
     * @see BioAssayService#thaw(BioAssay)
     */
    public void thaw( final BioAssay bioAssay ) {
        try {
            this.handleThaw( bioAssay );
        } catch ( Throwable th ) {
            throw new BioAssayServiceException( "Error performing 'BioAssayService.thaw(BioAssay bioAssay)' --> " + th,
                    th );
        }
    }

    /**
     * @see BioAssayService#update(BioAssay)
     */
    public void update( final BioAssay bioAssay ) {
        try {
            this.handleUpdate( bioAssay );
        } catch ( Throwable th ) {
            throw new BioAssayServiceException( "Error performing 'BioAssayService.update(BioAssay bioAssay)' --> "
                    + th, th );
        }
    }

    /**
     * Gets the reference to <code>bioAssay</code>'s DAO.
     */
    protected BioAssayDao getBioAssayDao() {
        return this.bioAssayDao;
    }

    /**
     * Gets the reference to <code>bioMaterialDao</code>.
     */
    protected ubic.gemma.model.expression.biomaterial.BioMaterialDao getBioMaterialDao() {
        return this.bioMaterialDao;
    }

    
    
    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.bioAssay.BioAssayService#create(ubic.gemma.model.expression.bioAssay.BioAssay)
     */
    public BioAssay create( BioAssay bioAssay ) {
        return this.getBioAssayDao().create( bioAssay );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ubic.gemma.model.expression.bioAssay.BioAssayServiceBase#handleAssociateBioMaterial(ubic.gemma.model.expression
     * .bioAssay.BioAssay, ubic.gemma.model.expression.biomaterial.BioMaterial)
     */
    protected void handleAddBioMaterialAssociation( BioAssay bioAssay, BioMaterial bioMaterial ) throws Exception {
        // add bioMaterial to bioAssay
        Collection<BioMaterial> currentBioMaterials = bioAssay.getSamplesUsed();
        currentBioMaterials.add( bioMaterial );
        bioAssay.setSamplesUsed( currentBioMaterials );

        // add bioAssay to bioMaterial
        Collection<BioAssay> currentBioAssays = bioMaterial.getBioAssaysUsedIn();
        currentBioAssays.add( bioAssay );
        bioMaterial.setBioAssaysUsedIn( currentBioAssays );

        // update bioMaterial name - remove text after pipes
        // this should not be necessary going forward

        // build regular expression - match only text before the first pipe
        Pattern pattern = Pattern.compile( "^(.+)|" );
        String bmName = bioMaterial.getName();
        Matcher matcher = pattern.matcher( bmName );
        if ( matcher.find() ) {
            String shortName = matcher.group();
            bioMaterial.setName( shortName );
        }

        this.update( bioAssay );
        this.getBioMaterialDao().update( bioMaterial );
    }

    protected Integer handleCountAll() throws Exception {
        return this.getBioAssayDao().countAll();
    }

    protected Collection<BioAssayDimension> handleFindBioAssayDimensions( BioAssay bioAssay ) throws Exception {
        if ( bioAssay.getId() == null ) throw new IllegalArgumentException( "BioAssay must be persistent" );
        return this.getBioAssayDao().findBioAssayDimensions( bioAssay );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssay.BioAssayService#findOrCreate(edu.columbia.gemma.expression.bioAssay.BioAssay)
     */
    protected BioAssay handleFindOrCreate( BioAssay bioAssay ) throws Exception {
        return this.getBioAssayDao().findOrCreate( bioAssay );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssay.BioAssayService#findById(Long)
     */
    protected BioAssay handleLoad( Long id ) throws Exception {
        return this.getBioAssayDao().load( id );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssay.BioAssayService#loadAll()
     */
    protected Collection<BioAssay> handleLoadAll() throws Exception {
        return ( Collection<BioAssay> ) this.getBioAssayDao().loadAll();
    }

    /**
     * @see ubic.gemma.model.expression.bioAssay.BioAssayService#markAsMissing(edu.columbia.gemma.expression.bioAssay.BioAssay)
     */
    protected void handleRemove( BioAssay bioAssay ) throws Exception {
        this.getBioAssayDao().remove( bioAssay );

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ubic.gemma.model.expression.bioAssay.BioAssayServiceBase#handleRemoveBioMaterial(ubic.gemma.model.expression.
     * bioAssay.BioAssay, ubic.gemma.model.expression.biomaterial.BioMaterial)
     */
    protected void handleRemoveBioMaterialAssociation( BioAssay bioAssay, BioMaterial bioMaterial ) throws Exception {
        // remove bioMaterial from bioAssay
        this.getBioAssayDao().thaw( bioAssay );

        Collection<BioMaterial> currentBioMaterials = bioAssay.getSamplesUsed();
        currentBioMaterials.remove( bioMaterial );
        bioAssay.setSamplesUsed( currentBioMaterials );

        // remove bioAssay from bioMaterial
        Collection<BioAssay> currentBioAssays = bioMaterial.getBioAssaysUsedIn();
        currentBioAssays.remove( bioAssay );
        bioMaterial.setBioAssaysUsedIn( currentBioAssays );

        this.getBioMaterialDao().update( bioMaterial );
        this.update( bioAssay );

        // check to see if the bioMaterial is now orphaned.
        // if it is, delete it; if not, update it
        if ( currentBioAssays.size() == 0 ) {
            this.getBioMaterialDao().remove( bioMaterial );
        }

    }

    /**
     * @see ubic.gemma.model.expression.bioAssay.BioAssayService#saveBioAssay(edu.columbia.gemma.expression.bioAssay.BioAssay)
     */
    protected void handleSaveBioAssay( ubic.gemma.model.expression.bioAssay.BioAssay bioAssay )
            throws java.lang.Exception {
        this.getBioAssayDao().create( bioAssay );
    }

    protected void handleThaw( BioAssay bioAssay ) throws Exception {
        this.getBioAssayDao().thaw( bioAssay );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssay.BioAssayService#update(BioAssay)
     */
    protected void handleUpdate( BioAssay bioAssay ) throws Exception {
        this.getBioAssayDao().update( bioAssay );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.bioAssay.BioAssayService#thaw(java.util.Collection)
     */
    public Collection<BioAssay> thaw( Collection<BioAssay> bioAssays ) {
        return this.getBioAssayDao().thaw( bioAssays );
    }

    public Collection<BioAssay> findByAccession( String accession ) {
        return this.getBioAssayDao().findByAccession( accession );
    }

}