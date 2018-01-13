package de.dietzm.foundation.usermgmt.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;

import de.dietzm.foundation.db.base.AbstractBaseEntity;


@XmlRootElement
@Entity
public class Registration  extends AbstractBaseEntity {

	private Long timestamp;
	
	private String username;

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	
}
