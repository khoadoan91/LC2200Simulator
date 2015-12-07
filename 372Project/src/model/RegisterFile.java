package model;

import java.io.PrintStream;

/**
 * @author KyleD
 */
public interface RegisterFile {
	
	/**
     * Set all registers to 0
     */
    void clear();

    /**
     * Get the contents of the register file. The array has 16 elements
     * @return
     */
    int[] registerContents();

    /**
     * Dump register contents
     * @param out
     */
    void dump(PrintStream out);

    /**
     * Get a register value
     * @param reg
     * @return
     */
    int get(Register reg);

    /**
     * Set a register value
     * @param register destination
     * @param value new value
     * @return the old value
     */
    int set(Register register, int value);
 
    RegisterFile clone();
}
