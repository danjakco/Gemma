package ubic.gemma.web.services.rest.util.args;

import ubic.gemma.persistence.util.ObjectFilter;
import ubic.gemma.web.services.rest.util.GemmaApiException;
import ubic.gemma.web.services.rest.util.WellComposedErrorBody;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A base class for filter arguments implementing methods common to all filter arguments.
 *
 * @author tesarst
 */
public abstract class FilterArg extends MalformableArg {

    public static final String ERROR_MSG_MALFORMED_REQUEST = "Entity does not contain the given property, or the provided value can not be converted to the property type.";
    private static final String ERROR_MSG_PARTS_TOO_SHORT = "Provided filter string does not contain at least one of property-operator-value sets.";
    private static final String ERROR_MSG_ILLEGAL_OPERATOR = "Illegal operator: %s is not an accepted operator.";
    private static final String ERROR_MSG_ARGS_MISALIGNED = "Filter query problem: Amount of properties, operators and values does not match";

    private List<String[]> propertyNames;
    private List<String[]> propertyValues;
    private List<String[]> propertyOperators;
    private List<Class[]> propertyTypes;
    private String objectAlias;

    /**
     * @param propertyNames     names of properties to filter by. <br/>
     *                          Elements in each array will be in a disjunction (OR) with each other.<br/>
     *                          Arrays will then be in a conjunction (AND) with each other.<br/>
     * @param propertyValues    values to compare the given property names to.
     * @param propertyOperators the operation used for comparison of the given value and the value of the object.
     *                          The propertyValues will be the right operand of each given operator.
     *                          <br/>
     *                          E.g: <code>object.propertyName[0] isNot propertyValues[0];</code><br/>
     * @param propertyTypes     Type of each property in propertyNames. Each property name can be referencing multiple
     *                          nested properties, but we only need to know the type of the last property in the line of nesting.
     * @param objectAlias       See {@link ObjectFilter} constructors objectAlias parameter.
     */
    FilterArg( List<String[]> propertyNames, List<String[]> propertyValues, List<String[]> propertyOperators,
            List<Class[]> propertyTypes, String objectAlias ) {
        super();
        this.propertyNames = propertyNames;
        this.propertyValues = propertyValues;
        this.propertyOperators = propertyOperators;
        this.propertyTypes = propertyTypes;
        this.objectAlias = objectAlias;
    }

    FilterArg( String errorMessage, Exception exception ) {
        super( errorMessage, exception );
    }

    public static FilterArg EMPTY_FILTER(){
        return new FilterArg(null, null, null, null, null) {};
    }

    /**
     * Tries to access the give properties on the given class to check if they exist. Does not check whether these properties
     * are a mapped hibernate property.
     *
     * @param propertyNames the names to be checked.
     * @param cls           the class to check the properties for.
     * @return a List of class arrays representing the class of each property from the 'propertyNames' arg.
     * @throws NoSuchFieldException if one of the properties did not exist in the given class.
     */
    static List<Class[]> getPropertiesTypes( List<String[]> propertyNames, Class cls ) throws NoSuchFieldException {
        List<Class[]> propertyTypes = new ArrayList<>( propertyNames.size() );
        for ( String[] array : propertyNames ) {
            Class[] arr = new Class[array.length];
            int i = 0;
            for ( String property : array ) {
                arr[i++] = getPropertyType( property, cls );
            }
            propertyTypes.add( arr );
        }
        return propertyTypes;
    }

    /**
     * Parses the input string into lists of logical disjunctions, that together form a conjunction (CNF).
     *
     * @param s                 the string to be parsed.
     * @param propertyNames     list to be populated with property name arrays for each disjunction.
     * @param propertyValues    list to be populated with property value arrays for each disjunction.
     * @param propertyOperators list to be populated with operator arrays for each disjunction.
     */
    static void parseFilterString( String s, List<String[]> propertyNames, List<String[]> propertyValues,
            List<String[]> propertyOperators ) {
        String[] parts = s.split( "\\s+" );

        List<String> propertyNamesDisjunction = new LinkedList<>();
        List<String> propertyOperatorsDisjunction = new LinkedList<>();
        List<String> propertyValuesDisjunction = new LinkedList<>();
        if ( parts.length < 3 ) {
            throw new IllegalArgumentException( ERROR_MSG_PARTS_TOO_SHORT );
        }

        for ( int i = 0; i < parts.length; ) {
            propertyNamesDisjunction.add( parts[i++] );
            propertyOperatorsDisjunction.add( parseObjectFilterOperator( parts[i++] ) );
            propertyValuesDisjunction.add( parts[i++] );

            if ( i == parts.length || parts[i].toLowerCase().equals( "and" ) ) {
                // We either reached an 'AND', or the end of the string.
                // Add the current disjunction.
                propertyNames.add( propertyNamesDisjunction.toArray( new String[propertyNamesDisjunction.size()] ) );
                propertyOperators
                        .add( propertyOperatorsDisjunction.toArray( new String[propertyOperatorsDisjunction.size()] ) );
                propertyValues.add( propertyValuesDisjunction.toArray( new String[propertyValuesDisjunction.size()] ) );
                // Start new disjunction lists
                propertyNamesDisjunction = new LinkedList<>();
                propertyOperatorsDisjunction = new LinkedList<>();
                propertyValuesDisjunction = new LinkedList<>();
                i++;
            } else if ( parts[i].toLowerCase().equals( "or" ) ) {
                // Skip this part and continue the disjunction
                i++;
            }
        }
    }

