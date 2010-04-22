/* v0.9.0beta */

// 201208: Added generateFigure() method to support loading of Blocks/Connections
// 201208: Added createBlockObject() method to delegate instantiation of Block object
// 201208: Modified activate() and dropTarget() to add Connection/Block manually to diagram

function PaletteFactory() {
  this.templates = new Array();	// template cache
}

/*
 * Override this method to return a different Connection object.
 * By default, this PaletteFactory will always return a DirectedConnection.
 */
PaletteFactory.prototype.createConnectionObject = function(id) {
	return new DirectedConnection();
}

PaletteFactory.prototype.defaultText = "&nbsp;";

/*
 * This method is invoked when the user clicks on a palette tool 
 * of type CLICK_TOOL
 */
PaletteFactory.prototype.activate = function(objId) {

  /* 
   * this is not a bug - by default, this method will assume that
   * a connection is to be made, and will behave accordingly.
   * If you wish for activate to perform some other action, then you should
   * override this method to check the objId and perform the appropriate 
   * if-else conditions.
   */
  //if (objId=="connection" || true) {
if (objId=="connection") {
	
    Jalava.factory = this;  // store the factory
    Jalava.temp_var.objId = objId;
	
	// mouse move handler
	Jalava.diagram.container.onmousemove = function(event) {		
	  if (!event) event = window.event;
	  var obj = DOM.getEventTarget(event, "figure");
	  var fig = obj ? Jalava.diagram.getFigure(obj.id) : null;
  	  if (!obj || !fig || fig.clazz!="Block") {
	  	if (Jalava.temp_var.outline) { 
		  Jalava.diagram.container.removeChild(Jalava.temp_var.outline);
	      delete Jalava.temp_var.outline;	
		}
        return false;
	  }
	  
    if (fig.isConnectable()) {
      var mousePos = Jalava.diagram.getRelativeMousePos(event);
      var anchorIdx = Connection.findNearestAnchor(mousePos, fig);
      
      if (!Jalava.temp_var.outline) {
        Jalava.temp_var.outline = document.createElement("DIV");
        Jalava.temp_var.outline.className = "anchorHover";
        Jalava.diagram.container.appendChild(Jalava.temp_var.outline);
      }
      Jalava.temp_var.outline.style.left = fig.x + fig.anchors[anchorIdx].x - 5;
      Jalava.temp_var.outline.style.top = fig.y + fig.anchors[anchorIdx].y - 5;
    }
	  
	return false;
  }

	
	// mouse down handler
	Jalava.mousedownHandler = Jalava.returnfalse;
	Jalava.diagram.container.onmouseup = function(event) {
	
	  if (!event) event = window.event;
	  var obj = DOM.getEventTarget(event, "figure");

      if (!obj) { Palette.prototype.deactivate(); return false; }
	  var fig = Jalava.diagram.getFigure(obj.id);
	  if (!fig) { Palette.prototype.deactivate(); return false; }
	  
	  if (Jalava.temp_var.outline) { 
		Jalava.diagram.container.removeChild(Jalava.temp_var.outline);
	    delete Jalava.temp_var.outline;	
	  }
	  Jalava.diagram.container.onmousemove = "";
	  Jalava.diagram.container.onmouseup = Palette.prototype.deactivate;
	  
	  var mousePos = Jalava.diagram.getRelativeMousePos(event); 
      var anchorIdx = Connection.findNearestAnchor(mousePos, fig);
	  	  
	  //var conn = objId=="connection" ? new Connection() : new DirectedConnection();
	  var conn = Jalava.factory.createConnectionObject(Jalava.temp_var.objId);
	  Jalava.diagram.addFigure(conn);  // modified to add manually to diagram
      conn.setSource(fig, anchorIdx);

	  Jalava.temp_var.connection = conn;
	  var anchor = new Object();
	  anchor.id = "anchor" + (conn.numAnchors-1);
	  try {
		Jalava.temp_var.IEDrag = false;
	  	Jalava.temp_var.selected = conn;
		conn.select();
		Jalava.resizeStart(event, anchor);
		conn.resize(event);
	  } catch (e) {
	  	alert(e.message);
	  }
	  DOM.cancelBubble(event);

	  return false;
	}  
  }	
  else if (objId=="debug") {
  	Jalava.diagram.persist();
  }
}

/*
 * Deactivates a Palette tool of type CLICK_TOOL
 */
PaletteFactory.prototype.deactivate = function(objId) {
  /*
   * default implementation assumes tool is a Connection tool, and 
   * performs action based on that assumption
   */
  if (objId=="connection" || true) {
  	delete Jalava.temp_var.objId;
	delete Jalava.temp_var.IEDrag;
  	if (Jalava.temp_var.outline) { 
	  Jalava.diagram.container.removeChild(Jalava.temp_var.outline);
      delete Jalava.temp_var.outline;	
	}	
	Jalava.diagram.container.onmousemove = "";
	Jalava.diagram.container.onmouseup = "";
	Jalava.mousedownHandler = Jalava.returntrue;
	
	if (Jalava.temp_var.connection) {
	  if (Jalava.propertyPage)
	    Jalava.temp_var.connection.addPropertyChangeListener(Jalava.propertyPage);
	  Jalava.temp_var.connection.select();
	  delete Jalava.temp_var.connection;
	}
  }  
}

