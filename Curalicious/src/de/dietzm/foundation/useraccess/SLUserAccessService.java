package de.dietzm.foundation.useraccess;

import java.util.HashMap;
import java.util.List;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;
import de.dietzm.foundation.usermgmt.model.SLRole;
import de.dietzm.foundation.usermgmt.model.SLUser;

public class SLUserAccessService implements UserAccessService {

	private SecurityContext ctx;

	SLUserAccessService() {
		this.ctx = SecurityContextHolder.getContext();
	}

	public SLUser getCurrentUser() {

		if (ctx.getAuthentication() == null)
			return null;

		String user = (String) ctx.getAuthentication().getPrincipal();
		if (user == null)
			return null;

		if(user.equals("anonymousUser"))
			return null;
		
		try {

			@SuppressWarnings("unchecked")
			DAO<SLUser> userDAO = DAO.getByName("SLUser");
			List<SLUser> userList = userDAO.query("username", user);
			if (userList.size() == 1) {
				return userList.get(0);
			} else {
				return null;
			}
			
		} catch (Exception e) {
			System.out.println("Error quering user: " + e.getMessage());
			return null;
		}
	}

	@Override
	public Long getCurrentUserID() {
		SLUser user = getCurrentUser();
		if (user != null)
			return user.getId();
		return null;
	}

	@Override
	public String getCurrentUserEmail() {
		SLUser user = getCurrentUser();
		if (user != null)
			return user.getEmailAdress();
		return null;
	}

	@Override
	public String getCurrentUserName() {
		SLUser user = getCurrentUser();
		if (user != null)
			return user.getUsername();
		return null;
	}

	@Override
	public String getCurrentUserAuthProvider() {
		return "WORK&LIFE";
	}

	@Override
	public boolean isUserLoggedIn() {
		SLUser user = getCurrentUser();
		if (user != null)
			return true;
		return false;
	}

	@Override
	public boolean isAdmin() {
		SLUser user = getCurrentUser();
		if (user != null) {
			isInGroup("ROLE_ADMIN");
		}
		return false;
	}

	@Override
	public String getLoginURL() {
		return "login/login.html";
	}

	@Override
	public HashMap<String, String> getAllLoginURLsForProvider() {

		HashMap<String, String> loginProviders = new HashMap<String, String>();
		loginProviders.put("Work&Life", "/login/login.html");
		return loginProviders;
	}

	@Override
	public String getLogoutURL() {
		return "/login/logut.html";
	}

	public boolean isApplicationUser() {
		return isInGroup("ROLE_BASIC_USER");
	}

	@Override
	public boolean isInGroup(String roleName) {
		SLGroupAccessService roleService = new SLGroupAccessService();
		Long userID = getCurrentUserID();
		List<SLRole> roles = roleService.getRolesOfUser(userID);

		for (SLRole role : roles) {

			if (role.getRolename().equals(roleName)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<SLRole> getGroupsOfUser() {
		SLGroupAccessService roleService = new SLGroupAccessService();
		return roleService.getRolesOfUser(getCurrentUserID());
	}

	@Override
	public boolean isInGroupID(String groupID) {
		SLGroupAccessService roleService = new SLGroupAccessService();
		Long userID = getCurrentUserID();
		List<SLRole> roles = roleService.getRolesOfUser(userID);

		for (SLRole role : roles) {
			if (role.getId().equals(groupID)) {
				return true;
			}
		}

		return false;
	}
}
