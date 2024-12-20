package phase3_generator;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 
     *  STO memory[s2] <- fpreg[r1]: 8
     *  HLT: 9
     * 
     *  (ADD, test, 10, t0) =>
     *      lw $t0, test(ex. 101)------700000101
     *      li $t1, 10(ex. 102)  ------700100102
     *      add $t3, $t0, $t1    ------10010000
     *      sw $t3, t0(ex. 103)  ------800300103
     * 
     *  (SUB, 1, 0, t0) =>
     *      li $t0, 1(ex. 101)------700000101
     *      li $t1, 0(ex. 102)------700100102
     *      sub $t3, $t0, $t1 ------20010000
     *      sw $t3, t0(ex. 103)------800300103
     * 
     *  (MUL, t0, t1, t2) => 
     *      lw $t0, t0(ex. 101)------700000101
     *      lw $t1, t1(ex. 102)------700100102
     *      mul $t3, $t0, $t1  ------30010002
     *      sw $t3, t2(ex. 103)------800300103
     * 
     *  (DIV, t0, t1, t2) => 
     *      lw $t0, t0(ex. 101)------700000101
     *      lw $t1, t1(ex. 102)------700100102
     *      div $t3, $t0, $t1 ------40010002
     *      sw $t3, t2(ex. 103)------800300103
     * 
     *  (JMP, , , , , L1) => 
     *      jmp L1(ex. 104)
     * 
     *  (LBL,,,,,L0) =>
     *      L0(ex. 116)
     * 
     *  (TST, 11, t0, , 4, L0) => 
     *      blt $t0, 4, L0(ex. 100) ------6000
     * 
     *  (MOV, t1, , i)
     *      lw $t1, i(ex. 10000)
     *      sw $t1, i
     * 
     */
    private List<String> atoms;
    private int pc;    
    private byte[][] result;
    private int instructionCounter;
    private int variableCounter;
    private int registerCounter;
    private int litCount;

    private boolean optimize;
    private HashMap<String, Integer> label_map;
    private HashMap<String, Integer> address_map;
    private HashMap<Integer, Integer> lit_map;

    public Generator(List<String> atoms, boolean optimize) {
        this.atoms = atoms;
        this.pc = 1;
        this.registerCounter = 0;
        this.instructionCounter = 0;
        this.variableCounter = 2000;
        this.litCount = 1000;

        this.optimize = optimize;

        this.label_map = new HashMap<String, Integer>();
        this.address_map = new HashMap<String, Integer>();
        this.lit_map = new HashMap<Integer, Integer>();

        //Create label table
        int size = createLabelTable(atoms);
        //+1 accounts for the HLT instruction
        result = new byte[size+1][8];

    }

    private void writeBinaryFile(byte[][] result, HashMap<Integer, Integer> data, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename)){

            //Index Header, must be integer 1
            fos.write(0x00);//1 byte
            fos.write(0x00);//1 byte
            fos.write(0x00);//1 byte
            fos.write(0x01);//1 byte


            //Code Section
            for (byte[] result1 : result) {
                //Take pair and write it to file
                //op & cmp
                byte[] res = new byte[4];
                res[0] = (byte) ((result1[0] & 0x0F) << 4 | (result1[1] & 0x0F));

                //combine memory
                byte[] mem = new byte[5];
                String num = "";
                for (int i = 0; i < 5; i++) {
                    String binary = "";
                    int number = result1[i + 3] & 0xFF;
                    num = num + number;
                }

                int number = Integer.parseInt(num, 10);

                //Now we have the entire number.
                //convert int to hex representation
                
                BigInteger bigInt = BigInteger.valueOf(number);
                byte[] bytes = bigInt.toByteArray();
                int count = 5;//determine leading zeros
                for (byte b : bytes) {
                    int unsignedByte = b & 0xFF;//get unsigned byte to determine leading zeros
                    if(unsignedByte > 0xF){
                        //there is two hexadecimal numbers
                        count -= 2;
                    } else {
                        //there is one hexadecimal number
                        count -= 1;
                    }
                }

                if(count <= 0){
                    //register and 4 of memory
                    res[1] = (byte) ((result1[2] & 0x0F) << 4 | (bytes[0] << 4 & 0x0F));//CHanged
                    //5th and 6th
                    res[2] = (byte) (bytes[1]);
                    //7th and 8th
                    res[3] = (byte) (bytes[2]);
                }
                else if(count == 1){
                    //register and 4 of memory
                    res[1] = (byte) ((result1[2] & 0x0F) << 4);
                    //5th and 6th
                    res[2] = (byte) (bytes[0]);
                    //7th and 8th
                    res[3] = (byte) (bytes[1]);
                }
                else if(count == 2){
                    //register and 4 of memory
                    res[1] = (byte) ((result1[2] & 0x0F) << 4);
                    //5th and 6th
                    res[2] = (byte) (bytes[0]);
                    //7th and 8th
                    res[3] = (byte) (bytes[1]);
                }
                else if(count == 3){
                    //register and 4 of memory
                    res[1] = (byte) ((result1[2] & 0x0F) << 4);
                    //5th and 6th
                    res[2] = (byte) (0x00);
                    //7th and 8th
                    res[3] = (byte) (bytes[0]);
                }
                else if(count == 4){
                    //register and 4 of memory
                    res[1] = (byte) ((result1[2] & 0x0F) << 4);
                    //5th and 6th
                    res[2] = (byte) (0x00);
                    //7th and 8th
                    res[3] = (byte) (bytes[0]);
                }
                
                //System.out.println("COUNT: " + count);
                    //mem[i] = (byte) (number & 0xFF);                

                //System.out.println("BYTES: " + Arrays.toString(bytes));

                //System.out.println("MEM: " + Arrays.toString(mem));
                

                //register and 4 of memory
                //res[1] = (byte) ((result1[2] & 0x0F) << 4 | (result1[3] & 0x0F));
                //5th and 6th
                //res[2] = (byte) ((result1[4] & 0x0F) << 4 | (result1[5] & 0x0F));
                //7th and 8th
                //res[3] = (byte) ((result1[6] & 0x0F) << 4 | (result1[7] & 0x0F));
                
                fos.write(res);
            }

            //Data Section
            for(Map.Entry<Integer, Integer> entry: data.entrySet()){
                //32 bit integer
                BigInteger bigInt = BigInteger.valueOf(entry.getValue());
                byte[] bytes = bigInt.toByteArray();

                byte[] padding = new byte[4];
                System.arraycopy(bytes, 0, padding, 4-bytes.length, bytes.length);
                fos.write(padding);
                //fos.write((entry.getValue().byteValue() >> 24) & 0xFF);
                //fos.write((entry.getValue().byteValue() >> 16) & 0xFF);
                //fos.write((entry.getValue().byteValue() >> 8) & 0xFF);
                //fos.write(entry.getValue().byteValue() & 0xFF);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int readAddress(byte[] bytes) {
        int num1 = bytes[0] * 10000;
        int num2 = bytes[1] * 1000;
        int num3 = bytes[2] * 100;
        int num4 = bytes[3] * 10;
        int num5 = bytes[4];
    
        return num1 + num2 + num3 + num4 + num5;
    }

    public void printMnemonics(){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < result.length; i++) {

            boolean line_is_jump = false;

            if(result[i] == null){
                continue;
            }

            sb.append((i+1) + " | ");

            byte[] bytes = result[i];

            byte op = bytes[0];
            byte cmp = bytes[1];
            byte reg = bytes[2];
            byte[] mem = new byte[5];

            for (int j = 0; j < 5; j++) {
                mem[j] = bytes[j + 3];
            }
            
            int mem_int = readAddress(mem);

            switch (op) {
                case 0:
                    sb.append("CLR ");
                    break;

                case 1:
                    sb.append("ADD ");
                    break;
                case 2:
                    sb.append("SUB ");
                    break;
                case 3:
                    sb.append("MUL ");
                    break;
                case 4:
                    sb.append("DIV ");
                    break;
                
                case 5:
                    sb.append("JMP ");
                    line_is_jump = true;
                    break;
                
                case 6:
                    sb.append("CMP ");
                    break;
                
                case 7:
                    sb.append("LOD ");
                    break;

                case 8:
                    sb.append("STO ");
                    break;

                case 9:
                    sb.append("HLT ");
                    break;

            }

            switch (cmp) {
                case 0:
                    sb.append(" ");
                    break;
                case 1:
                    sb.append("EQ ");
                    break;
                case 2:
                    sb.append("LT ");
                    break;
                case 3:
                    sb.append("GT ");
                    break;
                case 4:
                    sb.append("LE ");
                    break;
                case 5:
                    sb.append("GE ");
                    break;
                case 6:
                    sb.append("NE ");
                    break;
                default:
                    break;
            }

            sb.append("$" + reg + ", ");

            //match the addresses to labels/idetifiers/literals if possible
            boolean found = false;      //there likely a way to do this without a flag.

            if (line_is_jump) {
                for(HashMap.Entry<String, Integer> entry : label_map.entrySet()) {
                    if(entry.getValue() == mem_int) {
                        sb.append(entry.getKey() + "\n");
                        found = true;
                        break;
                    }
                }
            }

            //search the address table
            if (!found) {
                for(HashMap.Entry<String, Integer> entry : address_map.entrySet()) {
                    if(entry.getValue() == mem_int) {
                        sb.append(entry.getKey() + "\n");
                        found = true;
                        break;
                    }
                }
            }

            //if not found in address table, search the literals table
            if (!found) {
                for(HashMap.Entry<Integer, Integer> entry : lit_map.entrySet()) {
                    if(entry.getKey() == mem_int) {
                        sb.append(entry.getValue() + "\n");
                        found = true;
                        break;
                    }
                }
            }
            
            //if not found in literals table, print the memory address
            if (!found && mem_int != 0) {   //if the memory address is 0 it likely doesnt need to be printed
                sb.append("#" + mem_int + "\n"); 
            }

            if (mem_int == 0) {
                sb.append("\n");
            }
        }

        System.out.println(sb.toString());
    }

    public void printInstructions() {
        int pcTemp = 1;
        System.out.println("\nLoc | Instruction");
        System.out.println("------------------");
        for(int i = 0; i < result.length; i++){

            System.out.print(pcTemp + " | ");

            if(result[i] == null){
                pcTemp+=1;
                System.out.println("");
                continue;
            }
            for(int j = 0; j < result[i].length; j++){

                System.out.print(result[i][j]);
            }

            pcTemp+=1;
            System.out.println();
        }

        
        //Print address table
        System.out.println("\nAddress Table: {Identifier} : {Address}");
        System.out.println("----------------------------------------------");
        for(HashMap.Entry<String, Integer> entry : address_map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("----------------------------------------------");
        System.out.println();

        //Print literals table
        System.out.println("\nLiterals Table: {Value} : {Address}");
        System.out.println("----------------------------------------------");
        for(HashMap.Entry<Integer, Integer> entry : lit_map.entrySet()) {
            // System.out.println(entry.getKey() + " : " + entry.getValue());
            System.out.println(entry.getValue() + " : " + entry.getKey());
        }
        System.out.println("----------------------------------------------");
        System.out.println();

    }

    private void writeLoadInstruction(int r, int a){
        writeInstruction(7, 0, r, a);

    }

    private void writeStoreInstruction(int r, String name){

        //Check to see if variable exists. If so, save STO register to existing variable. If not, create new variable and STO.
        if (address_map.containsKey(name)) {
            writeInstruction(8, 0, r, address_map.get(name));
        }
        else{ 
            address_map.put(name, variableCounter);
            variableCounter+=1;
            writeInstruction(8, 0, r, variableCounter-1);
        }

        //Reset register counter
        registerCounter-=1;
    }

    private void writeByteToStream(ByteArrayOutputStream stream, byte b){
        stream.write(b);
    }

    private void writeAddress(ByteArrayOutputStream stream, int mem) {
        int num1 = mem/10000;
        int num2 = (mem/1000)%10;
        int num3 = (mem/100)%10;
        int num4 = (mem/10)%10;
        int num5 = mem%10;

        writeByteToStream(stream, (byte)num1);
        writeByteToStream(stream, (byte)num2);
        writeByteToStream(stream, (byte)num3);    
        writeByteToStream(stream, (byte)num4);
        writeByteToStream(stream, (byte)num5);
    }

    private int checkConst(String s) {
        if (s.matches("-?\\d+")) {
            int val = Integer.parseInt(s);
            //System.out.println("CONST\nLitCount: " + litCount + " VAL: " + val + " Atom: " + s);

            //Check to see if literal already contains address
            if(!lit_map.containsValue(val)){
                lit_map.put(litCount++, val);
            }
            //variableCounter+=1;
            //store the constant in the memory
        }

        else{
            if (address_map.containsKey(s)) {
                int addr = address_map.get(s);
                lit_map.put(addr, 0);
                //System.out.println("inADDRMAP \nLitCount: " + 0 + " VAL: " + addr + " Atom: " + s);
                return addr;
            }
            else{   //if the value is not a constant (variable) and not in the address table, reserve a memory address for it containing 0
                //System.out.println("LitCount: " + litCount + " VAL: " + 0 + " Atom: " + s);
                lit_map.put(litCount++, 0);    //if the value is not a constant (variable), reserve a memory address for it containing 0
                //variableCounter+=1;
            }
        }

        return litCount-1;//variableCounter-1
    }
    
    private void writeInstruction(int op, int cmp, int reg, int mem) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //OP CODE
        //ensure the op code is 4bits
        if (op > 15) {
            System.out.println("Op code out of range");
            System.exit(-1);
        }
        //ensure the cmp is a 0 + 3 bit number
        if (cmp > 6) {
            System.out.println("CMP out of range");
            System.exit(-1);
        }
        //ensure the register is a 4 bit number
        // if (reg > 15) {
        //     System.out.print("Register out of range : (");
        //     System.out.println(op + " " + cmp + " " + reg + " " + mem + ")");
        //     System.exit(-1);
        // }
        //ensure the memory address is a 20 bit number
        if (mem > 1048575) {
            System.out.println("memory address out of range");
            System.exit(-1);
        }

        stream.write((byte)op);
        //CMP
        stream.write((byte)cmp);
        //REGISTER
        stream.write((byte)reg);
        //MEMORY ADDRESS
        writeAddress(stream, mem);

        result[instructionCounter++] = stream.toByteArray();
        pc+=1;
    }

    private byte[] makeInstruction(int op, int cmp, int reg, int mem){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //OP CODE
        //ensure the op code is 4bits
        if (op > 15) {
            System.out.println("Op code out of range");
            System.exit(-1);
        }
        //ensure the cmp is a 0 + 3 bit number
        if (cmp > 6) {
            System.out.println("CMP out of range");
            System.exit(-1);
        }

        //ensure the memory address is a 20 bit number
        if (mem > 1048575) {
            System.out.println("memory address out of range");
            System.exit(-1);
        }

        stream.write((byte)op);
        //CMP
        stream.write((byte)cmp);
        //REGISTER
        stream.write((byte)reg);
        //MEMORY ADDRESS
        writeAddress(stream, mem);

        return stream.toByteArray();
    }

    private void arithmeticInstruction(int op, String[] split){
        //System.out.println(split[0] + " " + split[1] + " " + split[2] + " " + split[3]);

        int reg1 = ++registerCounter;   //register for a

        //if a or b are constants, they are not yet stored in memory so we need to store them
        int mem = checkConst(split[1]);
        //obtain memory address of a
        if (address_map.containsKey(split[1])) {
            mem = address_map.get(split[1]);
            writeLoadInstruction(reg1, mem);
        }
        else if (lit_map.containsKey(mem)) {
            writeLoadInstruction(reg1, mem);
        }
        else {
            System.out.println("left operand could not be resolved");
            System.exit(-1);
        }
        
        int memR = checkConst(split[2]);

        //obtain memory address of b. do not have to write load instruction since we're using the memory address directly.
        if (address_map.containsKey(split[2])) {
            memR = address_map.get(split[2]);
        }
        else if (lit_map.containsKey(memR)) {
            //Do nothing
        }
        else {
            System.out.println("right operand could not be resolved");
            System.exit(-1);
        }

        switch(op){
            case 1://ADD
                writeInstruction(1,0, reg1, memR);
                break;
            case 2://SUB
                writeInstruction(2,0, reg1, memR);
                break;
            case 3://MUL
                writeInstruction(3,0, reg1, memR);
                break;
            case 4://DIV
                writeInstruction(4,0, reg1, memR);
                break;
        }

        //STORE
        writeStoreInstruction(registerCounter, split[3]);

    }
    
    public byte[][] atomsToMachineCode(String filename) {
        //append the literals at the end of the file

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
                    arithmeticInstruction(1, split);
                    break;
                case "SUB":
                    /*
                    int op = 2;
                    int cmp = 0;
                    int reg = 1;
                    mem = 11111;
                    
                    writeInstruction(op, cmp, reg, mem);
                    */

                    arithmeticInstruction(2, split);

                    break;
                case "MUL":
                    /*
                    op = 3;
                    cmp = 0;
                    reg = 1;
                    mem = 11111;

                    writeInstruction(op, cmp, reg, mem);
                    */

                    arithmeticInstruction(3, split);
                    break;
                case "DIV":

                    /*
                    op = 4;
                    cmp = 0;
                    reg = 1;
                    mem = 11111;

                    writeInstruction(op, cmp, reg, mem);
                    */

                    arithmeticInstruction(4, split);
                    break;
                case "JMP":

                    int op = 5;
                    int cmp = 0;
                    int reg = 0;
                    
                    int mem = 0;
                    //MEMORY ADDRESS using Lable, our frontend uses all labels as L0, L1, L2, etc.
                    String label = split[5];
                    if(label.startsWith("L")) {
                        if (label_map.containsKey(label)) {     //check if the cooresponding label exists in the table
                            mem = label_map.get(label);     //get the memory address of the label
                            // stream.write((byte)mem);            //write the memory address to the stream
                            
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
                    writeInstruction(op, cmp, reg, mem);

                    break;
                case "LBL":
                    //writeInstruction(0,0,0,0);
                    //result[instructionCounter++] = null;
                    break;
                case "TST":
                    // // true : 0, == : 1, < : 2, > : 3, <= : 4, >= : 5, != : 6
                    
                    //print the atom
                    //System.out.println(split[0] + " " + split[1] + " " + split[2] + " " + split[3] + " " + split[4] + " " + split[5]);

                    //if value is a constant, store it in memory
                    int addr1 = checkConst(split[1]);
                    int addr2 = checkConst(split[2]);

                    //load the first value into a register
                    writeLoadInstruction(++registerCounter, addr1);


                    op = 6;
                    cmp = Integer.parseInt(split[4]);
                    reg = registerCounter;
                    mem = addr2;

                    writeInstruction(op, cmp, reg, mem);

                    //jump to the label if the condition is true
                    //OP CODE
                    op = 5;

                    mem = 0;
                    //resolve label
                    if (split[5].startsWith("L")) {
                        if (label_map.containsKey(split[5])) {
                            mem = label_map.get(split[5]);
                            //writeAddress(stream, mem);
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

                    writeInstruction(op, 0, 0, mem);

                    //Reset register counter
                    registerCounter-=1;

                    break;
                case "MOV":
                    //(MOV, <val>, , <dest>) --> lod <val>, r1
                    //                           sto r1, <dest>
                    
                    String value = split[1];
                    String name = split[3];

                    //if value is a constant, store it in memory
                    int addr = checkConst(value);
                    
                    writeLoadInstruction(++registerCounter, addr);
                    writeStoreInstruction(registerCounter, name);

                    break;
                default:
                    System.out.println("Invalid instruction (default)");
                    System.exit(-1);
                    break;
            }
        }

        //Halt
        writeInstruction(9, 0, 0, 0);
        System.out.println("OPTIMIZE: " + optimize);
        if (optimize) {
            localOptimization();
        }

        writeBinaryFile(result, lit_map, filename);

        //append data on to the end of result (literals)
        return result;

    }

    private void localOptimization(){
        //load store optimization
        //remove redundant load and store instructions

        int num_removed = 1;

        System.out.println("Optimizing...");

        byte[] prevMem = new byte[5];
        byte prevReg = 0;

        for (int i = 0; i < result.length; i++) {

            if(result[i] == null){
                continue;
            }

            byte[] bytes = result[i];

            byte op = bytes[0];
            byte cmp = bytes[1];
            byte reg = bytes[2];
            byte[] mem = new byte[5];

            for (int j = 0; j < 5; j++) {
                mem[j] = bytes[j + 3];
            }
            
            int mem_int = readAddress(mem);

            if (op == 7) {  //load instruction
                if (prevReg == reg && prevMem[0] == mem[0] && prevMem[1] == mem[1] && prevMem[2] == mem[2] && prevMem[3] == mem[3] && prevMem[4] == mem[4]) {
                    //remove the previous store instruction
                    result[i-1] = null;
                    //remove the current load instruction
                    result[i] = null;

                    num_removed+=2;
                }
            }

            if (op == 8) {  //store instruction
                if (prevReg == reg && prevMem[0] == mem[0] && prevMem[1] == mem[1] && prevMem[2] == mem[2] && prevMem[3] == mem[3] && prevMem[4] == mem[4]) {
                    //remove the previous load instruction
                    result[i-1] = null;
                    //remove the current store instruction
                    result[i] = null;

                    num_removed+=2;
                }
            }

            //remove aritmetic of identity elements
            if (op == 1 || op == 2) {//ADD or SUB
                //search for mem_int in literals table
                if (lit_map.containsKey(mem_int)) {
                    if (lit_map.get(mem_int) == 0) {
                        //remove the current instruction
                        result[i] = null;

                        num_removed+=1;
                    }
                }
            }

            if (op == 3 || op == 4) {//MUL or DIV
                //search for mem_int in literals table
                if (lit_map.containsKey(mem_int)) {
                    if (lit_map.get(mem_int) == 1) {
                        //remove the current instruction
                        result[i] = null;

                        num_removed+=1;
                    }
                }
            }

            if (op == 5) {//JMP 
                continue;           
                //adjust the address in the jump instruction
                //adjust the label table
                /*
                System.out.println("FIXING JUMP INSTRUCTION: " + i);

                //find the label table entry that matches the memory address
                for(HashMap.Entry<String, Integer> entry : label_map.entrySet()) {
                    if(entry.getValue() == mem_int) {
                        //adjust the memory address
                        label_map.put(entry.getKey(), i-num_removed);
                        break;
                    }
                }

                //overwrite the jump instruction with the new memory address
                result[i] = makeInstruction(op, cmp, reg, i-num_removed);
                */
            }

            prevReg = reg;
            prevMem = mem;

        }

        result = removeNulls(result);

        //Print new label table
        System.out.println("\nLabel Table (POST-OPTIMIZATION): ");
        System.out.println("----------------------------------------------");
        for(HashMap.Entry<String, Integer> entry : label_map.entrySet()) {
            System.out.print(entry.getKey() + ": ");
            System.out.printf("%05d\n", entry.getValue());
        }
        System.out.println("----------------------------------------------");
        System.out.println();
    }
        
    private byte[][] removeNulls(byte[][] result2) {
        
        //count non nulls
        int count = 0;
        for (int i = 0; i < result2.length; i++) {
            if (result2[i] != null) {
                count++;
            }
        }

        //create new array with only non nulls
        byte[][] result3 = new byte[count][8];
        count = 0;
        for (int i = 0; i < result2.length; i++) {
            if (result2[i] != null) {
                result3[count] = result2[i];
                count++;
            }
        }

        return result3;

    }
        
    private int createLabelTable(List<String> atoms) {
        //Gather size for storing machine code
        int size = 0;
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
                
                label_map.put(name, pc);
                //pc+=1;
                //size+=1;
                //continue;   //continue so pc isn't incremented. LBL points to the next line, so the next line should have the same line number as current.
                //no need to increment pc since there is no instruction
            }
            else if(atoms.get(i).contains("ADD") || atoms.get(i).contains("SUB") || atoms.get(i).contains("MUL") || atoms.get(i).contains("DIV")) {
                pc+=3;
                size+=3;
            }
            else if(atoms.get(i).contains("MOV")) {
                pc+=2;
                size+=2;
            }
            else if(atoms.get(i).contains("TST")) {
                pc+=3;
                size+=3;
            }
            else if(atoms.get(i).contains("JMP")) {
                pc+=1;
                size+=1;
            } 
        }

        pc = 1;   //reset pc to 100

        //Print label table
        System.out.println("\nLabel Table (PRE-OPTIMIZATION): ");
        System.out.println("----------------------------------------------");
        for(HashMap.Entry<String, Integer> entry : label_map.entrySet()) {
            System.out.print(entry.getKey() + ": ");
            System.out.printf("%05d\n", entry.getValue());
        }
        System.out.println("----------------------------------------------");
        System.out.println();
        return size;
    }
}
