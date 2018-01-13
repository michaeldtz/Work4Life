package de.dietzm.alphazone.persistence;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Index;

import de.dietzm.foundation.db.base.AbstractBaseEntity;

@XmlRootElement
public class AbstractEntity extends AbstractBaseEntity {
	
	@Index
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
