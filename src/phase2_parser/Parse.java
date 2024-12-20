package phase2_parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/*************************************************************
 * Group 16 - Parser
 * 
 * Authors: 
 *      Parse.java: Andrew DeGarmo, Greyson Meares, Michael Ames
 *      ParseTest.java (Junit tests for different inputs): Greyson Meares
 * Reviewers: 
 *      Luke Hare, Quinn McAuliffe, Eric Ristey
 * 
 * CS410 - Compiler Construction
 * 10/31/2024
 * 
 * Functionality:
 *      interprets int array of terminals & string array of tokens from Scanner
 *      and builds a list of decaf instructions accordingly
 * 
 ************************************************************/

public class Parse{
    private int[] terminals;
    private String[] tokens;
    private int index;
    private int length;
    private List<String> decafAtoms;
    private List<String> mathOps;
    private int lbl;
    private int tempDest;
    private String dest;
    private int flag;// 1 => Assignment, 0 => Bool
    private boolean optimize;

    //Using VS CODE:
    //Compile the files:  javac -d bin src/phase2_parser/Parse.java 
    //Run the program:  java -cp bin phase2_parser.Parse

    private void printAtoms(List<String> instructions) {
        for(String atom : instructions) {
            System.out.println(atom);
        }
    }

    public void printAtoms() {
        for(String atom : decafAtoms) {
            System.out.println(atom);
        }
    }

    public List<String> getAtoms() {
        if(optimize)
            optimizeAtoms();

        return decafAtoms;
    }

    private void createLBL(int lblNumber){
        decafAtoms.add(new String ("(" + "LBL, " + ", " + ", " + ", " + ", " + "L"+lblNumber + ")"));
    }

    private void createJMP(int lblNumber){
        decafAtoms.add(new String ("(" + "JMP, " + ", " + ", " + ", " + ", " + "L"+lblNumber + ")"));
    }

    private void createTST(int lblNumber, String left, String right, int cmp){
        decafAtoms.add(new String ("(" + "TST, " + left+", " + right+", " + ", " + Integer.toString(cmp)+", " + "L"+lblNumber + ")"));
    }

    private void createMOV(String token, String a, String dest){
        decafAtoms.add(new String ("(" + "MOV, " + a+", " + token+", " + dest + ")"));
    }

    private void createADD(String a, String b, String dest){
        if("".equals(dest)  || dest == null){
            tempDest++;
            decafAtoms.add(new String ("(" + "ADD, " + a+", " + b+", " + "t"+tempDest + ")"));
        }
        else{
            decafAtoms.add(new String ("(" + "ADD, " + a+", " + b+", " + dest + ")"));
        }
    }

    private void createSUB(String a, String b, String dest){
        if("".equals(dest) || dest == null){
            tempDest++;
            decafAtoms.add(new String ("(" + "SUB, " + a+", " + b+", " + "t"+tempDest + ")"));
        }
        else{
            decafAtoms.add(new String ("(" + "SUB, " + a+", " + b+", " + dest + ")"));
        }
    }

    private void createMUL(String a, String b, String dest){
        if("".equals(dest) || dest == null){
            tempDest++;
            decafAtoms.add(new String ("(" + "MUL, " + a+", " + b+", " + "t"+tempDest + ")"));
        }
        else{
            decafAtoms.add(new String ("(" + "MUL, " + a+", " + b+", " + dest + ")"));
        }
    }

    private void createDIV(String a, String b, String dest){
        if("".equals(dest) || dest == null){
            tempDest++;
            decafAtoms.add(new String ("(" + "DIV, " + a+", " + b+", " + "t"+tempDest + ")"));
        }
        else{
            decafAtoms.add(new String ("(" + "DIV, " + a+", " + b+", " + dest + ")"));
        }
    }

