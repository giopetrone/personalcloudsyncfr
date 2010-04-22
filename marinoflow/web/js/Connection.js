/* v0.9.0beta */

// 201208 DESIGN CHANGE - Connection is no longer added to the diagram in the constructor.
//                        You must make an explicit call to Diagram.addFigure() to add
//                        the connection to the diagram.
// 201208 DESIGN CHANGE - Added load/save functionality

/*
 * Represents a connection between two Blocks.
 */
function Connection() {
  this.clazz = "Connection";
  this.element = DOM.createElement("div", "figure");
  
  // IMPORTANT Connection is no longer added to diagram in constructor
  //this.element.id = Jalava.diagram.addFigure(this);
  //this.id = this.element.id;
  this.id = Jalava.diagram.generateId();
  this.element.id = this.id;
  
  this.propertyListener = new Array();
  
  this.startX = 0;
  this.startY = 0;  
  this.endX = 0;
  this.endY = 0;
  this.startDir = Connection.VERTICAL;
  this.endDir = Connection.VERTICAL;
  
  this.anchors = new Array();
  this.anchors[0] = new Object();
  this.anchors[1] = new Object();
  this.anchors[2] = new Object();
  this.anchors[3] = new Object();
  this.anchors[4] = new Object();
  
  this.propertyListener = new Array();
  this.segment = new Array();
  
  this.halfBrushWidth = 1;
  this.color = "#000000";
}

// Global constants
Connection.VERTICAL = 1;
Connection.HORIZONTAL = 2;

Connection.START = 1;
Connection.END = 2;

Connection.TOTAL_LINE_ANCHORS = 5;


Connection.prototype.setLineWidth = function(w) {
  var wid = parseInt(w);
  if (isNaN(wid) || wid<2) wid=2;
  this.halfBrushWidth = wid / 2;
  this.redraw();	
}

Connection.prototype.setLineColor = function(colorCode) {
  this.color = colorCode;
  for (var i = 1; i < this.numAnchors; i++) {
    if (this.segment[i]) this.segment[i].style.backgroundColor = colorCode;
  }
}

Connection.prototype.setTarget = function(target, anchorIdx, suppress) {
  if (this.target) { 
    //alert("settarget " + this.target.id);
    this.target.removePropertyChangeListener(this);
    //alert(this.target.removePropertyChangeListener);
	this.target.removeTo(this);
  }
  if (target) {
    this.target = target;
    this.targetAnchor = anchorIdx;
    this.endX = target.x + target.anchors[this.targetAnchor].x;
    this.endY = target.y + target.anchors[this.targetAnchor].y;
    var dirn = target.getAnchorType("anchor" + anchorIdx);
    this.endDir = ((dirn & (Diagram.TOP | Diagram.BOTTOM)) > 0) ? Connection.VERTICAL : Connection.HORIZONTAL;
    this.findPath();
    this.redraw();
    target.addPropertyChangeListener(this);
	if (!suppress) target.addTo(this);
  }
  else 
    delete this.target;

  if (!suppress) this.firePropertyChange("connect", this.target);
}

Connection.prototype.setSource = function(source, anchorIdx, suppress) {
  if (this.source) { 
    //alert("setsource");
    this.source.removePropertyChangeListener(this);
    this.source.removeFrom(this);
  }
  if (source) {
    this.source = source;
    this.sourceAnchor = anchorIdx;
    this.startX = source.x + source.anchors[this.sourceAnchor].x;
    this.startY = source.y + source.anchors[this.sourceAnchor].y;
    var dirn = source.getAnchorType("anchor" + anchorIdx);
    this.startDir = ((dirn & (Diagram.TOP | Diagram.BOTTOM)) > 0) ? Connection.VERTICAL : Connection.HORIZONTAL;
    this.findPath();
    this.redraw();
    source.addPropertyChangeListener(this);
	if (!suppress) source.addFrom(this);
  }
  else {
    delete this.source;
  }

  if (!suppress) this.firePropertyChange("connect", this.source);
}

