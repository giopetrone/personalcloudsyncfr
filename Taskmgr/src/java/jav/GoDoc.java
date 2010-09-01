/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

//import com.google.appengine.repackaged.com.google.protobuf.ServiceException;
import com.google.gdata.client.DocumentQuery;
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
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.ServiceException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import pubsublib.event.AtomEvent;
import pubsublib.test.TestPub;

/**
 *
 * @author marino
 */
public class GoDoc {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    URL documentListFeedUrl = null;
    DocsService service = null;
    static HashMap<String, DateTime> documentVersions = new HashMap();
    private boolean printed = false;
    boolean one = false;
    //anna gio
    //   static String docMakerLogin = "annamaria.goy@gmail.com";  // fino a che  ???  auth funziona
    //   static String docMakerPasswd = "tex_willer";
    // static String docMakerLogin = "sgnmrn@gmail.com";  // fino a che  ???  auth funziona
    //static String docMakerPasswd = "micio11";
    static String docMakerLogin = "fabrizio.torretta@gmail.com";  // fino a che  ???  auth funziona
    static String docMakerPasswd = "gregorio";


    //fine Anna gio

    public GoDoc(DocsService service) {
        try {
            documentListFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.service = service;
    }

    public static void main(String[] args) throws MalformedURLException, IOException, ServiceException {
        DocsService service = new DocsService("Document List Demo");
        service.setUserCredentials(docMakerLogin, docMakerPasswd);
        DocumentQuery query = new DocumentQuery(new URL("http://docs.google.com/feeds/documents/private/full/-/mine"));
//DocumentListEntry documentEntry = findEntry("prova6.txt");
        DocumentListFeed resultFeed = service.getFeed(query, DocumentListFeed.class);
     //   System.out.println(prendi());
    }

    public static String prendi(String login, String pwd) {
        DocsService service = new DocsService("Document List Demo");
        try {
            service.setUserCredentials(login, pwd);
          
            //   return new GoDoc(service).saveTemplate("NULLUSER", "temp.txt", "ciao");
            return new GoDoc(service).showAllDocs();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    /*

    public List<String> saveDoc(String name, String s,String users, String writers) {
    List<String> status = new ArrayList();
    int update = 0;
    int newriters = 0;
    int newreaders = 0;

     */
    public void saveNewDiagram(String login, String documentName,String s, String users, String writers )
    {
                try{
                    DocumentListEntry documentEntry = findEntry(documentName);
                    System.out.println("DocumentEntry null");
                    documentEntry = uploadFile(s, documentName);
                    AtomEvent eventSave = new AtomEvent(login, "TaskManager", "Save New Diagram");
                    String link = documentEntry.getDocumentLink().getHref();
                    eventSave.setParameter("File", documentName);
                    eventSave.setParameter("Link", link);
                    FeedUtil.addEntry("", documentName, eventSave);
                    String[] tempwriter;
                    String delimiter = ",";
                    //     if(writers == null) writers = "";
                    if (users != null && writers != null) {
                        String[] tempusers;

                        tempusers = users.split(delimiter);

                        for (int i = 0; i < tempusers.length; i++) {
                            if (!tempusers[i].equals("")) {

                                System.out.println("Reader " + tempusers[i]);
                                addReaders(documentEntry, tempusers[i]);
                                AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                event.setParameter("File", documentName);
                                event.setParameter("Permission", "Read");
                                event.setParameter("Who", tempusers[i]);
                                FeedUtil.addEntry("", documentName, event);
                                new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));


                            }

                        }
                        tempwriter = writers.split(delimiter);
                        for (int j = 0; j < tempwriter.length; j++) {
                            boolean check = false;

                            for (int i = 0; i < tempusers.length; i++) {
                                if (tempusers[i].equals(tempwriter[j])) {
                                    check = true;
                                    System.out.println("true");
                                }

                            }
                            if (check == false) {
                                System.out.println("Writer " + tempwriter[j] + check);
                                addWriting(documentEntry, tempwriter[j]);
                                AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                event.setParameter("File", documentName);
                                event.setParameter("Permission", "Write");
                                event.setParameter("Who", tempwriter[j]);
                                FeedUtil.addEntry("", documentName, event);
                                new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                            }
                        }

                    } else {
                        /* tempwriter = writers.split(delimiter);
                        for (int j = 0; j < tempwriter.length; j++) {
                        addWriting(documentEntry, tempwriter[j]);
                        AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                        event.setParameter("File", documentName);
                        event.setParameter("Permission", "Write");
                        event.setParameter("Who", tempwriter[j]);
                        FeedUtil.addEntry("", documentName, event);
                        new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                        }*/

                    }

                     System.out.println("DENTRO SAVENEW!") ;
                    }catch(Exception ex)
                    {
                         System.out.println(ex.toString());
                        
                    }



    }

    public void uploadDiagram(String login, String documentName, String users, String writers) throws Exception
    {
       System.out.println("DocumentEntry NOT null");
                DocumentListEntry documentEntry = findEntry(documentName);
                AtomEvent eventUpdate = new AtomEvent(login, "TaskManager", "Update Diagram");
                eventUpdate.setParameter("File", documentName);
                FeedUtil.addEntry("", documentName, eventUpdate);
                //     service.getRequestFactory().setHeader("If-Match", "*");
                //    AclFeed aclFeed = service.getFeed(new URL(documentEntry.getAclFeedLink().getHref()), AclFeed.class);
                //   for (AclEntry entry : aclFeed.getEntries()) entry.delete();

                try {

                    List<String> readers = new ArrayList();
                    List<String> collaborators = new ArrayList();
                    AclFeed aclFeed = service.getFeed(new URL(documentEntry.getAclFeedLink().getHref()), AclFeed.class);

                    for (AclEntry entry : aclFeed.getEntries()) {
                        if (entry.getRole().getValue().equals("reader")) {
                            readers.add(entry.getScope().getValue());
                        } else if (entry.getRole().getValue().equals("writer")) {
                            collaborators.add(entry.getScope().getValue());
                        }


                    }




                    String[] tempwriter;
                    String delimiter = ",";

                    if (users != null) {
                        String[] tempusers;
                        if (writers == null) {
                            writers = "";
                        }
                        tempusers = users.split(delimiter);
                        System.out.println("READERS SIZE: " + readers.size());
                        System.out.println("TEMPUSERS LENGTH: " + tempusers.length);
                        for (int i = 0; i < tempusers.length; i++) {
                            boolean check = false;
                            System.out.println("-------------------------");

                            if (!tempusers[i].equals("")) {
                                System.out.println("TEMPUSER: " + tempusers[i] + " finisce qui");
                                for (int z = 0; z < readers.size(); z++) {
                                    System.out.println(readers.get(z));
                                    if (tempusers[i].contains(readers.get(z))) {
                                        check = true;
                                    }

                                    System.out.println("Check Reader: " + check);
                                }

                                for (int z = 0; z < collaborators.size(); z++) {

                                    System.out.println("collaborators: " + collaborators.get(z));
                                    if (tempusers[i].contains(collaborators.get(z))) {
                                        update(documentEntry, aclFeed, collaborators.get(z));
                                        AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                        event.setParameter("File", documentName);
                                        event.setParameter("Change Permission", "From Write To Read");
                                        event.setParameter("Who", collaborators.get(z));
                                        FeedUtil.addEntry("", documentName, event);
                                        new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                                        System.out.println("DOPO UPDATE");
                                        check = true;

                                    }
                                }

                                if (check == false) {
                                    System.out.println("QUI!");
                                    addReaders(documentEntry, tempusers[i]);
                                    AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                    event.setParameter("File", documentName);
                                    event.setParameter("Permission", "Read");
                                    event.setParameter("Who", tempusers[i]);
                                    FeedUtil.addEntry("", documentName, event);
                                    new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                                }

                            }
                        }
                        tempwriter = writers.split(delimiter);
                        // System.out.println("tempwriter: "+tempwriter.length);
                        // System.out.println("Collaborators SIZE: "+collaborators.size());
                        for (int j = 0; j < tempwriter.length; j++) {
                            boolean check = false;
                            System.out.println("-------------------------");
                            System.out.println("TEMPWRITER: " + tempwriter[j]);

                            for (int i = 0; i < tempusers.length; i++) {
                                // System.out.println("TEMPUSER: " + tempusers[i]);
                                if (tempusers[i].equals(tempwriter[j])) {
                                    check = true;
                                }
                            }
                            for (int z = 0; z < collaborators.size(); z++) {
                                System.out.println("collaborators: " + collaborators.get(z));
                                if (collaborators.get(z).contains(tempwriter[j]) || tempwriter[j].contains(collaborators.get(z))) {
                                    System.out.println("DENTRO IF");
                                    check = true;
                                }
                            }
                            System.out.println("Check Writer: " + check);
                            if (check == false) {
                                System.out.println("Writer " + tempwriter[j] + check);
                                addWriting(documentEntry, tempwriter[j]);
                                AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                event.setParameter("File", documentName);
                                event.setParameter("Permission", "Write");
                                event.setParameter("Who", tempwriter[j]);
                                FeedUtil.addEntry("", documentName, event);
                                new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                            }
                        }

                    } else {
                        tempwriter = writers.split(delimiter);
                        for (int j = 0; j < tempwriter.length; j++) {
                            boolean check = false;
                            for (int z = 0; z < collaborators.size(); z++) {
                                System.out.println("collaborators: " + collaborators.get(z));
                                if (collaborators.get(z).equals(tempwriter[j])) {
                                    check = true;
                                }
                            }
                            System.out.println("Check Writer: " + check);
                            if (check == false) {
                                System.out.println("Writer " + tempwriter[j] + check);
                                addWriting(documentEntry, tempwriter[j]);
                                AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                event.setParameter("File", documentName);
                                event.setParameter("Permission", "Write");
                                event.setParameter("Who", tempwriter[j]);
                                FeedUtil.addEntry("", documentName, event);
                                new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                            }
                        }
                    }

                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }

    public String saveDoc(String login, String documentName, String s, String users, String writers) {

        try {
            DocumentListEntry documentEntry = findEntry(documentName);

            if (documentEntry == null) {
               
               saveNewDiagram(login,documentName,s,users,writers);
/*
                System.out.println("DocumentEntry null");
                documentEntry = uploadFile(s, documentName);
                AtomEvent eventSave = new AtomEvent(login, "TaskManager", "Save New Diagram");
                String link = documentEntry.getDocumentLink().getHref();
                eventSave.setParameter("File", documentName);
                eventSave.setParameter("Link", link);
                FeedUtil.addEntry("", documentName, eventSave);
                String[] tempwriter;
                String delimiter = ",";
                //     if(writers == null) writers = "";
                if (users != null && writers != null) {
                    String[] tempusers;

                    tempusers = users.split(delimiter);

                    for (int i = 0; i < tempusers.length; i++) {
                        if (!tempusers[i].equals("")) {

                            System.out.println("Reader " + tempusers[i]);
                            addReaders(documentEntry, tempusers[i]);
                            AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                            event.setParameter("File", documentName);
                            event.setParameter("Permission", "Read");
                            event.setParameter("Who", tempusers[i]);
                            FeedUtil.addEntry("", documentName, event);
                            new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));


                        }

                    }
                    tempwriter = writers.split(delimiter);
                    for (int j = 0; j < tempwriter.length; j++) {
                        boolean check = false;

                        for (int i = 0; i < tempusers.length; i++) {
                            if (tempusers[i].equals(tempwriter[j])) {
                                check = true;
                                System.out.println("true");
                            }

                        }
                        if (check == false) {
                            System.out.println("Writer " + tempwriter[j] + check);
                            addWriting(documentEntry, tempwriter[j]);
                            AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                            event.setParameter("File", documentName);
                            event.setParameter("Permission", "Write");
                            event.setParameter("Who", tempwriter[j]);
                            FeedUtil.addEntry("", documentName, event);
                            new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                        }
                    }

                } else {
                    /* tempwriter = writers.split(delimiter);
                    for (int j = 0; j < tempwriter.length; j++) {
                    addWriting(documentEntry, tempwriter[j]);
                    AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                    event.setParameter("File", documentName);
                    event.setParameter("Permission", "Write");
                    event.setParameter("Who", tempwriter[j]);
                    FeedUtil.addEntry("", documentName, event);
                    new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                    }
                }
*/
                return "new";

            } else {


                uploadDiagram(login,documentName,users,writers);
                service.getRequestFactory().setHeader("If-Match", "*");
            // documentEntry.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));
            documentEntry.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));

            //         documentEntry.setContent(new PlainTextConstruct(s));
            documentEntry.updateMedia(false);
            return "notnew";

            }
                /*
                System.out.println("DocumentEntry NOT null");
                AtomEvent eventUpdate = new AtomEvent(login, "TaskManager", "Update Diagram");
                eventUpdate.setParameter("File", documentName);
                FeedUtil.addEntry("", documentName, eventUpdate);
                //     service.getRequestFactory().setHeader("If-Match", "*");
                //    AclFeed aclFeed = service.getFeed(new URL(documentEntry.getAclFeedLink().getHref()), AclFeed.class);
                //   for (AclEntry entry : aclFeed.getEntries()) entry.delete();

                try {

                    List<String> readers = new ArrayList();
                    List<String> collaborators = new ArrayList();
                    AclFeed aclFeed = service.getFeed(new URL(documentEntry.getAclFeedLink().getHref()), AclFeed.class);

                    for (AclEntry entry : aclFeed.getEntries()) {
                        if (entry.getRole().getValue().equals("reader")) {
                            readers.add(entry.getScope().getValue());
                        } else if (entry.getRole().getValue().equals("writer")) {
                            collaborators.add(entry.getScope().getValue());
                        }


                    }




                    String[] tempwriter;
                    String delimiter = ",";

                    if (users != null) {
                        String[] tempusers;
                        if (writers == null) {
                            writers = "";
                        }
                        tempusers = users.split(delimiter);
                        System.out.println("READERS SIZE: " + readers.size());
                        System.out.println("TEMPUSERS LENGTH: " + tempusers.length);
                        for (int i = 0; i < tempusers.length; i++) {
                            boolean check = false;
                            System.out.println("-------------------------");

                            if (!tempusers[i].equals("")) {
                                System.out.println("TEMPUSER: " + tempusers[i] + " finisce qui");
                                for (int z = 0; z < readers.size(); z++) {
                                    System.out.println(readers.get(z));
                                    if (tempusers[i].contains(readers.get(z))) {
                                        check = true;
                                    }

                                    System.out.println("Check Reader: " + check);
                                }

                                for (int z = 0; z < collaborators.size(); z++) {

                                    System.out.println("collaborators: " + collaborators.get(z));
                                    if (tempusers[i].contains(collaborators.get(z))) {
                                        update(documentEntry, aclFeed, collaborators.get(z));
                                        AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                        event.setParameter("File", documentName);
                                        event.setParameter("Change Permission", "From Write To Read");
                                        event.setParameter("Who", collaborators.get(z));
                                        FeedUtil.addEntry("", documentName, event);
                                        new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                                        System.out.println("DOPO UPDATE");
                                        check = true;

                                    }
                                }

                                if (check == false) {
                                    System.out.println("QUI!");
                                    addReaders(documentEntry, tempusers[i]);
                                    AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                    event.setParameter("File", documentName);
                                    event.setParameter("Permission", "Read");
                                    event.setParameter("Who", tempusers[i]);
                                    FeedUtil.addEntry("", documentName, event);
                                    new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                                }

                            }
                        }
                        tempwriter = writers.split(delimiter);
                        // System.out.println("tempwriter: "+tempwriter.length);
                        // System.out.println("Collaborators SIZE: "+collaborators.size());
                        for (int j = 0; j < tempwriter.length; j++) {
                            boolean check = false;
                            System.out.println("-------------------------");
                            System.out.println("TEMPWRITER: " + tempwriter[j]);

                            for (int i = 0; i < tempusers.length; i++) {
                                // System.out.println("TEMPUSER: " + tempusers[i]);
                                if (tempusers[i].equals(tempwriter[j])) {
                                    check = true;
                                }
                            }
                            for (int z = 0; z < collaborators.size(); z++) {
                                System.out.println("collaborators: " + collaborators.get(z));
                                if (collaborators.get(z).contains(tempwriter[j]) || tempwriter[j].contains(collaborators.get(z))) {
                                    System.out.println("DENTRO IF");
                                    check = true;
                                }
                            }
                            System.out.println("Check Writer: " + check);
                            if (check == false) {
                                System.out.println("Writer " + tempwriter[j] + check);
                                addWriting(documentEntry, tempwriter[j]);
                                AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                event.setParameter("File", documentName);
                                event.setParameter("Permission", "Write");
                                event.setParameter("Who", tempwriter[j]);
                                FeedUtil.addEntry("", documentName, event);
                                new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                            }
                        }

                    } else {
                        tempwriter = writers.split(delimiter);
                        for (int j = 0; j < tempwriter.length; j++) {
                            boolean check = false;
                            for (int z = 0; z < collaborators.size(); z++) {
                                System.out.println("collaborators: " + collaborators.get(z));
                                if (collaborators.get(z).equals(tempwriter[j])) {
                                    check = true;
                                }
                            }
                            System.out.println("Check Writer: " + check);
                            if (check == false) {
                                System.out.println("Writer " + tempwriter[j] + check);
                                addWriting(documentEntry, tempwriter[j]);
                                AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                event.setParameter("File", documentName);
                                event.setParameter("Permission", "Write");
                                event.setParameter("Who", tempwriter[j]);
                                FeedUtil.addEntry("", documentName, event);
                                new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                            }
                        }
                    }

                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }*/
            
            //    return documentEntry.getDocumentLink().getHref();
        } catch (Exception ex) {
            ex.printStackTrace();

            return ex.toString();
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
            documentEntry.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));

