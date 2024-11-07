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
    private List<Object[]> decafAtoms;
    private List<String> mathOps;
    private int lbl;
    private String dest;

    //Using VS CODE:
    //Compile the files:  javac -d bin src/phase2_parser/Parse.java 
    //Run the program:  java -cp bin phase2_parser.Parse

    public static void main(String[] args) {
        Parse parser = new Parse();
        ////FOR TESTS///
        /// 
        //For w/ assignment
        //int[] terminals = new int[]{32, 18, 38, 39, 30, 40, 43, 39, 26, 40, 43, 39, 30, 39, 20, 40, 19, 16, 38, 39, 30, 40, 43, 17};
        //String[] tokens = new String[]{"for", "(", "int", "test", "=", "10", ";", "test", "<", "10", ";", "test", "=", "test", "+", "10", ")", "{", "int", "test", "=", "10", ";", "}"};

        ///Empty Nested For        
        //int[] terminals = new int[] {32, 18, 38, 39, 30, 40, 43, 39, 26, 40, 43, 39, 30, 39, 20, 40, 19, 16, 32, 18, 38, 39, 30, 40, 43, 39, 26, 40, 43, 39, 30, 39, 20, 40, 19, 16, 17, 17};
        //String[] tokens = new String[]{"for", "(", "int", "test", "=", "10", ";", "test", "<", "10", ";", "test", "=", "test", "+", "10", ")", "{", "for", "(", "int", "test", "=", "10", ";", "test", "<", "10", ";", "test", "=", "test", "+", "10", ")", "{", "}", "}"};

        ///REJECT INPUT///
        
        //Assignment w/o dividend
        int[] terminals = new int[]{39, 30, 23, 40, 43};
        String[] tokens = new String[]{"test", "=", "/", "10", ";"};

        ///WHILE TESTS///
        
        /// Empty While
        //int[] terminals = new int[]{33, 18, 40, 19, 16, 17};
        //String[] tokens = new String[]{"while", "(", "10", ")", "{", "}"};

        ///IFs///
        
        /// Empty If and Elseif 
        //int[] terminals = new int[] {34, 18, 39, 26, 40, 19, 16, 17, 36, 18, 39, 28, 40, 19, 16, 17};
        //String[] tokens = new String[]{"if", "(", "test", "<", "10", ")", "{", "}", "else if", "(", "test", ">", "10", ")", "{", "}"};

        //If else
            // if ( i < 10 ) { int IDENT = INT_LIT; } else { float IDENT = FLOAT_LIT; }
        //int[] terminals = new int[] {34, 18, 39, 26, 40, 19, 16, 38, 39, 30, 40, 43, 17, 35, 16, 37, 39, 30, 41, 43, 17};
        //String[] tokens = new String[]{"if", "(", "test", "<", "10", ")", "{", "int", "test", "=", "10", ";", "}", "else", "{", "float", "test", "=", "10.0", ";", "}"};

        //Complex if
            // if ( ( INT_LIT * INT_LIT ) <= ( IDENT + FLOAT_LIT ) ) { IDENT = INT_LIT; }
        //int[] terminals = new int[] {34, 18, 18, 40, 22, 40, 19, 26, 18, 39, 20, 41, 19, 17, 16, 39, 30, 40, 43, 17};
        //String[] tokens = new String[]{"if", "(", "(", "10", "*", "10", ")", "<=", "(", "test", "+", "10.0", ")", ")", "{", "test", "=", "10", ";", "}"};

        ///Empty Nested If
        //int[] terminals = new int[] {34, 18, 39, 26, 40, 19, 16, 34, 18, 39, 28, 40, 19, 16, 17, 17};
        //String[] tokens = new String[]{"if", "(", "test", "<", "10", ")", "{", "if", "(", "test", ">", "10", ")", "{", "}", "}"};

        ///ASSIGNMENTS///
                //IDENT = float_literal * ( INT_LIT + INT_LIT );
        //int[] terminals = new int[] {39, 30, 41, 22, 18, 40, 20, 40, 19, 43};
        //String[] tokens = new String[]{"test", "=", "10.0", "*", "(", "10", "+", "10", ")", ";"};

        parser.setTerminals(terminals, tokens);
        String result = parser.Statement();

        System.out.println("RESULT: "+result);
        parser.printAtoms(parser.decafAtoms);
    }

    private void printAtoms(List<Object[]> instructions) {
        for(Object[] atom : instructions) {
            System.out.println(Arrays.toString(atom));
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

        for(String token : expression) {
            System.out.println("TOKEN: " + token);
            if(!token.equals("+") && !token.equals("-") && !token.equals("*") && !token.equals("/")) {
                stack.push(token);
            } else if(stack.size() > 1) {
                String b = stack.pop();
                String a = stack.pop();
                if(token.equals("+")) {
                    //Add decaf
                    decafAtoms.add(new Object[] {"ADD", a, b, dest});
                    stack.push(dest);
                } else if(token.equals("-")) {
                    //Add decaf
                    decafAtoms.add(new Object[] {"SUB", a, b, dest});
                    stack.push(dest);
                } else if(token.equals("*")) {
                    //Add decaf
                    decafAtoms.add(new Object[] {"MUL", a, b, dest});
                    stack.push(dest);
                } else if(token.equals("/")) {
                    //Add decaf
                    decafAtoms.add(new Object[] {"DIV", a, b, dest});
                    stack.push(dest);
                }
            } else {
                String a = stack.pop();
                //Add decaf
                decafAtoms.add(new Object[] {"MOV", token, a, dest});
            }
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
        System.err.println("COMPARISON_OPERATOR_TOKENS: " + terminals[index]);

        switch (terminals[index]) {
            case 25:
                return 6;
            case 26:
                return 2;
            case 27:
                return 4;
            case 28:
                return 3;
            case 29:
                return 5;
            case 31:
                return 1;
            default:
                return -1;
        }
        //return COMPARISON_OPERATOR_TOKENS.contains(terminals[index]);
    }


    public Parse(int[] terminals, String[] tokens) {
        this.terminals = terminals;
        this.index = 0;
        this.length = terminals.length;
        this.decafAtoms = new ArrayList<>();
        this.lbl = 0;
        this.dest = "";
        this.mathOps = new ArrayList<>();
        this.tokens = tokens;
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
     * we have reached the end of the input, an IllegalArgumentException is
     * thrown.
     * 
     * @param terminal the terminal to match
     * @throws IllegalArgumentException if the current token does not match the
     *         given terminal, or if we have reached the end of the input
     */
    public void expect(int terminal) {
        //If end of input
        if (index >= length) {
            throw new IllegalArgumentException();
        }
        //If terminal matches
        if(terminal == terminals[index]) {
            index++;
            return;
        }
        //mismatch
        throw new IllegalArgumentException();
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
                return "REJECT";
            }
        }
        if (index >= length) {
            return "ACCEPT";
        }

        //FOR CASE
        if (accept(32)) {
            if (For().equals("REJECT")) {
                return "REJECT";
            }
        }
        if (index >= length) {
            return "ACCEPT";
        }

        //WHILE CASE
        if(accept(33)){
            if (While().equals("REJECT")) {
                return "REJECT";
            }
        }
        if (index >= length) {
            return "ACCEPT";
        }

        //ASSIGNMENT CASE
        //int_type or float_type or identifier
        if(peak(37) || peak(38) || isIdentifier(index)){
            //accept(terminals[index]);      //if token is an identifier or number type, accept it

            String result = Assignment();
            
            if (result.equals("REJECT")) {
                return "REJECT";
            }

            expect(43);
        }
        if (index >= length) {
            return "ACCEPT";
        }

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
        decafAtoms.add(new Object[] {"LBL", "", "", "", "", "L"+lbl});
        expect(18);//(
        Bool();        //ex: i < 10
        expect(19);//)
        
        expect(16);//{
        Statement();
        //Add decaf
        decafAtoms.add(new Object[] {"JMP", "", "", "", "", "L"+lbl});
        expect(17);//}
        //Add decaf
        decafAtoms.add(new Object[] {"LBL", "", "", "", "", "L"+lbl});
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
        //Add decaf
        decafAtoms.add(new Object[] {"LBL", "", "", "", "", "L"+lbl});
        expect(18);  //(
        Assignment();     //ex: i = 0
        expect(43); //;
        Bool();         //ex: i < 10
        expect(43); //;
        Assignment();   //ex: i = i + 1
        expect(19);  //)
        
        expect(16);  //{
        Statement();
        //Add decaf
        decafAtoms.add(new Object[] {"JMP", "", "", "", "", "L"+lbl});
        expect(17);  //}
        //Add decaf
        decafAtoms.add(new Object[] {"LBL", "", "", "", "", "L"+lbl});
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
        expect(18);        //(
        if (Bool().equals("REJECT")) {
            return "REJECT";
        }

        expect(19); //)
        expect(16); //{
        Statement();
        expect(17); // }
        
        //ELSE-IF CASE
        if(accept(36)){
            return ElseIf();
        }
        //ELSE CASE
        else if(accept(35)){
            return Else();
        }
        //Add decaf
        decafAtoms.add(new Object[] {"LBL", "", "", "", "", "L"+lbl});
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
        expect(16);
        Statement();
        //}
        expect(17);

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
        expect(18);//(
        if (Bool().equals("REJECT")) {
            return "REJECT";
        }
        expect(19);//)
        expect(16);//{
        Statement();
        expect(17);//}
        //ELSE CASE
        if(accept(35)){
            Else();
        }
        //Add decaf
        decafAtoms.add(new Object[] {"LBL", "", "", "", "", "L"+lbl});
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
        //float
        if(accept(37)){
            //identifier
            if(isIdentifier(index)){
                dest = tokens[index];
                accept(terminals[index]);
                expect(30); //=
                String result = Expr(dest);
                if(result.equals("REJECT")){
                    return "REJECT";
                }
                infixToPostfix(mathOps);
                mathOps.clear();
                //expect(41); //float literal
                return "ACCEPT";
            }
        }
        //int
        else if(accept(38)){
            //identifier
            if(isIdentifier(index)){
                dest = tokens[index];
                accept(terminals[index]);
                expect(30); //=
                String result = Expr(dest);
                if(result.equals("REJECT")){
                    return "REJECT";
                }
                infixToPostfix(mathOps);
                mathOps.clear();
                //expect(40); //int literal
                return "ACCEPT";
            }
        }

        //reassign case
        //identifier
        else if(isIdentifier(index)){
            dest = tokens[index];
            accept(terminals[index]);       //if token is an identifier, accept it
            expect(30); //= 
            //expr
            String result = Expr(dest);
            if(result.equals("REJECT")){
                return "REJECT";
            }
            infixToPostfix(mathOps);
            mathOps.clear();
            return "ACCEPT";
        }
        /*
        else{
            expect(30);//=
            if(isOperator(index) != -1)    //check for operator after = (no number/identifer to operate)
                return "REJECT";
            //expr
            if (Expr().equals("ACCEPT")) {
                return "ACCEPT";
            }
        }*/
        throw new IllegalArgumentException("Invalid statement");
        //return "REJECT";
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
                return "ACCEPT";//INSERT DECAF
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
            return "ACCEPT";//INSERT DECAF

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

            return "ACCEPT";//INSERT DECAF
        }
        
        if(accept(18)){ // ( Expr )
            mathOps.add(tokens[index-1]);
            Expr(dest);
            mathOps.add(tokens[index]);
            expect(19); //)
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

            return "ACCEPT";//INSERT DECAF
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
    public String Bool() {
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
            decafAtoms.add(new Object[] {"TST", left, right, "", cmp, "L"+lbl});
            return "ACCEPT";
        }
        
        return "REJECT";
    }
}