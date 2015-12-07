package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ASMReader {
	
	private static final Pattern LINEREGEX =
            Pattern.compile("^(?:[\\s]*)"     
            + "(?:([\\w]+):)?(?:[\\s]*)" // address label 
            + "([a-zA-Z]{2,5})?(?:[\\s]*)" // instruction 
            + "([^\\;\\#]+)?" // registers
            + "(?:[\\;\\#]+(.*))?");                 // comments
	
	public Program readAssemblyCode(InputStream input) throws IOException {
		boolean startProgram = false;
		Program result = new Program(3000);
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(input));
		String line;
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty() || line.startsWith(";")) {
				continue;
			}
			// handle the beginning of the program and the value of the label.
			if (line.contains(".")) {
				int i = line.indexOf(".");
				String blkw = line.substring(i + 1);   // get rid the dot "."
				if (blkw.toUpperCase().startsWith("ORIG")) {
					String[] str = line.split(" ");  
					int addStart = Integer.decode(str[1]);
					result.setProgramCntr(addStart);
					startProgram = true;
					continue;
				} else if (blkw.toUpperCase().startsWith("END")) {
					// TODO end program, do something
					startProgram = false;
				} else if (blkw.toUpperCase().startsWith("FILL")) {
					if(!startProgram) {
						int space = line.indexOf(" ");
						String label = line.substring(0, space);
						// TODO save the label to the program.
					} else {
						throw new IllegalArgumentException("The value should be outside the program.");
					}
				}
			}
			if (startProgram) {
				Instruction inst = parseInstruction(line);
				result.addInstruction(inst);
			}
		}
		
		result.assembleProgram(System.out);
		
		if (result.size() != 0)
			return result;
		else return null;
	}

	private Instruction parseInstruction(String line) {
		Matcher matcher = LINEREGEX.matcher(line);
		if (!matcher.find()) {
			throw new IllegalArgumentException(line);
		}
		String code[] = new String[matcher.groupCount()];
		for (int i = 0; i < matcher.groupCount(); i++) {
			code[i] = matcher.group(i+1);
		}
		// FIXME fix the instructor constructor that takes 4 parameters
		return new Instruction(code[0], null, code[1], code[2], code[3]);
	}
}
