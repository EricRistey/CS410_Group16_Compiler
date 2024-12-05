package phase3_generator;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class Generator {
    /*
     * Absolute Code
     *  32 bits
     *  4 bits (0-3): Opcode
     *  1 + 3 bits (4-7): 0 + cmp (0 to 6)
     *  4 bits (8-11): r
     *  20 bits (12-31): a
     * 
     * Assume T0 starts at memory cell 10000 and so on
     * Assume L0 starts at memory cell 01000 and so on
     * Assume registers start at 0
     * 
     *  CLR fpreg[r1] <- 0 : 0
     *  ADD fpreg[r1] <- fpreg[r1] + memory[s2]: 1
     *  SUB fpreg[r1] <- fpreg[r1] - memory[s2]: 2
     *  MUL fpreg[r1] <- fpreg[r1] * memory[s2]: 3
     *  DIV fpreg[r1] <- fpreg[r1] / memory[s2]: 4
     *  JMP PC <- s2 if flag is true: 5
     *  CMP flag <- r1 cmp memory[s2]: 6
     *  LOD fpreg[r1] <- memory[s2]: 7
     *  STO memory[s2] <- fpreg[r1]: 8
     *  HLT: 9
     * 
     *  (ADD, test, 10, t0) => 10010000
     *  (SUB, 1, 0, t0) => 20010000
     *  (MUL, t0, t1, t2) => 30010002
     *  (DIV, t0, t1, t2) => 40010002
     *  (JMP, , , , , L1) => 
     *  (TST, 11, t0, , 4, L0) => 
     */
    private List<String> atoms;
    private boolean flag;
    private int pc;
    private byte[][] result;

    private HashMap<String, Integer> label_map;
    private HashMap<String, Integer> fixup_map;
    private HashMap<String, Integer> index_map;

    public Generator(List<String> atoms) {
        this.atoms = atoms;
        this.flag = false;
        this.pc = 100;
        this.label_map = new HashMap<String, Integer>();
        this.fixup_map = new HashMap<String, Integer>();
        this.index_map = new HashMap<String, Integer>();
    }

    public void printInstructions() {
        for(int i = 0; i < result.length; i++){
            System.out.print(i + ". ");
            for(int j = 0; j < result[i].length; j++){
                System.out.print(result[i][j]);
            }
            System.out.println();
        }
    }

    private void writeByteToStream(ByteArrayOutputStream stream, byte b){
        stream.write(b);
        //this.pc+=4;
    }

    public byte[][] atomsToBinary() {
        result = new byte[atoms.size()][8];

        //#TODO Convert atoms to binary using the machine code instructions from phase 3 file
        for(int i = 0; i < atoms.size(); i++) {

            //If LBL is in fix_up table, and not in the label table, then skip instruction
            if(!fixup_map.isEmpty()){
                //Get certain Label
                int index = atoms.lastIndexOf(atoms.get(i));
                if(index != -1) {
                    String label = atoms.get(i).substring(index-3, index-1);
                    if(fixup_map.containsKey(label) && !label_map.containsKey(label)) {
                        continue;
                    }
                }
            }


            //Read each atom
            //Split (ADD, test, 10, t0) on commas and leave parenthesis out
            //Remove parenthesis
            String atom = atoms.get(i).replace("(", "").replace(")", "");
            String[] split = atom.split(",");
            for(int j = 0; j < split.length; j++) {
                split[j] = split[j].trim();
                System.out.print(split[j]);
            }
            System.out.println();
           
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            //Find Op Code
            switch (split[0]) {
                case "ADD":
                    //OP CODE
                    // stream.write((byte)1);
                    writeByteToStream(stream, (byte)1);
                    //CMP (none for ADD)
                    // stream.write((byte)0);
                    writeByteToStream(stream, (byte)0);
                    //REGISTER
                    // stream.write((byte)1);
                    writeByteToStream(stream, (byte)1);    

                    //MEMORY ADDRESS (Our frontend uses all destinations as t0, t1, t2, etc.)
                    if(split[3].startsWith("t")) {
                        int mem = Integer.parseInt(split[3].substring(1)) + 10000;
                        // stream.write((byte)mem);
                        writeByteToStream(stream, (byte)mem);
                        pc+=4;
                    }
                    else {
                        System.out.println("Invalid instruction (ADD)");
                        System.exit(-1);
                    }
                    break;
                case "SUB":
                    //OP CODE
                    // stream.write((byte)2);
                    writeByteToStream(stream, (byte)2);
                    //CMP (none for SUB)
                    // stream.write((byte)0);
                    writeByteToStream(stream, (byte)0);
                    //REGISTER
                    // stream.write((byte)1);
                    writeByteToStream(stream, (byte)1);
                    //MEMORY ADDRESS (Our frontend uses all destinations as t0, t1, t2, etc.)
                    if(split[3].startsWith("t")) {
                        int mem = Integer.parseInt(split[3].substring(1)) + 10000;
                        // stream.write((byte)mem);
                        writeByteToStream(stream, (byte)mem);
                        pc+=4;
                    }
                    else {
                        System.out.println("Invalid instruction (SUB)");
                        System.exit(-1);
                    }
                    break;
                case "MUL":
                    //OP CODE
                    // stream.write((byte)3);
                    writeByteToStream(stream, (byte)3);
                    //CMP (none for MUL)
                    // stream.write((byte)0);
                    writeByteToStream(stream, (byte)0);
                    //REGISTER
                    // stream.write((byte)1);
                    writeByteToStream(stream, (byte)1);
                    //MEMORY ADDRESS (Our frontend uses all destinations as t0, t1, t2, etc.)
                    if(split[3].startsWith("t")) {
                        int mem = Integer.parseInt(split[3].substring(1)) + 10000;
                        // stream.write((byte)mem);
                        writeByteToStream(stream, (byte)mem);
                        pc+=4;
                    }
                    else {
                        System.out.println("Invalid instruction (MUL)");
                        System.exit(-1);
                    }
                    break;
                case "DIV":
                    //OP CODE
                    // stream.write((byte)4);
                    writeByteToStream(stream, (byte)4);
                    //CMP (none for DIV)
                    // stream.write((byte)0);
                    writeByteToStream(stream, (byte)0);
                    //REGISTER
                    // stream.write((byte)1);
                    writeByteToStream(stream, (byte)1);
                    //MEMORY ADDRESS (Our frontend uses all destinations as t0, t1, t2, etc.)
                    if(split[3].startsWith("t")) {
                        int mem = Integer.parseInt(split[3].substring(1)) + 10000;
                        // stream.write((byte)mem);
                        writeByteToStream(stream, (byte)mem);
                        pc+=4;
                    }
                    else {
                        System.out.println("Invalid instruction (DIV)");
                        System.exit(-1);
                    }
                    break;
                case "JMP":
                    //OP CODE
                    // stream.write((byte)5);
                    writeByteToStream(stream, (byte)5);
                    //CMP (none for JMP)
                    // stream.write((byte)0);
                    writeByteToStream(stream, (byte)0);
                    // //REGISTER (none for JMP)
                    // stream.write((byte)0);
                    writeByteToStream(stream, (byte)0);
                    //MEMORY ADDRESS using Lable, our frontend uses all labels as L0, L1, L2, etc.
                    String label = split[5];
                    if(label.startsWith("L")) {
                        if (label_map.containsKey(label)) {     //check if the cooresponding label exists in the table
                            int mem = label_map.get(label);     //get the memory address of the label
                            writeByteToStream(stream, (byte)mem);
                            
                            pc = fixup_map.get(label); //pc becomes the memory address of the label from fixup table because that is the next execution
                            i = index_map.get(label)-1; //Change i to get the instructions from LBL and on to execute again minus 1 because for loop increments after each iteration
                        }
                        else {
                            fixup_map.put(label, pc);       //if the label does not exist, add it to the fixup table
                            writeByteToStream(stream, (byte)0);
                        }

                    }
                    else {
                        System.out.println("Invalid instruction (JMP)");
                        System.exit(-1);
                    }
                    break;
                case "LBL":
                    //When a LBL atom is encountered enter it in the label table.
                    String name = split[5];
                    if(label_map.containsKey(name)) {
                        break; // Label is already in table
                    }
                    label_map.put(name, pc);
                    index_map.put(name, i); // Note the index of the label to use for jumping to instructions
                    //no need to increment pc since there is no instruction
                    break;
                case "TST":
                    // true : 0, == : 1, < : 2, > : 3, <= : 4, >= : 5, != : 6
                    //OP CODE for CMP
                    // stream.write((byte)6);
                    writeByteToStream(stream, (byte)6);
                    //CMP (none for JMP)
                    // stream.write((byte)0);
                    writeByteToStream(stream, (byte)0);
                    // //REGISTER (none for JMP)
                    // stream.write((byte)0);
                    writeByteToStream(stream, (byte)0);
                    //TODO fill in the rest for TST
                    break;
                case "MOV":
                    //STO (7) or LOD (8)
                    // stream.write((byte)7);
                    writeByteToStream(stream, (byte)7);
                    //TODO fill in the rest for LOD
                    break;
                default:
                    System.out.println("Invalid instruction (default)");
                    System.exit(-1);
                    break;
            }
            //Convert each atom to binary
            result[i] = stream.toByteArray();
            //result[i] = (byte)Integer.parseInt(atoms[i], 2);
            
        }
        return result;
    }
}
