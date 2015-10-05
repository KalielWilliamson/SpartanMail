import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
 

public class ReceiveMail {
	public static void main(String[] args) throws IOException {
		  run();
	  }
    private String saveDirectory;
 
    /**
     * Sets the directory where attached files will be stored.
     * @param dir absolute path of the directory
     */
    public void setSaveDirectory(String dir) {
        this.saveDirectory = dir;
    }
 
    /**
     * Downloads new messages and saves attachments to disk if any.
     * @param host
     * @param port
     * @param userName
     * @param password
     */
    
    public void downloadEmailAttachmentsPOP3(String host, String port,
            String userName, String password, int filenumber) { 
        Properties properties = new Properties();
 
        // server setting
        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", port);
 
        // SSL setting
        properties.setProperty("mail.pop3.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.pop3.socketFactory.fallback", "false");
        properties.setProperty("mail.pop3.socketFactory.port",
                String.valueOf(port));
 
        Session session = Session.getDefaultInstance(properties);
 
        try {
            // connects to the message store
            Store store = session.getStore("pop3");
            store.connect(userName, password);
 
            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);
 
            // fetches new messages from server
            Message[] arrayMessages = folderInbox.getMessages();
 
            for (int i = 0; i < arrayMessages.length; i++) {
                Message message = arrayMessages[i];
                Address[] fromAddress = message.getFrom();
                String from = fromAddress[0].toString();
                String subject = message.getSubject();
                String sentDate = message.getSentDate().toString();
 
                String contentType = message.getContentType();
                String messageContent = "";
 
                // store attachment file name, separated by comma
                String attachFiles = "";
 
                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            String fileName = part.getFileName();
                            attachFiles += fileName + ", ";
                            part.saveFile(saveDirectory + File.separator + subject+"_"+Integer.toString(filenumber)+".txt");
                            filenumber++;
                            System.out.println("\tdownload successfull...");
                        } else {
                            // this part may be the message content
                            messageContent = part.getContent().toString();
                        }
                    }
 
