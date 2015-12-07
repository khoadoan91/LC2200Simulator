package model;

public enum Opcode {
	add('R'),
    nand('R'),
    addi('I'),
    lw('W'),
    sw('W'),
    beq('B'),
    jalr('J'),
    halt('O');
	
    private final char type;
    
    
    private Opcode(char type){
        this.type = type;
    }

    /**
     * Get the instruction type
     * @return 
     */
    public char getType() {
        return type;
    }
    
    /**
     * Get the opcode integer value
     * @return 
     */
    public int getCode(){
        return ordinal();
    }
    
    /**
     * Look up by code number (0-D)
     * @param opcode
     * @return 
     */
    public static Opcode valueOfCode(int opcode){   
        return values()[opcode];
    }
    
    /**
     * Look up by name 
     * @param name
     * @return 
     */
    public static Opcode valueOfName(String name){
        name = name.trim().toLowerCase();
        // Bad documentation
        if("jal".equals(name)){
            name = "jalr";
        }
        return valueOf(name);
    }
}
