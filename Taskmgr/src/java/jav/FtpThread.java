/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

/**
 *
 * @author marino
 */
public class FtpThread extends Thread{

    String nameFeed;

    public FtpThread(String nameFeed) {
        this.nameFeed = nameFeed;

    }

    public void run() {
        try {
            System.err.println("thread prima di upload");
         SunFtpWrapper ftp = new SunFtpWrapper();
                ftp.uploadFeed(nameFeed);
                  System.err.println("thread dopo di upload");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
