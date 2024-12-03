package phase3_generator;

import java.io.ByteArrayOutputStream;
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

    public Generator(List<String> atoms) {
        this.atoms = atoms;
        this.flag = false;
        this.pc = 0;
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

    public byte[][] atomsToBinary() {
        result = new byte[atoms.size()][8];
        //#TODO Convert atoms to binary using the machine code instructions from phase 3 file
        for(int i = 0; i < atoms.size(); i++) {
            //Read each atom
            //Split (ADD, test, 10, t0) on commas and leave parenthesis out
            //Remove parenthesis
            String atom = atoms.get(i).replace("(", "").replace(")", "");
            String[] split = atom.split(",");
            for(int j = 0; j < split.length; j++) {
                split[j] = split[j].trim();
            }
            
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            //Find Op Code
            switch (split[0]) {
                case "ADD":
                    //OP CODE
                    stream.write((byte)1);
                    //CMP (none for ADD)
                    stream.write((byte)0);
                    //REGISTER
                    stream.write((byte)1);
                    //MEMORY ADDRESS (Our frontend uses all destinations as t0, t1, t2, etc.)
                    if(split[3].startsWith("t")) {
                        int mem = Integer.parseInt(split[3].substring(1)) + 10000;
                        stream.write((byte)mem);
                    }
                    else {
                        System.out.println("Invalid instruction (ADD)");
                        System.exit(-1);
                    }
                    break;
                case "SUB":
                    //OP CODE
                    stream.write((byte)2);
                    //CMP (none for SUB)
                    stream.write((byte)0);
                    //REGISTER
                    stream.write((byte)1);
                    //MEMORY ADDRESS (Our frontend uses all destinations as t0, t1, t2, etc.)
                    if(split[3].startsWith("t")) {
                        int mem = Integer.parseInt(split[3].substring(1)) + 10000;
                        stream.write((byte)mem);
                    }
                    else {
                        System.out.println("Invalid instruction (SUB)");
                        System.exit(-1);
                    }
                    break;
                case "MUL":
                    //OP CODE
                    stream.write((byte)3);
                    //CMP (none for MUL)
                    stream.write((byte)0);
                    //REGISTER
                    stream.write((byte)1);
                    //MEMORY ADDRESS (Our frontend uses all destinations as t0, t1, t2, etc.)
                    if(split[3].startsWith("t")) {
                        int mem = Integer.parseInt(split[3].substring(1)) + 10000;
                        stream.write((byte)mem);
                    }
                    else {
                        System.out.println("Invalid instruction (MUL)");
                        System.exit(-1);
                    }
                    break;
                case "DIV":
                    //OP CODE
                    stream.write((byte)4);
                    //CMP (none for DIV)
                    stream.write((byte)0);
                    //REGISTER
                    stream.write((byte)1);
                    //MEMORY ADDRESS (Our frontend uses all destinations as t0, t1, t2, etc.)
                    if(split[3].startsWith("t")) {
                        int mem = Integer.parseInt(split[3].substring(1)) + 10000;
                        stream.write((byte)mem);
                    }
                    else {
                        System.out.println("Invalid instruction (DIV)");
                        System.exit(-1);
                    }
                    break;
                case "JMP":
                    if(!flag) break;
                    //OP CODE
                    stream.write((byte)5);
                    //CMP (none for JMP)
                    stream.write((byte)0);
                    //REGISTER (none for JMP)
                    stream.write((byte)0);
                    //MEMORY ADDRESS using Lable, our frontend uses all labels as L0, L1, L2, etc.
                    if(split[5].startsWith("L")) {
                        int mem = Integer.parseInt(split[5].substring(1))+1000;
                        stream.write((byte)0);
                        stream.write((byte)mem);
                    }
                    else {
                        System.out.println("Invalid instruction (JMP)");
                        System.exit(-1);
                    }
                    break;
                case "LBL":
                    //TODO fill in the rest for LBL
                    break;
                case "TST":
                    stream.write((byte)6);
                    //TODO fill in the rest for TST
                    break;
                case "MOV":
                    //STO (7) or LOD (8)
                    stream.write((byte)7);
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
