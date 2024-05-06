package listener;

import javax.mail.MessagingException;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;

import actions.EmailNotification;
import database.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@WebListener
public class DailyDeadlineNotification implements ServletContextListener {
    private Timer timer;

    public void contextInitialized(ServletContextEvent sce) {
        timer = new Timer();
        scheduleDailyTask();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void scheduleDailyTask() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTime().before(new Date())) {
            calendar.add(Calendar.DATE, 1);
        }

        long period = 86400000; // 24 hours in milliseconds
        timer.scheduleAtFixedRate(new ReminderTask(), calendar.getTime(), period);
    }

    class ReminderTask extends TimerTask {
        public void run() {
            sendReminders();
        }
    }
    
    private void sendReminders() {
    	List<String> studentEmailList = new ArrayList<String>();
    	List<String> supervisorEmailList = new ArrayList<String>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement studentStmt = conn.prepareStatement(
                     " SELECT s.student_id, s.email "
                   + " FROM students s "
                   + " JOIN deadlines d ON s.department = d.department "
                   + " LEFT JOIN preferences p ON s.student_id = p.student_id "
                   + " WHERE p.student_id IS NULL AND d.student_deadline > NOW()");
             PreparedStatement staffStmt = conn.prepareStatement(
                     " SELECT st.staff_id, st.email "
                   + " FROM staff st "
                   + " JOIN deadlines d ON st.department = d.department "
                   + " LEFT JOIN supervisor_prefs sp ON st.staff_id = sp.staff_id "
                   + " WHERE sp.staff_id IS NULL AND d.staff_deadline > NOW() ");
             ResultSet rsStudents = studentStmt.executeQuery();
             ResultSet rsStaff = staffStmt.executeQuery()) {

            while (rsStudents.next()) {
                String studentEmail = rsStudents.getString("email");
                studentEmailList.add(studentEmail);
            }

            while (rsStaff.next()) {
                String staffEmail = rsStaff.getString("email");
                supervisorEmailList.add(staffEmail);
            }
            
            if(!studentEmailList.isEmpty()) {
            	String subject = "Reminder: Submit Your Preferences (MSc. Individual Project Testing)";
            	String message = "Hello,\n\n" + "This is a reminder to submit your preferences before the deadline. "
            	+ "Please ignore this message if already submitted.\n\n" + "Best Regards,\n"
            			+"MSc. Individual Project Testing";
            	
            	EmailNotification sendEmail = new EmailNotification();
            	try {
					sendEmail.sendEmailToRecipients(studentEmailList, subject, message);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            if(!supervisorEmailList.isEmpty()) {
            	String subject = "Reminder: Submit Your Preferences (MSc. Individual Project Testing)";
            	String message = "Hello,\n\n" + "This is a reminder to submit your preferences before the deadline. "
            	+ "Please ignore this message if already submitted.\n\n" + "Best Regards,\n"
            			+"MSc. Individual Project Testing";
            	
            	EmailNotification sendEmail = new EmailNotification();
            	try {
					sendEmail.sendEmailToRecipients(supervisorEmailList, subject, message);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
