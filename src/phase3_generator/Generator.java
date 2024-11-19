package phase3_generator;

public class Generator {
    
    private byte[] atomsToBinary(String[] atoms) {
        byte[] result = new byte[atoms.length];
        //#TODO Convert atoms to binary using the machine code instructions from phase 3 file
        for(int i = 0; i < atoms.length; i++) {
            result[i] = (byte)Integer.parseInt(atoms[i], 2);
        }
        return result;
    }
}
