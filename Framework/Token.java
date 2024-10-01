package Framework;

public class Token {
	private String name;
	private Object value;

	/**
	 * Method Name: Token
	 * Purpose: Initializes the field variables
	 * Parameters: String name The token name, Object value The value of the token
	 * Preconditions: N/A
	 * Postconditions: N/A
	 * Exceptions: N/A
	 */
	public Token(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Method Name: toString
	 * Purpose: Get a modified String containing name and value
	 * Parameters: N/A
	 * Preconditions: N/A
	 * Postconditions: Returns a String containing name and value
	 * Exceptions: N/A
	 */
	@Override
	public String toString() {
		return name.toString() + ": " + value.toString();
	}
}