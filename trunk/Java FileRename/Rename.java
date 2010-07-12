/* A file rename tool
 *
 * idea started by CharCheng Mun; completed and enhanced by Cheong Wee Lau, 15 August 2009 
 *
 * GUI and additional functionality added, and work on Whole day by Cheong Wee Lau, 16 Aug 2009 :P
 * Now, it's Version 2.0! - 19 Aug with More Powerful Options and File Extension Manipulation
 */


import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.text.*;
import javax.swing.plaf.metal.*; //deals with the look and feel of the GUI

public class Rename extends JFrame implements ActionListener{	

	private static final String TITLE = "File Rename Tool  ver2.0";
	private static final int WIDTH = 500;
	private static final int HEIGHT = 300;
	private static final String DESCRIPTION = "Select a folder, to batch rename all the files in the directory.";
	private boolean OUTPUT_ON = false;
		 
	private String newNamePrefix;
	private File directory;   //change all the files in this directory;
	
	private Container contentPane;
	private JPanel pnlDescription, pnlDirectory, pnlPrefix, pnlRename, pnlSuffix, pnlCtrl, pnlOption;
	private JLabel lblDesc, lblSequence, lblDirectory;
	private JTextField txtDirectory, txtPrefix, txtSuffix, txtRename, txtSequence;
	private JButton btnOk, btnCancel, btnAbout;
	private JCheckBox cbxPrefix, cbxSuffix, cbxRename, cbxIgnoreExtension, cbxExperiment, cbxOutput;
	private JComboBox cboSequence;
	private Dimension stdDim;
	private static JFrame outputFrame; //the output display of the system console
	
