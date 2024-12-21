import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import phase1_scanner.Scan;
import phase1_scanner.TokenContainer;
import phase2_parser.Parse;


public class RunFront {
    public static void main(String[] args) throws IOException {
        boolean optimize = false;


        if(args.length < 2) {
            System.out.println("Usage: java RunFront <source file> <atoms file> [-o/+o]");
            System.exit(1);
        }

        String srcFile = args[0];
        String atomsFile = args[1];
        //System.out.println(".atom file: " + atomsFile);
        
        if(args.length == 3) {
            String opFlag = args[2].toLowerCase();
            //System.out.println("OPFLAG: " + opFlag);
            switch (opFlag) {
                case "-o" -> optimize = false;
                case "+o" -> optimize = true;
                default -> {
                    System.out.println("Flag not recognized");
                    System.out.println("Continuing without optimization");
                }
            }
        }

        else if (args.length == 2) {
            System.out.println("No optimization flag provided");
            System.out.println("Continuing without optimization");
        }

        else if (args.length > 3) {
            System.out.println("Too many arguments");
            System.exit(1);
        }
        
        //Obtain tokens
        Scan scanner = new Scan();
        TokenContainer tokens = scanner.scan(srcFile);

        //Send tokens to Scanner
        Parse parser = new Parse(tokens.states, tokens.states_string, tokens.length, optimize);
        System.out.println(parser.Statement());
        parser.printAtoms();

        //Generate file containing atoms
        FileWriter writer = new FileWriter(atomsFile);
        List<String> atoms = parser.getAtoms();
        for(String atom : atoms){
            writer.write(atom+"\n");
        }
        writer.close();
    }
}
