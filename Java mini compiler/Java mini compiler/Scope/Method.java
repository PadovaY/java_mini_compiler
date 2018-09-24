package oop.ex6.Scope;

import oop.ex6.Variable.Variable;
import java.util.ArrayList;


/**
 * this class represents a method
 */
public abstract class Method extends Scope{

    /*--data members--*/
    String name;
    String type;
    ArrayList<Variable> parameters;

    /**
     * constructor.
     * @param name - name pf method
     * @param type - type of method
     * @param parameters - the parameters of the method
     */
    public Method(String name, String type,  ArrayList<Variable> parameters){
        super(parameters);
        this.name = name;
        this.type = type;
        this.parameters = parameters;
    }

    /**
     * the method gts the name of the method
     * @return = the name of method
     */
    public String getName(){

        return this.name;
    }


    /**
     * the method gets the parameters of the method
     * @return list of the parameters
     */
    public ArrayList<Variable> getParameters() {

        return this.parameters;
    }






}
