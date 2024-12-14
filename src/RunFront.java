import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import phase1_scanner.Scan;
import phase1_scanner.TokenContainer;
import phase2_parser.Parse;


public class RunFront {
    public static void main(String[] args) throws IOException {
        boolean optimize = false;

        String srcFile = args[0];
        String atomsFile = args[1];
        
        if(args.length > 2) {
            String opFlag = args[2];
            System.out.println("OPFLAG: " + opFlag);
            if(opFlag.equals("-o")) {
                optimize = false;
            }
            else if(opFlag.equals("+o")) {
                optimize = true;
            }
        }

        //Obtain tokens
        Scan scanner = new Scan();
        TokenContainer tokens = scanner.scan(srcFile);

        //Send tokens to Scanner
        Parse parser = new Parse(tokens.states, tokens.states_string, tokens.length, optimize);
        System.out.println(parser.Statement());

        //Generate file containing atoms
        FileWriter writer = new FileWriter(atomsFile);
        List<String> atoms = parser.getAtoms();
        for(String atom : atoms){
            writer.write(atom+"\n");
        }
        writer.close();
    }
}
