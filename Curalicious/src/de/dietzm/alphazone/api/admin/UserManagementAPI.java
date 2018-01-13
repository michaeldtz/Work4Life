package de.dietzm.alphazone.api.admin;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;
import de.dietzm.foundation.useraccess.SLGroupAccessService;
import de.dietzm.foundation.usermgmt.model.SLRole;

@Path("/usermgmt")
public class UserManagementAPI {

	@GET
	@Path("/userrole/list")
	@Produces("application/json")
	public Response getAllRoles(@QueryParam("id") long id) throws JSONException, UnsupportedEncodingException {

		SLGroupAccessService roleService = new SLGroupAccessService();
		List<SLRole> roles = roleService.getAllRoles();
		
		JSONArray jsonList = new JSONArray();
		for (SLRole role : roles) {
			JSONObject json = new JSONObject();
			json.put("id", role.getId());
			json.put("name", role.getRolename());
			jsonList.put(json);
		}
		
		return Response.ok(jsonList.toString()).build();
	}
	
	@POST
	@Path("/userrole/create")
	@Produces("application/json")
	public Response createRole(@FormParam("name") String name) throws JSONException, UnsupportedEncodingException {

		DAO<SLRole> roleDAO = DAOFactory.getEntityManager("SLRole");
		
		SLRole role = new SLRole();
		role.setDescription(name);
		
		String techName = name.toUpperCase().replace(" ", "_");
		role.setRolename(techName); 
	
		roleDAO.create(role);
		
		JSONObject json = new JSONObject();
		json.put("newId", role.getId());
		json.put("success", true);
		
		return Response.ok(json.toString()).build();
	}
	
	
	
}
