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
    

}