    /**
     * Converts an infix expression to postfix notation. The expression is
     * tokenized and then processed in order from left to right. When a
     * digit is encountered, it is appended to the result string. When an
     * operator is encountered, it is pushed onto the stack. When a
     * parenthesis is encountered, the operators on the stack are popped
     * and appended to the result string until the parenthesis is
     * encountered. When the end of the expression is reached, any remaining
     * operators are popped and appended to the result string. The result
     * string is then evaluated using the postfix evaluation algorithm.
     * @param expression the expression to convert to postfix
     */
    private void infixToPostfix(List<String> expression) {
        StringBuilder result = new StringBuilder();
        Stack<String> stack = new Stack<>();
        
        for(String token : expression) {
            //Check for digits
            if(!token.equals("+") && !token.equals("-") && !token.equals("*") && !token.equals("/") && !token.equals("(") && !token.equals(")")){
                //append digit results
                result.append(token).append(" ");
            } else if (token.equals("(")) { 
                stack.push(token);
            } else if (token.equals(")")) {
                //pop all operators from the stack until ( is encountered
                while(!stack.isEmpty() && !stack.peek().equals("(")) {
                    result.append(stack.pop()).append(" ");
                }
                //pop the open parenthesis
                stack.pop();
            } else {
                //make sure operator has a higher precedence than the top of the stack
                while(!stack.isEmpty() && precedence(token) <= precedence(stack.peek())) {
                    //Otherwise, pop the operator onto the stack and append to result
                    result.append(stack.pop()).append(" ");
                }
                //Push the new operator onto the stack
                stack.push(token);
            }
        }

        while(!stack.isEmpty()) {
            result.append(stack.pop()).append(" ");
        }

        evalPostfix(result.toString().split(" "));
    }

