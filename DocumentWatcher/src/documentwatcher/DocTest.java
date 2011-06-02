/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package documentwatcher;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.extensions.LastModifiedBy;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URL;
import org.netbeans.saas.google.GoogleAccountsService;
import org.netbeans.saas.RestResponse;

/**
 *
 * @author marino
 */
public class DocTest {

    static DocsService client = null;

    public static void showAllDocs() throws IOException, ServiceException {
        URL feedUri = new URL("https://docs.google.com/feeds/documents/private/full/");
        DocumentListFeed feed = client.getFeed(feedUri, DocumentListFeed.class);
 System.out.println("ciao");
        for (DocumentListEntry entry : feed.getEntries()) {
            printDocumentEntry(entry);
        }
    }

    public static void printDocumentEntry(DocumentListEntry doc) {
        String resourceId = doc.getResourceId();
        String docType = resourceId.substring(0, resourceId.lastIndexOf(':'));

        System.out.println("'" + doc.getTitle().getPlainText() + "' (" + docType + ")");
        System.out.println("  link to Google Docs: " + doc.getHtmlLink().getHref());
        System.out.println("  resource id: " + resourceId);

        // print the parent folder the document is in
        if (!doc.getFolders().isEmpty()) {
            System.out.println("  in folder: " + doc.getFolders());
        }

        // print the timestamp the document was last viewed
        DateTime lastViewed = doc.getLastViewed();
        if (lastViewed != null) {
            System.out.println("  last viewed: " + lastViewed.toString());
        }

        // print who made that modification
        LastModifiedBy lastModifiedBy = doc.getLastModifiedBy();
        if (lastModifiedBy != null) {
            System.out.println("  updated by: "
                    + lastModifiedBy.getName() + " - " + lastModifiedBy.getEmail());
        }

        // print other useful metadata
        System.out.println("  last updated: " + doc.getUpdated().toString());
        System.out.println("  viewed by user? " + doc.isViewed());
        System.out.println("  writersCanInvite? " + doc.isWritersCanInvite().toString());
        System.out.println("  hidden? " + doc.isHidden());
        System.out.println("  starrred? " + doc.isStarred());
        System.out.println();
    }

    public static void main(String[] args) {

        try {

/*
            try {
                String accountType = "GOOGLE";
                String email = "sgnmrn@gmail.com";
                String passwd = "micio11";
                String service = "cl";
                String source = "uni-doc";

                RestResponse result = GoogleAccountsService.accountsClientLogin(accountType, email, passwd, service, source);
                //TODO - Uncomment the print Statement below to print result.
                System.out.println("The SaasService returned: "+result.getDataAsString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }*/



            client = new DocsService("yourCo-yourAppName-v1");
            client.setUserCredentials("sgnmrn@gmail.com", "micio11");
           DocTest dt = new DocTest();
          dt.showAllDocs();
        } catch (Exception e) {
            System.out.println("catch" + e.getMessage());
        }
    }
}
