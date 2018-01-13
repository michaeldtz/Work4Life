package de.dietzm.foundation.useraccess;

import java.util.HashMap;
import java.util.List;

import de.dietzm.foundation.usermgmt.model.SLRole;
import de.dietzm.foundation.usermgmt.model.SLUser;

public interface UserAccessService {

	public abstract Long getCurrentUserID();

	public abstract String getCurrentUserEmail();

	public abstract String getCurrentUserName();

	public abstract String getCurrentUserAuthProvider();

	public abstract boolean isUserLoggedIn();

	public abstract boolean isAdmin();
	
	public abstract String getLoginURL();
	
	public abstract String getLogoutURL();
	
	public abstract boolean isApplicationUser();
	
	public abstract boolean isInGroup(String string);
	
	public abstract HashMap<String,String> getAllLoginURLsForProvider();

	public abstract List<SLRole> getGroupsOfUser();
	
	public abstract boolean isInGroupID(String groupID);

	public abstract SLUser getCurrentUser();

}