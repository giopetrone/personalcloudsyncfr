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
public class OrCondition extends Condition {

     public  boolean evaluate (ArrayList <EventDescription> availableEvents) {
         for (TreeElement el: children) {
             if (el.evaluate(availableEvents))
                 return true;
         }
         return false;
     }

    public OrCondition(String name) {
        super("OR");
    }

     public OrCondition() {
    }

    public void addCondition(AndCondition a) {
        getChildren().add(a);
    }
}
