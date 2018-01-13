package de.dietzm.foundation.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.dietzm.foundation.configuration.ConfigurationManager;
import de.dietzm.foundation.initilization.InitializationError;
import de.dietzm.foundation.initilization.InitializationManager;
import de.dietzm.foundation.initilization.InitializationManager.InitializationResult;
import de.dietzm.foundation.interfaces.ResponseBuilder;

@Path("/administrator")
public class AdministratorService {

	@GET
	@Path("/public/isInitilized")
	@Produces("application/json")
	public Response isInitilized() {
		
		String initStatus = ConfigurationManager.getInstance().getString("INIT_STATUS", "NEVER");
		int initVersion = ConfigurationManager.getInstance().getInt("INIT_VERSION", 0);
		
		if(initStatus.equals("NS")){
			return ResponseBuilder.createFlexibleResponse("InitializationStatus", "NeverStarted", "InitVersion", new Integer(initVersion).toString());
		} else if(initStatus.equals("STARTED")){
			return ResponseBuilder.createFlexibleResponse("InitializationStatus", "Started", "InitVersion", new Integer(initVersion).toString());
		} else if(initStatus.equals("FINISHED")){
			return ResponseBuilder.createFlexibleResponse("InitializationStatus", "Finished", "InitVersion", new Integer(initVersion).toString());
		} else if(initStatus.equals("ERROR")){
			return ResponseBuilder.createFlexibleResponse("InitializationStatus", "Error", "InitVersion", new Integer(initVersion).toString());
		} else {
			return ResponseBuilder.createFlexibleResponse("InitializationStatus", "Unknown_" + initStatus, "InitVersion", new Integer(initVersion).toString());
		}
		
	}
	
	@POST
	@Path("/admin/initialize")
	@Produces("application/json")
	public Response initialize() {



		InitializationResult result;
		try {
			
			result = InitializationManager.getInstance().executeInitialization();
		
			if(result.getResult() <= 1){
				return ResponseBuilder.createSuccessResponse();
			} else {
				return ResponseBuilder.createErrorResponse("Initialization Failed", "INIT01");
			}
		
		} catch (InitializationError e) {
			return ResponseBuilder.createErrorResponse("Initialization Failed with Exception", "INIT02");

		}

	}
}
