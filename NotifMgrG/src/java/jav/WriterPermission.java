/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author fabrizio
 */
public class WriterPermission {


    public static void main(String args[]) throws Exception
    {

        File f;
        f=new File("/var/www/Permissions/fabrizio.toorrettta.txt");
        String newtext = "new permissions";
        if(!f.exists())
        {

              f.createNewFile();

              FileWriter writer = new FileWriter(f);
              writer.write(newtext);
              writer.close();
        }
        else
        {
              System.out.println("ESISTE");
              String s;
              String oldtext = "";
              FileReader fr = new FileReader(f);
              BufferedReader br = new BufferedReader(fr);
              String oldsetting = "";
             // FileWriter writer = new FileWriter(f);
              while((s = br.readLine()) != null)
              {
                 System.out.println("LINE: "+s);
                 if(s.contains("ciao.txt")) oldsetting =s;
                 oldtext += s + "\r\n";
              }
              fr.close();
              System.out.println("OLD: "+oldsetting);
              if(!oldsetting.equals(""))  newtext = oldtext.replaceAll(oldsetting, "blah blah blah");
              else newtext = oldtext;
              FileWriter writer = new FileWriter(f);
              writer.write(newtext);
              writer.close();

          }
    
        }

        public static void writeNotifications(String user,String docname,String notification)
        {
            try
            {
                
                File f;
                f=new File("/var/www/Permissions/"+user+".txt");
                String newtext = "";
                if(!f.exists())
                {

                      f.createNewFile();
                      FileWriter writer = new FileWriter(f);
                      newtext = docname+"/"+notification;
                      writer.write(newtext);
                      writer.close();
                }
                else
                {
                      System.out.println("ESISTE");
                      String s;
                      String oldtext = "";
                      FileReader fr = new FileReader(f);
                      BufferedReader br = new BufferedReader(fr);
                      String oldsetting = "";
                     // FileWriter writer = new FileWriter(f);
                      while((s = br.readLine()) != null)
                      {
                         System.out.println("LINE: "+s);
                         if(s.contains(docname)) oldsetting =s;
                         oldtext += s + "\r\n";
                      }
                      fr.close();
                      System.out.println("OLD: "+oldsetting);
                      if(!oldsetting.equals("")) 
                      {
                          newtext = oldtext.replaceAll(oldsetting, docname+"/"+notification);
                          FileWriter writer = new FileWriter(f);
                          writer.write(newtext);
                          writer.close();
                      }
                      else
                      {
                          String add = docname+"/"+notification;
                          oldtext += add + "\r\n";
                          FileWriter writer = new FileWriter(f);
                          writer.write(oldtext);
                          writer.close();
                      
                      
                      }

                  }
             
            }
            catch(Exception ex)
            {
                System.out.println("Dentro writer notif: "+ex.getMessage());

            }

            
        }

        public static String checkNotifications(String user,String docname,String notification)
        {
            try
            {
                File f;
                f=new File("/var/www/Permissions/"+user+".txt");
                if(!f.exists())
                {
                    System.out.println("NO FILE");
                    return "nosubscription";
                }
                else
                {
                    String s;
                    String toverify = docname+"/"+notification;
                    FileReader fr = new FileReader(f);
                    BufferedReader br = new BufferedReader(fr);
                    boolean verification = false;
                    while((s = br.readLine()) != null)
                    {
                         System.out.println("LINE: "+s);
                         if(s.contains(docname+"/All"))
                         {
                             verification = true;
                         }
                         else if(s.contains(toverify)) verification = true;

                     }
                     fr.close();
                     System.out.println("VErifica: "+verification);
                     if(verification == true) return "subscribed";
                     else return "nosubscription";
                }

            }
            catch(Exception ex)
            {
                System.out.println("Errore in checkNotifications "+ex.getMessage());
                return "nosubscription";
            }

        }
        
    }