Connection.prototype.firePropertyChange = function(property, value){
  var n = this.propertyListener.length;
  for (var i = 0; i < n; i++) {
    this.propertyListener[i].propertyChange(this, property, value);
  }
}

Connection.addToArray = function(array, obj) {
  var n = array.length;
  for (var i = 0; i < n; i++) {
    if (obj == array[i]) return;
  }	
  array.push(obj);	
}

Connection.prototype.addPropertyChangeListener = function(callback){
  //this.propertyListener.push(callback);
  Connection.addToArray(this.propertyListener, callback);		// prevent repetition
}

Connection.prototype.removePropertyChangeListener = function(callback){
  var list = new Array();
  var n = this.propertyListener.length;
  for (var i = 0; i < n; i++) {
    if (callback != this.propertyListener[i]) 
      list.push(this.propertyListener[i]);
  }
  delete this.propertyListener;
  this.propertyListener = list;
}

Connection.prototype.propertyChange = function(firer, property, value) {
  var show = true;
  if (property == "position" || property == "size") {
    if (this.source == firer) {
      this.startX = firer.x + firer.anchors[this.sourceAnchor].x;
      this.startY = firer.y + firer.anchors[this.sourceAnchor].y;
	  show = false;
      //this.findPath();
      //this.redraw();
    }
    else if (this.target == firer) {
      this.endX = firer.x + firer.anchors[this.targetAnchor].x;
      this.endY = firer.y + firer.anchors[this.targetAnchor].y;
	  show = false;
      //this.findPath();
      //this.redraw();
    }
	else return;
  } 
  else if (property == "delete") {
  	show = false;
  	if (this.source == firer) { this.setSource(null); }
  	if (this.target == firer) { this.setTarget(null); }
  }
  else return;
  
  this.findPath();
  this.redraw();
  if (show) this.showAnchors();
}

Connection.prototype.setProperty = function(property, value) {
  if (property=="Line Color") { this.setLineColor(value); }
  else if (property=="Line Width") { this.setLineWidth(value); }
  this.findPath();
  this.redraw();	
  this.showAnchors();
}

Connection.prototype.showProperties = function(page) {
  page.update("Line Color", this.color);
  page.update("Line Width", this.halfBrushWidth*2);
}

Connection.prototype.redraw = function() {
  for (var i = 1; i < this.numAnchors; i++) {
    var xx1 = Math.min(this.anchors[i].x, this.anchors[i - 1].x);
    var xx2 = Math.max(this.anchors[i].x, this.anchors[i - 1].x);
    var yy1 = Math.min(this.anchors[i].y, this.anchors[i - 1].y);
    var yy2 = Math.max(this.anchors[i].y, this.anchors[i - 1].y);
    

    if (!this.segment[i]) this.segment[i] = this.createSegment();
	/*
    this.segment[i].style.width = xx1 == xx2 ? this.halfBrushWidth*2 : xx2 - xx1 + this.halfBrushWidth;
    this.segment[i].style.height = yy1 == yy2 ? this.halfBrushWidth*2 : yy2 - yy1 + this.halfBrushWidth;
    this.segment[i].style.top = yy1 == yy2 ? yy1 - this.halfBrushWidth : yy1;
    this.segment[i].style.left = xx1 == xx2 ? xx1 - this.halfBrushWidth : xx1;
    */
    this.segment[i].style.width = xx1 == xx2 ? this.halfBrushWidth*2 : xx2 - xx1 + this.halfBrushWidth*2;
    this.segment[i].style.height = yy1 == yy2 ? this.halfBrushWidth*2 : yy2 - yy1 + this.halfBrushWidth*2;
    this.segment[i].style.top = yy1 - this.halfBrushWidth;
    this.segment[i].style.left = xx1 - this.halfBrushWidth;
	this.segment[i].style.backgroundColor = this.color;
  }
  for (var i = this.numAnchors; i < Connection.TOTAL_LINE_ANCHORS; i++) {
    if (this.segment[i]) {
      //diagram.removeElement(this.segment[i]);
	  this.element.removeChild(this.segment[i]);
      delete this.segment[i];
    }
  }
}

