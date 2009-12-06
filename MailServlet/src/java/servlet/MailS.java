/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import maillib.SendMail;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author giovanna
 */
public class MailS extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final String SMTP_AUTH_USER = "gio.petrone@gmail.com";
    private static final String SMTP_AUTH_PWD = "mer20ia05";
    private static final String emailMsgTxt = "prova di mail con attachemnt";
    private static final String emailSubjectTxt = "Prova di mail ";
//  private static final String emailFromAddress = "giovanna@di.unito.it";
    private static final String emailFromAddress = "gio.petrone@gmail.com";
    // Add List of Email address to who email needs to be sent to
    private static final String[] emailList = {"giovanna@di.unito.it"};

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MailS</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MailS at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
            try {
// upload
                // Create a factory for disk-based file items
                // Check that we have a file upload request
                boolean isMultipart = ServletFileUpload.isMultipartContent(request);
DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
                ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

List fileItemsList = servletFileUpload.parseRequest(request);


diskFileItemFactory.setSizeThreshold(40960); /* the unit is bytes */

File repositoryPath = new File("/temp");
diskFileItemFactory.setRepository(repositoryPath);
servletFileUpload.setSizeMax(81920); /* the unit is bytes */

try {
   fileItemsList = servletFileUpload.parseRequest(request);

  /* Process file items... */

Iterator it = fileItemsList.iterator();
while (it.hasNext()){
  FileItem fileItem = (FileItem)it.next();
  if (fileItem.isFormField()){
    /* The file item contains a simple name-value pair of a form field */
  }
  else{
    /* The file item contains an uploaded file */
  }
}
}
catch (Exception ex) {
  /* The size of the HTTP request body exceeds the limit */
}
// Process the uploaded items
               



//
//                FileUpload fu = new FileUpload();
//                DiskFileItemFactory fileItems = (DiskFileItemFactory) fu.getFileItemFactory();
//                     FileItem fi = null;
//
//
//
//                    if (!fi.isFormField()) {
//                        System.out.println("\nNAME: " + fi.getName());
//                        System.out.println("SIZE: " + fi.getSize());
//                        System.out.println(fi.getOutputStream().toString());
//                        File fNew = new File(request.getContextPath(), fi.getName());
//
//                        System.out.println(fNew.getAbsolutePath());
//                        fi.write(fNew);
//                    }


                //end upload

                //---------------
                SendMail smtpMailSender = new SendMail();
                smtpMailSender.postMail(emailList, emailSubjectTxt, emailMsgTxt, emailFromAddress);
                System.out.println("Sucessfully Sent mail to All Users");
            } catch (Exception e) {
                System.out.println("FAIL sens mail");
            }
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
