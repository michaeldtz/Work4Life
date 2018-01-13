package de.dietzm.foundation.usermgmt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

import de.dietzm.foundation.db.ApplicationDataStoreException;
import de.dietzm.foundation.db.InstanceNotFound;
import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;
import de.dietzm.foundation.usermgmt.model.SLRole;
import de.dietzm.foundation.usermgmt.model.SLRoleUserAssignment;
import de.dietzm.foundation.usermgmt.model.SLUser;

public class UserManagement {

	private static final Object FIXED_SALT = "I&19wshsZSA18jH";

	public SLUser getUserByID(Long id) throws InstanceNotFound {

		DAO<SLUser> userDAO = DAOFactory.getEntityManager("SLUser");
		SLUser user = userDAO.get(id);

		if (user == null) {
			throw new InstanceNotFound("SLUser", id);
		}

		return user;
	}

	public boolean isUserPasswordCombinationCorrect(String username, String password) {

		DAO<SLUser> userDAO = DAOFactory.getEntityManager("SLUser");
		List<SLUser> userList = userDAO.query("username", username);

		if (userList.size() == 1) {

			SLUser user = userList.get(0);
			MessageDigestPasswordEncoder md5Encoder = new MessageDigestPasswordEncoder("MD5");
			if (md5Encoder.isPasswordValid(user.getPasswordHash(), password, FIXED_SALT)) {
				return true;
			} else {
				return false;
			}
			
		} else if (userList.size() > 1) {
			Logger.getAnonymousLogger().warning("More than one result to user/pass combination");
		} else {
			return false;
		}

		return false;

	}

	public boolean isUsernameExisting(String username) {
		DAO<SLUser> userDAO = DAOFactory.getEntityManager("SLUser");
		List<SLUser> userList = userDAO.query("username", username);

		if (userList.size() >= 1) {
			return true;
		}

		return false;

	}

	public Long createUser(SLUser userToCreate) throws ApplicationDataStoreException {

		DAO<SLUser> userDAO = DAOFactory.getEntityManager("SLUser");
		List<SLUser> userList = userDAO.query("username", userToCreate.getUsername());

		if (userList.size() >= 1) {
			throw new ApplicationDataStoreException("Username already in use");
		}

		if (userToCreate.getPasswordHash() == null || !userToCreate.getPasswordHash().equals("")) {
			MessageDigestPasswordEncoder md5Encoder = new MessageDigestPasswordEncoder("MD5");
			userToCreate.setPasswordHash(md5Encoder.encodePassword(userToCreate.getPassword(), FIXED_SALT));
		}

		Long newId = userDAO.create(userToCreate);
		
		//Assign Basic Role
		SLUser user = userDAO.get(newId);
		SLRole roleBasic = new RoleManagement().getRoleByName("ROLE_CORE_BASIC_USER");
		SLRoleUserAssignment assignment = new SLRoleUserAssignment(user, roleBasic);		
		DAO<SLRoleUserAssignment> assignDAO = DAOFactory.getEntityManager("SLRoleUserAssignment");
		assignDAO.create(assignment);
		
		return newId;
	}

	public boolean isEmailExisting(String emailAdress) {
		DAO<SLUser> userDAO = DAOFactory.getEntityManager("SLUser");
		List<SLUser> userList = userDAO.query("emailAdress", emailAdress);

		if (userList.size() >= 1) {
			return true;
		}

		return false;
	}

	public SLUser getUserByName(String username) {
		DAO<SLUser> userDAO = DAOFactory.getEntityManager("SLUser");
		List<SLUser> userList = userDAO.query("username", username);

		if (userList.size() >= 1) {
			return userList.get(0);
		}

		return null;

	}
	
	public List<SLRole> getRolesOfUser(String username){
		SLUser user = getUserByName(username);
		if(user == null)
			return null;
		
		DAO<SLRoleUserAssignment> roleasDAO = DAOFactory.getEntityManager("SLRoleUserAssignment");
		List<SLRoleUserAssignment> roleasList = roleasDAO.query("user", user);
		
		ArrayList<SLRole> roleList = new ArrayList<SLRole>();
		for (Iterator<SLRoleUserAssignment> iterator = roleasList.iterator(); iterator.hasNext();) {
			SLRoleUserAssignment roleasEntry = (SLRoleUserAssignment) iterator.next();
			
			if(roleasEntry.getStatus() == SLRoleUserAssignment.STATUS_ACTIVE){
				SLRole role = roleasEntry.getRole().get();
				if(role != null)
					roleList.add(role);
			}
		}
		
		return roleList;		
	}

}
