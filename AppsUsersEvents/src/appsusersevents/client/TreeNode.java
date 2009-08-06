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
public class TreeNode extends TreeElement{

     protected ArrayList <TreeElement> children = new ArrayList();


    public TreeNode(String name) {
       super(name);
    }

     public TreeNode() {
    }

     public void addChild(TreeElement t) {
         children.add(t);
     }

    /**
     * @return the children
     */
    public ArrayList<TreeElement> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(ArrayList<TreeElement> children) {
        this.children = children;
    }


    public void propagateEvent (FlowEvent event) {
          ArrayList <TreeElement> chil = getChildren();
        for (TreeElement ch: chil) {
            ch.propagateEvent(event);
        }
    }

    public boolean findPath(ArrayList <TreeElement> a, TreeElement b) {
        //   StockWatcher.debug("AD_"+ activityName);
        ArrayList <TreeElement> chil = getChildren();
        for (int i = 0; i < chil.size(); i++) {
            TreeElement ch =  chil.get(i);
          //  StockWatcher.debug("AD_chil" + ch.getName() + " " + b + " " + ch);
            if (ch.findPath(a, b)) {
                a.add(ch);
                return true;
            }
        }
        return false;
    }
}
