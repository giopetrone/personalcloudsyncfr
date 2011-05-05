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

    private String content;
    private String contentText;
    private Address[] from;
    private Address[] recipientsTo;
    private Address[] recipientsCC;
    private Date dateSent;
    private String subject;

    public HubMailMsg(Message msg) {
        try {
            content = msg.getContent().toString();

            from = msg.getFrom();
            recipientsTo = msg.getRecipients(Message.RecipientType.TO);
            recipientsCC = msg.getRecipients(Message.RecipientType.CC);
            dateSent = msg.getSentDate();
            subject = msg.getSubject();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTextContent(Message m) {
        String s="";
        try {
        Object o = m.getContent();
        String mimeType = m.getContentType();
                if (o instanceof String) {
                    System.out.println("**This is a String Message**");
                    System.out.println((String)o);
                }
                else if (o instanceof Multipart) {
                    System.out.print("**This is a Multipart Message.  ");
                    Multipart mp = (Multipart)o;
                    int count3 = mp.getCount();
                    System.out.println("It has " + count3 +
                        " BodyParts in it**");
                    for (int j = 0; j < count3; j++) {
                        // Part are numbered starting at 0
                        BodyPart b = mp.getBodyPart(j);
                        String mimeType2 = b.getContentType();
                        System.out.println( "BodyPart " + (j + 1) +
                                            " is of MimeType " + mimeType);

                        Object o2 = b.getContent();
                        if (o2 instanceof String) {
                            System.out.println("**This is a String BodyPart**");
                            System.out.println((String)o2);
                        }
                        else if (o2 instanceof Multipart) {
                            System.out.print(
                                "**This BodyPart is a nested Multipart.  ");
                            Multipart mp2 = (Multipart)o2;
                            int count2 = mp2.getCount();
                            System.out.println("It has " + count2 +
                                "further BodyParts in it**");
                        }
                        else if (o2 instanceof InputStream) {
                            System.out.println(
                                "**This is an InputStream BodyPart**");
                        }
                    } //End of for
                }
                else if (o instanceof InputStream) {
                    System.out.println("**This is an InputStream message**");
                    InputStream is = (InputStream)o;
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
                return s;
    }
    
}