Connection.prototype.bringToFront = function() {
  for (var i = 1; i < this.numAnchors; i++) {
    if (this.segment[i]) this.segment[i].style.zIndex = Jalava.diagram.baseZIndex + 100;
  }	
}

Connection.prototype.sendToBack = function() {
  for (var i = 1; i < this.numAnchors; i++) {
    if (this.segment[i]) this.segment[i].style.zIndex = Jalava.diagram.baseZIndex - 1;
  }	
}

Connection.prototype.select = function(evt){
  this.showAnchors();
  this.bringToFront();
  this.firePropertyChange("select", null);
}

Connection.prototype.deselect = function(){
  this.hideAnchors();
  this.sendToBack();
  this.firePropertyChange("deselect", null);
}

Connection.prototype.showAnchors = function(hovering){
  var type = hovering ? "hover" : "anchor";
  for (var i = 0; i < this.numAnchors; i++) {
    var anchor = document.getElementById(type + i);
    anchor.style.top = this.anchors[i].y - 3 + "px";
    anchor.style.left = this.anchors[i].x - 3 + "px";
    anchor.style.display = "block";
  }
  for (var i = this.numAnchors; i < Connection.TOTAL_LINE_ANCHORS; i++) {
    var anchor = document.getElementById(type + i);
    anchor.style.display = "none";
  }
}

Connection.prototype.hideAnchors = function(hovering){
  var type = hovering ? "hover" : "anchor";
  for (var i = 0; i < Connection.TOTAL_LINE_ANCHORS; i++) {
    var anchor = document.getElementById(type + i);
    anchor.style.display = "none";
  }
}

Connection.prototype.resizeStart = function(evt, what){
  if (what.id == "anchor0") Jalava.temp_var.mode = Connection.START;
  else if (what.id == ("anchor" + (this.numAnchors - 1)))  Jalava.temp_var.mode = Connection.END;

  Jalava.temp_var.target = this.target;
  Jalava.temp_var.source = this.source;
  
  return false;
}

Connection.prototype.resize = function(evt){
  //var mousePos = calibrate(getMousePos(evt));
  var mousePos = Jalava.diagram.getRelativeMousePos(evt);
  
  var block = DOM.getEventTarget(evt, "figure");
  var currBlock = block ? Jalava.diagram.getFigure(block.id) : null;
  var anchorIdx = currBlock && 
                  currBlock.clazz=="Block" && 
				  currBlock.isConnectable(this, Jalava.temp_var.mode==Connection.START)? 
				    Connection.findNearestAnchor(mousePos, currBlock) : -1;
  
  // snap to the nearest anchor
  if (anchorIdx >= 0 &&
       ((Jalava.temp_var.mode == Connection.START && this.target != currBlock) ||
        (Jalava.temp_var.mode == Connection.END && this.source != currBlock))) {
    if (Jalava.temp_var.mode == Connection.START) {
	  this.setSource(currBlock, anchorIdx, true);
    }
    else if (Jalava.temp_var.mode == Connection.END) {
	  this.setTarget(currBlock, anchorIdx, true);
    }
  }
  // follow the mouse cursor
  else {
    if (Jalava.temp_var.mode == Connection.START) {
      this.startX = Jalava.diagram.bound(mousePos.x - 4 - this.halfBrushWidth, Diagram.LEFT|Diagram.RIGHT);
      this.startY = Jalava.diagram.bound(mousePos.y - 4 - this.halfBrushWidth, Diagram.TOP|Diagram.BOTTOM);
	  if (this.source) this.source.removeFrom(this);
      delete this.source;
      delete this.sourceAnchor;
    }
    else if (Jalava.temp_var.mode == Connection.END) {
      this.endX = Jalava.diagram.bound(mousePos.x - 4 - this.halfBrushWidth, Diagram.LEFT|Diagram.RIGHT);
      this.endY = Jalava.diagram.bound(mousePos.y - 4 - this.halfBrushWidth, Diagram.TOP|Diagram.BOTTOM);
	  if (this.target) this.target.removeTo(this);
      delete this.target;
      delete this.targetAnchor;
    }
    else return false;
  }
  
  this.findPath();
  this.redraw();
  this.showAnchors();

  return false;
}

