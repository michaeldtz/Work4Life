package de.dietzm.foundation.db;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import de.dietzm.foundation.db.base.AbstractChangeableEntity;
import de.dietzm.foundation.interfaces.NoJSONConversion;

@XmlRootElement
@Entity
public class Project extends AbstractChangeableEntity{
	
	private String title;
	
	private String description;
	
	@Index
	private String accessKey;
	
	@Index
	private String appKey;
	
	private String secret;
	
		
	private String password;
	
	private String language;
	
	private com.google.appengine.api.datastore.Text eula;
	
	

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	
	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@NoJSONConversion
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
	public String getEula() {
		if (eula == null)
			return "";
		else
			return eula.getValue();
	}

	public void setEula(String eula) {
		this.eula = new Text(eula);
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	
	

}
