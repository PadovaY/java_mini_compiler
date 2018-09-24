package oop.ex6.Scope;

/**
 * Created by yoavabadi on 6/17/17.
 */
public class MethodNotExistsException extends Exception{

    private final static String ERROR_MESSAGE = "Error: Method not exists";
    public MethodNotExistsException(){

        super(ERROR_MESSAGE);
    }
}
