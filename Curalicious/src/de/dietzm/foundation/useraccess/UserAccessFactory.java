package de.dietzm.foundation.useraccess;

public class UserAccessFactory {

	public static UserAccessService getAccessService(){
		return new SLUserAccessService();
	}
	
}
