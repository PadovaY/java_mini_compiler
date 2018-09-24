package oop.ex6.Variable;

/**
 * this class represents a boolean variable
 */
public class BooleanVariable extends Variable{

    /**
     *  constructor
     * @param varName - name of variable
     * @param varValue - value of variable
     * @param isModifier - final or not
     */
    public BooleanVariable(String varName, String varValue, boolean isModifier){
        super(varName, varValue, isModifier);
    }

    /**
     * get the type of the variable
     * @return String, type of variable
     */
    @Override
    public String getType(){
        return BOOLEAN;
    }


}
