/*
 * GWTServiceAsync.java
 *
 * Created on May 19, 2009, 12:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.ice.groupmgr.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;

/**
 *
 * @author giovanna
 */
public interface GWTServiceAsync {

    public void getGruppi(AsyncCallback asyncCallback);

    public void getContatti(AsyncCallback asyncCallback);

    public void getMembriGruppo(String idGruppo, AsyncCallback asyncCallback);
    //   public void getMembriGruppo(int idGruppo, AsyncCallback asyncCallback);

    public void creaGruppo(String nome, List<ContattoModelData> contatti,
            AsyncCallback asyncCallback);

    public void eliminaGruppo(String nomeGruppo, String idGruppo, boolean elimina, AsyncCallback asyncCallback);

    public void getEvents(String userName, AsyncCallback callback);

    //  public void modificaGruppo(String nomeGruppo, int idGruppo, List<ContattoModelData> contatti,            AsyncCallback asyncCallback);
    public void modificaGruppo(String nomeGruppo, String idGruppo, List<ContattoModelData> contatti, AsyncCallback asyncCallback);

    public void validateUser(String name, String pwd, AsyncCallback callback);

    public void getParameter(String paramname, AsyncCallback callback);
}
