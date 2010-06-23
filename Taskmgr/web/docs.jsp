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
        for (var i=0; i<document.myform.radiogroup.length; i++){

                if (document.myform.radiogroup[i].checked==true){
                   diagram = document.myform.radiogroup[i].value;


                }}
            // alert(diagram);
             opener.document.getElementById('area').value = diagram;
             opener.loadDiagram(diagram);
        }


                 </script>
    </head>
    <body>
        <h1>Load a diagram</h1>
        <form id="myform" name="myform">

<%
        String docs = jav.GoDoc.prendi();
        String[] diagramdocs;
        String delimiter = ",";
        diagramdocs = docs.split(delimiter);
        
        for(int i=0;i<diagramdocs.length;i++)
        {
            String doc = diagramdocs[i];
            if(doc.contains(".txt")){
%>
<input type="radio" id='radiogroup' name="radiogroup" value='<%=doc%>' /><%=doc%>
<br>
<%}}%>
        </form>
<input type="button" onclick="get2();self.close();" value="Ok"/>
     
    </body>
</html>
