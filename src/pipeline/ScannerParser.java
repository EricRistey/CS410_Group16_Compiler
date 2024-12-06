package pipeline;
import java.util.Scanner;
import phase1_scanner.Scan;
import phase1_scanner.TokenContainer;
import phase2_parser.Parse;
import phase3_generator.Generator;

public class ScannerParser {

    public static void main(String[] args) {
        Scan scanner = new Scan();
        Scanner file = new Scanner(System.in);
        String fileName = file.nextLine();
        TokenContainer tokens = scanner.scan("src/pipeline/test.c");
        file.close();
        //Create new Parse object
        Parse parse = new Parse(tokens.states, tokens.states_string, tokens.length);

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

        if(parseResult != "REJECT"){
            parse.printAtoms();
            System.out.println();
        }

        //Generator
        Generator gen = new Generator(parse.getAtoms());
        gen.atomsToMachineCode();
        gen.printInstructions();
    }
}
