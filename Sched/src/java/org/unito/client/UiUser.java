/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
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
    private AppointmentStyle styleInt;
    private double weight = 1.0;
    private static ArrayList<UiUser> users = new ArrayList();
   // private static UiUser Anonymous = null;

    public UiUser() {
        this.id = "anonymous";
        this.weight = 0.0;
        addUser(this, false);
    }
    /*
     public static void addUser(UiUser u) {
     u.setStyle("styleUser" + (users.size() + 1));
     users.add(u);
     }
     */

    public static void addUser(UiUser u, boolean dostyle) {
        if (trovato(u.getId()) != null) {
            return;
        }
        if (dostyle) {
            int sti = users.size() + 1;
            if (AppointmentBuilder.GOOGLE_STYLES.length < sti) {
                //  Window.alert("too few styles:");
                sti = 0;
            }
            //   Window.alert("sti,i "+sti+" "+ AppointmentBuilder.GOOGLE_STYLES[sti]);
            u.setStyleInt(AppointmentBuilder.GOOGLE_STYLES[sti]);
            u.setStyle(AppointmentBuilder.GOOGLE_STYLES_STRING[sti]);
        }
        users.add(u);
    }

    public UiUser(String name, AppointmentStyle style) {
        this.id = name;
        this.styleInt = style;
    }

    public static void addSpecialUsersAA() {
        UiUser fre = new UiUser("Free", AppointmentStyle.GREEN);
        users.add(fre);
        fre = new UiUser("Busy1", AppointmentStyle.YELLOW);
        users.add(fre);
        fre = new UiUser("Busy2", AppointmentStyle.RED_ORANGE);
        users.add(fre);
        fre = new UiUser("Busy3", AppointmentStyle.RED);
        users.add(fre);
    }

    public UiUser(String name, double weight, boolean dostyle) {
        this.id = name;
        this.weight = weight;
        addUser(this, dostyle);
    }

    public UiUser(String name, boolean dostyle) {
        this.id = name;
        addUser(this, dostyle);
    }

    public static UiUser getFreeUser() {
        return find("Free");
    }

    public static UiUser trovato(String us) {
        for (UiUser u : getUsers()) {
            if (u.id.equals(us)) {
                return u;
            }
        }
        return null;
    }
     
    public static UiUser find(String us) {
        for (UiUser u : getUsers()) {
            if (u.id.equals(us)) {
                return u;
            }
        }
      //  Window.alert("UIuser not found: " + us);
        return null; //Anonymous;
    }

    public static void createUsers() {
        new UiUser("liliana", true);
        new UiUser("giovanna", true);
        new UiUser("gianluca", true);
        new UiUser("marino", true);
        new UiUser("prof.Rossi", 10.0, true);
      //  Anonymous = new UiUser("Anonymous", false);
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

    public void setStyleInt(AppointmentStyle styleInt) {
        this.styleInt = styleInt;
    }

    /**
     * @return the styleInt
     */
    public AppointmentStyle getStyleInt() {
        return styleInt;
    }
}
