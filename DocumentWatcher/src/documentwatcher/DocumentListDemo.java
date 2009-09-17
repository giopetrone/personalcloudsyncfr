/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package documentwatcher;

import appsusersevents.client.EventDescription;
import appsusersevents.client.SingleUser;
import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.ServiceException;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.activation.MimetypesFileTypeMap;

/**
 *
 * @author marino
 */
public class DocumentListDemo {

    URL documentListFeedUrl = null;
    DocsService service = null;
    HashMap<String, DateTime> documentVersions = new HashMap();
    private boolean printed = false;
    boolean one = false;
    //anna gio
//    static String docMakerLogin = "annamaria.goy@gmail.com";  // fino a che  ???  auth funziona
//    static String docMakerPasswd = "tex_willer";
    static String docMakerLogin = "annamaria.goy@gmail.com";  // fino a che  ???  auth funziona
    static String docMakerPasswd = "tex_willer";
    //fine Anna gio

    public DocumentListDemo(SingleUser user) {
        service = new DocsService("Document List");
        try {
            service.setUserCredentials(user.getMailAddress(), user.getPwd());
            documentListFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public DocumentListDemo(String docList) {
        XStream xstream = new XStream();
        // DA FARE IN SEGUITO : se si vuole avere uno snapshot della situazione dei docs di un utente se, docWatcher cade.
        //  documentVersions = (HashMap<String, DateTime>) xstream.fromXML(docList);
        service = new DocsService("Document List Demo");
        try {
            //   service.setUserCredentials("sgnmrn@gmail.com", "micio11");
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            documentListFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full");
        //   doStuffSingle();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("CIAO");
    }

    public DocumentListDemo(DocsService service) {
        try {
            documentListFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.service = service;
    }

    public static void main(String[] args) {
        DocsService service = new DocsService("Document List Demo");
        try {
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            new DocumentListDemo(service).doStuff();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("CIAO");
    }

    public ArrayList<EventDescription> doStuffSingle(SingleUser user) {
        try {
            service.setUserCredentials(user.getMailAddress(), user.getPwd());
            DocumentListFeed feed = service.getFeed(documentListFeedUrl, DocumentListFeed.class);
            return generateChangeEvents(feed);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    void doStuff() {
        try {

            for (int i = 0; i < 10; i++) {
                DocumentListFeed feed = service.getFeed(documentListFeedUrl, DocumentListFeed.class);


                generateChangeEvents(feed);
                Thread.currentThread().sleep(1000 * 10);
            }
        //  showAllDocs();
        //   uploadFile("/home/marino/prova.txt");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showAllDocs() throws IOException, ServiceException {

        for (int i = 0; i < 10; i++) {
            DocumentListFeed feed = service.getFeed(documentListFeedUrl, DocumentListFeed.class);


            for (DocumentListEntry entry : feed.getEntries()) {
                printDocumentEntry(entry);
            }
            try {
                Thread.currentThread().sleep(1000 * 121);
            } catch (Exception e) {
            }
        }
    }

    public void printDocumentEntry(DocumentListEntry doc) {
        String shortId = doc.getId().substring(doc.getId().lastIndexOf('/') + 1);
        DateTime date = doc.getPublished();
        DateTime date1 = doc.getUpdated();
        String versionId = doc.getVersionId();
        //   System.out.println(" -- Document(" + shortId + "/" + doc.getTitle().getPlainText() + ")" + date.toString() +" ver. "+ versionId);
        System.out.println(" " + doc.getTitle().getPlainText() + "C: " + date.toString() + " U: " + date1.toString());
    }

    void printMap() {
        XStream xstream = new XStream();
        System.out.println(xstream.toXML(documentVersions));
    }

    public void uploadFile(String filePath) throws IOException,
            ServiceException {
        DocumentEntry newDocument = new DocumentEntry();
        File documentFile = new File(filePath);
        newDocument.setFile(documentFile, new MimetypesFileTypeMap().getContentType(documentFile));
        // Set the title for the new document. For this example we just use the
        // filename of the uploaded file.
        newDocument.setTitle(new PlainTextConstruct(documentFile.getName()));
        DocumentListEntry uploaded = service.insert(documentListFeedUrl,
                newDocument);
        System.out.println("Docunent uploaded:");
        printDocumentEntry(uploaded);
    }

    private ArrayList<EventDescription> generateChangeEvents(DocumentListFeed feed) {
        ArrayList<EventDescription> ret = new ArrayList();
        HashSet<String> currDocList = new HashSet();
        for (DocumentListEntry doc : feed.getEntries()) {
            String tit = doc.getTitle().getPlainText();
            currDocList.add(tit);
            DateTime prevDate = documentVersions.get(tit);
            if (prevDate == null) { // new document
                prevDate = doc.getUpdated();
                if (prevDate == null) {
                    prevDate = doc.getPublished();
                }
                documentVersions.put(tit, prevDate);
                  ret.add(generateEvent(tit, "DocCreated", prevDate));
                continue;
            }
            DateTime currDate = doc.getUpdated();
            if (currDate == null) {
                currDate = doc.getPublished();
            }
            if (!currDate.equals(prevDate)) {
                documentVersions.put(tit, currDate);
                //     ret.add(generateEvent(tit, "DocUpdated", currDate));
                //23-6-09 getContributors restituisce lista vuota, sara' baco di Google ????
                List<Person> contributors = doc.getContributors();
                   System.out.println("TUTTI ! di doc = " + doc.getTitle().getPlainText());
                       System.out.println("size di contributors  = " + contributors.size());
                if (contributors.size() == 0) {
                    System.out.println("AUTHORS vuoto !!!!! di doc = " + doc.getTitle().getPlainText());
                    ret.add(generateEvent(tit, "DocUpdated", prevDate, "gio.petrone@gmail.com"));  // TEMP per baco contributors
                } else {
                for (Person pers : contributors) {
                    String mail = pers.getEmail();
                    ret.add(generateEvent(tit, "DocUpdated", prevDate, mail));
                }
                }
            }
        }
        // now look if any document has benn removed
        Set<String> prevDocList = documentVersions.keySet();
        // questo peerche' dice strano errore concurrent access!!!
        String[] y = prevDocList.toArray(new String[0]);
        //      prevDocList.removeAll(currDocList); non si puo' fare
        //  distrugge la mia variabile d'istanza !!!
        for (int i = 0; i < y.length; i++) {
            String tit = y[i];
            if (!currDocList.contains(tit)) {
                documentVersions.remove(tit);
                ret.add(generateEvent(tit, "DocRemoved", null));
            }
        }
        if (printed) {
            printed = false;
        } else {
            System.out.println("No events");
        }
        if (!one) {
            one = true;
            printMap();
        }
        return ret;
    }

    /*generate event for document currently:
     *
     * application =  "GoogleDocs"
     * eventName == "created" | "modified" | "removed"
     * param1 == <nome documento>
     * param2 == <data>
     *
     */
    private EventDescription generateEvent(String docName, String op, DateTime d) {
        printed = true;
        System.out.println("Document \t " + docName + "\t\thas been \t\t" + op);
        EventDescription des = new EventDescription("*");
        //   des.setDestinatario(docMakerLogin);  // NON CI SI NOTIFICA ... vedi altra generateEvent
        des.setApplication("GoogleDocs");
        des.setEventName(op);
        des.setParameter("docName", docName);
        des.setParameter("date", d == null ? "  ?  " : d.toUiString());
        String link = getEditLink(docName);
        des.setParameter("docLink", link == null ? "  ?  " : link);
        //   ArrayList par = new ArrayList();
        //   par.add(docName);
        //   par.add(d == null ? "  ?  " : d.toUiString());
        //  des.setParameters(par);
        return des;
    }

    private EventDescription generateEvent(String docName, String op, DateTime d, String destinatario) {
        printed = true;
        System.out.println("Document \t " + docName + "\t\thas been \t\t" + op);
        EventDescription des = new EventDescription("*");
        des.setUser(docMakerLogin);  // utente autenticato nel desktop
        des.setDestinatario(destinatario);
        des.setApplication("GoogleDocs");
        des.setEventName(op);
        des.setParameter("docName", docName);
        des.setParameter("date", d == null ? "  ?  " : d.toUiString());
        String link = getEditLink(docName);
        des.setParameter("docLink", link == null ? "  ?  " : link);
        //   ArrayList par = new ArrayList();
        //   par.add(docName);
        //   par.add(d == null ? "  ?  " : d.toUiString());
        //  des.setParameters(par);
        return des;
    }

    public String getEditLink(String docName) {
        String ret = null;
        try {
            DocumentQuery query = new DocumentQuery(documentListFeedUrl);
            query.setTitleQuery(docName);
            query.setTitleExact(true);
            query.setMaxResults(10);
            DocumentListFeed feed = service.getFeed(query, DocumentListFeed.class);
            for (DocumentListEntry doc : feed.getEntries()) {
                ret = doc.getDocumentLink().getHref();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //     System.err.println("Link= " + ret);
        return ret;
    }
}