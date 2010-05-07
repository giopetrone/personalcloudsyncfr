/* v0.9.0beta */

// 201208: BUGFIX - prevent repetition in from, to array
// 201208: BUGFIX - prevent repetition in propertyListener array

// 201208: DESIGN CHANGE - The block is no longer added to the diagram in the
//                         constructor. This is to allow the block to be loaded
//                         from external source, then added to the diagram separately.

// 201208: DESIGN CHANGE - added load/save functionality

/*
 * Represents a single object on the diagram. Blocks can have 
 * any number of incoming and outgoing connections.
 * 
 * As part of the constructor, you can specify the inital position and size.
 * Position is defined as the centre of the block. 
 * 
 * You can specify a background image, as well as a CSS class for which 
 * you can define styles. 
 * 
 * You should also supply an array of anchors - anchors allow the user to 
 * resize the block, as well as to hook Connections to the the Block as specific
 * points. Anchors are defined relative the centre of the block.
 * 
 * The 'clazz' attribute identifies this object as a Block object.
 * Child classes should retain this attribute.
 * 
 */
function Block(content, x, y, width, height, anchors, type, imagepath) {
  this.clazz = "Block";
  this.type = type;
  
  this.x = Jalava.diagram.bound(x+width/2, Diagram.LEFT|Diagram.RIGHT) - width/2;
  this.y = Jalava.diagram.bound(y+height/2, Diagram.TOP|Diagram.BOTTOM) - height/2;
  this.height = height;
  this.width = width;
  this.content = content;
  this.anchors = anchors;
  this.propertyListener = new Array();
   this.brushWidth = 0;
  this.from = new Array();
  this.to = new Array();
  this.element = this.generateTemplate(content, this.x, this.y, width, height, this.clazz, imagepath)
  this.id = Jalava.diagram.generateId();

  // IMPORTANT!!! block is no longer added to diagram
  //this.element.id = Jalava.diagram.addFigure(this);
  //this.id = this.element.id;
  
  this.element.id = this.id;
  this.element.content = this.content;
  this.element.type = this.type;
 
}

// Global constants
Block.THRESHOLD = 10;
Block.MINSIZE = 20;

/*
 * Generates the actual DOM element that makes up this Block
 */
Block.prototype.generateTemplate = function(content, x, y, width, height, clazz, imagepath, status) {
  var element = DOM.createElement("div", "figure");
  element.style.zIndex = Jalava.diagram.baseZIndex;
  element.className = clazz ? clazz : "block";
  element.style.width = width;
  element.style.height = height;
  element.style.top = y ? (y - height/2) : 0;
  element.style.left = x ? (x  - width/2) : 0;
  element.style.borderStyle="none";
  element.style.borderColor="transparent";
  element.style.borderWidth="0px";
  var span = DOM.createElement("INPUT", "share");
  span.setAttribute("id","share");
  span.setAttribute("type","hidden");
  var button = DOM.createElement("INPUT", "button");
  button.setAttribute("id","button");
  button.setAttribute("value","click");
//  span.innerHTML = "CIAO!!!!!!!!";
  document.body.appendChild(element);
  element.appendChild(span);
 // element.appendChild(button);
    
  // populate the figure  
  if (content) {
  	var e = element.appendChild(content);
	e.style.zIndex = 2;
	e.style.position = "absolute";
	e.style.width = width;
	e.style.top = (height - e.offsetHeight) / 2;
  }  
  if (imagepath) {
    var backgroundImg = DOM.createElement("img", "background");
    backgroundImg.src = imagepath;
    backgroundImg.height = height;
    backgroundImg.width = width;
	backgroundImg.style.zIndex = 1;
	backgroundImg.style.position = "absolute";
    element.appendChild(backgroundImg);
  }

  if(status){

      var s = element.appendChild(status);

  }
  
  return element;
}

/*
 * Cleans up this object
 */
Block.prototype.dispose = function() {
  this.hideAnchors();
  this.firePropertyChange("delete", null);
}

Block.prototype.addFrom = function(connection) {
  // this.from.push(connection); 
  Block.addToArray(this.from, connection);		//201208: Add to array only if not exists
}

Block.prototype.removeFrom = function(connection) {
  var list = Block.removeFromArray(this.from, connection);
  delete this.from;
  this.from = list;
}

