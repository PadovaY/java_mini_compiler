package oop.ex6.Variable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oop.ex6.Scope.*;

/**
 * this class represents the variable factory. it validates
 * assigns and generate new variables.
 */

public class VariableFactory {

    /*--constants--*/
    public final static String STRING = "String";
    public final static String INT = "int";
    public final static String DOUBLE = "double";
    public final static String BOOLEAN = "boolean";
    public final static String CHAR = "char";

    /*--magic numbers--*/
    private final static int  INT_GROUP =6;
    private final static int  DOUBLE_GROUP = 5;
    private final static int  BOOLEAN_GROUP = 4;
    private final static int  STRING_GROUP = 2;
    private final static int  CHAR_GROUP = 3;

    private final static String VALUE_FORMAT = "((\".*\")|('[\\x00-\\x7F]')|" +
                                                            "(true|false)|(-?[0-9]+\\.[0-9]+)|(-?[0-9]+))";
    private final static Pattern VALUE_CHECK = Pattern.compile(VALUE_FORMAT);

    /**
     * this method generates the new variable, based the type, name and value.
     * if there is no value, it will be set as null
     * @param varType - the type of variable
     * @param varName - the name of variable
     * @param isModifier - is there a final in the assign
     * @param varValue - the value of the variable
     * @return - Variable object.
     * @throws IllegalVariableException - no such variable exist
     * @throws IncompatibleTypesException - type of value and type incompatible
     * @throws IllegalAssignmentToFinalException -  try to assign new value to a final variable
     */
    public static Variable variableGenerator(String varType, String varName,
                                                                        boolean isModifier, String varValue)
            throws IllegalVariableException, IncompatibleTypesException, IllegalAssignmentToFinalException {
        boolean isValueAsType;
        try {
            if (varValue != null) {  //checks if the type and value compatible
                isValueAsType = typeOfValue(varType, varValue);
            if(!isValueAsType)
                throw new IncompatibleTypesException();
        }
            if(varValue == null && isModifier){     //checks for try to declare a final without value
            throw new IllegalAssignmentToFinalException();
            }
            switch (varType) {      //switch cases based on type
                case INT:
                    return new IntVariable(varName, varValue, isModifier);
                case DOUBLE:
                        return new DoubleVariable(varName, varValue, isModifier);
                case BOOLEAN:
                        return new BooleanVariable(varName, varValue, isModifier);
                case STRING:
                        return new StringVariable(varName, varValue, isModifier);
                case CHAR:
                        return new CharVariable(varName, varValue, isModifier);
                default:
                    throw new IllegalVariableException();   //the kind of type does not exist
            }

        } catch (IllegalVariableException exception) {
            System.err.println("No such variable exists");
            return null;
        } catch (IncompatibleTypesException | IllegalAssignmentToFinalException exception) {
            System.err.println(exception.getMessage());
            return null;
        }

    }


    /**
     * this method validates that the type and value are compatible
     * @param type - the type of variable
     * @param value - the value of variable
     * @return - true if compatible, false otherwise
     */
    private static boolean typeOfValue(String type ,String value) {
        //with the matcher we can find if the value is in the right group of type
        Matcher matchValueToType = VALUE_CHECK.matcher(value);
        if (matchValueToType.find()) {
            if (matchValueToType.group(INT_GROUP) != null && type.equals(INT)) {
                return true;
            } else if (type.equals(DOUBLE) && (matchValueToType.group(DOUBLE_GROUP) != null ||
                                                                matchValueToType.group(INT_GROUP) != null )) {
                return true;
            } else if (matchValueToType.group(STRING_GROUP) != null && type.equals(STRING)) {
                return true;
            } else if ((matchValueToType.group(BOOLEAN_GROUP) != null || matchValueToType.group(INT_GROUP) !=
                            null|| matchValueToType.group(DOUBLE_GROUP) != null) && type.equals(BOOLEAN)) {
                return true;
            } else if (matchValueToType.group(CHAR_GROUP) != null && type.equals(CHAR)) {
                return true;
            }
        }
        //meaning the types are incompatible.
        return false;
    }

}