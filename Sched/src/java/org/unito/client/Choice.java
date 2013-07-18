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
public class Choice implements IsSerializable {

    private ArrayList<String> ant = new ArrayList();
    private ArrayList<String> post = new ArrayList();

    public Choice(String a, String p) {
        if (!a.equals("")) {
            ant.add(a);
        }
        if (!p.equals("")) {
            post.add(p);
        }
    }

    public String toRequest() {
        String ret = "<choice ant=\"";
        for (int i = 0; i < getAnt().size(); i++) {
            ret += getAnt().get(i);
            if (i < getAnt().size() - 1) {
                ret += ",";
            }
        }
        ret += "\" post=\"";
        for (int i = 0; i < getPost().size(); i++) {
            ret += getPost().get(i);
            if (i < getPost().size() - 1) {
                ret += ",";
            }
        }
        ret += "\" />";
        return ret;
    }

    public Choice() {
    }

    public String miaStringa() {
        String ret = "";
        if (getAnt().size() > 0) {
            //  String h = ant.get(0);
            //   if (!h.equals("")){
            ret += "anticipate: " + getAnt().get(0);
            //  }
        }
        if (getPost().size() > 0) {
            //     String h = post.get(0);
            //    if (!h.equals("")){
            ret += "posticipate: " + getPost().get(0);
            //   }
        }
        return ret;
    }

    public String leggibile() {
        String ret = "";
        if (getAnt().size() > 0) {
            //  String h = ant.get(0);
            //   if (!h.equals("")){
            ret += "anticipate: " + getAnt().get(0);
            //  }
        }
        if (getPost().size() > 0) {
            //     String h = post.get(0);
            //    if (!h.equals("")){
            ret += "posticipate: " + getPost().get(0);
            //   }
        }
        if (ret.equals("")) {
            return "Nothing";
        } else {
            return ret;
        }
    }

    /**
     * @return the ant
     */
    public ArrayList<String> getAnt() {
        return ant;
    }

    /**
     * @param ant the ant to set
     */
    public void setAnt(ArrayList<String> ant) {
        this.setAnt(ant);
    }

    /**
     * @return the post
     */
    public ArrayList<String> getPost() {
        return post;
    }

    /**
     * @param post the post to set
     */
    public void setPost(ArrayList<String> post) {
        this.setPost(post);
    }
}
