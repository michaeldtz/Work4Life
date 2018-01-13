package de.dietzm.foundation.configuration.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import de.dietzm.foundation.db.base.AbstractBaseEntity;

@Entity
public class SLConfiguration extends AbstractBaseEntity {

	@Index
	private String key;

	@Index
	private String owner;

	private String valueString;

	private int valueInt;

	private float valueFloat;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getValueString() {
		return valueString;
	}

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

	public int getValueInt() {
		return valueInt;
	}

	public void setValueInt(int valueInt) {
		this.valueInt = valueInt;
	}

	public float getValueFloat() {
		return valueFloat;
	}

	public void setValueFloat(float valueFloat) {
		this.valueFloat = valueFloat;
	}

}
