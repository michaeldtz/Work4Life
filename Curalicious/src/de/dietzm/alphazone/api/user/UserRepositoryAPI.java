package de.dietzm.alphazone.api.user;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.sun.jersey.core.util.Base64;

import de.dietzm.alphazone.entitystore.exceptions.EntityExistsException;
import de.dietzm.alphazone.repository.model.RepositoryItem;
import de.dietzm.alphazone.repository.model.RepositoryProject;
import de.dietzm.alphazone.security.AuthenicationService;
import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;
import de.dietzm.foundation.interfaces.JSONBuilder;
import de.dietzm.foundation.interfaces.ResponseBuilder;
import de.dietzm.foundation.useraccess.UserAccessFactory;

@Path("/repository")
public class UserRepositoryAPI {

	/**
	 * Item Handling
	 * 
	 * @throws UnsupportedEncodingException
	 */

	@GET
	@Path("/getItem")
	@Produces("application/json")
	public Response getItemDetails(@QueryParam("id") long id) throws JSONException,
			UnsupportedEncodingException {

		DAO<RepositoryItem> itemDAO = DAOFactory.getEntityManager("RepositoryItem");
		RepositoryItem item = itemDAO.get(id);

		Blob blob = item.getContentBlob();

		if (blob != null)
			item.setContent(new String(blob.getBytes()));

		return ResponseBuilder.createObjectJSONResponse(item, true);
		//return Response.ok(item).build();
	}

	@GET
	@Path("/listItems")
	@Produces("application/json")
	public Response getItemList() throws JSONException {

		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;
		List<RepositoryItem> repoContentList = repoDAO.queryAll();

		JSONArray jsonList = new JSONArray();

		for (RepositoryItem repoContent : repoContentList) {
			JSONObject json = new JSONObject();
			json.put("id", repoContent.getId());
			json.put("name", repoContent.getName());
			jsonList.put(json);
		}

		return Response.ok(jsonList.toString()).build();
	}

	@POST
	@Path("/updateItem")
	@Consumes("application/json")
	@Produces("application/json")
	public Response updateItem(String jsonData) throws JSONException, UnsupportedEncodingException {

		RepositoryItem item = (RepositoryItem) JSONBuilder.convertJSON2Object(jsonData, RepositoryItem.class);
		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;

		AuthenicationService auth = new AuthenicationService();
		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");
		RepositoryProject project = projectDAO.get(item.getProjectID());
		if(auth.isUserAuthorizedToChange(project) == AuthenicationService.RESULT_NO_ACCESS)
			return ResponseBuilder.createErrorResponse("Not Authorized to Change", "REP01");
		
		String content = item.getContent();

		if (content != null && !content.equals("")) {
			Blob blob = new Blob(content.getBytes());
			item.setContentBlob(blob);
		}

		if(doesItemNameAlreadyExist(item.getName(), item.getProjectID(), item.getId())){
			return ResponseBuilder.createErrorResponse("There is already a file with this name in this project", "REP02");
		}
		
		
		repoDAO.update(item);

		JSONObject json = new JSONObject();
		json.put("success", true);

		return Response.ok(json.toString()).build();
	}
	
	@POST
	@Path("/updateItemAttribute")
	@Produces("application/json")
	public Response updateItemAttribute(@FormParam("id")long id, @FormParam("attribute")String attribute, @FormParam("value")String value) throws JSONException, UnsupportedEncodingException {

		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");
		RepositoryItem item = repoDAO.get(id);
		
		AuthenicationService auth = new AuthenicationService();
		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");
		
		RepositoryProject project = projectDAO.get(item.getProjectID());
		
		if(auth.isUserAuthorizedToChange(project) == AuthenicationService.RESULT_NO_ACCESS)
			return ResponseBuilder.createErrorResponse("Not Authorized to Change", "REP03");
		
		if(attribute.equalsIgnoreCase("NAME")){
			
			//Check if name is already in use
			if(doesItemNameAlreadyExist(value, item.getProjectID(), item.getId())){
				return ResponseBuilder.createErrorResponse("There is already a file with this name in this project", "REP05");
			}
			item.setName(value);
		}
					
		
		repoDAO.update(item);

		JSONObject json = new JSONObject();
		json.put("success", true);

		return Response.ok(json.toString()).build();
	}

