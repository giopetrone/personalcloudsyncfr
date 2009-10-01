/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

import java.util.HashMap;

/**
 *
 * @author giovanna
 */
public class CloudUsers {

    private HashMap<String, SingleUser> cloudUsers;

    public CloudUsers() {
        cloudUsers = new HashMap();
        tempInit();
    }

    public void addCloudUser(SingleUser sU) {
        cloudUsers.put(sU.getGoogleId(), sU);

    }

    public SingleUser getUser(String googleId) {
        return cloudUsers.get(googleId);
    }

    public boolean removeCloudUser(String googleId) {
        boolean ret = false;
        ret = (cloudUsers.remove(googleId) != null);
        return ret;
    }

    public HashMap<String, SingleUser> getCloudUsers() {
        return cloudUsers;
    }

    public void setCloudUsers(HashMap<String, SingleUser> cloudUsers) {
        this.cloudUsers = cloudUsers;
    }

    private void tempInit() {
        //aggiungere altri utenti
        cloudUsers.put("113562596126755969974", new SingleUser("Giovanna", "gio.petrone@gmail.com", "mer20ia05", "113562596126755969974", ""));
        cloudUsers.put("105431105185369856275", new SingleUser("Anna", "annamaria.goy@gmail.com", "tex_willer", "105431105185369856275", ""));
    }
}
