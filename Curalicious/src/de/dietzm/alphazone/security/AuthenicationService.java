package de.dietzm.alphazone.security;

import de.dietzm.alphazone.persistence.AuthorizationModeEnum;
import de.dietzm.alphazone.persistence.EntityAuthorization;
import de.dietzm.alphazone.repository.model.RepositoryProject;
import de.dietzm.foundation.useraccess.UserAccessFactory;
import de.dietzm.foundation.useraccess.UserAccessService;

public class AuthenicationService {

	public static final int RESULT_ADMIN 		= 6;
	public static final int RESULT_OWNER 		= 5;
	public static final int RESULT_IS_PUBLIC 	= 4;
	public static final int RESULT_ANYUSER 		= 3;
	public static final int RESULT_REGISTUSER 	= 2;
	public static final int RESULT_GROUP 		= 1;
	public static final int RESULT_NO_ACCESS 	= -1;
	

	public int isUserAuthorizedToDisplay(EntityAuthorization entity) {

		String userID = null;	
		boolean userHasBasicRole = false;
		boolean isAdmin = false;
		
		UserAccessService userService = UserAccessFactory.getAccessService();
		
		if(userService.isUserLoggedIn()){
			userID = userService.getCurrentUserName();
			
			if(userService.isInGroup("ROLE_BASIC_USER"))
				userHasBasicRole = true;	
			
			if(userService.isAdmin())
				isAdmin = true;
		}
			

		if(isAdmin){
			return RESULT_ADMIN;
		} else if(checkEquals(entity.getUserID(), userID)){
			return RESULT_OWNER;
		} else if(entity.getDisplayAuth() == AuthorizationModeEnum.PUBLIC.getValue()){
			return RESULT_IS_PUBLIC;
		} else if(entity.getDisplayAuth() == AuthorizationModeEnum.ANYUSER.getValue() && userID != null){
			return RESULT_ANYUSER;
		} else if(entity.getDisplayAuth() == AuthorizationModeEnum.REGISTERED.getValue() && userHasBasicRole){
			return RESULT_REGISTUSER;
		} else if(entity.getDisplayAuth() == AuthorizationModeEnum.GROUP.getValue() && userService.isInGroupID(entity.getGroupID())){
			return RESULT_GROUP;
		}
		
		return RESULT_NO_ACCESS;
	}
	
	public int isUserAuthorizedToExecute(EntityAuthorization entity) {

		String userID = null;	
		boolean userHasBasicRole = false;
		boolean isAdmin = false;
		
		UserAccessService userService = UserAccessFactory.getAccessService();
		
		if(userService.isUserLoggedIn()){
			userID = userService.getCurrentUserName();
			
			if(userService.isInGroup("BASIC_USER"))
				userHasBasicRole = true;	
			
			if(userService.isAdmin())
				isAdmin = true;
		}
			

		if(isAdmin){
			return RESULT_ADMIN;
		} else if(checkEquals(entity.getUserID(), userID)){
			return RESULT_OWNER;
		} else if(entity.getExecuteAuth() == AuthorizationModeEnum.PUBLIC.getValue()){
			return RESULT_IS_PUBLIC;
		} else if(entity.getExecuteAuth() == AuthorizationModeEnum.ANYUSER.getValue() && userID != null){
			return RESULT_ANYUSER;
		} else if(entity.getExecuteAuth() == AuthorizationModeEnum.REGISTERED.getValue() && userHasBasicRole){
			return RESULT_REGISTUSER;
		} else if(entity.getExecuteAuth() == AuthorizationModeEnum.GROUP.getValue() && userService.isInGroupID(entity.getGroupID())){
			return RESULT_GROUP;
		}
		
		return RESULT_NO_ACCESS;
	}
	
	public int isUserAuthorizedToChange(EntityAuthorization entity) {
		
		if(isUserAuthorizedToDisplay(entity) == RESULT_NO_ACCESS)
			return RESULT_NO_ACCESS;
		
		String userID = null;	
		boolean userHasBasicRole = false;
		boolean isAdmin = false;
		
		UserAccessService userService = UserAccessFactory.getAccessService();
		
		if(userService.isUserLoggedIn()){
			userID = userService.getCurrentUserName();
			
			if(userService.isInGroup("BASIC_USER"))
				userHasBasicRole = true;	
			
			if(userService.isAdmin())
				isAdmin = true;
		}
			

		if(isAdmin){
			return RESULT_ADMIN;
		} else if(checkEquals(entity.getUserID(), userID)){
			return RESULT_OWNER;
		} else if(entity.getChangeAuth() == AuthorizationModeEnum.PUBLIC.getValue()){
			return RESULT_IS_PUBLIC;
		} else if(entity.getChangeAuth() == AuthorizationModeEnum.ANYUSER.getValue() && userID != null){
			return RESULT_ANYUSER;
		} else if(entity.getChangeAuth() == AuthorizationModeEnum.REGISTERED.getValue() && userHasBasicRole){
			return RESULT_REGISTUSER;
		} else if(entity.getChangeAuth() == AuthorizationModeEnum.GROUP.getValue() && userService.isInGroupID(entity.getGroupID())){
			return RESULT_GROUP;
		}
		
		return RESULT_NO_ACCESS;
	}

	private boolean checkEquals(Object obj1, Object obj2) {
		if(obj1== null || obj2 == null)
			return false;
		return obj1.equals(obj2);		
	}

	public boolean isUserOwner(RepositoryProject repoProject) {
		UserAccessService userService = UserAccessFactory.getAccessService();
		
		if(userService.getCurrentUserName().equals(repoProject.getUserID()))
			return true;
		
		return false;
	}
}
