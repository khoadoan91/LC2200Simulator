package model;

import java.util.Map;
import java.util.TreeMap;

public class Memory {
	// key is address and value is data
	private Map<Integer, Integer> myMemory;
	
	public Memory() {
		this(new TreeMap<>());
	}
	
	public Memory(Map<Integer, Integer> memory) {
		myMemory = memory;
	}

	public int getDataFrom(int address) {
		return myMemory.get(address);
	}
	
	public void storeDataTo(int address, int value) {
		myMemory.put(address, value);
	}
}
