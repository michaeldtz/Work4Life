package de.dietzm.foundation.db;

@SuppressWarnings("serial")
public class InstanceNotFound extends Exception {

	private Long id;
	private String string;

	public InstanceNotFound(String string, Long id) {
		this.string = string;
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
	
	

}
