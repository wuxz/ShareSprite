package com.zhuaiwa.util;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

public class GetMail {
    public static String receive(String popServer, String popUser, String popPassword) {
        String mailContent = "";
        Store store = null;
        Folder folder = null;
        try {
            Properties props = System.getProperties();
//            props.put("mail.pop3.user", "");
//            props.put("mail.pop3.host", "");
//            props.put("mail.pop3.port", "");
            //props.put("", "");
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("pop3");
            store.connect(popServer, popUser, popPassword);
            folder = store.getDefaultFolder();
            if(folder == null) throw new Exception("No default folder");
            folder = folder.getFolder("INBOX");
            if(folder == null) throw new Exception("No POP3 INBOX");
//            folder.open(Folder.READ_ONLY);
            folder.open(Folder.READ_WRITE);
            Message[] msgs = folder.getMessages();
            System.out.println("message count : " + msgs.length);
            System.out.println("unread message count : " + folder.getUnreadMessageCount());
            System.out.println("new message count : " + folder.getNewMessageCount());
//            for(int msgNum = 0; msgNum < msgs.length; msgNum++) {
//                mailContent = mailContent + getMessage(msgs[msgNum]) + "\n\n\n\n";
//            }
            
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                if (folder!=null) folder.close(false);
                if (store!=null) store.close();
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        return mailContent;
    }

    public static String getMessage(Message message) {
    	try {
			message.setFlag(Flag.SEEN, true);
			 Flags flags = message.getFlags();
			 Flags.Flag[] sf = flags.getSystemFlags();
			 for (int i = 0; i < sf.length; i++) {
			        if (sf[i] == Flags.Flag.DELETED)
			            System.out.println("DELETED message");
			        else if (sf[i] == Flags.Flag.SEEN)
			            System.out.println("SEEN message");
			        else if(sf[i] == Flags.Flag.RECENT)
			        	System.out.println("Recent message");
			        else
			        	System.out.println("others");
			 }
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String mailContent = null;
        mailContent = "====new mail:====\n" + mailContent; 
        try {
            String from = ((InternetAddress)message.getFrom()[0]).getPersonal();
            if(from==null) from = ((InternetAddress)message.getFrom()[0]).getAddress();
            mailContent = "FROM: "+from;
            String subject = message.getSubject();
            mailContent = mailContent + "\n" +"SUBJECT: "+subject;
            mailContent = mailContent + "\n" + "ReceivedDate:" + message.getReceivedDate();
            /*
            Part messagePart = message;
            Object content = messagePart.getContent();
            if(content instanceof Multipart) {
                messagePart = ((Multipart)content).getBodyPart(0);
                mailContent = mailContent + "\n" +"[ Multipart Message ]";
            }
            mailContent = mailContent + "\n" +"CONTENT: "+content.toString();
            String contentType = messagePart.getContentType();
            mailContent = mailContent + "\n" +"CONTENT:"+contentType;
            if(contentType.startsWith("text/plain") || contentType.startsWith("text/html")) {
                InputStream is = messagePart.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String thisLine = reader.readLine();
                while(thisLine!=null) {
                    mailContent = mailContent + "\n" +thisLine;
                    thisLine = reader.readLine();
                }
            }
            */
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return mailContent;
    } 
    
    public static void main(String[] args) {
    	System.out.println(GetMail.receive("mail.zhuaiwa.com", "services", "zhuaiwa"));
	}
}
