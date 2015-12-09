package view;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import java.awt.Font;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.BoxLayout;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.table.TableColumn;

import model.ASMReader;
import model.EncodedInstruction;
import model.Instruction;
import model.Program;
import model.Register;
import model.Simulator;

import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;


public class GUI {
	private static final char[] DIGITS = "0123456789ABCDEF".toCharArray();
	private int size = 0;
	private String registersText;
	private final JTextArea registersTextArea = new JTextArea (registersText);
	private String assemblyCodeText = "";
	private final JTextArea assemblyCodeTextArea = new JTextArea (assemblyCodeText);
	private JFrame frame;
	Object[][] rowData = new Object[22000][5];
	private JTextField txtPc;
	private int lastPC;
	private int currentPC = 2000;
	private JFileChooser myChooser;
	private ASMReader myReader;
	private Simulator myLC2200;
	private Program myProgram;
	
	public String assemblyCode;
	public int[] registerData = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	//TODO the array above holds the current data for registers, you can adjust number of registers by adding more
	// (, "null")s I just had 8 registers for initial testing.
	
	

	/**
	 * Launch the application.
	 */	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		myChooser = new JFileChooser(".");
		myReader = new ASMReader();
		initialize();
	
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("LC-2200");
		frame.setBounds(200, 0, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu menu_File = new JMenu("File");
		menu_File.setMnemonic('F');
		menu_File.setFont(new Font("Segoe UI", Font.BOLD, 21));
		menuBar.add(menu_File);
		
		JMenuItem menu_Item_Open = new JMenuItem("Open");
		menu_Item_Open.setMnemonic('O');
		menu_Item_Open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFileChooser();
			}
		});
		menu_File.add(menu_Item_Open);
		
		JMenuItem menu_Item_Quit = new JMenuItem("Quit");
		menu_Item_Quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu_Item_Quit.setMnemonic('Q');
		menu_File.add(menu_Item_Quit);
		
		JMenu menu_About = new JMenu("About");
		menu_About.setFont(new Font("Segoe UI", Font.BOLD, 21));
		menu_About.setMnemonic('A');
		menuBar.add(menu_About);
		
		JMenuItem mntmInfo = new JMenuItem("Info");
		mntmInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 JOptionPane.showMessageDialog (null, "This program simulates the LC-2200, "
				 		+ "taking in instructions in Assmebly Language and performing them.", "About "
				 				+ "this Program", JOptionPane.INFORMATION_MESSAGE);

			}
		});
		menu_About.add(mntmInfo);
		
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {//TODO this is the acionlistener of the next button. Feel free to adjust if you need to, 
														//to get the GUI to display the results of you're back end properly. 
				myLC2200.runProcessor();
				setPC();
				setMemoryText();
				setRegistersText();
				registersTextArea.setText(registersText);
				rowData[currentPC][0] = "->";
				if(currentPC > 2000) rowData[lastPC][0] = "";
				size++; 	
				txtPc.setText("PC = " + currentPC);
				frame.repaint();
			}
		});
		btnNext.setFont(new Font("Tahoma", Font.BOLD, 19));
		btnNext.setHorizontalAlignment(SwingConstants.RIGHT);
		menuBar.add(btnNext);
		
		txtPc = new JTextField();
		txtPc.setFont(new Font("Tahoma", Font.BOLD, 20));
		txtPc.setHorizontalAlignment(SwingConstants.TRAILING);
		txtPc.setEditable(false);
		txtPc.setText("PC = ");
		menuBar.add(txtPc);
		txtPc.setColumns(1);
		
		JPanel content_Panel = new JPanel();
		frame.getContentPane().add(content_Panel, BorderLayout.NORTH);
		content_Panel.setLayout(new BoxLayout(content_Panel, BoxLayout.X_AXIS));

	    content_Panel.setPreferredSize(new Dimension(1400, 1000));
	    
	    JSplitPane splitPane_1 = new JSplitPane();
	    splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    content_Panel.add(splitPane_1);
	    
	    JLabel lblMemory = new JLabel("Memory");
	    lblMemory.setFont(new Font("Tahoma", Font.BOLD, 4));
	    lblMemory.setHorizontalAlignment(SwingConstants.CENTER);

	    Object columnNames[] = { "PC", "Addr", "Data In Binary", "In Hex", "Label"};
	    
	    JTable memoryTable = new JTable(rowData, columnNames);
	    memoryTable.setRowSelectionAllowed(true);
	    memoryTable.setEnabled(false);

	    TableColumn column = null;
	    for (int i = 0; i < 5; i++) {
	        column = memoryTable.getColumnModel().getColumn(i);
	        switch (i) {
	        case 0: column.setMinWidth(50);
	        		column.setMaxWidth(50);
	        		break;
	        case 1: column.setMinWidth(100); 
	        		column.setMaxWidth(100);
	        		break;
	        case 3: column.setMinWidth(150);
	        		column.setMaxWidth(150);
	        		break;
	        case 4: column.setMinWidth(110);
	        		column.setMaxWidth(110);
	        		break;
	        }
	    }   
	    
	    memoryTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 26));
	    memoryTable.setRowHeight(28);
	    memoryTable.setFont(new Font("Tahoma", Font.PLAIN, 20));
	    JScrollPane memoryScroll = new JScrollPane (memoryTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    
	    memoryScroll.setColumnHeaderView(lblMemory);
	    
	    JSplitPane splitPane = new JSplitPane();
		assemblyCodeTextArea.setFont(new Font("Monospaced", Font.PLAIN, 20));

		assemblyCodeTextArea.setEditable(false);
		assemblyCodeTextArea.setLineWrap(true);
		
		
		JScrollPane assemblyCodeScroll = new JScrollPane (assemblyCodeTextArea, 
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		splitPane.setLeftComponent(assemblyCodeScroll);
		
		JLabel lblAssemblyCode = new JLabel("Assembly Code");
		lblAssemblyCode.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblAssemblyCode.setHorizontalAlignment(SwingConstants.CENTER);
		assemblyCodeScroll.setColumnHeaderView(lblAssemblyCode);
		registersTextArea.setFont(new Font("Monospaced", Font.BOLD, 26));

		registersTextArea.setEditable(false);
		registersTextArea.setLineWrap(true);
		
		JScrollPane registersScroll = new JScrollPane (registersTextArea, 
     	JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		registersScroll.setEnabled(false);
		splitPane.setRightComponent(memoryScroll);
	    splitPane_1.setLeftComponent(registersScroll);
	    splitPane_1.setRightComponent(splitPane);
	    splitPane_1.setDividerLocation(300);
	    splitPane_1.setEnabled(false);
	    splitPane.setDividerLocation(500);
	    
		
		JLabel lblRegisters = new JLabel("Registers");
		lblRegisters.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblRegisters.setHorizontalAlignment(SwingConstants.CENTER);
		registersScroll.setColumnHeaderView(lblRegisters);
		panelSetUp();
		frame.pack();
	       
	}
	
	protected void openFileChooser() {
		myChooser.setCurrentDirectory(myChooser.getCurrentDirectory());
        if (myChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = myChooser.getSelectedFile();
            InputStream asmFile;
			try {
				asmFile = new BufferedInputStream(new FileInputStream(file));
	            myProgram = myReader.readAssemblyCode(asmFile);
	    		myLC2200 = new Simulator(myProgram.getStartingAddress());
	            myLC2200.setProgram(myProgram);
	            setMemoryText();
	            setRegistersText();
	            registersTextArea.setText(registersText);
	            setAssemblyCodeText();
	            
	            currentPC = myProgram.getStartingAddress();
	            rowData[currentPC][0] = "->";
				txtPc.setText("PC = " + currentPC);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}

	public void setMemoryText() {//TODO this method will write the memory portion of the GUI. rowData[][1] contains PC
								//rowData[][2] contains memory address, rowData[][3] contains Data rowData[][4] contains Label.
								//The following code in this method was just for my testing but 
		int[][] memory = myLC2200.getInitialMemory().memoryContents();
		Map<Integer, String> labels = new TreeMap<>();
		for (Instruction inst : myProgram.getProgram()) {
			int address = inst.getAddress();
			labels.put(address, inst.getLabel());
		}
		int pc = 0;
		for (int i = 0; i < memory.length; i++) {
			rowData[i][1] = String.format("%04d", pc);
			rowData[i][2] = toBinaryString(memory[i][1]);
			rowData[i][3] = toHexString(memory[i][1]).toUpperCase();
			if (labels.containsKey(i)) {
				rowData[i][4] = labels.get(i);
			}
			pc++;
			
		}
//    	rowData[currentPC - 3000][1] = currentPC;   
//    	rowData[currentPC - 3000][2] = "0000000000000000";
//    	rowData[currentPC - 3000][3] = "null";
//    	rowData[currentPC - 3000][4] = "ADD";	
		//rowData[size][0] = arrowIcon;
	}
	
	public void setRegistersText() {//TODO I created a String array registerData[] change the value of the registers in this method
									//The code currently in this method will print out the information and space it properly, you just
									//have to put the values of the current step into registerData[] in the beginning of this method.
		
		
		int j = 0;
		registersText = "";
		registerData = myLC2200.getCurrentRegs().registerContents();
		for (int i = 0; i < registerData.length; i++) {
			if (j == 4) {
				registersText += "\n";
				j = 0;
			}
			registersText += Register.valueOfNumber(i).toString() + " = "  + registerData[i];
			
			for (int space = 0; space < 10 - (String.valueOf(registerData[i]).length() + Register.valueOfNumber(i).toString().length()) ; space++) {
				registersText = registersText + " ";
			}
		
			j++;
		}
		
	}
	
	public void setAssemblyCodeText() {//TODO Set "assemblyCode" = to the assembly code of current step and it will append onto gui.
		for (EncodedInstruction inst : myProgram.getEncodedProgram()) {
			assemblyCode = inst.toString();
			assemblyCodeTextArea.append(assemblyCode + "\n");
			
		}
	}
	
	public void setPC() {//TODO set currentPC = to next PC after instruction.
		lastPC = currentPC;
		currentPC = myLC2200.getCurrentPC();
	}
	
	private void panelSetUp() {
		int pc = 0;
		for (int i = 0; i < rowData.length; i++) {
			rowData[i][1] =  String.format("%04d", pc);
			rowData[i][2] = "00000000000000000000000000000000";
			rowData[i][3] = "x00000000";
			pc++;
			
		}
	}
	
	private String toBinaryString(int number) {
		char[] buff = new char[32];
        for (int i = 0; i < 32; i++) {
            buff[i] = (((number >> (31 - i)) & 0x1) == 0) ? '0' : '1';
        }
        return new String(buff);
	}

	public String toHexString(int number) {
    	return new String(new char[]{
                DIGITS[(int) (0xf & (number >> 28))],
                DIGITS[(int) (0xf & (number >> 24))],
                DIGITS[(int) (0xf & (number >> 20))],
                DIGITS[(int) (0xf & (number >> 16))],
                DIGITS[(int) (0xf & (number >> 12))],
                DIGITS[(int) (0xf & (number >> 8))],
                DIGITS[(int) (0xf & (number >> 4))],
                DIGITS[(int) (0xf & number)]});
    }
}