package model;

import java.io.PrintStream;
import java.util.List;

/**
 * @author KyleD
 */
public class Instruction extends EncodedInstruction {
	
	/** Deliminates arguments for display **/
    private static final String ARG_DELIM = ",  ";
	/** Holds the 32 bit memory address of the instruction **/
    protected int address;
    /** Holds the address label for the instruction **/
    protected String label;
    /** Holds the comment string  */
    protected String comment;
    /** for branch instructions, the target address might change **/
    protected String branchTarget;

    public Instruction(Opcode code, Register rx, Register ry, Register rz) {
        super();
        if (code != null) {
            setOpCode(code);
        }
        if (rx != null) {
            setX(rx);
        }
        if (ry != null) {
            setY(ry);
        }
        if (rz != null) {
            setZ(rz);
        }
    }

    public Instruction(Opcode code, Register rx, Register ry) {
        super();
        if (code != null) {
            setOpCode(code);
        }
        if (rx != null) {
            setX(rx);
        }
        if (ry != null) {
            setY(ry);
        }
    }

    public Instruction(Opcode code, Register rx, Register ry, String target) {
        super();
        if (code != null) {
            setOpCode(code);
        }
        if (rx != null) {
            setX(rx);
        }
        if (ry != null) {
            setY(ry);
        }
        this.branchTarget = target;
    }

    public Instruction(Opcode code, Register rx, Register ry, int i) {
        super();
        if (code != null) {
            setOpCode(code);
        }
        if (rx != null) {
            setX(rx);
        }
        if (ry != null) {
            setY(ry);
        }
        setI(i);
    }

    public Instruction(Opcode code) {
        super();
        if (code != null) {
            setOpCode(code);
        }

    }

    /**
     * Create a new instruction 
     * @param label
     * @param address
     * @param cmd
     * @param args
     * @param comment 
     */
    public Instruction(String label, String addy, String cmd, String args, String comment) {
        super(0);
        setLabel(label);
        if (addy != null) {
            int address = 0;
            try {
                address = Integer.decode(addy);
            } catch (NumberFormatException ex) {
                try {
                    // required for 
                    address = Integer.valueOf(addy);
                } catch (Exception ey) {
                    throw new NumberFormatException("Illegal instruction count address: " + addy);
                }
            }
            setAddress(address);
        }
        setOpCode(cmd);
        parseArgsString(args);
        setComment(comment);
    }
    
//    public Instruction(String label, int addy, String cmd, String args, String comment) {
//        super(0);
//        setLabel(label);
//        setAddress(addy);
//        setOpCode(cmd);
//        parseArgsString(args);
//        setComment(comment);
//    }

    /**
     * Empty instruction 
     * @param word 
     */
    public Instruction(int word) {
        super(0);
    }

    /**
     * Empty constructor 
     */
    public Instruction() {
    }

    /**
     * Perform any additional steps required to encode the instruction 
     * @return 1 if linked, 0 otherwise 
     */
    public int encode(Program program, List<EncodedInstruction> linkout, PrintStream out) {
        // Add to linker output
        linkout.add(this);

        if (branchTarget != null) {
        	Instruction target = program.getInstructionByLabel(branchTarget);
            if (target == null) {
                throw new RuntimeException("Assembly Error: No instruction labeled " + branchTarget);
            }
            // Offset from PC+1
            int iValue = intepretIValue(target.getAddress(), address);
//target.getAddress() - (address + 1);
            setI(iValue);
//            out.printf(";### linked %s: %04d -> %04d (%d) \n", branchTarget, getAddress(), target.getAddress(), iValue);
            if (comment == null) {
                comment = String.format("(%d)", iValue);
            }
            return 1;
        }
        return 0;
    }

    protected int intepretIValue(int labelTargetAddress, int address) {
        if (getInstruction() == Opcode.beq) {
            return labelTargetAddress - (address + 1);
        } else {
            return labelTargetAddress;
        }
    }

    /**
     * Set the branch target. This only makes sense for bne and beq
     * @param branchTarget 
     */
    public void setBranchTarget(Instruction branchTarget) {
        this.branchTarget = branchTarget.getLabel();
    }

    /**
     * Get the branch target. This only makes sense for bne and beq
     * @return 
     */
    public String getBranchTargetLabel() {
        return branchTarget;
    }

    /**
     * Get the program address
     * @return 
     */
    public int getAddress() {
        return address;
    }

    /**
     * Set the program address
     * @param address 
     */
    public void setAddress(int address) {
        this.address = address;
    }

    /**
     * Get the comment string
     * @return 
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set the comment string (null clears)
     * @param comment 
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Get the address label 
     * @return label, or null if no label 
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the address label (null clears)
     * @param label 
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the instruction name
     * @return 
     */
    public String getName() {
        return getInstruction().name();
    }

    @Override
    public String toString() {
        // max width is "$zero,  1048575($zero)" = 22 chars (never gonna happen)
//        return String.format("%10s %04x  %-5s %-25s %s", (label == null ? "" : label + ":"), address, getName(), getArgsString(), (comment == null || comment.isEmpty() ? "" : ";" + comment));
        return String.format("%10s %04d  %-5s %-25s %s", (label == null ? "" : label + ":"), address, getName(), getArgsString(), (comment == null || comment.isEmpty() ? "" : ";" + comment));
    }

    /**
     * Get the args string - automatically picks the right one for HW instructions
     * @return 
     */
    public String getArgsString() {
        switch (getInstructionType()) {
            // O type inst
            case 'O':
                return getArgsStringO();
            // J type 
            case 'J':
                return getArgsStringJ();
            // R type insts
            case 'R':
                return getArgsStringR();
            // I type insts
            case 'I':
                return getArgsStringI();
            // I type, lw and sw are special
            case 'W':
                return getArgsStringW();
            // B type
            case 'B':
                return getArgsStringB();
            default:
                return "Unknown opcode: " + getOpCode();
        }
    }

