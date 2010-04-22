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

  TextEdit.showframe();
  } catch (e) {
  	alert(e.message);
  }

}


TextEdit.invokeNEW = function(event) {
   try {
  TextEdit.target = DOM.getEventTarget(event, "mytextarea");
 
 // TextEdit.target.style.border = "5px solid red";
  var comments = document.getElementById('comments');
  comments.value = TextEdit.target.innerHTML;
 
  } catch (e) {
  	alert(e.message);
  }
 // confirm("dopo invoke edit");
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
  TextEdit.frame.src = 'http://marinoflow.appspot.com/mce.html';
  
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
  var content = TextEdit.target.innerHTML;
  if (content=="&nbsp;") content = "";
  return content;
}

TextEdit.setContent = function(data) {
  if (data=="") data = "&nbsp;";
  TextEdit.target.innerHTML = data;
  TextEdit.reposition();
  TextEdit.cancel();
}

TextEdit.cancel = function() {
  document.body.removeChild(TextEdit.opaque);
  TextEdit.frame.src = 'http://marinoflow.appspot.com/blank.html';
  TextEdit.frame.style.display = 'none';
}

TextEdit.createFrame = function() {
  TextEdit.frame = document.createElement("IFRAME");
  TextEdit.frame.src = "http://marinoflow.appspot.com/blank.html";
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