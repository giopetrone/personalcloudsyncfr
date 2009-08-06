/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package appsusersevents.client;



/**
 *
 * @author marino
 */
public class NotCondition extends Condition  {

    public NotCondition() {
    }

    public void addCondition(Condition a) {
        getChildren().add(a);
    }
}