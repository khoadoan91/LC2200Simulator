package model;

public class Memory {
	
	private int[] myMemory; 
	private int size;
	// key is address and value is data
//	private Map<Integer, Integer> myMemory;
	public final static int MAX_MEMORY = 20000;
	
	public Memory() {
		this(new int[MAX_MEMORY]);
	}
	
	public Memory(int[] memory) {
		myMemory = memory;
	}

	public int getDataFrom(int address) {
		return myMemory[address];
	}
	
	public void storeDataTo(int address, int value) {
		myMemory[address] = value;
	}
	
	public int size() {
		return size;
	}
	
	public void clear() {
		for(int i = 0; i < myMemory.length; i++) {
			myMemory[i] = 0;
		}
	}
	
	/**
     * Get a int[n][2] where n is the number of words stored in memory. The 
     * format is [[address,value],[address,value],[address,value]]
     * @return 
     */
    public int[][] memoryContents() {
        int[][] rv = new int[myMemory.length][2];
        for (int i = 0; i < myMemory.length; i++) {
            rv[i][0] = i;
            rv[i][1] = myMemory[i];
        }
        return rv;
    }
}
