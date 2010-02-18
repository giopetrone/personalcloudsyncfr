<%-- 
    Document   : service1
    Created on : 1-feb-2010, 16.38.25
    Author     : liliana
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="myCLasses.*,java.util.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Mail Service</title>
    </head>
    <body>
        <h1>Mail Service: <%=session.getAttribute("userID") %> </h1>
        <h2>E-mail message</h2>
        <h2> From: utntest2@gmail.com</h2>
        <form action="/UserCenteredCloud/Controller" method="POST"/>
        <p>Cc.: <textarea name="cc." rows="2" cols="21" readonly="readonly">
<%= EventUtilities.genString(ConfigurationSettings.mailReceivers) %>
        </textarea></p>
        <p>subject: <input type="text" name="subject" value="RE: argomento" /></p>
        <p><textarea name="body" rows="6" cols="60">
> qui puoi inserire il testo del messaggio ricevuto
        </textarea></p>


        <p><input type="submit" name="operation" value="send"></p>
    </body>
</html>
