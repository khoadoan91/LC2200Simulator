package model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KyleD
 */
public class Program {
	private int startingAddr;
	private int programCntr = 0;
    private List<EncodedInstruction> encodedProgram;
    private Map<String, Instruction> program;
    
    public Program(int address) {
    	programCntr = address;
    }
    
    public void setProgramCntr(int address) {
    	startingAddr = address;
    	programCntr = address;
    }
    
    public int getStartingAddress() {
    	return startingAddr;
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
