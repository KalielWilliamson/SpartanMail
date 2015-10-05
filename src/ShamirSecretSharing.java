import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/* When recombining shares, be sure to choose 'bits' bits (now 10 bits)
 * to read in at a time. Read until you hit EndOfTo then save that data as the 'To' share
 * read until you hit EndOfFrom then save that data as the 'From' share
 * read until you hit EndOfAttachment then save that data as the 'Attachment' share
 * repeat this stage and save to new attachment until you hit EndOfFile
 * 
 */

/**This class utilizes the SSS class to create shares and save them
 * to the appropriate file locations
 * @author Kaliel
 *
 */
public class ShamirSecretSharing {
	/**
	 * @param args
	 */
	static int bits = 10;
	static int andValue = 511;
	static int EndOfTo = 257;
	static int EndOfFrom = 258;
	static int EndOfAttachment = 259;
	static int EndOfFile = 260;
	static int FoundAllFiles = 261;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			File f = new File("C:\\Users\\Kaliel\\Desktop\\abstract.txt");
			takeInFile("ExampleOutput",3,"Hello from messager",f,"kalielwilliamson@gmail.com","ALPHASSE@gmail.com");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Here I want to verify that I can truely put out only 9-bits
	}
	public ShamirSecretSharing(String ShareID, int Number, String message, File attatchment, String to, String from) throws IOException {
		takeInFile(ShareID, Number, message, attatchment, to, from);
	}
	//Take in one file
	public static void takeInFile(String ShareID, int Number, String message,File attachment, String to, String from) throws IOException {
		//create folder
		(new File("src\\shares\\"+ShareID)).mkdir();
		//get file
		SSS toShares = new SSS(to,Number);
		SSS fromShares = new SSS(from,Number);
		SSS messageShares = new SSS(message,Number);
		SSS attachmentShares = new SSS(attachment, Number);
		File file = null;
		int counter = 0;
		for(int i = 0; i < Number; i++) {
			file = new File("src\\shares\\"+ShareID+"\\"+ShareID+"_"+Integer.toString(i+1)+".bin");
			@SuppressWarnings("resource")
			WritableByteChannel channel = new FileOutputStream(file,true).getChannel();
			 @SuppressWarnings("deprecation")
			BitOutput output = new BitOutput(new BitOutput.ChannelOutput(channel));
			 
			 /*inp = "<to>";
			 output.writeInt(inp.length(),Integer.parseInt(inp));*/
			 for(int n = 0; n < toShares.messageShares[i].length; n++)
			 {
				 /*if(toShares.messageShares[n][i] > 256)
				 {
					 System.out.println("ERROR. Greater than 256");
				 }
				 else if(toShares.messageShares[n][i] < 2)
					 System.out.println("ERROR. Less than 2");*/
				 output.writeInt(bits, toShares.messageShares[n][i] & andValue); // writes a 10-bit signed int
				 counter ++;
			 }
			 output.writeInt(bits, EndOfTo);
			 /*inp = "</to>";
			 output.writeInt(inp.length(),Integer.parseInt(inp));
			 
			 
			 inp = "<from>";
			 output.writeInt(inp.length(),Integer.parseInt(inp));*/
			 for(int n = 0; n < fromShares.messageShares[i].length; n++)
			 {
				 output.writeInt(bits, fromShares.messageShares[n][i] & andValue); // writes a 9-bit signed int
				 counter ++;
			 }
			 output.writeInt(bits, EndOfFrom);
			 /*inp = "</from>";
			 output.writeInt(inp.length(),Integer.parseInt(inp));
			 
			 
			 inp = "<attachment>";
			 
			 output.writeInt(inp.length(),Integer.parseInt(inp));*/
			 if(attachment != null)
			 {
				 //System.out.println("attachment length: "+attachmentShares.messageShares[i].length);
				 for(int n = 0; n < attachmentShares.fileShares.length; n++)
				 {
					 output.writeUnsignedInt(bits, attachmentShares.fileShares[n][i] & andValue); // writes a 9-bit unsigned int
					 counter ++;
				 }
			 }
			 output.writeInt(bits, EndOfAttachment);
			 
			 /*inp = "</attachment>";
			 output.writeInt(inp.length(),Integer.parseInt(inp));
			 
			 inp = "<message>";
			 output.writeInt(inp.length(),Integer.parseInt(inp));
			 */
			 
			 for(int n = 0; n < messageShares.messageShares[i].length; n++)
			 {
				 output.writeInt(bits, messageShares.messageShares[i][n] & andValue); // writes a 9-bit signed int
				 counter ++;
			 }
			 output.writeInt(bits, EndOfFile);
			 //inp = "</message>";
			 //output.writeInt(inp.length(),Integer.parseInt(inp));
			 System.out.println(output.getCount()+"\t"+counter);
			 counter = 0;
			 channel.close();
		}
	}

	//Turn integer to 9-bit data
	//Make a folder with the name of that attachment

	//Spit out several files in the shares file
	
	//Now for the reconstruction methods:
	/**
	 * This method is meant to reconstruct the secret from several files that
	 * exist in the given folder
	 * Psuedocode:
	 * 	1) get into the given folder
	 * 	2) grab all files that have the same name before the '_' character
	 * 	3) read the first 10 bits of each file
	 * 	4) push through the reconstruction method
	 * 	5) if they secret for that 10-bit share is the 'FoundAllFiles' number,
	 * 	6) do
	 * 		6) read the first file until the given EndOf___ number
	 * 		7) repeat for all files
	 * 		8) push that information into the SSS class and recombine the information
	 * 	7) until all pieces are read
	 * 	6) within the inbox folder, create a folder with the same name as the shares
	 * 	7) save each recovered information type into that folder
	 * 	8) delete the shares from the 'inbox_shares' folder
	 */
	public static void Reconstruction(Path shareFolderPath, Path inboxFolderPath, File[] shares)
	{
		Queue<String> matches = new LinkedList<String>();
		File[] fMatches = null;
		String[] names = null;
		//get into shareFolderPath folder
		File directory = new File(shareFolderPath.toString());
		//grab all files that have the same name before the '_' character
        File[] fList = directory.listFiles();
        String[] listNames = new String[fList.length];
        //get all the files from a directory
        for(int i = 0; i < fList.length; i++)
        {
        	listNames[i] = fList[i].getName().split("_")[0];
        }
        for(int i = 0; i < fList.length; i++)
        {
        	for(int j = 0; j < fList.length; j++)
            {
        		//grab all files that have the same name before the '_' character
            	if(listNames[i] == listNames[j])
            		matches.add(listNames[i]);
            }
        }
        names = (String[]) matches.toArray();
	}
}
