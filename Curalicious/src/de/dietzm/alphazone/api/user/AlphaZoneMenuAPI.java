package de.dietzm.alphazone.api.user;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.appengine.labs.repackaged.org.json.JSONException;

import de.dietzm.foundation.interfaces.ResponseBuilder;
import de.dietzm.foundation.useraccess.UserAccessFactory;

@Path("/alphazonemenu")
public class AlphaZoneMenuAPI {

	class MenuEntry{
		
		private String name;
		private String template;
		public MenuEntry(String name, String template) {
			this.name = name;
			this.template = template;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTemplate() {
			return template;
		}
		public void setTemplate(String template) {
			this.template = template;
		}
			
	}
	
	/**
	 * Item Handling
	 * 
	 * @throws UnsupportedEncodingException
	 */

	@GET
	@Path("/user/list")
	@Produces("application/json")
	public Response listAlphazoneMenu() throws JSONException, UnsupportedEncodingException {
		
		
		ArrayList<MenuEntry> entries = new ArrayList<MenuEntry>();
		
		if(UserAccessFactory.getAccessService().isInGroup("ROLE_CORE_USER_ALPHAZONE")){
			entries.add(new MenuEntry("Your Project", "partials/projects.html"));
			entries.add(new MenuEntry("Shared Project", "partials/sharedprojects.html"));
		}
		
		return ResponseBuilder.createListJSONResponse(entries, true);
		
		
		
	}

	
}
