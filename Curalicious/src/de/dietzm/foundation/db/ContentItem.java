package de.dietzm.foundation.db;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import de.dietzm.foundation.db.base.AbstractChangeableEntity;

@XmlRootElement
@Entity
public class ContentItem extends AbstractChangeableEntity {

	@Index
	private Long projectID = 0l;

	private String category;

	private String type;

	private String tags;

	private String title;

	private String icon;
	
	private String iconColor;

	private String stylingInfo;

	private String otherInfo;

	@Index
	private int sortorder;

	private com.google.appengine.api.datastore.Text content;
	
	private com.google.appengine.api.datastore.Text quiz;

	public Long getProjectID() {
		return projectID;
	}

	public void setProjectID(Long projectID) {
		this.projectID = projectID;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getStylingInfo() {
		return stylingInfo;
	}

	public void setStylingInfo(String stylingInfo) {
		this.stylingInfo = stylingInfo;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public String getContent() {
		if (content == null)
			return "";
		else
			return content.getValue();
	}

	public void setContent(String content) {
		this.content = new Text(content);
	}
	
	
	public String getQuiz() {
		if (quiz == null)
			return "";
		else
			return quiz.getValue();
	}

	public void setQuiz(String quiz) {
		this.quiz = new Text(quiz);
	}

	public int getSortorder() {
		return sortorder;
	}

	public void setSortorder(int sortorder) {
		this.sortorder = sortorder;
	}

	public String getIconColor() {
		return iconColor;
	}

	public void setIconColor(String iconColor) {
		this.iconColor = iconColor;
	}

	
}