Connection.prototype.resizeEnd = function() {
  if (Jalava.temp_var.source) {
  	if (!this.source) {
      this.firePropertyChange("disconnect", Jalava.temp_var.source);
	  Jalava.temp_var.source.removeFrom(this);
	}
	else if (this.source!=Jalava.temp_var.source) {
	  Jalava.temp_var.source.removeFrom(this);
      this.firePropertyChange("reconnect", this.source);
	  this.source.addFrom(this);
    }
	else this.source.addFrom(this);
  }
  else if (this.source) {
  	this.firePropertyChange("connect", this.source);
    this.source.addFrom(this);
  }
  if (Jalava.temp_var.target) {
  	if (!this.target) {
      this.firePropertyChange("disconnect", Jalava.temp_var.target);
  	  Jalava.temp_var.target.removeTo(this);
    }
	else if (this.target!=Jalava.temp_var.target) {
  	  Jalava.temp_var.target.removeTo(this);
      this.firePropertyChange("reconnect", this.target);
	  this.target.addTo(this);
    }
	else this.target.addTo(this);
  }
  else if (this.target) {
  	this.firePropertyChange("connect", this.target);
    this.target.addTo(this);
  }

  Jalava.mouseoverHandler = Jalava.donothing;
  delete Jalava.temp_var.mode;
  delete Jalava.temp_var.source;
  delete Jalava.temp_var.target;
}

Connection.prototype.createSegment = function() {
  var ele = DOM.createElement("div", "segment");
  ele.id = this.id;
  //confirm ("absolute");
  ele.style.position = "absolute";
  ele.style.border = "0px solid black";
  ele.style.overflow = "hidden";
  ele.style.padding = "0px";
  
  this.element.appendChild(ele);
  return ele;
}

Connection.prototype.dispose = function() {
  this.hideAnchors();
  this.setSource(null);
  this.setTarget(null);	
  this.firePropertyChange("delete", null);
}

/*
 * This is the heart of the Connection - the algorithm that determines
 * the path taken by the connection. This is a simple implementation which
 * will divide the connection into 2 or 3 segments evenly, without consideration
 * of any intersections with other Blocks.
 */
Connection.prototype.findPath = function() {
  this.anchors[0].x = this.startX;
  this.anchors[0].y = this.startY;
  var width = this.endX - this.startX;
  var height = this.endY - this.startY;
  if (this.startDir == this.endDir) {
    if (this.startDir == Connection.VERTICAL) {	// vertical, horizontal, vertical
      this.anchors[1].x = this.startX;
      this.anchors[1].y = this.startY + height / 2;
      this.anchors[2].x = this.endX;
      this.anchors[2].y = this.startY + height / 2;
      this.anchors[3].x = this.endX;
      this.anchors[3].y = this.endY;
      this.numAnchors = 4;
      return;
    }
    else {	// horizontal, vertical, horizontal
      this.anchors[1].x = this.startX + width / 2;
      this.anchors[1].y = this.startY;
      this.anchors[2].x = this.startX + width / 2;
      this.anchors[2].y = this.endY;
      this.anchors[3].x = this.endX;
      this.anchors[3].y = this.endY;
      this.numAnchors = 4;
      return;
    }
  }
  else 
    if (this.startDir == Connection.VERTICAL) {	// vertical, horizontal
      this.anchors[1].x = this.startX;
      this.anchors[1].y = this.endY;
      this.anchors[2].x = this.endX;
      this.anchors[2].y = this.endY;
      this.numAnchors = 3;
      return;
    }
    else {		// horizontal, vertical
      this.anchors[1].x = this.endX;
      this.anchors[1].y = this.startY;
      this.anchors[2].x = this.endX;
      this.anchors[2].y = this.endY;
      this.numAnchors = 3;
      return;
    }
}
/*
 * Utility function to find the nearest anchor for a given Block, given 
 * the current mouse position.
 */
