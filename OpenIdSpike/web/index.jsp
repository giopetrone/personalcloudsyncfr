<%-- 
    Document   : index
    Created on : Jan 25, 2010, 9:13:44 AM
    Author     : giovanna
--%>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page session="true" %>
<html>
<body>
<%
    if (request.getParameter("logout")!=null)
    {
        session.removeAttribute("openid");
        session.removeAttribute("openid-claimed");
%>
    Logged out!<p>
<%
    }
	if (session.getAttribute("openid")==null) {
%>
<form method="POST" action="consumer_redirect.jsp">
<strong>OpenID:</strong>
<input type="text" name="openid" width="30"/><br>
<input type="submit"/>
</form>
<%
} else {

%>
Logged in as <%= session.getAttribute("openid") %><p>
<a href="?logout=true">Log out</a>

<% } %>
</body>
</html>
