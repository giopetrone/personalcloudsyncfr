/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.server;

/**
 *
 * @author marino
 */
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.unito.client.Choice;
import org.unito.client.Interval;
import org.unito.client.Task;
import org.unito.client.TaskGroup;
import org.unito.client.Update;
import org.w3c.dom.*;

public class MyDOMParserBean
        implements java.io.Serializable {

    static boolean debug = false;

    public MyDOMParserBean() {
    }

    public static Document getDocument(String file) throws Exception {

        // Step 1: create a DocumentBuilderFactory
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        dbf.setValidating(false);
        // Step 2: create a DocumentBuilder
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Step 3: parse the input file to get
        //  a Document object
        Document doc = db.parse(new File(file));
        doc.normalizeDocument();


        return doc;
    }

    public static Document getDocumentFromStream(InputStream is) throws Exception {

        // Step 1: create a DocumentBuilderFactory
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        dbf.setValidating(false);
        // Step 2: create a DocumentBuilder
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Step 3: parse the input file to get
        //  a Document object
        Document doc = db.parse(is);
        doc.normalizeDocument();


        return doc;
    }

    public static void main(String[] args) {
        TaskGroup ret = fillReply(null);
        Request r = new Request(ret, "", "", "", "", "");
        System.out.println(r.toServerString());
    }
    static String taskName = "";

    public static void parseIntervals(Node n, ArrayList<Interval> inter, String indent) {
        // recupera sia gli intervalli che eventualmente
        // in quali casi  ci siano utenti con schedule da modificare
        String s = "" + n.getClass();
        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");
        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa && debug) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap l5 = n.getAttributes();
        if (l5 == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {

            if (stampa && debug) {
                System.out.println(indent + l5.getLength() + " attributes ");
            }
            for (int k = 0; k < l5.getLength(); k++) {
                Node n8 = l5.item(k);
                // System.out.print(" " + attributo.getNodeName() + " ='" + attributo.getNodeValue() + "'");
                if (n8.getNodeName().equals("task") && n.getNodeName().equals("action")) {
                    taskName = n8.getNodeValue();
                    if (debug) {
                        System.out.print("carico " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                    }
                }
                if (n.getNodeName().equals("interval")) {// provare!!!! || n.getNodeName().equals("intervals")) {
                    n8 = l5.getNamedItem("start");

                    String s1 = n8.getNodeValue();
                    s1 = s1.replaceAll("[(),]", " ");
                    String[] pars = s1.split(" ");
                    // causa reasoner di Gianluca, non si accettano 0
                    // per inizio, quindi sottraggo 1
                    int min = Integer.parseInt(pars[1]) - 1;
                    int max = Integer.parseInt(pars[2]);
                    Interval inte = new Interval(taskName, min, max);
                    inter.add(inte);
                    if (debug) {
                        System.out.print("intervallo task=" + inte.getName() + " " + s1);
                    }
                    n8 = l5.getNamedItem("users");
                    if (n8 != null) {  // recupera utenti
                        s1 = n8.getNodeValue();
                        if (!s1.equals("")) {
                            if (debug) {
                                System.out.print("utenti " + s1);
                            }
                            pars = s1.split(",");
                            for (int i = 0; i < pars.length; i++) {
                                inte.addUser(pars[i]);
                            }
                        }
                    }
                    n8 = l5.getNamedItem("impacts");
                    if (n8 != null) {  // recupera utenti
                        s1 = n8.getNodeValue();
                        if (!s1.equals("")) {
                            if (debug) {
                                System.out.print(" impacts " + s1);
                            }
                            pars = s1.split(",");
                            for (int i = 0; i < pars.length; i++) {
                                inte.addImpact(pars[i]);
                            }
                        }
                        if (debug) {
                            System.out.println();
                        }
                    }
                    NodeList l3333 = n.getChildNodes();
                    for (int rr = 0; rr < l3333.getLength(); rr++) {
                        Node n65 = l3333.item(rr);
                        if (n65.getNodeName().equals("updates")) {
                           
                            NamedNodeMap l54 = n65.getAttributes();
                            Node uu = l54.getNamedItem("user");                          
                            String utente = uu.getNodeValue();
                            Update upda = new Update(utente);
                            inte.addUpdate(upda);
                            // ora o ha figli choice
                            NodeList l123 = n65.getChildNodes();
                            for (int rrr = 0; rrr < l123.getLength(); rrr++) {
                                Node n656 = l123.item(rrr);
                                if (n656.getNodeName().equals("choices")) {
                                    NamedNodeMap l89 = n656.getAttributes();
                                    Node ante = l89.getNamedItem("ant");
                                    Node poste = l89.getNamedItem("post");
                                    String anteTask = ante != null ? ante.getNodeValue() : null;
                                    String posteTask = poste != null ? poste.getNodeValue() : null;
                                    Choice cho = new Choice(anteTask,posteTask);
                                    upda.addChoice(cho);
                                    if (debug) {
                                        System.out.println("task UPDATE: " + anteTask + " "+ posteTask);
                                    }
                                }
                            }
                            if (debug) {
                                System.out.println("UTENTE UPDATE: " + utente);
                            }


                         //   inte.addUpdate(strippa(xmlToString(n65)));
                            if (debug) {
                                System.out.print("imaptto INIZIO\n" + strippa(xmlToString(n65)) + "\nimpatto FINE\n");
                            }
                        }
                    }
                    if (n.getNodeName().equals("interval")) {
                        // fine procedura dopo 1 intervallo
                        return;
                    }
                }
            }
        }
        if (debug) {
            System.out.println();
        }
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ figli.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            parseIntervals(n67, inter, indent + "    ");
        }
    }

    private static String strippa(String s) {
        return s.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").replace('\n', ' ') + "\n";
    }

    public static void parseIntervalsOLD(Node n, ArrayList<Interval> inter, String indent) {
        // recupera sia gli intervalli che eventualmente
        // in quali casi  ci siano utenti con schedule da modificare

        String s = "" + n.getClass();

        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");

        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa && debug) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap l5 = n.getAttributes();
        if (l5 == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {

            if (stampa) {
                System.out.println(indent + l5.getLength() + " attributes ");
            }
            for (int k = 0; k < l5.getLength(); k++) {
                Node n8 = l5.item(k);
                // System.out.print(" " + attributo.getNodeName() + " ='" + attributo.getNodeValue() + "'");
                if (n8.getNodeName().equals("task") && n.getNodeName().equals("action")) {
                    taskName = n8.getNodeValue();
                    if (debug) {
                        System.out.print("carico " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                    }
                }
                if (n.getNodeName().equals("interval")) {// provare!!!! || n.getNodeName().equals("intervals")) {
                    n8 = l5.getNamedItem("start");

                    String s1 = n8.getNodeValue();
                    s1 = s1.replaceAll("[(),]", " ");
                    String[] pars = s1.split(" ");
                    // causa reasoner di Gianluca, non si accettano 0
                    // per inizio, quindi sottraggo 1
                    int min = Integer.parseInt(pars[1]) - 1;
                    int max = Integer.parseInt(pars[2]);
                    Interval inte = new Interval(taskName, min, max);
                    inter.add(inte);
                    if (debug) {
                        System.out.print("intervallo " + s1);
                    }
                    n8 = l5.getNamedItem("users");
                    if (n8 != null) {  // recupera utenti
                        s1 = n8.getNodeValue();
                        if (!s1.equals("")) {
                            if (debug) {
                                System.out.print("utenti " + s1);
                            }
                            pars = s1.split(",");
                            for (int i = 0; i < pars.length; i++) {
                                inte.addUser(pars[i]);
                            }
                        }
                    }
                    if (n.getNodeName().equals("interval")) {
                        // fine procedura dopo 1 intervallo
                        return;
                    }
                }
            }
        }
        if (debug) {
            System.out.println();
        }
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ figli.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            parseIntervalsOLD(n67, inter, indent + "    ");
        }
    }

    public static TaskGroup fillReply(InputStream is) {
        Document doc = null;
     //   is = null;
        try {
            if (is == null) { //per prova leggi da file
                doc = MyDOMParserBean.getDocument("/home/marino/torta/samples/resp1.xml");
                //doc = MyDOMParserBean.getDocument("/home/marino/torta/Resp3");
            } else {
                doc = MyDOMParserBean.getDocumentFromStream(is);
            }
            //  doc = MyDOMParserBean.getDocument("/home/marino/ESEMPIO.gpx");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("in fillreply, document =\n" + xmlToString(doc) + "\n\nFINE DOC INPUT\n");
        Element ele = doc.getDocumentElement();//.getElementById("ACTIVE LOG 004");
        ArrayList<Interval> inte = new ArrayList();
        parseIntervals(ele, inte, "");
        TaskGroup ret = new TaskGroup();
        ret.setTaskSchedule(inte);
        parseNet(ele, ret, "", "");
        parsePrecs(ele, ret, "", "");
        return ret;
    }

    static private void printUnNodoOLD(Node n, String indent) {
        String s = "" + n.getClass();
        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");

        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap l5 = n.getAttributes();
        if (l5 == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {

            //  if (stampa)  System.out.println(indent + attributi.getLength() + " attributes ");
            for (int k = 0; k < l5.getLength(); k++) {
                Node n8 = l5.item(k);
                System.out.print(" ATTR " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
            }
        }
        System.out.println();
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ figli.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            printUnNodoOLD(n67, indent + "    ");
        }
        //   Punto p = new Punto(lat, lon, ele, ora);
        //  Punto.add(p);
    }

    public static void parsePrecs(Node n, TaskGroup iTasks, String prima, String indent) {
        String s = "" + n.getClass();

        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");

        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa && debug) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap l5 = n.getAttributes();
        if (l5 == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {

            //  if (stampa)  System.out.println(indent + attributi.getLength() + " attributes ");
            for (int k = 0; k < l5.getLength(); k++) {
                Node n8 = l5.item(k);
                // System.out.print(" " + attributo.getNodeName() + " ='" + attributo.getNodeValue() + "'");
                if (n.getNodeName().equals("prec")) {
                    prima = n8.getNodeValue();
                    if (debug) {
                        System.out.print("caricoprec " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                    }
                }
                if (n.getNodeName().equals("succ")) {
                    String dopo = n8.getNodeValue();
                    Task who = iTasks.getI(dopo);
                    if (who == null) {
                        if (debug) {
                            System.out.print("tasknet, not found: " + dopo);
                        }
                    } else {
                        //    iTasks.getI(prima).addAfter(dopo); noppure:
                        iTasks.getI(dopo).addBefore(prima);
                        if (debug) {
                            System.out.print("caricosucc " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                        }
                    }
                }
            }
        }
        if (debug) {
            System.out.println();
        }
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ figli.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            parsePrecs(n67, iTasks, prima, indent + "    ");
        }
    }

    public static void parseNet(Node n, TaskGroup iTasks, String prima, String indent) {
        String s = "" + n.getClass();

        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");

        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa && debug) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap attributi = n.getAttributes();
        if (attributi == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {
            String name = null;
            String durata = null;
            String start = null;
            String end = null;
            //  if (stampa)  System.out.println(indent + attributi.getLength() + " attributes ");
            for (int k = 0; k < attributi.getLength(); k++) {
                Node attributo = attributi.item(k);
                // System.out.print(" " + attributo.getNodeName() + " ='" + attributo.getNodeValue() + "'");
                if (attributo.getNodeName().equals("name") && n.getNodeName().equals("task")) {
                    name = attributo.getNodeValue();
                    if (debug) {
                        System.out.print("tasco " + attributo.getNodeName() + " ='" + attributo.getNodeValue() + "'");
                    }
                }
                if (attributo.getNodeName().equals("dur") && n.getNodeName().equals("task")) {
                    durata = attributo.getNodeValue();
                    if (debug) {
                        System.out.print("dur " + attributo.getNodeName() + " ='" + attributo.getNodeValue() + "'");
                    }
                }
                if (attributo.getNodeName().equals("start") && n.getNodeName().equals("task")) {
                    start = attributo.getNodeValue();
                    if (debug) {
                        System.out.print("start " + attributo.getNodeName() + " ='" + attributo.getNodeValue() + "'");
                    }
                }
                if (attributo.getNodeName().equals("end") && n.getNodeName().equals("task")) {
                    end = attributo.getNodeValue();
                    if (debug) {
                        System.out.print("end " + attributo.getNodeName() + " ='" + attributo.getNodeValue() + "'");
                    }
                }
            }
            if (name != null) {
                start = start.replaceAll("[(),]", " ");
                String[] pars = start.split(" ");
                // causa reasoner di Gianluca, non si accettano 0
                // per inizio, quindi sottraggo 1
                int par = Integer.parseInt(pars[1]) - 1;
                end = end.replaceAll("[(),]", " ");
                pars = end.split(" ");
                int fin = Integer.parseInt(pars[2]);
                int du = Integer.parseInt(durata);
                Task tasn = new Task(name, par, fin, du);
                iTasks.addI(tasn);
            }
        }
        if (debug) {
            System.out.println();
        }
        NodeList figli = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ figli.getLength() + " children ");
        for (int w = 0; w < figli.getLength(); w++) {
            Node n67 = figli.item(w);
            parseNet(n67, iTasks, "", indent + "    ");
        }
    }

    private static void parseIntervalsOLDOLD(Node n, ArrayList<Interval> inter, String indent) {
        String s = "" + n.getClass();

        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");

        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa && debug) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap l5 = n.getAttributes();
        if (l5 == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {

            //  if (stampa)  System.out.println(indent + attributi.getLength() + " attributes ");
            for (int k = 0; k < l5.getLength(); k++) {
                Node n8 = l5.item(k);
                // System.out.print(" " + attributo.getNodeName() + " ='" + attributo.getNodeValue() + "'");
                if (n8.getNodeName().equals("task") && n.getNodeName().equals("action")) {
                    taskName = n8.getNodeValue();
                    if (debug) {
                        System.out.print("carico " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                    }
                }
                if (n.getNodeName().equals("interval") || n.getNodeName().equals("intervals")) {
                    if (n8.getNodeName().equals("start")) {
                        String s1 = n8.getNodeValue();
                        s1 = s1.replaceAll("[(),]", " ");
                        String[] pars = s1.split(" ");
                        int min = Integer.parseInt(pars[1]);
                        int max = Integer.parseInt(pars[2]);
                        Interval inte = new Interval(taskName, min, max);
                        inter.add(inte);
                        if (debug) {
                            System.out.print("intervallo " + s1);
                        }
                    }
                }
            }
        }
        if (debug) {
            System.out.println();
        }
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ figli.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            parseIntervalsOLD(n67, inter, indent + "    ");
        }
    }

    private static String xmlToString(Node node) {
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
