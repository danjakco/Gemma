/*
 * The Gemma project.
 * 
 * Copyright (c) 2006-2012 University of British Columbia
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import ubic.gemma.model.common.Identifiable;
import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.model.common.description.VocabCharacteristic;
import ubic.gemma.model.common.measurement.Measurement;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Collection;

/**
 * The value for a ExperimentalFactor, representing a specific instance of the factor, such as "10 ug/kg" or "mutant"
 */
public class FactorValue implements Identifiable, Serializable, gemma.gsec.model.SecuredChild {

    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long serialVersionUID = -3783172994360698631L;
    private ExpressionExperiment securityOwner = null;
    private String value;
    private Boolean isBaseline;
    private Long id;
    private ExperimentalFactor experimentalFactor;
    private Measurement measurement;
    private Collection<Characteristic> characteristics = new java.util.HashSet<>();



    /**
     * No-arg constructor added to satisfy javabean contract
     *
     * @author Paul
     */
    public FactorValue() {
    }



    @Override
    public boolean equals( Object object ) {
        if ( object == null )
            return false;
        if ( this == object )
            return true;
        if ( !( object instanceof FactorValue ) )
            return false;
        FactorValue that = ( FactorValue ) object;
        if ( this.getId() != null && that.getId() != null )
            return this.getId().equals( that.getId() );

        if ( that.getId() == null && this.getId() != null )
            return false;

        /*
         * at this point, we know we have two FactorValues, at least one of which is transient, so we have to look at
         * the fields; pain in butt
         */

        return checkGuts( that );

    }

    @Override
    public int hashCode() {
        if ( this.getId() != null )
            return this.getId().hashCode();

        HashCodeBuilder builder = new HashCodeBuilder( 17, 7 ).append( this.getId() )
                .append( this.getExperimentalFactor() ).append( this.getMeasurement() );
        if ( this.getCharacteristics() != null ) {
            for ( Characteristic c : this.getCharacteristics() ) {
                if ( c instanceof VocabCharacteristic )
                    builder.append( ( ( VocabCharacteristic ) c ).hashCode() );
                else
                    builder.append( c.hashCode() );
            }
        }
        return builder.toHashCode();
    }

    /**
     * @see ubic.gemma.model.expression.experiment.FactorValue#toString()
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        // this can be null in tests or with half-setup transient objects
        buf.append( "FactorValue " + this.getId() + ": " );

        if ( this.getExperimentalFactor() != null )
            buf.append( this.getExperimentalFactor().getName() + ":" );
        if ( this.getCharacteristics().size() > 0 ) {
            for ( Characteristic c : this.getCharacteristics() ) {
                buf.append( c.getValue() );
                if ( this.getCharacteristics().size() > 1 )
                    buf.append( " | " );
            }
        } else if ( this.getMeasurement() != null ) {
            buf.append( this.getMeasurement().getValue() );
        } else if ( StringUtils.isNotBlank( this.getValue() ) ) {
            buf.append( this.getValue() );
        }
        return buf.toString();
    }



    @Override
    public Long getId() {
        return this.id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    @Override
    public ExpressionExperiment getSecurityOwner() {
        return securityOwner;
    }

    public void setSecurityOwner( ExpressionExperiment ee ) {
        this.securityOwner = ee;
    }

    public Collection<Characteristic> getCharacteristics() {
        return this.characteristics;
    }

    public void setCharacteristics( Collection<Characteristic> characteristics ) {
        this.characteristics = characteristics;
    }

    public ExperimentalFactor getExperimentalFactor() {
        return this.experimentalFactor;
    }

    public void setExperimentalFactor( ExperimentalFactor experimentalFactor ) {
        this.experimentalFactor = experimentalFactor;
    }

    /**
     * <p>
     * True if this is to be considered the baseline condition. This is ignored if the factor is numeric
     * (non-categorical).
     * </p>
     */
    public Boolean getIsBaseline() {
        return this.isBaseline;
    }

    public void setIsBaseline( Boolean isBaseline ) {
        this.isBaseline = isBaseline;
    }

    public Measurement getMeasurement() {
        return this.measurement;
    }

    public void setMeasurement( ubic.gemma.model.common.measurement.Measurement measurement ) {
        this.measurement = measurement;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @Transient
    public String getDescriptiveString() {
        if ( this.characteristics != null && this.characteristics.size() > 0 ) {
            StringBuilder fvString = new StringBuilder();
            for ( Characteristic c : this.characteristics ) {
                fvString.append( c.getValue() ).append( " " );
            }
            return fvString.toString();
        } else if ( this.measurement != null ) {
            return this.measurement.getValue();
        } else if ( this.value != null && !this.value.isEmpty() ) {
            return this.value;
        }

        return "absent ";
    }



    private boolean checkGuts( FactorValue that ) {

        if ( this.getExperimentalFactor() != null ) {
            if ( that.getExperimentalFactor() == null )
                return false;
            if ( !this.getExperimentalFactor().equals( that.getExperimentalFactor() ) ) {
                return false;
            }
        }

        if ( this.getCharacteristics().size() > 0 ) {
            if ( that.getCharacteristics().size() != this.getCharacteristics().size() )
                return false;

            for ( Characteristic c : this.getCharacteristics() ) {
                boolean match = false;
                for ( Characteristic c2 : that.getCharacteristics() ) {
                    if ( c.equals( c2 ) ) {
                        if ( match ) {
                            return false;
                        }
                        match = true;
                    }
                }
                if ( !match )
                    return false;
            }

        }

        if ( this.getMeasurement() != null ) {
            if ( that.getMeasurement() == null )
                return false;
            if ( !this.getMeasurement().equals( that.getMeasurement() ) )
                return false;
        }

        if ( this.getValue() != null ) {
            if ( that.getValue() == null )
                return false;
            if ( !this.getValue().equals( that.getValue() ) )
                return false;
        }

        // everything is empty...
        return true;
    }



    /**
     * Constructs new instances of {@link FactorValue}.
     */
    public static final class Factory {
        /**
         * Constructs a new instance of {@link FactorValue}.
         */
        public static FactorValue newInstance() {
            return new FactorValue();
        }

        /**
         * Constructs a new instance of {@link FactorValue}, taking all required and/or read-only properties as
         * arguments.
         */
        public static FactorValue newInstance( ExperimentalFactor experimentalFactor ) {
            final FactorValue entity = new FactorValue();
            entity.setExperimentalFactor( experimentalFactor );
            return entity;
        }
    }

}