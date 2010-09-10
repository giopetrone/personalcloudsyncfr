

<%--
    Document   : index
    Created on : Mar 17, 2010, 1:29:46 PM
    Author     : Fabrizio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>




<html>
    <%


                String email = request.getParameter("email");
          //      String email = "fabrizio.torretta@gmail.com";


                String pwd = request.getParameter("pwd");
                String flow = request.getParameter("Flow");
                String sessuser =(String) session.getAttribute("email");
                String sesspwd = (String) session.getAttribute("pwd");

                if(sessuser == null)
                {
           //     String pwd = "gregorio";

    %>

    <jsp:forward page="/login.jsp">
        <jsp:param name = "Flow" value='<%=flow%>' />
    </jsp:forward>
    <%}%>
    

    
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
                     cb.id = "check"+i;
                     cb.value = value;
                     cb.checked = false;
                     cb.name = "checkgroup";
                     var select = document.createElement("select")
                     var text = document.createTextNode(value);
                     var br = document.createElement("br");
                     var opt=document.createElement("option");
                     var opt1=document.createElement("option");
                     var opt2=document.createElement("option");
                     opt.text=("All");
                     opt.value=("All");
                     select[0] =opt;
                     opt1.text=("Change status of task");
                     opt1.value=("Changestatusoftask");
                     select[1] =opt1;
                     opt2.text=("Workflow is done");
                     opt2.value=("workflowisdone");
                     select[2]=opt2;
                     var idname = "select"+i;
                     select.setAttribute("id", idname);
                     select.setAttribute("name",idname);
                     add.appendChild(cb);
                     add.appendChild(text);
                     add.appendChild(select);
                     add.appendChild(br);
                 }

            }
            }catch(e){alert(e.message);}}

        function get(){
        try{
        var diagramName = "";

     //   alert(document.myform.radiogroup.length);
        for (var i=0; i<document.myform.checkgroup.length; i++){

                if (document.myform.checkgroup[i].checked==true)
                {
                   var notif = document.getElementById("select"+i).value;
                   diagramName += document.myform.checkgroup[i].value +"/"+notif+" ";
              

                }
            }
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
                   

                      //alert("pippo");

                objXml.send(null);
            }

            function gup( name )
            {
                name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
                var regexS = "[\\?&]"+name+"=([^&#]*)";
                var regex = new RegExp( regexS );
                var results = regex.exec( window.location.href );
                if( results == null )
                    return null; //return "";
                else
                    return results[1];
            }


            function carica(){
                
                nomeFile = gup("Flow");
                
                if (nomeFile != null && nomeFile !="undefined" && nomeFile != "") {
                  //  document.getElementById('name').value = nomeFile ;
                    loadNotifiche(nomeFile);
                    return true;
                }
                else return false;
            }


            </script>

    </head>
    <body onload="if(carica()==false){getDocs();};" style="background-color: #add8e6"/>
        <h1>Notification Settings</h1>
   
  <form name="myform" id="myform" >
        <input type="hidden" id="owner" name="owner" value= '<%=sessuser%>' disabled="disabled" />
        <input type="hidden" id="pwd" name="pwd" value='<%=sesspwd%>' disabled="disabled" />
        <input type="text" value='' name="name" id="name">
        <div id="menu">


        </div>


<input type="button" onclick="get();" value="Ok" />
<a href="logout.jsp">Log out </a>

    </form>
    </body>
</html>
