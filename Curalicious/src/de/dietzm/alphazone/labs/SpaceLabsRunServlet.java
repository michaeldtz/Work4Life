package de.dietzm.alphazone.labs;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.dietzm.alphazone.repository.model.RepositoryItem;
import de.dietzm.alphazone.repository.model.RepositoryProject;
import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;

public class SpaceLabsRunServlet extends HttpServlet {

	private static final long serialVersionUID = 6002812714165428741L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {

		PrintWriter out = new PrintWriter(resp.getWriter());
		String pathInfo = req.getPathInfo();

		// Remove first /
		if (pathInfo.startsWith("/"))
			pathInfo = pathInfo.substring(1);

		String projectId = pathInfo;

		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");
		RepositoryProject project = projectDAO.get(new Long(projectId));

		if (project != null) {
			
			Long startupItemId = project.getStartupItem();

			if (startupItemId != null) {

				DAO<RepositoryItem> itemDAO = DAOFactory.getEntityManager("RepositoryItem");
				RepositoryItem startItem = itemDAO.get(project.getStartupItem());

				if (startItem != null) {

					
					
					String projectName = project.getName();
					String contentName = startItem.getName();

					resp.setContentType("text/html");

					out.println("<html><head><title>Space Labs</title>");
					out.println("<link href=\"/resources/styles/all.css\" rel=\"stylesheet\" type=\"text/css\"	media=\"all\" />");
					out.println("</head><body style='overflow:hidden;'>");
					out.println("<div id='navigationArea' style='webkit-box-shadow:none; moz-box-shadow:none; box-shadow:none;border-bottom:2px solid #CCC;'>");
					out.println("<div id='navigationAreaInner'>");
					out.println("<span id='mainTitle' class='titleBold link' onclick='javascript:window.location=\"/\";'>Space Labs |</span>");
					out.println("<span id='name' class=''>" + projectName + "</span>");
					out.println("</div></div>");
					//out.println("<div id='headerSpacer'></div>");
					out.println("<iframe src='/repo/" + projectId + "/" + contentName
							+ "' width='100%' height='100%' style='padding-top:70px;box-sizing:border-box;border:none;'></div>");
					out.println("</body></html>");

				} else {
					out.println("Error: No startable item");
				}
			} else {
				out.println("Error: No startable item");
			}
		} else {
			out.println("Error: Project not found");
		}

	}

}
