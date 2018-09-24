package oop.ex6.main;


import java.io.BufferedReader;
import java.io.File;
import oop.ex6.Scope.*;
import oop.ex6.Variable.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * this class is the parser of the program
 */
public class Parser {

    /*-- constants-- */
    private boolean isInMethodScope = false;
    public final static int LEGAL_CODE = 0;
    public final static int ILLEGAL_CODE = 1;
    public final static int IO_ERROR = 2;
    public final static String STRING = "String";
    public final static String INT = "int";
    public final static String DOUBLE = "double";
    public final static String BOOLEAN = "boolean";
    public final static String CHAR = "char";
    private final static String SCOPE_START = "{";
    private final static String SCOPE_END = "}";
    private final static String COMMENT_START = "//";
    private final static String SPACE = " ";
    private  final static String GLOBAL_SCOPE = "globalScope";
    private  final static String EMPTY_PARAM_LINE = "";
    private  final static String BAD_FORMAT_MSG = "Error: Bad Format";

    /*--magic numbers--*/
    private final static int TYPE_GROUP = 4;
    private final static int NAME_GROUP = 5;
    private final static int MODIFIER_GROUP = 3;
    private final static int VALUE_GROUP = 7;
    private final static int  INT_GROUP = 4;
    private final static int  DOUBLE_GROUP = 5;
    private final static int  BOOLEAN_GROUP = 3;
    private final static int  STRING_GROUP = 1;
    private final static int  CHAR_GROUP = 2;
    private final static int  PARAM_MODIFIER_GROUP = 4;
    private final static int  PARAM_TYPE_GROUP = 5;
    private final static int  PARAM_NAME_GROUP = 6;
    private final static int  METHOD_GROUP = 5;
    private final static int  WHILE_GROUP = 2;
    private final static int  IF_GROUP = 3;
    private final static int  METHOD_NAME_GROUP = 6;
    private final static int  PARAMETERS_GROUP = 7;
    private final static int  CONDITION_VAR_GROUP = 4;
    private final static int  RETURN_GROUP = 1;
    private final static int  OVERALL_VARIABLE_GROUP = 13;
    private final static int  MULTI_DECLARE_TYPE_GROUP = 4;
    private final static int  OTHER_VARIABLE_GROUP = 0;
    private final static int  SPLIT_PARAMETERS_GROUP = 2;
    private final static int  SPLIT_METHOD_NAME_GROUP = 1;

    public final static String VOID = "void";


    /*--regex--*/
    private final static String VARIABLE_BASE_FORMAT = "((\\s*(final)\\s+)?\\s*(int|String|double|char|" +
            "boolean)\\s+)?([a-zA-Z]+\\w*|[_]+\\w+)(\\s*=\\s*((\".*\")|(\'[\\x00-\\x7F]\')|(true|false)|-?" +
            "([0-9]+)|-?([0-9]*\\.?[0-9]*)|([a-zA-Z]+\\w*|[_]+\\w+)))?\\s*(;)\\s*";

    private final static String VARIABLE_FORMAT = "^" + VARIABLE_BASE_FORMAT + "$";

    private final static String SCOPE_FORMAT = "^\\s*((while)|(if))\\s*\\((.*)\\)\\s*\\{|\\s*(void)" +
                                                            "\\s+([a-zA-Z]\\w*)\\s*\\((.*)\\)\\s*\\{\\s*$";

    private final static String CALL_METHOD_FORMAT = "^\\s*([a-zA-Z]\\w*)\\s*\\((.*)\\)\\s*;$";

    private final static String PARAMETER_VALUE_FORMAT = "(\".*\")|('[\\x00-\\x7F]')|(true|false)" +
                                                                            "|-?([0-9]+)|-?([0-9]+\\.[0-9]+)";

    private final static Pattern CONDITION_VARIABLE = Pattern.compile("(\\s*((true|false)|([a-zA-Z]" +
                                                    "+\\w*|[_]+\\w+)|(-?[0-9]+\\.[0-9]+)|(-?[0-9]+))\\s*)");

