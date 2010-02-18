<%-- 
    Document   : index
    Created on : 1-feb-2010, 15.21.01
    Author     : liliana
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My Web Desktop</title>
    </head>
    <body>
       <form action="/UserCenteredCloud/Controller" method="POST"/>
       <h1> User Centered Cloud </h1>

       <input type="hidden" name="fromPage" value="menu" />
       <!-- fromPage used to distinguish cases where notifications.jsp
            is invoked from index.jsp from cases where it is invoked
            from IM window -->

      <p><input type="submit" name="operation" value="Mail Service"></p>
      <p><input type="submit" name="operation" value="Doc Sharing"></p>
      <p><input type="submit" name="operation" value="Group Calendar"></p>
      <p><input type="submit" name="operation" value="Notifications"></p>
      <p><input type="submit" name="operation" value="Quit"></p>
    </body>
</html>

