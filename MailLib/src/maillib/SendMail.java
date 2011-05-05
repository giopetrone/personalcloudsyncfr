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
import java.io.InputStream;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;

public class SendMail {

    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final String SMTP_PORT = "465";
    //  private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final String SMTP_AUTH_USER = "sgnmrn@gmail.com";
    private static final String SMTP_AUTH_PWD = "micio11";
    private static final String emailMsgTxt = "Online Order Confirmation Message. Also include the Tracking Number.";
    private static final String emailSubjectTxt = "Order Confirmation Subject";
//  private static final String emailFromAddress = "giovanna@di.unito.it";
    private static final String emailFromAddress = "sgnmrn@gmail.com";
    // Add List of Email address to who email needs to be sent to
    private static final String[] emailList = {"gio.petrone@gmail.com"};

    public static void main(String args[]) throws Exception {
        SendMail smtpMailSender = new SendMail();
         smtpMailSender.postMail(emailList, emailSubjectTxt, emailMsgTxt, emailFromAddress);
         //   smtpMailSender.getMailMessages();
        System.out.println("Sucessfully Sent mail to All Users");
    }

    public void getMailMessages() {
        try {
            System.out.println("uno");
            InitialContext ic = new InitialContext();
            System.out.println("due");
            Properties properties = System.getProperties();
            System.out.println("tre");
            Authenticator auth = new ImapAuthenticator();
            Session session = Session.getDefaultInstance(properties, auth);
            System.out.println("quattro");
            Store store = session.getStore("imaps");
            System.out.println("5");
            //  store.connect("imap.gmail.com", "gio.petrone@gmail.com", "mer20ia05");
            store.connect("imap.gmail.com", "annamaria.goy@gmail.com", "tex_willer");

            System.out.println("6");
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            System.out.println("7");
            Message[] messages = folder.getMessages();
            // Display message.
            for (int i = 0; i < messages.length; i++) {

                System.out.println("------------ Message " + (i + 1) + " ------------");

                System.out.println("SentDate : " + messages[i].getSentDate());
                System.out.println("From : " + messages[i].getFrom()[0]);
                System.out.println("Subject : " + messages[i].getSubject());
                System.out.print("Message : ");
                System.out.print(messages[i].getContent().toString());
//                InputStream stream = messages[i].getInputStream();
//                while (stream.available() != 0) {
//                    System.out.print((char) stream.read());
//                }
                System.out.println();
            }

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            /* The size of the HTTP request body exceeds the limit */
        }
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
      //  addAtachments(st, multipart);
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
//    
            return new PasswordAuthentication(username, password);
        }
    }

    private class ImapAuthenticator extends Authenticator {

        public PasswordAuthentication getPasswordAuthentication() {
            String username, password;

            username = "giovanna@di.unito.it";
            password = "mer06ia";
            return new PasswordAuthentication(username, password);
        }
    }
    
    
    
}