	@POST
	@Path("/updateItemContent")
	@Produces("application/json")
	public Response updatItemContent(@FormParam("id") long id, @FormParam("content") String content)
			throws JSONException, UnsupportedEncodingException {

		
		JSONObject json = new JSONObject();

		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");
		RepositoryItem repoContent = repoDAO.get(id);

		AuthenicationService auth = new AuthenicationService();
		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");
		RepositoryProject project = projectDAO.get(repoContent.getProjectID());
		if(auth.isUserAuthorizedToChange(project) == AuthenicationService.RESULT_NO_ACCESS)
			return ResponseBuilder.createErrorResponse("Not Authorized to Change", "REP06");
		
		Blob blob = new Blob(content.getBytes());
		repoContent.setContentBlob(blob);

		repoDAO.update(repoContent);

		json.put("success", true);

		return Response.ok(json.toString()).build();
	}

	@POST
	@Path("/createItem")
	@Produces("application/json")
	public Response createNewItem(@FormParam("name") String name, @FormParam("projectID") String projectID)
			throws JSONException {

		AuthenicationService auth = new AuthenicationService();
		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");;
		RepositoryProject project = projectDAO.get(new Long(projectID));
		if(auth.isUserAuthorizedToChange(project) == AuthenicationService.RESULT_NO_ACCESS)
			return ResponseBuilder.createErrorResponse("Not Authorized to Create", "REP07");
		
		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;		
		name = name.trim();
		
		//Check if name is already in use
		if(doesItemNameAlreadyExist(name, new Long(projectID), null)){
			return ResponseBuilder.createErrorResponse("There is already a file with this name in this project", "REP08");
		}
		
		RepositoryItem newContent = new RepositoryItem();
		newContent.setName(name);
		newContent.setProjectID(new Long(projectID));
		newContent.setContentBlob(new Blob("".getBytes()));


		Long newId = repoDAO.create(newContent);

		// This ensures that the object is ready in the db
		repoDAO.get(newId);

		JSONObject json = new JSONObject();
		json.put("newId", newId);
		json.put("success", true);


		return Response.ok(json.toString()).build();
	}

	private boolean doesItemNameAlreadyExist(String name, Long projectID, Long itemID) {
		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;
		
		List<RepositoryItem> items = repoDAO.query("name", name);
		for(RepositoryItem item : items){
			if(itemID != null && itemID.equals(item.getId()))
				continue;
			if(item.getProjectID().equals(projectID))
				return true;
		}
		return false;		
	}

	@POST
	@Path("/deleteItem")
	@Produces("application/json")
	public Response deleteItem(@FormParam("id") Long itemID) throws JSONException {


		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;
		RepositoryItem repoItem = repoDAO.get(itemID);
		
		AuthenicationService auth = new AuthenicationService();
		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");;
		RepositoryProject project = projectDAO.get(repoItem.getProjectID());
		if(auth.isUserAuthorizedToChange(project) == AuthenicationService.RESULT_NO_ACCESS)
			return ResponseBuilder.createErrorResponse("Not Authorized to Delete Item", "REP09");
		
		
		repoDAO.delete(repoItem);

		JSONObject json = new JSONObject();
		json.put("success", true);

		return Response.ok(json.toString()).build();
	}

