<%-- 
    Document   : meetingDates
    Created on : 4-feb-2010, 14.04.45
    Author     : liliana
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="myCLasses.*,java.util.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<% myCLasses.UserAgt ua = (UserAgt)session.getAttribute("userAgt");
   ArrayList<String> selectedSphs = (ArrayList)session.getAttribute("spheresForMeeting");

   String invited = selectedSphs.toString();

   String[] options = GroupCalendar.getPossibleDates(request.getParameter("firstDay"),
                    request.getParameter("lastDay"), request.getParameter("month"),
                    request.getParameter("year"), request.getParameter("hours"));
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Group Calendar Service</title>
    </head>
    <body>
        <h1>Group Calendar Service: <%=session.getAttribute("userID")%></h1>
        <h2> Meeting: <%= request.getParameter("meetingTopic") %></h2>
        <h2> Invited collaboration groups: <%= invited %></h2>
        <h2> Possible dates: </h2>

        <form action="/UserCenteredCloud/Controller" method="POST"/>

        <p><input type="radio" name="meetingDate" value=<%= options[0] %> checked="checked" />
            <%= options[0] %></p>
        <p><input type="radio" name="meetingDate" value=<%= options[1] %> />
            <%= options[1] %></p>
            
        <p><input type="submit" name="operation" value="select date"></p>

    </body>
</html>
