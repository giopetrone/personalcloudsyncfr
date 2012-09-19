<%-- 
    Document   : index
    Created on : 19-set-2012, 13.14.16
    Author     : goy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Prova AJAX</title>     
   <script>
     function setXMLHttpRequest() {
       var xhr = null;
       // browser standard con supporto nativo
       if (window.XMLHttpRequest) {
         xhr = new XMLHttpRequest();
       }
       else if (window.ActiveXObject) {
         xhr = new ActiveXObject("Microsoft.XMLHTTP");
       }
       return xhr;
     }

     var xhrObj = setXMLHttpRequest();

     function callServerCity(c) {
       var url = "getCapServlet?city="+c;
       xhrObj.open("GET", url, true);
       xhrObj.onreadystatechange = updatePage;
       xhrObj.send(null);
     }
     function updatePage() {
       if (xhrObj.readyState == 4) {
         var risp = xhrObj.responseText;
         document.getElementById("cap").value = risp;
       }
     }
   </script>
    </head>
    <body>
        <h1>Hello World!</h1>
<FORM METHOD="GET" ACTION="login">
 <P ALIGN="CENTER">
  Città: <INPUT TYPE="TEXT" ID="citta" NAME="citta" SIZE="30" onChange="callServerCity(this.value);">
  <BR>
  CAP: <INPUT TYPE="TEXT" ID="cap" NAME="cap" SIZE="30">
  <BR><BR>
  <INPUT TYPE="Submit" VALUE="OK">
 </P>
</FORM>
    </body>
</html>
