

function TextEdit() { }

TextEdit.invoke = function(event) {
try {
  TextEdit.target = DOM.getEventTarget(event, "mytextarea");
 
  TextEdit.showframe();
  
  } catch (e) {
  	alert("DENTRO INVOKE " +e.message);
  }

}


TextEdit.invokeView = function(event) {
try {
  TextEdit.target = DOM.getEventTarget(event, "mytextarea");

  TextEdit.showframe4();

  } catch (e) {
  	alert("DENTRO INVOKE View " +e.message);
  }

}



TextEdit.invokeLoad= function(event)
{
    try
    {
        TextEdit.target = DOM.getEventTarget(event, "mytextarea");
        var name = TextEdit.target.childNodes[0].innerHTML;

        if( name == "And" || name =="Or" || name=="And?Or?") TextEdit.showframe2();
        else if(name == "Decision" || TextEdit.target.parentNode.parentNode.type =="decision" ) TextEdit.showframe6();
        else if(name=="Start" || name=="End" || name=="Start?End?") alert("Da definire");
        else
        {
            
            var user = parent.document.getElementById("owner").value;
       //     alert(user);
           // var readers = TextEdit.target.parentNode.parentNode.shared.value;
        //    var writers = TextEdit.target.parentNode.parentNode.writers;
            alert(user);
            var own = TextEdit.target.parentNode.parentNode.owner;
            var assign = TextEdit.target.parentNode.parentNode.assign;
            if(assign == null) assign="";
            var writers = parent.document.getElementById("writers").value;
            var shared = parent.document.getElementById("users").value;
          
            /*
            
            if(share d==null) shared="No Shared Users!";
            var assign = TextEdit.target.parentNode.parentNode.assign;
            if(assign==null) assign="No Users assigned to this task!"
            var own;
            
          
             own = TextEdit.target.parentNode.parentNode.owner;
          
 
  */
  if(own == user  ||assign.indexOf(user)!=-1 ||writers.indexOf(user) !=-1 )
  {
      //Utente loggato  e' owner, assegnatario o writer'
    //  alert("dentro if");
      TextEdit.showframe();
  }
 
  else if(shared.indexOf(user)!=-1){
     //   alert("Solo details");
        TextEdit.showframe3();
  }
    }
}
    catch(e){alert("DENTRO INVOKE LOAD "+e.message());}
}
TextEdit.invokeAndor = function(event){
    try{
          TextEdit.target = DOM.getEventTarget(event, "mytextarea");
          var content =  TextEdit.target.childNodes[0].innerHTML;
          var opaque = document.createElement("DIV");
          opaque.style.width = "100%";
          opaque.style.height = "100%";
          opaque.style.position = "absolute";
          opaque.style.top = "0px";
          opaque.style.left = "0px";
          opaque.style.zIndex = 399;
          opaque.className = "blanket"
          document.body.appendChild(opaque);
          TextEdit.opaque = opaque;

          if (!TextEdit.frame) TextEdit.createFrame();
          if(TextEdit.target.parentNode.parentNode.type =="decision" || content == "Decision") TextEdit.frame.src = '/decision.html';
          else if(content=="Start" || content == "Start?End?" || content=="End") TextEdit.frame.src = '/startend.html'
          else TextEdit.frame.src = '/andor.html';
              // centre the frame
          var h = document.body.clientHeight;
          var w = document.body.clientWidth;
          var clientH = parseInt(TextEdit.frame.style.height);
          var clientW = parseInt(TextEdit.frame.style.width);
          TextEdit.frame.style.top = (h - clientH) / 2;
          TextEdit.frame.style.left = (w - clientW) / 2;
          TextEdit.frame.style.display = 'block';
          TextEdit.frame.style.zIndex = 400;
     //     if(content == "Decision") TextEdit.showframe5();
       //   else TextEdit.showframe2();
    }
    catch(e){alert(e.message);}
}









TextEdit.setTarget = function(element) {
  TextEdit.target = element;
}

