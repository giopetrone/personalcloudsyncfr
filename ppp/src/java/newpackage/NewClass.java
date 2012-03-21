/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage;

import com.gdevelop.gwt.syncrpc.SyncProxy;
import java.util.ArrayList;
import org.yournamehere.client.GWTService;

/**
 *
 * @author marino
 */
public class NewClass {

    private static GWTService rpcService = null;

    public static void main(String[] args) {

        rpcService = (GWTService) SyncProxy.newProxyInstance(GWTService.class,
                "http://localhost:8080/ppp/org.yournamehere.Main/", "gwtservice");  

        String result = rpcService.myMethod("SyncProxy");

        System.out.println("result=" + result);

        ArrayList<String> ret = new ArrayList();
        ret.add("sadDFFF");
        ret.add("BBBBBBB");
        ret.add("wqeqdSSSSS");

        ArrayList<String> re = rpcService.myMethod1(ret);
        
        for (String de : re) {
            System.out.println("bassa=" + de);
        }
    }
}
