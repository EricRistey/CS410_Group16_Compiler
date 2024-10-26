package phase2_parser;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ParseTest {

    private Parse parser;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private final HashMap<Integer, String> decoding = new HashMap<>();  //using this to decode the terminals and display them when a test is run

    private void initDecoding() {
        decoding.put(16, "{" );
        decoding.put(17, "} " );
        decoding.put(18, "(" );
        decoding.put(19, ") " );
        decoding.put(20, "+ " );
        decoding.put(21, "- " );
        decoding.put(22, "* " );
        decoding.put(23, "/ " );
        decoding.put(24, "! " );
        decoding.put(25, "!= " );
        decoding.put(26, "< " );
        decoding.put(27, "<= " );
        decoding.put(28, "> " );
        decoding.put(29, ">= " );
        decoding.put(30, "= ");
        decoding.put(31, "== " );
        decoding.put(32, "for " );
        decoding.put(33, "while " );
        decoding.put(34, "if " );
        decoding.put(35, "else " );
        decoding.put(36, "else if " );
        decoding.put(37, "float" );
        decoding.put(38, "int " );
        decoding.put(39, "IDENT " );
        decoding.put(40, "INT_LIT " );
        decoding.put(41, "FLOAT_LIT " );
        decoding.put(43, "; " );
    }


    @Before
    public void setUp() {
        initDecoding();
        parser = new Parse();
        System.setOut(new PrintStream(outContent));
    }
    private void printTerminals(int[] terminals) {
        for (int terminal : terminals) {
            System.out.print(decoding.get(terminal));
        }
        System.out.println();
    }

    @Test
    public void testIfStatement() {
        // if ( i < 10 ) { int IDENT = INT_LIT; }
        int[] terminals = {34, 18, 6, 26, 40, 19, 16, 38, 39, 30, 40, 43, 17};
        printTerminals(terminals);

        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testIfElseStatement() {
        // if ( i < 10 ) { int IDENT = INT_LIT; } else { float IDENT = FLOAT_LIT; }
        int[] terminals = {34, 18, 6, 26, 40, 19, 16, 38, 39, 30, 40, 43, 17, 35, 16, 37, 39, 30, 41, 43, 17};
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testDeclareIntLit() {
        // INT IDENT = INT_LIT;
        int[] terminals = {38, 39, 30, 40, 43};
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testAssignIntLit() {
        // IDENT = FLOAT_LIT;
        int[] terminals = {39, 30, 41, 43};
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testAssignExpr() {
        //            IDENT = float_literal * ( INT_LIT + INT_LIT );
        int[] terminals = {39, 30, 41, 22, 18, 40, 20, 40, 19, 43};
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testForStatement() {
        int[] terminals = {32, 18, 38, 39, 30, 40, 43, 40, 43, 40, 19, 16, 38, 39, 30, 40, 43, 17};
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }



    @Test
    public void testWhileStatement() {
        int[] terminals = {33, 18, 40, 19, 16, 38, 39, 30, 40, 43, 17};
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testAssignmentStatement() {
        int[] terminals = {38, 39, 30, 40, 43};
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testInvalidStatement() {
        int[] terminals = {34, 18, 40, 19, 16, 38, 39, 30, 40, 43}; // Missing closing brace
        parser.setTerminals(terminals);
        assertThrows(IllegalArgumentException.class, () -> parser.Statement());
    }
}