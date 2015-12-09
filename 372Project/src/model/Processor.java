package model;

import java.util.List;

public class Processor {
	private int myPC;
	private EncodedInstruction myCurrentInst;
	private List<EncodedInstruction> myProgram;
	private Memory myMemory;
	private RegisterFile myRegs;
	
	public Processor(List<EncodedInstruction> theProgram, RegisterFile theRegs, Memory theMemory) {
		myPC = 0;
		this.myProgram = theProgram;
		myMemory = theMemory;
		myRegs = theRegs;
	}
	
	public boolean runNext() {
		if (myPC < 0) return false;
		if (myPC >= myProgram.size()) {
			myPC = -1;
			return false;
		}
		myCurrentInst = myProgram.get(myPC++);
		execute(myCurrentInst);
		return true;
	}
	
	public int getPC() {
		return myPC;
	}
	
	public EncodedInstruction getCurrentInstruction() {
		return myCurrentInst;
	}
	
	public int[] getRegisterContents() {
        return myRegs.registerContents();
    }
	
	public int[][] getMemoryContents() {
        return myMemory.memoryContents();
    }
	
	public Memory getMemory() {
        return myMemory;
    }
	
	public RegisterFile getRegisters() {
		return myRegs;
	}
	
	public void execute(EncodedInstruction inst) {
		switch(inst.getInstruction()) {
		case add: add(inst); break;
		case nand: nand(inst); break;
		case addi: addi(inst); break;
		case lw: lw(inst); break;
		case sw: sw(inst); break;
		case beq: beq(inst); break;
		case jalr: jalr(inst); break;
		default:
			break;
		}
	}

	private void jalr(EncodedInstruction inst) {
		Register rx = inst.getRegisterX();
		Register ry = inst.getRegisterY();
		int x = myRegs.get(rx);
		myRegs.set(ry, myPC);
		myPC = x;
	}

	private void beq(EncodedInstruction inst) {
		Register rx = inst.getRegisterX();
		Register ry = inst.getRegisterY();
		int x = myRegs.get(rx);
		int y = myRegs.get(ry);
		int i = inst.getI();
		if (x == y) myPC = myPC + i;
	}

	private void sw(EncodedInstruction inst) {
		Register rx = inst.getRegisterX();
		Register ry = inst.getRegisterY();
		int x = myRegs.get(rx);
		int y = myRegs.get(ry);
		int i = inst.getI();
		int address = y + i;
		myMemory.storeDataTo(address, x);
	}

	private void lw(EncodedInstruction inst) {
		Register rx = inst.getRegisterX();
		Register ry = inst.getRegisterY();
		int y = myRegs.get(ry);
		int i = inst.getI();
		int address = y + i;
		int value = myMemory.getDataFrom(address);
		myRegs.set(rx, value);
	}

	private void addi(EncodedInstruction inst) {
		Register rx = inst.getRegisterX();
		Register ry = inst.getRegisterY();
		int y = myRegs.get(ry);
		int i = inst.getI();
		int x = y + i;
		myRegs.set(rx, x);
	}

	private void nand(EncodedInstruction inst) {
		Register rx = inst.getRegisterX();
		Register ry = inst.getRegisterY();
		Register rz = inst.getRegisterZ();
		int y = myRegs.get(ry);
		int z = myRegs.get(rz);
		int x = ~(y & z);
		myRegs.set(rx, x);
	}

	private void add(EncodedInstruction inst) {
		Register rx = inst.getRegisterX();
		Register ry = inst.getRegisterY();
		Register rz = inst.getRegisterZ();
		int y = myRegs.get(ry);
		int z = myRegs.get(rz);
		int x = y + z;
		myRegs.set(rx, x);
	}
}
