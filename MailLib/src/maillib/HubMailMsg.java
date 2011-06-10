/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maillib;

import java.io.InputStream;
import java.util.Date;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;

import javax.mail.Multipart;

/**
 *
 * @author giovanna
 */
public class HubMailMsg {

    private String content;       //body della mail
    private String contentText;
    private Address[] from;
    private Address[] recipientsTo;
    private Address[] recipientsCC;
    private Date dateSent;
    private String subject;

    private static boolean debug = false;

    public HubMailMsg(Message msg) {
        try {
            content = getTextContent(msg);

            from = msg.getFrom();
            recipientsTo = msg.getRecipients(Message.RecipientType.TO);
            recipientsCC = msg.getRecipients(Message.RecipientType.CC);
            dateSent = msg.getSentDate();
            subject = msg.getSubject();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFrom() {
        String fromR = "";
        fromR = from[0].toString();
        return fromR;
    }

    public Date getSentDate() {
        return dateSent;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getTextContent(Message m) {
        String s = "";
        try {
            Object o = m.getContent();
            String mimeType = m.getContentType();
            if (o instanceof String) {
                if (debug) {
                    System.out.println("**This is a String Message**");
                System.out.println((String) o);
                }
            } else if (o instanceof Multipart) {
                  if (debug) System.out.print("**This is a Multipart Message.  ");
                Multipart mp = (Multipart) o;
                int count3 = mp.getCount();
                 if (debug)  System.out.println("It has " + count3 + " BodyParts in it**");
                for (int j = 0; j < count3; j++) {
                    // Part are numbered starting at 0
                    BodyPart b = mp.getBodyPart(j);
                    String mimeType2 = b.getContentType();
                      if (debug) System.out.println("BodyPart " + (j + 1) + " is of MimeType " + mimeType);

                    Object o2 = b.getContent();
                    if (o2 instanceof String) {
                          if (debug) System.out.println("**This is a String BodyPart**");
                        s = (String) o2;
                          if (debug) System.out.println((String) s);
                    } else if (o2 instanceof Multipart) {
                          if (debug) System.out.print("**This BodyPart is a nested Multipart.  ");
                        Multipart mp2 = (Multipart) o2;
                        int count2 = mp2.getCount();
                         if (debug)  System.out.println("It has " + count2 + "further BodyParts in it**");
                    } else if (o2 instanceof InputStream) {
                          if (debug) System.out.println("**This is an InputStream BodyPart**");
                    }
                } //End of for
            } else if (o instanceof InputStream) {
                  if (debug) System.out.println("**This is an InputStream message**");
                InputStream is = (InputStream) o;
                // Assumes character content (not binary images)
                int c;
                while ((c = is.read()) != -1) {
                    System.out.write(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getCause().toString());
        }

        // Uncomment to set "delete" flag on the message
        //m.setFlag(Flags.Flag.DELETED,true);
        content = s;
        return s;
    }
}
