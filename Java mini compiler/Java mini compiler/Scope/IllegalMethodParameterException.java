package oop.ex6.Scope;

/**
 * Created by yoavabadi on 6/16/17.
 */
public class IllegalMethodParameterException extends Exception {
    private final static String ERROR_MESSAGE = "Error: parameters are not compatible";

    public IllegalMethodParameterException() {

        super(ERROR_MESSAGE);
    }

}