TextEdit.showframe = function() {

  // add a translucent cover to prevent user access
  var opaque = document.createElement("DIV");
  opaque.style.width = "100%";
  opaque.style.height = "100%";
  opaque.style.position = "absolute";
  opaque.style.top = "0px";
  opaque.style.left = "0px";
  opaque.style.zIndex = 399;
  opaque.className = "blanket";
  document.body.appendChild(opaque);
  TextEdit.opaque = opaque;

  if (!TextEdit.frame) TextEdit.createFrame();
  TextEdit.frame.src = '/modifyLoad.html';

  // centre the frame
  var h = document.body.clientHeight;
  var w = document.body.clientWidth;
  var clientH = parseInt(TextEdit.frame.style.height);
  var clientW = parseInt(TextEdit.frame.style.width);
  TextEdit.frame.style.top = (h - clientH) / 2;
  TextEdit.frame.style.left = (w - clientW) / 2;
  TextEdit.frame.style.display = 'block';
  TextEdit.frame.style.zIndex = 400;
}


TextEdit.showframe2 = function() {

  // add a translucent cover to prevent user access
  var opaque = document.createElement("DIV");
  opaque.style.width = "100%";
  opaque.style.height = "100%";
  opaque.style.position = "absolute";
  opaque.style.top = "0px";
  opaque.style.left = "0px";
  opaque.style.zIndex = 399;
  opaque.className = "blanket"
  document.body.appendChild(opaque);
  TextEdit.opaque = opaque;

  if (!TextEdit.frame) TextEdit.createFrame();
  TextEdit.frame.src = '/andor.html';

  // centre the frame
  var h = document.body.clientHeight;
  var w = document.body.clientWidth;
  var clientH = parseInt(TextEdit.frame.style.height);
  var clientW = parseInt(TextEdit.frame.style.width);
  TextEdit.frame.style.top = (h - clientH) / 2;
  TextEdit.frame.style.left = (w - clientW) / 2;
  TextEdit.frame.style.display = 'block';
  TextEdit.frame.style.zIndex = 400;
}



TextEdit.showframe3 = function() {

  // add a translucent cover to prevent user access
  var opaque = document.createElement("DIV");
  opaque.style.width = "100%";
  opaque.style.height = "100%";
  opaque.style.position = "absolute";
  opaque.style.top = "0px";
  opaque.style.left = "0px";
  opaque.style.zIndex = 399;
  opaque.className = "blanket"
  document.body.appendChild(opaque);
  TextEdit.opaque = opaque;

  if (!TextEdit.frame) TextEdit.createFrame();
  TextEdit.frame.src = '/details.html';

  // centre the frame
  var h = document.body.clientHeight;
  var w = document.body.clientWidth;
  var clientH = parseInt(TextEdit.frame.style.height);
  var clientW = parseInt(TextEdit.frame.style.width);
  TextEdit.frame.style.top = (h - clientH) / 2;
  TextEdit.frame.style.left = (w - clientW) / 2;
  TextEdit.frame.style.display = 'block';
  TextEdit.frame.style.zIndex = 400;
}

TextEdit.showframe4 = function() {

  // add a translucent cover to prevent user access
  var opaque = document.createElement("DIV");
  opaque.style.width = "100%";
  opaque.style.height = "100%";
  opaque.style.position = "absolute";
  opaque.style.top = "0px";
  opaque.style.left = "0px";
  opaque.style.zIndex = 399;
  opaque.className = "blanket"
  document.body.appendChild(opaque);
  TextEdit.opaque = opaque;

  if (!TextEdit.frame) TextEdit.createFrame();
  TextEdit.frame.src = '/view.html';

  // centre the frame
  var h = document.body.clientHeight;
  var w = document.body.clientWidth;
  var clientH = parseInt(TextEdit.frame.style.height);
  var clientW = parseInt(TextEdit.frame.style.width);
  TextEdit.frame.style.top = (h - clientH) / 2;
  TextEdit.frame.style.left = (w - clientW) / 2;
  TextEdit.frame.style.display = 'block';
  TextEdit.frame.style.zIndex = 400;
}



TextEdit.showframe6 = function() {

  // add a translucent cover to prevent user access
  var opaque = document.createElement("DIV");
  opaque.style.width = "100%";
  opaque.style.height = "100%";
  opaque.style.position = "absolute";
  opaque.style.top = "0px";
  opaque.style.left = "0px";
  opaque.style.zIndex = 399;
  opaque.className = "blanket"
  document.body.appendChild(opaque);
  TextEdit.opaque = opaque;

  if (!TextEdit.frame) TextEdit.createFrame();
  TextEdit.frame.src = '/decision.html';

  // centre the frame
  var h = document.body.clientHeight;
  var w = document.body.clientWidth;
  var clientH = parseInt(TextEdit.frame.style.height);
  var clientW = parseInt(TextEdit.frame.style.width);
  TextEdit.frame.style.top = (h - clientH) / 2;
  TextEdit.frame.style.left = (w - clientW) / 2;
  TextEdit.frame.style.display = 'block';
  TextEdit.frame.style.zIndex = 400;
}