            //  documentEntry.setContent(new PlainTextConstruct(s));
            documentEntry.updateMedia(false);
            return "salvato";
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    public DocumentListEntry uploadFile(String content, String title)
            throws IOException, ServiceException {
        File file = new File("/tmp/" + title);
        FileOutputStream of = new FileOutputStream(file);
        of.write(content.getBytes(), 0, content.length());
        of.close();
        DocumentListEntry newDocument = new DocumentListEntry();
        String mimeType = DocumentListEntry.MediaType.fromFileName(file.getName()).getMimeType();
        newDocument.setFile(file, mimeType);
        newDocument.setTitle(new PlainTextConstruct(title));
        DocumentListEntry ret = service.insert(new URL("http://docs.google.com/feeds/documents/private/full/"), newDocument);
        file.delete();
        return ret;
    }

    private AclEntry addWriting(DocumentListEntry documentEntry, String who) throws IOException, MalformedURLException, ServiceException {
        AclRole role = new AclRole("writer");

        AclScope scope = new AclScope(AclScope.Type.USER, who);
        AclEntry entry = new AclEntry();
        entry.setRole(role);
        entry.setScope(scope);
        URL url = new URL("http://docs.google.com/feeds/acl/private/full/" + documentEntry.getResourceId());
        return service.insert(url, entry);
    }

