<%-- 
    Document   : home3
    Created on : Sep 7, 2010, 10:28:18 AM
    Author     : fabrizio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<%
    String email = request.getParameter("email");
    String pwd = request.getParameter("pwd");
    session.setAttribute("email", email);
    session.setAttribute("pwd",pwd);
%>


    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login with sucessfull</title>
    </head>
     <body onLoad="window.location.href='index.jsp'">
      
        <A HREF="index.jsp">Continue....</A>
    </body>
</html>