    public void getUsedRegisters(Program program, RegisterFile regs) {
        switch (getInstructionType()) {
            // O type inst
            case 'O':
                return;
            // J type 
            case 'J':
                regs.set(getRegisterX(), 0);
                regs.set(getRegisterY(), -1);
                return;
            // R type insts
            case 'R':
                regs.set(getRegisterX(), 0);
                regs.set(getRegisterY(), -1);
                regs.set(getRegisterZ(), -1);
                return;
            // I type insts
            case 'I':
            case 'W':
                regs.set(getRegisterX(), 0);
                regs.set(getRegisterY(), -1);
                return;
            case 'B':
                regs.set(getRegisterX(), -1);
                regs.set(getRegisterY(), -1);
                return;
            default:
                throw new AssertionError("Unhandled type case");
        }
    }

    protected char getInstructionType() {
        return getInstruction().getType();
    }

    /**
     * Get O type args string
     * @return 
     */
    protected String getArgsStringO() {
        return "";
    }

    /**
     * Get J type args string
     * @return 
     */
    protected String getArgsStringJ() {
        return getRegisterX() + ARG_DELIM + getRegisterY();
    }

    /**
     * Get R type args string
     * @return 
     */
    protected String getArgsStringR() {
        return getRegisterX() + ARG_DELIM + getRegisterY() + ARG_DELIM + getRegisterZ();
    }

    /**
     * Get I type args string
     * @return 
     */
    protected String getArgsStringI() {
        return getRegisterX() + ARG_DELIM + getRegisterY() + ARG_DELIM + getI();
    }

    /**
     * Get W type args string
     * @return 
     */
    protected String getArgsStringW() {
        return getRegisterX() + ARG_DELIM + getI() + "(" + getRegisterY() + ")";
    }

    /**
     * Get B type args string
     * @return 
     */
    protected String getArgsStringB() {
        String bl = getBranchTargetLabel();
        return getRegisterX() + ARG_DELIM + getRegisterY() + ARG_DELIM + (bl == null ? getI() : bl);
    }

    /**
     * Set the op code based on the string alias
     * @param opCodeAlias 
     */
    public void setOpCode(String opCodeAlias) {
        try {
            setOpCode(Opcode.valueOfName(opCodeAlias));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("No such instruction: " + opCodeAlias, ex);
        }
    }

    /**
     * Parses args to an instruction line 
     * @param args 
     */
    protected void parseArgsString(String args) {
        try {
            switch (getInstructionType()) {
                // J type 
                case 'J':
                    parseJargs(args);
                    return;
                // R type insts
                case 'R':
                    parseTypeRargs(args);
                    return;
                // I type insts
                case 'I':
                    parseTypeIargs(args);
                    return;
                // I type, lw and sw are special
                case 'W':
                    parseWargs(args);
                    return;
                case 'B':
                    parseBranchArgs(args);
                    return;
                // O type inst
                case 'O':
                default:
                    return;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error parsing instruction argument \"" + args + "\"", ex);
        }
    }

    /**
     * Parses arguments for J type instructions 
     * @param args 
     */
    protected void parseJargs(String args) {
        String[] parts = args.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("JALR type should have 2 args: " + args);
        }
        Register rx = Register.valueOfName(parts[0].trim());
        Register ry = Register.valueOfName(parts[1].trim());
        setX(rx);
        setY(ry);
    }

    /**
     * Parses arguments for LW/SW instructions
     * @param args 
     */
    protected void parseWargs(String args) {
        String[] parts = args.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("LW/SW type should have 2 args: " + args);
        }
        Register rx = Register.valueOfName(parts[0].trim());
        setX(rx);

        parts = parts[1].trim().split("\\(");
        if (parts.length != 2) {
            throw new IllegalArgumentException("LW/SW syntax: " + args);
        }

        int i = Integer.decode(parts[0]);
        setI(i);
        parts = parts[1].trim().split("\\)");
//        if (parts.length ) {
//            throw new IllegalArgumentException("LW/SW syntax: " + args);
//        }
        Register ry = Register.valueOfName(parts[0].trim());
        setY(ry);
    }

    /**
     * Parses branch instruction arguments 
     * @param args 
     */
    protected void parseBranchArgs(String args) {
        String[] parts = args.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("I type should have 3 args");
        }
        Register rx = Register.valueOfName(parts[0].trim());
        Register ry = Register.valueOfName(parts[1].trim());
        String bt = parts[2].trim();
        try {
            int i = Integer.decode(bt);
            setI(i);
        } catch (Exception ex) {
            branchTarget = parts[2].trim();
//            setI(BTMAGIC);  FIXME check 
        }
        setX(rx);
        setY(ry);
    }

    /**
     * Parses I type args
     * @param args 
     */
    protected void parseTypeIargs(String args) {
        String[] parts = args.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("I type should have 3 args");
        }

        Register rx = Register.valueOfName(parts[0].trim());
        Register ry = Register.valueOfName(parts[1].trim());

        args = parts[2].replaceAll("[\\s]+", "");
        int i = Integer.decode(args);

        setX(rx);
        setY(ry);
        setI(i);
    }

    /**
     * Parse the R type args
     * @param args 
     */
    protected void parseTypeRargs(String args) {
        String[] parts = args.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("R type should have 3 args");
        }

        Register rx = Register.valueOfName(parts[0]);
        Register ry = Register.valueOfName(parts[1]);
        Register rz = Register.valueOfName(parts[2]);

        setX(rx);
        setY(ry);
        setZ(rz);
    }
}
