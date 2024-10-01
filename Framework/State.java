package Framework;

public class State {
		private String name;
		private int tableIndex;
	
		/**
		 * Method Name: State
		 * Purpose: Initializes the field variables
		 * Parameters: String name The state's name, int tableIndex The state's index on the FSM table
		 * Preconditions: N/A
		 * Postconditions: N/A
		 * Exceptions: N/A
		 */
		public State(String name, int tableIndex) {
			this.name = name;
			this.tableIndex = tableIndex;
		}
	
		/**
		 * Method Name: getName
		 * Purpose: Gets the name value
		 * Parameters: N/A
		 * Preconditions: N/A
		 * Postconditions: Returns the name value
		 * Exceptions: N/A
		 */
		public String getName() {
			return name;
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
		 * Purpose: Get a modified String containing name and tableIndex
		 * Parameters: N/A
		 * Preconditions: N/A
		 * Postconditions: Returns a String containing name and tableIndex
		 * Exceptions: N/A
		 */
		@Override
		public String toString() {
			return name.toString() + ": " + tableIndex;
		}
	}