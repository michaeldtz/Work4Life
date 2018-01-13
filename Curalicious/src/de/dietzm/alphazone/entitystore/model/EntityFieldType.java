package de.dietzm.alphazone.entitystore.model;

public enum EntityFieldType {

	NULL(-1),
	INT(1),
	STRING(2),
	FLOAT(3),
	DOUBLE(4),
	TEXT(5),
	
	USERID(10),
	TIMESTAMP(11), 
	
	FOREIGN_ENTITY(20);

	private int value;

	private EntityFieldType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static EntityFieldType byName(String name) {
		Object[] allValues = EntityFieldType.class.getEnumConstants();
		for(Object value : allValues){
			if(value.toString().equalsIgnoreCase(name)){
				return (EntityFieldType)value;
			}
		}
		return NULL;
	}
	
}
