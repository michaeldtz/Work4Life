package de.dietzm.foundation.db;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import de.dietzm.foundation.db.base.AbstractChangeableEntity;
import de.dietzm.foundation.interfaces.DetailOnlyJSONConversion;

@XmlRootElement
@Entity
public class Media extends AbstractChangeableEntity {

	@Index
	private Long projectID = 0l;
	
	@Index
	private String filename;
	
	@Index
	private String type;
	
	private String mimeType;
	
	private com.google.appengine.api.datastore.Text content;

	
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Long getProjectID() {
		return projectID;
	}

	public void setProjectID(Long projectID) {
		this.projectID = projectID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@DetailOnlyJSONConversion
	public String getContent() {
		if (content == null)
			return "";
		else
			return content.getValue();
	}

	public void setContent(String content) {
		this.content = new Text(content);
	}

	
	
	
}
