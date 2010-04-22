/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

//import com.google.appengine.repackaged.com.google.protobuf.ServiceException;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.ServiceException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

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
    HashMap<String, DateTime> documentVersions = new HashMap();
    private boolean printed = false;
    boolean one = false;
    //anna gio
    //   static String docMakerLogin = "annamaria.goy@gmail.com";  // fino a che  ???  auth funziona
    //   static String docMakerPasswd = "tex_willer";
    static String docMakerLogin = "sgnmrn@gmail.com";  // fino a che  ???  auth funziona
    static String docMakerPasswd = "micio11";
    //fine Anna gio

    public GoDoc(DocsService service) {
        try {
            documentListFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.service = service;
    }

    public static void main(String[] args) {
        System.out.println(prendi());
    }

    public static String prendi() {
        DocsService service = new DocsService("Document List Demo");
        try {
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            return new GoDoc(service).showAllDocs();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    public String saveDoc(String name, String s) {
        try {
            DocumentListEntry documentEntry = findEntry(name);
            if (documentEntry == null) {
                documentEntry = uploadFile(s, name);
                addWriting(documentEntry,"fabrizio.torretta@gmail.com");
                        addWriting(documentEntry,"gio.petrone@gmail.com");
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

    public String loadDoc(String valorefile) {
        try {
            DocumentListEntry documentEntry = findEntry(valorefile);
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
            return s;
        } catch (Exception ex) {
            return ex.toString();
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
            ret += printDocumentEntry(entry) + "\n";
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
        //   System.out.println(" -- Document(" + shortId + "/" + doc.getTitle().getPlainText() + ")" + date.toString() +" ver. "+ versionId);
        String s = " " + doc.getTitle().getPlainText() + "C: " + date.toString() + " U: " + date1.toString();
        // System.out.println(s);
        return s;
    }

    public static String saveDiagram(String name, String s) {
        DocsService service = new DocsService("Document List Demo");
        try {
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            return new GoDoc(service).saveDoc(name, s);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    public static String loadDiagram(String valorefile) {
        DocsService service = new DocsService("Document List Demo");
        try {
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            return new GoDoc(service).loadDoc(valorefile);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
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
