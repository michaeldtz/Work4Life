package de.dietzm.foundation.initilization;

import java.util.ArrayList;
import java.util.HashMap;

public class InitializationManager {

	


	private static InitializationManager instance;
	
	private InitializationManager(){
		initIntilization();
	}

	public static InitializationManager getInstance() {
		if(instance == null){
			instance = new InitializationManager();
			instance.initIntilization();
		}
		return instance;
	}
	
	private ArrayList<IHandler> initList = new ArrayList<IHandler>();
	
	public void initIntilization(){
		
		initList.add(new SLInitializationHandler()); 		
		
	}
	
	public InitializationResult executeInitialization() throws InitializationError{
		
		InitializationResult result = new InitializationResult();
		
		for (IHandler handler : initList) {
			handler.setLogger(result.getLogger());
			handler.initilize(0);
		}
		
		return result;
		
	}

	
	
	public class InitializationResult {

		private int result = 0;
		
		private InitializationLogger logger = new InitializationLogger();
		
		private HashMap<String, Integer> resultPerUnit = new HashMap<>();
		
		private HashMap<String, String> messagePerUnit = new HashMap<>();

		public int getResult() {
			return result;
		}

		public void setResult(int result) {
			this.result = result;
		}

		public InitializationLogger getLogger() {
			return logger;
		}

		public void setLogger(InitializationLogger logger) {
			this.logger = logger;
		}

		public HashMap<String, Integer> getResultPerUnit() {
			return resultPerUnit;
		}

		public void setResultPerUnit(HashMap<String, Integer> resultPerUnit) {
			this.resultPerUnit = resultPerUnit;
		}

		public HashMap<String, String> getMessagePerUnit() {
			return messagePerUnit;
		}

		public void setMessagePerUnit(HashMap<String, String> messagePerUnit) {
			this.messagePerUnit = messagePerUnit;
		}
		
		
		
	}
	
}
