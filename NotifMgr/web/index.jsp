<%-- 
    Document   : index
    Created on : Jun 17, 2010, 11:03:34 AM
    Author     : giovanna
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
                <script language="JavaScript">

   function loadNotifiche(){

                var url2 = "./SubscribeServlet";
                var url3 = "./CallbackServlet/subscribe/pippo";
                objXml = new XMLHttpRequest();
                objXml.onreadystatechange  = function()
                {
                    if(objXml.readyState  == 4)
                    {
                        if(objXml.status  == 200) {
                            str = objXml.responseText;
                            alert(str);
                        } else {}

                    }
                };
                
                    var diagramName = document.getElementById('area').value;
                    objXml.open("GET",url2,true);
                    objXml.setRequestHeader('Content-Type',"text/plain");
                    objXml.setRequestHeader('notifica',"start");
                    //objXml.setRequestHeader('filenamemio',diagramName);
                      objXml.setRequestHeader('filenamemio',"abc.txt");
                      alert("pippo");

                objXml.send(null);
            }
            </script>

    </head>
    <body onload="loadNotifiche();"/>
        <h1>Hello World!</h1>
  <form name="saveandload" id="saveandload" >
       <input type="text" id="owner" name="owner" value= "fabrizio.torretta@gmail.com" disabled="disabled" />

        <textarea rows="1" cols="5" name="area" id="area"> </textarea>
         




    </form>
    </body>
</html>
