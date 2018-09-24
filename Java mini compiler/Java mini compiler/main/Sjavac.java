package oop.ex6.main;

import java.io.File;

/**
 * this class complies the code file
 */
public class Sjavac {

    /**
     * the main method. prints 0 if code legal, 1 if illegal, 0 if I/O exception
     * @param args - input of directory and file
     * @throws Exception - in case of illegal actions
     */
    public static void main(String[] args) throws Exception {
        Parser parser = new Parser(new File(args[0]));
        int result = parser.compileCode();
        System.out.println(result);

    }


}
