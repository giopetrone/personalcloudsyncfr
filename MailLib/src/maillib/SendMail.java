/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maillib;

/**
 *
 * @author giovanna
 */

/*
To use this program, change values for the following three constants,

SMTP_HOST_NAME -- Has your SMTP Host Name
SMTP_AUTH_USER -- Has your SMTP Authentication UserName
SMTP_AUTH_PWD  -- Has your SMTP Authentication Password

Next change values for fields

emailMsgTxt  -- Message Text for the Email
emailSubjectTxt  -- Subject for email
emailFromAddress -- Email Address whose name will appears as "from" address

Next change value for "emailList".
This String array has List of all Email Addresses to Email Email needs to be sent to.


Next to run the program, execute it as follows,

SendMailUsingAuthentication authProg = new SendMailUsingAuthentication();

 */

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

public class SendMail {

   //  private static final String SMTP_HOST_NAME = "pianeta.di.unito.it";
//  private static final String SMTP_AUTH_USER = "giovanna";
//  private static final String SMTP_AUTH_PWD  = "mer06ia";
    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final String SMTP_AUTH_USER = "gio.petrone@gmail.com";
    private static final String SMTP_AUTH_PWD = "mer20ia05";
    private static final String emailMsgTxt = "Online Order Confirmation Message. Also include the Tracking Number.";
    private static final String emailSubjectTxt = "Order Confirmation Subject";
//  private static final String emailFromAddress = "giovanna@di.unito.it";
    private static final String emailFromAddress = "gio.petrone@gmail.com";
    // Add List of Email address to who email needs to be sent to
    private static final String[] emailList = {"giovanna@di.unito.it"};

    public static void main(String args[]) throws Exception {
        SendMail smtpMailSender = new SendMail();
        smtpMailSender.postMail(emailList, emailSubjectTxt, emailMsgTxt, emailFromAddress);
        System.out.println("Sucessfully Sent mail to All Users");
    }

    public void postMail(String recipients[], String subject,
            String message, String from) throws MessagingException {
        boolean debug = true;

        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");


        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getDefaultInstance(props, auth);

        session.setDebug(debug);

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);


        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);


        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");

        // Create a message part to represent the body text
        BodyPart messageBodyPart = new MimeBodyPart();
        String messageContent = "";
        messageBodyPart.setContent(messageContent, "text/html");

        // use a MimeMultipart as we need to handle the file attachments
        Multipart multipart = new MimeMultipart();

        // add the message body to the mime message
        multipart.addBodyPart(messageBodyPart);

        // add any file attachments to the message


        // Put all message parts in the message
        msg.setContent(multipart);
        String[] st = {"", ""};
        addAtachments(st, multipart);
        Transport.send(msg);
    }

    /**
     * @param attachments
     * @param multipart
     * @throws MessagingException
     * @throws AddressException
     */
    protected void addAtachments(String[] attachments, Multipart multipart)
            throws MessagingException, AddressException {
        for (int i = 0; i <= attachments.length - 1; i++) {
            // String filename = attachments[i]; TEMP
            String filename = "/home/giovanna/lugio.txt";
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();

            // use a JAF FileDataSource as it does MIME type detection
            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));

            // assume that the filename you want to send is the same
//as the
            // actual file name - could alter this to remove the file
//path
            attachmentBodyPart.setFileName(filename);

            // add the attachment
            multipart.addBodyPart(attachmentBodyPart);
        }
    }

    /**
     * SimpleAuthenticator is used to do simple authentication
     * when the SMTP server requires it.
     */
    private class SMTPAuthenticator extends javax.mail.Authenticator {

        public PasswordAuthentication getPasswordAuthentication() {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
//    String username = "giovanna@di.unito.it";
//    String password = "mer06ia";
            //        String username = "gio.petrone@gmail.com";
            //        String password = "mer20ia05";
            return new PasswordAuthentication(username, password);
        }
    }

}
