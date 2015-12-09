/**
 * 
 */
package model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @author KyleD
 */
public class SimulatorMain {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		ASMReader reader = new ASMReader();
		InputStream input = new BufferedInputStream(new FileInputStream("exampleWithLabel.txt"));
		try {
			Program program = reader.readAssemblyCode(input);
			for (EncodedInstruction enc : program.getEncodedProgram()) {
                System.out.println(enc);
                System.out.println(enc.toBinaryString());
                System.out.println(enc.toHexString());
            }
			Simulator lc2200 = new Simulator(program.getStartingAddress());
			lc2200.setProgram(program);
			Scanner scan = new Scanner(System.in);
			while (scan.nextInt() == 1) {
				System.out.println(lc2200.getCurrentInstruction());
				lc2200.runProcessor();
			}
			scan.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