Block.prototype.addTo = function(connection) {
  // this.to.push(connection);
  Block.addToArray(this.to, connection);		//201208: Add to array only if not exists
}

Block.prototype.removeTo = function(connection) {
  var list = Block.removeFromArray(this.to, connection);
  delete this.to;
  this.to = list;
}

Block.addToArray = function(array, obj) {
  var n = array.length;
  for (var i = 0; i < n; i++) {
    if (obj == array[i]) return;
  }	
  array.push(obj);	
}

Block.removeFromArray = function(array, obj) {
  var list = new Array();
  var n = array.length;
  for (var i = 0; i < n; i++) {
    if (obj != array[i]) 
      list.push(array[i]);
  }	
  return list;
}

/*
 * Sets the Block at position x,y which refers to the
 * center of the Block. 
 * 
 * This will fire a PropertyChange event and will also cause 
 * anchors to be displayed.
 */
Block.prototype.setPosition = function(x, y){
  this.x = x;
  this.y = y;
  this.element.style.top = y - this.height / 2 + "px";
  this.element.style.left = x - this.width / 2 + "px";
  
  this.firePropertyChange("position", {  x: x,
    									 y: y  });
  this.showAnchors();
}

/*
 * Sets the size of the Block. Takes into account the border width.
 * Depending on which browser is in use, the true height/width is
 * adjusted accordingly.
 * 
 * This will fire a PropertyChange event and will also cause 
 * anchors to be displayed.
 */
Block.prototype.setSize = function(width, height){
  var scaleX = width / this.width;
  var scaleY = height / this.height;
  
  this.width = width;
  this.height = height;
  
  var effectiveBrush = this.element.style.borderTopStyle=="none" ? 0 : this.brushWidth*2;
  var pseudobrush = isIE() ? 0 : effectiveBrush;
  var truewidth = width - effectiveBrush;
  var trueheight = height - effectiveBrush;
  
  this.element.style.width = width - pseudobrush + "px";
  this.element.style.height = height - pseudobrush + "px";
  
  // resize the content
  if (this.element.firstChild) {
    this.element.firstChild.style.width = truewidth + "px";
    this.element.firstChild.style.top = (trueheight - this.element.firstChild.offsetHeight) / 2;	
  }
  // resize the background image
  var backgroundImg = DOM.findNodeByName(this.element, "IMG", "background", false);
  if (backgroundImg) {
    backgroundImg.height = trueheight;
    backgroundImg.width = truewidth;
  }

  this.scaleAnchors(scaleX, scaleY);
  this.firePropertyChange("size", { x: this.x,
								    y: this.y,
								    width: width,
								    height: height }); 
  this.showAnchors();
}


/*
 * Fired when the Block is selected.
 */
Block.prototype.select = function(evt){
  this.showAnchors();
  this.firePropertyChange("select", null);
}

/*
 * Fired when the Block is deselected.
 */
Block.prototype.deselect = function(){
  this.hideAnchors();
  this.firePropertyChange("deselect", null);
}

/*
 * Hides the anchors.
 */
Block.prototype.hideAnchors = function(hovering){
  var type = hovering ? "hover" : "anchor";
  var numAnchors = this.anchors.length;
  for (var i = 0; i < numAnchors; i++) {
    var anchor = document.getElementById(type + i);
    anchor.style.display = "none";
  }
}

/*
 * Shows the anchors.
 */
Block.prototype.showAnchors = function(hovering){
  var type = hovering ? "hover" : "anchor";
  var numAnchors = this.anchors.length;
  for (var i = 0; i < numAnchors; i++) {
  	var anchor = document.getElementById(type + i);
    anchor.style.top = this.y + this.anchors[i].y - 2 + "px";
    anchor.style.left = this.x + this.anchors[i].x - 2 + "px";   
    anchor.style.display = "block";
  }
}

/*
 * Reposition the anchors based on the size of the Block. Fired 
 * whenever the Block is resized.
 */
Block.prototype.scaleAnchors = function(scaleX, scaleY){
  var numAnchors = this.anchors.length;
  for (var i = 0; i < numAnchors; i++) {
    this.anchors[i].y = this.anchors[i].y * scaleY;
    this.anchors[i].x = this.anchors[i].x * scaleX;
  }
}

