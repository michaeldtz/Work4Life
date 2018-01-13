package de.dietzm.foundation.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import de.dietzm.foundation.interfaces.ResponseBuilder;
import de.dietzm.foundation.useraccess.UserAccessFactory;
import de.dietzm.foundation.usermgmt.model.SLUser;

@Path("/session")
public class SessionService  {

	@GET
	@Path("/public/check")
	@Produces("application/json")
	public Response checkSession() {

		String username = UserAccessFactory.getAccessService().getCurrentUserName();
		if (username == null) {
			return ResponseBuilder.createFlexibleResponse("session","false");
			
		} else {
			return ResponseBuilder.createFlexibleResponse("session","true","username", username);
		}

	}
	
	@GET
	@Path("/public/userhasrole")
	@Produces("application/json")
	public Response userHasRole(@QueryParam("role") String rolename) {

		if(UserAccessFactory.getAccessService().isInGroup(rolename)){
			return ResponseBuilder.createSuccessResponse();
		} else {
			return ResponseBuilder.createErrorResponse("User doesn't have role " + rolename, "USERROLE01");
		}

	}

	@GET
	@Path("/user/check")
	@Produces("application/json")
	public Response checkSessionAlternative() {
		return checkSession();
	}

	@GET
	@Path("/user/getuserinfo") 
	@Produces("application/json")
	public Response getUserInfo() {

		SLUser user = UserAccessFactory.getAccessService().getCurrentUser();
		if (user == null) {
			return ResponseBuilder.createErrorResponse("No session active", "SES01");
		} else {
			return ResponseBuilder.createObjectJSONResponse(user, false);
		}
	}

}
