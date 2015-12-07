package model;

/**
 * @author KyleD
 */
public class EncodedInstruction {
	
	private static final char[] DIGITS = "0123456789ABCDEF".toCharArray();
	
	/** Holds the 32 bit machine language LC-2200 instruction **/
    protected int word;

    /**
     * Create an instruction with a 0 address 
     * @param word 
     */
    public EncodedInstruction(int word) {
        this.word = word;
    }

    public EncodedInstruction() {
        this(0);
    }

    /**
     * LC-2200 OpCode is the first 4-bit field 
     * @return the 4 bit opcode
     */
    public int getOpCode() {
        return (word >> 28) & 0xf;
    }

    /**
     * LC-2200 OpCode is the first 4-bit field 
     * @param opcode the 4 bit opcode
     */
    public void setOpCode(int opcode) {
        word = (word & 0x0fffffff) | ((opcode & 0xf) << 28);
    }

        /**
     * LC-2200 OpCode is the first 4-bit field 
     * @param opcode the 4 bit opcode
     */
    public void setOpCode(Opcode opcode){
        setOpCode(opcode.getCode());
    }
    
    /**
     * LC-2200 X register is the second 4 bit field
     * @return the 4 bit x register
     */
    public int getX() {
        return (word >> 24) & 0xf;
    }

    /**
     * LC-2200 X register is the second 4 bit field
     * @param x 4 bit register
     */
    public void setX(int x) {
        word = (word & 0xf0ffffff) | ((x & 0xf) << 24);
    }

    /**
     * LC-2200 X register is the second 4 bit field
     * @param x 4 bit register
     */
    public void setX(Register x) {
        setX(x.getNumber());
    }

    /**
     * LC-2200 Y register is the third 4 bit field
     * @return the 4 bit y register
     */
    public int getY() {
        return (word >> 20) & 0xf;
    }

    /**
     * LC-2200 Y register is the third 4 bit field
     * @param y 4 bit register
     */
    public void setY(int y) {
        word = (word & 0xff0fffff) | ((y & 0xf) << 20);
    }

    /**
     * LC-2200 Y register is the second 4 bit field
     * @param y 4 bit register
     */
    public void setY(Register y) {
        setY(y.getNumber());
    }

    /**
     * LC-2200 z register is the last 4 bit field
     * @return the 4 bit z register
     */
    public int getZ() {
        return word & 0xf;
    }

    /**
     * LC-2200 z register is the last 4 bit field
     * @param z 4 bit register
     */
    public void setZ(int z) {
        word = (word & 0xfffffff0) | (z & 0xf);
    }

    /**
     * LC-2200 z register is the last 4 bit field
     * @param z 4 bit register
     */
    public void setZ(Register z) {
        setZ(z.getNumber());
    }

    /**
     * LC-2200 I type instructions have an I value which is the last 20 bits 
     * of the instruction
     * @return the I value (will also return the Z value for R type instructions)
     */
    public int getI() {
        // preserve 2's complement 
        return ((word & 0x000fffff) << 12) >> 12;
    }

    /**
     * LC-2200 I type instructions have an I value which is the last 20 bits 
     * of the instruction
     * @param i value of the instruction 
     */
    public void setI(int i) {
        word = (word & 0xfff00000) | (i & 0xfffff);
    }

    /**
     * Get the 32 bit machine language instruction 
     * @return 
     */
    public int getWord() {
        return word;
    }

    /**
     * Set the 32 bit machine language instruction 
     * @param word 
     */
    public void setWord(int word) {
        this.word = word;
    }

    public String toString() {
        return toHexString();
    }

    public String toHexString() {
    	return new String(new char[]{
                DIGITS[(int) (0xf & (word >> 28))],
                DIGITS[(int) (0xf & (word >> 24))],
                DIGITS[(int) (0xf & (word >> 20))],
                DIGITS[(int) (0xf & (word >> 16))],
                DIGITS[(int) (0xf & (word >> 12))],
                DIGITS[(int) (0xf & (word >> 8))],
                DIGITS[(int) (0xf & (word >> 4))],
                DIGITS[(int) (0xf & word)]});
    }

    public String toBinaryString() {
    	char[] buff = new char[32];
        for (int i = 0; i < 32; i++) {
            buff[i] = (((word >> (31 - i)) & 0x1) == 0) ? '0' : '1';
        }
        return new String(buff);
    }

    public byte[] getBytes() {
        return new byte[]{
                    (byte) (word >> 24),
                    (byte) (word >> 16),
                    (byte) (word >> 8),
                    (byte) (word)};

    }


    /**
     * Get the X register
     * @return 
     */
    public Register getRegisterX() {
        return Register.valueOfNumber(getX());
    }

    /**
     * Get the Y register
     * @return 
     */
    public Register getRegisterY() {
        return Register.valueOfNumber(getY());
    }

    /**
     * Get the Z register
     * @return 
     */
    public Register getRegisterZ() {
        return Register.valueOfNumber(getZ());
    }
    
    
    /**
     * Get the Enum opCode
     * @return 
     */
    public Opcode getInstruction() {
        return Opcode.valueOfCode(getOpCode());
    }
}