/*
 * Fires the PropertyChange event. Current bound properies include
 * 'position', 'size', 'select', and 'deselect'.
 */
Block.prototype.firePropertyChange = function(property, value){
  var n = this.propertyListener.length;
  for (var i = 0; i < n; i++) {
    this.propertyListener[i].propertyChange(this, property, value);
  }
}

/*
 * Registers a PropertyListener to listen for changes in the
 * bound properties of this Block.
 */
Block.prototype.addPropertyChangeListener = function(callback){
  // this.propertyListener.push(callback);
  Block.addToArray(this.propertyListener, callback);	// 201208: add to array only if not exists
}

/*
 * Remove a previously registered PropertyListener
 */
Block.prototype.removePropertyChangeListener = function(callback){
  //alert(this.id + "removePropertyChangeListener");
  var list = new Array();
  var n = this.propertyListener.length;
  for (var i = 0; i < n; i++) {
    if (callback != this.propertyListener[i]) 
      list.push(this.propertyListener[i]);
  }
  delete this.propertyListener;
  this.propertyListener = list;
}

/*
 * Determine the type of a given anchor. 'name' is the name of the 
 * DOM element, which is typically 'anchorX', where X is a number.
 * 
 * Anchor can be LEFT, RIGHT, TOP or BOTTOM depending on its position
 * relative to the center of the Block.
 */
Block.prototype.getAnchorType = function(name){
  var i = parseInt(name.substring(6));
  if (isNaN(i)) 
    return 0;
  
  var leftBound = -this.width / 2 + Block.THRESHOLD;
  var rightBound = +this.width / 2 - Block.THRESHOLD;
  var topBound = -this.height / 2 + Block.THRESHOLD;
  var bottomBound = +this.height / 2 - Block.THRESHOLD;
  
  var result = 0;
  if (this.anchors[i].x <= leftBound) 
    result |= Diagram.LEFT;
  if (this.anchors[i].x >= rightBound) 
    result |= Diagram.RIGHT;
  if (this.anchors[i].y >= bottomBound) 
    result |= Diagram.BOTTOM;
  if (this.anchors[i].y <= topBound) 
    result |= Diagram.TOP;
  
  return result;
}

/*
 * Determine if the given connection is allowed to connect
 * to this Block
 */
Block.prototype.isConnectable = function(connection) {
  return true;	
}

/*
 * Fired when one of the anchors on the Block is first dragged.
 */
Block.prototype.resizeStart = function(evt, what){
  var posi = Jalava.diagram.findPos(what);
  var mousePos = getMousePos(evt);
  Jalava.temp_var.mode = this.getAnchorType(what.id);
  
  Jalava.temp_var.grabbedX = mousePos.x;// - posi[0];
  Jalava.temp_var.grabbedY = mousePos.y;// - posi[1]; 
  if ((Jalava.temp_var.mode & Diagram.LEFT) > 0) {
    Jalava.temp_var.grabbedCornerX = parseInt(this.element.style.left) + this.width;
  }
  else {
    Jalava.temp_var.grabbedCornerX = parseInt(this.element.style.left);
  }
  
  if ((Jalava.temp_var.mode & Diagram.TOP) > 0) {
    Jalava.temp_var.grabbedCornerY = parseInt(this.element.style.top) + this.height;
  }
  else {
    Jalava.temp_var.grabbedCornerY = parseInt(this.element.style.top);
  }

  Jalava.temp_var.grabbedHeight = this.height;
  Jalava.temp_var.grabbedWidth = this.width;
}

/*
 * Fired when the mouse moves and an anchor is currently being dragged.
 */
