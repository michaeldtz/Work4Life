package de.dietzm.alphazone.entitystore.exceptions;

@SuppressWarnings("serial")
public class EntityMandatoryFieldNotProvided extends Exception {

	private String fieldName;

	public EntityMandatoryFieldNotProvided(String fieldName) {
		this.setFieldName(fieldName);
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
