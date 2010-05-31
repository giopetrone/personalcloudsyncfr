

function TextEdit() { }

TextEdit.invoke = function(event) {
try {
  TextEdit.target = DOM.getEventTarget(event, "mytextarea");
  //TextEdit.target.parentNode.parentNode.owner = parent.document.getElementById("owner").value;
  TextEdit.showframe();
  
  } catch (e) {
  	alert("DENTRO INVOKE " +e.message);
  }

}
TextEdit.invokeLoad= function(event)
{
    try
    {
        TextEdit.target = DOM.getEventTarget(event, "mytextarea");
        var name = TextEdit.target.childNodes[0].innerHTML;

        if( name == "And" || name =="Or" || name=="And?Or?") TextEdit.showframe2();
        else if(name == "Decision" ) TextEdit.showframe6();
        else
        {
            var user = parent.document.getElementById("owner").value;
           // var readers = TextEdit.target.parentNode.parentNode.shared.value;
            var writers = TextEdit.target.parentNode.parentNode.writers;
            var own = TextEdit.target.parentNode.parentNode.owner;
            var assign = TextEdit.target.parentNode.parentNode.assign;
            if(assign == null) assign = '';
            /*
            var shared = TextEdit.target.parentNode.parentNode.shared;
            if(share d==null) shared="No Shared Users!";
            var assign = TextEdit.target.parentNode.parentNode.assign;
            if(assign==null) assign="No Users assigned to this task!"
            var own;
            
          
             own = TextEdit.target.parentNode.parentNode.owner;
          
 
  */
  if(own == user || writers.indexOf(user)!=-1 ||assign.indexOf(user)!=-1 )
  {
      //Utente loggato
      TextEdit.showframe4();
  }
 
  else{
        //alert("Solo details");
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
          if(content=="Decision") TextEdit.frame.src = '/decision.html';
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
  TextEdit.frame.src = '/modify.html';

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


  var content =  TextEdit.target.childNodes[0].innerHTML
  if (content=="") content = "Task Name";
  return content;
}



TextEdit.getCurrentStatus = function() {

//  var status = TextEdit.target.childNodes[1].value;
  var status = TextEdit.target.parentNode.parentNode.type;
  if (status=="Done")
  {
      TextEdit.target.childNodes[0].style.color = "green";
  

  }
  if (status=="Not Started yet")
  {
   TextEdit.target.childNodes[0].style.color = "red";
 
  }
  if (status=="In progress") {
      TextEdit.target.childNodes[0].style.color = "orange";
   
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






TextEdit.getCurrentAssign = function()
{
    var assign = TextEdit.target.parentNode.parentNode.assign;
    if (assign==null) assign = "Assign Task to users";
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
      TextEdit.target.childNodes[0].style.color = "green";
      TextEdit.target.parentNode.parentNode.type = status;
      

  }
  if (status=="Not Started yet")
  {
   TextEdit.target.childNodes[0].style.color = "red";
   TextEdit.target.parentNode.parentNode.type = status;

   
  }
  if (status=="In progress") {
      TextEdit.target.childNodes[0].style.color = "orange";
      TextEdit.target.parentNode.parentNode.type = status;

  }

  if (status=="") {
      TextEdit.target.childNodes[0].style.color = "black";
      TextEdit.target.parentNode.parentNode.type = status;
    
  }
  TextEdit.check();
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
        TextEdit.target.parentNode.parentNode.type =andor;
        TextEdit.check();
        TextEdit.reposition();
        TextEdit.cancel();

    }
    catch(e){alert(e.message);}
}



TextEdit.setContentDecision = function(decision,target)
{
    try{

       // TextEdit.target.childNodes[0].innerHTML = andor;
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
                   if(done.length != list.length)
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
                                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].style.color = "red";
                                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].type = "Not Started yet";
                                           }
                                       }
                                   }
                               }
                           }
                       }
                   }else if(done.length == list.length)
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
                                               alert(TextEdit.target.parentNode.parentNode.parentNode.childNodes[s].childNodes[0].style.color);
                                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[s].childNodes[0].style.color = "orange";
                                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[s].type = "In progress";
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
                                       alert(children);
                                       for(var r=20;r<children;r++)
                                       {
                                              // alert(TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].shared)
                                           var id4 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].id;

                                           if(id3 == id4)
                                           {
                                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].style.color = "orange";
                                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].type = "In progress";
                                           }
                                       }
                                   }
                               }
                           }
                       }
                   }else
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
                                       //alert(children);
                                       for(var r=20;r<children;r++)
                                       {
                                              // alert(TextEdit.target.parentNode.parentNode.parentNode.childNodes[h].shared)
                                           var id4 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].id;

                                           if(id3 == id4)
                                           {
                                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].style.color = "red";
                                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].type = "Not Started yet";
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


                var j =0;
                for(var i=0;i<connections;i++)
                {
                    var source = persisted.connections[i].source;


                    if(source == id) {
                        trovati[j] = persisted.connections[i].target;

                        j++;

                    }
                }
                for(var h=0;h<trovati.length;h++)
                {
                    var trovatoid = trovati[h];
                    var children= TextEdit.target.parentNode.parentNode.parentNode.childNodes.length;
                    if(trovatoid == target)
                    {

                       for(var r=20;r<children;r++)
                       {
                           var id2 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].id;
                           if(target == id2)
                           {
                              
                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[r].childNodes[0].style.color = "pink";
                              // content = content + " Not Approved By decisione Node";
                           
                           }

                       }
                    }
                    else
                    {
                        for(var l=20;l<children;l++)
                       {
                           var id3 = TextEdit.target.parentNode.parentNode.parentNode.childNodes[l].id;
                           if(trovatoid == id3)
                           {
                              
                               TextEdit.target.parentNode.parentNode.parentNode.childNodes[l].childNodes[0].style.color = "blue";
                              // content = content + " Not Approved By decisione Node";
                              
                           }

                       }
                    }
                }


}catch(e){alert("Dentro checkDecision "+e.message)}
}


Jalava._modules['TextEdit'] = true;