Block.prototype.resize = function(evt){
  var mousePos = getMousePos(evt);
  
  var diffY = mousePos.y - Jalava.temp_var.grabbedY;
  var diffX = mousePos.x - Jalava.temp_var.grabbedX;
  var width, height;
  
  if ((Jalava.temp_var.mode & Diagram.TOP) > 0) {
    height = Jalava.temp_var.grabbedHeight - diffY;
    if (height < Block.MINSIZE) height = Block.MINSIZE;
	// prevent the Block from going out of the bounds of the container
    this.element.style.top = Jalava.diagram.bound(Jalava.temp_var.grabbedCornerY - height, Diagram.TOP);
  }
  else 
    if ((Jalava.temp_var.mode & Diagram.BOTTOM) > 0) {
      height = Jalava.temp_var.grabbedHeight + diffY;
      if (height < Block.MINSIZE) height = Block.MINSIZE;
	  // prevent the Block from going out of the bounds of the container
	  height = Jalava.diagram.bound(Jalava.temp_var.grabbedCornerY + height, Diagram.BOTTOM) - Jalava.temp_var.grabbedCornerY;
      this.element.style.top = Jalava.temp_var.grabbedCornerY;
    }
    else {
      height = Jalava.temp_var.grabbedHeight;
      this.element.style.top = Jalava.temp_var.grabbedCornerY;
    }
  
  if ((Jalava.temp_var.mode & Diagram.LEFT) > 0) {
    width = Jalava.temp_var.grabbedWidth - diffX;
    if (width < Block.MINSIZE) width = Block.MINSIZE;
	// prevent the Block from going out of the bounds of the container
    this.element.style.left = Jalava.diagram.bound(Jalava.temp_var.grabbedCornerX - width, Diagram.LEFT);
  }
  else 
    if ((Jalava.temp_var.mode & Diagram.RIGHT) > 0) {
      width = Jalava.temp_var.grabbedWidth + diffX;
      if (width < Block.MINSIZE) width = Block.MINSIZE;
	  // prevent the Block from going out of the bounds of the container
	  width = Jalava.diagram.bound(Jalava.temp_var.grabbedCornerX + width, Diagram.RIGHT) - Jalava.temp_var.grabbedCornerX;
      this.element.style.left = Jalava.temp_var.grabbedCornerX;
    }
    else {
      width = Jalava.temp_var.grabbedWidth;
      this.element.style.left = Jalava.temp_var.grabbedCornerX;
    }
  
  this.x = parseInt(this.element.style.left) + width / 2;
  this.y = parseInt(this.element.style.top) + height / 2;  
  this.setSize(width, height);
  
  return false;
}

/*
 * Fired onmouseup when an anchor was previously being dragged.
 */
Block.prototype.resizeEnd = function(){
  delete Jalava.temp_var.grabbedX;
  delete Jalava.temp_var.grabbedY;
  delete Jalava.temp_var.grabbedCornerX;
  delete Jalava.temp_var.grabbedCornerY;
  delete Jalava.temp_var.grabbedHeight;
  delete Jalava.temp_var.grabbedWidth;
  delete Jalava.temp_var.mode;
}

/*
 * Sets a particular property for this Block
 */
Block.prototype.setProperty = function(property, value){
  if (property=="Backgrd Color") { this.setBackgroundColor(value); }
  else if (property=="Border Color") { this.setBorderColor(value); }
  else if (property=="Border Style") { this.setBorderStyle(value); }
  else if (property=="Border Width") { this.setBorder(value); }
  else if (property=="Layer") { this.setLayer(parseInt(value)); }
}

/*
 * Sets the background color of the Block
 */
Block.prototype.setBackgroundColor = function(colorCode) {
  this.element.style.backgroundColor = colorCode;	
}

/*
 * Sets the border color of the Block
 */
Block.prototype.setBorderColor = function(colorCode) {
  if (colorCode=="") colorCode="transparent";
  this.element.style.borderColor = colorCode;	
}

/*
 * Gets the border color of the Block
 */
Block.prototype.getBorderColor = function() {
  return this.element.style.borderTopColor;
}

/*
 * Sets the border thickness of the Block. Because this will affect
 * the effective size of the Block, a call to setSize() is made to
 * refresh the Block size.
 */
Block.prototype.setBorder = function(w) {
  var oldwidth = this.brushWidth;
  var newwidth = parseInt(w);
  if (isNaN(newwidth)) newwidth = 0;  
  this.brushWidth = newwidth;
  this.element.style.borderWidth = newwidth + "px";	
  
  // dun bother if borderStyle is set to 'none'
  if (!this.element.style.borderStyle || 
        this.element.style.borderStyle=="" || 
		this.element.style.borderTopStyle=="none") return;
		
  if (!isIE()) {
    // rescale the block
	var diff = newwidth - oldwidth;
	this.x += diff;
	this.y += diff;
    this.setSize(this.width + diff*2, this.height + diff*2)
  } else {
  	this.setSize(this.width, this.height)
  }
}

