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

import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;


public class GUI {
	private int size = 0;
	private String registersText;
	private final JTextArea registersTextArea = new JTextArea (registersText);
	private String assemblyCodeText = "";
	private final JTextArea assemblyCodeTextArea = new JTextArea (assemblyCodeText);
	private JFrame frame;
	Object[][] rowData = new Object[1000][5];
	private JTextField txtPc;
	private int lastPC;
	private int currentPC = 3000;
	private JFileChooser myChooser;
	private ASMReader myReader;
	
	public String assemblyCode;
	public String[] registerData = {"null", "null", "null", "null", "null", "null", "null", "null"};
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
				setMemoryText();
				setRegistersText();
				registersTextArea.setText(registersText);
				assemblyCodeTextArea.setText(assemblyCodeText);
				rowData[currentPC - 3000][0] = "->";
				if(currentPC > 3000) rowData[lastPC - 3000][0] = "";
				size++; 	
				setPC();
				txtPc.setText("PC = " + currentPC);
				frame.repaint();
			}
		});
		btnNext.setFont(new Font("Tahoma", Font.BOLD, 19));
		btnNext.setHorizontalAlignment(SwingConstants.RIGHT);
		menuBar.add(btnNext);
		
		txtPc = new JTextField();
		txtPc.setFont(new Font("Tahoma", Font.BOLD, 20));
		txtPc.setText("PC = " + (3000 + size));
		txtPc.setHorizontalAlignment(SwingConstants.TRAILING);
		txtPc.setEditable(false);
		menuBar.add(txtPc);
		txtPc.setColumns(1);
		
		JPanel content_Panel = new JPanel();
		frame.getContentPane().add(content_Panel, BorderLayout.NORTH);
		content_Panel.setLayout(new BoxLayout(content_Panel, BoxLayout.X_AXIS));

	    content_Panel.setPreferredSize(new Dimension(1000, 1000));
	    
	    JSplitPane splitPane_1 = new JSplitPane();
	    splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    content_Panel.add(splitPane_1);
	    
	    JLabel lblMemory = new JLabel("Memory");
	    lblMemory.setFont(new Font("Tahoma", Font.BOLD, 4));
	    lblMemory.setHorizontalAlignment(SwingConstants.CENTER);

	    Object columnNames[] = { "", "PC", "Mem Address", "Binary Data", "Label"};
	    
	    JTable memoryTable = new JTable(rowData, columnNames);
	    memoryTable.setRowSelectionAllowed(true);
	    memoryTable.setEnabled(false);

	    TableColumn column = null;
	    for (int i = 0; i < 5; i++) {
	        column = memoryTable.getColumnModel().getColumn(i);
	        switch (i) {
	        case 0: column.setMaxWidth(30); 
	        case 1: column.setMaxWidth(100); 
	        case 4: column.setMaxWidth(110); 
	        }
	    }   
	    
	    memoryTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 26));
	    memoryTable.setRowHeight(28);
	    memoryTable.setFont(new Font("Tahoma", Font.PLAIN, 23));
	    JScrollPane memoryScroll = new JScrollPane (memoryTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    
	    memoryScroll.setColumnHeaderView(lblMemory);
	    
	    JSplitPane splitPane = new JSplitPane();
		assemblyCodeTextArea.setFont(new Font("Monospaced", Font.PLAIN, 26));

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
		splitPane.setEnabled( false );
		splitPane.setRightComponent(memoryScroll);
	    splitPane_1.setLeftComponent(registersScroll);
	    splitPane_1.setRightComponent(splitPane);
	    splitPane_1.setDividerLocation(300);
	    splitPane_1.setEnabled(false);
	    splitPane.setDividerLocation(300);
	    
		
		JLabel lblRegisters = new JLabel("Registers");
		lblRegisters.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblRegisters.setHorizontalAlignment(SwingConstants.CENTER);
		registersScroll.setColumnHeaderView(lblRegisters);
		frame.pack();
	       
	}
	
	protected void openFileChooser() {
		myChooser.setCurrentDirectory(myChooser.getCurrentDirectory());
        if (myChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = myChooser.getSelectedFile();
            InputStream asmFile;
			try {
				asmFile = new BufferedInputStream(new FileInputStream(file));
	            myReader.readAssemblyCode(asmFile);
	            setMemoryText();
	            setRegistersText();
	            setAssemblyCodeText();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}

	public void setMemoryText() {//TODO this method will write the memory portion of the GUI. rowData[][1] contains PC
								//rowData[][2] contains memory address, rowData[][3] contains Data rowData[][4] contains Label.
								//The following code in this method was just for my testing but 

    	rowData[currentPC - 3000][1] = currentPC;   
    	rowData[currentPC - 3000][2] = "0000000000000000";
    	rowData[currentPC - 3000][3] = "null";
    	rowData[currentPC - 3000][4] = "ADD";	
		
		
		
		//rowData[size][0] = arrowIcon;
	}
	
	public void setRegistersText() {//TODO I created a String array registerData[] change the value of the registers in this method
									//The code currently in this method will print out the information and space it properly, you just
									//have to put the values of the current step into registerData[] in the beginning of this method.
		
		
		int j = 0;
		registersText = "";
		
		for (int i = 0; i < registerData.length; i++) {
			if (j == 4) {
				registersText = registersText + "\n";
				j = 0;
			}
			registersText = registersText + "R" + (i + 1) + " = "  + registerData[i];
			for (int space = 0; space < 8 - registerData[i].length(); space++) {
				registersText = registersText + " ";
			}
			j++;
		}
		
	}
	
	public void setAssemblyCodeText() {//TODO Set "assemblyCode" = to the assembly code of current step and it will append onto gui.
		assemblyCode = "ADD R1, R2, R3";
		if (assemblyCodeText == "") assemblyCodeText = assemblyCode;
		else assemblyCodeText = assemblyCodeText + "\n" + assemblyCode;
	}
	
	public void setPC() {//TODO set currentPC = to next PC after instruction.
		lastPC = currentPC;
		currentPC = currentPC + 1;
	}
	

}