package servlet;

import javax.mail.MessagingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import actions.EmailNotification;

@WebServlet("/SendEmailServlet")
public class SendEmailServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String emailsJson = request.getParameter("emails");
        String message = request.getParameter("message");
        String subject = request.getParameter("subject");
        
        List<String> emailToList = jsonToList(emailsJson);
        
        EmailNotification sendEmail = new EmailNotification();
        try {
			sendEmail.sendEmailToRecipients(emailToList, subject, message);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private List<String> jsonToList(String jsonArrayStr) {
        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }
}
