<%-- 
    Document   : service3
    Created on : 1-feb-2010, 16.39.27
    Author     : liliana
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="java.util.*" import="myCLasses.*,java.util.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">


<% myCLasses.UserAgt ua = (UserAgt)session.getAttribute("userAgt");
   ArrayList<String> spheres = GroupCalendar.getSpheres(ua.getUserSpheres());
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Group Calendar Service</title>
    </head>
    <body>
        <h1>Group Calendar Service: <%=session.getAttribute("userID")%></h1>


        <form action="/UserCenteredCloud/Controller" method="POST"/>

        <h2> Invited collaboration groups: </h2>
        <% for (int i=0; i<spheres.size(); i++) {
               String sph = spheres.get(i);
        %>
               <p><input type="checkbox" name="<%=sph%>" value="ON" /> <%=sph%> </p>
        <% } %>

        <h2> Subject of meeting:
            <input type="text" name="meetingTopic" value="Meeting Subject" size="20" />
        </h2>

        <h2>Time window:</h2>
        <p>
        First day:
        <input type="text" name="firstDay" value="1" size="3" />
        Last day:
        <input type="text" name="lastDay" value="31" size="3" />
        Month: <input type="text" name="month" value=<%=Calendar.MONTH%> size="3" />
        Year: <input type="text" name="year" value="2010" size="4" />
        Duration (in hours): <input type="text" name="hours" value="1" size="3" /></p>

        <p><input type="submit" name="operation" value="searchDate"></p>
    </body>
</html>
