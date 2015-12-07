/**
 * 
 */
package model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
