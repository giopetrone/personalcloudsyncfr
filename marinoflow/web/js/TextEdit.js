/* v0.8.0beta */

/*
 * Links to a TinyMCE Editor using an iframe. 
 * 
 * TinyMCE is an open source HTML editor. More info can be found
 * at http://tinymce.moxiecode.com/
 */

function TextEdit() { }

TextEdit.invoke = function(event) {
try {
  TextEdit.target = DOM.getEventTarget(event, "mytextarea");
  alert(TextEdit.target.parentNode.parentNode.type);
//  alert(window.parent.parent.document.getElementById('width').value)
 // alert(TextEdit.target.parentNode.parentNode.status.innerHTML);
//  alert(TextEdit.target.parentNode.parentNode.childNodes[1]);
//  alert(TextEdit.target.parentNode.parentNode.childNodes[2]);
 // alert(TextEdit.target.parentNode.parentNode.childNodes[3]);
  TextEdit.showframe();
  } catch (e) {
  	alert(e.message);
  }

}









TextEdit.invokeOrig = function(event) {
try {
  TextEdit.target = DOM.getEventTarget(event, "mytextarea");

  TextEdit.showframe();
  } catch (e) {
  	alert(e.message);
  }

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
  opaque.className = "blanket"
  document.body.appendChild(opaque);
  TextEdit.opaque = opaque;
  
  if (!TextEdit.frame) TextEdit.createFrame();
  TextEdit.frame.src = '/index.html';
  
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

  var status = TextEdit.target.childNodes[1].value;
  if (status=="") status="Not Started yet";
  return status;
}

TextEdit.getCurrentCategory = function() {

  var cat = TextEdit.target.childNodes[3].value;
  return cat;
}

TextEdit.getCurrentShare = function() {

  var share = window.parent.document.getElementById('share').value;
  return share;
}

TextEdit.getCurrentAssign = function()
{
    var assign = TextEdit.target.childNodes[4].value;
    return assign;
}


TextEdit.getCurrentDate = function() {

  var date = TextEdit.target.childNodes[2].value;
  if (date=="") date = "Choose a Date(Optional)";
  return date;
}

TextEdit.setContent = function(data, status,date,cat,people,assign) {
  try
  {

  if (data=="") data = "&nbsp;";
  TextEdit.target.childNodes[0].innerHTML = data;
  //TextEdit.target.parentNode.parentNode.content.value = data;
  TextEdit.target.childNodes[1].value = status;
 TextEdit.target.childNodes[2].value = date;
 TextEdit.target.childNodes[3].value = cat;
  parent.document.getElementById('share').value = people;
 TextEdit.target.childNodes[4].value = assign;
  if (status=="Done") 
  {
      TextEdit.target.childNodes[0].style.color = "green";
      TextEdit.target.parentNode.parentNode.type = status;
      //state = "Done";

  }
  if (status=="Not Started yet")
  {
   TextEdit.target.childNodes[0].style.color = "red";
   TextEdit.target.parentNode.parentNode.type = status;
     // state = "Not Started yet";
  }
  if (status=="In progress") {
      TextEdit.target.childNodes[0].style.color = "orange";
      TextEdit.target.parentNode.parentNode.type = status;
     // state = "In progress";
  }

  if (status=="") {
      TextEdit.target.childNodes[0].style.color = "black";
      TextEdit.target.parentNode.parentNode.type = status;
     // state = "In progress";
  }
  
  //TextEdit.reposition();
  TextEdit.cancel();
  }
  catch (e) {
  	alert(e.message);
  }
}


TextEdit.setColor = function(data)
{
    if (data=="Done") TextEdit.frame.style.border = "3px solid green";
    if (data=="Not Started yet") TextEdit.frame.style.border = "3px solid black"
    if (data=="In progress") TextEdit.frame.style.border = "3px solid yellow"
    TextEdit.cancel();
}

TextEdit.cancel = function() {
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

Jalava._modules['TextEdit'] = true;