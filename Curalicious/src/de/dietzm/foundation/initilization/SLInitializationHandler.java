package de.dietzm.foundation.initilization;

import java.io.IOException;

import de.dietzm.foundation.db.ApplicationDataStoreException;
import de.dietzm.foundation.usermgmt.RoleManagement;
import de.dietzm.foundation.usermgmt.UserManagement;
import de.dietzm.foundation.usermgmt.model.SLUser;


public class SLInitializationHandler implements IHandler {

	public void createRole(StringBuffer log, RoleManagement roleMgmt,  String rolename,  String roledesc){
		Long id = roleMgmt.checkkNameAndCreateRole(rolename, roledesc);
		log.append("Creatied Role" + rolename + " with id " + id );		
	}
	
	@Override
	public boolean initilize(int version)  throws InitializationError {
		
		StringBuffer log = new StringBuffer();
		
		//UserManagement
		
		
		//Init Roles
		RoleManagement roleMgmt = new RoleManagement();
		createRole(log, roleMgmt, "ROLE_CORE_BASIC_USER", "App User");
		createRole(log, roleMgmt, "ROLE_CORE_ADMIN", "Admin");
		createRole(log, roleMgmt, "ROLE_CORE_COMPANY_USER_ADMIN", "Company User Admin");
		createRole(log, roleMgmt, "ROLE_CORE_COMPANY_SETTINGS_ADMIN", "Company Settings Admin");
		createRole(log, roleMgmt, "ROLE_CORE_ALPHAZONE_DEVELOPER", "Alphazone Developer");
		
		UserManagement userMgmt = new UserManagement();
		SLUser user = new SLUser();
		user.setEmailAdress("michael.dtz@googlemail.com");
		user.setUsername("admin");
	
		try {
			userMgmt.createUser(user);
		} catch (ApplicationDataStoreException e) {
			//NOthing
		}
		
		return false;
	}

	@Override
	public boolean upgrade(int oldversion, int version)  throws InitializationError {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLogger(InitializationLogger logger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getModuleName() {
		return "SLRoles";
	}



}
