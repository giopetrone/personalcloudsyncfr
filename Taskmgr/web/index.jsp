<%-- 
    Document   : index
    Created on : Mar 17, 2010, 1:29:46 PM
    Author     : Fabrizio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="documentwatcher.GoDoc;"%>




<html>
    <%


                String email = request.getParameter("email");



                String pwd = request.getParameter("pwd");
                String flow = request.getParameter("Flow");
                String sessuser = (String) session.getAttribute("email");
                String sesspwd = (String) session.getAttribute("pwd");

                if (sessuser == null) {


    %>

    <jsp:forward page="login.jsp">
        <jsp:param name = "Flow" value='<%=flow%>' />
    </jsp:forward>
    <%}%>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
        <title>Collaborative Task Manager</title>

        <script language="JavaScript" src="./js/Jalava.js"></script>

        <script language="JavaScript">
        
            // pezzo timer
            // pezzo timer
            var nome;
            var c = 0;
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
            var versioneOriginale = "{\"connections\":[],\"blocks\":[]}"; // vuota
            // pezzi per creare oggetto DeltaGrafico da spedire a servlet
            var primoPezzo = "{\"vecchio\":";
            var secondoPezzo = ",\"nuovo\":";
            var terzoPezzo = "}";
          
            function timedCount()
            {
                document.getElementById('txt').value=c;
                nuovaVersione = 1;
                //  loadDiagram(null);
                // loadNotifiche();
                checkVersion();
                c=c+1;
                t=setTimeout("timedCount()",20000);
            }

            function checkVersion()
            {

                var url = "./CheckVersion";
                objXml = new XMLHttpRequest();
                objXml.onreadystatechange  = function()
                {
                    if(objXml.readyState  == 4)
                    {
                        if(objXml.status  == 200) {
                            str = objXml.responseText;

                           
                            if(str == "new")
                            {
                                confirm("Do you want a new version?");
                                if(confirm)
                                {
                                    var diagname = document.getElementById('area').value;
                                    var notification = document.getElementById("notification").value;
                                    if (notification == "remote") {
                                        window.location = "http://www.piemonte.di.unito.it/TaskMgr/login.jsp?Flow="+diagname;}
                                    else {  window.location = "http://localhost:8080/login.jsp?Flow="+diagname; }

                                    //loadDiagram(document.getElementById('area').value);
                                    //  window.location = link;
                                }
                            }
                        } else {}

                    }
                };
                
                var diagramName = document.getElementById('area').value;

                var user = document.getElementById('owner').value;
                var pwd = document.getElementById('pwd').value;
                if(diagramName != "" && diagramName != null)
                {
                    objXml.open("GET",url,true);
                    objXml.setRequestHeader('Content-Type',"text/plain");
                    objXml.setRequestHeader('user',user);
                    objXml.setRequestHeader('filenamemio',diagramName);
                    objXml.setRequestHeader('pwd',pwd);


                }

                objXml.send(null);







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
                    
                    var diagramName = document.getElementById('area').value;
                    if(diagramName != "" && diagramName != null)
                    {
                        notifica = 1;
                        objXml.open("GET",url2,true);
                        objXml.setRequestHeader('Content-Type',"text/plain");
                        objXml.setRequestHeader('notifica',"start");
                        objXml.setRequestHeader('filenamemio',diagramName);
                    }
                } else
                {

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
            function saveDiagram(publish){
                // if (window.event.type == "click") {
                // alert(param != 0);
                //   }
                // var url1 = "http://marinoflow.appspot.com/nuovastr.txt";
                // alert(rect);
                c = 1;
               
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
                //   alert(param);
                // var diagramName = param;
                //   if(diagramName.indexOf(".txt") == -1) diagramName = diagramName + ".txt";
                //  alert(diagramName);
                var owner = document.getElementById('owner').value;
                var users = document.getElementById('users').value;
                var writers = document.getElementById('writers').value;
                var pwd = document.getElementById('pwd').value;
                var assignees = document.getElementById("assignees").value;
                var notification = document.getElementById("notification").value;
                if (diagramName.length <= 1) {
                    alert("Missing diagram name");
                    return;
                }
                else if(diagramName.indexOf("template")!=-1)
                {


                }
                try{
                    var url1 = "./SaveServlet";
              
                    var a = loadjson.substring(0, 4000);
                    var b = loadjson.substring(4000, loadjson.length);
                    var old = a+b;
              
                    objXml = new XMLHttpRequest();
                
                    objXml.open("POST",url1,false);
                
                    objXml.setRequestHeader('Content-Type', "text/plain;charset=UTF-8");

                    objXml.setRequestHeader('filenamemio',diagramName);
                    objXml.setRequestHeader('owner',owner);
                    objXml.setRequestHeader('users',users);
                    objXml.setRequestHeader('writers',writers);
                    objXml.setRequestHeader('pwd',pwd);
                    objXml.setRequestHeader('assignees', assignees);
                    objXml.setRequestHeader('notification', notification);
                    if (publish == "true") {
                        objXml.setRequestHeader('publish', "true");
                    }
                    //     objXml.setRequestHeader('a',a);
                    //     objXml.setRequestHeader('b',b);

                    //  objXml.setRequestHeader('blocks',rect);
                    //    objXml.setRequestHeader('loadjson',loadjson);
                
        
                    var deltaString = primoPezzo + versioneOriginale + secondoPezzo + jsonString + terzoPezzo;
                    
                    objXml.send(deltaString);
                    
                    str = objXml.responseText;
                    confirm("Salvato il grafico: " +str);
                    document.body.style.cursor = "default";
                    var link = document.createElement("a");
                    var link2 = document.createElement("a");
                    var add = document.getElementById("menu2");
                    var h4 = document.createElement("h4");
                    var subtitle = document.createTextNode(str+" ");
                    while(add.childNodes.length>=1)
                    {
                        add.removeChild(add.firstChild);
                    }
                    h4.appendChild(subtitle);
                    add.appendChild(h4);
                    var notification = document.getElementById("notification").value;
                    var urlfeed= "";
                    var urlnotif = " ";
                    if (notification == "remote") {
                        urlfeed = "http://www.piemonte.di.unito.it/Flow/"+str+".xml";
                        urlnotif = "http://www.piemonte.di.unito.it/NotifMgrG/settings.jsp?Flow="+str;}

                    else {urlfeed = "/var/www/Flow/"+str+".xml";
                        urlnotif = "http://localhost:8080/NotifMgrG/settings.jsp?Flow="+str;}
                    //   var urlfeed = "http://taskmanagerunito.xoom.it/Flow/"+str+".xml";
                    //var urlnotif = "http://localhost:8080/NotifMgrG/settings.jsp?Flow="+str;
                    link.setAttribute('href',urlfeed);
                    link.setAttribute('target', '_blank');
                    link2.setAttribute('href',urlnotif);
                    link2.setAttribute('target', '_blank');

                    var text = document.createTextNode("Vai al Feed ");
                    var text2 = document.createTextNode(" Notifications settings for this flow");
                   
                    
                    if(str.indexOf("template") == -1)
                    {
                       
                        var palette3 = document.getElementById("LinkNotif");
                        while(palette3.childNodes.length>=1)
                        {
                            palette3.removeChild(palette3.firstChild);
                        }
                        var palette4 = document.getElementById("LinkFeed");
                        while(palette4.childNodes.length>=1)
                        {
                            palette4.removeChild(palette4.firstChild);
                        }
                        link.appendChild(text);
                        link2.appendChild(text2);
                        palette4.appendChild(link);
                        palette3.appendChild(link2);
                        var diagname = document.getElementById('area').value;
                        // var link = "http://localhost:8080/login.jsp?Flow="+diagname;
                        var notification = document.getElementById("notification").value;
                        if (notification == "remote")
                            window.location = "http://www.piemonte.di.unito.it/TaskMgr/login.jsp?Flow="+diagname;

                        else  window.location = "http://localhost:8080/login.jsp?Flow="+diagname;
                        //loadDiagram(document.getElementById('area').value);
                       // window.location = link;
                       
                    
                    }
                   
                  

                }catch(e){alert(e.message);}
            }

            function sendname()
            {
                return nome;
            }


            function saveTemplate(param){

                try{
                    c = 1;
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
                    //    if(diagramName == null || diagramName == "") { alert("Missing diagram name");return;}

                    //     else diagramName = "template_" + diagramName;
                  
                    // var diagramName = param;
                    //   if(diagramName.indexOf(".txt") == -1) diagramName = diagramName + ".txt";
                
                    var owner = document.getElementById('owner').value;
                    var users = document.getElementById('users').value;
                    var writers = document.getElementById('writers').value;
                    var pwd = document.getElementById('pwd').value;
                
                    if (diagramName.length <= 1) {
                   
                    }
                    else if(diagramName.indexOf("template")==-1)
                    {


                    }
               
                    var url1 = "./SaveServlet";
                    alert("In save Template");
                    var a = loadjson.substring(0, 4000);
                    var b = loadjson.substring(4000, loadjson.length);
                    var old = a+b;

                    objXml = new XMLHttpRequest();

                    objXml.open("POST",url1,false);

                    objXml.setRequestHeader('Content-Type', "text/plain;charset=UTF-8");

                    objXml.setRequestHeader('filenamemio',diagramName);
                    objXml.setRequestHeader('owner',owner);
                    objXml.setRequestHeader('users',users);
                    objXml.setRequestHeader('writers',writers);
                    objXml.setRequestHeader('public', param);
                    objXml.setRequestHeader('pwd',pwd);
                    //     objXml.setRequestHeader('b',b);

                    //  objXml.setRequestHeader('blocks',rect);
                    //    objXml.setRequestHeader('loadjson',loadjson);


                    var deltaString = primoPezzo + versioneOriginale + secondoPezzo + jsonString + terzoPezzo;
                    objXml.send(deltaString);
                    str = objXml.responseText;
                    confirm("RISP from server: " +str);
                }catch(e){alert(e.message);}

            }

            function crepa() {
               
                while (Jalava.diagram.container.childNodes.length >= 1 ){
                    Jalava.diagram.container.removeChild( Jalava.diagram.container.firstChild );
                }
                Jalava.diagram = new Diagram(220, 130, "550", "600");
            }

            function update(str){
                //    var msg = "update nuova stringa ? ";
                //    alert(msg + "|" + str + "|");
                try{
                    if (nuovaVersione  == 1) {
                        nuovaVersione = 0;
                        if (str.length == 0) {
                            return;
                        }
                        //  var answer = confirm("vuoi nuova versione?");
                        //    if (answer){
                        crepa();
                        Jalava.diagram.load(str);
                        //   }
                    } else {
                        crepa();
                        //  alert(str);
                        Jalava.diagram.load(str);
                    }
                }catch(e){ alert("DENTRO UPDATE "+e.message);
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
                
                nomeFile = gup("Flow");
                if (nomeFile != null) {
                    document.getElementById('area').value = nomeFile ;
                    loadDiagram(nomeFile);
                }
            }
            
        

            function loadDiagram(param){
                
                //  var diagramName = document.getElementById('area').value;
                var diagramName = param;
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
                            try{
                                
                                str = objXml.responseText;
                                loadjson = str;
                                people = objXml.getResponseHeader("people");
                                writers = objXml.getResponseHeader("writers");
                                var nosave = objXml.getResponseHeader("nosave");
                                if(str=="DIAGRAMMA NON TROVATO") {alert("DIAGRAMMA NON TROVATO");return}
                            
                                
                                document.getElementById('users').value =  people;
                                document.getElementById('writers').value = writers;

                               

                                versioneOriginale = str;
                                update(str);
                                
                                var add = document.getElementById("menu2");
                                while(add.childNodes.length>=1)
                                {
                                    add.removeChild(add.firstChild);
                                }
                                var h4 = document.createElement("h4");
                                if(nosave != null && nosave !="") var subtitle = document.createTextNode(diagramName+" View only");
                                else var subtitle = document.createTextNode(diagramName);
                                h4.appendChild(subtitle);
                                add.appendChild(h4);
                                nome = diagramName;
                                var sav = document.getElementById("savenew");
                                var template = document.getElementById("savetemplate");
                                var share = document.getElementById("share");
                                var draft = document.getElementById("savedraft");
                                var change = document.getElementById("changepriv");
                                var diagname = document.getElementById('area').value;
                                // var link = "http://localhost:8080/login.jsp?Flow="+diagname;
                                var urlfeed ="";
                                var notification = document.getElementById("notification").value;
                                if (notification == "remote)")
                                    urlfeed = "http://www.piemonte.di.unito.it/Flow/"+diagramName+".xml";
                                else urlfeed = "/var/www/Flow/"+diagramName+".xml";
                                //  var urlfeed = "http://taskmanagerunito.xoom.it/Flow/"+diagramName+".xml";
                          
                                var urlnotif = "http://localhost:8080/NotifMgrG/settings.jsp?Flow="+diagramName;
                                var link = document.createElement("a");
                                var link2 = document.createElement("a");
                                link.setAttribute('href',urlfeed);
                                link.setAttribute('target', '_blank');
                                link2.setAttribute('href',urlnotif);
                                link2.setAttribute('target', '_blank');

                                var text = document.createTextNode("Vai al Feed ");
                                var text2 = document.createTextNode(" Notifications settings for this flow");
                                var palette3 = document.getElementById("LinkNotif");
                                while(palette3.childNodes.length>=1)
                                {
                                    palette3.removeChild(palette3.firstChild);
                                }
                                var palette4 = document.getElementById("LinkFeed");
                                while(palette4.childNodes.length>=1)
                                {
                                    palette4.removeChild(palette4.firstChild);
                                }
                                link.appendChild(text);
                                link2.appendChild(text2);
                                palette4.appendChild(link);
                                palette3.appendChild(link2);
                                document.getElementById("area").value = diagramName;
                                doTimer();
                               
                                if(nosave != null && nosave !="")
                                {
                                    //alert("You can't modify and save this diagram");
                                   
                                    
                                    // div.removeChild(sav);
                                    sav.onclick = "";
                                    draft.onclick = "";
                                    share.onclick = "";
                                    template.onclick = "";
                                    change.onclick = "";
                                    
                                   
                                }
                                else if(nosave == null)
                                {

                                    var open = "childWindow=open('docsave.html','_blank','status=1,toolbar=1,scrollbars=1,width=400,height=200')"
                                    sav.removeAttribute("onclick");
                                    sav.setAttribute("onclick", open)
                                    draft.removeAttribute("onclick");
                                    draft.setAttribute("onclick","saveDiagram('false');")
                                    share.removeAttribute("onclick");
                                    open = "childWindow=open('shared.html','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800')";
                                    share.setAttribute("onclick", open)
                                    template.removeAttribute("onclick");
                                    open = "childWindow=open('/docs3.jsp','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800')";
                                    template.setAttribute("onclick", open);
                                    change.removeAttribute("onclick");
                                    open = "childWindow=open('changepriv.html','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800')";
                                    change.setAttribute("onclick",open);
                                }
                               

                                
                            }catch(e){alert(e.message);}
                        } else {}

                    }
                };
               
                objXml.open("GET",url1,true);
                objXml.setRequestHeader('Content-Type',"text/plain");
                objXml.setRequestHeader('filenamemio',diagramName);
                var owner = document.getElementById('owner').value;
                var pwd = document.getElementById('pwd').value;
                objXml.setRequestHeader('owner',owner);
                objXml.setRequestHeader('pwd',pwd);
                if (param == null) {
                    objXml.setRequestHeader('refresh',"true");
                }
                objXml.send(null);
               
            }


            



            // METODO DI CONTROLLO SU AND OR
            function setCondition()
            {
                try{


                    // Variabile json del diagramma
                    var jsonString = Jalava.diagram.persist();
        
                    //persited: l'oggetto JSON'
                    var persisted = JSON.parse(jsonString);
                    //Numero blocchi
                    var blocks = persisted.blocks.length;
                    //Numero connessioni
                    var connections = persisted.connections.length;
                    var y=0;
                    //Cerca tra tutti i BLOCKS i nodi and/or
                    for(var i=0;i<blocks;i++)
                    {
                        //image id dice il tipo di immagine:tonda,rect,ecc..
                        var imageId = persisted.blocks[i].imageId;
                        //se ellipse ---> AND/OR, Start/End Element
                        if(imageId == "ellipse")
                        {
                            //Prende id del blocco, e il tipo(and,or,start,end)
                            var blockid = persisted.blocks[i].id;
                            var andor =  persisted.blocks[i].type;
                            var list = new Array;
                            //ciclo connections
                            for(var j=0;j<connections;j++)
                            {
                                //target == dove punta la connession
                                var target = persisted.connections[j].target;
                                if(blockid == target)
                                {
                                    //se qui: abbiamo connessione che ENTRA dentro and/or, e prendiamo la source
                                    var source = persisted.connections[j].source;
                                    for(var l=0;l<blocks;l++)
                                    {
                                        //id source
                                        var blockid2 = persisted.blocks[l].id;
                                        if(blockid2 == source)
                                        {
                                            //in questo ciclo, per ogni source, prende il tipo, essendo source=task, il tipo == stato
                                            var type = persisted.blocks[l].type;
                                            //aggiunge all'array lo stato di ogni task sorgente'
                                            list[y] = type;
                                            y++;
                                        }
                                    }
                                }
                            }
                            //se l'ellipse e' AND entra in questo ciclo'
                            if(andor=="And")
                            {
                                // array dove si memorizzano i task con status DONE
                                var done = new Array();
                                var x=0;
                                //Ciclo per lunghezza list, cioe' numero di task sorgenti'
                                for(var h=0;h<list.length;h++)
                                {
                                    var check = list[h];
                                    // se source == done, aggiunge a lista done
                                    if(check == "Done") {done[x]=check;x++}
                                }
                                //Se done e' diverso da list, condizione equivale a dire che NON TUTTI I padri sono DONE
                                if(done.length != list.length || list.length == 0)
                                {
                                    for(var z=0;z<connections;z++)
                                    {
                                        //TROVA la connessione tra AND e FIGLIO
                                        var source2 = persisted.connections[z].source;
                                        if(blockid == source2)
                                        {
                                            //Memorizza in target2 l'id del nodo/nodi figli'
                                            var target2 = persisted.connections[z].target;

                                            for(var g=0;g<blocks;g++)
                                            {
                                                var id3 = persisted.blocks[g].id;
                                                //se entra in questo if, troviamo il task figlio
                                                if(target2 == id3)
                                                {

                                                    var children= TextEdit.target.parentNode.parentNode.parentNode.childNodes.length;

                                                    for(var r=20;r<children;r++)
                                                    {
                                                        // alert(TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].shared)
                                                        var id4 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].id;

                                                        if(id3 == id4)
                                                        {
                                                            //DENTRO QUESTO IF metto lo status a NOT ENABLED, e COLORO di ROSSO LA SCRITTA

                                                            TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].childNodes[0].childNodes[0].style.color = "red";
                                                            TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].type = "Not Enabled";
                                                            // TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].childNodes[0].childNodes[7].value = "false";
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //Il ciclo sotto se i genitori sono DONE
                                }else if(done.length == list.length && done.length != 0)
                                {
                                    for(var z=0;z<connections;z++)
                                    {
                                        var source2 = persisted.connections[z].source;
                                        if(blockid == source2){
                                            var target2 = persisted.connections[z].target;
                                            for(var g=0;g<blocks;g++)
                                            {
                                                var id3 = persisted.blocks[g].id;
                                                if(target2 == id3)
                                                {

                                                    var children2= TextEdit.target.parentNode.parentNode.parentNode.childNodes.length;

                                                    for(var s=20;s<children2;s++)
                                                    {
                                                        // alert(TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].shared)
                                                        var id5 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[s].id;

                                                        if(id3 == id5)
                                                        {
                                                            var check = TextEdit.target.parentNode.parentNode.parentNode.childNodes[s].childNodes[0].childNodes[0].childNodes[8].value;
                                                            //alert(check);
                                                            //  alert(TextEdit.target.parentNode.parentNode.parentNode.childNodes[s].childNodes[0].childNodes[0].childNodes[8].value);
                                                            if(check != "true")
                                                            {
                                                                //METTO Il figlio/figli a Enabled
                                                                TextEdit.target.parentNode.parentNode.parentNode.childNodes[s].childNodes[0].childNodes[0].childNodes[0].style.color = "green";
                                                                TextEdit.target.parentNode.parentNode.parentNode.childNodes[s].type = "Enabled";
                                                                TextEdit.target.parentNode.parentNode.parentNode.childNodes[s].childNodes[0].childNodes[0].childNodes[8].value = "true";
                                                            }
                                              
                                               
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }







                                }

                            }
                            //INIZIA CONDIZIONE OR
                            else if(andor=="Or") {
                                var done = new Array();
                                var x=0;
                                for(var h=0;h<list.length;h++)
                                {
                                    var check = list[h];
                                    if(check == "Done") {done[x]=check;x++}
                                }
                                if(done.length >=1)
                                {
                                    for(var z=0;z<connections;z++)
                                    {
                                        var source2 = persisted.connections[z].source;
                                        if(blockid == source2){
                                            var target2 = persisted.connections[z].target;
                                            for(var g=0;g<blocks;g++)
                                            {
                                                var id3 = persisted.blocks[g].id;
                                                if(target2 == id3)
                                                {

                                                    var children= TextEdit.target.parentNode.parentNode.parentNode.childNodes.length;

                                                    for(var r=20;r<children;r++)
                                                    {
                                                        // alert(TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].shared)
                                                        var id4 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].id;

                                                        if(id3 == id4)
                                                        {
                                                            var check = TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].childNodes[0].childNodes[8].value;
                                                            if(check != "true")
                                                            {
                                                                var nome = TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].childNodes[0].childNodes[0];
                                                                nome.style.color = "green";
                                                                //   TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].style.color = "green";
                                                                TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].type = "Enabled";
                                                                TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].childNodes[0].childNodes[8].value = "true";
                                                            }
                                               
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }else
                                {
                                    for(var q=0;q<connections;q++)
                                    {
                                        var source3 = persisted.connections[q].source;
                                        if(blockid == source3){
                                            var target3 = persisted.connections[q].target;
                                            for(var d=0;d<blocks;d++)
                                            {
                                                var id6 = persisted.blocks[d].id;
                                                if(target3 == id6)
                                                {

                                                    var children3= TextEdit.target.parentNode.parentNode.parentNode.childNodes.length;
                                                    //alert(children);
                                                    for(var p=20;p<children3;p++)
                                                    {
                                                        // alert(TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].shared)
                                                        var id7 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[p].id;

                                                        if(id6 == id7)
                                                        {
                                                            var nome = TextEdit.target.parentNode.parentNode.parentNode.childNodes[p].childNodes[0].childNodes[0].childNodes[0];
                                                            nome.style.color = "red";
                                                            //    TextEdit.target.parentNode.parentNode.parentNode.childNodes[p].childNodes[0].style.color = "red";
                                                            TextEdit.target.parentNode.parentNode.parentNode.childNodes[p].type = "Not Enabled";
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }







                                }

                            }







                        }
                    }




                }
                catch(e){alert("Dentro check "+e.message)}
            }







            function dado(){
                var jsonString = Jalava.diagram.persist();
                var url1 = "http://taskmgrunito.x10.mx/scrifile.php";
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
                // confirm(jsonString);
            }


            // initialise Jalava here
            function initJalava(){
               
                Jalava.diagram = new Diagram(220, 130, "550", "600");
                var palette = new Palette(new FlowChartPaletteFactory(), 0, 130, 200,"","Digram Palette");
                palette.addItem("rect", "Task",  Palette.DRAG_TOOL, "./img/rect.gif");
                palette.addItem("connection", "Connection",  Palette.CLICK_TOOL, "./img/line.gif");
                palette.addItem("diamond", "Decision", Palette.DRAG_TOOL, "./img/diamond.gif");
                palette.addItem("parallel", "Input/Output", Palette.DRAG_TOOL, "./img/parallel.gif");
                palette.addItem("ellipse", "And/Or", Palette.DRAG_TOOL, "./img/ellipse.gif");
                palette.addItem("rounded", "Start/End", Palette.DRAG_TOOL, "./img/ellipse.gif");
                Jalava.propertyPage = new FlowChartPropertyPage(0, 320, 10);
                var palette2 = new Palette(new FlowChartPaletteFactory(), 780, 130, 200,"","Task Status");

                palette2.addItem("Task Status", "Task Done", Palette.CLICK_TOOL, "./img/quadratino_grigio.gif");
                palette2.addItem("Task Status", "Task Not Enabled", Palette.CLICK_TOOL, "./img/redquad.jpeg");
                palette2.addItem("Task Status", "Task Enabled", Palette.CLICK_TOOL, "./img/quadratino_verde.jpg");
               
                var palette3 = new Palette(new FlowChartPaletteFactory(), 780, 240, 200,"","Links");
                palette3.addItem("LinkFeed","", palette.Click_TOOl,"");
                palette3.addItem("LinkNotif","", palette.Click_TOOl,"");
            }

            // start Jalava
            Jalava.addModule("FlowChartPaletteFactory");
            //   Jalava.addModule("MyPaletteFactory");
            Jalava.addModule("FlowChartPropertyPage");
            Jalava.addModule("UrlGradientBlock");

            Jalava.start(initJalava);


            function closeIt()
            {
                if(c==0) {
                    // var aa = 1000;
                    //  saveDiagram(aa);
                    return "You should save your diagram before exit";
                }
                if(c==1)
                {
                    return "Diagramma gia' salvato";
                }
            }

            function dado()
            {
                var jsonString = Jalava.diagram.persist();
                alert(jsonString);
            }
            window.onbeforeunload = closeIt;

        </script>

    </head>

    <body onload="carica();" style="background-color: #add8e6"/>

    <!-- in onload c'era questo doTimer(); -->


    <h1>Collaborative Task Manager  </h1>




    <div id="menu" style="font-size: 13px"> <a href="#" onclick="childWindow=open('docsave.html','_blank','status=1,toolbar=1,scrollbars=1,width=400,height=200')" id="savenew" name="savenew" >Save Diagram </a> |  <a href="#" onClick=" saveDiagram('false');" id="savedraft">Save Draft</a> | <a href="#" onclick="childWindow=open('docs.jsp','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800')" id="load" name="load" >LoadDiagram </a>  | <a href="#" onclick="childWindow=open('shared.html','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800');" id="share">Share</a>
        | <a href="#" onclick="childWindow=open('/docs3.jsp','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800')" id="savetemplate">Save as Template</a> |
        <%=sessuser%> |<a href="http://localhost:8080/NotifMgrG/index.jsp"  target ="_blank" id="settings" name="settings"> <u>Notification settings</u> </a> |<a href="#" onclick="childWindow=open('/changepriv.html','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800')" id="changepriv" name="changepriv" disabled="disabled" >Change privileges of users </a>| <a target=_blank href="http://docs.google.com/support/?hl=en" class=gb4>Help</a>
        | <select name="notification" id="notification" >




           
           <option value="remote">Remote</option>
 <option value="local">Local</option>
  


        </select> | <a href="logout.jsp" class=gb4 >Log out </a> </div>
    <div id="menu2"></div>



    <form name="saveandload" id="saveandload" name="saveandload" >

        <input type="hidden"  name="area" id="area" value=""/>




        <input type="hidden" id="txt" />






        <input type="hidden" id="owner" name="owner" value= '<%=sessuser%>' disabled="disabled" />
        <input type="hidden" id="users" name="users" value= "" disabled="disabled" />
        <input type="hidden" id="writers" name="writers" value= "" disabled="disabled" size="100" />
        <input type="hidden" id="pwd" name="pwd" value='<%=sesspwd%>' disabled="disabled" />
        <input type="hidden" id="assignees" name="assignees" value='' size="100" />


        <!--
         <input type="button" value="See JSON" onclick="dado();"/>
 <input type="button" value="Check Condition" onclick="setCondition();"/>

         <input type="button" value="Share" onclick="childWindow=open('shared.html','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800');"/>
         <a href="#" onclick="childWindow=open('/docs2.jsp','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800')" id="add" name="add" >SaveDiagram </a>
            <input type="button" value="See JSON" onclick="dado();TextEdit.check();TextEdit.setOwner();"/>
        <input type="text" id="diagramName" name ="diagramName" value=""    />
        <input type="button" value="Do TImer" onclick="doTimer();"/>
        <br>
        <a href="#" onclick="childWindow=open('/docs.jsp','_blank','status=1,toolbar=1,scrollbars=1,width=600,height=800')" id="add" name="add" >LoadDiagram </a>

        -->






    </form>

</body>
</html>
