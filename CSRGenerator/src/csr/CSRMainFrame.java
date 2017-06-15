package csr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CSRMainFrame extends JFrame {

	private static final long serialVersionUID = -6505639681215242239L;
	private JTextField commonNameTF;
	private JTextField givenNameTF;
	private JTextField surnameTF;
	private JTextField organizationTF;
	private JTextField organizationalUnitTF;
	private JTextField countryCodeTF;
	private JTextField emailTF;
	private JTextField ksFilenameTF;
	private JTextField ksPasswordTF;
	
	private CSRGenerator csrGenerator;
	private JTextField ksAliasTF;
	private JTextField csrFilenameTF;
	
	public CSRMainFrame(){
		super();
		this.csrGenerator = new CSRGenerator();
		
		setSize(320,550);
		setResizable(false);
		setLocation(300,100);		
		setTitle("Create New CSR File");
		
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		JLabel lblCommonName = new JLabel("Common name:");
		springLayout.putConstraint(SpringLayout.WEST, lblCommonName, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblCommonName);
		
		commonNameTF = new JTextField();
		springLayout.putConstraint(SpringLayout.SOUTH, lblCommonName, 0, SpringLayout.SOUTH, commonNameTF);
		springLayout.putConstraint(SpringLayout.EAST, lblCommonName, -79, SpringLayout.WEST, commonNameTF);
		springLayout.putConstraint(SpringLayout.NORTH, commonNameTF, 14, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, lblCommonName, 0, SpringLayout.NORTH, commonNameTF);
		getContentPane().add(commonNameTF);
		commonNameTF.setColumns(10);
		
		JLabel lblGivenName = new JLabel("Given name:");
		springLayout.putConstraint(SpringLayout.WEST, lblGivenName, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblGivenName);
		
		JLabel lblSurname = new JLabel("Surname:");
		springLayout.putConstraint(SpringLayout.WEST, lblSurname, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblSurname);
		
		JLabel lblOrganization = new JLabel("Organization:");
		springLayout.putConstraint(SpringLayout.EAST, lblGivenName, 0, SpringLayout.EAST, lblOrganization);
		springLayout.putConstraint(SpringLayout.WEST, lblOrganization, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblOrganization);
		
		JLabel lblOrganizationalUnit = new JLabel("Organizational unit:");
		springLayout.putConstraint(SpringLayout.WEST, lblOrganizationalUnit, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblOrganizationalUnit);
		
		JLabel lblCountryCode = new JLabel("Country code:");
		springLayout.putConstraint(SpringLayout.WEST, lblCountryCode, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblCountryCode);
		
		JLabel lblEmail = new JLabel("Email:");
		springLayout.putConstraint(SpringLayout.WEST, lblEmail, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblEmail);
		
		givenNameTF = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, givenNameTF, 9, SpringLayout.SOUTH, commonNameTF);
		springLayout.putConstraint(SpringLayout.NORTH, lblGivenName, 3, SpringLayout.NORTH, givenNameTF);
		springLayout.putConstraint(SpringLayout.EAST, commonNameTF, 0, SpringLayout.EAST, givenNameTF);
		springLayout.putConstraint(SpringLayout.EAST, givenNameTF, -25, SpringLayout.EAST, getContentPane());
		getContentPane().add(givenNameTF);
		givenNameTF.setColumns(10);
		
		surnameTF = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, surnameTF, 15, SpringLayout.SOUTH, givenNameTF);
		springLayout.putConstraint(SpringLayout.NORTH, lblSurname, 3, SpringLayout.NORTH, surnameTF);
		springLayout.putConstraint(SpringLayout.EAST, surnameTF, 0, SpringLayout.EAST, commonNameTF);
		getContentPane().add(surnameTF);
		surnameTF.setColumns(10);
		
		organizationTF = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, lblOrganization, 3, SpringLayout.NORTH, organizationTF);
		springLayout.putConstraint(SpringLayout.EAST, organizationTF, 0, SpringLayout.EAST, commonNameTF);
		getContentPane().add(organizationTF);
		organizationTF.setColumns(10);
		
		organizationalUnitTF = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, organizationalUnitTF, 153, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, organizationTF, -17, SpringLayout.NORTH, organizationalUnitTF);
		springLayout.putConstraint(SpringLayout.NORTH, lblOrganizationalUnit, 3, SpringLayout.NORTH, organizationalUnitTF);
		springLayout.putConstraint(SpringLayout.EAST, organizationalUnitTF, 0, SpringLayout.EAST, commonNameTF);
		getContentPane().add(organizationalUnitTF);
		organizationalUnitTF.setColumns(10);
		
		countryCodeTF = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, lblCountryCode, 3, SpringLayout.NORTH, countryCodeTF);
		springLayout.putConstraint(SpringLayout.EAST, countryCodeTF, 0, SpringLayout.EAST, commonNameTF);
		getContentPane().add(countryCodeTF);
		countryCodeTF.setColumns(10);
		
		emailTF = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, lblEmail, 3, SpringLayout.NORTH, emailTF);
		springLayout.putConstraint(SpringLayout.SOUTH, countryCodeTF, -18, SpringLayout.NORTH, emailTF);
		springLayout.putConstraint(SpringLayout.EAST, emailTF, 0, SpringLayout.EAST, commonNameTF);
		getContentPane().add(emailTF);
		emailTF.setColumns(10);
		
		JSeparator separator = new JSeparator();
		springLayout.putConstraint(SpringLayout.WEST, separator, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, separator, -209, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, commonNameTF);
		getContentPane().add(separator);
		
		JLabel lblNewLabel = new JLabel("Key store location:");
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 332, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Key store password:");
		springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_1, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblNewLabel_1);
		
		ksFilenameTF = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, ksFilenameTF, -3, SpringLayout.NORTH, lblNewLabel);
		springLayout.putConstraint(SpringLayout.EAST, ksFilenameTF, 0, SpringLayout.EAST, commonNameTF);
		ksFilenameTF.setEditable(false);
		getContentPane().add(ksFilenameTF);
		ksFilenameTF.setColumns(10);
		
		ksPasswordTF = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, ksPasswordTF, -3, SpringLayout.NORTH, lblNewLabel_1);
		springLayout.putConstraint(SpringLayout.EAST, ksPasswordTF, 0, SpringLayout.EAST, commonNameTF);
		getContentPane().add(ksPasswordTF);
		ksPasswordTF.setColumns(10);
		
		JButton btnCancel = new JButton("Exit");
		springLayout.putConstraint(SpringLayout.SOUTH, btnCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnCancel, 0, SpringLayout.EAST, commonNameTF);
		
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		getContentPane().add(btnCancel);
		
		JButton btnCreate = new JButton("Create");
		springLayout.putConstraint(SpringLayout.NORTH, btnCreate, 0, SpringLayout.NORTH, btnCancel);
		springLayout.putConstraint(SpringLayout.EAST, btnCreate, -6, SpringLayout.WEST, btnCancel);
		
		btnCreate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (ksFilenameTF.getText().equals("")){
					JOptionPane.showMessageDialog(null, "You need to choose a Key Store file location.");
					return;
				}
				if (csrFilenameTF.getText().equals("")){
					JOptionPane.showMessageDialog(null, "You need to choose a CSR file location.");
					return;
				}
				
				SubjectData data = new SubjectData();
				data.setCommonName(commonNameTF.getText().trim());
				data.setCountryCode(countryCodeTF.getText().trim());
				data.setEmail(emailTF.getText().trim());
				data.setGivenName(givenNameTF.getText().trim());
				data.setOrganization(organizationTF.getText().trim());
				data.setOrganizationalUnit(organizationalUnitTF.getText().trim());
				data.setSurname(surnameTF.getText().trim());
				
				csrGenerator.generateCSR(data, csrFilenameTF.getText());
				KeyStoreWriter ksWriter = new KeyStoreWriter(ksFilenameTF.getText());
				ksWriter.loadKeyStore(ksPasswordTF.getText().toCharArray());
				ksWriter.writePrivateKey(ksAliasTF.getText(), csrGenerator.getKeyPair(),
						ksPasswordTF.getText().toCharArray(), csrGenerator.getX500Name());
				ksWriter.saveKeyStore(ksPasswordTF.getText().toCharArray());
				
				commonNameTF.setText("");
				countryCodeTF.setText("");
				emailTF.setText("");
				givenNameTF.setText("");
				organizationTF.setText("");
				organizationalUnitTF.setText("");
				surnameTF.setText("");
				ksFilenameTF.setText("");
				ksPasswordTF.setText("");
				ksAliasTF.setText("");
				csrFilenameTF.setText("");
				
				JOptionPane.showMessageDialog(null, "CSR file created successfully!\nThe private key has been saved in Key Store");
			}
			
		});
		
		getContentPane().add(btnCreate);
		
		JLabel lblKeyStoreAlias = new JLabel("Key store alias:");
		springLayout.putConstraint(SpringLayout.NORTH, lblKeyStoreAlias, 434, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblKeyStoreAlias, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblNewLabel_1, -20, SpringLayout.NORTH, lblKeyStoreAlias);
		getContentPane().add(lblKeyStoreAlias);
		
		ksAliasTF = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, ksAliasTF, -3, SpringLayout.NORTH, lblKeyStoreAlias);
		springLayout.putConstraint(SpringLayout.EAST, ksAliasTF, 0, SpringLayout.EAST, commonNameTF);
		getContentPane().add(ksAliasTF);
		ksAliasTF.setColumns(10);
		
		JButton btnBrowseKeyStore = new JButton("Browse");
		springLayout.putConstraint(SpringLayout.NORTH, btnBrowseKeyStore, 6, SpringLayout.SOUTH, ksFilenameTF);
		springLayout.putConstraint(SpringLayout.WEST, btnBrowseKeyStore, 203, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnBrowseKeyStore, 0, SpringLayout.EAST, commonNameTF);
		
		btnBrowseKeyStore.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Java Key Store (.jks)", "jks");
				fc.setFileFilter(filter);
				fc.setDialogTitle("Choose Key Store location and name");
				int returnVal = fc.showOpenDialog(btnBrowseKeyStore);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            String path = file.getAbsolutePath() + ".jks";
		            file = new File(path);
		            if (file.exists()){
		            	int response = JOptionPane.showConfirmDialog(null, "File already exists. Are you sure you want to override?",
		            			"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		            	if (response == JOptionPane.YES_OPTION){
		            		ksFilenameTF.setText(path);
		            	}
		            } else
		            	ksFilenameTF.setText(path);
				}
			}
		});
		
		getContentPane().add(btnBrowseKeyStore);
		
		JLabel lblCsrFileLocation = new JLabel("CSR file location:");
		springLayout.putConstraint(SpringLayout.WEST, lblCsrFileLocation, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblCsrFileLocation);
		
		csrFilenameTF = new JTextField();
		csrFilenameTF.setEditable(false);
		springLayout.putConstraint(SpringLayout.NORTH, lblCsrFileLocation, 3, SpringLayout.NORTH, csrFilenameTF);
		springLayout.putConstraint(SpringLayout.SOUTH, emailTF, -7, SpringLayout.NORTH, csrFilenameTF);
		springLayout.putConstraint(SpringLayout.EAST, csrFilenameTF, 0, SpringLayout.EAST, commonNameTF);
		getContentPane().add(csrFilenameTF);
		csrFilenameTF.setColumns(10);
		
		JButton btnBrowseCSR = new JButton("Browse");
		springLayout.putConstraint(SpringLayout.SOUTH, btnBrowseCSR, -217, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, separator, 6, SpringLayout.SOUTH, btnBrowseCSR);
		springLayout.putConstraint(SpringLayout.SOUTH, csrFilenameTF, -6, SpringLayout.NORTH, btnBrowseCSR);
		springLayout.putConstraint(SpringLayout.WEST, btnBrowseCSR, 203, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnBrowseCSR, 0, SpringLayout.EAST, commonNameTF);
		
		btnBrowseCSR.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CSR file (.csr)", "csr");
				fc.setFileFilter(filter);
				fc.setDialogTitle("Choose CSR file location and name");
				int returnVal = fc.showOpenDialog(btnBrowseCSR);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            String path = file.getAbsolutePath() + ".csr";
		            file = new File(path);
		            if (file.exists()){
		            	int response = JOptionPane.showConfirmDialog(null, "File already exists. Are you sure you want to override?",
		            			"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		            	if (response == JOptionPane.YES_OPTION){
		            		csrFilenameTF.setText(path);
		            	}
		            } else
		            	csrFilenameTF.setText(path);
				}
			}
		});
		
		getContentPane().add(btnBrowseCSR);
		
		
		
	}
	
	protected void processWindowEvent(WindowEvent e) {
	    super.processWindowEvent(e);
	    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
	      System.exit(0);
	    }
	}
}
