package de.dietzm.alphazone.persistence;

public interface EntityAuthorization {

	public String getUserID();

	public void setUserID(String userID);

	public String getGroupID();

	public void setGroupID(String groupID);

	public int getDisplayAuth();

	public void setDisplayAuth(int displayAuth);

	public int getChangeAuth();

	public void setChangeAuth(int changeAuth);
	
	public int getExecuteAuth();

	public void setExecuteAuth(int executeAuth);

}
