/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maillib;

/**
 *
 * @author giovanna
 */
import java.security.Security;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailCl {

    private String SMTP_HOST_NAME = "smtp.gmail.com";
    private String SMTP_PORT = "465";
    private String emailMsgTxt = "Test Message Contents";
    private String emailSubjectTxt = "A test from gmail";
    private String emailFromAddress = "sgnmrn@gmail.com";
    private String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private String[] recipients = {"gio.petrone@gmail.com", "fabrizio.torretta@gmail.com"};

    public SendMailCl(String[] sendTo, String emailSubjectTxt, String emailMsgTxt, String emailFromAddress) {

        this.emailMsgTxt = emailMsgTxt;
        this.emailSubjectTxt = emailSubjectTxt;
        this.emailFromAddress = emailFromAddress;
        this.recipients = sendTo;
    }

    public SendMailCl() {
    }

    public static void main(String args[]) throws Exception {

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        String[] sT = {"gio.petrone@gmail.com", "fabrizio.torretta@gmail.com"};
        SendMailCl sm = new SendMailCl(sT, "A test from gmail22", "Test Message Contents", "sgnmrn@gmail.com");
        sm.sendSSLMessage();
        System.out.println("Sucessfully Sent mail to All Users");
    }

    //    public void sendSSLMessage(String recipients[], String subject, String message, String from)
    public void sendSSLMessage()
            throws MessagingException {
        boolean debug = true;

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("sgnmrn@gmail.com", "micio11");

                    }
                });

        session.setDebug(debug);

        Message msg = new MimeMessage(session);
        InternetAddress addressFrom = new InternetAddress(emailFromAddress);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

// Setting the Subject and Content Type
        msg.setSubject(emailMsgTxt);
        msg.setContent(emailSubjectTxt, "text/plain");
        Transport.send(msg);
    }
} 

 
