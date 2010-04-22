/* v0.8.0beta */
/*
 * Allow the editing of static text.
 */
function Editable() {
}

Editable.tryedit = function() { if (Jalava.temp_var.textbox) return true; else return false; }

Editable.startedit = function(evt, callback)
{
    if (Jalava.temp_var.textbox) return true;
	var textbox = DOM.getEventTarget(evt, "mytextarea");
	var type =  Editable.type(textbox);
	if (!type) return false;

	Jalava.temp_var.textbox = textbox;
	Jalava.temp_var.callback = callback;
	var val = Jalava.temp_var.textbox.innerHTML;
	
	if (type==Editable.TEXTAREA) {
	  var ta = DOM.createElement("TEXTAREA", "textbox");
	  ta.style.border = '1px solid #DDDDDD';
	  ta.style.fontFamily = 'Arial';
	  ta.style.fontSize = '12px';
	  ta.style.padding = '3px';
	  ta.style.marginLeft = '5px'; ta.style.marginRight = '5px';
	  ta.style.width = '90%';
	
      while (Jalava.temp_var.textbox.hasChildNodes()) 
	    { Jalava.temp_var.textbox.removeChild(Jalava.temp_var.textbox.firstChild); }
	  Jalava.temp_var.textbox.appendChild(ta);
	  ta.value = (val=="&nbsp;") ? "" : Editable.br2nl(val);
	}
	if (type==Editable.INPUT) {
	  var ta = DOM.createElement("INPUT", "textbox", "text");
	  ta.style.position = "absolute";
	  ta.style.border = '1px solid #DDDDDD';
	  ta.style.fontFamily = 'Arial';
	  ta.style.fontSize = '12px';
	  ta.style.padding = '1px';
	  ta.style.marginLeft = '5px'; ta.style.marginRight = '5px';
	  ta.style.width = '90%';
	
      while (Jalava.temp_var.textbox.hasChildNodes()) 
	    { Jalava.temp_var.textbox.removeChild(Jalava.temp_var.textbox.firstChild); }
	  Jalava.temp_var.textbox.appendChild(ta);
	  ta.value = (val=="&nbsp;") ? "" : Editable.br2nl(val);		
	}
	else alert("text input not implemented yet");
	
    /*
	if (Jalava.temp_var.textbox.currentStyle) {	// for IE, manual inherit
	  Jalava.temp_var.textbox.firstChild.style.fontFamily = Jalava.temp_var.textbox.currentStyle.fontFamily;
	  Jalava.temp_var.textbox.firstChild.style.fontSize = Jalava.temp_var.textbox.currentStyle.fontSize;
	  Jalava.temp_var.textbox.firstChild.style.fontWeight = Jalava.temp_var.textbox.currentStyle.fontWeight;
	  Jalava.temp_var.textbox.firstChild.style.fontStyle = Jalava.temp_var.textbox.currentStyle.fontStyle;
	  Jalava.temp_var.textbox.firstChild.style.textDecoration = Jalava.temp_var.textbox.currentStyle.textDecoration;
	  Jalava.temp_var.textbox.firstChild.style.color = Jalava.temp_var.textbox.currentStyle.color;
	}
	else if (window.getComputedStyle) {	// W3C compliant
	  Jalava.temp_var.textbox.firstChild.style.color = window.getComputedStyle(Jalava.temp_var.textbox, null).getPropertyValue("color");
	}
	*/

    var parent = Editable.bubbleToTarget(Jalava.temp_var.textbox, "DIV", "figure", true);
	var fig = Jalava.diagram.getFigure(parent.id);
    if (parent && fig) {
      ta.style.left = "2px";	
      if (type==Editable.TEXTAREA) ta.style.height = fig.height - 10;	
	}
	
	if (Jalava.temp_var.textbox.firstChild.addEventListener) 
	  Jalava.temp_var.textbox.firstChild.addEventListener("blur", Editable.stopedit, false);
	else if (Jalava.temp_var.textbox.firstChild.attachEvent) {
	  Jalava.temp_var.textbox.firstChild.attachEvent("onblur", Editable.stopedit);
	  document.body.attachEvent("onselectstart", Jalava.returntrue);
	}

    Jalava.mousedownHandler = Editable.stopedit;
	Jalava.mousemoveHandler = Jalava.returntrue;
    Jalava.disableHighlight = Jalava.returntrue;
    Jalava.keyDownHandler = Jalava.returntrue;
	Jalava.keyPressHandler = Jalava.returntrue;
	
	Jalava.temp_var.textbox.firstChild.focus();

	return false;
}

