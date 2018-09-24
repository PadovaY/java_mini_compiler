package oop.ex6.Scope;

import oop.ex6.Variable.Variable;
import java.util.ArrayList;

/**
 * this class represents a Scope
 */
public class Scope {

    /*--data members--*/
    private ArrayList<Scope> innerScopes;
    private ArrayList<Variable> scopeVariables;
    private ArrayList<Method> scopeMethods;

    /**
     * default constructor
     */
    public Scope(){
        this.innerScopes = new ArrayList<Scope>();
        this.scopeVariables = new ArrayList<Variable>();
        this.scopeMethods = new ArrayList<Method>();
    }

    /**
     *  constructor with parameters of scope
     * @param scopeVariables - list of variables in scope
     */
    public Scope(ArrayList<Variable> scopeVariables){
        this.innerScopes = new ArrayList<Scope>();
        this.scopeVariables = scopeVariables;
        this.scopeMethods = new ArrayList<Method>();
    }


    /**
     * the method gets the variables of the scope
     * @return - list of tha variables of the scope
     */
    public ArrayList<Variable> getScopeVariables(){
        return this.scopeVariables;
    }

    /**
     * this method gets the names of the variables in the scope
     * @return - list of strings of the variables in scope
     */
    public ArrayList<String> getScopeVariablesNames(){
        ArrayList<String> varNames = new ArrayList<String>();
        for(Variable var : this.scopeVariables){
            varNames.add(var.getName());
        }
        return varNames;
    }


    /**
     * this method sets the variables of the scope
     * @param variable - the variable to set
     */
    public void setScopeVariable(Variable variable){
        this.scopeVariables.add(variable);
    }

}
