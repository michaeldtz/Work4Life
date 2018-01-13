package de.dietzm.foundation.usermgmt.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import de.dietzm.foundation.db.base.AbstractBaseEntity;

@XmlRootElement
@Entity
public class SLRole extends AbstractBaseEntity {
	
	@Index
	private String rolename;
	
	private String description;
	
	@Index
	private boolean isCoreGroup;
	
	public SLRole(String rolename, String description, boolean isCoreGroup){
		this.rolename = rolename;
		this.description = description;
		this.isCoreGroup = isCoreGroup;
	}

	public SLRole() {
		
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCoreGroup() {
		return isCoreGroup;
	}

	public void setCoreGroup(boolean isCoreGroup) {
		this.isCoreGroup = isCoreGroup;
	}
	
}
