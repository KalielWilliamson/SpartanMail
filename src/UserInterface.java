import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextPane;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JList;

import java.awt.Component;

import javax.swing.ScrollPaneConstants;

/**This program is the User Interface for the Mail User Application.
 * It will contain a contact text field, a send button, a message pain,
 * and an attachment text field.
 * @author Kaliel
 *
 */

public class UserInterface {
	private static File selectedFile;
	private static boolean selectedFileIn = false;
	private JFrame frame;
	private JTextField attField;
	private JTextField txtSubject;
	private JTextField contactField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserInterface window = new UserInterface();
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
	public UserInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.getContentPane().setForeground(Color.RED);
		frame.setBackground(Color.WHITE);
		frame.setBounds(100, 100, 613, 459);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		
		
		
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setForeground(Color.BLACK);
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setBounds(1, 2, 596, 408);
		frame.getContentPane().add(tabbedPane);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setForeground(Color.GREEN);
		tabbedPane.addTab("Recieved Mail", null, panel_1, null);
		panel_1.setLayout(null);
		
		
		
		JTextPane textPane_1 = new JTextPane();
		textPane_1.setBackground(Color.WHITE);
		textPane_1.setForeground(Color.BLACK);
		textPane_1.setEditable(false);
		
		JScrollPane scrollPane1 = new JScrollPane(textPane_1);
		scrollPane1.setBounds(150, 100, 420, 250);
		panel_1.add(scrollPane1);
		
		
		

		
		
		
		
		//panel_1.add(textPane_1);
		
		
		DefaultListModel listModel = new DefaultListModel();
		File[] files1 = new File("src/inbox").listFiles();
		for(File file : files1) {
			if(file.isFile())
				listModel.addElement((file.getName()).substring(0,(file.getName()).length()-4));
		}

		JList list = new JList(listModel);
		list.setForeground(Color.BLACK);
		list.setBackground(Color.WHITE);

		//list.setBounds(10, 11, 112, 280);
		//panel_1.add(list);
		
		JScrollPane scrollPane = new JScrollPane(list);
		//scrollPane.setViewportView(list);
		scrollPane.setBounds(20,11,112,340);
		panel_1.add(scrollPane);
		
		JLabel lblMessage = new JLabel("Message");
		lblMessage.setForeground(Color.BLACK);
		lblMessage.setBounds(175, 77, 79, 14);
		panel_1.add(lblMessage);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		tabbedPane.addTab("Send Email", null, panel, null);
		panel.setLayout(null);
		
		JComboBox contactCombo = new JComboBox();
		contactCombo.setForeground(Color.BLACK);
		contactCombo.setBackground(Color.WHITE);
		contactCombo.setBounds(172, 21, 409, 20);
		panel.add(contactCombo);
		
		JLabel label = new JLabel("Message:");
		label.setForeground(Color.BLACK);
		label.setBounds(13, 117, 91, 14);
		panel.add(label);
		