    /**
     * Returns the precedence of the given operator. Higher values indicate a
     * higher precedence.
     * 
     * @param token
     *            the operator whose precedence is to be determined
     * @return the precedence of the given operator
     */
    private static int precedence(String token) {
        switch(token) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return -1;
        }
    }

    /**
     * Evaluates a postfix expression and builds a list of decaf instructions.
     * The expression is given as an array of strings, where each string is 
     * either an operand or an operator. Operands are pushed onto a stack, and 
     * operators pop operands from the stack to perform the operation and 
     * generate corresponding decaf instructions.
     *
     * @param expression the postfix expression to evaluate
     */
    private void evalPostfix(String[] expression) {
        Stack<String> stack = new Stack<>();

        //System.out.println("SIZE: " + expression.length);
        //System.out.println("POST: " + Arrays.toString(expression));
        for(String token : expression) {
            //System.out.println("TOKEN: " + token);
            if(!token.equals("+") && !token.equals("-") && !token.equals("*") && !token.equals("/")) {
                stack.push(token);
            } else if(stack.size() > 1) {
                String b = stack.pop();
                String a = stack.pop();
                //System.out.println("AB: " + a + "," + b);
                if(token.equals("+")) {
                    //Make atoms
                    createADD(a, b, "t"+(++tempDest));
                    //push temp var on stack
                    stack.push("t"+tempDest);
                } else if(token.equals("-")) {
                    //Make atoms
                    createSUB(a, b, "t"+(++tempDest));
                    //push temp var on stack
                    stack.push("t"+tempDest);
                } else if(token.equals("*")) {
                    //Make atoms
                    createMUL(a, b, "t"+(++tempDest));
                    //push temp var on stack
                    stack.push("t"+tempDest);
                } else if(token.equals("/")) {
                    //Make atoms
                    createDIV(a, b, "t"+(++tempDest));
                    //push temp var on stack
                    stack.push("t"+tempDest);
                }
            } else {
                //At the end of the expression and there is one more operand in stack, move into the original destination
                String a = stack.pop();
                //System.out.println("AX: " + a);
                //Add decaf
                createMOV("", a, dest);
            }
        }

        //Account for operations like "int x = 10;"
        if(!stack.isEmpty() && flag == 1) {
            String a = stack.pop();
            //System.out.println("A: " + a);
            //Add decaf
            createMOV("", a, dest);
        }
    }

    /**
     * Determines if the token at the specified index is an identifier.
     *
     * @param index the index of the token to check
     * @return true if the token is an identifier, false otherwise
     */
    private boolean isIdentifier(int index) {
        List<Integer> IDENTIFIER_TOKENS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 39);

        if (index >= length) {
            return false;
        }
        return IDENTIFIER_TOKENS.contains(terminals[index]);
    }

    /**
     * Determines if the token at the specified index is an operator.
     * 
     * @param index the index of the token to check
     * @return the corresponding integer value of the operator if the token 
     *         is an operator, otherwise returns -1
     */
    private int isOperator(int index) {
        //final List<Integer> OPERATOR_TOKENS = Arrays.asList(20, 21, 22, 23);
        if (index >= length) {
            return -1;
        }

        switch (terminals[index]) {
            case 20:
                return 0;//+
            case 21:
                return 1;//-
            case 22:
                return 2;//*
            case 23:
                return 3;///
            default:
                return -1;
        }
        //return OPERATOR_TOKENS.contains(terminals[index]);
    }

   
    /**
     * Determines if the token at the specified index is a comparison operator.
     * 
     * @param index the index of the token to check
     * @return the corresponding integer value of the comparison operator if the token
     *         is a comparison operator, otherwise returns -1
     */
    private int isComparisonOperator(int index) {
        //final List<Integer> COMPARISON_OPERATOR_TOKENS = Arrays.asList(25, 26, 27, 28, 29, 31);
        if (index >= length) {
            return -1;
        }
        //System.err.println("COMPARISON_OPERATOR_TOKENS: " + terminals[index]);

        // == : 1, < : 2, > : 3, <= : 4, >= : 5, != : 6
        switch (terminals[index]) {
            case 25:        //NOT_EQUALS_OP
                return 1;   
            case 26:        //LT_OP
                return 5;   
            case 27:        //LT_ET_OP
                return 3;
            case 28:        //GT_OP
                return 4;
            case 29:        //GT_ET_OP
                return 2;
            case 31:        //EQUALS_OP
                return 6;
            default:
                return -1;
        }
        //return COMPARISON_OPERATOR_TOKENS.contains(terminals[index]);
    }


    public Parse(int[] terminals, String[] tokens, int length, boolean optimize) {
        this.terminals = terminals;
        this.index = 0;
        this.length = length;
        this.decafAtoms = new ArrayList<>();
        this.lbl = -1;
        this.tempDest = -1;
        this.dest = "";
        this.mathOps = new ArrayList<>();
        this.tokens = tokens;
        this.flag = 0;
        this.optimize = optimize;
    }
    public Parse() {
        this.terminals = new int[0];
        this.index = 0;
    }

    public void setTerminals(int[] terminals, String[] tokens) {
        this.terminals = terminals;
        this.index = 0;
        this.length = terminals.length;
        this.decafAtoms = new ArrayList<>();
        this.mathOps = new ArrayList<>();
        this.tokens = tokens;
        this.lbl = -1;
        this.tempDest = -1;
        this.dest = "";
    }

    /**
     * Returns true if the current token matches the given terminal, and then
     * advances the current token index. If the current token does not match the
     * given terminal, or if we have reached the end of the input, false is
     * returned and the current token index is left unchanged.
     * 
     * @param terminal the terminal to match
     * @return true if the current token matches the given terminal, false
     *         otherwise
     */
    public boolean accept(int terminal) {
        //If end of input
        if (index >= length) {
            return false;
        }
        //If terminal matches
        if(terminal == terminals[index]) {
            index++;
            return true;
        }
        //mismatch
        return false;
    }

    /**
     * Advances the current token index if the current token matches the given
     * terminal. If the current token does not match the given terminal, or if
     * we have reached the end of the input, "REJECT" is returned.
     * 
     * @param terminal the terminal to match
     */
    public int expect(int terminal) {
        //If end of input
        if (index >= length) {
           return -1;
        }
        //If terminal matches
        if(terminal == terminals[index]) {
            index++;
            return 0;
        }
        //mismatch
        return -1;
    }

    /**
     * Returns true if the current token matches the given terminal, and does
     * not advance the current token index. If the current token does not match
     * the given terminal, or if we have reached the end of the input, false is
     * returned and the current token index is left unchanged.
     * 
     * @param terminal the terminal to match
     * @return true if the current token matches the given terminal, false
     *         otherwise
     */
    public boolean peak(int terminal) {
        //If end of input
        if (index >= length) {
            return false;
        }
        //If terminal matches
        return (terminal == terminals[index]);
    }
    
    /**
     * Parses a Statement according to the following grammar rules:
     * Statement → if ( Bool ) { Statement }
     * Statement → if ( Bool ) { Statement } Else 
     * Statement → if ( Bool ) { Statement } Else-if
     * Statement → for (Assignment ; Bool ; Expr) { Statement }
     * Statement → while ( Bool ) { Statement }
     * Statement → Assignment
     * If the input matches the grammar rules, returns "ACCEPT", otherwise
     * returns "REJECT".
     * 
     * @return "ACCEPT" if the input matches the grammar rules, "REJECT" otherwise
     */
    public String Statement() {
        //IF CASE
        if (accept(34)) {
            if (If().equals("REJECT")) {
                //Empty atoms
                decafAtoms.clear();
                return "REJECT";
            }
        }
        if (index >= length) {
            return "ACCEPT";
        }
        else if(peak(17)){
            return "ACCEPT";
        }//}

        //FOR CASE
        if (accept(32)) {
            if (For().equals("REJECT")) {
                //Empty atoms
                decafAtoms.clear();
                return "REJECT";
            }
        }
        if (index >= length) {
            return "ACCEPT";
        }
        else if(peak(17)){
            return "ACCEPT";
        }//}

        //WHILE CASE
        if(accept(33)){
            if (While().equals("REJECT")) {
                //Empty atoms
                decafAtoms.clear();
                return "REJECT";
            }
        }
        if (index >= length) {
            return "ACCEPT";
        }
        else if(peak(17)){
            return "ACCEPT";
        }//}

        //ASSIGNMENT CASE
        //int_type or float_type or identifier
        if(peak(37) || peak(38) || isIdentifier(index)){
            //accept(terminals[index]);      //if token is an identifier or number type, accept it
            System.out.println();
            if (Assignment().equals("REJECT")) {
                //Empty atoms
                decafAtoms.clear();
                return "REJECT";
            }
            if(expect(43) == -1){
                return "REJECT";
            }//;
            if(peak(17)){
                return "ACCEPT";
            }//}
            else{
                return Statement();
            }
        }
        if (index >= length) {
            return "ACCEPT";
        }
        else if(peak(17)){
            return "ACCEPT";
        }//}

        //Empty atoms
        decafAtoms.clear();
        return "REJECT";    //not end of input, but no valid statement
        
    }

    /**
     * Parses a While according to the following grammar rules:
     * While → while ( Bool ) { Statement }
     * If the input matches the grammar rules, returns "ACCEPT", otherwise
     * returns "REJECT".
     * 
     * @return "ACCEPT" if the input matches the grammar rules, "REJECT" otherwise
     */
    private String While(){
        //Add decaf
        lbl++;
        int lblNumber = lbl;
        createLBL(lblNumber);
        if(expect(18) == -1){
                return "REJECT";
        }//(

        lbl++;
        int lblNumberJMP = lbl;
        Bool(lblNumberJMP);        //ex: i < 10
        if(expect(19) == -1){
            return "REJECT";
        }//)
        
        if(expect(16) == -1){
            return "REJECT";
        }//{

        if(Statement().equals("REJECT")){
            return "REJECT";
        }
        
        if(expect(17) == -1){
            return "REJECT";
        }  //}

        //Add decaf
        createJMP(lblNumber);
       
        //Add decaf
        createLBL(lblNumberJMP);

        if(peak(17)){
            return "ACCEPT";
        }//}

        if(index < length){
            return Statement();
        }

        return "ACCEPT";
    }

    /**
     * Parses a For according to the following grammar rules:
     * For → for ( Assignment ; Bool ; Assignment ) { Statement }
     * If the input matches the grammar rules, returns "ACCEPT", otherwise
     * returns "REJECT".
     * 
     * @return "ACCEPT" if the input matches the grammar rules, "REJECT" otherwise
     */
    private String For(){
        
        String res;
        
        if(expect(18) == -1){
            return "REJECT";
        }  //(

        res = Assignment();     //ex: i = 0
        if(expect(43) == -1 || res.equals("REJECT")){
            return "REJECT";
        } //;

        lbl++;
        int lblNumber = lbl;
        createLBL(lblNumber);

        lbl++;
        int lblNumberJMP = lbl;
        res = Bool(lblNumberJMP);         //ex: i < 10
        if(expect(43) == -1 || res.equals("REJECT")){
            return "REJECT";
        } //;

        //grab input for i = 1 + 1;
        //String[] input = new String[];
        //while(!tokens[index].equals(")")){
        //    System.out.println("TOKEN: " + tokens[index]);
        //}

        res = Assignment();     //ex: i = i + 1
        if(expect(19) == -1 || res.equals("REJECT")){
            return "REJECT";
        } //)

        if(expect(16) == -1){
            return "REJECT";
        }  //{
        
        if(Statement().equals("REJECT")){
            return "REJECT";
        }
        
        if(expect(17) == -1){
            return "REJECT";
        }  //}

        //Move add and Mov to the end of the atoms list
        int trackIndex = decafAtoms.size()-1;
        System.out.println("LABEL NUMBER: " + lblNumber);
        while(trackIndex >= 1){
            if(decafAtoms.get(trackIndex).contains("TST") && decafAtoms.get(trackIndex).contains("L"+(lblNumber+1))){
                //Found, Move to end
                System.out.println("ATOM: " + decafAtoms.get(trackIndex+1));
                System.out.println("ATOM: " + decafAtoms.get(trackIndex+2));
                if(decafAtoms.get(trackIndex+1).contains("ADD") && decafAtoms.get(trackIndex+2).contains("MOV")){
                    System.out.println("MOVING___");
                    String add = decafAtoms.remove(trackIndex+1);
                    String mov = decafAtoms.remove(trackIndex+1);
                    decafAtoms.add(add);
                    decafAtoms.add(mov);
                }               
                
                
            }
            trackIndex--;
        }

        //Add decaf
        createJMP(lblNumber);
       
        //Add decaf
        createLBL(lblNumberJMP);

        if(peak(17)){
            return "ACCEPT";
        }//}

        if(index < length){
            return Statement();
        }

        return "ACCEPT";
    }

    /**
     * Parses an If according to the following grammar rules:
     * If → if ( Bool ) { Statement }
     * If → if ( Bool ) { Statement } Else
     * If → if ( Bool ) { Statement } Else-if
     * If the input matches the grammar rules, returns "ACCEPT", otherwise
     * returns "REJECT".
     * 
     * @return "ACCEPT" if the input matches the grammar rules, "REJECT" otherwise
     */
    private String If(){
        if(expect(18) == -1){
            return "REJECT";
        }        //(

        lbl++;
        int lblNumber = lbl;
        if (Bool(lblNumber).equals("REJECT")) {
            return "REJECT";
        }

        if(expect(19) == -1){
            return "REJECT";
        } //)
        if(expect(16) == -1){
            return "REJECT";
        } //{
        
        if(Statement().equals("REJECT")){
            return "REJECT";
        }

        if(expect(17) == -1){
            return "REJECT";
        }  //}
        
        //Need to create jmp within if block in order to skip elseif/else in the case if is true (basically mimics an if elseif else program)
 



        //ELSE-IF CASE
        if(accept(36)){
            //Add decaf
            lbl++;
            int lblNumberJMP = lbl;
            createJMP(lblNumberJMP);
            createLBL(lblNumber);
            
            if(ElseIf().equals("REJECT")){
                return "REJECT";
            }
            createLBL(lblNumberJMP);
        }
        //ELSE CASE
        else if(accept(35)){
            //Add decaf
            lbl++;
            int lblNumberJMP = lbl;
            createJMP(lblNumberJMP);
            createLBL(lblNumber);
            
            if(Else().equals("REJECT")){
                return "REJECT";
            }
            createLBL(lblNumberJMP);
        }
        else{
            //Add decaf
            createLBL(lblNumber);
        }
        

        if(peak(17)){
            return "ACCEPT";
        }//}

        if(index < length){
            return Statement();
        }

        return "ACCEPT";
    }

    /**
     * Parses an Else according to the following grammar rules:
     * Else → else { Statement  }
     * If the input matches the grammar rules, returns "ACCEPT", otherwise
     * returns "REJECT".
     * 
     * @return "ACCEPT" if the input matches the grammar rules, "REJECT" otherwise
     */
    public String Else() {
        //{
        if(expect(16) == -1){
            return "REJECT";
        }//{
        
        if(Statement().equals("REJECT")){
            return "REJECT";
        }

        if(expect(17) == -1){
            return "REJECT";
        }//}

        if(peak(17)){
            return "ACCEPT";
        }//}

        //No decaf for else because there is no TST to compliment it
        return "ACCEPT";
    }
    
    /**
     * Parses an Else-if according to the following grammar rules:
     * Else-if → else if ( Bool ) { Statement  }
     * Else-if → else if ( Bool ) { Statement  } Else
     * If the input matches the grammar rules, returns "ACCEPT", otherwise
     * returns "REJECT".
     * 
     * @return "ACCEPT" if the input matches the grammar rules, "REJECT" otherwise
     */
    public String ElseIf() {
        if(expect(18) == -1){
            return "REJECT";
        }//(

        lbl++;
        int lblNumber = lbl;
        if (Bool(lblNumber).equals("REJECT")) {
            return "REJECT";
        }
        if(expect(19) == -1){
            return "REJECT";
        }//)
        if(expect(16) == -1){
            return "REJECT";
        }//{
        
        if(Statement().equals("REJECT")){
            return "REJECT";
        }

        if(expect(17) == -1){
            return "REJECT";
        }//}

        //Need to create jmp within elseif block in order to skip elseif/else in the case elseif is true (basically mimics an if elseif else program)
        lbl++;
        int lblNumberJMP = lbl;
        createJMP(lblNumberJMP);
        createLBL(lblNumber);
        
        //ELSE-IF CASE
        if(accept(36)){
            if(ElseIf().equals("REJECT")){
                return "REJECT";
            }
        }
        //ELSE CASE
        else if(accept(35)){
            if(Else().equals("REJECT")){
                return "REJECT";
            }
        }
        createLBL(lblNumberJMP);

        if(peak(17)){
            return "ACCEPT";
        }//}

        return "ACCEPT";
    }

    /**
     * Parses an Assignment according to the following grammar rules:
     * Assignment → int_type identifier = Expr;
     * Assignment → float_type identifier = Expr;
     * Assignment → identifier = Expr;
     * If the input matches the grammar rules, returns "ACCEPT", otherwise
     * returns "REJECT".
     * 
     * @return "ACCEPT" if the input matches the grammar rules, "REJECT" otherwise
     */
    public String Assignment() {
        flag = 1;
        //float
        if(accept(37)){
            //identifier
            if(isIdentifier(index)){
                dest = tokens[index];
                accept(terminals[index]);
                if(expect(30) == -1){
                    return "REJECT";
                } //=
                String result = Expr(dest);
                if(result.equals("REJECT")){
                    return "REJECT";
                }
                infixToPostfix(mathOps);
                mathOps.clear();
                return "ACCEPT";
            }
        }
        //int
        else if(accept(38)){
            //identifier
            if(isIdentifier(index)){
                dest = tokens[index];
                accept(terminals[index]);
                if(expect(30) == -1){
                    return "REJECT";
                } //=
                String result = Expr(dest);
                if(result.equals("REJECT")){
                    return "REJECT";
                }
                infixToPostfix(mathOps);
                mathOps.clear();
                return "ACCEPT";
            }
        }

        //reassign case
        //identifier
        else if(isIdentifier(index)){
            dest = tokens[index];
            accept(terminals[index]);       //if token is an identifier, accept it
            if(expect(30) == -1){
                return "REJECT";
            } //= 
            //expr
            String result = Expr(dest);
            if(result.equals("REJECT")){
                return "REJECT";
            }
            infixToPostfix(mathOps);
            mathOps.clear();
            return "ACCEPT";
        }
        return "REJECT";
    }

    /**
     * Parses an expression according to the following grammar rules:
     * Expr → identifier
     * Expr → int_literal
     * Expr → float_literal
     * Expr → identifier * Expr
     * Expr → identifier / Expr
     * Expr → identifier + Expr
     * Expr → identifier - Expr
     * Expr → int_literal * Expr
     * Expr → int_literal / Expr
     * Expr → int_literal + Expr
     * Expr → int_literal - Expr
     * Expr → float_literal * Expr
     * Expr → float_literal / Expr
     * Expr → float_literal + Expr
     * Expr → float_literal - Expr
     * Expr → ( Expr )
     * If the input matches the grammar rules, returns "ACCEPT", otherwise
     * returns "REJECT".
     * @param dest the destination register for the decaf instruction
     * @return "ACCEPT" if the input matches the grammar rules, "REJECT" otherwise
     */
    public String Expr(String dest) {
        //identifier
        if (index >= length) {
            mathOps.clear();
            return "REJECT";
        }
        
        if(isIdentifier(index)){
            mathOps.add(tokens[index]);
            accept(terminals[index]);
            if (isOperator(index) != -1) {       //peek for operator. if there is an operator, then there is another expression
                //*, /, +, -
                if(accept(20) || accept(21) || accept(22) || accept(23)){
                    mathOps.add(tokens[index-1]);
                    if (isOperator(index) != -1){
                        return "REJECT";        //if there are two operators in a row, reject
                    }
                    return Expr(dest);
                    // return "ACCEPT";//INSERT DECAF
                }
            }
            else{
                return tokens[index-1];//INSERT DECAF
            }
        }
        //int_literal
        if(accept(40)){
            mathOps.add(tokens[index-1]);
            if (isOperator(index) != -1) {       //peek for operator. if there is an operator, then there is another expression
                //*, /, +, -
                if(accept(20) || accept(21) || accept(22) || accept(23)){
                    mathOps.add(tokens[index-1]);
                    if (isOperator(index) != -1){
                        return "REJECT";        //if there are two operators in a row, reject
                    }
                    return Expr(dest);
                    // return "ACCEPT";//INSERT DECAF
                }
            }
            if(mathOps.size() == 1){
                return tokens[index-1];//INSERT DECAF
            }
            else{
                return "t"+(tempDest+1);
            }

        }
        //float_literal
        if(accept(41)){
            mathOps.add(tokens[index-1]);
            if (isOperator(index) != -1) {       //peek for operator. if there is an operator, then there is another expression
                //*, /, +, -
                if(accept(20) || accept(21) || accept(22) || accept(23)){
                    mathOps.add(tokens[index-1]);
                    if (isOperator(index) != -1){
                        return "REJECT";        //if there are two operators in a row, reject
                    }
                    return Expr(dest);
                    // return "ACCEPT";//INSERT DECAF
                }
            }
            if(mathOps.size() == 1){
                return tokens[index-1];//INSERT DECAF
            }
            else{
                return "t"+(tempDest+1);
            }
        }
        
        if(accept(18)){ // ( Expr )
            mathOps.add(tokens[index-1]);
            Expr(dest);
            mathOps.add(tokens[index]);
        if(expect(19) == -1){
            return "REJECT";
        } //)
            if (isOperator(index) != -1) {       //peek for operator. if there is an operator, then there is another expression
                //*, /, +, -
                if(accept(20) || accept(21) || accept(22) || accept(23)){
                    mathOps.add(tokens[index-1]);
                    if (isOperator(index) != -1){
                        return "REJECT";        //if there are two operators in a row, reject
                    }
                    return Expr(dest);
                    // return "ACCEPT";//INSERT DECAF
                }
            }
            if(mathOps.size() == 3){
                return tokens[index-2];//INSERT DECAF
            }
            else{
                return "t"+(tempDest+1);
            }
        }
        mathOps.clear();
        return "REJECT";
    }

    /**
     * Bool → Expr < Expr
     * Bool → Expr > Expr
     * Bool → Expr <= Expr
     * Bool → Expr >= Expr
     * Bool → Expr != Expr
     * Bool → Expr == Expr
     * 
     * @return "ACCEPT" if Bool is valid, "REJECT" otherwise
     */
    public String Bool(int lblNumber) {
        flag = 0;
        //EXPR CASE
        String left = Expr(null);
        infixToPostfix(mathOps);
        mathOps.clear();
        int cmp = isComparisonOperator(index);
        if (cmp != -1) { //!=, <, <=, >=, >, ==
            accept(terminals[index]); //accept comparison operator
            String right = Expr(null);
            if(right.equals("REJECT")){
                return "REJECT";
            }   
            infixToPostfix(mathOps);
            mathOps.clear();
            createTST(lblNumber, left, right, cmp);
            return "ACCEPT";
        }
        return "REJECT";
    }

    private void optimizeAtoms(){

        List<String> optimizedAtoms = new ArrayList<>(decafAtoms);

        System.out.println("Optimizing...\n");
        printAtoms(decafAtoms);
        int indexCounter = 0;
        for(String atom: decafAtoms){
            atom = atom.replace("(", "").replace(")", "");
            String [] split = atom.split(",");

            for(int j = 0; j < split.length; j++) {
                split[j] = split[j].trim();
            }

            
            if(split[0].equals("MOV") && indexCounter > 1){
                //ONLY FOR ADD, SUB, MUL, DIV
                if(decafAtoms.get(indexCounter-1).contains("MOV") || decafAtoms.get(indexCounter-1).contains("JMP") || decafAtoms.get(indexCounter-1).contains("LBL") || decafAtoms.get(indexCounter-1).contains("TST"))
                    continue;
                optimizedAtoms.set(indexCounter-1, decafAtoms.get(indexCounter-1).replace("(", "").replace(")", ""));
                String [] splitPrevious = optimizedAtoms.get(indexCounter-1).split(",");

                for(int j = 0; j < splitPrevious.length; j++) {
                    splitPrevious[j] = splitPrevious[j].trim();
                }
                
                if(splitPrevious[0].equals("ADD")||splitPrevious[0].equals("SUB")||splitPrevious[0].equals("MUL")||splitPrevious[0].equals("DIV")){

                    if((splitPrevious[1].matches("-?\\d+") || splitPrevious[1].matches("-?\\d*(\\.\\d+)?")) && (splitPrevious[2].matches("-?\\d+")|| splitPrevious[2].matches("-?\\d*(\\.\\d+)?"))){

                        float a = Float.parseFloat(splitPrevious[1]);
                        float b = Float.parseFloat(splitPrevious[2]);
                        float result;

                        switch (splitPrevious[0]){
                            case "ADD": 
                                result = a + b;
                                break;
                            case "SUB":
                                result = a - b;
                                 break;
                            case "MUL":
                                result = a * b;
                                break;
                            case "DIV":
                                result = a/b;
                                break; 
                            default:
                                result = 0;
                                break;
                        }
                        
                        split[1] = result+"";
                        
                        //Build the MOV atom back
                        StringBuilder sb = new StringBuilder();
                        sb.append("(");
                        
                        sb.append(split[0]).append(", ").append(split[1]).append(", ").append(split[2]).append(", ").append(split[3]);
                        
                        sb.append(")");
                        String s = sb.toString();
                        optimizedAtoms.set(indexCounter, s);
                        optimizedAtoms.remove(indexCounter-1);
                        
                    } else {
                        //REBUILD ATOM
                        StringBuilder sb = new StringBuilder();
                        sb.append("(");
                        
                        sb.append(split[0]).append(", ").append(split[1]).append(", ").append(split[2]).append(", ").append(split[3]);
                        
                        sb.append(")");
                        String s = sb.toString();
                        optimizedAtoms.set(indexCounter, s);
                    }
                }
            }

            indexCounter++;
        }
        decafAtoms = optimizedAtoms;
        System.out.println("Optimization complete.\n");
        printAtoms(decafAtoms);
    }

}