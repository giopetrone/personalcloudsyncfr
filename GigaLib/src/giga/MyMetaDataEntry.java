/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package giga;

import appsusersevents.client.EventDescription;
import com.gigaspaces.annotation.pojo.FifoSupport;
import com.gigaspaces.annotation.pojo.SpaceClass;
import com.thoughtworks.xstream.XStream;
import java.io.Serializable;

//@SpaceClass(replicate = true, persist = false, fifo = true)
@SpaceClass(replicate = true, persist = false, fifoSupport=FifoSupport.OPERATION)

public class MyMetaDataEntry  implements Serializable {
    private String senderId;
    private String receiverId;
    private String content;
    //   public String request;
    //   public Object /*MyCalendaEventEntry*/ cee;

    public MyMetaDataEntry() {
    }

    public MyMetaDataEntry(Object o) {
        //   cee= o;
        XStream xstream = new XStream();
        content = xstream.toXML(o);
    }

    public Object fromXmlToObject() {
        if (getContent() == null) {
            return null;
        }
        XStream xstream = new XStream();
        return xstream.fromXML(getContent());
    }

    public EventDescription getEvent() {
        if (getContent() != null) {
            XStream xstream = new XStream();
            try {
                Object ob = xstream.fromXML(getContent());
                if (ob.getClass() == appsusersevents.client.EventDescription.class) {
                    return (EventDescription) ob;
                }
            } catch (com.thoughtworks.xstream.io.StreamException ex) {

                System.err.println("xstream, getEvent, error in metadataentry! content= " + getContent());
            }
        }
        return null;
    }

    public String printString() {

        String ret = "EMPTY METADATA INSTANCE";
        if (getContent() != null) {


            /*  temporaneo */
            XStream xstream = new XStream();
            try {
                Object ob = xstream.fromXML(getContent());
                if (ob.getClass() == appsusersevents.client.EventDescription.class) {
                    return "content = " + ((EventDescription) ob).getDescription();
                } else {
                    return "content = " + xstream.fromXML(getContent()).toString();
                }
            } catch (com.thoughtworks.xstream.io.StreamException ex) {
                // ex.printStackTrace();
                return "xml error in metadataentry! content= " + getContent();
            }
        /* */

        }
        return ret;
    }

    /**
     * @return the senderId
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * @param senderId the senderId to set
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the receiverId
     */
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * @param receiverId the receiverId to set
     */
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
}