	@POST
	@Path("/uploadNewItem")
	@Produces("application/json")
	public Response uploadNewItem(@QueryParam("projectID") String projectID,
			@FormParam("filename") String filename, @FormParam("content") String fileContent)
			throws JSONException, UnsupportedEncodingException, EntityExistsException {

		AuthenicationService auth = new AuthenicationService();
		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");
		RepositoryProject project = projectDAO.get(new Long(projectID));
		if(auth.isUserAuthorizedToChange(project) == AuthenicationService.RESULT_NO_ACCESS)
			return ResponseBuilder.createErrorResponse("Not Authorized to Create", "REP10");
		
		
		Blob contentBlob = new Blob(fileContent.getBytes());

		//Check if name is already in use
		if(doesItemNameAlreadyExist(filename, new Long(projectID), null)){
			return ResponseBuilder.createErrorResponse("There is already a file with this name in this project", "REP11");
		}
		
		RepositoryItem newContent = new RepositoryItem();
		newContent.setName(filename);
		newContent.setContentBlob(contentBlob);
		newContent.setProjectID(new Long(projectID));

		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;
		Long newId = repoDAO.create(newContent);

		// This ensures that the object is ready in the db
		repoDAO.get(newId);

		JSONObject json = new JSONObject();
		json.put("newId", newId);
		json.put("success", true);

		if (filename.endsWith(".zip")) {
			json.put("iszip", true);
		}

		return Response.ok(json.toString()).build();

	}

	@POST
	@Path("/unzipItem")
	@Produces("application/zip")
	public Response unzipItem(@FormParam("id") Long id) throws JSONException {

		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;
		RepositoryItem repoContent = repoDAO.get(id);

		AuthenicationService auth = new AuthenicationService();
		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");;
		RepositoryProject project = projectDAO.get(repoContent.getProjectID());
		if(auth.isUserAuthorizedToChange(project) == AuthenicationService.RESULT_NO_ACCESS)
			return ResponseBuilder.createErrorResponse("Not Authorized to Unzip", "REP13");
		
		
		Long projectID = repoContent.getProjectID();

		try {

			byte[] encContent = repoContent.getContentBlob().getBytes();
			byte[] content = Base64.decode(encContent);
			ByteArrayInputStream bais = new ByteArrayInputStream(content);
			ZipInputStream zipIn = new ZipInputStream(bais);

			ArrayList<RepositoryItem> itemList = new ArrayList<RepositoryItem>();
			ZipEntry zipentry = zipIn.getNextEntry();
			String commonPrefix = null;
			boolean hasSamePrefix = true;

			while (zipentry != null) {
				// for each entry to be extracted
				String entryName = zipentry.getName();
				String prefix = entryName.substring(0, entryName.indexOf("/"));

				if (commonPrefix != null && !commonPrefix.equals(prefix)) {
					hasSamePrefix = false;
				} else {
					commonPrefix = prefix;
				}

				byte[] newContent = IOUtils.toByteArray(zipIn);
				byte[] encNewContent = Base64.encode(newContent);
				Blob newBlob = new Blob(encNewContent);

				//Check if name is already in use
				if(doesItemNameAlreadyExist(entryName, new Long(projectID), null)){
					return ResponseBuilder.createErrorResponse("There is already a file with this name in this project", "REP14");
				}
				
				// Create item
				RepositoryItem newItem = new RepositoryItem();
				newItem.setName(entryName);
				newItem.setContentBlob(newBlob);
				newItem.setProjectID(projectID);
				itemList.add(newItem);

				zipIn.closeEntry();
				zipentry = zipIn.getNextEntry();

			}// while

			for (RepositoryItem item : itemList) {
				if (hasSamePrefix) {
					String name = item.getName();
					name = name.substring(name.indexOf("/") + 1);
					item.setName(name);
				}
				Long itemID = repoDAO.create(item);
				repoDAO.get(itemID);
			}

			JSONObject json = new JSONObject();
			json.put("success", true);
			return Response.ok(json.toString()).build();

		} catch (Exception e) {

			JSONObject json = new JSONObject();
			json.put("success", false);
			json.put("error", e.toString());

			return Response.ok(json.toString()).build();

		}
	}

	@GET
	@Path("/downloadProject")
	@Produces("application/zip")
	public StreamingOutput downloadProject(@QueryParam("projectID") Long projectID) throws JSONException,
			UnsupportedEncodingException {

		return new ProjectFilesStreamingOutput(projectID);
	}

