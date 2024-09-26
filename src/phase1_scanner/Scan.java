package phase1_scanner;
import java.util.Scanner;

/*************************************************************
 * Group 16 - Scanner
 * Members: Andrew DeGarmo, 
 * 
 * CS410 - Compiler Construction
 * 9/24/2024
 * 
 * To Note: Will we need to implement?
 *  This program does not account for 
 *  ex. 10s (reads identifier, does it need to be invalid?)
 * 
 ************************************************************/
public class Scan {

    //Using VS CODE:
        //Adding class for file: javac src/phase1_scanner/*.java
        //Compile the files:  javac -d bin src/phase1_scanner/StateTable.java src/phase1_scanner/Scan.java
        //Run the program:  java -cp bin phase1_scanner.Scan
    public static void main(String[] args) {

        StateTable info = new StateTable();
        int[][] state_table = info.st_table;
        String[] accepting_states = info.acceptingStates_string;

        //initial state
        int state = 0;

        //Gather input from user
        System.out.println("Enter input stream: ");
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();

        //States list will contain all final states that the state machine reaches
        int states[] = new int[100];
        String states_string[] = new String[100];

        //current character
        int inp;

        int j = 0;
        
        for(int i = 0; i < input.length() ; i++){
            StringBuilder token = new StringBuilder();
            //read character from input stream
            while(i<input.length() && (inp=input.charAt(i) - ' ') > 0){
                token.append(input.charAt(i));
                state = state_table[state][inp];
                i++;
            }
            
            states_string[j] = token.toString();
            System.out.println("\nState: " + state);
 
            //Store Final state
            states[j++] = state-1;


            //reset state
            state = 0;
        }

        //print final states
        print_states(states, accepting_states, states_string);
    };

    static void print_states(int[] states, String[] accepting_states, String[] states_string){
        System.out.println("\nFinal States: ");
        for(int i = 0; states[i] != 0 || states_string[i] != null; i++){
            System.out.println("State: " + accepting_states[states[i]] + " | " + "Input: " + states_string[i]);
        }
    }
};
