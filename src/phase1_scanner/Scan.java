package phase1_scanner;
import java.io.IOException;
import java.util.Scanner;
import phase2_parser.Parse;
import phase3_generator.Generator;

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

    //constructor

    //fields
    private StateTable info;
    private int[][] state_table;
    private String[] accepting_states;
    private int state;

    public Scan(){
        //Create StateTable object
        info = new StateTable();
        state_table = info.st_table;
        accepting_states = info.acceptingStates_string;

        //Initial state
        state = 0;

    }

    public TokenContainer scan(String file_path){
        String input = read_file(file_path);
        return tokenize(input);
    }

    public TokenContainer scan(){
        /****USING THE TERMINAL****/
        System.out.println("Enter input stream: ");
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        scan.close();
        return tokenize(input);
    }

    public TokenContainer tokenize(String input){

        System.out.println("_________________________________________________________________");
        System.out.println("Scanning");
        System.out.println("_________________________________________________________________");

        //States list will contain all final states that the state machine reaches
        int states[] = new int[200];
        String states_string[] = new String[200];

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

        return new TokenContainer(states, states_string, j);

    }

    public static void main(String[] args) {

        //Create StateTable object
        StateTable info = new StateTable();
        int[][] state_table = info.st_table;
        String[] accepting_states = info.acceptingStates_string;

        //Initial state
        int state = 0;

        //Gather input from user
        /****USING THE Testing.txt FILE: Just type in Testing.txt when running for the contents to be read****/
        String input = read_file();
        
        /****USING THE TERMINAL****/
        //System.out.println("Enter input stream: ");
        //Scanner scan = new Scanner(System.in);
        //String input = scan.nextLine();
        //scan.close();

        //States list will contain all final states that the state machine reaches
        int states[] = new int[200];
        String states_string[] = new String[200];

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

        //print final statesif
        print_states(states, accepting_states, states_string);
        System.out.println("_________________________________________________________________");
        System.out.println("PARSING");
        System.out.println("_________________________________________________________________");
        //Create new Parse object
        Parse parse = new Parse(states, states_string, j, false);

        //Run the parser and print result of statement (ACCEPT or REJECT)
        String parseResult = parse.Statement();
        System.out.println(parseResult);

        //Print the atoms
        //Examples:
        //Complex Expression : test=(test+10)*(test-10);                                                     
        //If :  if(4+5<10*2){}
        //else if :  if(x<y){}elseif(a<b){}
        //else :  if(x<y){}else{}
        //For :  for(int x=5;x<10;x=x+1){}
        //While :  while(x<y){}
        if(!"REJECT".equals(parseResult)){
            parse.printAtoms();
        }
        else{
            return;
        }

        //Generator
        System.out.println("_________________________________________________________________");
        System.out.println("Generating");
        System.out.println("_________________________________________________________________");
        Generator gen = new Generator(parse.getAtoms(), false);
        gen.atomsToMachineCode("test.mc");
        gen.printInstructions();
    };

    // Method to print final states
    static void print_states(int[] states, String[] accepting_states, String[] states_string){
        System.out.println("\nFinal States: ");
        for(int i = 0; states[i] != 0 || states_string[i] != null; i++){
            System.out.println("State: " + accepting_states[states[i]] + " | " 
            + "Input: " + states_string[i] + " | " + "Encoding: " + states[i]);
        }
    }

    String read_file(String fileName){
        try{
            //Open file
            java.io.File file1 = new java.io.File(fileName);
            if(!file1.exists()){
                System.out.println("File does not exist.");
            }
            else{
                
                //Read file
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file1));
                StringBuilder fileContents = new StringBuilder();
                try {
                    String line = br.readLine();
                    while(line != null){
                        System.out.println(line);
                        fileContents.append(line);
                        line = br.readLine();
                    }
                } finally {
                    br.close();
                }
                System.out.println("FILE CONTENTS:\n" + fileContents.toString());
                return fileContents.toString();
            }
        }
        catch(Exception e){
            System.out.println("File does not exist." + e);
        }

        return "";
    }

    static String read_file(){
        Scanner file = new Scanner(System.in);
        while(true){
            System.out.println("Enter input file name: ");
            String fileName = file.nextLine();
            //file.close();
            try{
                //Open file
                java.io.File file1 = new java.io.File(fileName);
                if(!file1.exists()){
                    System.out.println("File does not exist.");
                    
                }
                else{
                    
                    //Read file
                    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file1));
                    StringBuilder fileContents = new StringBuilder();
                    try {
                        String line = br.readLine();
                        while(line != null){
                            System.out.println(line);
                            fileContents.append(line);
                            line = br.readLine();
                        }
                    } finally {
                        br.close();
                    }
                    System.out.println("FILE CONTENTS:\n" + fileContents.toString());
                    file.close();
                    return fileContents.toString();
                }
            }
            catch(IOException e){
                System.out.println("File does not exist." + e);
            }
        }
        
    }
};