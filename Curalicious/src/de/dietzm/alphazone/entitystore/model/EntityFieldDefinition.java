package de.dietzm.alphazone.entitystore.model;

public class EntityFieldDefinition {

	
	private String name;
	private EntityFieldType type;
	private boolean mandatory;
	private String foreignEntity;

	public EntityFieldDefinition(String name, EntityFieldType type) {
		this.name = name;
		this.type = type;
	}

	public EntityFieldDefinition(String name) {
		this(name, EntityFieldType.STRING);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EntityFieldType getType() {
		return type;
	}

	public void setType(EntityFieldType type) {
		this.type = type;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public void setForeignEntity(String foreignEntity) {
		this.foreignEntity = foreignEntity;
	}

	public String getForeignEntity() {
		return foreignEntity;
	}
	
	

	

}
