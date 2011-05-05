/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mailhubevents;

import maillib.HubMailMsg;
import com.thoughtworks.xstream.XStream;
import java.util.ArrayList;
import maillib.NewGmail;

/**
 *
 * @author giovanna
 */
public class MailHubEvents {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        NewGmail gmail = new NewGmail();
        ArrayList <HubMailMsg> msg = gmail.getMailMessages();
        if (msg.size() > 0) {
            HubMailMsg hm = msg.get(0);
           // hm.getTextContent(msg[0]); 
            String s = toXML(hm);
             System.out.println("messaggio in XML \n " + s);

        }

    }
    public static String toXML(HubMailMsg hm) {
        XStream xstream = new XStream();
        String s = xstream.toXML(hm);
        return s;
    }

    public static HubMailMsg fromXml(String s) {
        XStream xstream = new XStream();
        try {
            Object ob = xstream.fromXML(s);
            if (ob.getClass() == maillib.HubMailMsg.class) {

                return (HubMailMsg) ob;
            }
        } catch (com.thoughtworks.xstream.io.StreamException ex) {
            System.err.println("xstream, getEvent, error in HubMailMsg content = " + s);
        }
        return null;
    }
}
