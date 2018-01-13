package de.dietzm.foundation.db.base;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;

@XmlRootElement
@Entity
public class AbstractChangeableEntity extends AbstractBaseEntity {
	
	private String creationUser;
	
	private String lastchangeUser;
	
	private Long creationDate;
	
	private Long lastchangeDate;

	public String getCreationUser() {
		return creationUser;
	}

	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}

	public String getLastchangeUser() {
		return lastchangeUser;
	}

	public void setLastchangeUser(String lastchangeUser) {
		this.lastchangeUser = lastchangeUser;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}

	public Long getLastchangeDate() {
		return lastchangeDate;
	}

	public void setLastchangeDate(Long lastchangeDate) {
		this.lastchangeDate = lastchangeDate;
	}

	
	
}
