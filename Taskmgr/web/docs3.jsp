<%--
    Document   : docs
    Created on : Jun 21, 2010, 4:21:58 PM
    Author     : fabrizio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="documentwatcher.GoDoc"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Save the diagram as a Template</title>
        <script type="text/javascript">
        function get(){
        var diagram;
     //   alert(document.myform.radiogroup.length);
        for (var i=0; i<document.myform.radiogroup.length; i++){

                if (document.myform.radiogroup[i].checked==true){
                   diagram = document.myform.radiogroup[i].value;


                }}
            // alert(diagram);
             opener.document.getElementById('area').value = diagram;
        }

        function get2()
        {
            var diagram;
     //   alert(document.myform.radiogroup.length);
        var name = document.getElementById("name").value;

        if(name == "Save as new" || name == "")
        {
           /* for (var i=0; i<document.myform.radiogroup.length; i++){

                if (document.myform.radiogroup[i].checked==true){
                   diagram = document.myform.radiogroup[i].value;
                   document.getElementById("name").value = diagram;


                }}
            opener.document.getElementById('area').value  = diagram;
             opener.saveDiagram("true");
             */
            alert("Choose a name for the diagram");

        }
        else
        {
            if(name.indexOf(".txt") == -1) name = name +".txt";
            opener.document.getElementById('area').value  = name;
            opener.saveDiagram("true");
        }

        }

        function getname()
        {
            var name =opener.sendname();

            if(name == "" || name == null) document.myform.name.value = "Save as new template"
            else {

                if(name.indexOf("template")==-1) document.myform.name.value = "template_"+name;

            }
        }


                 </script>


    </head>
    <body style="background-color: #add8e6" onload="getname();">
        <h1>Save the diagram as a Template</h1>
        <form id="myform" name="myform">
        <input type="text" id="name" name="name" value="" onclick="document.myform.name.value=''"/>


        </form>
<input type="button" onclick="get2();self.close();" value="Ok"/>

    </body>
</html>