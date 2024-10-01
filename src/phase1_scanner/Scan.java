package phase1_scanner;
import java.util.Scanner;

/*************************************************************
 * Group 16 - Scanner
 * 
 * Authors: 
 *      Scan.java: Andrew DeGarmo, Luke Hare
 *      StateTable.java: Andrew Degarmo, Luke Hare
 *      ScanTest.java (Junit tests for different inputs): Eric Ristey, Quinn McAuliffe, Greyson Meares
 * Reviewers: 
 *      Michael Ames
 * 
 * CS410 - Compiler Construction
 * 9/24/2024
 * 
 * To Note: 
 *    - States F, FL, FLO, FLOA, I, IN, W, WH, WHI, WHIL, E, EL, ELS, ELSEI are assumed to be IDENTIFIERS
 * 
 ************************************************************/
public class Scan {

    //Using VS CODE:
        //Adding class for file: javac src/phase1_scanner/*.java
        //Compile the files:  javac -d bin src/phase1_scanner/StateTable.java src/phase1_scanner/Scan.java
        //Run the program:  java -cp bin phase1_scanner.Scan

    public static void main(String[] args) {

        //Create StateTable object
        StateTable info = new StateTable();
        int[][] state_table = info.st_table;
        String[] accepting_states = info.acceptingStates_string;

        //Initial state
        int state = 0;

        //Gather input from user
        System.out.println("Enter input stream: ");
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        scan.close();

        //States list will contain all final states that the state machine reaches
        int states[] = new int[100];
        String states_string[] = new String[100];

        //Current character
        int inp;

        int j = 0;
        
        //Previous state
        int prevState = 0;

        //Traverse the state table to tokenize the input
        for(int i = 0; i < input.length() ; i++){
            StringBuilder token = new StringBuilder();
            //Read character from input stream
            while(i<input.length() && (inp=input.charAt(i) - ' ') > 0){

                //Check if character is within our ASCII range (1-126)
                if(inp > 126 || inp < 1){
                    state = info.INVALID;
                    print_states(states, accepting_states, states_string);
                    System.out.println(input.charAt(i) + " is not a valid character.");
                    System.exit(-1);
                }
                state = state_table[state][inp];

                //If state == invalid transition or space
                if(state == info.INVALID){

                    //Decrement i once so for loop won't skip the current character when traversing again
                    i--;
                    break;
                }
                else{

                    //Saving current state into prevState so we can still access the final state if we hit an invalid transition or space
                    prevState = state;
                    token.append(input.charAt(i));
                    i++;
                }
            }
            
            //If a character is not covered by the transition table
            if(state == info.INVALID && token.length() == 0){
                //i+1 is necessary to counteract the i-- before it got here
                //Print final states
                print_states(states, accepting_states, states_string);
                System.out.println(input.charAt(i+1) + " is not a valid character.");
                System.exit(-1);
            }
            else if(token.length() == 0) {
                state = 0;
                continue;
            }

            states_string[j] = token.toString();
 
            //Store Final state
            states[j++] = prevState;


            //Reset state for next token
            state = 0;
        }

        //print final states
        print_states(states, accepting_states, states_string);
    };

    // Method to print final states
    static void print_states(int[] states, String[] accepting_states, String[] states_string){
        System.out.println("\nFinal States: ");
        for(int i = 0; states[i] != 0 || states_string[i] != null; i++){
            System.out.println("State: " + accepting_states[states[i]] + " | " + "Input: " + states_string[i]);
        }
    }
};