TextEdit.getCurrentContent = function() {


  var content =  TextEdit.target.childNodes[0].innerHTML;
  if (content=="") content = "Task Name";
  return content;
}


TextEdit.getCheck = function() {


  var content =  TextEdit.target.childNodes[8].value;
 // if (content=="") content = "Task Name";
  return content;
}



TextEdit.getCurrentStatus = function() {

//  var status = TextEdit.target.childNodes[1].value;
  var status = TextEdit.target.parentNode.parentNode.type;
  if (status=="Done")
  {
      TextEdit.target.childNodes[0].style.color = "black";
  

  }
  if (status=="Not Enabled")
  {
   TextEdit.target.childNodes[0].style.color = "red";
 
  }
  if (status=="Enabled") {
      TextEdit.target.childNodes[0].style.color = "green";
   
  }
  if(status==null)
  {
      TextEdit.target.childNodes[0].style.color = "black";
  }
  return status;
}

TextEdit.getCurrentCategory = function() {

  var cat = TextEdit.target.parentNode.parentNode.cat;
  return cat;
}


TextEdit.getCurrentOwner = function() {

  var owner = TextEdit.target.parentNode.parentNode.owner;
  return owner;
}

TextEdit.getCurrentShare = function() {

 
  var share = TextEdit.target.parentNode.parentNode.shared;
  /*
  var i= TextEdit.target.parentNode.parentNode.parentNode.childNodes.length;

  var j= 0;
  var h =0;

  for (h=20;h<i;h++)
             {
               share = TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].shared;
               if(share!= null) return share;
             
             }*/

 return share;

}


TextEdit.getCurrentLinks = function()
{
    var links = TextEdit.target.childNodes[6].value;
   
    return links;
}



TextEdit.getCurrentAssign = function()
{
    var assign = TextEdit.target.parentNode.parentNode.assign;
    if (assign==null) assign = "";
    return assign;
}


TextEdit.getCurrentDesc = function()
{
    var desc = TextEdit.target.parentNode.parentNode.desc;
    if(desc==null) desc="";
    return desc;
}


TextEdit.getCurrentDate = function() {

  //var date = TextEdit.target.childNodes[2].value;
  var date = TextEdit.target.parentNode.parentNode.date;
  if (date==null) date = "Choose a Date(Optional)";
  
  return date;
}

TextEdit.setContent = function(data, status,date,cat,people,assign,desc) {
  try
  {

  if (data=="") data = "&nbsp;";
 
  TextEdit.target.childNodes[0].innerHTML = data;
  TextEdit.target.parentNode.parentNode.name = data;
  TextEdit.target.parentNode.parentNode.date = date;
  TextEdit.target.parentNode.parentNode.cat = cat;
  TextEdit.target.parentNode.parentNode.assign = assign;
  TextEdit.target.parentNode.parentNode.desc = desc;
  
      //var http = "http://";
     // if(link.indexOf(http)<0) link = http + link;
   //   TextEdit.target.childNodes[6].innerHTML = "\nLink"
   //   TextEdit.target.childNodes[6].setAttribute('href',link);
  
  TextEdit.target.childNodes[1].value = parent.document.getElementById("owner").value;
  var i= TextEdit.target.parentNode.parentNode.parentNode.childNodes.length;
  var j= 20;
  var h =0;
  for (h=j;h<i;h++)
             {
                TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].shared = people;
             //   TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].owner = parent.document.getElementById("owner").value;
             }

  
  if (status=="Done")
  {
      TextEdit.target.childNodes[0].style.color = "gray";
      TextEdit.target.parentNode.parentNode.type = status;
      

  }
  if (status=="Not Enabled")
  {
   TextEdit.target.childNodes[0].style.color = "red";
   TextEdit.target.parentNode.parentNode.type = status;

   
  }
  if (status=="Enabled") {
      TextEdit.target.childNodes[0].style.color = "green";
      TextEdit.target.parentNode.parentNode.type = status;

  }

  if (status=="") {
      TextEdit.target.childNodes[0].style.color = "black";
      TextEdit.target.parentNode.parentNode.type = status;
    
  }
  var check = TextEdit.getCheck();
  //alert(check);
  if(check == "false") TextEdit.check();
  TextEdit.reposition();
 
  TextEdit.cancel();
  
  }
  catch (e) {
  	alert("DentroSetContent " +e.message);
  }
}

