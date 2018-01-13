package de.dietzm.foundation.usermgmt;

import java.util.List;

import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;
import de.dietzm.foundation.usermgmt.model.SLRole;

public class RoleManagement {


	
	public SLRole getRoleByName(String rolename){
		
		DAO<SLRole> roleDAO = DAOFactory.getEntityManager("SLRole");
		List<SLRole> roles = roleDAO.query("rolename", rolename);
		if(roles.size() == 1){
			return roles.get(0);
		}
		
		return null;
	}

	public SLRole getRoleById(Long roleId) {
		
		DAO<SLRole> roleDAO = DAOFactory.getEntityManager("SLRole");
		SLRole role = roleDAO.get(roleId);
		return role;
	}
	
	public Long checkkNameAndCreateRole(String rolename, String descripiton){
		DAO<SLRole> roleDAO = DAOFactory.getEntityManager("SLRole");
		
		if(roleDAO.query("rolename", rolename).isEmpty()){
			return roleDAO.create(new SLRole(rolename, descripiton, true));
			
		}
		return -1l;
		
	}
	
}
