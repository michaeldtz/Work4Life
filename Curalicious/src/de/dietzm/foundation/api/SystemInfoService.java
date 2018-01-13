package de.dietzm.foundation.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@Path("/systeminfo")
public class SystemInfoService {

	@Autowired
	private User dao;
	
	@GET
	@Path("/public/info")
	@Produces("application/json")
	public Response getSystemInfo() throws JSONException {

		JSONObject json = new JSONObject();

		json.put("system", "GAE");

		return Response.ok(json.toString()).build();
	}
	
		
	@GET
	@Path("/admin/admininfo")
	@Produces("application/json")
	public Response getAdminSystemInfo(@Context SecurityContext secctx) throws JSONException {
		
		
		System.out.println(secctx.isUserInRole("ROLE_ADMIN"));
		System.out.println(secctx.getUserPrincipal());
		System.out.println(dao);
		
		JSONObject json = new JSONObject();

		json.put("system", "GAE");
		json.put("admininfo", "LetsGo");
		//json.put("user", username);

		return Response.ok(json.toString()).build();
	}
}
