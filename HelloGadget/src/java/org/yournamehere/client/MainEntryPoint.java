/*
 * MainEntryPoint.java
 *
 * Created on April 9, 2009, 11:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.yournamehere.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author giovanna
 */
public class MainEntryPoint implements EntryPoint {
    
    /** Creates a new instance of MainEntryPoint */
    public MainEntryPoint() {
    }
    
    /**
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    public void onModuleLoad() {
        final Label label = new Label("Hello, GWT!!!");
        final Button button = new Button("Click me!");
        
        button.addClickListener(new ClickListener(){
            public void onClick(Widget w) {
                label.setVisible(!label.isVisible());
            }
        });
        
        RootPanel.get().add(button);
        RootPanel.get().add(label);
        RootPanel.get().add(new GWTServiceUsageExample());
    }
    
}