		JTextPane messagePane = new JTextPane();
		messagePane.setBackground(Color.WHITE);
		messagePane.setForeground(Color.BLACK);
		messagePane.setBounds(10, 124, 539, 194);
		//panel.add(messagePane);
		
		
		JButton button_1 = new JButton("Send");
		button_1.setForeground(Color.BLACK);
		button_1.setBackground(Color.WHITE);
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String to = contactCombo.getSelectedItem().toString();
				String subject = txtSubject.getText();
				Random rn = new Random();
				String ShareID = Integer.toString((new Random()).nextInt((999999999 - 100) + 1) + 100);
				System.out.println(ShareID);
				String from = "src/proxies/proxies.txt";
				String message = messagePane.getText();
				try {
					//if(selectedFileIn == true)
					System.out.println("sendmail1");
						SendMail.Distributor(to, ShareID, from, message,"", subject);
					System.out.println("sendmail2");
					//selectedFileIn = false;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				messagePane.setText("");
				contactCombo.repaint();
				attField.setText("");
				attField.removeAll();
			}
		});
		
		JScrollPane sP = new JScrollPane(messagePane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sP.setBounds(13, 142, 568, 194);
		panel.add(sP);
		button_1.setBounds(482, 346, 99, 23);
		panel.add(button_1);
		
		JLabel lblTo = new JLabel("To");
		lblTo.setForeground(Color.BLACK);
		lblTo.setBackground(Color.BLACK);
		lblTo.setBounds(116, 24, 46, 14);
		panel.add(lblTo);
		
		attField = new JTextField();
		attField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		attField.setBounds(172, 52, 409, 20);
		panel.add(attField);
		attField.setColumns(10);
		
		JButton btnSearchButton = new JButton("Search");
		btnSearchButton.setBackground(Color.WHITE);
		btnSearchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(frame);
				//fileChooser.setCurrentDirectory(new File(System.getProperty("src/")));
				selectedFile = fileChooser.getSelectedFile();
				attField.setText(selectedFile.getName());
				selectedFileIn = true;
				//selectedFile = fileChooser.getSelectedFile();
			}
		});
		btnSearchButton.setBounds(73, 51, 89, 23);
		panel.add(btnSearchButton);
		
		txtSubject = new JTextField();
		txtSubject.setBounds(172, 83, 409, 20);
		panel.add(txtSubject);
		txtSubject.setColumns(10);
		
		JLabel lblSubject = new JLabel("Subject");
		lblSubject.setForeground(Color.BLACK);
		lblSubject.setBackground(Color.BLACK);
		lblSubject.setBounds(116, 85, 46, 14);
		panel.add(lblSubject);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		tabbedPane.addTab("Add Contact", null, panel_2, null);
		panel_2.setLayout(null);
		
		JLabel lblEmailAddress = new JLabel("Email Address");
		lblEmailAddress.setForeground(Color.BLACK);
		lblEmailAddress.setBounds(75, 159, 72, 14);
		panel_2.add(lblEmailAddress);
		
		JTextPane proxyPane = new JTextPane();
		proxyPane.setForeground(Color.BLACK);
		proxyPane.setBackground(Color.LIGHT_GRAY);
		proxyPane.setBounds(157, 159, 298, 102);
		panel_2.add(proxyPane);
		
		JLabel lblContactName = new JLabel("Contact Name");
		lblContactName.setForeground(Color.BLACK);
		lblContactName.setBounds(69, 124, 72, 14);
		panel_2.add(lblContactName);
		
		contactField = new JTextField();
		contactField.setBackground(Color.LIGHT_GRAY);
		contactField.setColumns(10);
		contactField.setBounds(157, 121, 298, 20);
		panel_2.add(contactField);
		
		JButton btnAddContact = new JButton("Add Contact");
		btnAddContact.setBackground(Color.WHITE);
		btnAddContact.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				BufferedWriter output = null;
				//Add new contact
				String conName = contactField.getText();
				String allprox = proxyPane.getText();
				File conFile = new File("src/contacts/"+conName+".txt");
				try {
					output = new BufferedWriter(new FileWriter(conFile));
					output.write(allprox);
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnAddContact.setBounds(157, 301, 118, 23);
		panel_2.add(btnAddContact);
		
		//List<String> contacts = new ArrayList<String>();
		//comboBox.addItem("hg");
		File[] files = new File("src/contacts").listFiles();
		for(File file : files) {
			if(file.isFile())
				contactCombo.addItem((file.getName()).substring(0,(file.getName()).length()-4));
		}
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String strbldr = "";
				String loc = "src/inbox/"+list.getSelectedValue().toString()+".txt";
				try {
					BufferedReader buffread = new BufferedReader(new FileReader(loc));
					String line = buffread.readLine();
				    while (line != null) {
				        strbldr += line;
				        strbldr += "\n";
				        line = buffread.readLine();
				    }
				    buffread.close();
				} catch(Exception e){};
				textPane_1.setText(strbldr);
				textPane_1.setCaretPosition(0);
			} 
		});
		class Inner extends Thread {

		    public void run() {
		        
		        do {
		        	System.out.println("Hello from a thread!");
		        	try {
		        		ReceiveMail.run();
		        		scrollPane.repaint();
						this.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }while(true);
		    }

		    public void main(String args[]) {
		        (new Inner()).start();
		    }

		}
		Inner reloads = new Inner();
		reloads.start();
	}
}
