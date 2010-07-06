/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

/**
 *
 * @author fabrizio
 */

import java.io.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
  * a very simple example of using the SunFtpWrapper class,
  * available at http://www.nsftools.com/tips/JavaFtp.htm
  */

public class FtpWrapperTest {
	public static void main (String[] args) {
		try {
			SunFtpWrapper ftp = new SunFtpWrapper();
			String serverName = "ftp.d1036477.xoomers.virgilio.it";
			ftp.openServer(serverName);
			if (ftp.serverIsOpen()) {
				System.out.println("Connected to " + serverName);
				try {
					ftp.login("fabriziotorretta", "sync09fr");
					System.out.println("Welcome message:\n" + ftp.welcomeMsg);
					System.out.println("Current Directory: " + ftp.pwd());
					System.out.println("Results of a raw LIST command:\n" + ftp.listRaw());
					
					ftp.ascii();
					//
                                  //      ftp.deleteFile("/Flow/abc.txt.xml");
                                        String path = "/var/www/Flow/";
                                        String nome = "io6.txt";
                                        String xml = ".xml";
                                        String local = path+nome+xml;
                                        String path1 = "/webspace/httpdocs/Flow/";
                                        String remote = path1+nome+xml;
                                       
                                     //   ftp.downloadFile("index.html", path+"index.html");
                                        ftp.uploadFile(local, remote);
                                 //       ftp.uploadFile("/rules.log", "/index2.log");
				} catch (Exception ftpe) {
					ftpe.printStackTrace();
				} finally {
					ftp.closeServer();
				}
			} else {
				System.out.println("Unable to connect to" + serverName);
			}
			System.out.println("Finished");
		} catch(Exception e) {
			e.printStackTrace();
		}
/*
    boolean error = false;
    FTPClient ftp=new FTPClient();
    try {
      int reply;

      String server = "ftp.d1036472.xoomers.virgilio.it";
      ftp.connect(server);
      System.out.println("Connected to " + server + ".");
      System.out.print(ftp.getReplyString());
      ftp.login("fabriziotorretta", "sync09fr");
   
      FTPFile[] files = ftp.listFiles("/Flow");
      //fis = null;
      String filename = "/var/www/Flow/io.txt.xml";
      InputStream fis = new FileInputStream(filename);
      ftp.storeFile("/Flow/bbb.txt.xml", fis);
      // After connection attempt, you should check the reply code to verify
      // success.
      reply = ftp.getReplyCode();

      if(!FTPReply.isPositiveCompletion(reply)) {
        ftp.disconnect();
        System.err.println("FTP server refused connection.");
        System.exit(1);
      }

      ftp.logout();
      fis.close();
    } catch(IOException e) {
      error = true;
      e.printStackTrace();
    } finally {
      if(ftp.isConnected()) {
        try {
          ftp.disconnect();
        } catch(IOException ioe) {
          // do nothing
        }
      }
      System.exit(error ? 1 : 0);
    }
*/



	}
}

