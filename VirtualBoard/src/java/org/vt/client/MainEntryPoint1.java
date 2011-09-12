/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vt.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Main entry point.
 *
 * @author marino
 */
public class MainEntryPoint1 implements EntryPoint {

    private static TextArea messaggio = new TextArea();
    AbsolutePanel currTab = null;
    PickupDragController dragController = null;

    /**
     * Creates a new instance of MainEntryPoint
     */
    public MainEntryPoint1() {
    }

    public static void debug(String s) {
        messaggio.setText(messaggio.getText() + "\n" + s);
    }

    /**
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    public void onModuleLoad() {
        messaggio.setPixelSize(400, 150);
        RootPanel.get().add(messaggio);
         VerticalPanel mainPanel = new VerticalPanel();
         mainPanel.addStyleName("mainPanelStyle");
         mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
         RootPanel.get("demo").add(mainPanel);
        Label mainTitle = new Label("My Tables");
        mainTitle.addStyleName("MainLabel");
        mainTitle.setPixelSize(600, 40);
        mainPanel.add(mainTitle);
        final TabPanel tabPanel = new TabPanel();
        mainPanel.add(tabPanel);
        tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

            public void onSelection(SelectionEvent<Integer> event) {
                currTab = (AbsolutePanel) tabPanel.getWidget(event.getSelectedItem().intValue());
            }
        });
        AbsolutePanel absolutePanel = new AbsolutePanel();
        currTab = absolutePanel;
        absolutePanel.addStyleName("style1");
        absolutePanel.setPixelSize(900, 400);
        dragController = new PickupDragController(absolutePanel, true);
        dragController.setBehaviorDragStartSensitivity(5);
        dragController.setBehaviorDragProxy(true);
        DropController dropController = new AbsolutePositionDropController(RootPanel.get());
        // Don't forget to register each DropController with a DragController
        dragController.registerDropController(dropController);
        PushButton nut = new PushButton(new Image("TaskMgr.jpg"));
        VerticalPanel pa = new VerticalPanel();
        Label lab = new Label("STEP 1");
        HTML header = new HTML("Drag Handle");
        pa.add(header);
        pa.add(nut);
        pa.add(lab);
        absolutePanel.add((pa));
        dragController.makeDraggable(pa, header);
        nut.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open(" http://www.piemonte.di.unito.it/TaskMgr/index.jsp?Flow=Flow_aug30-1.txt", "_blank", "");
            }
        });
        tabPanel.add(absolutePanel, "STEPS");
        absolutePanel = new AbsolutePanel();
          absolutePanel.addStyleName("style2");
        absolutePanel.setPixelSize(900, 400);
        tabPanel.add(absolutePanel, "Virtual Collaboration");
          nut = new PushButton(new Image("GogDoc.jpg"));
        pa = new VerticalPanel();
        lab = new Label("titolo documento");
        header = new HTML("Title/Header (Drag Handle) with <a href='http://google.com/' target='_blank'>link</a>");
        pa.add(header);
        pa.add(nut);
        pa.add(lab);
        absolutePanel.add((pa));
        dragController.makeDraggable(pa, header);
        nut.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open("https://docs.google.com/document/d/11zOX13sBVGExwkFgmlUdGFgz3L0Uvp7l73rL2VR0sxk/edit?hl=en_US", "_blank", "");
            }
        });
        tabPanel.selectTab(0);
        tabPanel.setSize("900px", "400px");
        tabPanel.addStyleName("table-center");
       // RootPanel.get("demo").add(tabPanel);
        //   String content = " <div id=\"obiettivo\" ondragenter=\"return false;\" ondragover=\"return false;\" ondrop=\"return OnDropTarget (event);\" style=\"width:300px; height:100px; background-color:#f08080;\"> </div>";
        String content = " <div id=\"obiettivo\" style=\"width:30px; height:30px; background-color:#f09080;\"> </div>";
        HTML widget = new HTML(content);
        RootPanel.get("demo").sinkEvents(Event.MOUSEEVENTS);
        Event.addNativePreviewHandler(new NativePreviewHandler() {

            public void onPreviewNativeEvent(NativePreviewEvent preview) {
                NativeEvent event = preview.getNativeEvent();
                Element elt = event.getEventTarget().cast();
                if (!elt.getId().equals("obiettivo")) {
                    return;
                }
                if (!event.getType().equals("click")) {
                    return;
                }
                //  debug("Evento: " + event.getString() + " " + event.getType());
                addWidget(event.getClientX(), event.getClientY());
                // Tell the event handler that this event has been consumed
                //  preview.consume();
            }
        });
        RootPanel.get("demo").add(widget);
    }

    private String contentOf(TableElement tabo) {
        NodeList<TableRowElement> righe = tabo.getRows();
        // debug("dopo righe");
        String cont = "";
        for (int i = 0; i < righe.getLength(); i++) {
            TableRowElement riga = righe.getItem(i);
            NodeList<TableCellElement> colonne = riga.getCells();
            for (int j = 0; j < colonne.getLength(); j++) {
                TableCellElement cella = colonne.getItem(j);
                cont += cella.getInnerText() + " ";
            }
            cont += "\n";
        }
        return cont;
    }

    private String getUrl(TableElement tabo) {
        NodeList<TableRowElement> righe = tabo.getRows();
        for (int i = 1; i < righe.getLength(); i++) {
            TableRowElement riga = righe.getItem(i);
            NodeList<TableCellElement> colonne = riga.getCells();
            TableCellElement cella = colonne.getItem(0);
            //  if (cella.getInnerText().equals("text/x-moz-url")) {
            if (cella.getInnerText().equals("text/plain")) {
                String cont = colonne.getItem(1).getInnerText();
                if (!cont.equals("")) {
                    return cont;
                }
            }
        }
        return "noUrl";
    }

    private String primaRigaBuona(TableElement tabo) {
        NodeList<TableRowElement> righe = tabo.getRows();
        debug("dopo righe");
        String cont = "";
        for (int i = 1; i < righe.getLength(); i++) {
            TableRowElement riga = righe.getItem(i);
            NodeList<TableCellElement> colonne = riga.getCells();
            for (int j = 1; j < colonne.getLength(); j++) {
                TableCellElement cella = colonne.getItem(j);
                cont += cella.getInnerText();
                if (!cont.equals("")) {
                    return cont;
                }
            }
        }
        return cont;
    }

    private String abbrevia(String ur) {
        String ret = "";
        int ind = ur.indexOf('/');
        int ind1 = ur.indexOf('/', ind + 2);
        if (ind1 > 0) {
            return ur.substring(0, ind1) + "(..)";
        } else {
            return ur.substring(0, ind) + "(..)";
        }
    }

    private void addWidget(int posX, int posY) {
        Element el = DOM.getElementById("info");
        TableElement tabo = TableElement.as(el);
        debug(contentOf(tabo));
        final String opeart = getUrl(tabo);
        HorizontalPanel pa = new HorizontalPanel();
        pa.setBorderWidth(2);
        //  HTML header = new HTML("(Drag Handle)with <a href='http://google.com/' target='_blank'>link</a>");
        String titolo = abbrevia(opeart);
        debug("link == "+titolo + " <a href='" + opeart + "' target='_blank'>link</a>");
        HTML header = new HTML(titolo + " <a href='" + opeart + "' target='_blank'>link</a>");
        pa.add(header);
        dragController.makeDraggable(pa, header);
        currTab.add(pa, posX, posY - 20);
    }
}
