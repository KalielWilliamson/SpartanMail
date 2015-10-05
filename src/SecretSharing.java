import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Queue;

import com.tiemens.secretshare.BuildVersion;
import com.tiemens.secretshare.main.*;
import com.tiemens.secretshare.main.cli.Main;



public class SecretSharing {
	private static String[] Share = null;
	private static int byteStream = 0;
	static String mod = null;
	private static String fLocation = null;
	private static String[] argument = null;
	private static String sID = null;
	private static int numb = 0;
	private static Queue<String> receiver = null;
	private static Queue<String> sender = null;
	public static Queue<String> shareFileLocations = null;
	
	/** The purpose of this object is to take information regarding the sender, receiver,
	 * message and attachment. It essentially takes in everything relevant to the email
	 * and deals with it so that each share can be sent.
	 */ 
	 
	
	/**This method creates a folder and the corresponding file shares.
	 */

	/*If you create a SecretSharing object, give it the path for an attachment and/or a message,
	 * you'll be able to take the shareFileLocations queue object which holds the share file 
	 * locations.
	 */
	public SecretSharing(String[] arguments,String fileLocation, String ShareID,
			Queue<String> to, Queue<String> from,String subject) {
		//make a new directory for this object, place files shares in that folder
		(new File("src\\shares\\"+ShareID)).mkdir();
		//Add all the input variables into the object fields
		argument = arguments;
		fLocation = fileLocation;
		sID = ShareID;
		receiver = to;
		sender = from;
		
		//If there is an attachment
		/**if(!fileLocation.isEmpty()) {
			//read attachment as byte stream
			try {
				BufferedReader br = new BufferedReader(new FileReader(fileLocation));
				byteStream = br.read();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] attachmentArg = argument;
			attachmentArg[6] = Integer.toString(byteStream);
			
			
		}*/
		//Make shares based on the message and save them as an object field
		//setShare(arguments);
		
		//Convert queries to arrays
		System.out.println("makeFile");
		setShare(argument);
		for(int index = 0; index < Integer.parseInt(argument[2]); index++ )
			makeFile(index,to.poll(),from.poll(), subject);
	}

	public void makeFile(int ShareNumber, String to, String from, String subject) {
		//generate the file content
		String fileContent = "";
		fileContent += "<to>";
		fileContent += to;
		fileContent += "</to>\n";
		fileContent += "<from>";
		fileContent += from;
		fileContent += "</from>\n";
		fileContent += "<subject>";
		//fileContent += primeSetShare(subject, ShareNumber);
		fileContent += "</subject>\n";
		fileContent += "<attachment>";
		fileContent += "</attachment>\n";
		fileContent += "<message>";
		fileContent += Share[ShareNumber];
		fileContent += "</message>\n";
		System.out.println("-------------\n"+fileContent+"-------------");
		//create the file and place the file content inside of it
		try {
			//File shareFile = new File("src\\shares\\"+sID+"\\"+sID+"_"+Integer.toString(ShareNumber)+".txt");
			PrintWriter writer = new PrintWriter("src\\shares\\"+sID+"\\"+sID+"_"+Integer.toString(ShareNumber)+".txt", "UTF-8");
			writer.println(fileContent);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String primeSetShare(String piece, int ShareNumber) {
		argument[6] = piece;
		setShare(argument);
		return Share[ShareNumber];
	}
	
	private void setShare(String[] arg) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		Main.main(arg,System.in, ps, true);
		String ret = null;
		try {
			ret = baos.toString("UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ret);
		String[] a = ret.split("\n");
		Share = new String[Integer.parseInt(arg[4])];
		for(int i = 0, n = 0, z = 0; i < a.length; i++) {
			if(a[i].contains("Share")) {
				if(a[i].split(" = ").length == 2 && n < Share.length) {
					Share[n] = a[i].split(" = ")[1].trim();
					n++;
				}
			}
			if(a[i].contains("modulus") && z == 0) {
				this.mod = a[i].split(" = ")[1].trim();
				z++;
			}
		}
	}
	
}
