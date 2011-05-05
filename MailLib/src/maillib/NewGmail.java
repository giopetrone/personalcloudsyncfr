/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maillib;


import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

/**
 *
 * @author giovanna
 */
public class NewGmail {

    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final int SMTP_HOST_PORT = 465;
    private static final String SMTP_AUTH_USER = "sgnmrn@gmail.com";
    private static final String SMTP_AUTH_PWD = "micio11";

    public static void main(String[] args) throws Exception {
        NewGmail gMail = new NewGmail();
      //  gMail.testSendMail();
        gMail.getMailMessages();
    }

    public void testSendMail() throws Exception {
        Properties props = new Properties();

        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", SMTP_HOST_NAME);
        props.put("mail.smtps.auth", "true");
        // props.put("mail.smtps.quitwait", "false");

        Session mailSession = Session.getDefaultInstance(props);
        mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();

        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject("Testing SMTP-SSL");
        message.setContent("This is a test", "text/plain");

        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress("gio.petrone@gmail.com"));

        transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);

        transport.sendMessage(message,
                message.getRecipients(Message.RecipientType.TO));
        transport.close();
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

            username = "sgnmrn@gmail.com";
            password = "micio11";
            return new PasswordAuthentication(username, password);
        }
    }
}
