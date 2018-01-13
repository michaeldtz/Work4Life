package de.dietzm.alphazone.persistence;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum AuthorizationModeEnum {

	PRIVATE(0), 
	GROUP(1),
	REGISTERED(2),
	ANYUSER(3),
	PUBLIC(4);

	private int value;

	private AuthorizationModeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	
}
