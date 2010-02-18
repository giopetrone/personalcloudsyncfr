<%-- 
    Document   : document2
    Created on : 3-feb-2010, 16.34.08
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
        <h2> Document: Document2 </h2>
        <p>Contributors.: <textarea name="contributors" rows="4" cols="21" readonly="readonly">
<%= EventUtilities.genString(ConfigurationSettings.document2Contributors) %>
        </textarea></p>
        <p> Text to be translated...  </p>

        <form action="/UserCenteredCloud/Controller" method="POST"/>
        <textarea name="summary" rows="15" cols="40">
Please, translate ...
        </textarea>
        <% String dName = request.getParameter("documentName"); %>
        <input type="hidden" name="documentName" value=<%=dName%> />

        <p><input type="submit" name="operation" value="save"></p>

    </body>
</html>
