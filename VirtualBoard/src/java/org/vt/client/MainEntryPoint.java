/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vt.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Main entry point.
 *
 * @author marino
 */
public class MainEntryPoint implements EntryPoint {

    /**
     * Creates a new instance of MainEntryPoint
     */
    public MainEntryPoint() {
    }

    /** 
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    public void onModuleLoad() {

//        String text1 = "fshadhdsah";
//        String text2 = "Sed egestas, arcu nec accumsan...";
//        String text3 = "Proin tristique, elit at blandit...";
        TabPanel panel = new TabPanel();
        AbsolutePanel absolutepanel = new AbsolutePanel();
        absolutepanel.setPixelSize(600, 800);
        PickupDragController dragController = new PickupDragController(absolutepanel, true);
        PushButton nut = new PushButton(new Image("TaskMgr.jpg"));
        nut.addDomHandler(new HH(),MouseMoveEvent.getType());
// nut.sinkEvents(Event.ONMOUSEMOVE);
        VerticalPanel pa = new VerticalPanel();
        Label lab = new Label("titolo documento");
        HTML header = new HTML("Title/Header (Drag Handle) with <a href='http://google.com/' target='_blank'>link</a>");
        pa.add(header);
        pa.add(nut);
        pa.add(lab);
        absolutepanel.add((pa));
        dragController.makeDraggable(pa, header);
        nut.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open(" http://www.piemonte.di.unito.it/TaskMgr/index.jsp?Flow=Flow_aug30-1.txt", "_blank", "");
                //  Window.alert("Hello, again");
            }
        });
        nut = new PushButton(new Image("GogDoc.jpg"));
        pa = new VerticalPanel();
        lab = new Label("titolo documento");
        header = new HTML("Title/Header (Drag Handle) with <a href='http://google.com/' target='_blank'>link</a>");
        pa.add(header);
        pa.add(nut);
        pa.add(lab);
        absolutepanel.add((pa));
        dragController.makeDraggable(pa, header);
        nut.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open("https://docs.google.com/document/d/11zOX13sBVGExwkFgmlUdGFgz3L0Uvp7l73rL2VR0sxk/edit?hl=en_US", "_blank", "");
            }
        });
        nut = new PushButton(new Image("GogDoc.jpg"));
        pa = new VerticalPanel();
        lab = new Label("titolo documento2");
        header = new HTML("Title/Header ALTRA ROBA with <a href='http://google.com/' target='_blank'>link</a>");
        pa.add(header);
        pa.add(nut);
        pa.add(lab);
        absolutepanel.add((pa));
        dragController.makeDraggable(pa, header);
        nut.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open(" http://www.piemonte.di.unito.it/TaskMgr/index.jsp?Flow=Flow_aug30-1.txt", "_blank", "");
            }
        });
        //   absolutepanel.add(new Label(text1));
        panel.add(absolutepanel, "STEPS");
        absolutepanel = new AbsolutePanel();
        //  absolutepanel.add(new Label(text2));
        panel.add(absolutepanel, "Virtual Collaboration");
        absolutepanel = new AbsolutePanel();
        // absolutepanel.add(new Label(text3));
        panel.add(absolutepanel, "Trip");
        panel.selectTab(0);
        panel.setSize("500px", "250px");
        panel.addStyleName("table-center");
        RootPanel.get("demo").add(panel);
    }

    class HH implements MouseMoveHandler{
        @Override
public void onMouseMove(MouseMoveEvent event) {

event.preventDefault();
            com.google.gwt.user.client.Window.alert("onMouseUp "+ event.toDebugString());
}

    }
}
