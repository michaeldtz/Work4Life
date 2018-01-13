package de.dietzm.alphazone.entitystore.model;

public class ReverseLinkDefinition {

	private String field;
	private String foreignEntity;
	private String name;

	public ReverseLinkDefinition(String fentity, String field, String name) {
		this.foreignEntity = fentity;
		this.field = field;
		this.name = name;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getForeignEntity() {
		return foreignEntity;
	}

	public void setForeignEntity(String foreignEntity) {
		this.foreignEntity = foreignEntity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
