package stu.cn.ua.kurs4.model.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Properties;

@Service
public class SendEmailService {

    @Autowired
    private Environment env;

    public void sendMail(String to, String body, String topic) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.transport.protocol", env.getProperty("mail.transport.protocol"));
        props.put("mail.smtp.host", env.getProperty("mail.smtp.host"));
        props.put("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));
        props.put("mail.smtp.port", env.getProperty("mail.smtp.port"));

        String email = env.getProperty("email_for_smtp");
        String password = env.getProperty("password");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setSubject(topic);
        message.setText(body);

        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSentDate(new Date());

        Transport.send(message);

        System.out.println("Email sent successfully.");
    }
}
