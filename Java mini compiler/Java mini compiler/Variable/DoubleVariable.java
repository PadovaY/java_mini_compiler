package oop.ex6.Variable;


public class DoubleVariable extends Variable{

    /**
     *  constructor
     * @param varName - name of variable
     * @param varValue - value of variable
     * @param isModifier - final or not
     */
    public DoubleVariable(String varName, String varValue, boolean isModifier){
        super(varName, varValue, isModifier);
    }

    /**
     * get the type of the variable
     * @return String, type of variable
     */
    @Override
    public String getType(){
        return DOUBLE;
    }


}
