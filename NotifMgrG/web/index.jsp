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
        <title>Notification Manager</title>
                <script language="JavaScript">
function getDocs(){
  try{
                    var url1 = "./PrendiDocs";
                    var email = document.getElementById('owner').value;
                    var pwd = document.getElementById('pwd').value
               //     var email = "fabrizio.torretta@gmail.com";
               //     var pwd = "gregorio";

                    objXml = new XMLHttpRequest();

                    objXml.open("POST",url1,false);

                    objXml.setRequestHeader('Content-Type', "text/plain;charset=UTF-8");


                    objXml.setRequestHeader('email',email);
                    objXml.setRequestHeader('pwd',pwd);
                    objXml.send(null);
                    str = objXml.responseText;
                   // confirm("LISTA: " +str);
                    var array = str.split(",");
                    var size = array.length;
                    var add = document.getElementById("menu");

                    for(var i=0;i<size;i++)
            {
                 var cb = document.createElement( "input" );
                 var value = array[i];
                 if(value != "")
                 {
                     cb.type = "checkbox";
                     cb.id = value;
                     cb.value = value;
                     cb.checked = false;
                     cb.name = "checkgroup";
                     var text = document.createTextNode(value);
                     var br = document.createElement("br");

                     add.appendChild(cb);
                     add.appendChild(text);
                     add.appendChild(br);
                 }

            }
            }catch(e){alert(e.message);}}

        function get(){
        try{
        var diagramName = "";

     //   alert(document.myform.radiogroup.length);
        for (var i=0; i<document.myform.checkgroup.length; i++){

                if (document.myform.checkgroup[i].checked==true){
                   diagramName += document.myform.checkgroup[i].value +" ";


                }}
             document.getElementById('name').value = diagramName;
             loadNotifiche(diagramName);
              //opener.loadDiagram(diagram);
         }
         catch(e){alert(e.message);}
        }



   function loadNotifiche(diagramName){

                var email = document.getElementById('owner').value;
                var pwd = document.getElementById('pwd').value
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


                    objXml.open("GET",url2,true);
                    objXml.setRequestHeader('Content-Type',"text/plain");
                    objXml.setRequestHeader('notifica',"start");
                    objXml.setRequestHeader('filenamemio',diagramName);
                    objXml.setRequestHeader('email',email);
                    objXml.setRequestHeader('pwd',pwd);

                      //alert("pippo");

                objXml.send(null);
            }
            </script>

    </head>
    <body onload="getDocs();" style="background-color: #add8e6"/>
        <h1>Notification Settings</h1>
          <%


                   String email = request.getParameter("email");
          //      String email = "fabrizio.torretta@gmail.com";


                String pwd = request.getParameter("pwd");
           //     String pwd = "gregorio";

    %>
  <form name="myform" id="myform" >
        <input type="hidden" id="owner" name="owner" value= '<%=email%>' disabled="disabled" />
        <input type="hidden" id="pwd" name="pwd" value='<%=pwd%>' disabled="disabled" />
        <input type="text" value="" name="name" id="name">
        <div id="menu">


        </div>


<input type="button" onclick="get();" value="Ok" />

    </form>
    </body>
</html>
