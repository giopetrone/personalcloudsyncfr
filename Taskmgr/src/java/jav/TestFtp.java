/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author fabrizio
 */

public class TestFtp {


    public static void main (String[] args) {

    boolean error = false;
    FTPClient ftp=new FTPClient();
    try {
      int reply;

      String server = "ftp.d1036472.xoomers.virgilio.it";
      ftp.connect(server);
      System.out.println("Connected to " + server + ".");
      System.out.print(ftp.getReplyString());
      ftp.login("fabriziotorretta", "sync09fr");

     // FTPFile[] files = ftp.listFiles("/Flow");
      //fis = null;
      String path = "/var/www/Flow/";
      String nome = "io.txt";
      String xml = ".xml";
      String local = path+nome+xml;
      String path1 = "/webspace/httpdocs/Flow/";
      String remote = path1+nome+xml;
//      String filename = "/var/www/Flow/io.txt.xml";
      FileInputStream fis = new FileInputStream(local);
      boolean appendFile = ftp.appendFile(path1, fis);
 
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



	}

}
