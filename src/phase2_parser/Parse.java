package phase2_parser;

import java.util.Arrays;
import java.util.List;

public class Parse{
    private int[] terminals;
    private int index;
    private int length;

    //Check if token at current index is an identifier, this is necessary because there are multiple tokens that can be identifiers
    private boolean isIdentifier(int index) {
        List<Integer> IDENTIFIER_TOKENS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 39);

        if (index >= length) {
            return false;
        }
        return IDENTIFIER_TOKENS.contains(terminals[index]);
    }

    //Check if token at current index is an arithmetic operator
    private boolean isOperator(int index) {
        final List<Integer> OPERATOR_TOKENS = Arrays.asList(20, 21, 22, 23);
        if (index >= length) {
            return false;
        }
        return OPERATOR_TOKENS.contains(terminals[index]);
    }

    //Check if token at current index is a comparison operator
    private boolean isComparisonOperator(int index) {
        final List<Integer> COMPARISON_OPERATOR_TOKENS = Arrays.asList(25, 26, 27, 28, 29, 31);
        if (index >= length) {
            return false;
        }
        return COMPARISON_OPERATOR_TOKENS.contains(terminals[index]);
    }


    public Parse(int[] terminals) {
        this.terminals = terminals;
        this.index = 0;
        this.length = terminals.length;
    }
    public Parse() {
        this.terminals = new int[0];
        this.index = 0;
    }

    public void setTerminals(int[] terminals) {
        this.terminals = terminals;
        this.index = 0;
        this.length = terminals.length;
    }

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

    public boolean peak(int terminal) {
        //If end of input
        if (index >= length) {
            return false;
        }
        //If terminal matches
        return (terminal == terminals[index]);
    }

    /*
     * Statement → if ( Bool ) { Statement }
     * Statement → if ( Bool ) { Statement } Else 
     * Statement → if ( Bool ) { Statement } Else-if
     * Statement → for (Assignment ; Bool ; Expr) { Statement }
     * Statement → while ( Bool ) { Statement }
     * Statement → Assignment
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
            accept(terminals[index]);      //if token is an identifier or number type, accept it

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

    private String While(){
        
        expect(18);//(
        Bool();        //ex: i < 10
        expect(19);//)
        
        expect(16);//{
        Statement();
        expect(17);//}

        return "ACCEPT";
    }

    private String For(){
        expect(18);  //(
        Assignment();     //ex: i = 0
        expect(43); //;
        Bool();         //ex: i < 10
        expect(43); //;
        Assignment();   //ex: i = i + 1
        expect(19);  //)
        
        expect(16);  //{
        Statement();
        expect(17);  //}

        return "ACCEPT";
    }

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

        return "ACCEPT";
    }
    
    /*
     * Else → else { Statement  }
     */
    public String Else() {
        //{
        expect(16);
        Statement();
        //}
        expect(17);
        return "ACCEPT";
    }

    /*
     * Else-if → else if ( Bool ) { Statement  }
     * Else-if → else if ( Bool ) { Statement  } Else
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
        return "ACCEPT";
    }

    /*
     * Assignment → int_type identifier = int_literal;
     * Assignment → float_type identifier = float_literal;
     */
    public String Assignment() {
        //float
        if(accept(37)){
            //identifier
            if(isIdentifier(index)){
                accept(terminals[index]);
                expect(30); //=
                
                expect(41); //float literal
                return "ACCEPT";
            }
        }
        //int
        else if(accept(38)){
            //identifier
            if(isIdentifier(index)){
                accept(terminals[index]);
                expect(30); //=
                
                expect(40); //int literal
                return "ACCEPT";
            }
        }

        //reassign case
        //identifier
        else if(isIdentifier(index)){
            accept(terminals[index]);       //if token is an identifier, accept it
            expect(30); //= 
            //expr
            return Expr();
        }
        else{
            expect(30);//=
            //expr
            if (Expr().equals("ACCEPT")) {
                return "ACCEPT";
            }
        }
        return "REJECT";
    }

    /*
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
     */
    public String Expr() {
        //identifier

        if (index >= length) {
            return "REJECT";
        }
        
        if(isIdentifier(index)){
            accept(terminals[index]);
            if (isOperator(index)) {       //peek for operator. if there is an operator, then there is another expression
                return Expr();
            }
            else{
                return "ACCEPT";//INSERT DECAF
            }
        }
        //int_literal
        if(accept(40)){
            if (isOperator(index)) {       //peek for operator. if there is an operator, then there is another expression
                Expr();
                return "ACCEPT";//INSERT DECAF
            }
            return "ACCEPT";//INSERT DECAF

        }
        //float_literal
        if(accept(41)){
            if (isOperator(index)) {       //peek for operator. if there is an operator, then there is another expression
                Expr();
                return "ACCEPT";//INSERT DECAF
            }
            return "ACCEPT";//INSERT DECAF
        }
        //*, /, +, -
        if(accept(20) || accept(21) || accept(22) || accept(23)){
            if (isOperator(index)){
                return "REJECT";        //if there are two operators in a row, reject
            }
            return Expr();
            // return "ACCEPT";//INSERT DECAF
        }
        
        if(accept(18)){ // ( Expr )
            
            Expr();

            expect(19); //)
            if (isOperator(index)) {       //peek for operator. if there is an operator, then there is another expression
                return Expr();
            }
            return "ACCEPT";//INSERT DECAF
        }
        return "REJECT";
    }

    /*
     * Bool → Expr < Expr
     * Bool → Expr > Expr
     * Bool → Expr <= Expr
     * Bool → Expr >= Expr
     * Bool → Expr != Expr
     * Bool → Expr == Expr
     */
    public String Bool() {
        //EXPR CASE
        Expr();

        if (isComparisonOperator(index)) { //!=, <, <=, >=, >, ==
            accept(terminals[index]); //accept comparison operator
            return Expr();
        }
        
        return "REJECT";
    }
}
