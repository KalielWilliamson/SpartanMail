import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class SendMail {
	static String statusbar = "";
   public static void main(String[] args) throws IOException {
	   String contact = "Kaliel";
	   Distributor(contact,"654687531","src/proxies/proxies.txt","LOL","","subject");
	   //Distributor("AlphaSSSE","65164981");
   }
   public static String Distributor(String to, String ShareID, String from, String message, String path, String subject) throws IOException {
	   System.out.println(to+"\n"+ShareID+"\n"+from+"\n"+message+"\n");
	    String filepath = "";
	    int count = 1, row1 = 1, row2 = 1, numShares = 0;
	    String input;
	    /*Queues for the proxy emails and
	     * queues for the receiving emails.
	     * Both of these are only relevant if
	     * there is more than 1 share to send,
	     * therefore if there is a contact without
	     * an @ symbol. 
	     */
	    Queue<String> senderQ = new LinkedList<String>();
	    BufferedReader SenderBr = new BufferedReader(new FileReader(from));
	    while((input = SenderBr.readLine()) != null) {
			senderQ.add(input);
			if(count == 3) {
				count = 0;
				row1++;
			}
			else
				count++;
		}
	    if(!to.contains("@")) {
		    SenderBr.close();
			Queue<String> receiverQ = new LinkedList<String>();
			/*if there is an attachment, ready the file containing
			 * a list of proxy emails for sending shares.
			 * Put every line of content in that file into a queue
			 * So this only applies when there is more than one share
			 * to send.
			 */
			
			BufferedReader ReceiverBr = new BufferedReader(new FileReader("src/contacts/"+to+".txt"));
			while((input = ReceiverBr.readLine()) != null) {
				receiverQ.add(input);
				row2++;//use this variable to determine the number of shares
			}		
			/*if this is the type of contact that doesn't contain an @ symbol,
			 * then ready the contacts file for reading.
			 * While there are usernames and passwords in the file, put them in a queue
			 * otherwise, put the single @servername.com email adress into the queue
			 */
			ReceiverBr.close();
			String[] val = {"","",""};
			
			numShares = row1;
			if(row2<row1)
				numShares = row2; //numShares is the input to the Shamir Secret Sharing
			count = 1;
			
			//(path, ShareID, numShares);
			int sharenumber = row2;
			ShamirSecretSharing.takeInFile(ShareID,numShares,message,(new File(path)),senderQ.peek(),receiverQ.peek());
			/* Complete rigging up SSS object to perform sharing
			 * 
			 */
			//SSS secretSharing = new SSS(arguments[6],Integer.parseInt(arguments[2]));
			//int[][] shares = secretSharing.messageShares;
			String msg = null;
			int n = 0;
			/** Where shares are sent to emails */
			while((!senderQ.isEmpty()) && (!receiverQ.isEmpty())) {
				filepath = "";
				val[0] = receiverQ.poll();
				val[1] = senderQ.poll();
				val[2] = senderQ.poll();
				statusbar += ("\n----------------\n"+"to: "+val[0]+"\nfrom: "+val[1]+"\nusername: "+
				val[1]+"\npassword: "+val[2]+"\n----------------\n");
				filepath = "src/shares/"+ShareID.toString()+"/"+ShareID+"_"+Integer.toString(n+1)+".bin";
				msg = "";
				n++;
				MailSendor(val[0],val[1],val[1],val[2],filepath,ShareID,msg);
			}
	    }
	    else {
	    	System.out.println("2.2");
	    	String myAddress = senderQ.poll();
	    	String myPassword = senderQ.poll();
	    	System.out.println(myAddress+"\n"+myPassword);
	    	MailSendor(to,myAddress,myAddress,myPassword,filepath,ShareID,message);
	    	System.out.println("Sent");
	    }
		return statusbar;
	}
   protected static void MailSendor(String to, String from, String username, String password, 
		   String filepath, String ShareID, String msg) {
	   Properties props = new Properties();
      if(from.contains("@gmail.com")) {
    	  String host = "smtp.gmail.com";
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "587");
      }
      else if(from.contains("@yahoo.com")) {
    	  String host = "smtp.mail.yahoo.com";
    	  props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "587");
      }
      else if(from.contains("@hotmail.com")) {
    	  String host = "smtp.live.com";
    	  props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "587");
      }
      Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
            }
         }
      );
      System.out.println(filepath);
      try {
         Message message = new MimeMessage(session);
         message.setFrom(new InternetAddress(from));
         message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
         message.setSubject(ShareID);
         BodyPart messageBodyPart = new MimeBodyPart();
         messageBodyPart.setText(msg);
         Multipart multipart = new MimeMultipart();
         multipart.addBodyPart(messageBodyPart);
         messageBodyPart = new MimeBodyPart();
         if(filepath != ""){
        	 System.out.println("attachment here");
	         String filename = "share.txt";
	         DataSource source = new FileDataSource(filepath);
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName(filename);
	         multipart.addBodyPart(messageBodyPart);
         }
         else
        	 System.out.println("attachment not here");
         message.setContent(multipart);
         System.out.println("msg---\n"+msg+"\n---msg");
         Transport.send(message);
         statusbar += ("\nsuccess");
         System.out.print(statusbar);
      } catch(MessagingException e) {
         throw new RuntimeException(e);
      }
   }
}