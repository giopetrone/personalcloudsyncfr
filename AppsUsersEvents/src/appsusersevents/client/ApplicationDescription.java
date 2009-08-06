/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class ApplicationDescription extends TreeNode {

    /**
     * @return the appStrings
     */
 //   private FlowEvent lastEvent = null;
    private static ApplicationDescription MainApplication = new ApplicationDescription("Empty");
    private static ApplicationDescription UserApplication = new ApplicationDescription("Empty UserApps");

    public void setMainApplication() {
        MainApplication = this;
    }
/*
    public static void PropagateEvent(FlowEvent event) {
        MainApplication.lastEvent = event;
        MainApplication.propagateEvent(event);
    }

    public FlowEvent getOnceLastEvent()  {
        FlowEvent ret = lastEvent;
        lastEvent = null;
        return ret;
    }
 * */

    public void setUserApplication() {
        UserApplication = this;
    }

    public ApplicationDescription() {
    }

    public static String getAppStrings() {
        return appStrings;
    }

    public static ApplicationDescription getMain() {
        return MainApplication;
    }

    public static ApplicationDescription getUser() {
        return UserApplication;
    }

    /**
     * @param aAppStrings the appStrings to set
     */
    public static void setAppStrings(String aAppStrings) {
        appStrings = aAppStrings;
    }
    /**
     * @return the applications
     */
    private static String appStrings;
//     private static ArrayList applications = new ArrayList();

    public ApplicationDescription(String name) {
        super(name);
    }

    // return the event path, starting from the event
    // and ending with the applications to which it belongs
    public static ArrayList<TreeElement> getPath(ApplicationDescription ap, FlowEvent b) {
        ArrayList<TreeElement> ret = new ArrayList();
        ApplicationDescription aa = ap == null ? MainApplication : ap;
        boolean found = aa.findPath(ret, b);
        return ret;
    }
}