	//------- Output Window ----- variables
	private JScrollPane scrOutput;
	private JTextArea txaOutput;
	
	
	//Constructor - never should have a return type, a lil mistake make thing doesn't work
	public Rename(){
		
		setSize(WIDTH, HEIGHT);
		setTitle(TITLE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setLocation(400, 300);
		
		//Add the closingWindow listener
    	this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					int response = JOptionPane.showConfirmDialog(null, "Are you sure want to exit?", 
						"Close Program", JOptionPane.YES_NO_OPTION);
						
					if(response == JOptionPane.YES_OPTION)
						System.exit(0);
				}
			}//end - WindowAdapter
		); //close - addWindowListener
		
		buildGUI();
	
	}// end constructor
	
	
	
	//Main
    public static void main(String[] args) {    
    	
    	Rename renameTask = new Rename();
    	
    	
    	/*	
    	if(renameTask.welcomeScreen())
    		renameTask.renameFile();
    	else
			System.exit(1);
			
			*/
			
    	renameTask.setVisible(true);		
    }//end Main
    
    
    private void renameFile(){
    	
    	boolean operationResult = false;
    	boolean overallResult = true;
    	int failCount = 0;
    	
    	/* the operation of this part is ensured by the chooseDirectory()
    	 * WE get the list of files in the directory
    	 * get the conditions set by users
    	 * and perform the file rename operation.
    	 */
    	 
    	//Let's get all the information from user
    	String[] fileList = directory.list();  //the list of files in the directory
    	String Prefix = txtPrefix.getText();
    	String Rename = txtRename.getText();
    	String Suffix = txtSuffix.getText();
    	String digits = (String) cboSequence.getSelectedItem();
    	int StartingNum;
    	String generatedSequence;
    	File oldFile;
    	 
    	//let's call the output frame
    	if(cbxOutput.isSelected() && OUTPUT_ON == false){
    		buildOutput();
    		OUTPUT_ON = true;
    	}
    		
    	
    	
    	
    	//display the list of files and readability of each file
    	for(int i = 0; i < fileList.length; i++){	
    		oldFile = new File(directory.getPath()+"/"+ fileList[i]);
    		String readability = fileList[i] +" - readable?: "+oldFile.canRead();
    		System.out.println(readability);
    		
    		if(OUTPUT_ON)
    			txaOutput.append("\n"+readability);
    	}
    	  	
    	for(int i = 0; i < fileList.length; i++){
    		
    		/* get the file extension that we need, and form a new name, 
    		 * we would check if the Ignore File Extension is selected
    		 */
    		oldFile = new File(directory.getPath()+"/"+ fileList[i]);
    		
    		String fileExtension;
    		
    		if(cbxIgnoreExtension.isSelected() == true ){
    			fileExtension = "";
    		}
    		else
    			fileExtension = getFileExtension(fileList[i]);
    			
    		//this part get the original filename		
    		String fileName = getFileName(fileList[i]);
    		
    		
    		
    		String inputInfo = "The input filename->"+ fileList[i] + "\nfile name->" + fileName + "\nextension->" + fileExtension;	 
    		System.out.println(inputInfo);
    			 
    		if(OUTPUT_ON)
    			txaOutput.append("\n"+inputInfo);
    		
    		
    		
    		/* generate sequence for the Name
    		 *if the digits selection is NONE, we ignore it
    		 */
    		if(digits.equals("None") == true){
    			generatedSequence = "";
	    	}
	    	else{
	    		StartingNum = Integer.parseInt(txtSequence.getText());
	    		generatedSequence = nameSequence(StartingNum + i, digits);
	    	}
    		
    		
    		
    		
    		//this is affected by the RenameOption, if Rename has something then only we RENAME
    		if(cbxRename.isSelected() == true){
    			fileName = Rename + generatedSequence;   //the fileName will change.
    		}
    		else{
    			//if Rename has nothing, but the txtSequence has some Value, we take it to the naming too
    			fileName = fileName + generatedSequence;
    		}
    		
    		
    		//the New File Name
    		String newFileName = Prefix + fileName + Suffix + fileExtension;
    		String tentativeName = "new Filename will be ->"+newFileName+"\n";
    		System.out.println(tentativeName);
    		
    		if(OUTPUT_ON)
    			txaOutput.append("\n"+tentativeName);
    		
    		
    		
    		
		    // ! Perform the file rename, if the Experimental Mode is not selected
		    if(cbxExperiment.isSelected() == false){
		    	
		    	operationResult = oldFile.renameTo(new File(directory.getPath()+"/"+newFileName));
		    	String renameResult = "\t*Rename successfully?: " + operationResult+"\n\n";
		    	System.out.println(renameResult);
		    		if(operationResult == false)
		    			failCount++;
		    			
					if(OUTPUT_ON)
    					txaOutput.append("\n"+renameResult);
		    		
		    	//make up the overall result
		    	overallResult = (operationResult && overallResult);
		    }
		    		
    	}
    	
    	if(cbxExperiment.isSelected() == false){
	    	System.out.println("Overall Result: "+overallResult);
	    	if(overallResult)
	    		JOptionPane.showMessageDialog(null, "All files renamed successfully!");
	    	else
	    		JOptionPane.showMessageDialog(null, "File renamed with "+ failCount+ " failure(s)");
    	}//end if
    			
    }//end renameFile
    
    
    
    private boolean chooseDirectory(){
    	
    	/* Choose the file Directory
    	 * this will ensure that the class variable directory get the value
    	 * only when a directory is chosen, then the button Ok will be enabled
    	 */
    	 
    	JFileChooser fc = new JFileChooser();
    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	fc.setAcceptAllFileFilterUsed(false);
    	
    	int returnval = fc.showOpenDialog(this);

    	if(returnval == JFileChooser.APPROVE_OPTION){
    		directory = fc.getSelectedFile();
    		btnOk.setEnabled(true);
    		return true;		
    	}
    	
    	return false;
    }// end chooseDirectory
    
    
    
    private boolean welcomeScreen(){
    	
    	//Display the instruction
    	JOptionPane.showMessageDialog(null, "Select a folder, to rename all the files inside", 
    		"File Rename Tool", JOptionPane.OK_OPTION);
    		
    	//Decide the file prefix
    	String prefix = JOptionPane.showInputDialog(null, "Please specify the name prefix", "File Rename Tool", JOptionPane.YES_NO_OPTION );
    	
    	//if it's a null entry, we just make it "" better, than getting a word null.
    	if(prefix == null){
    		prefix = "";
    	}
    	
    	System.out.println(prefix);
    	newNamePrefix = prefix;    	
    	
    	int agree = JOptionPane.showConfirmDialog(null, "Are you sure with this?", "Confirmation", JOptionPane.YES_NO_OPTION);
    	
    	if(agree == JOptionPane.YES_OPTION)
    		return true;
		else
			return false;
    }//end welcomeScreen
    
    
    //buildOutputFrame
    private void buildOutput(){
    	outputFrame = new JFrame("Output");
    	outputFrame.setSize(WIDTH+100, HEIGHT);
    	
    	Container outputPane = outputFrame.getContentPane();
    	outputPane.setBackground(Color.BLACK);
    	outputPane.setForeground(Color.WHITE);
    	
    	txaOutput = new JTextArea();
    	txaOutput.setEditable(false);
    	txaOutput.setBackground(Color.BLACK);
    	txaOutput.setForeground(Color.WHITE);
    	txaOutput.setFont(new Font("Courier New", 1, 15));
    	
    	scrOutput = new JScrollPane(txaOutput);
    	
    	outputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	outputFrame.setVisible(true);
    	
    	outputFrame.addWindowListener(
    		new WindowAdapter(){
    			public void windowClosing(WindowEvent e){
    				outputFrame.dispose();
    				OUTPUT_ON = false;
    			}
    		}
    		
    	);// close addWindowListener
    	
    	outputPane.add(scrOutput);
    }
    
    
    
    //buildGUI
    private void buildGUI(){
    	
    	stdDim = new Dimension(WIDTH, 25);
    	
    	contentPane = getContentPane();
    	contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    	
    	buildDescPanel();
    	buildDirPanel();
    	buildPrefixPanel();
    	buildRenamePanel();
    	buildSuffixPanel();
    	buildOptPanel();
    	buildCtrlPanel();
    	
    	
    	/* the look and feel part of the GUI, 
    	 * experimental
    	 * from http://java.sun.com/docs/books/tutorial/uiswing/lookandfeel/plaf.html
    	 *
    	 * 

		 *
    	 */ 	 
    	 	
    	/* other motifs are
    	 * lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel;
    	 * lookAndFeel = UIManager.getSystemLookAndFeelClassName();
    	 * lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel;
    	 * lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel;
    	 *
    	 */
    	 
		try {
		// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e) {
			System.out.println(e.getMessage());
		}
		catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		catch (InstantiationException e) {
			System.out.println(e.getMessage());
		}
		catch (IllegalAccessException e) {
			System.out.println(e.getMessage());
		}
    	

    	
    }//end buildGUI
    
    private void buildDescPanel(){
    	
    	pnlDescription = new JPanel();
    	pnlDescription.setLayout(new FlowLayout(FlowLayout.LEADING));
    	Border blackline = BorderFactory.createLineBorder(Color.BLACK);
    	pnlDescription.setBorder(BorderFactory.createTitledBorder(blackline, "Description"));
    	
    	//the Upper Description field --------------------
    	lblDesc = new JLabel(DESCRIPTION);
    	lblDesc.setMaximumSize(new Dimension(WIDTH-10, HEIGHT/10));
    	pnlDescription.add(lblDesc);
    
    	contentPane.add(pnlDescription);
    	
    }//end buildDescPanel
    
    private void buildDirPanel(){
    	
    	pnlDirectory = new JPanel();
    	pnlDirectory.setLayout(new BoxLayout(pnlDirectory, BoxLayout.X_AXIS));
    	//pnlDirectory.setPreferredSize(stdDim);
    	pnlDirectory.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    	
    	//choose the Directory ----------------------------
    	lblDirectory = new JLabel("Directory: ");
    	txtDirectory = new JTextField();
    	txtDirectory.setEditable(false);
    	txtDirectory.setPreferredSize(stdDim);
    	txtDirectory.setMaximumSize(stdDim);
    	JButton btnSelectDirectory = new JButton("select");
    	
    	ActionListener selectAction = new ActionListener(){
    		public void actionPerformed(ActionEvent e){
    			if(chooseDirectory())
    				txtDirectory.setText(directory.getPath()); //set the Path to the directory display
    		}
    	};
    
    	btnSelectDirectory.addActionListener(selectAction); //close addActionListener
    	pnlDirectory.add(lblDirectory);
    	pnlDirectory.add(txtDirectory);
    	pnlDirectory.add(btnSelectDirectory);
    	
    	contentPane.add(pnlDirectory);
    	
    }
    
    
    private void buildPrefixPanel(){
    	
    	pnlPrefix = new JPanel();
    	pnlPrefix.setLayout(new BoxLayout(pnlPrefix, BoxLayout.X_AXIS));
    	pnlPrefix.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    	
    	// the Prefix -----------------------
    	
    	//lblPrefix = new JLabel("Prefix");
    	txtPrefix = new JTextField();
    	txtPrefix.setMaximumSize(stdDim);
    	txtPrefix.setEditable(false);
    	cbxPrefix = new JCheckBox("Prefix");
    	cbxPrefix.addActionListener(
    		new ActionListener(){
    			public void actionPerformed(ActionEvent e){
    				if(cbxPrefix.isSelected())
    					txtPrefix.setEditable(true);
    				else{
    					txtPrefix.setEditable(false);
    					txtPrefix.setText("");
    				}
    					
    			}
    		}
    	); //close addActionListener
    	
    	pnlPrefix.add(cbxPrefix);
    	pnlPrefix.add(txtPrefix);
    	
    	contentPane.add(pnlPrefix);
    	
    }//end buildPrefixPanel
    
    
    
    private void buildRenamePanel(){
    	
    	pnlRename = new JPanel();
    	pnlRename.setLayout(new BoxLayout(pnlRename, BoxLayout.X_AXIS));
    	pnlRename.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    	
    	// the Rename -------------------------
    	txtRename = new JTextField();
    	txtRename.setMaximumSize(stdDim);
    	txtRename.setEditable(false);
    	cbxRename = new JCheckBox("Rename");
    	lblSequence = new JLabel("Sequence: [Leading Zero]");
    	txtSequence = new JTextField();
    	txtSequence.setEditable(false);
    	
    	
    	Dimension seqDim = new Dimension(150,25);
    	txtSequence.setMaximumSize(seqDim);
    	txtSequence.setEditable(false);
    		
    	
    	String[] sequenceValue = {"None", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    	cboSequence = new JComboBox(sequenceValue);
    	cboSequence.setMaximumSize(new Dimension(80, 25));
    	cboSequence.setEnabled(true);
    	    	        	
    	//check listener
    	cbxRename.addActionListener(
    		new ActionListener(){
    			public void actionPerformed(ActionEvent e){
    				if(cbxRename.isSelected()){
    					txtRename.setEditable(true);
    					//txtSequence.setEditable(true);
    					//cboSequence.setEnabled(true);
    				}    				
    				else{
    					txtRename.setEditable(false);
    					//txtSequence.setEditable(false);    					
    					//cboSequence.setEnabled(false);
    					//cboSequence.setSelectedIndex(0);
    					txtRename.setText("");
    					//txtSequence.setText("");
    				}
    					
    			}
    		}
    	); //close addActionListener    	    	    	
    	
    	//special actionEvent control by the combox to the textfield
    	cboSequence.addActionListener(
    		new ActionListener (){
    			public void actionPerformed(ActionEvent e){
    				String value = (String) cboSequence.getSelectedItem();
    				if(value.equals("None")){
    					txtSequence.setEditable(false);
    					txtSequence.setText("");
    				}else{
    					int digit = Integer.parseInt(value);
    					txtSequence.setEditable(true);
    					txtSequence.setColumns(digit);
    					txtSequence.setDocument(new JTextFieldLimit(digit));
    				}	
    			}//end actionPerformed
    		}//end ActionListener	
    	);//close addActionListener
    	
    	pnlRename.add(cbxRename);
    	pnlRename.add(txtRename);
    	pnlRename.add(lblSequence);
    	pnlRename.add(txtSequence);
    	pnlRename.add(cboSequence);
    	
    	contentPane.add(pnlRename);
    	
    }//end buildRenamePanel
    
    private void buildSuffixPanel(){
    	
    	pnlSuffix = new JPanel();
    	pnlSuffix.setLayout(new BoxLayout(pnlSuffix, BoxLayout.X_AXIS));
    	pnlSuffix.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    	
    	// the Suffix -------------------------
    	txtSuffix = new JTextField();
    	txtSuffix.setMaximumSize(stdDim);
    	txtSuffix.setEditable(false);
    	cbxSuffix = new JCheckBox("Suffix");
    	cbxIgnoreExtension = new JCheckBox("Ignore File Extension");
    	
    	
    	
    		
    	ActionListener cbxSuffixListener = new ActionListener(){
    		public void actionPerformed(ActionEvent e){
				if(cbxSuffix.isSelected())
					txtSuffix.setEditable(true);
				else
				{
					txtSuffix.setEditable(false);
					txtSuffix.setText("");
				}	
    		}
    	};
    	
    	//check listener
    	cbxSuffix.addActionListener(cbxSuffixListener);
    	
    	pnlSuffix.add(cbxSuffix);
    	pnlSuffix.add(txtSuffix);
    	pnlSuffix.add(cbxIgnoreExtension);
    	
    	
    	contentPane.add(pnlSuffix);
    }//end buildSuffixPanel
    
    
    //buildOptPanel
    private void buildOptPanel(){
    	
    	pnlOption = new JPanel();
    	pnlOption.setLayout(new FlowLayout());
    	pnlOption.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    	
    	cbxExperiment = new JCheckBox("Experimental Mode");
    	cbxOutput = new JCheckBox("Output Window");
    	
    	pnlOption.add(cbxExperiment);
    	pnlOption.add(cbxOutput);
    	contentPane.add(pnlOption);
    	
    }//end buildOptPanel
    
    
    private void buildCtrlPanel(){
    	
    	pnlCtrl = new JPanel();
    	pnlCtrl.setLayout(new BoxLayout(pnlCtrl, BoxLayout.X_AXIS));
    	pnlCtrl.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    	
    	//setup the ctrl panel ---------------------
    	btnOk = new JButton("Ok");
    	btnOk.setEnabled(false);
    	btnOk.addActionListener(this);
    	
    	btnCancel = new JButton("Cancel");
    	btnCancel.addActionListener(this);
    	
    	btnAbout = new JButton("About");
    	btnAbout.addActionListener(this);
    	
    	pnlCtrl.add(btnOk);
    	pnlCtrl.add(btnCancel);
    	pnlCtrl.add(btnAbout);
    	
    	contentPane.add(pnlCtrl);
    	
    }//end buildCtrlPanel
    
    
    //the getFileExtension seems useful, so we left it open for public use.
    public String getFileExtension(String filename){
    	
    	int dotIndex = filename.lastIndexOf(".");
    	if(dotIndex >= 0){
    		String fileXT = filename.substring(dotIndex);
	    	//fileXT = fileXT.toLowerCase(); //make the extension to lower case, nvm just follow the original
	    	//System.out.println("The input string: "+ filename + " file extension: " + fileXT);
	    	return fileXT;
    	}
    	else
    		return "";
    }
    
    
    //this will get the FileName without the extension
    public String getFileName(String filename){

    	int dotIndex = filename.lastIndexOf(".");
    	
    	if(dotIndex >= 0){
    		String fileName = filename.substring(0, dotIndex);   	
    		//System.out.println("The input string: "+ filename + " file name: " + fileName);
    		return fileName;
    	}
    	else
    		return "";
    	
    }
    
    private String nameSequence(int number, String digits) {
    	    	
    	String leadingZeroSpecifier = "%0" + digits + "d";
    	
    	String generatedSequence = String.format(leadingZeroSpecifier, number);
    	
    	return generatedSequence;
    	
    }     
    	
    public void actionPerformed(ActionEvent e){
    	JButton clickedButton = (JButton) e.getSource();
    	
    	JOptionPane.showMessageDialog(null, clickedButton.getText() + " clicked");
    	
    	//when we click the ok button... then
    	if(clickedButton == btnOk){
    		
    		String cboString = (String) cboSequence.getSelectedItem();
    		
    		/* we have to check if user have entered any value, is the chosen digit is NOT NONE.
    		 * the ComboBox selection is NOT "None"
    		 */
    		if(cboString.equals("None") == false){
    			String temp = txtSequence.getText();
    			
    			// then it has to be have some value for the textfield
    			if(temp.equals("") || temp == null){
    				JOptionPane.showMessageDialog(null, "Oops... Please fill up the sequence number field");
    				txtSequence.grabFocus();
    				//something to highlight the field in future?
    				return;
    			}
    		}
    		
    		JOptionPane.showMessageDialog(null, "rename invoked");
			renameFile();	
				
    	}
    	else if(clickedButton == btnCancel){
    		btnOk.setEnabled(false);
    		txtDirectory.setText("");
    		directory = null;
    		cbxPrefix.setSelected(false);
    		cbxRename.setSelected(false);
    		cbxSuffix.setSelected(false);
    		txtPrefix.setEditable(false);
    		txtPrefix.setText("");
    		txtSuffix.setEditable(false);
    		txtSuffix.setText("");
    		txtRename.setEditable(false);
    		txtRename.setText("");
    		
    		cbxIgnoreExtension.setSelected(false);
    		txtSequence.setEditable(false);
    		txtSequence.setText("");
    		cboSequence.setSelectedIndex(0);
    		
    	}
    	else if(clickedButton == btnAbout){
    	}
    }
    
    
    /* for nested class */
    //special nested class for TextField Limit
	class JTextFieldLimit extends PlainDocument{
		private int limit;
		private boolean toUpperCase = false;
		
		//constructor
		JTextFieldLimit(int limit){
			super();
			this.limit = limit;
		}
		
		JTextFieldLimit(int limit, boolean upperCase){
			super();
			this.limit = limit;
			toUpperCase = upperCase;
		}
		
		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException{
			if(str == null)
				return;
			if((getLength() + str.length())<= limit){
				if(toUpperCase) str = str.toUpperCase();
				super.insertString(offset, str, attr);
					
			}
		}
		
	}//end of nested class
}