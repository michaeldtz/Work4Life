package de.dietzm.foundation.initilization;

public interface IHandler {
	
	public String getModuleName();
	
	public void setLogger(InitializationLogger logger);
	
	public boolean initilize(int version) throws InitializationError;
	
	public boolean upgrade(int oldversion, int version) throws InitializationError;
	
}