TextEdit.setContentCondition = function(andor)
{
    try{
        if (andor=="") andor = "And?Or?";
        TextEdit.target.childNodes[0].innerHTML = andor;
        TextEdit.target.parentNode.parentNode.name = andor;
        TextEdit.target.parentNode.parentNode.type =andor;
        TextEdit.check();
        TextEdit.reposition();
        TextEdit.cancel();

    }
    catch(e){alert("Dentro SetContentCondition "+e.message);}
}
TextEdit.setLinks = function(links)
{
    try
    {
        TextEdit.target.childNodes[6].value = links;
      //  TextEdit.target.childNodes[6].setAttribute('href',link);
        

    }
    catch(e){alert("DENTRO SETLINKS "+e.message);}
}


TextEdit.setContentDecision = function(decision,target,nome)
{
    try{

        TextEdit.target.childNodes[0].innerHTML = nome;
        TextEdit.target.parentNode.parentNode.name = nome;
        TextEdit.target.parentNode.parentNode.desc = decision;
        TextEdit.target.parentNode.parentNode.type = "decision";
        
        TextEdit.checkDecision(target);
        TextEdit.reposition();
        TextEdit.cancel();

    }
    catch(e){alert(e.message);}
}


TextEdit.cancel = function() {


//  TextEdit.target.parentNode.parentNode.owner = parent.document.getElementById("owner").value;
  document.body.removeChild(TextEdit.opaque);
  
  TextEdit.frame.src = '/blank.html';

  TextEdit.frame.style.display = 'none';
 
  
}
TextEdit.setShare = function(people)
{
    try
    {

        var i= TextEdit.target.parentNode.parentNode.parentNode.childNodes.length;
        var j= 20;
        var h =0;
        for (h=j;h<i;h++)
                 {
                    TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].shared = people;
                 //   TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].owner = parent.document.getElementById("owner").value;
                 }
    }catch(e){alert("Dentro setShare: "+e.message)}
}



TextEdit.close = function() {


 
  document.body.removeChild(TextEdit.opaque);

  TextEdit.frame.src = '/blank.html';

  TextEdit.frame.style.display = 'none';
 

}

 TextEdit.createFrame = function() {
  TextEdit.frame = document.createElement("IFRAME");
  TextEdit.frame.src = "/blank.html";
  TextEdit.frame.style.width = "420px";
  TextEdit.frame.style.height = "340px";
  TextEdit.frame.style.position = "absolute";
  TextEdit.frame.style.display = "none";
  TextEdit.frame.style.border = "1px solid #888888";
  document.body.appendChild(TextEdit.frame);
}

TextEdit.reposition = function() {
  // reposition itself
  var parent =  Editable.bubbleToTarget(TextEdit.target, "DIV", "figure", true);
  var fig = Jalava.diagram.getFigure(parent.id);
  if (parent && fig && fig.clazz=="Block") {
    var trueheight = fig.height - fig.brushWidth*2;
    parent.firstChild.style.top = (trueheight - parent.firstChild.offsetHeight) / 2;
  }
}


