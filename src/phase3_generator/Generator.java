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
    private int pc;
    private int registerCounter;    
    private byte[][] result;
    private boolean labelFlag;
    private int labelCounter;
    private int instructionCounter;
    private int variableCounter;

    private HashMap<String, Integer> label_map;
    private HashMap<String, Integer> address_map;

    public Generator(List<String> atoms) {
        this.atoms = atoms;
        pc = 100;
        registerCounter = 1;
        label_map = new HashMap<String, Integer>();
        address_map = new HashMap<String, Integer>();
        labelFlag = false;
        labelCounter = 0;
        instructionCounter = 0;
    }

    public void printInstructions() {
        int pcTemp = 100;
        System.out.println("Loc | Instruction");
        System.out.println("------------------");
        for(int i = 0; i < result.length; i++){
            System.out.print(pcTemp + " | ");
            for(int j = 0; j < result[i].length; j++){
                System.out.print(result[i][j]);
            }
            pcTemp+=4;
            System.out.println();
        }
    }

    private byte[] getLoadInstruction(int r, int a){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //OP CODE for STO
        stream.write((byte)7);
        //CMP (none for STO)
        stream.write((byte)0);
        // //REGISTER
        stream.write((byte)r);
        //MEMORY ADDRESS
        stream.write((byte)a);
        return stream.toByteArray();

    }

    private byte[] getStoreInstruction(int r, int a){
        //NOT FINISHED
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //OP CODE for STO
        stream.write((byte)8);
        //CMP (none for STO)
        stream.write((byte)0);
        // //REGISTER
        stream.write((byte)r);
        //MEMORY ADDRESS
        stream.write((byte)a);
        return stream.toByteArray();
    }

    private void writeByteToStream(ByteArrayOutputStream stream, byte b){
        stream.write(b);
        //this.pc+=4;
    }

    private void writeAddress(ByteArrayOutputStream stream, int mem) {
        int num1 = mem/10000;
        int num2 = (mem/1000)%10;
        int num3 = (mem/100)%10;
        int num4 = (mem/10)%10;
        int num5 = mem%10;

        System.out.println("NUMBERS: " + num1 + ", " + num2 + ", " + num3 + ", " + num4 + ", " + num5);

        writeByteToStream(stream, (byte)num1);
        writeByteToStream(stream, (byte)num2);
        writeByteToStream(stream, (byte)num3);    
        writeByteToStream(stream, (byte)num4);
        writeByteToStream(stream, (byte)num5);
    }

    public byte[][] atomsToMachineCode() {
        //Create label table first
        createLabelTable(atoms);
        //Create address table
        createAddressTable(atoms);

        result = new byte[(atoms.size()-labelCounter)][8];

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
                    //(ADD, b, a, <destination>)  --> lod a, r1
                    //                                sto b, mR
                    //                                add r1, mR
                    //                                sto r1, <destination>
                                    //  opp              a                   b              dest
                    System.out.println(split[0] + " " + split[1] + " " + split[2] + " " + split[3]);

                    int reg1 = ++registerCounter;   //register for a
                    // int reg2 = ++registerCounter;   //register for b
                    int memR = -1;

                    //if a or b are constants, they are not yet stored in memory so we need to store them
                    if (split [1].matches("-?\\d+")) {
                        int val = Integer.parseInt(split[1]);
                        //store the constant in the memory
                    }
                    //obtain memory address of a
                    if (address_map.containsKey(split[1])) {
                        int mem = address_map.get(split[1]);
                        for (byte b : getLoadInstruction(reg1, mem)) {
                            stream.write(b);
                        }
                    }
                    else {
                        System.out.println("left operand could not be resolved");
                        System.exit(-1);
                    }

                    if (split [2].matches("-?\\d+")) {
                        //store the constant in the memory
                    }
                    //obtain memory address of b
                    if (address_map.containsKey(split[2])) {
                        memR = address_map.get(split[2]);
                        // for (byte b : getLoadInstruction(reg2, mem)) {
                        //     stream.write(b);
                        // }
                    }
                    else {
                        System.out.println("right operand could not be resolved");
                        System.exit(-1);
                    }
                    
                    //by this point, we have the memory addresses of a and b in registers r1 and r2
                    //we can now add them

                    //OP CODE
                    stream.write((byte)1);
                    //CMP (none for ADD)
                    stream.write((byte)0);
                    //REGISTER
                    stream.write((byte)reg1);

                    //MEM (right operand)
                    stream.write((byte)memR);

                    // writeByteToStream(stream, (byte)registerCounter);    

                    //MEMORY ADDRESS (Our frontend uses all destinations as t0, t1, t2, etc.)
                    if(split[3].startsWith("t")) {
                        int mem = Integer.parseInt(split[3].substring(1)) + 10000;
                        
                        writeAddress(stream, mem);

                        pc+=4;
                        registerCounter+=1;
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
                        // stream.write((byte)mem);

                        writeAddress(stream, mem);

                        pc+=4;
                        registerCounter+=1;
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

                        writeAddress(stream, mem);

                        pc+=4;
                        registerCounter+=1;
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

                        writeAddress(stream, mem);
                        
                        pc+=4;
                        registerCounter+=1;
                    }
                    else {
                        System.out.println("Invalid instruction (DIV)");
                        System.exit(-1);
                    }
                    break;
                case "JMP":
                    //if (!flag) break;
                    //OP CODE
                    stream.write((byte)5);
                    //CMP (none for JMP)
                    stream.write((byte)0);
                    // //REGISTER (none for JMP)
                    stream.write((byte)0);

                    //MEMORY ADDRESS using Lable, our frontend uses all labels as L0, L1, L2, etc.
                    String label = split[5];
                    if(label.startsWith("L")) {
                        if (label_map.containsKey(label)) {     //check if the cooresponding label exists in the table
                            int mem = label_map.get(label);     //get the memory address of the label
                            stream.write((byte)mem);            //write the memory address to the stream
                            
                            pc = label_map.get(label); //pc becomes the memory address of the label from label table because that is the next execution
                            //(GREYSON)Not sure if we need to change pc here, The target device may changes its own pc to accomplish the jump
                            
                        }
                        else {//LABEL ALWAYS IN LABEL TABLE
                            System.out.println("Invalid instruction (JMP)");
                            System.exit(-1);
                        }

                    }
                    else {
                        System.out.println("Invalid instruction (JMP)");
                        System.exit(-1);
                    }
                    break;
                case "LBL":
                    labelFlag = true;
                    break;
                case "TST":
                    // true : 0, == : 1, < : 2, > : 3, <= : 4, >= : 5, != : 6
                    //OP CODE for CMP
                    stream.write((byte)6);
                    //CMP (none for JMP)
                    stream.write((byte)0);
                    // //REGISTER (none for JMP)
                    stream.write((byte)0);
                    
                    //print the atom
                    System.out.println(split[0] + " " + split[1] + " " + split[2] + " " + split[3] + " " + split[4] + " " + split[5]);

                    //resolve label
                    if (split[5].startsWith("L")) {
                        if (label_map.containsKey(split[5])) {
                            int mem = label_map.get(split[5]);
                            stream.write((byte)mem);
                        }
                        else {
                            System.out.println("Label not found in label table (TST)");
                            System.exit(-1);
                        }
                    }
                    else {
                        System.out.println("Invalid Label name (TST)");
                        System.exit(-1);
                    }

                    break;
                case "MOV":
                    //(MOV, <val>, , <dest>) --> lod <val>, r1
                    //                           sto r1, <dest>

                    //OP CODE for STO
                    stream.write((byte)8);
                    //CMP (none for STO)
                    stream.write((byte)0);
                    // //REGISTER
                    stream.write((byte)0);
                    //TODO fill in the rest for LOD
                    break;
                default:
                    System.out.println("Invalid instruction (default)");
                    System.exit(-1);
                    break;
            }

            //Do not want to add empty instructions (LBL case)
            if(labelFlag != true){
                //Convert each atom to binary
                result[instructionCounter] = stream.toByteArray();
                instructionCounter+=1;
                //result[i] = (byte)Integer.parseInt(atoms[i], 2);
            }
            labelFlag = false;
        }
        return result;
    }

    private void createAddressTable(List<String> atoms){
        // int curr_address = 0;    // not sure if this is the correct starting address

        for(int i = 0; i < atoms.size(); i++) {
            String atom = atoms.get(i).replace("(", "").replace(")", "");
            if(atoms.get(i).contains("MOV")) {
                //Split (MOV, 10, , t0) on commas and leave parenthesis out
                //Remove parenthesis
                atom = atoms.get(i).replace("(", "").replace(")", "");
                String[] split = atom.split(",");
                for(int j = 0; j < split.length; j++) {
                    split[j] = split[j].trim();
                }
                //When a MOV atom is encountered enter it in the address table.
                String name = split[3];
                if(address_map.containsKey(name)) {
                    break; // Address is already in table
                }
                // address_map.put(name, curr_address);
                // curr_address+=1;
                address_map.put(name, variableCounter);
                variableCounter+=1;
            }
        }

        //Print address table
        System.out.println("Address Table: ");
        System.out.println("----------------------------------------------");
        for(HashMap.Entry<String, Integer> entry : address_map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("----------------------------------------------");
        System.out.println();

    }

    private void createLabelTable(List<String> atoms) {
        for(int i = 0; i < atoms.size(); i++) {
            if(atoms.get(i).contains("LBL")) {
                //Split (LBL, L0) on commas and leave parenthesis out
                //Remove parenthesis
                String atom = atoms.get(i).replace("(", "").replace(")", "");
                String[] split = atom.split(",");
                for(int j = 0; j < split.length; j++) {
                    split[j] = split[j].trim();
                }
                //When a LBL atom is encountered enter it in the label table.
                String name = split[5];
                if(label_map.containsKey(name)) {
                    break; // Label is already in table
                }
                labelCounter+=1;
                label_map.put(name, pc);
                continue;   //continue so pc isn't incremented. LBL points to the next line, so the next line should have the same line number as current.
                //no need to increment pc since there is no instruction
            }
            pc+=4;
        }
        pc = 100;   //reset pc to 100

        //Print label table
        System.out.println("Label Table: ");
        System.out.println("----------------------------------------------");
        for(HashMap.Entry<String, Integer> entry : label_map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("----------------------------------------------");
        System.out.println();
    }
}
