/*
 * GWTService.java
 *
 * Created on May 19, 2009, 12:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.ice.groupmgr.client;

import appsusersevents.client.EventDescription;
import com.google.gwt.user.client.rpc.RemoteService;
import java.util.List;

/**
 *
 * @author giovanna
 */
public interface GWTService extends RemoteService {

    public List<GruppoModelData> getGruppi();

    public List<ContattoModelData> getContatti();

    //  public List[] getMembriGruppo(int idGruppo);
    public List[] getMembriGruppo(String idGruppo);

    public boolean creaGruppo(String nome, List<ContattoModelData> contatti);

    public boolean eliminaGruppo(String nomeGruppo, String idGruppo, boolean elimina);

//    public boolean modificaGruppo(String nomeGruppo, int idGruppo, List<ContattoModelData> contatti);
    public boolean modificaGruppo(String nomeGruppo, String idGruppo, List<ContattoModelData> contatti);

    public EventDescription[] getEvents(String userName);

    public boolean validateUser(String name, String pwd);

    public String getParameter(String paramname);
}
