package de.dietzm.alphazone.repository;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import com.google.appengine.api.datastore.Blob;

import de.dietzm.alphazone.dex.dexServices.JSServletRequest;
import de.dietzm.alphazone.dex.dexServices.JSServletResponse;
import de.dietzm.alphazone.entitystore.scriptobject.EntityStoreScriptObject;
import de.dietzm.alphazone.repository.model.RepositoryItem;
import de.dietzm.alphazone.repository.model.RepositoryProject;
import de.dietzm.alphazone.security.AuthenicationService;
import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;


public class RepositoryServlet extends HttpServlet {

	private static final long serialVersionUID = 6002812714165428741L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
	IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
	IOException {
		doGet(req, resp);
	}
	
	
	@Override
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

		String param = req.getParameter("no_base64_decode");
		boolean noBase64Decode = false;
		if(param != null && !param.equals(""))
			noBase64Decode = true;
		
		// Extract project ID
		int split = pathInfo.indexOf("/");
		String projectId = pathInfo.substring(0, split);
		String contentName = pathInfo.substring(split+1);

		//Get Project
		Long projID = new Long(projectId);
		DAO<RepositoryProject> projDAO = DAOFactory.getEntityManager("RepositoryProject");		
		RepositoryProject project = projDAO.get(projID);

		if(!contentName.startsWith("papi/")){	
			//Check Authorization
			AuthenicationService auth = new AuthenicationService();
			if(auth.isUserAuthorizedToExecute(project) <= 0){
				resp.sendError(403);
				return;
			}	
		}
		
		//Log Content Item
		DAO<RepositoryItem> itemDAO = DAOFactory.getEntityManager("RepositoryItem");
		List<RepositoryItem> contentList = itemDAO.query("name =", contentName);

		RepositoryItem reqContent = null;

		for (RepositoryItem content : contentList) {
			if (content.getProjectID().toString().equals(projectId)){
				reqContent = content;
			}
		}
			
		if (reqContent == null) {
			resp.sendError(404);
		} else if(contentName.endsWith(".sjs")) {
			
			RequestDispatcher reqDisp = req.getRequestDispatcher("/dex/" + pathInfo);
			reqDisp.forward(req, resp);
			return;
		
		} else if(contentName.endsWith(".etd")) {
			
			try {
					
				String entityName = contentName.substring(0, contentName.length()-4);
				
				// Init context and scope
				Context ctx = Context.enter();
				ScriptableObject scope = ctx.initStandardObjects();
				
				JSServletRequest jsReq = new JSServletRequest(req, contentName, scope);
				JSServletResponse jsResp = new JSServletResponse(resp);
				
				EntityStoreScriptObject scriptObj = new EntityStoreScriptObject(projID, getServletContext());
			
				scriptObj.api(entityName, jsReq, jsResp);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			
			String charSet = "UTF-8";
			String contentTypeCharSet = ";charset=" + charSet;
			if (contentName.endsWith(".js")) {
				resp.setContentType("text/javascript" + contentTypeCharSet);
			} else if (contentName.endsWith(".css")) {
				resp.setContentType("text/css" + contentTypeCharSet);
			} else if (contentName.endsWith(".html")) {
				resp.setContentType("text/html" + contentTypeCharSet);
			} else if (contentName.endsWith(".htm")) {
				resp.setContentType("text/html" + contentTypeCharSet);
			} else if (contentName.endsWith(".png")) {
				resp.setContentType("image/png" + contentTypeCharSet);
				resp.setHeader("Cache-Control", "public, max-age=600");
			} else if (contentName.endsWith(".jpg")) {
				resp.setContentType("image/jpg" + contentTypeCharSet);
			} else if (contentName.endsWith(".gif")) {
				resp.setContentType("image/gif" + contentTypeCharSet);
			} else if (contentName.endsWith(".json")) {
				resp.setContentType("application/json" + contentTypeCharSet);
			} else if (contentName.endsWith(".pdf")) {
				resp.setContentType("application/pdf" + contentTypeCharSet);
			} else {
				resp.setContentType("text/plain" + contentTypeCharSet);
			}

			Blob blob = reqContent.getContentBlob();	
			
			if(blob == null){
				resp.sendError(404);
				return;
			}
			
			byte[] blobContent = blob.getBytes();
			
			if(!noBase64Decode)
				blobContent = com.sun.jersey.core.util.Base64.decode(blobContent);
			
			OutputStream out = resp.getOutputStream();
			out.write(blobContent);
			out.flush();
	        out.close();
		}

	}

}
