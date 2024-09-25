package Framework;

public class Input {
		private char input;
		private int asciiValue;
		private int tableIndex;
	
		/**
		 * Method Name: Input
		 * Purpose: Initializes the field variables
		 * Parameters: char input The character of the input, int tableIndex The input's index in the FSM table
		 * Preconditions: N/A
		 * Postconditions: N/A
		 * Exceptions: N/A
		 */
		public Input(char input, int tableIndex) {
			this.input = input;
			this.asciiValue = input;
			this.tableIndex = tableIndex;
		}
	
		/**
		 * Method Name: getInput
		 * Purpose: Gets input
		 * Parameters: N/A
		 * Preconditions: N/A
		 * Postconditions: Returns input
		 * Exceptions: N/A
		 */
		public char getInput() {
			return input;
		}
		
		/**
		 * Method Name: getASCIIValue
		 * Purpose: Gets the ASCII value
		 * Parameters: N/A
		 * Preconditions: N/A
		 * Postconditions: Returns the ASCII value
		 * Exceptions: N/A
		 */
		public int getASCIIValue() {
			return asciiValue;
		}
		
		/**
		 * Method Name: getTableIndex
		 * Purpose: Gets the table index
		 * Parameters: N/A
		 * Preconditions: N/A
		 * Postconditions: Returns the table index
		 * Exceptions: N/A
		 */
		public int getTableIndex() {
			return tableIndex;
		}
		
		/**
		 * Method Name: toString
		 * Purpose: Get a modified String containing input and asciiValue
		 * Parameters: N/A
		 * Preconditions: N/A
		 * Postconditions: Returns a String containing input and asciiValue
		 * Exceptions: N/A
		 */
		@Override
		public String toString() {
			return input + ": " + asciiValue;
		}
	}