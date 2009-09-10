/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ice.groupmgr.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.ice.groupmgr.client.ContattoModelData;

/**
 *
 * @author giovanna
 */
public class GroupInProgress {

    private List<ContattoModelData> contatti;
    private HashSet<String> risposte;

    public List<ContattoModelData> getContatti() {
        return contatti;
    }

    public void setContatti(List<ContattoModelData> contatti) {
        this.contatti = contatti;
    }

    public HashSet<String> getRisposte() {
        return risposte;
    }

    public void setRisposte(HashSet<String> risposte) {
        this.risposte = risposte;
    }

    public GroupInProgress() {

        contatti = new ArrayList<ContattoModelData>();
        risposte = new HashSet();
    }

    public GroupInProgress(List<ContattoModelData> cont) {

        contatti = cont;
        risposte = new HashSet();
    }

    public void addRequest(String user) {
        risposte.add(user);
    }

    public boolean addAnswer(String user) {
        return risposte.remove(user);
    }
}
