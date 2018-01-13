package de.dietzm.alphazone.mail;

public interface IMailingService {
	

	void sendSingleMail(String receiver, String receiverTitle, String senderTitle, String title,
			String content);


	void sendMailToAdmin(String sendTitle, String title, String text);
	
}
