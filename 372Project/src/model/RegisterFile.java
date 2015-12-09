package model;

import java.io.PrintStream;
/**
 * @author KyleD
 */
public class RegisterFile {
	
	private int[] myRegisters;
	
	private static final char[] DIGITS = "0123456789ABCDEF".toCharArray();
	
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
	
	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder(((2 + 8 + 4) * Register.REGISTER_COUNT) + 16);
        sb.append("RegisterFile[");
        for (Register reg : Register.values()) {
            sb.append(reg.toString());
            sb.append("=");
            int word = get(reg);
            sb.append(new String(new char[]{
                    DIGITS[(int) (0xf & (word >> 28))],
                    DIGITS[(int) (0xf & (word >> 24))],
                    DIGITS[(int) (0xf & (word >> 20))],
                    DIGITS[(int) (0xf & (word >> 16))],
                    DIGITS[(int) (0xf & (word >> 12))],
                    DIGITS[(int) (0xf & (word >> 8))],
                    DIGITS[(int) (0xf & (word >> 4))],
                    DIGITS[(int) (0xf & word)]}));
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }

    /**
     * Get the contents of the register file. The array has 16 elements
     * @return 
     */
    public int[] registerContents() {
        int[] contents = new int[16];
        for (int i = 0; i < 16; i++) {
            contents[i] = get(Register.valueOfNumber(i));
        }
        return contents;
    }
}
