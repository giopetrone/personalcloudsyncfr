/* v0.8.0beta */

/*
 * Drag And Drop functionality.
 */

try {

function Dnd() {}

Dnd.updateDragBox = function(evt, draggedBox)
{
  var mousePos = getMousePos(evt);
  draggedBox.style.top = mousePos.y - draggedBox.grabbedY;
  draggedBox.style.left = mousePos.x - draggedBox.grabbedX;
}

Dnd.activateDragBox = function(yesno, evt, obj, divId)
{
  var draggedBox;
  	
  if (divId) draggedBox = document.getElementById(divId);
  else divId = "draggableDiv";
  
  if (!draggedBox) {
  	draggedBox = DOM.createElement("DIV", divId);
	draggedBox.id = divId;
	draggedBox.style.position="absolute";
	document.body.appendChild(draggedBox);
  }
  Dnd.bringToFront(draggedBox);
  
  if (yesno) {
  	draggedBox.style.display = "block";
  	Dnd.positionDragBoxRelatively(evt, obj, draggedBox);
  }
  else 
  	draggedBox.style.display = "none";	
	
  return draggedBox;
}

Dnd.cleanDragBox = function(draggedBox) { 
  try{
    if (draggedBox)
      while (draggedBox.hasChildNodes()) { draggedBox.removeChild(draggedBox.firstChild); }
  } catch (e) { /*TODO*/ }
}

Dnd.positionDragBoxRelatively = function(evt, obj, draggedBox)
{
	if (obj) {
		var posi = findPos(obj);
		var mousePos = getMousePos(evt);
		draggedBox.grabbedX = mousePos.x - posi[0];
		draggedBox.grabbedY = mousePos.y - posi[1];
	} else {
		draggedBox.grabbedX = 4;
		draggedBox.grabbedY = 4;
	}
    draggedBox.style.top = mousePos.y - draggedBox.grabbedY;
    draggedBox.style.left = mousePos.x - draggedBox.grabbedX;
}

Dnd.sendToBack = function(thisDiv)
{
  if (Jalava.temp_var.topmostDiv == thisDiv) delete Jalava.temp_var.topmostDiv;
  thisDiv.style.zIndex = 1;
}

Dnd.bringToFront = function(thisDiv)
{
	if (!Jalava.temp_var.topmostDiv) {
		Jalava.temp_var.topmostDiv = thisDiv;
		thisDiv.style.zIndex = 300;
	}
	else if (Jalava.temp_var.topmostDiv==thisDiv) { 
		// do nothing 
	}
	else {
		var z = Jalava.temp_var.topmostDiv.style.zIndex;
		thisDiv.style.zIndex = 300;
		Jalava.temp_var.topmostDiv.style.zIndex = 299;
		Jalava.temp_var.topmostDiv = thisDiv;
	}	
}

Dnd.dragStart = function(evt, what)
{
    Jalava.temp_var.grabDiv = what; //document.getElementById(what);
 	Dnd.bringToFront(Jalava.temp_var.grabDiv);
	
	var posi = findPos(Jalava.temp_var.grabDiv);
    var mousePos = getMousePos(evt);
    Jalava.temp_var.grabbedX = mousePos.x - posi[0];
    Jalava.temp_var.grabbedY = mousePos.y - posi[1]; 
	   
	Jalava.temp_var.mousemoveHandler = Jalava.mousemoveHandler;
	Jalava.temp_var.mouseupHandler = Jalava.mouseupHandler;
    Jalava.mousemoveHandler = Dnd.drag;
    Jalava.mouseupHandler = Dnd.dragEnd;   
}

Dnd.dragEnd = function(evt)
{
	Jalava.mouseupHandler = Jalava.temp_var.mouseupHandler;
	Jalava.mousemoveHandler = Jalava.temp_var.mousemoveHandler;

	Jalava.temp_var.grabDiv = null;
	Jalava.temp_var.grabbedX = null;
	Jalava.temp_var.grabbedY = null;
}

Dnd.drag = function(evt)
{
	if (isIE() && evt.button==0) return Dnd.dragEnd(evt);
	
	var mousePos = getMousePos(evt);
    Jalava.temp_var.grabDiv.style.top = mousePos.y - Jalava.temp_var.grabbedY;
    Jalava.temp_var.grabDiv.style.left = mousePos.x - Jalava.temp_var.grabbedX;

    // prevent dragging into no man land
	var windowH, windowW;
	if (window.innerHeight) windowH = window.innerHeight;
	else windowH = parseInt(document.body.offsetHeight);

	if (window.innerWidth) windowW = window.innerWidth;
	else windowW = parseInt(document.body.offsetWidth);
	
	var top = parseInt(Jalava.temp_var.grabDiv.style.top);
	var left = parseInt(Jalava.temp_var.grabDiv.style.left);

    if (top < 0) Jalava.temp_var.grabDiv.style.top = 0;
    if (left < 0) Jalava.temp_var.grabDiv.style.left = 0;
}

Jalava._modules['Dnd'] = true;

} catch (e) { alert(e.message); }