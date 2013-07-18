/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class Update implements IsSerializable {

    private String user = "";
    private ArrayList<Choice> choice = new ArrayList();

    public String toRequest() {
        String ret = "<updates user=\"" + user + "\">";
        for (Choice cho : getChoice()) {
            ret += cho.toRequest();
        }
        ret += "</updates>";
        return ret;
    }

    public Update(String user) {
        this.user = user;
    }

    public Update() {
    }

    public String miaStringa() {
        String ret = "Update: ";
        for (Choice cho : getChoice()) {
            ret += "choice: " + cho.miaStringa();

        }
        return ret;
    }

    public String leggibile() {
        String ret = "needs: ";
        for (int i = 0; i < choice.size();i++){
            Choice cho = choice.get(i);
            ret += cho.leggibile();
        }
        return ret;
    }

    public void addChoice(Choice t) {
        getChoice().add(t);
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the choice
     */
    public ArrayList<Choice> getChoice() {
        return choice;
    }

    /**
     * @param choice the choice to set
     */
    public void setChoice(ArrayList<Choice> choice) {
        this.choice = choice;
    }
}