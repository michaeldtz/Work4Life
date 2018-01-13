package de.dietzm.foundation.usermgmt.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

import de.dietzm.foundation.db.base.AbstractBaseEntity;
import de.dietzm.foundation.interfaces.NoJSONConversion;

@Entity
public class SLUser extends AbstractBaseEntity {

	@Index
	private String username;
	
	@Index
	private String emailAdress;
	
	@Index
	private String prename;
	
	@Index
	private String surname;
	
	@Ignore
	private String password;
	
	
	private String passwordHash; 



	public String getUsername() { 
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmailAdress() {
		return emailAdress;
	}

	public void setEmailAdress(String emailAdress) {
		this.emailAdress = emailAdress;
	}

	public String getPrename() {
		return prename;
	}

	public void setPrename(String prename) {
		this.prename = prename;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	@NoJSONConversion
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	@NoJSONConversion
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	
	
}