    private final static String PARAMETER_FORMAT = "^(((\\s*(final)\\s+)?\\s*" +
                                        "(int|String|double|char|boolean)\\s+)([a-zA-Z]+\\w*|[_]+\\w+)\\s*)$";
    private final static String RETURN_FORMAT = "\\s*(return;)\\s*";


    /*--regex pattern--*/
    private final static Pattern VALIDATE_BASE_VARIABLE = Pattern.compile(VARIABLE_BASE_FORMAT);
    private final static Pattern VALIDATE_VARIABLE = Pattern.compile(VARIABLE_FORMAT);
    private final static Pattern VALIDATE_PARAMETER = Pattern.compile(PARAMETER_FORMAT);
    private final static Pattern WHITE_SPACES = Pattern.compile("^\\s*$");
    private final static Pattern CLOSING_FORMAT = Pattern.compile("^\\s*" + SCOPE_END + "\\s*$");
    private final static Pattern VALIDATE_SCOPE = Pattern.compile(SCOPE_FORMAT);
    private final static Pattern VALIDATE_CALL_METHOD = Pattern.compile(CALL_METHOD_FORMAT);
    private final static Pattern VALIDATE_METHOD_CALL_PARAMETERS = Pattern.compile(PARAMETER_VALUE_FORMAT);
    private final static Pattern VALIDATE_RETURN = Pattern.compile(RETURN_FORMAT);


    /*-- data members --*/
    private File codeFile;
    int currentLineNumber = 0;
    GlobalScope globalScope = new GlobalScope();
    Scope currentScope;
    Stack<Scope> scopeStack = new Stack<Scope>();
    boolean isGlobalVariablesSet = false; // for global variable creation
    boolean isGroupTypeDeclared = false;
    String groupType = "";


    /**
     * constructor
     * @param codeFile - the file of code.
     */
    public Parser(File codeFile)
    {
        this.codeFile = codeFile;
        this.currentScope = globalScope;
        this.scopeStack.push(globalScope);
    }


    /**
     * this method runs over the code and checks if it is valid
     * @return int - 0 if valid, 1 if invalid, 2 I/O exception
     * @throws IllegalVariableException - variable not supported
     * @throws IllegalLineException - line not legal
     * @throws IncompatibleTypesException - type and value not the same
     * @throws DuplicatedVariableNameException -  duplicated variable error
     * @throws UnclosedScopeException - scope not closed
     * @throws MethodNotExistsException - calling a method not created
     * @throws IllegalMethodParameterException - method parameters invalid
     * @throws IllegalAssignmentToFinalException - assign new value to a final variable
     */
    public int compileCode() throws IllegalVariableException, IllegalLineException,
            IncompatibleTypesException, DuplicatedVariableNameException, UnclosedScopeException,
                MethodNotExistsException, IllegalMethodParameterException, IllegalAssignmentToFinalException{

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(this.codeFile));
            String line;

