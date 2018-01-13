package de.dietzm.alphazone.entitystore.model;

import java.util.ArrayList;

public class EntityAPIDefinition {

	private String apiMethod;
	private boolean publc;
	private ArrayList<String> roleNames = new ArrayList<String>();
	
	
	public EntityAPIDefinition(String method) {
		this.apiMethod = method;
	}
	public String getApiMethod() {
		return apiMethod;
	}
	public void setApiMethod(String apiMethod) {
		this.apiMethod = apiMethod;
	}
	public boolean isPublc() {
		return publc;
	}
	public void setPublc(boolean publc) {
		this.publc = publc;
	}
	public ArrayList<String> getRoleNames() {
		return roleNames;
	}
	public void setRoleNames(ArrayList<String> roleNames) {
		this.roleNames = roleNames;
	}
	public void addRole(String role) {
		if(role == null)
			publc = true;
		else
			roleNames.add(role);
		
	}
	public boolean isAuthorized() {
		return true; //TODO: Implement real logic
	}
	
	
}
