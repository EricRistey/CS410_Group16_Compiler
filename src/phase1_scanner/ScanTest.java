package phase1_scanner;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class ScanTest {

    private Scan scanner;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        scanner = new Scan();
        System.setOut(new PrintStream(outContent));
    }

    private void simulateInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    public void testFor() {
        simulateInput("for");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: FOR_COND"));
    }

    @Test
    public void testIf() {
        simulateInput("if");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: IF_COND"));
    }

    @Test
    public void testElseIf() {
        simulateInput("elseif");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: ELSE_IF_COND"));
    }

    @Test
    public void testWhile() {
        simulateInput("while");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: WHILE_COND"));
    }

    @Test
    public void testOpenBrace() {
        simulateInput("{");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: OPEN_BRACE"));
    }

    @Test
    public void testCloseBrace() {
        simulateInput("}");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: CLOSE_BRACE"));
    }

    @Test
    public void testOpenP() {
        simulateInput("(");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: OPEN_PARAN"));
    }

    @Test
    public void testCloseP() {
        simulateInput(")");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: CLOSE_PARAN"));
    }

    @Test
    public void testAddOp() {
        simulateInput("+");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: ADD_OP"));
    }

    @Test
    public void testSubtractOp() {
        simulateInput("-");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: SUBTRACT_OP"));
    }

    @Test
    public void testMultiplyOp() {
        simulateInput("*");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: MULTIPLY_OP"));
    }

    @Test
    public void testDivideOp() {
        simulateInput("/");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: DIVIDE_OP"));
    }

    @Test
    public void testEqualOp() {
        simulateInput("=");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: ASSIGN_OP"));
    }

    @Test
    public void testNotEqualOp() {
        simulateInput("!=");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: NOT_EQUAL_OP"));
    }

    @Test
    public void testLessThanOp() {
        simulateInput("<");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: LT_OP"));
    }

    @Test
    public void testLessThanOrEqualOp() {
        simulateInput("<=");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: LT_ET_OP"));
    }

    @Test
    public void testGreaterThanOp() {
        simulateInput(">");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: GT_OP"));
    }

    @Test
    public void testGreaterThanOrEqualOp() {
        simulateInput(">=");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: GT_ET_OP"));
    }

    @Test
    public void testExclamationOp() {
        simulateInput("!");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: EXCLAMATION_STATE"));
    }

    @Test
    public void testForStatement(){
        //must have spaces
        simulateInput("for (int i = 0; i < 10; i = 1 + i) {}");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: FOR_COND"));
        assertTrue(outContent.toString().contains("State: OPEN_PARAN"));
        assertTrue(outContent.toString().contains("State: INT_TYPE"));
        assertTrue(outContent.toString().contains("State: I_STATE"));
        assertTrue(outContent.toString().contains("State: ASSIGN_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: SEMICOLON"));
        assertTrue(outContent.toString().contains("State: I_STATE"));
        assertTrue(outContent.toString().contains("State: LT_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: SEMICOLON"));
        assertTrue(outContent.toString().contains("State: I_STATE"));
        assertTrue(outContent.toString().contains("State: ASSIGN_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: ADD_OP"));
        assertTrue(outContent.toString().contains("State: I_STATE"));
        assertTrue(outContent.toString().contains("State: CLOSE_PARAN"));
        assertTrue(outContent.toString().contains("State: OPEN_BRACE"));
        assertTrue(outContent.toString().contains("State: CLOSE_BRACE"));
    }

    @Test
    public void testVariableAssign1(){
        //only works with a space between 10 and ;
        simulateInput("int x = 10;");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: INT_TYPE"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: ASSIGN_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: SEMICOLON"));

    }

    @Test
    public void testVariableAssign2(){
        simulateInput("float i = 10.5;");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: FLOAT_TYPE"));
        assertTrue(outContent.toString().contains("State: I_STATE"));
        assertTrue(outContent.toString().contains("State: ASSIGN_OP"));
        assertTrue(outContent.toString().contains("State: FLOAT_LIT"));
        assertTrue(outContent.toString().contains("State: SEMICOLON"));
    }

    @Test
    public void testForStatement2(){
        //must have spaces
        simulateInput("for (int j = 0; j < 10; j = 1+j) {}");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: FOR_COND"));
        assertTrue(outContent.toString().contains("State: OPEN_PARAN"));
        assertTrue(outContent.toString().contains("State: INT_TYPE"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: ASSIGN_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: SEMICOLON"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: LT_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: SEMICOLON"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: ASSIGN_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: ADD_OP"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: CLOSE_PARAN"));
        assertTrue(outContent.toString().contains("State: OPEN_BRACE"));
        assertTrue(outContent.toString().contains("State: CLOSE_BRACE"));
    }

    @Test
    public void testIfStatement(){
        simulateInput("if (x < 10) {}");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: IF_COND"));
        assertTrue(outContent.toString().contains("State: OPEN_PARAN"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: LT_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: CLOSE_PARAN"));
        assertTrue(outContent.toString().contains("State: OPEN_BRACE"));
        assertTrue(outContent.toString().contains("State: CLOSE_BRACE"));
    }

    @Test
    public void testComparison(){
        simulateInput("10 <= 20");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: LT_ET_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
    }

    @Test
    public void testComparison2(){
        simulateInput("10 >= 20");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: GT_ET_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
    }

    @Test
    public void testComparison3(){
        simulateInput("10 == 20");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: EQUALS_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
    }

    @Test
    public void testComparison4(){
        simulateInput("10 != 20");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: NOT_EQUAL_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
    }

    @Test
    public void testWhileCond(){
        simulateInput("while (x < 10) {}");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: WHILE_COND"));
        assertTrue(outContent.toString().contains("State: OPEN_PARAN"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: LT_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: CLOSE_PARAN"));
        assertTrue(outContent.toString().contains("State: OPEN_BRACE"));
        assertTrue(outContent.toString().contains("State: CLOSE_BRACE"));
    }

    @Test
    public void whileCond2(){
        simulateInput("while (x    < 10) {if  (x >= 9)  {x = 1;} else {x = x  + 1;} }");
        scanner.main(new String[0]);
        assertTrue(outContent.toString().contains("State: WHILE_COND"));
        assertTrue(outContent.toString().contains("State: OPEN_PARAN"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: LT_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: CLOSE_PARAN"));
        assertTrue(outContent.toString().contains("State: OPEN_BRACE"));
        assertTrue(outContent.toString().contains("State: IF_COND"));
        assertTrue(outContent.toString().contains("State: OPEN_PARAN"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: GT_ET_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: CLOSE_PARAN"));
        assertTrue(outContent.toString().contains("State: OPEN_BRACE"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: ASSIGN_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: SEMICOLON"));
        assertTrue(outContent.toString().contains("State: CLOSE_BRACE"));
        assertTrue(outContent.toString().contains("State: ELSE_COND"));
        assertTrue(outContent.toString().contains("State: OPEN_BRACE"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: ASSIGN_OP"));
        assertTrue(outContent.toString().contains("State: IDENTIFIER"));
        assertTrue(outContent.toString().contains("State: ADD_OP"));
        assertTrue(outContent.toString().contains("State: INT_LIT"));
        assertTrue(outContent.toString().contains("State: SEMICOLON"));
        assertTrue(outContent.toString().contains("State: CLOSE_BRACE"));
        assertTrue(outContent.toString().contains("State: CLOSE_BRACE"));
    }

}