            // if the brackets are unbalanced, an exception should be thrown.
            if(!checkBracketsBalance()){
                throw new UnclosedScopeException();
            }
            isGlobalVariablesSet = true;
            // parsing the code, when we know it's brackets are balanced
            while ((line = reader.readLine()) != null) {
                currentLineNumber++;
                // check empty line
                if(isEmptyLine(line))
                    continue;

                if(checkMethodCall(line)){       // check if method defined before method call
                    continue;
                }

                if(checkScopes(line)){           // check scopes
                    continue;
                }

                if(checkReturn(line)){           //check return

                    continue;
                }

                if(handleVariablesCreation(line)){  // check variables
                    continue;
                }

                // check close brackets
                Matcher closingFormat = CLOSING_FORMAT.matcher(line);

                // close the currentScope and assigning the next in the stack as the currentScope
                if(closingFormat.find()){
                    if(!currentScope.getClass().getName().equals(GLOBAL_SCOPE)){
                    scopeStack.pop();
                    currentScope = scopeStack.peek();
                    }
                }
                else{
                    throw new IllegalLineException();
                }
            }
        }
        catch (IOException e){
            System.err.println(e.getMessage());
            return IO_ERROR;
        }
        catch (IllegalLineException | DuplicatedVariableNameException | UnclosedScopeException e){
            System.err.println(e.getMessage());
            return ILLEGAL_CODE;
        }
        // all is good
        return LEGAL_CODE;
    }

    /**
     * this method manges the process of variable creation and assigning.
     * @param line - the line of the code
     * @throws IncompatibleTypesException - type and value incompatible
     * @throws IllegalVariableException -  variable not exist
     * @throws DuplicatedVariableNameException - variable already exist
     */
    private boolean handleVariablesCreation(String line)
            throws IncompatibleTypesException, IllegalVariableException,
            DuplicatedVariableNameException, IllegalAssignmentToFinalException {
        // two matcher -  first for a single declaration,  second for multiple declaration
        Matcher validateVariable = VALIDATE_VARIABLE.matcher(line);
        // replacing the comma with a semi-colon for entering this method recursively
        Matcher validateBaseVariable = VALIDATE_BASE_VARIABLE.matcher(line.replace(',', ';'));
        boolean matchFound = validateVariable.find(); // flag to identify multi or single declaration
        // if this isn't a single variable and we didn't find the group's type yet, enter recursion
        if(!isGroupTypeDeclared && !matchFound){
            return multiDeclaration(validateBaseVariable);
        }
        // single variable handling
        if(matchFound){
            String varType = validateVariable.group(TYPE_GROUP);
            String varName = validateVariable.group(NAME_GROUP);
            boolean isModifier = validateVariable.group(MODIFIER_GROUP) != null;
            String varValue = validateVariable.group(VALUE_GROUP);
            // check for existing declared variable
            if(validateVariable.group(OVERALL_VARIABLE_GROUP) != null){

                for(Variable var : this.currentScope.getScopeVariables()){
                    if(var.getName().equals(validateVariable.group(OVERALL_VARIABLE_GROUP))){
                        varValue = var.getValue();
                    }
                }
                // check if there is a matching  global variable
                for(Variable var : this.globalScope.getScopeVariables()){
                    if(var.getName().equals(validateVariable.group(OVERALL_VARIABLE_GROUP))){
                        if(var.getValue() == null){
                            return false;
                        }
                        varValue = var.getValue();
                    }
                }
            }
            // check if variable already been initialised
            if(varType == null){
                return isVariableDeclared(varName, varValue);
            }
            // check if the type isn't consisted with already initialised variable
            for(Variable var : this.currentScope.getScopeVariables()){
                if(var.getName().equals(varName) && !var.getType().equals(varType)){
                    return false;
                }
            }
            //for each scope in our code, find if this variable already exists
            for(Variable var : this.currentScope.getScopeVariables()){
                try {
                    // if current variable name matching a variable in current scope
                    if(var.getName().equals(varName)){
                        if(isGlobalVariablesSet && this.currentScope == this.globalScope) {
                            return true;
                        }else if(isGlobalVariablesSet && this.currentScope != this.globalScope &&
                                this.globalScope.getScopeVariablesNames().contains(varName) && var.getValue()
                                                                                                    == null){
                            // initializationException
                            return false;
                        }else{
                            throw new DuplicatedVariableNameException();
                        }
                    }
                }
                catch (DuplicatedVariableNameException e){
                    return false;
                }
                catch (NullPointerException e){
                    return false;
                }
            }
            Variable newVar = VariableFactory.variableGenerator(varType, varName, isModifier, varValue);
            // if the variable hasn't been created properly
            if(newVar == null){
                return false;
            }
            currentScope.setScopeVariable(newVar);
            return true;
        }
        return false;
    }



    /**
     * this method handles multi declaration in a line
     * @param validateBaseVariable - matcher to catch variables
     * @return true if legal, false otherwise
     * @throws IncompatibleTypesException - type and value incompatible
     * @throws IllegalVariableException -  variable not exist
     * @throws DuplicatedVariableNameException - variable already exist
     * @throws IllegalAssignmentToFinalException - assign new value to final violation
     */
    private boolean multiDeclaration(Matcher validateBaseVariable) throws IncompatibleTypesException,
                IllegalVariableException, DuplicatedVariableNameException, IllegalAssignmentToFinalException {

        // validate the multiple line declaration, by recursively go over each single variable declare
        boolean matchBaseFound = validateBaseVariable.find();

        // if  there is a valid type, saves and pass it recursively for each variable in line
        if(matchBaseFound && validateBaseVariable.group(MULTI_DECLARE_TYPE_GROUP) != null
                && !isGroupTypeDeclared){
            isGroupTypeDeclared = true;
            groupType = validateBaseVariable.group(MULTI_DECLARE_TYPE_GROUP) + " ";

            // recursive call this method the first time
            handleVariablesCreation(validateBaseVariable.group(OTHER_VARIABLE_GROUP));

            // in the second recursive call (if exists) and so on, we pass the type to the recursion
        }
        if(isGroupTypeDeclared){
            while (validateBaseVariable.find()) {
                String newLine = groupType + validateBaseVariable.group(OTHER_VARIABLE_GROUP);
                // catch a validation problems before recursion
                Matcher validateVariableCheck = VALIDATE_VARIABLE.matcher(newLine);
                if(validateVariableCheck.find() && validateVariableCheck.group(MULTI_DECLARE_TYPE_GROUP)
                        != null){
                    // recursive call this method - mimic a single line
                    handleVariablesCreation(groupType + validateBaseVariable.group(OTHER_VARIABLE_GROUP));
                }
                // format's not valid
                else {
                    return false;
                }
            }
            // success - the multiple variables been added successfully!
            return true;
        }
        // this line is not a variable line - continue with parser
        return false;
    }



    /**
     * checks if a variable has been declared
     * @param varName - name of variable
     * @param varValue - value of variable
     * @return - true if legal assign, false otherwise
     */
    private boolean isVariableDeclared(String varName, String varValue){
        //runs over the variables in the scopes
        for(Scope scope : scopeStack){
            for(Variable var : scope.getScopeVariables()){
              if(var.getName().equals(varName) && !var.getIsModifier()){
                //found the variable
                if(this.currentScope != this.globalScope && this.globalScope.
                        getScopeVariablesNames().contains(varName)  && var.getValue() == null){
                    continue;
                }
                var.setValue(varValue);         //assign new value
                return true;
              }
            }
        }
        return false;
    }







    /**
     * checks if the line is empty or a comment
     * @param line - line of code
     * @return true if empty or comment, false otherwise
     */
    private boolean isEmptyLine(String line){
        Matcher whiteSpaces = WHITE_SPACES.matcher(line);
        //check for empty lines or comments - which are ignored
        return (line.isEmpty() || whiteSpaces.find() || line.startsWith(COMMENT_START));
    }


    /**
     * checks if the scope contains matching open and close brackets
     * and gets the  global methods and variables of the whole file
     * @return true if they are equal, false otherwise
     * @throws IOException
     */
    private boolean checkBracketsBalance() throws IOException, IllegalAssignmentToFinalException,
            IncompatibleTypesException, DuplicatedVariableNameException, IllegalVariableException{
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(this.codeFile));
        String line;
        Stack<String> bracketsStack = new Stack<String>();

        //goes iver file
        while ((line = reader.readLine()) != null) {
            Matcher openCheck = VALIDATE_SCOPE.matcher(line);
            Matcher closeCheck = CLOSING_FORMAT.matcher(line);

            if(openCheck.find()){           //found scope open, counts it and adds method to roaster
                bracketsStack.push(SCOPE_START);
                if(!methodValidation(line)){
                    return false;
                }
            }
            else if(closeCheck.find()){  //found scope close, reduce it and adds method to roaster
                if (bracketsStack.isEmpty()){
                    return false;
                }
                bracketsStack.pop();
            }
            //if one of the brackets is in middle of a line, not leagal false
            else if(line.contains(SCOPE_START) || line.contains(SCOPE_END)){
                return false;
            }
            // handles global variables (if the scope is global, the stack is empty):
            else if(bracketsStack.isEmpty()){
                Matcher validateVariable = VALIDATE_VARIABLE.matcher(line);
                if(validateVariable.find() && !handleVariablesCreation(line)){
                    return false;
                }
            }
        }
        return (bracketsStack.isEmpty());
    }



    /**
     * checks if a method call is valid
     * @param line - line of code
     * @return true if valid, false otherwise
     * @throws IllegalMethodParameterException method call is not legal
     */
    private boolean checkMethodCall(String line) throws  IllegalMethodParameterException {
        //pattern of a method call
        Matcher validateMethodCall = VALIDATE_CALL_METHOD.matcher(line);
        if (validateMethodCall.find()) {
            //goes over methods in the file and checks if the method exist
            for(Method method : this.globalScope.methods){
                if(method.getName().equals(validateMethodCall.group(SPLIT_METHOD_NAME_GROUP))){
                    //checks the parameters in tha call compatible with the declared ones
                    String[] parametersArray = validateMethodCall.group
                            (SPLIT_PARAMETERS_GROUP).split("\\s*,\\s*");
                    if(parametersCompatibility(method, parametersArray)){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * checks if parameters in a method call are compatible
     * @param method - the method
     * @param parametersArray - the parameters in the method call
     * @return true - param are compatible, false otherwise
     */
    private boolean parametersCompatibility(Method method, String[] parametersArray) {
        // no parameters
        if(parametersArray[0].equals("") && parametersArray.length ==1 && method.getParameters().size() == 0){
            return true;
        }
        //the number of param is too high or too low
        if(method.getParameters().size() != parametersArray.length){
            return false;
        }
        //goes over the parameters amd check they are the right type
        for (int i = 0; i < method.getParameters().size(); i++) {
            Matcher checkParameter = VALIDATE_METHOD_CALL_PARAMETERS.matcher(parametersArray[i]);
            try {
                //tries to validate type and value in method call
                if(checkParameter.find()) {
                    switch (method.getParameters().get(i).getType()) {
                        case INT:
                            if(checkParameter.group(INT_GROUP) != null)
                                continue;
                        case STRING:
                            if (checkParameter.group(STRING_GROUP) != null)
                                continue;
                        case BOOLEAN:
                            if (checkParameter.group(BOOLEAN_GROUP) != null)
                                continue;
                        case CHAR:
                            if (checkParameter.group(CHAR_GROUP) != null)
                                continue;
                        case DOUBLE:
                            if (checkParameter.group(DOUBLE_GROUP) != null)
                                continue;
                        default:
                            throw new IllegalMethodParameterException();
                    }
                }
            } catch (IllegalMethodParameterException e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return true;
    }


    /**
     * handles the initialization of a variable, if it had already been
     *  initialized or not.
     * @param varName - the name of the variable
     * @return - true if legal initialization, false otherwise
     */
    private boolean checkVariableInitialization(String varName){
        //checks in every scope
        for (Scope scope : scopeStack) {
            for(Variable var : scope.getScopeVariables()){
                if(var.getName().equals(varName) && var.getValue() != null && var.getType().equals(BOOLEAN)){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     *verifies that a condition in a condition scope os legal
     * @param condition - String to check
     * @return - true if valid, false otherwise.
     */
    private boolean validateCondition(String condition){
        String alterCondition = SPACE + condition  + SPACE;  // synchronize statement
        String[] variables = alterCondition.split("((\\|\\|)|(&&))");
        for(String var : variables){
            Matcher conditionVarMatch = CONDITION_VARIABLE.matcher(var);
            if(!conditionVarMatch.find()) {                 //condition didn't match, invalid
                System.err.println(BAD_FORMAT_MSG);
                return false;
            }
            //condition with vars not declared, invalid
            else if(conditionVarMatch.group(CONDITION_VAR_GROUP) != null &&
                    !(checkVariableInitialization(conditionVarMatch.group(CONDITION_VAR_GROUP).trim()))) {
                System.err.println("Error: This local variable does not exists");
                return false;
            }
        }
        return true;
    }

    /**
     * this method handles the extraction of the parameters
     * @param parameterLine - the parameters in the line
     * @return - list of parameters
     * @throws IllegalAssignmentToFinalException - assigning to a final variable
     */
    private ArrayList<Variable> handleParameters(String parameterLine) throws
                                                                            IllegalAssignmentToFinalException{

        ArrayList<Variable> parameters = new ArrayList<Variable>();
        String alterParameterLine = SPACE + parameterLine  + SPACE;  //arrange reference
        String[] paramsArray = alterParameterLine.split(",");   //buffer is a comma
        for(String param : paramsArray){
            Matcher paramMatch = VALIDATE_PARAMETER.matcher(param); //no parameters were find
            if(!paramMatch.find()&& !(parameterLine.equals(EMPTY_PARAM_LINE)) ) {
                System.err.println(BAD_FORMAT_MSG);
            }else if(parameterLine.equals(EMPTY_PARAM_LINE)){
                continue;
            }
            else {
                try {       //gets the parameter details
                    boolean isModifier = paramMatch.group(PARAM_MODIFIER_GROUP) != null;
                    String varType = paramMatch.group(PARAM_TYPE_GROUP);
                    String varName = paramMatch.group(PARAM_NAME_GROUP);
                    Variable newVar = VariableFactory.variableGenerator(varType, varName, isModifier, null);
                    // check for duplicate variable names:
                    for (Variable var : parameters){
                        if(var.getName().equals(newVar.getName())){
                            return null;
                        }
                    }
                    parameters.add(newVar);
                }catch (IllegalVariableException e){
                    return null;
                }
                catch (IncompatibleTypesException e){
                    return null;
                }catch (IllegalAssignmentToFinalException e){
                    return null;
                }

            }
        }
        return parameters;
    }



    /**
     * verifies the legality of a method code line
     * @param line - line to check
     * @return true if valid, false otherwise
     * @throws IllegalAssignmentToFinalException - assigning to a final variable
     */
    private boolean methodValidation(String line) throws IllegalAssignmentToFinalException{
        Matcher validateScope = VALIDATE_SCOPE.matcher(line);
        // check for scopes
        if(validateScope.find() && validateScope.group(METHOD_GROUP) != null){ //  line represent a method
            // handle "method" creation
            try {
                ArrayList<Variable> parameters = handleParameters(validateScope.group(PARAMETERS_GROUP));
                if(parameters == null){
                    return false;
                }
                // invalid format
                if(validateScope.group(PARAMETERS_GROUP).length() > 0 && parameters.size() == 0){
                    return false;
                }
                // check if one of the parameters is null
                for(Variable var : parameters){
                    if(var == null){
                        return false;
                    }
                }
                this.globalScope.methods.add(MethodFactory.methodGenerator(VOID, validateScope.group
                                                                            (METHOD_NAME_GROUP), parameters));
                return true;
            }catch (IllegalAssignmentToFinalException e){
                return false;
            }
        }
        return true;
    }


    /**
     * validates scopes are legal
     * @param line - the line to check
     * @return true if scope legal, false otherwise
     * @throws IllegalAssignmentToFinalException - assigning to a final variable
     */
    private boolean checkScopes(String line) throws IllegalAssignmentToFinalException{
        Matcher validateScope = VALIDATE_SCOPE.matcher(line);
        // check for scopes
        if(validateScope.find()){
            if(validateScope.group(METHOD_GROUP) != null){ // then the line represent a method
                // handle "method" creation
                for(Method method : this.globalScope.methods){
                    if(method.getName().equals(validateScope.group(METHOD_NAME_GROUP))){
                        this.isInMethodScope = true;
                        this.currentScope = method;
                        this.scopeStack.push(currentScope);
                        return true;
                    }
                }
                return false;
            }
            else if(validateScope.group(WHILE_GROUP) != null && validateCondition(validateScope.group
                                                                                    (PARAM_MODIFIER_GROUP))){
                // handle "while" scope creation

                this.currentScope = new WhileLoop();
                this.scopeStack.push(currentScope);
            }
            else if(validateScope.group(IF_GROUP) != null && validateCondition(validateScope.group
                                                                                    (PARAM_MODIFIER_GROUP))){
                // handle "if" scope creation
                this.currentScope = new IfCondition();
                this.scopeStack.push(currentScope);
                //some condition stuff
            }
            else{
                // throw some exception
                return false;
            }
            return true;

        }
        return false;
    }


    /**
     * the method checks there is a return in the end of a scope
     * @param line - line to check
     * @return true if there is, false otherwise
     */
    private boolean checkReturn(String line){
        Matcher returnMatcher = VALIDATE_RETURN.matcher(line);

        //validates return in a method scope
        if(returnMatcher.find() && returnMatcher.group(RETURN_GROUP) != null && this.isInMethodScope){
            return true;
        }
        return false;
    }

}
