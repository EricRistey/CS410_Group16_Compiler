import java.io.IOException;

public class RunBack {
    public static void main(String[] args) throws IOException {
        String atoms = args[0];
        String mcFileName = args[1];
        
        if(args.length > 2) {
            String opFlag = args[2];
            System.out.println("OPFLAG: " + opFlag);
        }
        System.out.println("ATOMS: " + atoms);
        System.out.println("MC FILE NAME: " + mcFileName);

        //File file = new File(fileName);
        
        //FileWriter writer = new FileWriter(file);
        //writer.write("Hello World!");
        //writer.close();
    }
}
