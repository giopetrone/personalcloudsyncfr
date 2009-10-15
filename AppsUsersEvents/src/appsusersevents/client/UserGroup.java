/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author marino
 */
public class UserGroup extends TreeNode {

    private String id;
    private static UserGroup MainGroup = new UserGroup("Empty", "");

    public void setMainGroup() {
        MainGroup = this;
    }

    public UserGroup() {
    }

    public UserGroup(String name, String id) {
        super(name);
        this.id = id;
    }

    public static ArrayList<TreeElement> getPath(TreeElement b) {
        ArrayList<TreeElement> ret = new ArrayList();
        boolean found = MainGroup.findPath(ret, b);
        return ret;
    }

    public static HashMap<String, SingleUser> getUserMap(ArrayList<TreeElement> destinationUsers) {
        // return selected single users avoiding duplicates
        Iterator<TreeElement> it = destinationUsers.iterator();
        HashMap<String, SingleUser> allUsers = new HashMap();
        while (it.hasNext()) {
            TreeElement item = it.next();
            Class cl = item.getClass();
            if (cl == appsusersevents.client.SingleUser.class) {
                allUsers.put(item.getName(), (SingleUser) item);
            } else {
                // must be a user group
                ArrayList<TreeElement> childr = ((TreeNode) item).getChildren();
                for (int i = 0; i < childr.size(); i++) {
                    SingleUser su = (SingleUser) childr.get(i);
                    allUsers.put(su.getName(), su);
                }
            }
        }
        return allUsers;
    }

    public static ArrayList<String> getUserAndGroups(ArrayList<TreeElement> destinationUsers) {
        // return selected single users avoiding duplicates
        ArrayList<String> ret = new ArrayList();
        for (TreeElement item : destinationUsers) {
            Class cl = item.getClass();
            if (cl == appsusersevents.client.SingleUser.class) {
                ret.add(((SingleUser) item).getMailAddress());
            } else {
                ret.add(item.getName());
            }
        }
        return ret;
    }

    public static ArrayList<String> getSingleUsers(ArrayList<TreeElement> destinationUsers) {
        // return all selected single users
        Iterator<TreeElement> it = destinationUsers.iterator();
        ArrayList<String> retUsers = new ArrayList();
        while (it.hasNext()) {
            TreeElement item = it.next();
            Class cl = item.getClass();
            if (cl == appsusersevents.client.SingleUser.class) {
                retUsers.add(item.getName());
            }
        }
        return retUsers;
    }

    public static ArrayList<String> getGroups(ArrayList<TreeElement> destinationUsers) {
        // return selected user groups avoiding duplicates
        Iterator<TreeElement> it = destinationUsers.iterator();
        ArrayList<String> retUsers = new ArrayList();
        while (it.hasNext()) {
            TreeElement item = it.next();
            Class cl = item.getClass();
            if (!(cl == appsusersevents.client.SingleUser.class)) { // must be a group
                retUsers.add(item.getName());
            }
        }
        return retUsers;
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
    public void setId(String id) {
        this.id = id;
    }
}
