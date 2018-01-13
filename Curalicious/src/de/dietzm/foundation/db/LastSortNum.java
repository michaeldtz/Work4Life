package de.dietzm.foundation.db;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import de.dietzm.foundation.db.base.AbstractBaseEntity;

@Entity
public class LastSortNum extends AbstractBaseEntity{

	
	@Index
	private Long projectId;
	
	@Index
	private int lastSortNum = 10;


	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public int getLastSortNum() {
		return lastSortNum;
	}

	public void setLastSortNum(int lastSortNum) {
		this.lastSortNum = lastSortNum;
	}

	public void nextSortNum() {
		this.lastSortNum += 10;
	}
	
	
	
}
