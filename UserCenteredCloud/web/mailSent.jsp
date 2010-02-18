<%-- 
    Document   : mailSent
    Created on : 2-feb-2010, 18.11.31
    Author     : liliana
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%!
public String summary() {
        String url = "http://localhost:8080/UserCenteredCloud/notifications.jsp";
        return "You have unread notifications <br> " +
                    "<A HREF = " + url + ">view notifications</A>";
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Mail Sent</title>
    </head>
    <body>
        <!-- PROVA DI INVOC NOTIF MGR -->
      <!--  <%= summary() %> -->

        <h1>Mail Service: <%=session.getAttribute("userID")%></h1>
        <h2>Mail <%= request.getParameter("subject") %>  sent!</h2>
        <form action="/UserCenteredCloud/index.jsp" method="POST"/>
        <p><input type="submit" name="submit" value="OK"></p>
    </body>
</html>
