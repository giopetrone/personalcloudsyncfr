/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;



import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class AndCondition extends Condition  {

    public AndCondition(String name) {
        super("AND");     
    }

     public  boolean evaluate (ArrayList <EventDescription> availableEvents) {
         for (TreeElement el: children) {

             if (!el.evaluate(availableEvents))
                 return false;
         }
         return true;
     }

    public AndCondition() {
       
    }

    public void addEvent(String name) {
        getChildren().add(new FlowEvent(name));
    }

    public void addEvent(FlowEvent bb) {
        getChildren().add(bb);
    }

}
