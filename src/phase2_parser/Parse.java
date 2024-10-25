package phase2_parser;

public class Parse{
    private final int[] terminals;
    private int index;

    public Parse(int[] terminals) {
        this.terminals = terminals;
        this.index = 0;
    }

    public boolean accept(int terminal) {
        //If terminal matches
        if(terminal == terminals[index]) {
            index++;
            return true;
        }
        //mismatch
        return false;
    }

    public void expect(int terminal) {
        //If terminal matches
        if(terminal == terminals[index]) {
            index++;
            return;
        }
        //mismatch
        throw new IllegalArgumentException();
    }

    public boolean peak(int terminal) {
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

        //FOR CASE
        //for
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

        //WHILE CASE
        //while
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

        //ASSIGNMENT CASE
        //int_type or float_type
        if(peak(38) || peak(41)){
            String result = Assignment();
            return result;//INSERT DECAF
        }
        return "REJECT";
    }

    /*
     * Assignment → int_type identifier = int_literal;
     * Assignment → float_type identifier = float_literal;
     */
    public String Assignment() {
        //INT CASE
        //int_type
        if(accept(38)){
            //identifier
            if(accept(39)){
                //=
                if(accept(30)){
                    //int_literal
                    if(accept(40)){
                        //;
                        expect(43);
                        return "";//INSERT DECAF
                    }
                }
            }
        }

        //FLOAT CASE
        //float_type
        if(accept(41)){
            //identifier
            if(accept(39)){
                //=
                if(accept(30)){
                    //float_literal
                    if(accept(40)){
                        //;
                        expect(43);
                        return "ACCEPT";//INSERT DECAF
                    }
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
        return "";
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
        return "";
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
    }
}
