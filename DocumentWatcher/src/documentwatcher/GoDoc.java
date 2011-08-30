/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package documentwatcher;

//import com.google.appengine.repackaged.com.google.protobuf.ServiceException;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.FolderEntry;

import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.ServiceException;
import event.AtomEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import maillib.NewGmail;
import pubsub.Discovery;
import pubsub.FeedUtil;
//import maillib.SendMailCl;
//import event.AtomEvent;
//import jav.FeedUtil;
//import jav.SaveServlet;
//import pubsub.Discovery;

/**
 *
 * @author marino
 */
public class GoDoc {

    private static final String APPLICATION_NAME = "JavaGDataClientSampleAppV3.0";
    private DocumentList documentList = null;

    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */
    private static URL DocumentListFeedUrl = null;
    private static String DownloadFeed = "http://docs.google.com/feeds/download/";
    DocsService service = null;
    static HashMap<String, DateTime> documentVersions = new HashMap();
  //  private boolean printed = false;
    boolean one = false;
    static String docMakerLogin = "icemgr09@gmail.com";  // fino a che  ???  auth funziona
    static String docMakerPasswd = "sync09fr";

    static {
        try {
            DocumentListFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full/");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //fine Anna gio
    public GoDoc(DocsService service) {
        this.service = service;
    }

      public static void main(String[] args) throws MalformedURLException, IOException, ServiceException {

        try {System.out.println(prendi(docMakerLogin , docMakerPasswd ));
          } catch (Exception e){}
    }

    public static void mainOLD(String[] args) throws MalformedURLException, IOException, ServiceException {

        try {

            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
              DocumentListFeed feed = service.getFeed(DocumentListFeedUrl, DocumentListFeed.class);
            //  System.out.println("FEED :"+feed.toString());
            DocumentListEntry doc = new GoDoc(service).findEntry("fabrizio_torretta.txt");
            if (doc != null) {
                String resourceId = doc.getResourceId();
                String docType = resourceId.substring(0, resourceId.lastIndexOf(':'));

                String docId = resourceId.substring(resourceId.lastIndexOf(':') + 1);

                URL exportUrl = new URL(DownloadFeed + docType
                        + "s/Export?docID=" + docId + "&exportFormat=" + "html");

                MediaContent mc = new MediaContent();
                mc.setUri(exportUrl.toString());

                MediaSource ms = service.getMedia(mc);
                String s = "";
                byte[] b = new byte[1024];
                InputStream inStream = ms.getInputStream();
                int c;
                int d;
                int l = 0;
                String r = "";
                while ((c = inStream.read(b)) != -1) {
                    s += new String(b, 0, c);
                }
                System.out.println("PRIMA " + s);
                s = new GoDoc(service).findFiles(s);
                System.out.println("DOPO: " + s);

                if (s.contains("ciao.txt")) {
                    s = s.replaceAll("ciao.txt", "blabla");
                } else {
                    s += "\nciao.txt/All";
                }

                service.getRequestFactory().setHeader("If-Match", "*");
                doc.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));
                doc.updateMedia(false);
            } else {
                String s = "ciao.txt/All";
                doc = new GoDoc(service).uploadFile(s, "fabrizio_torretta.txt");
            }

        } catch (Exception e) {
            System.out.println("ERRORE: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void printDocuments(DocumentListFeed feed) throws IOException, ServiceException {
        DocsService service2 = new DocsService("Document List Demo");
        service2.setUserCredentials(docMakerLogin, docMakerPasswd);
        String documentName = "ilarialbano.txt";
        try {
            DocumentListEntry documentEntry = findEntry(documentName);

        } catch (Exception ex) {
            Logger.getLogger(GoDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static DocumentListEntry createFolder(String title) throws IOException, ServiceException {

        DocsService service = new DocsService("Document List Demo");
        //service.setUserCredentials(docMakerLogin, docMakerPasswd);
        DocumentListEntry newEntry = new FolderEntry();
        newEntry.setTitle(new PlainTextConstruct(title));
        URL Url = new URL("http://docs.google.com/feeds/documents/private/full");
        return service.insert(Url, newEntry);
    }

    public static String prendi(String login, String pwd) {
        DocsService service = new DocsService("Document List Demo");
        try {

            service.setUserCredentials(login, pwd);
            System.out.println("GoDoc.prendi " + login + " " + pwd);
            return new GoDoc(service).showAllDocs();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    private List<String> split(String input) {
        // spezza i login eliminando i blank, e le virgole
        ArrayList<String> ret = new ArrayList();
        if (input != null) {
            String temp = input.replaceAll(" ", "");
            for (String s :  temp.split(",")) {
                if (!s.equals("")) {
                    ret.add(s);
                }
            }
        }
        return ret;
    }

    private AtomEvent generateAccessEvent(String login, String documentName, String permission, String who) {
        AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
        event.setParameter("File", documentName);
        event.setParameter("Permission", permission);
        event.setParameter("Who", who);
        return event;
    }

    private boolean userIdOk(String id) {
        int count0 = id.indexOf("@");
        int count1 = id.lastIndexOf("@");
        return count0 > 0 && count0 == count1;
    }

    public void saveNewDiagram(String login, String documentName, String s, String users, String writers, String pwd, String assignees, String hub) {
        try {
            DocumentListEntry documentEntry = findEntry(documentName);
            System.out.println("DOCUMENTO NUOVO");
            List<AtomEvent> listaeventi = new ArrayList();
            List<AtomEvent> listasave = new ArrayList();
            documentEntry = uploadFile(s, documentName);
            AtomEvent eventSave = new AtomEvent(login, "TaskManager", "Save New Diagram");
            String link = documentEntry.getDocumentLink().getHref();
            eventSave.setParameter("File", documentName);
            eventSave.setParameter("Link", link);
            listasave.add(eventSave);
            FeedUtil.addEntries("", documentName, listasave, hub);
            List<String> destinatari = new LinkedList();
            //if(writers == null) writers = "";
            if (users == null && writers == null && assignees == null) {
                System.out.println("NESSEUN UTENTE IN SHARING IN SAVE NEW DIAGRAM");
            } else {
                List<String> tempwriters = split(writers);
                List<String> tempusers = split(users);
                List<String> tempassignees = split(assignees);
                // aggiungi permessi al doc:
                // users normali hanno READ
                // writers ed assignees hanno WRITE
                for (String user : tempusers) {
                    if (!tempassignees.contains(user)) {
                        System.out.println("%%% ASSEGNO Reader a %%%%%%%%% " + user);
                        addRole(documentEntry, user, "reader");
                        AtomEvent event = generateAccessEvent(login, documentName, "Read", user);
                        listaeventi.add(event);
                        destinatari.add(user);
                    }
                }
                for (String writer : tempwriters) {
                    if (!tempusers.contains(writer)) {
                        //   System.out.println("#####ASSEGNO Writer a #### " + writer);
                        addRole(documentEntry, writer, "writer");
                        AtomEvent event = generateAccessEvent(login, documentName, "Write", writer);
                        listaeventi.add(event);
                        destinatari.add(writer);
                    }
                }
                for (String assignee : tempassignees) {
                    if (userIdOk(assignee)) {
                        try {
                            //     System.out.println("Assignees " + assignee);
                            addRole(documentEntry, assignee,"writer");
                            AtomEvent event = generateAccessEvent(login, documentName, "Write", assignee);
                            listaeventi.add(event);
                            destinatari.add(assignee);
                        } catch (Exception ex) {
                            if (ex.getMessage().equalsIgnoreCase("This user already has access to the document.")) {
                                System.out.println("DENTRO CATCH X  UTENTE: " + assignee);
                            } else {
                                ex.printStackTrace();
                                System.out.println("Dentro CATCH X utente " + assignee + " " + ex.getMessage());
                            }
                        }
                    }
                }
                destinatari.add(login);
                String dest = destinatari.toString();
                int length = dest.length();
                String destfinal = dest.substring(1, length - 1);
                if (destinatari.size() != 1) {
                    sendMail(destfinal, "icemgr09@gmail.com", "sync09fr", documentName);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("IN SAVE NEW DIAGRAM " + ex.getMessage());
        }
    }

    public String uploadDiagram(String login, String documentName, String users, String writers, String content, String assignees) throws Exception {
        try {
            //  DocsService service = new DocsService("Document List Demo");
            System.out.println("godoc.uploadDiagram, DocumentEntry NOT null");
            AtomEvent eventUpdate = new AtomEvent(login, "TaskManager", "Update Diagram");
            eventUpdate.setParameter("File", documentName);
            List<AtomEvent> listaeventi = new ArrayList();
            listaeventi.add(eventUpdate);
            List<String> oldReaders = new ArrayList();
            List<String> oldWriters = new ArrayList();
               DocumentListEntry documentEntry = findEntry(documentName);
            AclFeed aclFeed = getDocumentList().getAclFeed(documentEntry.getResourceId());
            for (AclEntry entry : aclFeed.getEntries()) {
                if (entry.getRole().getValue().equals("reader")) {
                    oldReaders.add(entry.getScope().getValue());
                } else if (entry.getRole().getValue().equals("writer")) {
                    oldWriters.add(entry.getScope().getValue());
                }
            }
            if (!oldWriters.contains(login)) {
                // owner e' SEMPRE writer se non c'era aggiungilo
                writers = writers + "," + login;
            }
            List<String> tempwriters = split(writers);
            List<String> tempassignees = split(assignees);
            // ogni user avra' permesso di READ: se era WRITE lo si demuove
            // Se non c'era gli si assegna READ
            // se invece e' un assignee diventera' WRITE
            // in termini googledocs
            // user= READER
            // writer = WRITER
            // assignee = WRITER
            if (users != null) {
                List<String> tempusers = split(users);
                for (String user : tempusers) {
                    if (!tempassignees.contains(user)) {
                        boolean found = false;
                        for (String oldWriter : oldWriters) {
                            if (user.equals(oldWriter)) {
                                //    System.out.println("%%%%% CAMBIO PERMESSI DI %%%% " + oldWriters.get(z));
                                updateRole(documentEntry, aclFeed, oldWriter);
                                AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                event.setParameter("File", documentName);
                                event.setParameter("Change Permission", "From Write To Read");
                                event.setParameter("Who", oldWriter);
                                listaeventi.add(event);
                                found = true;
                            }
                        }
                        if (!found && !oldReaders.contains(user)) {
                          //  System.out.println("%%%%%%%%% ASSEGNO READER a %%%%% " + user);
                            addRole(documentEntry, user,"reader");
                            AtomEvent event = generateAccessEvent(login, documentName, "Read", user);
                            listaeventi.add(event);
                        }
                    }
                }
                // dovrebbe essere inutile!!
                for (String writer : tempwriters) {
                    if (!tempusers.contains(writer) && !oldWriters.contains(writer)) {
                        System.out.println("%%%%%% Aggiungo a Writer%%%%% " + writer);
                        addRole(documentEntry, writer,"writer");
                        AtomEvent event = generateAccessEvent(login, documentName, "Write", writer);
                        listaeventi.add(event);
                    }
                }
            } else {
                for (String writer : tempwriters) {
                    if (!oldWriters.contains(writer)) {
                        //  System.out.println("%%%%%%% Aggiungo a Writer%%%%%% " + tempwriter[j]);
                        addRole(documentEntry, writer,"writer");
                        AtomEvent event = generateAccessEvent(login, documentName, "Write", writer);
                        listaeventi.add(event);
                    }
                }
            }
            // gli assignees hanno permesso di write!
            for (String assignee : split(assignees)) {
                if (userIdOk(assignee)) {
                    addRole(documentEntry, assignee,"writer");
                    AtomEvent event = generateAccessEvent(login, documentName, "Write", assignee);
                    listaeventi.add(event);
                }
            }

            FeedUtil.addEntries(documentEntry.getDocumentLink().getHref(), documentName, listaeventi, FeedUtil.isLocalMode() ? "local" : "remote");
            documentEntry.getService().getRequestFactory().setHeader("If-Match", "*");
            documentEntry.setMediaSource(new MediaByteArraySource(content.getBytes(), "text/plain"));
            documentEntry.updateMedia(false);
            return "notnew";
//  non BUTTARE: return documentEntry.getDocumentLink().getHref();

        } catch (Exception ex) {
           // ex.printStackTrace();
            if (ex.getMessage().equalsIgnoreCase("This user already has access to the document.")) {
   //  System.out.println("DENTRO If updateRole diagram "+ex.getMessage());
                return "notnew";
            } else {
                ex.printStackTrace();
                return ex.getMessage();
            }
        }
    }

    public String saveDoc(String login, String documentName, String s, String users, String writers, String pwd, String assignees, String hub) {

        try {
            //System.out.println ("saveDiagram \n" + s );
            //         System.out.println("-----");
            DocumentListEntry documentEntry = findEntry(documentName);
            if (documentEntry == null) {
                saveNewDiagram(login, documentName, s, users, writers, pwd, assignees, hub);
                return "new";
            } else {
                String ret = uploadDiagram(login, documentName, users, writers, s, assignees);
                return ret;
            }
        } catch (Exception ex) {
            System.out.println("Dentro SAVEDOC " + ex.getMessage());
            ex.printStackTrace();
            if (ex.getMessage().equalsIgnoreCase("Could not convert document.")) {
                return "notnew";
            } else {
                return ex.toString();
            }
        }
    }

    public DocumentListEntry uploadFile(String content, String title)
            throws IOException, ServiceException {
        System.out.println("in uploaffile : " + "/tmp/" + title);
        File file = new File("/tmp/" + title);
        FileOutputStream of = new FileOutputStream(file);
        of.write(content.getBytes(), 0, content.length());
        of.close();
        DocumentListEntry ret = null;
        try {
            ret = getDocumentList().uploadFile("/tmp/" + title, title);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        file.delete();
        return ret;
    }

    public void uploadPermissions(String documentName, String flowName, String notification) throws Exception {
        System.out.println("Doc: " + documentName);
        System.out.println("FlowName: " + flowName);
        System.out.println("Notification " + notification);
        DocumentListEntry documentEntry = findEntry(documentName);
        if (documentEntry == null) {
            String s = flowName + "/" + notification;
            documentEntry = uploadFile(s, documentName);
        } else {
            String resourceId = documentEntry.getResourceId();
            String docType = resourceId.substring(0, resourceId.lastIndexOf(':'));
            String docId = resourceId.substring(resourceId.lastIndexOf(':') + 1);
            URL exportUrl = new URL(DownloadFeed + docType
                    + "s/Export?docID=" + docId + "&exportFormat=" + "html");
            MediaContent mc = new MediaContent();
            mc.setUri(exportUrl.toString());
            MediaSource ms = service.getMedia(mc);
            String s = "";
            byte[] b = new byte[1024];
            InputStream inStream = ms.getInputStream();
            int c;
            String oldsetting = "";
            String oldtext = "";
            String newtext = "";
            while ((c = inStream.read(b)) != -1) {
                //System.out.println("LINE: "+s);
                s += new String(b, 0, c);
                if (s.contains(flowName)) {
                    oldsetting = s;
                }
                oldtext += s + "\r\n";
            }
            if (!oldsetting.equals("")) {
                newtext = oldtext.replaceAll(oldsetting, flowName + "/" + notification);

            }
            documentEntry = uploadFile(newtext, documentName);
        }
    }

    private AclEntry addRole(DocumentListEntry documentEntry, String who, String type) throws Exception {
        AclRole role = new AclRole(type); // read o write
        AclScope scope = new AclScope(AclScope.Type.USER, who);
        return getDocumentList().addAclRole(role, scope, documentEntry.getResourceId());
    }

    private void updateRole(DocumentListEntry documentEntry, AclFeed aclFeed, String who) throws IOException, MalformedURLException, ServiceException {
        for (AclEntry entry : aclFeed.getEntries()) {
            //  System.err.println(entry.getRole().getValue());
            if (entry.getScope().getValue().equals(who)) {
                entry.setRole(new AclRole("reader"));
                entry.update();
            }
        }
    }

    public List<String> loadDoc(String valorefile, boolean refresh, String owner) {
        try {

            DocumentListEntry documentEntry = findEntry(valorefile);
            //   System.out.println(documentEntry.toString());
            List<String> list = new LinkedList();
            if (documentEntry == null) {
                return list; //document not found
            }

            DateTime last = documentEntry.getUpdated();
            DateTime curr = documentVersions.get(valorefile);
            if (refresh) {
                if (curr == null || curr.getValue() == last.getValue()) {
                    return list;  // no version or no new version
                }
            }
            documentVersions.put(valorefile, last); // updateRole current version
            String resourceId = documentEntry.getResourceId();
            // System.out.println("resourceId= "+ resourceId + " "+ getDocumentList().getDocsListEntry(resourceId));
            MediaContent mc = (MediaContent) getDocumentList().getDocsListEntry(resourceId).getContent();

            String fileExtension = mc.getMimeType().getSubType();
            URL exportUrl = new URL(mc.getUri());

            // PDF file cannot be exported in different formats.
            String content = getDocumentList().downloadFile(exportUrl);

            //     System.out.println("3. stringa" + s);
            String s = findBody(content);
            //System.out.println("1. stringa" + s);
            String error = "Non hai i permessi";
            List<String> people = new ArrayList();
            List<String> collaborators = new ArrayList();
            List<String> readers = new ArrayList();
            System.out.println("Prima ciclo reader");
            String docowner = "";
            //  OLD  AclFeed aclFeed = service.getFeed(new URL(documentEntry.getAclFeedLink().getHref()), AclFeed.class);
            AclFeed aclFeed = getDocumentList().getAclFeed(resourceId);
            int size = aclFeed.getEntries().size();
            System.out.println("SIZE " + aclFeed.getEntries().size());
            for (AclEntry entry : aclFeed.getEntries()) {

                System.out.println("RUOLO " + entry.getRole().getValue());
                if (entry.getRole().getValue().equals("reader")) {
                    System.out.println("Trovato reader");
                    readers.add(entry.getScope().getValue());
                } else if (entry.getRole().getValue().equals("writer")) {
                    System.out.println("Trovato Writer");
                    collaborators.add(entry.getScope().getValue());
                } else if (entry.getRole().getValue().equals("owner")) {
                    docowner = entry.getScope().getValue();
                }
                people.add(entry.getScope().getValue());
            }
            String users = readers.toString();
            String nousers = "";
            if (users.equals("[]")) {
                users = "";
            } else {
                users = users.substring(1, users.length() - 1);
            }
            System.out.println("USERS: " + users);
            String writers = collaborators.toString();
            if (writers.equals("[]")) {
                writers = "";
            } else {
                writers = writers.substring(1, writers.length() - 1);
            }
            if (!writers.contains(owner)) // owner e' sempre writer
            {
                writers = writers + "," + owner;
            }
            System.out.println("Writers " + writers);
            System.out.println("People: " + people.toString());
            System.out.println("Owner: " + owner);
            System.out.println("docOwner: " + docowner);
            people.add(owner);
            readers.add(owner);
            if (size == 1 && !owner.equals(docowner)) {
                System.out.println("SIZE 1 e docowner");


                list.add(s);
                list.add(users);
                list.add(writers);
                // SBALLATO RIFARE!!!!!   System.out.println("NOSAVE 920");        list.add("nosave");
                return list;
            } else if (people.toString().contains(owner) == true) {
                list.add(s);
                list.add(users);
                list.add(writers);
                return list;
            } else {
                list.add(error);
                list.add(nousers);
                list.add(nousers);
                return list;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // System.out.println("In Load Doc " + ex.toString());
            List<String> list = new LinkedList();
            list.add("errore");
            list.add("errore");
            list.add("errore");
            return list;
        }
    }

    private String findBody(String r) {

        r = r.replaceAll("&gt;", ">");
        r = r.replaceAll("&lt;", "<");
        r = r.replaceAll("&quot;", "\"");

        int ind1 = r.indexOf("connections");
        int ind2 = r.indexOf("</span></p>");
        //System.out.println("findbody, tutto =\n"+r);
        // int ind2 = r.indexOf("<br></body>");
        //     int ind2 = r.indexOf("</body>");
        r = r.substring(ind1 - 2, ind2);
        //    System.out.println("findBody, doc: \n" + r);
        //   System.out.println("---------------");

        return r;
    }

    private String findFiles(String s) {

        int ind1 = s.lastIndexOf(" ");
        int ind2 = s.indexOf("<br></body>");

        s = s.substring(ind1, ind2);
        if (s.contains("\n")) {
            s = s.replaceAll("\n", "");
        }
        if (s.contains("<br>")) {
            s = s.replaceAll("<br>", "");
        }
        return s;
    }

    public String showAllDocs() throws Exception {

        DocumentListFeed feed = getDocumentList().getDocsListFeed("all");
        String ret = "";
        // for (int i = 0; i < 10; i++) {
        // DocumentListFeed feed = service.getFeed(DocumentListFeedUrl, DocumentListFeed.class);
        for (DocumentListEntry entry : feed.getEntries()) {
            ret += printDocumentEntry(entry) + ",";
        }
        try {
            //  Thread.currentThread().sleep(1000 * 121);
        } catch (Exception e) {
            System.out.println("Dentro show all: " + e.getMessage());
            // }
        }
        return ret;
    }

    public String printDocumentEntry(DocumentListEntry doc) throws MalformedURLException, IOException, ServiceException {
        String shortId = doc.getId().substring(doc.getId().lastIndexOf('/') + 1);
        DateTime date = doc.getPublished();
        DateTime date1 = doc.getUpdated();
        String versionId = doc.getVersionId();
        String resourceId = doc.getResourceId();
        String modified = doc.getLastModifiedBy().getName();
      
        String title = "";
        if (doc.getTitle().getPlainText().contains("Flow_")) {
            title = doc.getTitle().getPlainText();
        }
        String docType = resourceId.substring(0, resourceId.lastIndexOf(':'));

        //   System.out.println(" -- Document(" + shortId + "/" + doc.getTitle().getPlainText() + ")" + date.toString() +" ver. "+ versionId);
        String s = " " + doc.getTitle().getPlainText() + "C: " + date.toString() + " U: " + date1.toString();
        //   System.out.println("S "+s);

        return title;
    }

    public static String saveDiagram(String login, String name, boolean publish, String s, String users, String writers, String pwd, String assignees, String hub) {
        try {
            if (publish) {
                DocsService service = new DocsService("Document List Demo");
                service.setUserCredentials(login, pwd);
                if (users != null) {
                    return new GoDoc(service).saveDoc(login, name, s, users, writers, pwd, assignees, hub);
                } else {
                    return new GoDoc(service).saveDoc(login, name, s);
                }
            } else {
                File tmp = File.createTempFile(name, null);
                FileWriter w = new FileWriter(tmp);
                w.write(s, 0, s.length());
                w.close();
                return "saved temp copy";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    public static String savePermissions(String nomeFile, String flowName, String notification) {
        try {
            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            new GoDoc(service).savePermissionsOnFile(nomeFile, flowName, notification);
            return "save";
        } catch (Exception ex) {
            Logger.getLogger(GoDoc.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("In savePermission: " + ex.getMessage());
            return "error";
        }
    }

    public static String checkPermission(String nomeFile, String flowName, String notification) {
        try {
            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            return new GoDoc(service).checkPermissionOnFile(nomeFile, flowName, notification);


        } catch (Exception ex) {
            System.out.println("Errore checkPermission: " + ex.getMessage());
            ex.printStackTrace();
            return "error";
        }
    }

    public String checkPermissionOnFile(String nomeFile, String flowName, String notification) {
        try {
            DocumentListEntry doc = findEntry(nomeFile);
            if (doc != null) {

                String resourceId = doc.getResourceId();
                String docType = resourceId.substring(0, resourceId.lastIndexOf(':'));
                String docId = resourceId.substring(resourceId.lastIndexOf(':') + 1);
                URL exportUrl = new URL(DownloadFeed + docType
                        + "s/Export?docID=" + docId + "&exportFormat=" + "html");
                MediaContent mc = new MediaContent();
                mc.setUri(exportUrl.toString());
                MediaSource ms = service.getMedia(mc);
                String s = "";
                byte[] b = new byte[1024];
                InputStream inStream = ms.getInputStream();
                int c;
                while ((c = inStream.read(b)) != -1) {
                    s += new String(b, 0, c);
                }
                s = new GoDoc(service).findFiles(s);
                File f;
                f = new File("/var/www/Permissions/prova.txt");
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileWriter writer = new FileWriter(f);
                writer.write(s);
                writer.close();
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);

                String t = "";

                boolean subscribe = false;
                while ((t = br.readLine()) != null) {

                    if (t.contains(flowName + "/" + "All")) {
                        subscribe = true;
                    } else if (t.contains(flowName) && t.contains(notification)) {
                        subscribe = true;
                    }

                }
                fr.close();
                if (subscribe == true) {
                    return "subscribe";
                } else {
                    return "none";
                }


            } else {
                return "no permission file";
            }

        } catch (Exception ex) {
            System.out.println("Errore in check on file: " + ex.getMessage());
            ex.printStackTrace();
            return "errore";
        }
    }

    public void savePermissionsOnFile(String nomeFile, String flowName, String notification) {
        try {
            System.out.println("GoDOc.savePermissionsOnfile 1");
            DocumentListEntry documentEntry = findEntry(nomeFile);
            //   DocumentListEntry doc = new GoDoc(service).findEntry(nomeFile);
            if (documentEntry != null) {
                String resourceId = documentEntry.getResourceId();
                String docType = resourceId.substring(0, resourceId.lastIndexOf(':'));
                String docId = resourceId.substring(resourceId.lastIndexOf(':') + 1);
                URL exportUrl = new URL(DownloadFeed + docType
                        + "s/Export?docID=" + docId + "&exportFormat=" + "html");
                MediaContent mc = new MediaContent();
                System.out.println("GoDOc.savePermissionsOnfile 2");
                mc.setUri(exportUrl.toString());
                MediaSource ms = service.getMedia(mc);
                String s = "";
                byte[] b = new byte[1024];
                InputStream inStream = ms.getInputStream();
                int c;
                while ((c = inStream.read(b)) != -1) {
                    s += new String(b, 0, c);
                }
                s = new GoDoc(service).findFiles(s);
                String add = flowName + "/" + notification;
                System.out.println("GoDOc.savePermissionsOnfile 3");
                File f;
                if (FeedUtil.isLocalMode()) {
                    f = new File("/var/www/Permissions/prova.txt");
                } else {
                    f = new File("/var/www/html/Permissions/prova.txt");
                }
                if (!f.exists()) {
                    f.createNewFile();
                }
                System.out.println("GoDOc.savePermissionsOnfile 4");
                FileWriter writer = new FileWriter(f);
                writer.write(s);
                writer.close();
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String oldsetting = "";
                String t = "";
                String oldtext = "";
                while ((t = br.readLine()) != null) {
                    System.out.println("LINE: " + t);
                    if (t.contains(flowName)) {
                        oldsetting = t;
                    }
                    oldtext += t + "\r\n";
                }
                fr.close();
                System.out.println("GoDoc.savePermissionsonfile OLD: " + oldsetting + "add = " + add);
                if (!oldsetting.equals("")) {
                    s = oldtext.replaceAll(oldsetting, add);
                } else {
                    s += "\r\n" + add;
                }
                //   service.getRequestFactory().setHeader("If-Match", "*");
                //  doc.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));
                //  doc.updateMedia(false);

                documentEntry.getService().getRequestFactory().setHeader("If-Match", "*");
                documentEntry.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));
                documentEntry.updateMedia(false);
            } else {
                String s = flowName + "/" + notification + "\r\n";
                documentEntry = new GoDoc(service).uploadFile(s, nomeFile);
            }

        } catch (Exception e) {
            System.out.println("ERRORE: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public String saveDoc(String login, String name, String s) {
        try {
            DocumentListEntry documentEntry = findEntry(name);
            if (documentEntry == null) {
                documentEntry = uploadFile(s, name);

                return "salvato";
            }

            service.getRequestFactory().setHeader("If-Match", "*");
            System.out.println("saveDiagram \n" + s);
            System.out.println("-----");
            documentEntry.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));

            //  documentEntry.setContent(new PlainTextConstruct(s));
            documentEntry.updateMedia(false);
            return "salvato";
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    public static String saveTemplate(String login, String name, String s) {
        DocsService service = new DocsService("Document List Demo");
        try {
            service.setUserCredentials(docMakerLogin, docMakerPasswd);


            return new GoDoc(service).saveDoc(login, name, s);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    public static List<String> loadDiagram(String valorefile, boolean refresh, String owner, String pwd) {
        DocsService service = new DocsService("Document List Demo");
        try {

            service.setUserCredentials(owner, pwd);
            return new GoDoc(service).loadDoc(valorefile, refresh, owner);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("In loadDiagram " + ex.toString());
            return null;
        }
    }

    public DocumentListEntry findEntry(String title) {
        DocumentListEntry ret = null;
        try {
            DocumentList documentList = new DocumentList(APPLICATION_NAME, DocumentList.DEFAULT_HOST);
            documentList.login(docMakerLogin, docMakerPasswd);
            DocumentListFeed feed = documentList.getDocsListFeed("all");


            // System.out.println("search title =" + title);
            for (DocumentListEntry entry : feed.getEntries()) {
                //  System.out.println("entry name =" + entry.getTitle().getPlainText());
                if (entry.getTitle().getPlainText().equals(title)) {
                    return entry;
                }
            }
        } catch (Exception e) {
            System.out.println("findEntry" + e.getMessage());
        }
        return ret;
    }

    public DocumentListEntry findEntryOld(String title) throws Exception {
        DocumentListEntry ret = null;
        DocumentListFeed feed = service.getFeed(DocumentListFeedUrl, DocumentListFeed.class);
        for (DocumentListEntry entry : feed.getEntries()) {
            if (entry.getTitle().getPlainText().equals(title)) {
                return entry;
            }
        }
        return ret;
    }

    public static String checkUserId(String user, String pwd) {
        try {
            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(user, pwd);
            return "checked";
        } catch (Exception e) {
            System.out.println("Checkid method: " + e.getMessage());
            return "error";
        }
    }

    public DocumentListEntry uploadFile(String content, String title, URL uri)
            throws IOException, ServiceException {
        File file = new File("/tmp/" + title);
        FileOutputStream of = new FileOutputStream(file);
        of.write(content.getBytes(), 0, content.length());
        of.close();
        DocumentListEntry newDocument = new DocumentListEntry();
        String mimeType = DocumentListEntry.MediaType.fromFileName(file.getName()).getMimeType();
        newDocument.setFile(file, mimeType);
        ;
        newDocument.setTitle(new PlainTextConstruct(title));
        DocumentListEntry ret = service.insert(uri, newDocument);
        return ret;
    }

    public void sendMail(String dest, String emailFromAddress, String pwd, String name) {
        try {
            //   System.out.println(" godoc.sendMail() mittente: " + emailFromAddress);
            String url = FeedUtil.GetUrl() + "TaskMgr/index.jsp?Flow=" + name;
            //15-12-2010      String url = "http://localhost:8081/index.jsp?Flow="+name;
            String url2 = FeedUtil.GetUrl() + "NotifMgrG/settings.jsp?Flow=" + name;
            String text = "Hi, a new diagram is interested in you.\nGo and check this workflow " + url + "\n";
            String text2 = "Do not forget to subscribe your Notification Manager to this application: " + url2 + "\n";
            String emailMsgTxt = text + text2;

            String emailSubjectTxt = "New Collaborative Workflow: " + name;
            String[] sendTo = dest.split(",");
            new NewGmail().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt, emailFromAddress, pwd);
        } catch (Exception ex) {
            System.out.println("In SendMail Method: " + ex.getMessage());
        }


    }

    public static String checkNew(String nomeFile, String user, String pwd) {
        try {
            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(user, pwd);
            return new GoDoc(service).findNewVersion(nomeFile);
        } catch (Exception ex) {
            System.out.println("ERRORE IN CHECKNEW: " + ex.getMessage());
            ex.printStackTrace();
            return "error";
        }
    }

    public String findNewVersion(String nomeFile) {
        try {
            DocumentListEntry documentEntry = findEntry(nomeFile);
            DateTime last = documentEntry.getUpdated();
            DateTime curr = documentVersions.get(nomeFile);
            // System.out.println("LAST: "+last.getValue());
            // System.out.println("CURR: "+curr.getValue());
            if (curr == null || curr.getValue() == last.getValue()) {
                return "old";
            } else {
                documentVersions.put(nomeFile, last); // updateRole current version
                return "new";
            }
        } catch (Exception ex) {
            System.out.println("ERRORE IN FIND NEW VERSION " + ex.getMessage());
            ex.printStackTrace();
            return "error";
        }

    }

    private DocumentList getDocumentList() {
        try {
            if (documentList == null) {
                documentList = new DocumentList(APPLICATION_NAME, DocumentList.DEFAULT_HOST);
                documentList.login(docMakerLogin, docMakerPasswd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return documentList;
    }
}