	/**
	 * Project Handling
	 */

	@GET
	@Path("/listProjects")
	@Produces("application/json")
	public Response getProjectList() throws JSONException {

		AuthenicationService auth = new AuthenicationService();

		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");;
		List<RepositoryProject> repoProjectList = projectDAO.queryAll();

		JSONArray jsonList = new JSONArray();

		for (RepositoryProject repoProject : repoProjectList) {

			if(auth.isUserOwner(repoProject)){
			//if (auth.isUserAuthorizedToDisplay(repoProject) != AuthenicationService.RESULT_NO_ACCESS) {
				JSONObject json = new JSONObject();
				json.put("id", repoProject.getId());
				json.put("name", repoProject.getName());
				jsonList.put(json);
			}
		}

		return Response.ok(jsonList.toString()).build();
	}

	@GET
	@Path("/listProjectTemplates")
	@Produces("application/json")
	public Response getProjectTemplateList() throws JSONException {

		AuthenicationService auth = new AuthenicationService();

		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");;
		List<RepositoryProject> repoProjectList = projectDAO.query("template", new Boolean(true));

		JSONArray jsonList = new JSONArray();

		for (RepositoryProject repoProject : repoProjectList) {

			if(auth.isUserOwner(repoProject)){
			//if (auth.isUserAuthorizedToDisplay(repoProject) != AuthenicationService.RESULT_NO_ACCESS) {
				JSONObject json = new JSONObject();
				json.put("id", repoProject.getId());
				json.put("name", repoProject.getName());
				jsonList.put(json);
			}
		}

		return Response.ok(jsonList.toString()).build();
	}

	
	@GET
	@Path("/getProject")
	@Produces("application/json")
	public Response getProject(@QueryParam("id") Long projectID) throws JSONException {
		DAO<RepositoryProject> repoDAO = DAOFactory.getEntityManager("RepositoryProject");;
		RepositoryProject project = repoDAO.get(projectID);
		return Response.ok(project).build();
	}

	@POST
	@Path("/createProject")
	@Produces("application/json")
	public Response createNewProject(@FormParam("name") String name) throws JSONException {

		RepositoryProject newContent = new RepositoryProject();
		newContent.setName(name);
		newContent.setUserID(UserAccessFactory.getAccessService().getCurrentUserName());

		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");;
		Long newId = projectDAO.create(newContent);

		// This ensures that the object is ready in the db
		projectDAO.get(newId);

		JSONObject json = new JSONObject();
		json.put("newId", newId);
		json.put("success", true);

		return Response.ok(json.toString()).build();
	}

	@POST
	@Path("/createProjectFromTemplate")
	@Produces("application/json")
	public Response createProjectFromTemplate(@FormParam("name") String name, @FormParam("templateID") @DefaultValue("-1") Long templateID) throws JSONException {

		if(templateID == null || templateID == -1)
			return createNewProject(name);		
		
		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");;
		RepositoryProject templateProject = projectDAO.get(templateID);
		JSONObject json = new JSONObject();
		
		if(templateProject == null){
			templateProject = new RepositoryProject();
		}
		
		templateProject.setId(null);
		templateProject.setName(name);
		templateProject.setUserID(UserAccessFactory.getAccessService().getCurrentUserName());
		
		Long newId = projectDAO.create(templateProject);

		// This ensures that the object is ready in the db
		projectDAO.get(newId);

		
		//Get all files of templateProject
		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;
		List<RepositoryItem> repoContentList = repoDAO.query("projectID =", templateID);

		//Copy all items
		for (RepositoryItem repoContent : repoContentList) {
			repoContent.setId(null);
			repoContent.setProjectID(newId);
			Long newContentID = repoDAO.create(repoContent);
			repoDAO.get(newContentID);
			json.append("new_items", newContentID);
		}
		
		json.put("newId", newId);
		json.put("success", true);

		return Response.ok(json.toString()).build();
	}
	
