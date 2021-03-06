package com.contact.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;


@Service
public class Emailservice {

	public boolean sendEmail(String subject,String message,String to) {
		
		boolean f = false;
		
		String from = "panchald915@gmail.com";
		
		//Variable for gmail
		String host = "smtp.gmail.com";
		
		//get system properties
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES" + properties);
		
		//host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		//step1: get session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new javax.mail.PasswordAuthentication("panchald915@gmail.com", "bunny1996");
			}
	
		
		});
		session.setDebug(true);
		
		//Step2: compose the message
		MimeMessage m = new MimeMessage(session);
		
		try {
			
		//from email
		m.setFrom(from);
		
		//adding recipient to message
		m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		
		//adding subject to message
		m.setSubject(subject);
		
		//adding text to message
		//m.setText(message);
			m.setContent(message,"text/html");
		
		//send
		
				//step3:send the message using transpose class
				Transport.send(m);
				
				System.out.println("Send success......");
				f=true;

		}
		
				
		
		catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
}
