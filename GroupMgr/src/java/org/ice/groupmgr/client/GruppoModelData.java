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
public class GruppoModelData extends BaseModelData {

   public GruppoModelData() {
   }


   public GruppoModelData(String id, String nome) {
      setId(id);
      setNome(nome);
   }


   public String getId() {
      return get("id");
   }


   public void setId(String id) {
      set("id", id);
   }


   public String getNome() {
      return get("nome");
   }


   public void setNome(String nome) {
      set("nome", nome);
   }
}


