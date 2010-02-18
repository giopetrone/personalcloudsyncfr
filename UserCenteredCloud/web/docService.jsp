<%-- 
    Document   : service2
    Created on : 1-feb-2010, 16.39.03
    Author     : liliana
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="myCLasses.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Document Sharing Service</title>
    </head>
    <body>
        <h1>Document Sharing Service: <%=session.getAttribute("userID")%></h1>
        <form action="/UserCenteredCloud/Controller" method="POST"/>
        <p><input type="radio" name="documentName" value="document1" checked/> 
        Document1
        <!-- (shared with
        <%= ConfigurationSettings.document1Contributors.toString() %>)-->
        </p>
        <p><input type="radio" name="documentName" value="document2" /> 
        Document2 <!-- (shared with
        <%= ConfigurationSettings.document2Contributors.toString()%>) -->
        </p>

        <p><input type="submit" name="operation" value="open"></p>
    </body>
</html>
