/* v0.8.1beta */

// 201208: support for multiple CLICK_TOOLs in activate()

function Palette(factory, x, y, width, height) {

  Palette.factory = factory;
  
  var row, cell;
  this.element = DOM.createElement("DIV", "paletteDiv");
  this.element.className = "palette";
  if (y) this.element.style.top = y + "px";
  if (x) this.element.style.left = x + "px";
  if (width) this.element.style.width = width + "px";
  if (height) this.element.style.height = height + "px";
  
  var table = DOM.createElement("TABLE", "palette");
  table.className ="palette";
  if (width) table.style.width = width;
  if (height) table.style.height = height;
  
  this.element.appendChild(table);
  var thead = DOM.createElement("THEAD", "title");
  table.appendChild(thead);
  row = DOM.createElement("tr", "title");
  thead.appendChild(row);
  cell = row.insertCell(-1);
  cell.innerHTML = "Palette";


  thead.onmousedown = function(event) {
  	if (!event) event = window.event;
  	var obj = DOM.getEventTarget(event, "paletteDiv");
	if (!obj) return;
	Dnd.dragStart(event, obj);
	DOM.cancelBubble(event);
  }
 
 
  this.tbody = DOM.createElement("TBODY", "title");
  table.appendChild(this.tbody);
  
  document.body.appendChild(this.element);
}

Palette.DRAG_TOOL = 1;
Palette.CLICK_TOOL = 2;

Palette.prototype.hide = function() {
  this.element.style.display = "none";
}

Palette.prototype.show = function() {
  this.element.style.display = "block";
}

Palette.prototype.addItem = function(name, displayName, mode, iconUrl) {
	
  var row = DOM.createElement("tr", name);
  this.tbody.appendChild(row);
  var cell = DOM.createElement("td", name);
  row.appendChild(cell);
  cell.id = name;
  
  DOM.hookEvent(cell, "mouseover", function(event) { switchClass(event, 'hovering') } ); 
  DOM.hookEvent(cell, "mouseout", function(event) { switchClass(event, '') } );

  if (mode==Palette.DRAG_TOOL)
    cell.onmousedown = function(event) {return Palette.prototype.grab(event); }; 
  else  	
    cell.onclick = function(event) {return Palette.prototype.activate(event); }; 
	
  if (iconUrl) {
  	var image = new Image();
	image.src = iconUrl;
	image.style.verticalAlign = "middle";
	image.style.marginRight = "5px";
	cell.appendChild(image);
  }	
  cell.appendChild(document.createTextNode(displayName));
 
}


Palette.prototype.activate = function(evt) {
  
  if (!evt) evt = window.event;	
  var obj = DOM.getEventTarget(evt);
  if (obj==null) return false;

  var objId = obj.getAttribute("id");
  
  /* 201208 : enable existance of multiple tools */
  if (Jalava.temp_var.activated) {
  	if (objId==Jalava.temp_var.activated.getAttribute("id")) {
  	  Palette.prototype.deactivate();
	  return;
	} else {
	  Palette.prototype.deactivate();
	}
  }
  Jalava.temp_var.activated = obj;
  obj.style.backgroundColor="#DDDDDD";
  Palette.factory.activate(objId);
}

Palette.prototype.deactivate = function() {
     
  if (Jalava.temp_var.activated) {
    Jalava.temp_var.activated.style.backgroundColor="#FFFFFF";
    Palette.factory.deactivate(Jalava.temp_var.activated.getAttribute("id"));  	
    delete Jalava.temp_var.activated;  	
  }
}

Palette.prototype.grab = function(evt) {
  if (!evt) evt = window.event;	
  var obj = DOM.getEventTarget(evt);
  if (obj==null) return false;

  // deactivate any currently activated tool
  Palette.prototype.deactivate();
  Jalava.temp_var.objId = obj.getAttribute("id");

  Jalava.temp_var.draggedBox = Dnd.activateDragBox(true, evt, obj);
  Dnd.cleanDragBox(Jalava.temp_var.draggedBox);

  var template = Palette.factory.generateTemplate(Jalava.temp_var.objId);  
  if (template) {
  	Jalava.temp_var.draggedBox.appendChild(template);
    template.style.display = "block";
  }

  Jalava.mousemoveHandler = Palette.prototype.drag;
  Jalava.mouseupHandler = Palette.prototype.drop;
  
  return false;
}


Palette.prototype.drag = function(evt) {
  if (!evt) evt = window.event;	
  Dnd.updateDragBox(evt,Jalava.temp_var.draggedBox); 
  return false;
}

Palette.prototype.drop = function(evt) {
  if (!evt) evt = window.event;	

  Jalava.mouseupHandler = Jalava.donothing;
  Jalava.mousemoveHandler = Jalava.returnfalse;

  Palette.factory.dropTarget(evt, Jalava.temp_var.objId, parseInt(Jalava.temp_var.draggedBox.style.left), parseInt(Jalava.temp_var.draggedBox.style.top));	
  var template = Palette.factory.templates[Jalava.temp_var.objId];
  if (template) {
      // se commento next line tempalte spostato in alto ma non sparisce!!
        template.style.display = "none";
  	document.body.appendChild(template);
  }
  Dnd.cleanDragBox(Jalava.temp_var.draggedBox);
  Jalava.temp_var.draggedBox.style.display = "none";

  delete Jalava.temp_var.draggedBox;
  delete Jalava.temp_var.objId;
}


Jalava._modules['Palette'] = true;