TextEdit.check = function()
{
    try{
      
       // TextEdit.target = DOM.getEventTarget(event, "mytextarea");
        var jsonString = window.parent.Jalava.diagram.persist();
        //var id = TextEdit.target.parentNode.parentNode.id;
     
        var persisted = JSON.parse(jsonString);
        var blocks = persisted.blocks.length;
        var connections = persisted.connections.length;
        var y=0;
        for(var i=0;i<blocks;i++)
        {
             
            var imageId = persisted.blocks[i].imageId;
            if(imageId == "ellipse")
            {
               
               var blockid = persisted.blocks[i].id;
               var andor =  persisted.blocks[i].type;
               var list = new Array;
               for(var j=0;j<connections;j++)
               {
                   var target = persisted.connections[j].target;
                   if(blockid == target)
                   {
                       var source = persisted.connections[j].source;
                       for(var l=0;l<blocks;l++)
                       {
                           var blockid2 = persisted.blocks[l].id;
                           if(blockid2 == source)
                           {
                               var type = persisted.blocks[l].type;
                               list[y] = type;
                               y++;
                           }
                       }
                   }
               }
               if(andor=="And")
               {
                   var done = new Array();
                   var x=0;
                   for(var h=0;h<list.length;h++)
                   {  
                       var check = list[h];
                       if(check == "Done") {done[x]=check;x++}
                   }
                   if(done.length != list.length || list.length == 0)
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
                                              
                                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].childNodes[0].childNodes[0].style.color = "red";
                                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].type = "Not Enabled";
                                              // TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].childNodes[0].childNodes[7].value = "false";
                                           }
                                       }
                                   }
                               }
                           }
                       }
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
                                             //  alert(TextEdit.target.parentNode.parentNode.parentNode.childNodes[s].childNodes[0].style.color);
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


TextEdit.checkDecision = function(target)
{
    try{
                
                var jsonString = window.parent.Jalava.diagram.persist();
                var trovati = new Array();
                var id = TextEdit.target.parentNode.parentNode.id;
                var desc = TextEdit.target.parentNode.parentNode.desc;
                var persisted = JSON.parse(jsonString);
                var blocks = persisted.blocks.length;
                var connections = persisted.connections.length;
                var archi = new Array();
                var j =0;
                var t=0;
                for(var i=0;i<connections;i++)
                {
                    var source = persisted.connections[i].source;


                    if(source == id) {
                        trovati[j] = persisted.connections[i].target;
                        archi[t] = persisted.connections[i];
                        j++;
                        t++;


                    }
                }
                //alert(trovati.length);
                for(var h=0;h<trovati.length;h++)
                {
                    var trovatoid = trovati[h];
                    
                    if(trovatoid == target)
                    {
                       var children= TextEdit.target.parentNode.parentNode.parentNode.childNodes.length;
                       for(var r=20;r<children;r++)
                       {
                           var id2 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].id;
                           if(target == id2)
                           {
                              
                         
                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].childNodes[0].childNodes[5].style.color = "green";
                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].childNodes[0].childNodes[5].innerHTML = "\nApproved\n";
                              
                           
                           }


                       }

                       for(var l = 0;l<archi.length;l++)
                              {
                                  var target2 = archi[l].target;
                                 
                                  if(trovatoid == target2)
                                  {
                                      var id4 = archi[l].id;
                                      for(var z = 0;z<children;z++)
                                      {
                                           var id3 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[z].id;

                                           if(id4 == id3)
                                           {

                                              TextEdit.target.parentNode.parentNode.parentNode.childNodes[z].childNodes[2].style.color = "green";
                                              TextEdit.target.parentNode.parentNode.parentNode.childNodes[z].childNodes[2].innerHTML = "Yes";

                                           }
                                      }
                                  }
                                  else if(trovatoid != target2)
                                  {
                                      var id5 = archi[l].id;
                                      for(var x = 0;x<children;x++)
                                      {
                                           var id6 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[x].id;

                                           if(id5 == id6)
                                           {

                                              TextEdit.target.parentNode.parentNode.parentNode.childNodes[x].childNodes[2].style.color = "red";
                                              TextEdit.target.parentNode.parentNode.parentNode.childNodes[x].childNodes[2].innerHTML = "No";

                                           }
                                      }
                                  }
                              }



                    }
                    else if(trovatoid != target)
                    {
                       var children2= TextEdit.target.parentNode.parentNode.parentNode.childNodes.length;
                       for(var f=20;f<children2;f++)
                       {
                           var id7 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[f].id;
                          
                           if(trovatoid == id7)
                           {
                               
                              
                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[f].childNodes[0].childNodes[0].childNodes[5].style.color = "red";
                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[f].childNodes[0].childNodes[0].childNodes[5].innerHTML = "\nRejected\n";
                             
                           
                              
                           }
                          
                       }
             
                    }
                }


}catch(e){alert("Dentro checkDecision "+e.message)}
}


Jalava._modules['TextEdit'] = true;