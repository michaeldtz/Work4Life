package de.dietzm.foundation.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import de.dietzm.foundation.db.ApplicationDataStoreException;
import de.dietzm.foundation.interfaces.JSONBuilder;
import de.dietzm.foundation.interfaces.JSONConversionException;
import de.dietzm.foundation.interfaces.ResponseBuilder;
import de.dietzm.foundation.usermgmt.UserManagement;
import de.dietzm.foundation.usermgmt.model.SLUser;

@Path("/registration")
public class RegistrationService {

	@GET
	@Path("/public/checkusername")
	@Produces("application/json")
	public Response checkUsername(@QueryParam("username") String username) {

		if (new UserManagement().isUsernameExisting(username)) {
			return ResponseBuilder.createCustomResponse("UsernameExisting", "Yes");
		} else {
			return ResponseBuilder.createCustomResponse("UsernameExisting", "No");
		}

	}

	@POST
	@Path("/public/register")
	@Produces("application/json")
	@Consumes("application/json")
	public Response registerUser(String urlJsonData) {

		try {
			JSONObject jsonObj = new JSONObject(urlJsonData);
			SLUser userToCreate = (SLUser) JSONBuilder.convertJSON2Object(urlJsonData, SLUser.class);

			// Perform checks
			
			//Everthing entered?
			if(userToCreate.getPrename().equals("")){
				return ResponseBuilder.createErrorResponseWithReference("Prename is missing", "REG01", "prename");
			}
			
			if(userToCreate.getSurname().equals("")){
				return ResponseBuilder.createErrorResponseWithReference("Surname is missing", "REG02", "surname");
			}
			
			if(userToCreate.getEmailAdress().equals("")){
				return ResponseBuilder.createErrorResponseWithReference("Email is missing", "REG03", "emailAdress");
			}
			
//			//Email Adress confirmation correct
//			if(!userToCreate.getEmailAdress().equals(jsonObj.getString("emailAdressConfirm"))){
//				return ResponseBuilder.createErrorResponseWithReference("Email confirmation is not correct", "REG08", "emailAdressConfirm");
//			}
			
			//Password Confirmation
			if(!userToCreate.getPassword().equals(jsonObj.getString("passwordConfirm"))){
				return ResponseBuilder.createErrorResponseWithReference("Passwords are not matching", "REG06", "passwordConfirm");
			}
			
			
			
			 //Password Complexity / not possible due to hashing
//			 String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{5,10}";
//			 if(!userToCreate.getPassword().matches(pattern)){
//				 return ResponseBuilder.createErrorResponseWithReference("Passwords complexity not met", "REG05", "password");
//				}
						
			// Username / Email in use?
			if (new UserManagement().isEmailExisting(userToCreate.getEmailAdress())) {
				return ResponseBuilder.createErrorResponse("Email address already in use", "REG07");
			} else {

				userToCreate.setUsername(userToCreate.getEmailAdress());
				
				Long id = new UserManagement().createUser(userToCreate);
				return ResponseBuilder.createNewObjectCreatedResponse("SLUser", id);

			}
		} catch (JSONConversionException e) {
			return ResponseBuilder.createErrorResponse(e.getLocalizedMessage(), "GEN10");
		} catch (ApplicationDataStoreException e) {
			return ResponseBuilder.createErrorResponse(e.getLocalizedMessage(), "DAT11");
		} catch (JSONException e) {
			return ResponseBuilder.createErrorResponse(e.getLocalizedMessage(), "GEN11");
		}

	}
}
