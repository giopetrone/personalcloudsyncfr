/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class UiUser implements IsSerializable {

    /**
     * @return the users
     */
    public static ArrayList<UiUser> getUsers() {
        return users;
    }

    /**
     * @param aUsers the users to set
     */
    public static void setUsers(ArrayList<UiUser> aUsers) {
        users = aUsers;
    }
    private String id;
    private String style = "styleUser";
    private double weight = 1.0;
    private static ArrayList<UiUser> users = new ArrayList();

    public UiUser() {
        this.id = "anonymous";
        this.weight = 0.0;
        addUser(this);
    }

    public static void addUser(UiUser u) {
        u.setStyle("styleUser" + (users.size() + 1));
        users.add(u);
    }

    public UiUser(String name, double weight) {
        this.id = name;
        this.weight = weight;
        addUser(this);
    }

    public UiUser(String name) {
        this.id = name;
         addUser(this);
    }

    public static UiUser find(String us) {
        try {
        for (UiUser u : getUsers()) {
            if (u.id.equals(us)) {
                return u;
            }
        }
        throw new Exception("buu");
      //  Window.alert("User not found: "+us);
        } catch (Exception ex){ex.printStackTrace();}
        return null;
    }

    public static void createUsers() {
        new UiUser("liliana");
        new UiUser("giovanna");
        new UiUser("gianluca");
        new UiUser("marino");
        new UiUser("prof.Rossi", 10.0);
    }

    public static ArrayList<String> getUserIds() {
        ArrayList<String> ret = new ArrayList();
        for (UiUser u : getUsers()) {
            ret.add(u.id);
        }
        return ret;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String email) {
        this.id = email;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(String style) {
        this.style = style;
    }
}
