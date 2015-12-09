package model;

public class Simulator {
	private int myStartingAddress;
	private Processor myProcessor;
	private RegisterFile myInitialRegs;
	private Memory myInitialMemory;
	
	public Simulator(int startingAddr) {
		myInitialRegs = new RegisterFile();
		myInitialMemory = new Memory();
		myStartingAddress = startingAddr;
	}
	
	public void setProgram(Program pro) {
		for (Instruction inst : pro.getProgram()) {
			int index = inst.getAddress();
			myInitialMemory.storeDataTo(index, inst.word);
		}
		myProcessor = new Processor(pro.getEncodedProgram(), myInitialRegs, myInitialMemory);
	}
	
	public EncodedInstruction getCurrentInstruction() {
		return myProcessor.getCurrentInstruction();
	}
	
	public RegisterFile getInitialRegs() {
		return myInitialRegs;
	}
	
	public Memory getInitialMemory() {
		return myInitialMemory;
	}
	
	public RegisterFile getCurrentRegs() {
		return myProcessor.getRegisters();
	}
	
	public void runProcessor() {
		myProcessor.runNext();
	}
	
	public int getCurrentPC() {
		return myProcessor.getPC() + myStartingAddress;
	}
}
