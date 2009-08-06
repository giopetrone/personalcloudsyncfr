/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;


import java.util.ArrayList;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;

/**
 *
 * @author marino
 */
public class TreeElement implements Serializable, IsSerializable {

    private String name;

     public  boolean evaluate (ArrayList <EventDescription> availableEvents) {
         return false;
     }

    public TreeElement(String name) {
        this.name = name;
    } 

    public TreeElement() {
    }

    public ArrayList<TreeElement> getChildren() {
        return new ArrayList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean findPath(ArrayList <TreeElement> a, TreeElement b) {
     //   StockWatcher.debug(name);
       
        return b == this;
    }

    public void propagateEvent (FlowEvent event) {
    }


    public void setValue(boolean value) {
    }

}