    private void update(DocumentListEntry documentEntry, AclFeed aclFeed, String who) throws IOException, MalformedURLException, ServiceException {


        for (AclEntry entry : aclFeed.getEntries()) {

            //  System.err.println(entry.getRole().getValue());
            if (entry.getScope().getValue().equals(who)) {
                entry.setRole(new AclRole("reader"));
                entry.update();
            }



        }

    }

    private AclEntry addReaders(DocumentListEntry documentEntry, String who) throws IOException, MalformedURLException, ServiceException {

        AclRole role = new AclRole("reader");

        AclScope scope = new AclScope(AclScope.Type.USER, who);
        AclEntry entry = new AclEntry();
        entry.setRole(role);
        entry.setScope(scope);
        URL url = new URL("http://docs.google.com/feeds/acl/private/full/" + documentEntry.getResourceId());
        return service.insert(url, entry);
    }

    public List<String> loadDoc(String valorefile, boolean refresh, String owner) {
        try {
          //  System.out.println("-----------" +valorefile);
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
            documentVersions.put(valorefile, last); // update current version
            String resourceId = documentEntry.getResourceId();
           
            String docType = resourceId.substring(0, resourceId.lastIndexOf(':'));
            
            String docId = resourceId.substring(resourceId.lastIndexOf(':') + 1);
            
            URL exportUrl = new URL("http://docs.google.com/feeds/download/" + docType
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
      
            s = findBody(s);
        
            String error = "Non hai i permessi";
            List<String> people = new ArrayList();
            List<String> collaborators = new ArrayList();
            List<String> readers = new ArrayList();
            AclFeed aclFeed = service.getFeed(new URL(documentEntry.getAclFeedLink().getHref()), AclFeed.class);

            for (AclEntry entry : aclFeed.getEntries()) {
                if (entry.getRole().getValue().equals("reader")) {
                    readers.add(entry.getScope().getValue());
                }
                people.add(entry.getScope().getValue());
                if (entry.getRole().getValue().equals("writer")) {
                    collaborators.add(entry.getScope().getValue());
                }


            }
            String users = readers.toString();
           

            String nousers = "";
            if (users.equals("[]")) {
                users = "";
            } else {
                users = users.substring(1, users.length() - 1);
            }
            System.out.println(users);
            String writers = collaborators.toString();
          




            if (writers.equals("[]")) {
                writers = "";
            } else {
                writers = writers.substring(1, writers.length() - 1);
            }
            System.out.println(writers);


            if (people.toString().contains(owner) == true) {
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
            System.out.println("In Load Doc "+ex.toString());
            List<String> list = new LinkedList();
            list.add("errore");
            list.add("errore");
            list.add("errore");
            return list;
        }
    }

    private String findBody(String r) {
        int ind1 = r.indexOf("connections");
        int ind2 = r.indexOf("<br></body>");
        r = r.substring(ind1 - 2, ind2);
        r = r.replaceAll("&gt;", ">");
        r = r.replaceAll("&lt;", "<");
        return r;
    }

    public String showAllDocs() throws Exception {
        String ret = "";
        // for (int i = 0; i < 10; i++) {
        DocumentListFeed feed = service.getFeed(documentListFeedUrl, DocumentListFeed.class);
        for (DocumentListEntry entry : feed.getEntries()) {
            ret += printDocumentEntry(entry) + ",";
        }
        try {
            //  Thread.currentThread().sleep(1000 * 121);
        } catch (Exception e) {
            // }
        }
        return ret;
    }

    public String printDocumentEntry(DocumentListEntry doc) {
        String shortId = doc.getId().substring(doc.getId().lastIndexOf('/') + 1);
        DateTime date = doc.getPublished();
        DateTime date1 = doc.getUpdated();
        String versionId = doc.getVersionId();
        String resourceId = doc.getResourceId();
        String title = "";
        if (doc.getTitle().getPlainText().contains(".txt")) {
            title = doc.getTitle().getPlainText();
        }
        String docType = resourceId.substring(0, resourceId.lastIndexOf(':'));

        //   System.out.println(" -- Document(" + shortId + "/" + doc.getTitle().getPlainText() + ")" + date.toString() +" ver. "+ versionId);
        String s = " " + doc.getTitle().getPlainText() + "C: " + date.toString() + " U: " + date1.toString();
        // System.out.println(s);
        return title;
    }

    public static String saveDiagram(String login, String name, boolean publish, String s, String users, String writers,String pwd) {
        try {
            if (publish) {
                DocsService service = new DocsService("Document List Demo");
                service.setUserCredentials(login, pwd);
                if (users != null) {
                    return new GoDoc(service).saveDoc(login, name, s, users, writers);
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

    public static List<String> loadDiagram(String valorefile, boolean refresh, String owner,String pwd) {
        DocsService service = new DocsService("Document List Demo");
        try {
        
            service.setUserCredentials(owner, pwd);

            return new GoDoc(service).loadDoc(valorefile, refresh, owner);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("In loadDiagram "+ex.toString());
            return null;
        }
    }

    public DocumentListEntry findEntry(String title) throws Exception {
        DocumentListEntry ret = null;
        DocumentListFeed feed = service.getFeed(documentListFeedUrl, DocumentListFeed.class);
        for (DocumentListEntry entry : feed.getEntries()) {
            if (entry.getTitle().getPlainText().equals(title)) {
                return entry;
            }
        }
        return ret;
    }



}
