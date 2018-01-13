package de.dietzm.alphazone.dex;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.dietzm.alphazone.repository.model.RepositoryItem;
import de.dietzm.alphazone.repository.model.RepositoryProject;
import de.dietzm.alphazone.security.AuthenicationService;
import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;

public class DynamicExecutionServlet extends HttpServlet {

	private static final long serialVersionUID = 6002812714165428741L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DynamicExecutionServlet.class.getName());

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doGet(req, resp);
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doGet(req, resp);
	}

	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {

		String pathInfo = req.getPathInfo();

		// Remove first /
		if (pathInfo.startsWith("/"))
			pathInfo = pathInfo.substring(1);

		// Extract project ID
		int split = pathInfo.indexOf("/");
		String projectId = pathInfo.substring(0, split);
		String sscriptName = pathInfo.substring(split + 1);

		// Get Project
		Long projID = new Long(projectId);
		DAO<RepositoryProject> projDAO = DAOFactory.getEntityManager("RepositoryProject");
		RepositoryProject project = projDAO.get(projID);

		if(!sscriptName.startsWith("papi/")){
		
			// Check Authorization
			AuthenicationService auth = new AuthenicationService();
			if (auth.isUserAuthorizedToExecute(project) <= 0) {
				resp.sendError(403);
				return;
			}
	
			if (!sscriptName.endsWith(".sjs")) {
				resp.sendError(500);
				return;
			}
		
		}

		DAO<RepositoryItem> dao = DAOFactory.getEntityManager("RepositoryItem");
		List<RepositoryItem> contentList = dao.query("name =", sscriptName);

		// Get Contentq
		RepositoryItem reqContent = null;
		for (RepositoryItem content : contentList) {
			if (content.getProjectID().toString().equals(projectId)) {
				reqContent = content;
			}
		}

		// Content Found?
		if (reqContent == null) {
			resp.sendError(404);
			return;
		}

		JSExecutor exec = new JSExecutor();

		try {
			String result = exec.execute(getServletContext(), sscriptName, req, resp, reqContent);
			
			if(result != null)
				resp.getWriter().println(result);
			
		} catch (Exception e) {
			resp.getWriter().println(e.toString());
		}

	}

}
