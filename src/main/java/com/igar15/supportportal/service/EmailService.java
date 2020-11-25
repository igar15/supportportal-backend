package com.igar15.supportportal.service;

import com.igar15.supportportal.constant.EmailConstant;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailService {

    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        Message message = createMessage(firstName, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(EmailConstant.SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(EmailConstant.GMAIL_SMTP_SERVER, EmailConstant.USERNAME, EmailConstant.PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message createMessage(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(EmailConstant.FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailConstant.CC_EMAIL, false));
        message.setSubject(EmailConstant.EMAIL_SUBJECT);
        message.setText("Hello, " + firstName + ", \n\n Your new account password is " + password + "\n\n The Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }



    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(EmailConstant.SMTP_HOST, EmailConstant.GMAIL_SMTP_SERVER);
        properties.put(EmailConstant.SMTP_AUTH, true);
        properties.put(EmailConstant.SMTP_PORT, EmailConstant.DEFAULT_PORT);
        properties.put(EmailConstant.SMTP_STARTTLS_ENABLE, true);
        properties.put(EmailConstant.SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }

}
