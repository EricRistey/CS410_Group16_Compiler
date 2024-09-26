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
 *  integer numbers(ex. 10 read as an IDENTIFIER)
 *  floating point numbers (ex. 12.5 read as INVALID)
 *  comments (i.e. // read as INVALID)
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
        int state = 1;

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
            while(i<input.length() && state != -1){
                //read character from input stream
                inp = input.charAt(i) - ' ';
                token.append(input.charAt(i));
                state = state_table[state][inp];
                i++;
            }
            
            states_string[j] = token.toString();

            //Ensure final state is
            if(state == -1){
                states[j++] = 0;
            }
            else{
                //Store Final state
                states[j++] = state;
            }

            //reset state
            state = 1;
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
