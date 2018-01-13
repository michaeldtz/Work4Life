package de.dietzm.foundation.useraccess;

import java.util.ArrayList;
import java.util.List;

import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;
import de.dietzm.foundation.usermgmt.model.SLRole;
import de.dietzm.foundation.usermgmt.model.SLRoleUserAssignment;
import de.dietzm.foundation.usermgmt.model.SLUser;

public class SLGroupAccessService {

	public List<SLRole> getRolesOfUser(Long userID){
		
		DAO<SLUser> userDOA = DAOFactory.getEntityManager("SLUser");
		SLUser user = userDOA.get(userID);
		
		if(user == null)
			return null;
		
		DAO<SLRoleUserAssignment> roleAssDAO = DAOFactory.getEntityManager("SLRoleUserAssignment");
		List<SLRoleUserAssignment> rolesAss = roleAssDAO.query("user", user);
		
		ArrayList<SLRole> userRoles = new ArrayList<SLRole>();
		for(SLRoleUserAssignment roleAss : rolesAss){
			SLRole role = roleAss.getRole().get();
			
			userRoles.add(role);
		}
		
		return userRoles;
	}
	
	public List<SLRole> getAllRoles(){
		
		DAO<SLRole> roleDAO = DAOFactory.getEntityManager("SLRole");
		List<SLRole> roles = roleDAO.queryAll();
		
		return roles;
	}
	
	
}
