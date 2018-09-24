package oop.ex6.Scope;

import oop.ex6.Variable.Variable;

import java.util.ArrayList;

/**
 * this class represents a method of the type void
 */
public class VoidMethod extends Method{

    /*--constants--*/
    private static final String VOID = "void";

    /**
     * constructor
     * @param name - name of the method
     * @param parameters = parameters of the method
     */
    public VoidMethod(String name, ArrayList<Variable> parameters){

        super(name, VOID, parameters);
    }
}
