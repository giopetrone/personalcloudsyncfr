/* v0.8.1beta */

// 271208: Modified script loading mechanism in Jalava.LoadModule()

function Jalava() { }

// global variables
Jalava._modules = new Object();
Jalava.temp_var = new Object();


// global functions
Jalava.donothing = function(evt) { }
Jalava.returntrue = function(evt) { return true; }
Jalava.returnfalse = function(evt) { return false; }

// constants
Jalava.LATENCY = 10;


// objects
Jalava.diagram;
Jalava.propertyPage;


// OOP
Jalava.copyPrototype = function(descendant, parent) { 
  for (var m in parent.prototype) { 
    descendant.prototype[m] = parent.prototype[m]; 
  } 
  descendant.parent = parent;
}; 

Jalava.getClassName = function(obj) {
  if (!obj) return null;
  return obj.constructor.toString().replace(/^.*function\s+([^\s]*|[^\(]*)\([^\x00]+$/, "$1");
}

// Module Loading ///////////////////////////////////////////////////
Jalava.LoadModule = function(scriptname, idx) {
  if (!Jalava._modules[scriptname]) {   // 271208:  prevent loading of script twice
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
//    script.src = './js/' + scriptname + '.js';
  script.src = 'js/' + scriptname + '.js';
    head.appendChild(script);
    Jalava.showLoadingSign(scriptname);
  }	
  setTimeout("Jalava.checkModuleLoaded('" + scriptname + "', " + (idx+1) + ")", Jalava.LATENCY);
  return false;
}

Jalava.checkModuleLoaded = function(scriptname, idx) {
  if (Jalava._modules[scriptname]){
  	if (idx >= Jalava.Modules.length) {
	  document.getElementById("throbber").style.display = "none";
	  return;
	}
	Jalava.LoadModule(Jalava.Modules[idx], idx);
	return;
  }
  Jalava.showLoadingSign(scriptname);
  setTimeout("Jalava.checkModuleLoaded('" + scriptname + "', " + (idx) + ")", Jalava.LATENCY);
  //setTimeout('Jalava.checkModuleLoaded(' + scriptname + ')', Jalava.LATENCY);
}

Jalava.showLoadingSign = function(scriptname) {
  var div = document.getElementById("throbber");
  if (!div) {
    div = document.createElement("DIV");
	div.id = "throbber";
	div.className = "throbber";
	div.style.backgroundColor = "#FFFFFF";
	div.style.border = "1px solid #BBBBBB";
    var img = document.createElement("IMG");
    img.src = 'img/roller.gif';
	img.style.verticalAlign = "middle";
	img.style.marginRight = "5px";
    div.appendChild(img);
    div.style.position = "absolute";
    var text = document.createElement("SPAN");
    text.innerHTML = "Loading component...";
    div.appendChild(text);
    div.style.padding = "3px";
    div.style.zIndex = 300;
    document.body.appendChild(div);
  }
  else {
    div.childNodes[1].innerHTML = "Loading " + scriptname + "...";
  }

  if (div.style.display != "block") div.style.display = "block";
}


// User Interaction //////////////////////////////////////////////////
Jalava.onKeyDownEvent = function(evt) {
  if (!evt) evt = window.event;
  var key = evt.keyCode;
  if (!Jalava.temp_var.selected) return true;
  var fig = Jalava.diagram.getFigure(Jalava.temp_var.selected.id);
  if (!fig) return true;
  switch (key) {
  	case 46:
	  Jalava.diagram.removeFigure(fig.id);
	  delete Jalava.temp_var.selected;
	  break;	    
  }
  return true;
}

Jalava.onKeyPressEvent = function(evt) {
  if (!evt) evt = window.event;
  var key = evt.keyCode ? evt.keyCode : evt.charCode;
  if (!Jalava.temp_var.selected) return true;
  var fig = Jalava.diagram.getFigure(Jalava.temp_var.selected.id);
  if (!fig) return true;
  
  switch (key) {
  	case 45:
	  fig.sendBackward();
	  break;	    
  	case 43:
	  fig.bringForward();
	  break;	    
  }
  return true;
}

Jalava.mouseDown = function(evt) {
  var override = !Jalava.mousedownHandler(evt);
  if (override) return Jalava.disableHighlight();
  Jalava.select(evt); 
  return Jalava.diagram.dragStart(evt);
}


Jalava.resizeStart = function(evt, what){
  Jalava.diagram.getFigure(Jalava.temp_var.selected.id).resizeStart(evt, what);
  Jalava.mousemoveHandler = Jalava.resize;
  Jalava.mouseupHandler = Jalava.resizeEnd;
  return false;
}

Jalava.resize = function(evt){
  if (isIE() && Jalava.temp_var.IEDrag && evt.button == 0) return Jalava.resizeEnd(evt);

  Jalava.diagram.getFigure(Jalava.temp_var.selected.id).resize(evt);
  return false;
}

Jalava.resizeEnd = function(evt){
try {  
  Jalava.diagram.getFigure(Jalava.temp_var.selected.id).resizeEnd();
  Jalava.mouseupHandler = Jalava.donothing;
  Jalava.mousemoveHandler = Jalava.returnfalse;
  } catch (e) {
  	alert("resizeEnd : " + Jalava.temp_var.selected + " " + e.message);
  }
}

Jalava.select = function(evt){
  if (DOM.getEventTarget(evt, "anchor")) return;

  Jalava.deselect(evt);
  Jalava.temp_var.selected = DOM.getEventTarget(evt, "figure");
  if (Jalava.temp_var.selected==null) return;
  if (!Jalava.temp_var.selected.id) {
 	Jalava.temp_var.selected = null;
	return;
  }
  
  var fig = Jalava.diagram.getFigure(Jalava.temp_var.selected.id);
  if (!fig) {
  	Jalava.temp_var.selected = null;
	return;
  }
  
  fig.hideAnchors(true);
  fig.select(evt);
  
  return false;
}

Jalava.deselect = function(evt){
  if (Jalava.temp_var.selected) {
    Jalava.diagram.getFigure(Jalava.temp_var.selected.id).deselect();
    Jalava.temp_var.selected = null;
  }
}

Jalava.hover = function(evt, off){
  var blk = DOM.getEventTarget(evt, "figure");
  if (blk && blk.id) {
    if (off) Jalava.diagram.getFigure(blk.id).hideAnchors(true);
    else if (blk != Jalava.temp_var.selected) Jalava.diagram.getFigure(blk.id).showAnchors(true);
  }
}

// function pointers
Jalava.mouseupHandler = Jalava.donothing;
Jalava.mousemoveHandler = Jalava.returnfalse;
Jalava.mouseoverHandler = Jalava.donothing;
Jalava.mousedownHandler = Jalava.returntrue;
Jalava.disableHighlight = Jalava.returnfalse;
Jalava.keyPressHandler = Jalava.onKeyPressEvent;
Jalava.keyDownHandler = Jalava.onKeyDownEvent;


Jalava.Modules = new Array();
Jalava.Modules.push('json2');
Jalava.Modules.push('Ajax');
Jalava.Modules.push('DOM');
Jalava.Modules.push('Util');
Jalava.Modules.push('Diagram');
Jalava.Modules.push('PaletteFactory');
Jalava.Modules.push('Palette');
Jalava.Modules.push('PropertyPage');
Jalava.Modules.push('Block');
Jalava.Modules.push('Connection');
Jalava.Modules.push('Editable');
Jalava.Modules.push('Dnd');
Jalava.Modules.push('ColorPalette');
Jalava.Modules.push('NumberInput');
Jalava.Modules.push('DirectedConnection');
Jalava.Modules.push('GradientBlock');
Jalava.Modules.push('TextEdit');

Jalava.addModule = function(name) { Jalava.Modules.push(name); }


Jalava.init = function() {
  if (Jalava.ok) return; else Jalava.ok = true;	// init only once


  try {  
    //for (var i=0; i<Jalava.Modules.length; i++) {
  	  Jalava.LoadModule(Jalava.Modules[0], 0);
	//}
	setTimeout("Jalava.checkReady()", Jalava.LATENCY);	
  } catch (e) {
	alert("I am sorry, Jalava did not complete loading of all modules." + "["+e.message+"]");
  }

}

Jalava.checkReady = function() {
  var ready = true;
  for (var i=0; i<Jalava.Modules.length; i++) {
  	if (!Jalava._modules[Jalava.Modules[i]]) {
		ready = false;
	}
  }
  if (ready) 
    try {
      Jalava.hookEvent(document, "mouseup", function(event) { Jalava.mouseupHandler(event); });
      Jalava.hookEvent(document, "mousemove", function(event) { return Jalava.mousemoveHandler(event); });
      Jalava.hookEvent(document, "mousedown", function(event) { if (!Jalava.mouseDown(event)) if (event.preventDefault)  
	                                                                                           event.preventDefault();  
																							   else event.returnValue = false; });
      Jalava.hookEvent(document, "mouseover", function(event) { Jalava.hover(event); });
      Jalava.hookEvent(document, "mouseout", function(event) { Jalava.hover(event, true); });
      Jalava.hookEvent(document, "selectstart", function(event) { return true; });
      Jalava.hookEvent(document, "keydown", function(event) { return Jalava.keyDownHandler(event); });
      Jalava.hookEvent(document, "keypress", function(event) { return Jalava.keyPressHandler(event); });
      //Jalava.hookEvent(document, "click", function(event) { return false; });
	  
	  Jalava.callback();  	
	} catch (e) {
		alert("Sorry, an error occurred while running your initialization routine." + " ["+e.message+"]");
	}
  else setTimeout("Jalava.checkReady()", Jalava.LATENCY);
}


Jalava.hookEvent = function (obj, eventName, func){
  if (obj.attachEvent) {
    obj.attachEvent("on" + eventName, func);
  }
  else if (obj.addEventListener) {
    obj.addEventListener(eventName, func, false);
  }
}

/*
 * Test initialisation routine
 */
Jalava.testinit = function() {
  try {  
 	Jalava.diagram = new Diagram();
	var factory = new FlowChartPaletteFactory();
    var palette = new Palette(factory);
	palette.addItem("rect", "Processing", DRAG_TOOL, "./img/rect.gif");
	palette.addItem("diamond", "Decision", DRAG_TOOL, "./img/diamond.gif");
	palette.addItem("parallel", "Input/Output", DRAG_TOOL, "./img/parallel.gif");
	palette.addItem("ellipse", "Connection", DRAG_TOOL, "./img/ellipse.gif");
	palette.addItem("rounded", "Start/End", DRAG_TOOL, "./img/ellipse.gif");
	palette.addItem("d_connection", "Arrow Conn", CLICK_TOOL, "./img/line.gif");
	
	Jalava.propertyPage = new FlowChartPropertyPage(20, 240, 10);
  } catch (e) {
    alert(e.message);
  }
}

Jalava.start = function(callback) {
  Jalava.callback = callback;
  if (Jalava.ready) Jalava.init();
}

// immediate invocations
document.write('<link rel="stylesheet" type="text/css" href="css/styles_jalava.css" />');
Jalava.hookEvent(window, "load", function() { 
    if (Jalava.callback) Jalava.init();
	Jalava.ready=true; 
  });

