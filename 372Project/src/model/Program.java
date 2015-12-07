package model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author KyleD
 */
public class Program {
	int programCntr = 0;
    List<EncodedInstruction> encodedProgram;
    LinkedHashMap<String, Instruction> program;
    int ulCount = 0;
    
    public Program(int address) {
    	programCntr = address;
    }
    
    public void setProgramCntr(int address) {
    	programCntr = address;
    }
    
    /**
     * Add an instruction manually to the program
     * @param inst 
     */
    public void addInstruction(Instruction inst) {
        if (encodedProgram != null) {
            throw new AssertionError("Trying to add instructions after program has been linked");
        }

        if (program == null) {
            program = new LinkedHashMap<>(128);
            encodedProgram = null;
        }

        inst.setAddress(programCntr);
        String key = inst.getLabel();
        if (key == null) {
            key = "_pc" + programCntr; // illegal label, could never interfere with a real program
        }
        if ((program.put(key, inst)) != null) {
            throw new RuntimeException("Duplicate label: " + key);
        }
        programCntr++;
    }

    @SuppressWarnings("unchecked")
	public List<Instruction> getProgram() {
        return program == null ? Collections.EMPTY_LIST : new ArrayList<Instruction>(program.values());
    }

    @SuppressWarnings("unchecked")
	public List<EncodedInstruction> getEncodedProgram() {
        return encodedProgram == null ? Collections.EMPTY_LIST : new ArrayList<EncodedInstruction>(encodedProgram);
    }

    public int size() {
        return programCntr - 1;
    }

    public Instruction getInstructionByLabel(String label) {
        return program.get(label);
    }

    public int assembleProgram(PrintStream linkerOutput) {
        if (linkerOutput == null) {
        	throw new IllegalArgumentException();
//            linkerOutput = new PrintStream(new NullStream());
        }
        if (program == null) {
            throw new AssertionError("Assembling an empty program");
        }
        int counter = 0;

        List<EncodedInstruction> out = new ArrayList<EncodedInstruction>(programCntr);
        for (Instruction inst : program.values()) {
            counter += inst.encode(this, out, linkerOutput);
        }
        encodedProgram = out;
        return counter;
    }
}
