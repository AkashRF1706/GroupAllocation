package actions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.cj.MysqlConnection;

import database.MySQLConnection;

@WebServlet("/releaseResults")
public class EmailNotification extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	            emailMessage.setFrom(new InternetAddress(fromEmail)); //Setting From Address
	            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); //Setting To Address
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
	            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email)); //Set each email as BCC
	        }
	        message.setSubject(subject);
	        message.setText(messageBody);

	        Transport.send(message);
	        System.out.println("Sent reminder to: " + emailList.size() + " recipients");
	    } catch (MessagingException mex) {
	        mex.printStackTrace();
	    }
	}

	protected synchronized void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String[] emails = req.getParameterValues("emails");
		 String updateStudents = "UPDATE students SET is_group_allocated = 'Y' WHERE email IN (";
		 String updateStaff = "UPDATE staff SET is_group_allocated = 'Y' WHERE email IN (";
		 
		 String placeholders = String.join(", ", Collections.nCopies(emails.length, "?"));
		    updateStudents += placeholders + ")";
		    updateStaff += placeholders + ")";
		
		List<String> allEmails = new ArrayList<>(Arrays.asList(emails));
		try {
			Connection conn = MySQLConnection.getConnection();
			PreparedStatement stmtStudents = conn.prepareStatement(updateStudents);
	         PreparedStatement stmtStaff = conn.prepareStatement(updateStaff);
	         
	         for (int i = 0; i < emails.length; i++) {
	             stmtStudents.setString(i + 1, emails[i]);
	         }
	         int studentUpdateCount = stmtStudents.executeUpdate();
	         for (int i = 0; i < emails.length; i++) {
	             stmtStaff.setString(i + 1, emails[i]);
	         }
	         
	         int staffUpdateCount = stmtStaff.executeUpdate();
	         if(studentUpdateCount > 0 && staffUpdateCount > 0) {
	        	 sendEmailToRecipients(allEmails, "Group Allocations Released", "Hi,\n\n This is to notify you about your group allocation results have been released.\n\nBest Regards,\nMSc. Project Testing");
	        	 res.sendRedirect("formedGroups.jsp?success");
	         } else {
	        	 res.sendRedirect("formedGroups.jsp?failure");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.sendRedirect("formedGroups.jsp?failure");
			e.printStackTrace();
		}
	}

}
