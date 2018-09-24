package oop.ex6.Scope;
import oop.ex6.Variable.*;
import java.util.ArrayList;

public class MethodFactory {

    private static final String VOID = "void";

    /**
     * This method generates a new method, based on its type.
     * @param methodType the method's return type
     * @param name method's name
     * @param parameters method's parameters
     * @return a new method instance
     */
    public static Method methodGenerator(String methodType, String name, ArrayList<Variable> parameters){
        try {
            switch (methodType){
                case VOID:
                    return new VoidMethod(name, parameters);
                default:
                    throw new IllegalLineException();
            }
        }catch (IllegalLineException e){
            return null;
        }
    }
}
