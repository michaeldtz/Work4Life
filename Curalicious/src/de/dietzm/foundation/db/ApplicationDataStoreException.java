package de.dietzm.foundation.db;

@SuppressWarnings("serial")
public class ApplicationDataStoreException extends Exception {

	private String text;

	public ApplicationDataStoreException(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	
}
