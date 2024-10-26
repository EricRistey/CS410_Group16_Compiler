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

    private final HashMap<String, Integer> encoding = new HashMap<>();  //using this to encode the test tokens
    private void initEncoding(){
        encoding.put("{", 16);
        encoding.put("}", 17);
        encoding.put("(", 18);
        encoding.put(")", 19);
        encoding.put("+", 20);
        encoding.put("-", 21);
        encoding.put("*", 22);
        encoding.put("/", 23);
        encoding.put("!", 24);
        encoding.put("!=", 25);
        encoding.put("<", 26);
        encoding.put("<=", 27);
        encoding.put(">", 28);
        encoding.put(">=", 29);
        encoding.put("=", 30);
        encoding.put("==", 31);
        encoding.put("for", 32);
        encoding.put("while", 33);
        encoding.put("if", 34);
        encoding.put("else", 35);
        encoding.put("else if", 36);
        encoding.put("float", 37);
        encoding.put("int", 38);
        encoding.put("IDENT", 39);
        encoding.put("INT_LIT", 40);
        encoding.put("FLOAT_LIT", 41);
        encoding.put(";", 43);
    }

    private int[] encodeTerminals(String[] terminals) { //encode the test tokens using the encoding hashmap
        int[] encoded = new int[terminals.length];
        for (int i = 0; i < terminals.length; i++) {
            encoded[i] = encoding.get(terminals[i]);
        }
        return encoded;
    }


    @Before
    public void setUp() {
        initEncoding();
        parser = new Parse();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testIfStatement() {
        // if ( i < 10 ) { int IDENT = INT_LIT; }
        int[] terminals = encodeTerminals(new String[]{"if", "(", "IDENT", "<", "INT_LIT", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});

        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testIfElseStatement() {
        // if ( i < 10 ) { int IDENT = INT_LIT; } else { float IDENT = FLOAT_LIT; }
        // int[] terminals = {34, 18, 6, 26, 40, 19, 16, 38, 39, 30, 40, 43, 17, 35, 16, 37, 39, 30, 41, 43, 17};
        int[] terminals = encodeTerminals(new String[]{"if", "(", "IDENT", "<", "INT_LIT", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}", "else", "{", "float", "IDENT", "=", "FLOAT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testValidBooleanExpression() {
        // if ( ( INT_LIT * INT_LIT ) <= ( IDENT + FLOAT_LIT ) ) { IDENT = INT_LIT; }
        int[] terminals = encodeTerminals(new String[]{"if", "(", "(", "INT_LIT", "*", "INT_LIT",  ")", "<=", "(", "IDENT", "+", "FLOAT_LIT", ")",  ")", "{", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testValidComplexExpression() {
        // IDENT = ( IDENT + INT_LIT ) * ( IDENT - INT_LIT );
        int[] terminals = encodeTerminals(new String[]{"IDENT", "=", "(", "IDENT", "+", "INT_LIT", ")", "*", "(", "IDENT", "-", "INT_LIT", ")", ";"});
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testValidNestedIf() {
        // if ( IDENT < INT_LIT ) { if ( IDENT > INT_LIT ) { IDENT = INT_LIT; } }
        int[] terminals = encodeTerminals(new String[]{"if", "(", "IDENT", "<", "INT_LIT", ")", "{", "if", "(", "IDENT", ">", "INT_LIT", ")", "{", "IDENT", "=", "INT_LIT", ";", "}", "}"});
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testValidElseIf() {
        // if ( IDENT < INT_LIT ) { IDENT = INT_LIT; } else if ( IDENT > FLOAT_LIT ) { IDENT = FLOAT_LIT; } else { IDENT = INT_LIT; }
        int[] terminals = encodeTerminals(new String[]{"if", "(", "IDENT", "<", "INT_LIT", ")", "{", "IDENT", "=", "INT_LIT", ";", "}", "else if", "(", "IDENT", ">", "FLOAT_LIT", ")", "{", "IDENT", "=", "FLOAT_LIT", ";", "}", "else", "{", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testDeclareIntLit() {
        // INT IDENT = INT_LIT;
        // int[] terminals = {38, 39, 30, 40, 43};
        int[] terminals = encodeTerminals(new String[]{"int", "IDENT", "=", "INT_LIT", ";"});
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testAssignIntLit() {
        // IDENT = FLOAT_LIT;
        // int[] terminals = {39, 30, 41, 43};
        int[] terminals = encodeTerminals(new String[]{"IDENT", "=", "FLOAT_LIT", ";"});
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testAssignExpr() {
        //            IDENT = float_literal * ( INT_LIT + INT_LIT );
        // int[] terminals = {39, 30, 41, 22, 18, 40, 20, 40, 19, 43};
        int[] terminals = encodeTerminals(new String[]{"IDENT", "=", "FLOAT_LIT", "*", "(", "INT_LIT", "+", "INT_LIT", ")", ";"});
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testForStatement() {
        // for ( int i = 0; i < 10; i = i + 1 ) { int IDENT = INT_LIT; }
        // int[] terminals = {32, 18, 38, 39, 30, 40, 43, 39, 26, 40, 43, 39, 30, 39, 20, 40, 19, 16, 38, 39, 30, 40, 43, 17};
        int[] terminals = encodeTerminals(new String[]{"for", "(", "int", "IDENT", "=", "INT_LIT", ";", "IDENT", "<", "INT_LIT", ";",
         "IDENT", "=", "IDENT", "+", "INT_LIT", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    @Test
    public void testWhileStatement() {
        // int[] terminals = {33, 18, 40, 19, 16, 38, 39, 30, 40, 43, 17};
        int[] terminals = encodeTerminals(new String[]{"while", "(", "INT_LIT", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertEquals("ACCEPT", parser.Statement());
    }

    //tests for invalid statements
    @Test
    public void testInvalidStatement() {
        // if ( i < 10 ) { int IDENT = INT_LIT; } else { float IDENT = FLOAT_LIT;
        // int[] terminals = {34, 18, 6, 26, 40, 19, 16, 38, 39, 30, 40, 43}; // Missing closing brace
        int[] terminals = encodeTerminals(new String[]{"if", "(", "IDENT", "<", "INT_LIT", ")", "{", "int", "IDENT", "=", "INT_LIT", ";"}); // Missing closing brace
        parser.setTerminals(terminals);
        assertThrows(IllegalArgumentException.class, () -> parser.Statement());
    }

    @Test
    public void testInvalidIfStatement() {
        // if ( i < 10 { int IDENT = INT_LIT; }
        // Missing closing parenthesis
        int[] terminals = encodeTerminals(new String[]{"if", "(", "IDENT", "<", "INT_LIT", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertThrows(IllegalArgumentException.class, () -> parser.Statement());
    }

    @Test
    public void testInvalidElseif(){
        // if (Ident < 10) { } else {} else if (Ident > 10) { }
        // else if statement without an if-else block
        int[] terminals = encodeTerminals(new String[]{"if", "(", "IDENT", "<", "INT_LIT", ")", "{", "}", "else", "{", "}", "else if", "(", "IDENT", ">", "INT_LIT", ")", "{", "}"});
        parser.setTerminals(terminals);
        assertEquals("REJECT", parser.Statement());
    }

    @Test
    public void testInvalidForStatement() {
        // for ( int i = 0; i < 10; i = i + 1 { int IDENT = INT_LIT; }
        // Missing closing parenthesis
        int[] terminals = encodeTerminals(new String[]{"for", "(", "int", "IDENT", "=", "INT_LIT", ";", "IDENT", "<", "INT_LIT", ";", "IDENT", "=", "IDENT", "+", "INT_LIT", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertThrows(IllegalArgumentException.class, () -> parser.Statement());
    }

    @Test
    public void testInvalidWhileStatement() {
        // while ( INT_LIT { int IDENT = INT_LIT; }
        // Missing closing parenthesis
        int[] terminals = encodeTerminals(new String[]{"while", "(", "INT_LIT", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertThrows(IllegalArgumentException.class, () -> parser.Statement());
    }

    @Test
    public void testInvalidAssignment() {
        // int IDENT = ;
        // Missing literal
        int[] terminals = encodeTerminals(new String[]{"int", "IDENT", "=", ";"});
        parser.setTerminals(terminals);
        //expect reject
        assertEquals("REJECT", parser.Statement());
    }

    @Test
    public void testInvalidExpr() {
        // IDENT = IDENT + ;
        // Missing second operand
        int[] terminals = encodeTerminals(new String[]{"IDENT", "=", "IDENT", "+", ";"});
        parser.setTerminals(terminals);
        assertEquals("REJECT", parser.Statement());
    }

    @Test
    public void testInvalidBool() {
        // if ( IDENT == ) { int IDENT = INT_LIT; }
        // Missing second operand in boolean expression
        int[] terminals = encodeTerminals(new String[]{"if", "(", "IDENT", "==", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertEquals("REJECT", parser.Statement());
    }

    @Test
    public void testInvalidElseIf() {
        // if ( IDENT < INT_LIT ) { int IDENT = INT_LIT; } else if ( IDENT > ) { int IDENT = INT_LIT; }
        // Missing second operand in else-if condition
        int[] terminals = encodeTerminals(new String[]{"if", "(", "IDENT", "<", "INT_LIT", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}", "else if", "(", "IDENT", ">", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertEquals("REJECT", parser.Statement());
    }

    @Test
    public void testInvalidMissingSemicolon() {
        // int IDENT = INT_LIT
        // Missing semicolon
        int[] terminals = encodeTerminals(new String[]{"int", "IDENT", "=", "INT_LIT"});
        parser.setTerminals(terminals);
        //illegal argument exception
        assertThrows(IllegalArgumentException.class, () -> parser.Statement());
    }

    @Test
    public void testInvalidUnmatchedBraces() {
        // { int IDENT = INT_LIT;
        // Missing closing brace
        int[] terminals = encodeTerminals(new String[]{"{", "int", "IDENT", "=", "INT_LIT", ";"});
        parser.setTerminals(terminals);
        assertEquals("REJECT", parser.Statement());
    }

    @Test
    public void testInvalidNestedIf() {
        // if ( IDENT < INT_LIT ) { if ( IDENT > INT_LIT ) { int IDENT = INT_LIT; }
        // Missing closing braces for nested if
        int[] terminals = encodeTerminals(new String[]{"if", "(", "IDENT", "<", "INT_LIT", ")", "{", "if", "(", "IDENT", ">", "INT_LIT", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        //illegal argument exception
        assertThrows(IllegalArgumentException.class, () -> parser.Statement());
    }

    @Test
    public void testInvalidWhileWithoutCondition() {
        // while { int IDENT = INT_LIT; }
        // Missing condition in while loop
        int[] terminals = encodeTerminals(new String[]{"while", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        // assertEquals("REJECT", parser.Statement());
        assertThrows(IllegalArgumentException.class, () -> parser.Statement());
    }

    @Test
    public void testInvalidForWithoutInit() {
        // for ( ; i < 10; i = i + 1 ) { int IDENT = INT_LIT; }
        // Missing initialization in for loop
        int[] terminals = encodeTerminals(new String[]{"for", "(", ";", "IDENT", "<", "INT_LIT", ";", "IDENT", "=", "IDENT", "+", "INT_LIT", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        // assertEquals("REJECT", parser.Statement());
        assertThrows(IllegalArgumentException.class, () -> parser.Statement());
    }

    @Test
    public void testInvalidForWithoutUpdate() {
        // for ( int i = 0; i < 10; ) { int IDENT = INT_LIT; }
        // Missing update in for loop
        int[] terminals = encodeTerminals(new String[]{"for", "(", "int", "IDENT", "=", "INT_LIT", ";", "IDENT", "<", "INT_LIT", ";", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        // assertEquals("REJECT", parser.Statement());
        assertThrows(IllegalArgumentException.class, () -> parser.Statement());
    }

    @Test
    public void testInvalidExpression() {
        // IDENT = IDENT + * INT_LIT;
        // Invalid expression with consecutive operators
        int[] terminals = encodeTerminals(new String[]{"IDENT", "=", "IDENT", "+", "*", "INT_LIT", ";"});
        parser.setTerminals(terminals);
        assertEquals("REJECT", parser.Statement());
    }

    @Test
    public void testInvalidEmptyIf() {
        // if () { int IDENT = INT_LIT; }
        // Empty condition in if statement
        int[] terminals = encodeTerminals(new String[]{"if", "(", ")", "{", "int", "IDENT", "=", "INT_LIT", ";", "}"});
        parser.setTerminals(terminals);
        assertEquals("REJECT", parser.Statement());
    }
}