/*
 * This method is invoked when the user drags a component of type
 * DRAG_TOOL from the Palette. Typically, you should always override
 * this method to instantiate your own templates.
 * 
 * Note that this method only generates a 'template' which the user drags
 * around - when the user drops the template onto the diagram, another method
 * dropTarget() is invoked. You should also override that method.
 */
PaletteFactory.prototype.generateTemplate = function(objId) {
  
  // check the template cache
  if (this.templates[objId]) return this.templates[objId];
  
  // default behaviour
  var ele = this.createContent(objId, false);
  if (!ele) return;
  
  this.templates[objId] = GradientBlock.prototype.generateTemplate(ele, null, null, 100, 50, null, "http://marinoflow.appspot.com/img/rect/rect_template.gif"); 	
  return this.templates[objId];

}

/*
 * This method is invoked when the user drops a Figure onto the diagram.
 * This default implementation will instantiate a plain GradientBlock. You
 * should override this method to generate your own figures.
 */
PaletteFactory.prototype.dropTarget = function(evt, objId, left, top) {
  
  // try and determine the drop location
  //var mousePos = diagram.getRelativeMousePos(evt);
  var x = left - Jalava.diagram.container.offsetLeft;
  var y = top - Jalava.diagram.container.offsetTop;
  
  if (x<0 || y<0) return;
  
  var anchors  = this.generateAnchors(objId, 100, 50);
     
  var ele = this.createContent(objId, true);
  if (!ele) return;
  
  var block = new GradientBlock(ele, x+50, y+25, 100, 50, anchors, null, "rect", "transparent");
  Jalava.diagram.addFigure(block);   // modified to add manually to diagram
  block.setBorderStyle("solid");
  block.setBorderColor("#000000");
  block.setBorder(1);
  if (Jalava.propertyPage)
    block.addPropertyChangeListener(Jalava.propertyPage);
  Jalava.deselect();
  Jalava.temp_var.selected = block;
  block.select();
  return block;
   
}

/*
 * Allow creation of a Figure from persisted data
 */
PaletteFactory.prototype.generateFigure = function(obj) {
	
  if (obj.clazz=="Block") {	
    var anchors = this.generateAnchors(obj.type, obj.width, obj.height);
  //  var block = new GradientBlock(null, obj.x, obj.y, obj.width, obj.height, anchors, null, "rect", "transparent");
    var block = this.createBlockObject();
    block.load(obj);
   

    // add custom handlers to the block
    var span = DOM.findNodeByName(block.element, "SPAN", "mytextarea", true);
  //  confirm("generate span trovato? "+ span);
 // span.className = "editable"; // ?????
    span.ondblclick = function(event){
      TextEdit.invoke(event);
    }
    
    if (Jalava.propertyPage) block.addPropertyChangeListener(Jalava.propertyPage);
    
    Jalava.deselect();
    
	Jalava.diagram.addFigure(block,"absolute");
        //	alert("block " + block.x);
	
	
    return block;
  }
  else {	// Connection object
    var conn = this.createConnectionObject();
  	conn.load(obj);
     //   alert(" conn " +conn);
	Jalava.diagram.addFigure(conn,"default");
    if (Jalava.propertyPage) conn.addPropertyChangeListener(Jalava.propertyPage);
	return conn;
  }
}

PaletteFactory.prototype.createBlockObject = function() {
  return new GradientBlock(null, 0, 0, 10, 10, null, null, "rect", "transparent");
}


/*
 * If all you wish to do is change the content of the GradientBlock,
 * then you only need to override this method.
 */
PaletteFactory.prototype.createContent = function(objId, real) {
  if (objId=="rect") {
    var ele = DOM.createElement("DIV", "a");
    ele.className = "hello";
    var span = DOM.createElement("SPAN", "mytextarea");
    span.innerHTML = this.defaultText;
	if (real) {
      span.className = "editable";
      span.ondblclick = function(event) { TextEdit.invoke(event); }
	}
    ele.appendChild(span);
	return ele;
  }
  else return;	
}
	
PaletteFactory.prototype.generateAnchors = function(objId, width, height) {
  var anchors = new Array();
  if (objId=="rect" || true) {
    var i = 0;
	var offsetX = width / 2;
	var offsetY = height / 2;
    anchors[i] = new Object();
    anchors[i].x = -offsetX;
    anchors[i++].y = -offsetY;
	anchors[i] = new Object();
    anchors[i].x = 0;
    anchors[i++].y = -offsetY;
	anchors[i] = new Object();
    anchors[i].x = offsetX;
    anchors[i++].y = -offsetY;
	anchors[i] = new Object();
    anchors[i].x = offsetX;
    anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = offsetX;
    anchors[i++].y = offsetY;
	anchors[i] = new Object();
    anchors[i].x = 0;
    anchors[i++].y = offsetY;
	anchors[i] = new Object();
    anchors[i].x = -offsetX;
    anchors[i++].y = offsetY;
	anchors[i] = new Object();
    anchors[i].x = -offsetX;
    anchors[i++].y = 0;
    
  }
  return anchors;
}
	
Jalava._modules['PaletteFactory'] = true;
