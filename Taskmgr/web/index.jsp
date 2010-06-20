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
            var allusers = new Array();
            var z = 0;
            var rect = 0;
            var archi = 0;
            var blocksid = new Array();
            var blockstatus = new Array();
            var loadjson = "";

            function timedCount()
            {
                document.getElementById('txt').value=c;
                nuovaVersione = 1;
                loadDiagram(null);
                loadNotifiche();
                c=c+1;
                t=setTimeout("timedCount()",20000);
            }
            function setArray(recipients)
            {

                allusers = recipients;

                return allusers;
            }

            function cleanArray()
            {
                var size = allusers.length;
                for(var z=0;z<size;z++)
                {
                    allusers.shift();
                    
                }

                return allusers;
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
                    var diagramName = document.getElementById('area').value;
                    objXml.open("GET",url2,true);
                    objXml.setRequestHeader('Content-Type',"text/plain");
                    objXml.setRequestHeader('notifica',"start");
                    objXml.setRequestHeader('filenamemio',diagramName);
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
               // alert(rect);
                var jsonString = Jalava.diagram.persist();
                var persisted = JSON.parse(jsonString);
                var blocks = persisted.blocks;
                var startcount = 0;
                var endcount = 0;
                for(var i=0;i<blocks.length;i++) {
                    var type = blocks[i].type;
                    if(type == "Start") startcount ++
                    else if(type == "End") endcount ++
                }
                if(startcount != 1) {alert("You must have only one Start Element");return};
                if(endcount == 0) {alert("No End Element: at least one");return};
                var diagramName = document.getElementById('area').value;
                var owner = document.getElementById('owner').value;
                var users = document.getElementById('users').value;
                var writers = document.getElementById('writers').value;
                if (diagramName.length <= 1) {
                    alert("Missing diagram name");
                    return;
                }
                else if(diagramName.indexOf("template")!=-1)
                {


                }
                var url1 = "./SaveServlet";
                
                objXml = new XMLHttpRequest();
                objXml.open("POST",url1,false);
                objXml.setRequestHeader('Content-Type',"text/plain");
                objXml.setRequestHeader('filenamemio',diagramName);
                objXml.setRequestHeader('owner',owner);
                objXml.setRequestHeader('users',users);
                objXml.setRequestHeader('writers',writers);
              //  objXml.setRequestHeader('blocks',rect);
              //  objXml.setRequestHeader('connections',loadjson);
                objXml.setRequestHeader('loadjson',loadjson);
        
                
                objXml.send(jsonString);
                str = objXml.responseText;
                confirm("RISP from server: " +str);
            }


            function saveTemplate(param){
                // if (window.event.type == "click") {
                // alert(param != 0);
                //   }
                // var url1 = "http://marinoflow.appspot.com/nuovastr.txt";
                var jsonString = Jalava.diagram.persist();
                var persisted = JSON.parse(jsonString);
                var blocks = persisted.blocks;
                var startcount = 0;
                var endcount = 0;
                for(var i=0;i<blocks.length;i++) {

                    var type = blocks[i].type;
                    if(type == "Start") startcount ++
                    else if(type == "End") endcount ++
                }
                if(startcount != 1) {alert("No Start Element: you must use one");return};
                if(endcount == 0) {alert("No End Element: at least one");return};
                var diagramName = document.getElementById('area').value;
                var owner = document.getElementById('owner').value;
                var users = document.getElementById('users').value;
                var writers = document.getElementById('writers').value;
                if (diagramName.length ==0) {
                    alert("Missing diagram name");
                    return;
                }
                diagramName = "template_" + diagramName;
                alert(diagramName);
                var url1 = "./SaveServlet";
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
                        //     alert(str);
                            loadjson = objXml.responseText;
                      //      alert(loadjson);
                            people = objXml.getResponseHeader("people");
                            writers = objXml.getResponseHeader("writers");
                            if(str=="DIAGRAMMA NON TROVATO") {alert("DIAGRAMMA NON TROVATO");return}
                            
                            try{


                            var persisted = JSON.parse(str);
                            rect = persisted.blocks.length;
                            archi = persisted.connections.length;
                            var z =0;
                            for(var j=0;j<rect;j++)
                            {
                                if(persisted.blocks[j].imageId == "rect")
                                {
                                    var status = persisted.blocks[j].type;
                                    var id = persisted.blocks[j].id;
                                    blocksid[z] = id;
                                    blockstatus[z] = status;
                                    z++;
                                }
                                

                                var persisted = JSON.parse(str);
                                var blocks = persisted.blocks;


                            }
                               

                           

                           
                                document.getElementById('users').value =  people;
                                document.getElementById('writers').value = writers;

                               

                           
                                update(str);
                            }catch(e){alert("Non hai i permessi necessari per il Load");}
                        } else {}

                    }
                };
               
                objXml.open("GET",url1,true);
                objXml.setRequestHeader('Content-Type',"text/plain");
                objXml.setRequestHeader('filenamemio',diagramName);
                var owner = document.getElementById('owner').value;
                objXml.setRequestHeader('owner',owner);
                if (param == null) {
                    objXml.setRequestHeader('refresh',"true");
                }
                objXml.send(null);
               
            }


            












            function dado(){
                var jsonString = Jalava.diagram.persist();
                confirm(blocksid.toString());
                confirm(blockstatus.toString());
                confirm(jsonString);
            }


            // initialise Jalava here
            function initJalava(){
                Jalava.diagram = new Diagram(220, 100, "550", "600");
                var palette = new Palette(new FlowChartPaletteFactory(), 0, 100, 200);
                palette.addItem("rect", "Task",  Palette.DRAG_TOOL, "./img/rect.gif");
                palette.addItem("connection", "Connection",  Palette.CLICK_TOOL, "./img/line.gif");
                palette.addItem("diamond", "Decision", Palette.DRAG_TOOL, "./img/diamond.gif");
                palette.addItem("parallel", "Input/Output", Palette.DRAG_TOOL, "./img/parallel.gif");
                palette.addItem("ellipse", "And/Or", Palette.DRAG_TOOL, "./img/ellipse.gif");
                palette.addItem("rounded", "Start/End", Palette.DRAG_TOOL, "./img/ellipse.gif");
                Jalava.propertyPage = new FlowChartPropertyPage(0, 290, 10);
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


             //   String email = request.getParameter("email");
                   String email = "fabrizio.torretta@gmail.com";

               
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
        <input type="button" value="saveTemplate" name="buttonTemplate" onClick="aa=1000; saveTemplate(aa);"/>
        <input type="text" id="owner" name="owner" value= "fabrizio.torretta@gmail.com" disabled="disabled" />
        <input type="text" id="users" name="users" value= "" disabled="disabled" />
        <input type="text" id="writers" name="writers" value= "" disabled="disabled" />
        <a href="#" onclick="childWindow=open('/addpriv.html','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800')" id="add" name="add" >Change privileges of users </a>






    </form>

</body>
</html>
