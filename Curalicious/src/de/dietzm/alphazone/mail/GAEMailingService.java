package de.dietzm.alphazone.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class GAEMailingService implements IMailingService {

	private static final Logger LOG = Logger.getLogger(GAEMailingService.class.getName());
	private String senderMail;

	public GAEMailingService(String senderMail){
		this.senderMail = senderMail;
	}
	
	@Override
	public void sendSingleMail(String receiver, String receiverTitle, String senderTitle, String title, String content) {
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
		
        
        try {
        	
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderMail, senderTitle));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(receiver, receiverTitle));
            msg.setSubject(title);
            msg.setText(content);
            Transport.send(msg);
    
        } catch (AddressException e) {
        	LOG.warning(e.toString());
        } catch (MessagingException e) {
        	LOG.warning(e.toString());
        } catch (UnsupportedEncodingException e) {
        	LOG.warning(e.toString());
		}
        
	}

	@Override
	public void sendMailToAdmin(String sendTitle, String title, String content) {
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
		
        try {
        	
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderMail, sendTitle));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(senderMail));
            msg.setSubject(title);
            msg.setText(content);
            Transport.send(msg);
    
        } catch (AddressException e) {
        	LOG.warning(e.toString());
        } catch (MessagingException e) {
        	LOG.warning(e.toString());
        } catch (UnsupportedEncodingException e) {
        	LOG.warning(e.toString());
		}
        
	}

}