Editable.stopedit = function(evt)
{
  var textbox = DOM.getEventTarget(evt);
  if (Jalava.temp_var.textbox.firstChild == textbox) return false;
  
  Jalava.mousedownHandler = Jalava.returntrue;
  Jalava.mousemoveHandler = Jalava.returnfalse;
  Jalava.disableHighlight = Jalava.returnfalse;
  Jalava.keyPressHandler = Jalava.onKeyPressEvent;
  Jalava.keyDownHandler = Jalava.onKeyDownEvent;

  if (!Jalava.temp_var.textbox) return;
  try{
	if (Jalava.temp_var.textbox.firstChild.removeEventListener) 
		Jalava.temp_var.textbox.firstChild.removeEventListener("blur", Editable.stopedit, false);
	else 
		if (document.body.detachEvent) {
			Jalava.temp_var.textbox.firstChild.detachEvent("onblur", Editable.stopedit);
			document.body.detachEvent("onselectstart", Jalava.returntrue);
		}
  } catch (e) { alert(e.message);	/*TODO*/  }

  var newVal;
  if (Jalava.temp_var.textbox.firstChild.value=="")		
    newVal = "&nbsp;";
  else
    newVal = Editable.nl2br(Jalava.temp_var.textbox.firstChild.value);


  while (Jalava.temp_var.textbox.hasChildNodes()) 
	{ Jalava.temp_var.textbox.removeChild(Jalava.temp_var.textbox.firstChild); }

  Jalava.temp_var.textbox.innerHTML = newVal;
  // reposition itself
  var parent =  Editable.bubbleToTarget(Jalava.temp_var.textbox, "DIV", "figure", true);
  var fig = Jalava.diagram.getFigure(parent.id);
  if (parent && fig) {
    var trueheight = fig.height - fig.brushWidth*2;
    parent.firstChild.style.top = (trueheight - parent.firstChild.offsetHeight) / 2;	  
	parent.firstChild.style.scrollTop = "0px";
  }

  if (Jalava.temp_var.callback) Jalava.temp_var.callback(Jalava.temp_var.textbox);
  delete Jalava.temp_var.textbox;
  delete Jalava.temp_var.callback;
}

Editable.TEXTAREA = "editable";
Editable.INPUT = "editableLine";

Editable.type = function(obj) {
  try {
    var classname = obj.className;	
	var arr = classname.split(" ");
	for (var i=0; i<arr.length; i++) {
		if (arr[i]==Editable.TEXTAREA) return Editable.TEXTAREA;
		if (arr[i]==Editable.INPUT) return Editable.INPUT;
	}
  } catch (e) {
  	alert(e.message);
  }
  return false;
}

Editable.br2nl = function(str) { 
  if (isIE()) return str.replace(/<br\s*\/?>/mgi,"\r\n"); 
  else return str.replace(/<br\s*\/?>/mgi,"\n"); 
}
Editable.nl2br = function(str) { 
  if (isIE()) return str.replace(/\r\n/g, '<br/>'); 
  return str.replace(/\n/g, '<br/>'); 
}

Editable.bubbleToTarget = function(obj, tagName, name, isExact)
{
  if (!name || !tagName) return obj;
  try {
    if (obj.tagName==tagName && Editable.stringCompare(obj.getAttribute("name"), name, isExact)) return obj;

    if (obj.tagName=="HTML") return null;
    while (obj && obj.tagName!="BODY") {
	  obj = obj.parentNode;
  	  if (obj && obj.tagName==tagName && (Editable.stringCompare(obj.getAttribute("name"), name, isExact))) return obj;
    }
  } catch (e) { alert("bubble" + e.message); /*TODO*/ }
  return null;	
}

Editable.stringCompare = function(haystack, needle, isExact)
{
  if (!haystack) return false;
  if (isExact) return haystack==needle;
  else return haystack.indexOf(needle)>-1;
}
 
Jalava._modules['Editable'] = true;