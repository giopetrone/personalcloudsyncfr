/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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

    public SingleUser getUserByEmail(String mail) {
        SingleUser sU = null;
        boolean trovato = false;
        Set<String> ids = cloudUsers.keySet();
        Iterator<String> iter = ids.iterator();
        while (iter.hasNext() && !trovato) {
            String id = iter.next();
            sU = cloudUsers.get(id);
            if (mail.equals(sU.getMailAddress())) {
                trovato = true;
            }
        }
        if (!trovato) {
            sU = null;
        }
        return sU;
    }

    private void tempInit() {
        //aggiungere altri utenti
        cloudUsers.put("113562596126755969974", new SingleUser("Giovanna", "gio.petrone@gmail.com", "mer20ia05", "113562596126755969974", ""));
        cloudUsers.put("105431105185369856275", new SingleUser("Anna", "annamaria.goy@gmail.com", "tex_willer", "105431105185369856275", ""));
        cloudUsers.put("117045399709249522734", new SingleUser("Marino", "sgnmrn@gmail.com", "micio11", "117045399709249522734", ""));
        
        cloudUsers.put("105588383970476786723", new SingleUser("utntest1", "utntest1@gmail.com", "passtest", "105588383970476786723", ""));
        cloudUsers.put("106599813745807651946", new SingleUser("utntest2", "utntest2@gmail.com", "passtest", "106599813745807651946", ""));

        cloudUsers.put("109389007736899610246", new SingleUser("utntest3", "utntest3@gmail.com", "passtest", "109389007736899610246", ""));
        cloudUsers.put("113431938299429727191", new SingleUser("utntest4", "utntest4@gmail.com", "passtest", "113431938299429727191", ""));

        cloudUsers.put("109816003321688504682", new SingleUser("utntest5", "utntest5@gmail.com", "passtest", "109816003321688504682", ""));
        cloudUsers.put("105511499750624243589", new SingleUser("utntest6", "utntest6@gmail.com", "passtest", "105511499750624243589", ""));
        cloudUsers.put("101265267177331581252", new SingleUser("utntest7", "utntest7@gmail.com", "passtest", "101265267177331581252", ""));
        cloudUsers.put("102268038416982124819", new SingleUser("utntest8", "utntest8@gmail.com", "passtest", "102268038416982124819", ""));
        cloudUsers.put("104985310118499786663", new SingleUser("utntest9", "utntest9@gmail.com", "passtest", "104985310118499786663", ""));
        cloudUsers.put("100032147672403918727", new SingleUser("utntest10", "utntest10@gmail.com", "passtest", "100032147672403918727", ""));
        cloudUsers.put("113980194353852078947", new SingleUser("utntest11", "utntest11@gmail.com", "passtest", "113980194353852078947", ""));
        cloudUsers.put("107554240302350785323", new SingleUser("utntest12", "utntest12@gmail.com", "passtest", "107554240302350785323", ""));
        cloudUsers.put("111571259565140978311", new SingleUser("utntest13", "utntest13@gmail.com", "passtest", "111571259565140978311", ""));
    }
}
