package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/SetNumGroupsServlet")
public class SetNumGroupsServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) {
        String numGroups = request.getParameter("numGroups");
        HttpSession session = request.getSession();
        session.setAttribute("numGroupsSelected", numGroups);
    }
}