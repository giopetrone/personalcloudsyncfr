<%-- 
    Document   : index
    Created on : Dec 6, 2009, 3:10:09 PM
    Author     : giovanna
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <form action="/MailServlet/MailS" method="POST"  enctype="multipart/form-data" >

        File 1:<input type="file" name="file1"/><br/>
       
       <p> <input type="submit" name="Submit" value="Upload Files"/> </p>


      </form>
    </body>
</html>