                    if (attachFiles.length() > 1) {
                        attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                    }
                } else if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    Object content = message.getContent();
                    if (content != null) {
                        messageContent = content.toString();
                    }
                }
 
                // print out details of each message
                System.out.println("Message #" + (i + 1) + ":");
                System.out.println("\t From: " + from);
                System.out.println("\t Subject: " + subject);
                System.out.println("\t Sent Date: " + sentDate);
                System.out.println("\t Message: " + messageContent);
                System.out.println("\t Attachments: " + attachFiles);
            }
 
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for pop3.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /** I have absolutely on idea how this works!!!
     * be sure to review!!!
     */
    public void downloadEmailAttachmentsIMAP(String host, String port,
            String userName, String password, int filenumber) throws MessagingException, IOException {
    	  // inspired by :
    	  // http://www.mikedesjardins.net/content/2008/03/using-javamail-to-read-and-extract/
    	  //
    	    Folder folder = null;
    	    Store store = null;
    	    Properties props = System.getProperties();
    	    props.setProperty("mail.store.protocol", "imaps");
    	    Session session = Session.getDefaultInstance(props, null);
    	    store = session.getStore("imaps");
    	    System.out.println(host+userName+password);
    	    try{
    	    store.connect(host,userName,password);//some kind of issue here
    	    }catch(Exception e){System.out.println("Didn't work");}
    	    
    	    try {
    	      session.setDebug(true);
    	      System.out.println("In try");
    	      folder = store.getFolder("Inbox");
    	      /* Others GMail folders :
    	       * [Gmail]/All Mail   This folder contains all of your Gmail messages.
    	       * [Gmail]/Drafts     Your drafts.
    	       * [Gmail]/Sent Mail  Messages you sent to other people.
    	       * [Gmail]/Spam       Messages marked as spam.
    	       * [Gmail]/Starred    Starred messages.
    	       * [Gmail]/Trash      Messages deleted from Gmail.
    	       */
    	      folder.open(Folder.READ_WRITE);
    	      Message messages[] = folder.getMessages();
    	      System.out.println("No of Messages : " + folder.getMessageCount());
    	      System.out.println("No of Unread Messages : " + folder.getUnreadMessageCount());
    	      for (int i=0; i < messages.length; ++i) {
    	        System.out.println("MESSAGE #" + (i + 1) + ":");
    	        Message msg = messages[i];
    	        /*
    	          if we don''t want to fetch messages already processed
    	          if (!msg.isSet(Flags.Flag.SEEN)) {
    	             String from = "unknown";
    	             ...
    	          }
    	        */
    	        String from = "unknown";
    	        if (msg.getReplyTo().length >= 1) {
    	          from = msg.getReplyTo()[0].toString();
    	        }
    	        else if (msg.getFrom().length >= 1) {
    	          from = msg.getFrom()[0].toString();
    	        }
    	        String subject = msg.getSubject();
    	        System.out.println("Saving ... " + subject +" " + from);
    	        // you may want to replace the spaces with "_"
    	        // the TEMP directory is used to store the files
    	        String filename = "c:/temp/" +  subject;
    	        saveParts(msg.getContent(), filename);
    	        msg.setFlag(Flags.Flag.SEEN,true);
    	        // to delete the message
    	        // msg.setFlag(Flags.Flag.DELETED, true);
    	      }
    	    }
    	    finally {
    	      if (folder != null) { folder.close(true); }
    	      if (store != null) { store.close(); }
    	    }
    	  }

    	  public static void saveParts(Object content, String filename)
    	  throws IOException, MessagingException
    	  {
    	    OutputStream out = null;
    	    InputStream in = null;
    	    try {
    	      if (content instanceof Multipart) {
    	        Multipart multi = ((Multipart)content);
    	        int parts = multi.getCount();
    	        for (int j=0; j < parts; ++j) {
    	          MimeBodyPart part = (MimeBodyPart)multi.getBodyPart(j);
    	          if (part.getContent() instanceof Multipart) {
    	            // part-within-a-part, do some recursion...
    	            saveParts(part.getContent(), filename);
    	            System.out.println("--->>>>"+filename);
    	          }
    	          else {
    	            String extension = "";
    	            if (part.isMimeType("text/html")) {
    	              extension = "html";
    	            }
    	            else {
    	              if (part.isMimeType("text/plain")) {
    	                extension = "txt";
    	              }
    	              else {
    	                //  Try to get the name of the attachment
    	                extension = part.getDataHandler().getName();
    	              }
    	              filename = filename + "." + extension;
    	              System.out.println("... " + filename);
    	              out = new FileOutputStream(new File(filename));
    	              in = part.getInputStream();
    	              int k;
    	              while ((k = in.read()) != -1) {
    	                out.write(k);
    	              }
    	            }
    	          }
    	        }
    	      }
    	    }
    	    finally {
    	      if (in != null) { in.close(); }
    	      if (out != null) { out.flush(); out.close(); }
    	    }
    	  }
    	  
    /**
     * Runs this program with Gmail POP3 server
     * @throws IOException 
     */
    public static void run() throws IOException {
    	String userName = "", password = "", host = "", port = "", next = "";
        int count = 0;
        String saveDirectory = "src/inbox";
        Queue<String> queue = new LinkedList<String>();
        BufferedReader br = new BufferedReader(new FileReader("src/proxies/proxies.txt"));
        while((next = br.readLine())!=null) {
        	queue.add(next);
        }
        br.close();

        while(!queue.isEmpty()) {
        	count++;
        	userName = queue.poll();
        	password = queue.poll();
        	ReceiveMail receiver = new ReceiveMail();
	        receiver.setSaveDirectory(saveDirectory);
        	System.out.println("username: "+userName+"\npassword: "+password);
        	if(userName.contains("@gmail.com")) {
    	        host = "pop.gmail.com";
    	        port = "995";
    	        receiver.downloadEmailAttachmentsPOP3(host, port, userName, password,count);
        	}
        	else if(userName.contains("@yahoo.com")) {
        		host = "smtp.mail.yahoo.com";
                port = "587";
                try {receiver.downloadEmailAttachmentsIMAP(host, port, userName, password,count);
                }catch(Exception e) {}
        	}
	        
	        
        }
 
    }
    
}