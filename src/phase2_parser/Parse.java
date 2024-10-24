package phase2_parser;

public class Parse {
    
    public boolean accept(int terminal) {
        //If terminal matches
        return true;
        //else mismatch
        //return false;
    }

    public void expect(int terminal) {
        //If terminal matches
            //do nothing
        //else mismatch
            //Reject
    }

    public boolean peak(int terminal) {
        //If terminal matches
        return true;
        //else mismatch
        //return false;
    }

    /*
     * Statement → if ( Bool ) { Statement }
     * Statement → if ( Bool ) { Statement } Else 
     * Statement → if ( Bool ) { Statement } Else-if
     * Statement → for (Assignment ; Bool ; Expr) { Statement }
     * Statement → while ( Bool ) { Statement }
     */
    public String Statement() {
        return "";
    }

    /*
     * Assignment → int_type identifier = int_literal;
     * Assignment → float_type identifier = float_literal;
     */
    public String Assignment() {
        return "";
    }

    /*
     * Else → else { Statement  }
     */
    public String Else() {
        return "";
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
        return "";
    }
    
}
