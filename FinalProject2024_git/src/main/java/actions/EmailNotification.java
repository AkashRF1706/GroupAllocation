package actions;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailNotification{
	
	String host = "smtp.gmail.com";
	String port = "587";
	String fromEmail = "akashrf1706@gmail.com";
	String password = "riwg pldm qoqm odnt";

	public synchronized void sendEmail(String toEmail, String subject, String message) {
		
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		
		//Creating session
		
		 Session session = Session.getInstance(properties, new
		 javax.mail.Authenticator() { protected PasswordAuthentication
		 getPasswordAuthentication() { return new PasswordAuthentication(fromEmail,
		 password); } });
		 
		 try {
			 	MimeMessage emailMessage = new MimeMessage(session);
	            emailMessage.setFrom(new InternetAddress(fromEmail));
	            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
	            emailMessage.setSubject(subject);
	            emailMessage.setText(message);
	            Transport.send(emailMessage);
		 } catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
		}
	}

	public void sendEmailToRecipients(List<String> emailList, String subject, String messageBody) throws MessagingException {
	    
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		
		Session session = Session.getInstance(properties, new
				 javax.mail.Authenticator() { protected PasswordAuthentication
				 getPasswordAuthentication() { return new PasswordAuthentication(fromEmail,
				 password); } });

	    try {
	        MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(fromEmail));
	        for (String email : emailList) {
	            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email));
	        }
	        message.setSubject(subject);
	        message.setText(messageBody);

	        Transport.send(message);
	        System.out.println("Sent reminder to: " + emailList.size() + " recipients");
	    } catch (MessagingException mex) {
	        mex.printStackTrace();
	    }
	}


}
