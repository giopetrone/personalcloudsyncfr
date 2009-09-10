/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ice.groupmgr.client;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 *
 * @author giovanna
 */
public class ContattoModelData extends BaseModelData implements Comparable {

   public ContattoModelData() {
   }

    public ContattoModelData(String string, String plainText) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


   public ContattoModelData(String id, String cognome, String nome, String mail) {
      setId(id);
      setCognome(cognome);
      setNome(nome);
      setMail(mail);
   }


   public ContattoModelData(String id, String cognome, String nome, String mail,
          String statoConferma) {
      setId(id);
      setCognome(cognome);
      setNome(nome);
      setMail(mail);
      setStatoConferma(statoConferma);
   }


   public String getId() {
      return get("id");
   }


   public void setId(String id) {
      set("id", id);
   }


   public String getCognome() {
      return get("cognome");
   }


   public void setCognome(String cognome) {
      set("cognome", cognome);
   }


   public String getNome() {
      return get("nome");
   }


   public void setNome(String nome) {
      set("nome", nome);
   }


   public String getMail() {
      return get("mail");
   }


   public void setMail(String mail) {
      set("mail", mail);
   }


   public String getStatoConferma() {
      return get("statoConferma");
   }


   public void setStatoConferma(String statoConferma) {
      set("statoConferma", statoConferma);
   }


   public int compareTo(Object o) {
      String cognomeNome1 = getCognome() + " " + getNome();
      String cognomeNome2 = ((ContattoModelData) o).getCognome() + " " +
             ((ContattoModelData) o).getNome();
      return cognomeNome1.compareTo(cognomeNome2);
   }
}
