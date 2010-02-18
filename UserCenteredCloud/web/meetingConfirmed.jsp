<%-- 
    Document   : meetingConfirmed
    Created on : 4-feb-2010, 15.31.11
    Author     : liliana
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Group Calendar Service</title>
    </head>
    <body>
        <h1>Group Calendar Service: <%=session.getAttribute("userID")%></h1>
        <h2>Meeting <%= session.getAttribute("meetingTopic") %> scheduled on
            <%=request.getParameter("meetingDate")%></h2>

        <form action="/UserCenteredCloud/index.jsp" method="POST"/>
        <p><input type="submit" name="submit" value="OK"></p>
    </body>
</html>
