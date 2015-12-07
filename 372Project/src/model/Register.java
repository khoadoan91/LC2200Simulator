package model;

public enum Register {

    /**
     * 0000 (0) $zero (always zero by hardware)
     */
    zero,
    /**
     * 0001 (1) $at (reserved for assembler)
     */
    at,
    /**
     * 0010 (2) $v0 (return value)
     */
    v0,
    /**
     * 0011 (3) $a0 (argument 0)
     */
    a0,
    /**
     * 0100 (4) $a1 (argument 1)
     */
    a1,
    /**
     * 0101 (5) $a0 (argument 2)
     */
    a2,
    /**
     * 0110 (6) $t0 (temp 0)
     */
    t0,
    /**
     * 0111 (7) $t1 (temp 1)
     */
    t1,
    /**
     * 1000 (8) $t2 (temp 2)
     */
    t2,
    /**
     * 1001 (9) $s0 (saved 0)
     */
    s0,
    /**
     * 1010 (10) $s1 (saved 1)
     */
    s1,
    /**
     * 1011 (11) $s2 (saved 2)
     */
    s2,
    /**
     * 1100 (12) $k0 (reserved for OS traps)
     */
    k0,
    /**
     * 1101 (13) $sp (stack pointer)
     */
    sp,
    /**
     * 1110 (14) $fp (frame pointer)
     */
    fp,
    /**
     * 1111 (15) $ra (return address)
     */
    ra;
    /**
     * Get the number of registers (highest valid register number +1)
     */
    public static final int REGISTER_COUNT = values().length;

    /**
     * Get the number (0-f) 
     * @return 
     */
    public int getNumber() {
        return ordinal();
    }

    /**
     * Look up by code number (0-f)
     * @param number
     * @return 
     */
    public static Register valueOfNumber(int number) {
        return values()[number];
    }

    /**
     * Look up by name 
     * @param name
     * @return 
     */
    public static Register valueOfName(String name) {
        try {
            name = name.trim();
            if (name.startsWith("$")) {
                name = name.substring(1);
            }
            return valueOf(name.trim().toLowerCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("No such register: " + name);
        }
    }

    @Override
    public String toString() {
        return "$" + name();
    }
}