    /**
     * Checks if property of given name exists in the given class. If the given string specifies
     * nested properties (E.g. curationDetails.troubled), only the substring before the first dot is evaluated and the
     * rest of the string is processed in new recursion iteration.
     *
     * @param property the property to check for. If the string contains dot characters ('.'), only the part
     *                 before the first dot will be evaluated. Substring after the dot will be checked against the
     *                 type of the field retrieved from the substring before the dot.
     * @param cls      the class to check the property on.
     * @return the class of the property last in the line of nesting.
     * @throws NoSuchFieldException if the property did not exist on the given class, or one of the recursive iterations
     *                              thrown this exception.
     */
    private static Class getPropertyType( String property, Class cls ) throws NoSuchFieldException {
        String[] parts = property.split( "\\.", 2 );
        Field field = checkAllFields( cls, parts[0] );
        Class<?> subCls = field.getType();
        if ( parts.length > 1 ) {
            return getPropertyType( parts[1], subCls );
        } else {
            return subCls;
        }
    }

    /**
     * Checks whether a field exists among all declared fields on the given class.
     *
     * @param cls   the class to check.
     * @param field the field name to check for.
     * @return the Field from the given class matching the given field name.
     * @throws NoSuchFieldException if a field of given name was not found on the given class.
     */
    private static Field checkAllFields( Class<?> cls, String field ) throws NoSuchFieldException {
        List<Field> fields = new ArrayList<>();
        for ( Class<?> c = cls; c != null; c = c.getSuperclass() ) {
            fields.addAll( Arrays.asList( c.getDeclaredFields() ) );
        }

        for ( Field f : fields ) {
            if ( f.getName().equals( field ) )
                return f;
        }
        throw new NoSuchFieldException( "Class " + cls + " does not contain field '" + field + "'." );
    }

    /**
     * Parses the string into a valid operator.
     *
     * @param s the string to be parsed.
     * @return a string that is a valid operator.
     * @throws IllegalArgumentException if the
     */
    private static String parseObjectFilterOperator( String s ) {
        if ( s.equals( ObjectFilter.is ) || s.equals( ObjectFilter.isNot ) || s.equals( ObjectFilter.like ) || s
                .equals( ObjectFilter.greaterThan ) || s.equals( ObjectFilter.lessThan ) || s
                .equals( ObjectFilter.greaterOrEq ) || s.equals( ObjectFilter.lessOrEq ) || s
                .equals( ObjectFilter.in ) ) {
            return s;
        }
        throw new IllegalArgumentException( String.format( ERROR_MSG_ILLEGAL_OPERATOR, s ) );
    }

    /**
     * Creates an ArrayList of Object Filter arrays, that can be used as a filter parameter for service value object
     * retrieval.
     *
     * @return an ArrayList of Object Filter arrays, each array represents a disjunction (OR) of filters. Arrays
     * then represent a conjunction (AND) with other arrays in the list.
     */
    public ArrayList<ObjectFilter[]> getObjectFilters() {
        this.checkMalformed();
        if ( propertyNames == null || propertyNames.isEmpty() )
            return null;
        ArrayList<ObjectFilter[]> filterList = new ArrayList<>( propertyNames.size() );

        for ( int i = 0; i < propertyNames.size(); i++ ) {
            try {
                String[] properties = propertyNames.get( i );
                String[] values = propertyValues.get( i );
                String[] operators = propertyOperators.get( i );
                Class[] types = propertyTypes.get( i );

                ObjectFilter[] filterArray = new ObjectFilter[properties.length];
                for ( int j = 0; j < properties.length; j++ ) {
                    filterArray[j] = new ObjectFilter( properties[j], types[j], values[j], operators[j], objectAlias );
                }
                filterList.add( filterArray );

            } catch ( IndexOutOfBoundsException e ) {
                throw new GemmaApiException(
                        new WellComposedErrorBody( Response.Status.BAD_REQUEST, ERROR_MSG_ARGS_MISALIGNED ) );
            } catch ( ParseException e ) {
                WellComposedErrorBody error = new WellComposedErrorBody( Response.Status.BAD_REQUEST,
                        ERROR_MSG_MALFORMED_REQUEST );
                WellComposedErrorBody.addExceptionFields( error, e );
                throw new GemmaApiException( error );
            }
        }

        return filterList;
    }

}
