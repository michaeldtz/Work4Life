package de.dietzm.foundation.usermgmt.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;

import de.dietzm.foundation.db.base.AbstractBaseEntity;

@XmlRootElement
@Entity
public class Invitation extends AbstractBaseEntity {

	private String userID;

	private Date sentDate;

	private String reason;

	private String email;

	private String fullname;

	public Invitation() {

	}

	public Invitation(String userID, String email, String fullname, String reason) {
		this.fullname = fullname;
		this.userID = userID;
		this.email = email;
		this.reason = reason;
		this.sentDate = new Date();
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Object getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	

}