	@POST
	@Path("/updateProject")
	@Consumes("application/json")
	@Produces("application/json")
	public Response updateProject(RepositoryProject project) throws JSONException,
			UnsupportedEncodingException {
		AuthenicationService auth = new AuthenicationService();
		DAO<RepositoryProject> repoDAO = DAOFactory.getEntityManager("RepositoryProject");;

		if (auth.isUserAuthorizedToChange(project) != AuthenicationService.RESULT_NO_ACCESS) {

			repoDAO.update(project);

			JSONObject json = new JSONObject();
			json.put("success", true);

			return Response.ok(json.toString()).build();
		} else {
			return ResponseBuilder.createErrorResponse("Not Authorizated", "REP20");
		}
	}

	@GET
	@Path("/project/listItems")
	@Produces("application/json")
	public Response getItemsOfProject(@QueryParam("projectID") Long projectID) throws JSONException {

		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;
		List<RepositoryItem> repoContentList = repoDAO.queryAndOrder("projectID", projectID, "name");
		//repoContentList = repoDAO.query("projectID", projectID);
		
		JSONArray jsonList = new JSONArray();

		for (RepositoryItem repoContent : repoContentList) {
			JSONObject json = new JSONObject();
			json.put("id", repoContent.getId());
			json.put("name", repoContent.getName());

			jsonList.put(json);
		}

		return Response.ok(jsonList.toString()).build();
	}

	@GET
	@Path("/project/listStartableItems")
	@Produces("application/json")
	public Response getStartableItemsOfProject(@QueryParam("projectID") Long projectID) throws JSONException {

		DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;
		List<RepositoryItem> repoContentList = repoDAO.queryAndOrder("projectID =", projectID, "name");

		JSONArray jsonList = new JSONArray();

		for (RepositoryItem repoContent : repoContentList) {
			if (repoContent.isStartable()) {
				JSONObject json = new JSONObject();
				json.put("id", repoContent.getId());
				json.put("name", repoContent.getName());
				jsonList.put(json);
			}
		}

		return Response.ok(jsonList.toString()).build();
	}

	@POST
	@Path("/deleteProject")
	@Produces("application/json")
	public Response deleteProject(@FormParam("id") Long projectID) throws JSONException {

		AuthenicationService auth = new AuthenicationService();
		DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");
		RepositoryProject proj = projectDAO.get(projectID);

		if (auth.isUserAuthorizedToChange(proj) != AuthenicationService.RESULT_NO_ACCESS) {

			DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;
			List<RepositoryItem> repoContentList = repoDAO.query("projectID =", projectID);
			repoDAO.delete(repoContentList);

			projectDAO.delete(proj);

			JSONObject json = new JSONObject();
			json.put("success", true);

			return Response.ok(json.toString()).build();
		} else {
			return ResponseBuilder.createErrorResponse("Not authorized to delete", "REP21");
		}
	}

	private class ProjectFilesStreamingOutput implements StreamingOutput {

		private Long projectID;

		public ProjectFilesStreamingOutput(Long projectID) {
			this.projectID = projectID;
		}

		@Override
		public void write(OutputStream output) throws IOException, WebApplicationException {

			DAO<RepositoryItem> repoDAO = DAOFactory.getEntityManager("RepositoryItem");;
			List<RepositoryItem> items = repoDAO.query("projectID =", projectID);

			DAO<RepositoryProject> projectDAO = DAOFactory.getEntityManager("RepositoryProject");
			RepositoryProject project = projectDAO.get(projectID);

			String folderName = project.getName().replaceAll(" ", "_");

			ZipOutputStream zipOut = new ZipOutputStream(output);
			for (RepositoryItem item : items) {

				Blob blob = item.getContentBlob();
				ZipEntry entry = new ZipEntry(folderName + "/" + item.getName());
				zipOut.putNextEntry(entry);
				if (blob != null) {
					byte[] blobContent = Base64.decode(blob.getBytes());
					zipOut.write(blobContent);
				}
				zipOut.closeEntry();
			}

			zipOut.flush();
			zipOut.close();
			output.close();

		}

	}

	
}
