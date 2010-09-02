<%-- 
    Document   : docs
    Created on : Jun 21, 2010, 4:21:58 PM
    Author     : fabrizio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="jav.GoDoc"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Load a diagram</title>
        <script type="text/javascript">
        function get(){
        try{
        var diagram;
     //   alert(document.myform.radiogroup.length);
        for (var i=0; i<document.myform.radiogroup.length; i++){

                if (document.myform.radiogroup[i].checked==true){
                   diagram = document.myform.radiogroup[i].value;

                    
                }}
            
             opener.document.getElementById('area').value = diagram;
             opener.loadDiagram(diagram);
         }
         catch(e){alert(e.message);}
        }

        function get2()
        {
            var diagram;
     //   alert(document.myform.radiogroup.length);
        for (var i=0; i<document.myform.radiogroup.length; i++){

                if (document.myform.radiogroup[i].checked==true){
                   diagram = document.myform.radiogroup[i].value;


                }}
            // alert(diagram);
             opener.document.getElementById('area').value = diagram;

             opener.loadDiagram(diagram);
        }




function getDocs(){
  try{
                    var url1 = "./PrendiDocs";
                    var email = opener.document.getElementById('owner').value;
                    var pwd = opener.document.getElementById('pwd').value


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
                     cb.type = "radio";
                     cb.id = value;
                     cb.value = value;
                     cb.checked = false;
                     cb.name = "radiogroup";
                     var text = document.createTextNode( value);
                     var br = document.createElement("br");

                     add.appendChild(cb);
                     add.appendChild(text);
                     add.appendChild(br);
                 }

            }
            }catch(e){alert(e.message);}}

//window.onload=setCredentials;



                 </script>
    </head>
    <body style="background-color: #add8e6" onload="getDocs();"  >
        
        <h1>Load a diagram </h1>
        <form name="myform" id="myform" >
        <div id="menu" name="menu">

            </div>
        
       
        

   
<input type="button" onclick="get();self.close();" value="Ok" />

 </form>
     
    </body>
</html>
