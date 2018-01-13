package de.dietzm.foundation.usermgmt.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import de.dietzm.foundation.db.base.AbstractBaseEntity;

@XmlRootElement
@Entity
public class SLRoleUserAssignment extends AbstractBaseEntity{


	@Index
	private Ref<SLRole> role;
	
	@Index
	private Ref<SLUser> user;

	private int status = 1;

	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_INACTIVE = 0;
	
	public SLRoleUserAssignment(){
		
	}
	
	public SLRoleUserAssignment(SLUser user, SLRole role) {
		this.user = Ref.create(user);
		this.role = Ref.create(role);
		this.status = 1;
	}

	public Ref<SLRole> getRole() {
		return role;
	}

	public void setRole(Ref<SLRole> role) {
		this.role = role;
	}

	public Ref<SLUser> getUser() {
		return user;
	}

	public void setUser(Ref<SLUser> user) {
		this.user = user;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	
}
