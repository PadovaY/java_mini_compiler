package oop.ex6.Variable;


/**
 * this class represents a Variable object
 */
public abstract class Variable {

    /*--constants--*/
    public final static String STRING = "String";
    public final static String INT = "int";
    public final static String DOUBLE = "double";
    public final static String BOOLEAN = "boolean";
    public final static String CHAR = "char";

    /*--data members--*/
    private boolean isModifier;
    private String varValue;
    private String varName;

    /**
     *  constructor of declaration without value
     * @param varName - name of variable
     * @param isModifier - final or not
     */
    public Variable(String varName, boolean isModifier){
        this.varName = varName;
        this.isModifier = isModifier;
    }


    /**
     * constructor with variable value
     * @param varName - name of variable
     * @param varValue - value of variable
     * @param isModifier - final or not
     */
    public Variable(String varName, String varValue, boolean isModifier){
        this.varName = varName;
        this.varValue = varValue;
        this.isModifier = isModifier;
    }

    /**
     * gets the type of variable
     * @return String type of variable
     */
    public abstract String getType();


    /**
     * gets the name of variable
     * @return String name of variable
     */
    public String getName(){

        return this.varName;
    }

    /**
     * gets if the variable is final or not
     * @return true if final, false if not
     */
    public boolean getIsModifier(){

        return this.isModifier;
    }

    /**
     * gets the value of variable
     * @return String value of variable
     */
    public String getValue(){
        return this.varValue;
    }

    /**
     * sets the value of variable
     * @param value - the value to set to variable
     */
    public void setValue(String value){
        this.varValue = value;
    }
}
