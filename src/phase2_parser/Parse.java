package phase2_parser;

import java.util.Arrays;
import java.util.List;

public class Parse{
    private int[] terminals;
    private int index;
    private int length;

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
        //if
        if (peak(34)) {
            If();
        }

        //FOR CASE
        //for
        if (peak(32)) {
            For();
        }

        //WHILE CASE
        //while
        if(peak(33)){
            While();
        }
        //ASSIGNMENT CASE
        //int_type or float_type
        if(peak(37) || peak(38) || peak(39)){
            String result = Assignment();
            return result;//INSERT DECAF
        }

        if (index == length) {
            return "ACCEPT";
        }

        return "REJECT";
        
    }

    private String If(){
        if(accept(34)){
            //(
            if(accept(18)){
                Bool();
                //)
                if(accept(19)){
                    //{
                    if(accept(16)){
                        Statement();
                        //}
                        if(accept(17)){
                            //else
                            if(peak(35)){
                                //if 
                                if(peak(34)){
                                    //ELSE IF
                                    ElseIf();
                                }
                                else{
                                    //ELSE
                                    Else();
                                }
                            }
                            else{//NO else
                                //Anything else?
                                if(terminals.length == index){
                                    //ACCEPT
                                    return "ACCEPT";//INSERT DECAF
                                }
                                else{
                                    //REJECT
                                    throw new IllegalArgumentException();
                                }
                            }
                            
                        }
                    }
                }
            }
        }
        return "REJECT";
    }

    private String While(){
        if(accept(33)){
            //(
            if(accept(18)){
                Bool();
                //)
                if(accept(19)){
                    //{
                    if(accept(16)){
                        Statement();
                        //}
                        expect(17);
                        //ACCEPT
                        return "ACCEPT";//INSERT DECAF
                    }
                }
            }
        }
        return "REJECT";
    }

    private String For(){
        if(accept(32)){
            //(
            if(accept(18)){
                Assignment();
                //;
                if(accept(43)){
                    Bool();
                    //;
                    if(accept(43)){
                        Expr();
                        //)
                        if(accept(19)){
                            //{
                            if(accept(16)){
                                Statement();
                                //}
                                expect(17);
                                //ACCEPT
                                return "ACCEPT";//INSERT DECAF
                            }
                        }
                    }
                }    
            }
        }
        return "REJECT";
    }

    /*
     * Assignment → int_type identifier = int_literal;
     * Assignment → float_type identifier = float_literal;
     */
    public String Assignment() {
        //reassign case
        //identifier
        if(accept(39)){
            //= 
            if(accept(30)){
                //expr
                if (Expr().equals("ACCEPT")) {

                    //;
                    expect(43);
                    return "ACCEPT";
                }
            }
        }

        //INT CASE
        //int_type
        if(accept(38)){
            //identifier
            if(accept(39)){
                //=
                if(accept(30)){
                    //expression
                    if (Expr().equals("ACCEPT")) {
                        //;
                        expect(43);
                        return "ACCEPT";
                    }
                }
            }
        }

        //FLOAT CASE
        //float_type
        if(accept(37)){
            //identifier
            if(accept(39)){
                //=
                if(accept(30)){
                    //expression
                    Expr();
                    //;
                    expect(43);
                }
            }
        }
        return "REJECT";
    }

    /*
     * Else → else { Statement  }
     */
    public String Else() {
        //else
        if(accept(35)){
            //{
            if(accept(16)){
                Statement();
                //}
                expect(17);
                //ACCEPT
                return "ACCEPT";//INSERT DECAF
            }
        }
        return "REJECT";
    }

    /*
     * Else-if → else if ( Bool ) { Statement  }
     * Else-if → else if ( Bool ) { Statement  } Else
     */
    public String ElseIf() {
        //else
        if(accept(35)){
            //if
            if(accept(34)){
                //(
                if(accept(18)){
                    Bool();
                    //)
                    if(accept(19)){
                        //{
                        if(accept(16)){
                            Statement();
                            //}
                            expect(17);

                            return "ACCEPT";//INSERT DECAF
                        }
                    }
                }
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
        List<Integer> IDENTIFIER_TOKENS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 39);
        List<Integer> OPERATOR_TOKENS = Arrays.asList(20, 21, 22, 23);
        if(IDENTIFIER_TOKENS.contains(terminals[index])){
            accept(terminals[index]);
            return "ACCEPT";//INSERT DECAF
        }
        //int_literal
        if(accept(40)){
            if (OPERATOR_TOKENS.contains(terminals[index])) {       //peek for operator. if there is an operator, then there is another expression
                Expr();
                return "ACCEPT";//INSERT DECAF
            }
            return "ACCEPT";//INSERT DECAF

        }
        //float_literal
        if(accept(41)){
            if (OPERATOR_TOKENS.contains(terminals[index])) {       //peek for operator. if there is an operator, then there is another expression
                Expr();
                return "ACCEPT";//INSERT DECAF
            }
            return "ACCEPT";//INSERT DECAF
        }
        //*, /, +, -
        if(accept(20) || accept(21) || accept(22) || accept(23)){
            Expr();
            return "ACCEPT";//INSERT DECAF
        }
        //(
        if(accept(18)){
            Expr();
            //)
            if(accept(19)){
                return "ACCEPT";//INSERT DECAF
            }
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
        //!=, <, <=, >=, >, ==
        if (accept(25) ||accept(26) || accept(27) || accept(28) || accept(29) || accept(31)) {
            Expr();
            return "ACCEPT";//INSERT DECAF
        }
        return "REJECT";
    }
}
