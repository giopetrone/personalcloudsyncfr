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
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;

/**
 * Main entry point.
 *
 * @author marino
 */
public class MainEntryPoint1 implements EntryPoint {

    private static TextArea messaggio = new TextArea();
    AbsolutePanel currTab = null;
    PickupDragController dragController = null;
    TabPanel tabPanel = new TabPanel();
    ArrayList<Attiva> pannelli = new ArrayList();

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
        final VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.addStyleName("mainPanelStyle");
        mainPanel.setHeight(Window.getClientHeight() + "px");
        mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        RootPanel.get("demo").add(mainPanel);
        Window.addResizeHandler(new ResizeHandler() {

            public void onResize(ResizeEvent event) {
                int height = event.getHeight();
                mainPanel.setHeight(height + "px");
            }
        });
        Label mainTitle = new Label("My Tables");
        mainTitle.addStyleName("MainLabel");
        mainTitle.setPixelSize(600, 40);
        mainPanel.add(mainTitle);
        tabPanel.setSize("900px", "400px");
        tabPanel.addStyleName("table-center");
        mainPanel.add(tabPanel);
        tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                currTab = (AbsolutePanel) tabPanel.getWidget(event.getSelectedItem().intValue());
                dragController = pannelli.get(event.getSelectedItem().intValue()).getDrag();
            }
        });
        AbsolutePanel absolutePanel = addTabPanel( "style1","STEPS");
        VerticalPanel pa = creaPannello("TaskMgr.jpg","STEP 1","Drag Handle"," http://www.piemonte.di.unito.it/TaskMgr/index.jsp?Flow=Flow_aug30-1.txt",absolutePanel);
        absolutePanel.add((pa));
        VerticalPanel pag = creaPannello("GogDoc.jpg","STEP 1","Drag Handle"," https://docs.google.com/Doc?docid=0AbFwBaRMUH4EZGY2bng5ZjNfMTQxYzNtNWhtZHE&hl=en_US",absolutePanel);
        absolutePanel.add((pag));
        absolutePanel = addTabPanel( "style2","Virtual Collaboration");
        pa = creaPannello("GogDoc.jpg","titolo documento","Drag Handle","https://docs.google.com/document/d/11zOX13sBVGExwkFgmlUdGFgz3L0Uvp7l73rL2VR0sxk/edit?hl=en_US",absolutePanel);
        absolutePanel.add((pa));
        tabPanel.selectTab(0);
      
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

        FlexTable f = new FlexTable();
          FlexTable.FlexCellFormatter form = f.getFlexCellFormatter();
         f.setText(0, 0, "week"); form.addStyleName(0,0,"style1");

        RootPanel.get("demo").add(f);
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
        debug("link == " + titolo + " <a href='" + opeart + "' target='_blank'>link</a>");
        HTML header = new HTML(titolo + " <a href='" + opeart + "' target='_blank'>link</a>");
        Image la = new Image("TaskMgr.jpg");
        pa.add(la);
        pa.add(header);
        int dropX = currTab.getAbsoluteLeft();
        int dropY = currTab.getAbsoluteTop();
        currTab.add(pa, posX > dropX ? posX : dropX, posY - 20 > dropY ? posY - 20 : dropY);
        dragController.makeDraggable(pa, header);
    }
    
    private  AbsolutePanel addTabPanel( String a, String b){
        AbsolutePanel ret = new AbsolutePanel();       
        ret.addStyleName(a);
        ret.setPixelSize(900, 400);
        Attiva curAtt = new Attiva(ret);  
        pannelli.add(curAtt);     
        tabPanel.add(ret, b);
        return ret;      
    }

    private VerticalPanel creaPannello(String a, String b, String c, final String d, AbsolutePanel pan){
        VerticalPanel ret = new VerticalPanel();
        PushButton nut = new PushButton(new Image(a));
        Label lab = new Label(b);
        HTML header = new HTML(c);
        ret.add(header);
        ret.add(nut);
        ret.add(lab);
        getDrag(pan).makeDraggable(ret, header);
        nut.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open(d, "_blank", "");
            }
        });
        return ret;
    }

     private PickupDragController getDrag(AbsolutePanel pa){

                for (Attiva at: pannelli){
                    if (at.absolutePanel == pa)
                        return at.dragController;
                }
                return null;

        }

    class Attiva {

        AbsolutePanel absolutePanel;
        PickupDragController dragController;
        DropController dropController;

        public Attiva(AbsolutePanel absolutePanel) {
            this.absolutePanel = absolutePanel;
            dragController = new PickupDragController(absolutePanel, true);
            dragController.setBehaviorDragStartSensitivity(5);
            dragController.setBehaviorDragProxy(true);
            dragController.setBehaviorConstrainedToBoundaryPanel(true);
            dropController = new AbsolutePositionDropController(RootPanel.get());
            // Don't forget to register each DropController with a DragController

            // prox riga sembra inutile?????
           // dragController.registerDropController(dropController);
        }

        public PickupDragController getDrag(){
          
                return dragController;

        }
    }


}
