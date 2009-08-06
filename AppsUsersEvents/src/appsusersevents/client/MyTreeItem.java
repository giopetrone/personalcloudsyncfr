/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class MyTreeItem extends TreeItem {

    private TreeElement content;
  
    public static int STYLE_CALENDAR = 0;
    public static int STYLE_ACTIVITY = 1;

    public MyTreeItem() {
        super();
    }

    public MyTreeItem(Widget w) {
        super(w);
    }

    public MyTreeItem(final TreeElement element, String group, int style) {
        Widget w = null;
        Class cl = ((Object) element).getClass();
        if (style == STYLE_CALENDAR) {
            w = new CheckBox(element.getName());
        } else if (style == STYLE_ACTIVITY) {
            final ActivityDescription desc = cl == ActivityDescription.class ? (ActivityDescription) element : null;
            if (desc != null) {
                if (desc.canStart()) {
                    HTMLLink actionLink = new HTMLLink();
                    actionLink.setHref(desc.getLink());
                    actionLink.setHTML(desc.getName());
                    w = actionLink;
                }
            } else {
                if (cl == FlowEvent.class) {
                    CheckBox ch = new CheckBox(element.getName());
                 /*
                  done in the specific client application

                  ch.addClickListener(new ClickListener() {
                        public void onClick(Widget sender) {
                            CheckBox bo = (CheckBox) sender;
                            element.setValue(bo.isChecked());
                        }
                    }); */
                    w = ch;
                }
            }
        } else {
            System.err.println("MyTreeItem, unknown style: " + style);
        }
        content = element;
        if (w != null) {
            setWidget(w);
        } else {
            setText(element.getName());
        }
        if (cl == FlowEvent.class) {
            setStyleName("event");
        } else if (cl == OrCondition.class ||
                cl == AndCondition.class) {
            setStyleName("condition");

        } else if (cl == EventDescription.class) {
            // setStyleName("userevent");
            setStyleName("myevent");
        } else {
            setStyleName("quoteLabel");
        }
    }

    public void MyTreeItemNew(TreeElement c, String group) {


        //      super(element.getName());
        Class cl = ((Object) c).getClass();
        final ActivityDescription desc = cl == ActivityDescription.class ? (ActivityDescription) c : null;
        if (desc != null && desc.canStart()) {
            HTMLLink actionLink = new HTMLLink();
            actionLink.setHref(desc.getLink());
            actionLink.setHTML(desc.getName());
            setWidget(actionLink);
        }
        if (cl == FlowEvent.class) {
            setStyleName("event");
        } else if (cl == OrCondition.class ||
                cl == AndCondition.class) {
            setStyleName("condition");

        } else if (cl == EventDescription.class) {
            // setStyleName("userevent");
            setStyleName("myevent");
        } else {
            setStyleName("quoteLabel");
        }

        /* super(element.getName());
        RadioButton b = new RadioButton("ALB");
        setWidget(b); */
        content = c;
    /* b.addClickListener(new ClickListener() {

    public void onClick(Widget sender) {
    setText("cliccato");
    }
    }); */
    }

    public MyTreeItem(String s) {
        super(s);
    }

    /**
     * @return the content
     */
    public TreeElement getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(TreeElement content) {
        this.content = content;
    }

    static public MyTreeItem buildTree(TreeElement c, String group, int style) {
        MyTreeItem root = new MyTreeItem(c, group, style);
        return root.buildTree(group, style);
    }

    public MyTreeItem buildTree(String group, int style) {
        ArrayList chil = content.getChildren();
        for (int i = 0; i < chil.size(); i++) {
            TreeElement ch = (TreeElement) chil.get(i);
            Class cl = ((Object) ch).getClass();
            // special cases
            // if condition empty, do not show it
            if (false && (cl == OrCondition.class ||
                    cl == AndCondition.class)) {
                int sz = ch.getChildren().size();
                if (sz == 0) {
                    continue;
                } else if (sz == 1) {
                    // skip AND/OR node if 1 child
                    ch = (TreeElement) ch.getChildren().get(0);

                    if (cl == OrCondition.class) {
                        // ch is an "AND" if 1 child skip it as well
                        int sz1 = ch.getChildren().size();
                        if (sz1 == 0) {
                            continue;
                        } else if (sz1 == 1) {
                            // skip node if 1 child
                            ch = (TreeElement) ch.getChildren().get(0);
                        }
                    }
                }
            }
            addItem(new MyTreeItem(ch, group, style).buildTree(group, style));
        }
        return this;
    }

}
