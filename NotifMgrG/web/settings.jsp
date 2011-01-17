<%-- 
    Document   : settings
    Created on : Sep 11, 2010, 11:05:44 AM
    Author     : fabrizio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
  <%



                String flow = request.getParameter("Flow");
              
                
                String sessuser =(String) session.getAttribute("email");
    if(flow == null){  %>
   <jsp:forward page="/login.jsp"/>


   <%}else if(sessuser == null)
                {%>
         

  

    <jsp:forward page="/login.jsp">
        <jsp:param name = "Flow" value='<%=flow%>' />
    </jsp:forward>
    <%}%>
<html>
    <head>
        <script text="Javascript">
        function get()
        {

        try
        {

        var diagramName = "";
        var flowname = document.getElementById("flow").value;
        var notif = "";
        if(document.myform.settingsgroup[0].checked == true) notif="All";
        else
        {
            for (var i=1; i<document.myform.settingsgroup.length; i++)
            {
                if (document.myform.settingsgroup[i].checked==true)
                {
                   notif += document.myform.settingsgroup[i].value;
                }
            }

        }
   
        diagramName = flowname+"/"+notif;
        document.getElementById('name').value = diagramName;
        loadNotifiche(diagramName);
              //opener.loadDiagram(diagram);
        }
        catch(e){alert(e.message);}
        }

             function loadNotifiche(diagramName)
             {

                var email = document.getElementById('user').value;
                //var pwd = document.getElementById('pwd').value
                var url2 = "./SubscribeServlet";
               
                objXml = new XMLHttpRequest();
                objXml.onreadystatechange  = function()
                {
                    if(objXml.readyState  == 4)
                    {
                        if(objXml.status  == 200) {
                            str = objXml.responseText;
                            alert(str);
                         //   document.getElementById("link").click();
                           
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

            function setAll()
            {
               

                for (var i=1; i<document.myform.settingsgroup.length; i++)
                {
                    if(document.myform.settingsgroup[0].checked ==true)
                    {
                   
                        document.myform.settingsgroup[i].checked =true;
                    }
                    else document.myform.settingsgroup[i].checked = false;
                }
            }


        </script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Settings of Flow: <%=flow%></title>
    </head>
    <body style="background-color: #add8e6">
         <div id="menu" style="font-size: 13px">

        <%=sessuser%> | <u>Settings</u> | <a target=_blank href="http://docs.google.com/support/?hl=en" class=gb4>Help</a> |<a href="http://localhost:8080/NotifMgrG/index.jsp" id="link">Home page</a>| <a href="logout.jsp" class=gb4 >Log out </a> </div>
        <h1>Settings of Flow: <%=flow%></h1>
        <form name="myform" method="get">
            <input type="hidden" value='' name="name" id="name"/>
            <input type="hidden" value='<%=flow%>' name="flow" id="flow"/>
            <input type="hidden" value='<%=sessuser%>' id="user" name="user"/>
<table border=0>
<tr>
    <th align=right>
	<INPUT	TYPE	= "checkbox"
		NAME	= "settingsgroup"
                id=      "all"
                onclick = "setAll();"
		VALUE	= "All" >
			   All Notifications
    </th>
    <th align=center>
	<INPUT	TYPE	= "checkbox"
		NAME	= "settingsgroup"
		VALUE	= "Workflowisdone">
			   Workflow Is done
    </th>
    <th align=left>
	<INPUT	TYPE	= "checkbox"
		NAME	= "settingsgroup"
		VALUE	= "Changestatusoftask">
			   Change of Task
    </th>


    <th align=left>
	<INPUT	TYPE	= "checkbox"
		NAME	= "settingsgroup"
		VALUE	= "Deletetask">
			   Deletion of Task
    </th>
  </tr>
</table>

            <input type="button" value="Subscription" onclick="get();"/>
</form>

    </body>
    
</html>