/*
 * Gets the border thickness of the Block
 */
Block.prototype.getBorder = function() {
  var str = this.element.style.borderWidth.split(' ');
  return str[0];
}

/*
 * Sets the border style of the Block.
 */
Block.prototype.setBorderStyle = function(s) {
  this.element.style.borderStyle = s;
  if (this.brushWidth > 0) this.setBorder(this.brushWidth);
}

/*
 * Sets the background image of the Block.
 */
Block.prototype.setBackground = function(imageUrl) {
  
  var backgroundImg = DOM.findNodeByName(this.element, "IMG", "background", false);
  if (!backgroundImg) {
    backgroundImg = DOM.createElement("img", "background");
	backgroundImg.style.zIndex = 1;
	backgroundImg.style.position = "absolute";
    this.element.appendChild(backgroundImg);
  }
  backgroundImg.src = imageUrl;
  backgroundImg.height = this.height;
  backgroundImg.width = this.width;  
}

Block.prototype.getBackgroundColor = function(color) {
  return this.element.style.backgroundColor;
}

Block.prototype.setBackgroundColor = function(colorCode) {
  this.element.style.backgroundColor = colorCode;
}

/*
 * Sets the layer order of the Block. High layers are stacked on top (more 
 * visible) of lower layers
 */
Block.prototype.setLayer = function(layerNo) {
  this.element.style.zIndex = Jalava.diagram.baseZIndex + layerNo - 1;
}

/*
 * Gets the current layer order of the Block
 */
Block.prototype.getLayer = function() {
  return this.element.style.zIndex - Jalava.diagram.baseZIndex + 1;
}

/*
 * Brings the Block forward by 1 layer
 */
Block.prototype.bringForward = function() {  
  if (this.element.style.zIndex < Jalava.diagram.baseZIndex+100) // set a cap
    this.element.style.zIndex = parseInt(this.element.style.zIndex) + 1; 
  this.firePropertyChange("Layer", this.getLayer());
}

/*
 * Sends the Block backward by 1 layer
 */
Block.prototype.sendBackward = function() {  
  if (this.element.style.zIndex > Jalava.diagram.baseZIndex)   // set a cap
    this.element.style.zIndex = parseInt(this.element.style.zIndex) - 1; 
  this.firePropertyChange("Layer", this.getLayer());	
}


/*
 * 201208: Persistance functionality
 */
Block.prototype.save = function() {  
  var obj = new Object();
  obj.clazz = this.clazz;
  obj.type = this.element.type;
  obj.x = this.x;
  obj.y = this.y;
  obj.height = this.height;
  obj.width = this.width;
  obj.brushWidth = this.brushWidth;
  obj.id = this.id;

  obj.anchors = this.anchors;
  
  obj.borderStyle = this.element.style.borderStyle;
  obj.borderColor = this.element.style.borderColor;
  obj.borderWidth = this.element.style.borderWidth;
  
  obj.content = this.element.innerHTML;
  
  obj.to = new Array();
  obj.from = new Array();

  for (var i=0; i<this.to.length; i++) {
  	obj.to[i] = this.to[i].id;
  }      
  for (var i=0; i<this.from.length; i++) {
  	obj.from[i] = this.from[i].id;
  }      

  return obj;
}

Block.prototype.load = function(obj) {  
  this.clazz = obj.clazz;
  this.element.type =obj.type;
  this.brushWidth = obj.brushWidth;
  this.id = obj.id;

  this.anchors = obj.anchors;

  this.element.id = this.id;  

  this.x = obj.x;
  this.y = obj.y;

 // confirm( obj.x );

  this.element.style.width = obj.width;
  this.element.style.height = obj.height;
  this.element.style.top = obj.y - obj.height/2;
  this.element.style.left = obj.x  - obj.width/2;

  this.element.style.borderStyle = obj.borderStyle;
  this.element.style.borderColor = obj.borderColor;
  this.element.style.borderWidth = obj.borderWidth;
  this.element.innerHTML = obj.content;

  this.setSize(this.width, this.height);  
  this.hideAnchors();
}


Jalava._modules['Block'] = true;
