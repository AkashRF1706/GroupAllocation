package servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import com.google.gson.Gson;
import database.MessagesDAO;
import model.Message;

import java.util.List;

@WebServlet("/FetchMessagesServlet")
public class FetchMessagesServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String groupId = request.getParameter("groupId");
        List<Message> messages = MessagesDAO.getMessagesForGroup(groupId);
        String messagesJson = gson.toJson(messages);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(messagesJson);
    }
}
