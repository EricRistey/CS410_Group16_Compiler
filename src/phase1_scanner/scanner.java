package phase1_scanner;
import phase1_scanner.Information;
import java.util.Scanner;

/*************************************************************
 * Group 16 - Scanner
 * Members: Andrew DeGarmo, 
 * 
 * CS410 - Compiler Construction
 * 9/24/2024
 * 
 ************************************************************/
public class scanner {

    public static void main(String[] args) {

        Information info = new Information();
        int[][] state_table = info.st_table;

        //initial state
        int state = 0;

        //Gather input from user
        System.out.println("Enter input stream: ");
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();

        //print input stream
        System.out.println("Input Stream:\n" + input);

        //States list will contain all final states that the state machine reaches
        int states[] = new int[100];

        //current character
        int inp = 0;

        for(int i = 0; i < input.length(); i++){

            //read character from input stream
            inp = input.charAt(i) - '0';
            System.out.println("Current Input: "+inp);

            while (inp >= 0 && inp < i){
                state = state_table[state][inp];
                System.out.println("Current State: "+state);
                inp = input.charAt(i) - '0';
                System.out.println("Next Input: "+inp);
            }

            //Ensure final state is
            if(state == -1){
                System.out.println("Invalid input stream");
                return;
            }

            //Store Final state
            states[i] = state;

            //reset state
            state = 0;
        }

        //print final states
        System.out.println("Final States:");
        for(int i = 0; i < input.length(); i++){
            System.out.println(states[i]);
        }
    };
};
