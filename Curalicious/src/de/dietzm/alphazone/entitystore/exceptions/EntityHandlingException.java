package de.dietzm.alphazone.entitystore.exceptions;

@SuppressWarnings("serial")
public class EntityHandlingException extends Exception {

	private String text;

	public EntityHandlingException(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return this.text;
	}
	
	@Override
	public String getMessage() {
		return this.text;
	}

}
