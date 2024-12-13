import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import phase3_generator.Generator;
//import java.io.FileWriter;


public class RunBack {
    public static void main(String[] args) throws IOException {
        boolean optimize = false;
        List<String> atoms = new ArrayList<>();

        //For debugging
        String atomsFile = "";
        String mcFileName = "";
        if (args.length < 2) {
            // System.out.println("Usage: java RunBack <atomsFile> <mcFileName> [-o/+o]");
            // System.exit(1);

            //scanner
            Scanner scanner = new Scanner(System.in);
            System.out.println(".atom file: ");
            atomsFile = scanner.nextLine();
            System.out.println("MC file: ");
            mcFileName = scanner.nextLine();
            System.out.println("Optimize? (-o/+o): ");
            String opFlag = scanner.nextLine();
            System.out.println("OPFLAG: " + opFlag);
            if(opFlag.equals("-o")) {
                optimize = false;
            }
            else if(opFlag.equals("+o")) {
                optimize = true;
            }


        }
        else{
            atomsFile = args[0];
            mcFileName = args[1];
            
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
        }

        //Obtain List of atoms
        atoms = readFile(atomsFile);

        //Send to backend Generator & generate binary file
        Generator gen = new Generator(atoms, optimize);
        gen.atomsToMachineCode(mcFileName);
        gen.printInstructions();
        gen.printMnemonics();
    }

    private static List<String> readFile(String file){
        List<String> atoms = new ArrayList<>();
        try{
            File obj = new File (file);
            Scanner reader = new Scanner(obj);
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                atoms.add(line);
                System.out.println(line);
            }
            reader.close();
        } catch (FileNotFoundException e){
            System.out.println("Fie not found");
            e.printStackTrace();
        }
        return atoms;
    }
}
