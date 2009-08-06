/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;


/**
 *
 * @author marino
 */
public  class Condition extends TreeNode {


    public Condition() {    
    }

    public Condition(String name) {
       super(name);
    }

  
    public void addMember(EventDescription b) {
        getChildren().add(b);
    }



}
