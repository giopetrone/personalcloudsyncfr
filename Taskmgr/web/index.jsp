<%-- 
    Document   : index
    Created on : Mar 17, 2010, 1:29:46 PM
    Author     : Fabrizio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>




<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
        <title>Jalava : Web-based Diagram Editor</title>
        <script language="JavaScript" src="./js/Jalava.js"></script>
        <script language="JavaScript">
        
            // pezzo timer
            // pezzo timer
            var c=0;
            var t;
            var timer_is_on=0;
            var nuovaVersione = 0;
            var notifica = 0;

            function timedCount()
            {
                document.getElementById('txt').value=c;
                nuovaVersione = 1;
                loadDiagram(null);
                loadNotifiche();
                c=c+1;
                t=setTimeout("timedCount()",20000);
            }


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
                if (notifica ==0) {
                    notifica = 1;
                    objXml.open("GET",url2,true);
                    objXml.setRequestHeader('Content-Type',"text/plain");
                    objXml.setRequestHeader('notifica',"start");
                } else {
                    objXml.open("GET",url3,true);
                    objXml.setRequestHeader('Content-Type',"text/plain");
                    objXml.setRequestHeader('notifica',"refresh");
                }
                objXml.send(null);
            }

            function doTimer()
            {
                if (!timer_is_on)
                {
                    timer_is_on=1;
                    timedCount();
                }
            }
            // fine pezzo timer
            function saveDiagram(param){
                // if (window.event.type == "click") {
                // alert(param != 0);
                //   }
                // var url1 = "http://marinoflow.appspot.com/nuovastr.txt";
                var diagramName = document.getElementById('area').value;
                var owner = document.getElementById('owner').value;
                var users = document.getElementById('users').value;
                var writers = document.getElementById('writers').value;
                if (diagramName.length ==0) {
                    alert("Missing diagram name");
                    return;
                }
                var url1 = "./SaveServlet";
                var jsonString = Jalava.diagram.persist();
                if (confirm(jsonString)) {
                    // do things if OK
                }
                objXml = new XMLHttpRequest();
                objXml.open("POST",url1,false);
                objXml.setRequestHeader('Content-Type',"text/plain");
                objXml.setRequestHeader('filenamemio',diagramName);
                objXml.setRequestHeader('owner',owner);
                objXml.setRequestHeader('users',users);
                objXml.setRequestHeader('writers',writers);
                objXml.send(jsonString);
                str = objXml.responseText;
                confirm("RISP from server: " +str);
            }

            function crepa() {
                while (Jalava.diagram.container.childNodes.length >= 1 ){
                    Jalava.diagram.container.removeChild( Jalava.diagram.container.firstChild );
                }
                Jalava.diagram = new Diagram(220, 100, "550", "600");
            }

            function update(str){
                //    var msg = "update nuova stringa ? ";
                //    alert(msg + "|" + str + "|");
                if (nuovaVersione  == 1) {
                    nuovaVersione = 0;
                    if (str.length == 0) {
                        return;
                    }
                    var answer = confirm("vuoi nuova versione?");
                    if (answer){
                        crepa();
                        Jalava.diagram.load(str);
                    }
                } else {
                    crepa();
                    Jalava.diagram.load(str);
                }
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

               // if webapp loade3d with parameter 'flow''
               // eg calling like: http:.........?flow=ccc.txt
               // that filw is used as source

            function carica(){
                //  alert("in carica");
                nomeFile = gup("flow");
                if (nomeFile != null) {
                    document.getElementById('area').value = nomeFile ;
                    loadDiagram(100);
                }
            }
            
        

            function loadDiagram(param){
                var diagramName = document.getElementById('area').value;
                if (diagramName.length ==0) {
                    alert("Missing diagram name");
                    return;
                }

                //  confirm("ciclico? "+ param == null);

                var url1 = "./LoadServlet";
                objXml = new XMLHttpRequest();
                objXml.onreadystatechange  = function()
                {
                    if(objXml.readyState  == 4)
                    {
                        if(objXml.status  == 200) {
                            str = objXml.responseText;
                            try{
                                var persisted = JSON.parse(str);
                                var blocks = persisted.blocks;
                                var users = blocks[0].shared;
                                var writers = blocks[0].writers;
                           
                                document.getElementById('users').value =  users;
                                document.getElementById('writers').value = writers;
                           
                                update(str);
                            }catch(e){alert(e.message);}
                        } else {}

                    }
                };
               
                objXml.open("GET",url1,true);
                objXml.setRequestHeader('Content-Type',"text/plain");
                objXml.setRequestHeader('filenamemio',diagramName);
                if (param == null) {
                    objXml.setRequestHeader('refresh',"true");
                }
                objXml.send(null);
               
            }


            function loadLocal(){


                //  confirm("ciclico? "+ param == null);

                var url1 = "./SubServlet";
                objXml = new XMLHttpRequest();
                objXml.onreadystatechange  = function()
                {
                    if(objXml.readyState  == 4)
                    {
                        if(objXml.status  == 200) {
                            str = objXml.responseText;
                            update(str);
                        } else {}

                    }
                };
                objXml.open("GET",url1,true);
                objXml.setRequestHeader('Content-Type',"text/plain");
                objXml.setRequestHeader('filenamemio');
                if (param == null) {
                    objXml.setRequestHeader('refresh',"true");
                }
                objXml.send(null);
            }














            function dodo(){
                var url = "/nstr.txt";

                objXml = new XMLHttpRequest();
                objXml.open("GET",url,false);
                objXml.send(null);
                str = objXml.responseText;
                confirm("ECCO " +str);
                Jalava.diagram.load(str);
            }

            function dada(){
                var url1 = "./nuovastr.txt";
                var jsonString = Jalava.diagram.persist();
                if (confirm(jsonString)) {
                    // do things if OK
                }
                objXml = new XMLHttpRequest();
                objXml.open("POST",url1,false);
                objXml.setRequestHeader('Content-Type',"text/plain");
                objXml.send(jsonString);
                str = objXml.responseText;
                confirm("RISP: " +str);
            }

            function dado(){
                var jsonString = Jalava.diagram.persist();
                confirm(jsonString);
            }


            // initialise Jalava here
            function initJalava(){
                Jalava.diagram = new Diagram(220, 100, "550", "600");
                var palette = new Palette(new FlowChartPaletteFactory(), 0, 100, 200);
                palette.addItem("rect", "Processing",  Palette.DRAG_TOOL, "./img/rect.gif");
                palette.addItem("connection", "Connection",  Palette.CLICK_TOOL, "./img/line.gif");
                palette.addItem("diamond", "Decision", Palette.DRAG_TOOL, "./img/diamond.gif");
                palette.addItem("parallel", "Input/Output", Palette.DRAG_TOOL, "./img/parallel.gif");
                palette.addItem("ellipse", "And/Or", Palette.DRAG_TOOL, "./img/ellipse.gif");
                palette.addItem("rounded", "Start/End", Palette.DRAG_TOOL, "./img/ellipse.gif");
                Jalava.propertyPage = new FlowChartPropertyPage(20, 240, 10);
            }

            // start Jalava
            Jalava.addModule("FlowChartPaletteFactory");
            Jalava.addModule("FlowChartPropertyPage");
            Jalava.addModule("UrlGradientBlock");

            Jalava.start(initJalava);

        </script>

    </head>
    <body onload="carica();"/>
    <%

                String email = request.getParameter("email");

                String passWord = request.getParameter("passWord");

    %>

    <form name="saveandload" id="saveandload" >

        <textarea rows="1" cols="5" name="area" id="area"> </textarea>
        <input type="button" value="saveDiagram" name="buttonSave" onClick="aa=1000; saveDiagram(aa);"/>
        <!--INPUT type="button" value="provalocale" name="buttonprova" onClick="dada();"-->
        <input type="button" value="loadDiagram" name="buttonLoad" onClick="aa=1000;loadDiagram(aa);"/>
        <input type="text" id="diagramName" name ="diagramName" value=""    />
        <input type="button" value="Start count!" onClick="doTimer();"/>
        <input type="text" id="txt" />
        <input type="button" value="See JSON" onclick="dado();TextEdit.check();TextEdit.setOwner();"/>
        <input type="button" value="Share" onclick="childWindow=open('/shared.html','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800');"/>
        <input id="fileUtente" name="fileUtente" type="file" size="20"/>
        <input type="text" id="owner" name="owner" value= "<%=email%>" disabled="disabled" />
        <input type="text" id="users" name="users" value= "" disabled="disabled" />
        <input type="text" id="writers" name="writers" value= "" disabled="disabled" />






    </form>

</body>
</html>
