package model;

import java.io.PrintStream;

/**
 * @author KyleD
 */
public class RegisterFile {
	
	private int[] myRegisters;
	
	/**
	 * Create an empty registers file
	 */
	public RegisterFile() {
		myRegisters = new int[Register.REGISTER_COUNT];
		for (int i = 0; i < myRegisters.length; i++) 
			myRegisters[i] = 0;
	}

	public int get(Register reg) {
		return myRegisters[reg.getNumber()];
	}
	
	public void set(Register reg, int value) {
		// zero is not allowed to changed.
		if (reg != Register.zero) {
			myRegisters[reg.getNumber()] = value;
		}
	}
}
