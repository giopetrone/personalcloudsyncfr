<%-- 
    Document   : notifications
    Created on : 3-feb-2010, 16.53.04
    Author     : liliana
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import = "myCLasses.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<% myCLasses.NotificationMgr nm = (NotificationMgr)session.getAttribute("notifMgr");
   String fromPage = request.getParameter("fromPage"); %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Notification Service</title>
    </head>
    <body>

        <h1>Notification Service: <%= session.getAttribute("userID")%></h1>
        <%= nm.getFormattedNotifications() %>

        <% if (fromPage!=null && fromPage.equalsIgnoreCase("menu")) {
            %>
        <form action="/UserCenteredCloud/index.jsp" method="POST"/>
        <% } else { %>
        <form action="http://www.google.it/ig" method="GET"/>
        <% } %>
        <p><input type="submit" name="submit" value="OK"></p>
    </body>
</html>
