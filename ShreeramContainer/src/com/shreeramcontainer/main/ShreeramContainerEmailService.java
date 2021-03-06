package com.shreeramcontainer.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Component;

@Component
public class ShreeramContainerEmailService implements EmailService{
	private static final Logger LOGGER = Logger.getLogger(ShreeramContainerEmailService.class.getName());

	public void sendContactEmail(String contactNumber, String message){
		EmailConfigurations configurations = loadEmailConfigurations();
		StringBuilder buffer = new StringBuilder();
		buffer.append("We received enquiry from: -" + contactNumber);
		buffer.append("\n\n\nMessage: -");
		buffer.append(message);
		try {
			Properties mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
			mailServerProperties.put("mail.smtp.port", "587");
			mailServerProperties.put("mail.smtp.auth", "true");
			mailServerProperties.put("mail.smtp.starttls.enable", "true");

			Session session = Session.getInstance(mailServerProperties,new EmailAuthenticator(configurations.getSender(),configurations.getSenderCred()));
			MimeMessage mailMessage = new MimeMessage(session);
			mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(configurations.getTo()));

			if(configurations.getCc()!=null){
				String ccAddress = configurations.getCc();
				String[] ccAddresses = ccAddress.split(",");
				for(String email:ccAddresses){
					if(email!=null){
						mailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
					}
				}
			}
			mailMessage.setFrom(new InternetAddress(configurations.getSender()));
			mailMessage.setSubject("Enquiry on Website");
			mailMessage.setContent(buffer.toString(), "text/html");

			Transport transport = session.getTransport("smtp");
			transport.connect("smtp.gmail.com", configurations.getSender(), configurations.getSenderCred());
			transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
			transport.close();
			
		} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE, "Error while sending email", e);
		}
	}

	private EmailConfigurations loadEmailConfigurations(){
		EmailConfigurations credentials = new EmailConfigurations();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			String filename = "emailconfig.properties";
			input = ShreeramContainerResource.class.getClassLoader().getResourceAsStream(filename);

			if(input==null){
				LOGGER.warning("Config.properties not found.");
				return null;
			}
			prop.load(input);
			credentials.setSender(prop.getProperty("sender"));
			credentials.setSenderCred(prop.getProperty("password"));
			credentials.setTo(prop.getProperty("to"));
			credentials.setCc(prop.getProperty("cc"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return credentials;
	}

	private class EmailAuthenticator extends Authenticator{
		String username;
		String password;

		public EmailAuthenticator(String username, String password){
			this.username = username;
			this.password = password;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username,password);
		}
	}

	private class EmailConfigurations{
		String senderCred;
		String sender;
		String to;
		String cc;

		public String getSenderCred() {
			return senderCred;
		}
		public void setSenderCred(String senderCred) {
			this.senderCred = senderCred;
		}
		public String getSender() {
			return sender;
		}
		public void setSender(String sender) {
			this.sender = sender;
		}
		public String getTo() {
			return to;
		}
		public void setTo(String to) {
			this.to = to;
		}
		public String getCc() {
			return cc;
		}
		public void setCc(String cc) {
			this.cc = cc;
		}
	}
}
