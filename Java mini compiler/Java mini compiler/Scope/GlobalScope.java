package oop.ex6.Scope;


import oop.ex6.Variable.Variable;

import java.util.ArrayList;



/**
 * This class represents a global scope of the code.
 */
public class GlobalScope extends Scope {

    /*--data members--*/
    public ArrayList<Method> methods = new ArrayList<Method>();
    private ArrayList<Variable> variables = new ArrayList<Variable>();

    /**
     * constructor
     */
    public GlobalScope(){
        super();
    }





}
