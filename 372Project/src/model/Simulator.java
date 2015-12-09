package model;

public class Simulator {
	private int myStartingAddress;
	private Processor myProcessor;
	
	public Simulator(int startingAddr) {
		myStartingAddress = startingAddr;
	}
	
	public void setProgram(Program pro) {
		myProcessor = new Processor(myStartingAddress, pro.getEncodedProgram());
	}
	
	public void runNext() {
		myProcessor.runNext();
	}
	
	public EncodedInstruction getCurrentInstruction() {
		return myProcessor.getCurrentInstruction();
	}
}