Connection.findNearestAnchor = function(mousePos, blockObj) {
  var minDist;
  var nearestAnchor = -1;
  var numAnchors = blockObj.anchors.length;
  for (var i = 0; i < numAnchors; i++) {
    // compute distance from mouse
    var distX = blockObj.x + blockObj.anchors[i].x - mousePos.x;
    var distY = blockObj.y + blockObj.anchors[i].y - mousePos.y;
    
    var dist = Math.sqrt(distX * distX + distY * distY);
    if (!minDist || dist < minDist) {
      nearestAnchor = i;
      minDist = dist;
    }
  }
  return nearestAnchor;
}



/*
 * 201208: Persistance functionality
 */
Connection.prototype.save = function() {  
  var obj = new Object();
  
  obj.clazz = this.clazz;
  obj.id = this.id;
  
  obj.startX = this.startX;
  obj.startY = this.startY;  
  obj.endX = this.endX;
  obj.endY = this.endY;
  obj.startDir = this.startDir;
  obj.endDir = this.endDir;
  
  obj.anchors = this.anchors;

  obj.segmentHtml = this.element.innerHTML;
  
  obj.halfBrushWidth = this.halfBrushWidth;
  obj.color =  this.color;
  
  obj.source = this.source ? this.source.id : null;
  obj.sourceAnchor = this.sourceAnchor;
  
  obj.target = this.target ? this.target.id : null;
  obj.targetAnchor = this.targetAnchor;

  return obj;
}

Connection.prototype.load = function(obj) {  
  this.clazz = obj.clazz;
  this.id = obj.id;
  this.element.id = this.id;
  
  this.startX = obj.startX;
  this.startY = obj.startY;
  //confirm(obj.startY);
  this.endX = obj.endX;
  this.endY = obj.endY;
  this.startDir = obj.startDir;
  this.endDir = obj.endDir;
  
  this.anchors = obj.anchors;

  // remove all the segments first
  while (this.segment.length>0) {
	var seg = this.segment.pop();
	this.element.removeChild(seg);
  }
  this.segment.push(new Object());	// dummy segment
  
  var temporary = document.createElement("DIV");  
  temporary.innerHTML = obj.segmentHtml;
  
  for (var i = 0; i < temporary.childNodes.length; i++) {
  	if (temporary.childNodes[i].getAttribute("name")=="segment") {
	  this.segment.push(temporary.childNodes[i]);
	}
  }
  for (var i=1; i<this.segment.length; i++) {
	this.element.appendChild(this.segment[i]);  	
  }
 
  this.halfBrushWidth = obj.halfBrushWidth;
  this.color = obj.color;
  
  if (obj.source) {
  	this.source = Jalava.diagram.getFigure(obj.source);
	this.sourceAnchor = obj.sourceAnchor;
	if (this.source) this.setSource(this.source, obj.sourceAnchor, false);
  }
  if (obj.target) {
  	this.target = Jalava.diagram.getFigure(obj.target);
	this.targetAnchor = obj.targetAnchor;
	if (this.target) this.setTarget(this.target, obj.targetAnchor, false);
  }
}


Jalava._modules['Connection'] = true;
