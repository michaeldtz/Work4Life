package de.dietzm.alphazone.jsservices;

import org.mozilla.javascript.ScriptableObject;

import de.dietzm.alphazone.mail.GAEMailingService;
import de.dietzm.alphazone.mail.IMailingService;

@SuppressWarnings("serial")
public class EmailService extends ScriptableObject {

	public EmailService() {
		String[] functionNames = { "sendEmail" };
		defineFunctionProperties(functionNames, EmailService.class, ScriptableObject.DONTENUM);
	}
	
	
	public void sendEmail(String receiver, String subject, String content){
		
		IMailingService gaeMail = new GAEMailingService("michael.dtz@gmail.com");
		gaeMail.sendSingleMail(receiver,receiver, "SpaceLabs Email Service" , subject, content);
		
		
	}

	@Override
	public String getClassName() {
		return "EmailService";
	}


	
	
	
}
