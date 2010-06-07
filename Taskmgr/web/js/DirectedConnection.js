/* v0.9.0beta */

// 201208: Added persistance functionality.

/*
 * Represents a connection between two Blocks, with arrows.
 */
function DirectedConnection() {
  DirectedConnection.parent.apply(this);

  this.color = "#000000";
  this.startArrowStyle = 'none';
  this.startArrowSize = 10;
  this.endArrowStyle = 'arrow';
  this.endArrowSize = 10;
 
  this.lineStyle = "solid";
  this.size = "small";
  this.createArrowHead();
  
  this.label = this.createLabel();
  this.element.appendChild(this.label);
}
Jalava.copyPrototype(DirectedConnection, Connection);

DirectedConnection.IMAGEPATH = "/img/Connection/";

DirectedConnection.prototype.createLabel = function(labeltext) {
  var label = DOM.createElement("DIV", "mytextarea");
  label.setAttribute("id","label");
 // var text = "";
//  label.innerHTML = labeltext ? labeltext : text;
  label.innerHTML = "";
  label.style.position = "absolute";
  label.style.margin = "3px";
  label.style.color = "black";
  label.className = "editable";
  label.style.fontSize ="12px";
 
 // label.ondblclick = function(event) { TextEdit.invoke(event); }
  return label;
}

DirectedConnection.prototype.setLabel = function(labeltext) {
  var label = DOM.findNodeByName(this.element, "DIV", "mytextarea", true);
  if (label) label.innerHTML = labeltext=="" ? "&nbsp;" : labeltext;
}

DirectedConnection.prototype.redraw = function() {

  for (var i = 1; i < this.numAnchors; i++) {
    var xx1 = Math.min(this.anchors[i].x, this.anchors[i - 1].x);
    var xx2 = Math.max(this.anchors[i].x, this.anchors[i - 1].x);
    var yy1 = Math.min(this.anchors[i].y, this.anchors[i - 1].y);
    var yy2 = Math.max(this.anchors[i].y, this.anchors[i - 1].y);
    
    if (!this.segment[i]) this.segment[i] = this.createSegment();
    this.segment[i].style.width = xx1 == xx2 ? this.halfBrushWidth*2 : xx2 - xx1 + this.halfBrushWidth*2;
    this.segment[i].style.height = yy1 == yy2 ? this.halfBrushWidth*2 : yy2 - yy1 + this.halfBrushWidth*2;
    this.segment[i].style.top = yy1 - this.halfBrushWidth;
    this.segment[i].style.left = xx1 - this.halfBrushWidth;

	if (this.lineStyle=="solid") {
	  this.segment[i].style.backgroundColor = this.color;
	  this.segment[i].style.backgroundImage = "";
	}
	else {
	  this.segment[i].style.backgroundColor = "transparent";
	  this.segment[i].style.backgroundImage = 
	  				xx1 == xx2 ? "url(" + DirectedConnection.IMAGEPATH + this.lineStyle + "_v_" + DirectedConnection.COLOR_INDEX[this.color] + "_" + this.size + ".gif)"  
					           : "url(" + DirectedConnection.IMAGEPATH + this.lineStyle + "_h_" + DirectedConnection.COLOR_INDEX[this.color] + "_"+ this.size + ".gif)";  				
	  this.segment[i].style.backgroundRepeat = "repeat";
	}
  }
	
  this.drawArrow("end");
  this.drawArrow("start");

  
  for (var i = this.numAnchors; i < Connection.TOTAL_LINE_ANCHORS; i++) {
    if (this.segment[i]) {
	  this.element.removeChild(this.segment[i]);
      delete this.segment[i];
    }
  }
      
  var midpt = Math.round(this.numAnchors / 2) - 1;
  this.label.style.top = this.anchors[midpt].y;
  this.label.style.left = this.anchors[midpt].x;  
  
}

DirectedConnection.prototype.drawArrow = function(startOrEnd) {
  
  var theArrow,as,halfArrow,last,secondlast,segment;
  
  if (startOrEnd == "end") {
    theArrow = this.endArrow;
    as = this.endArrowStyle=="none" ? 0 : this.endArrowSize;
    halfArrow = this.endArrowStyle=="none" ? 0 : this.endArrowSize/2 - 2;
    last = this.numAnchors-1;
    secondlast = last - 1;
	segment = this.segment[last];
  }
  else {
    theArrow = this.startArrow;
    as = this.startArrowStyle=="none" ? 0 : this.startArrowSize;
    halfArrow = this.startArrowStyle=="none" ? 0 : this.startArrowSize/2;
    last = 0;
    secondlast = 1;
	segment = this.segment[1];
  }
   
  var colorpos = DirectedConnection.COLOR_POS[this.color];
  if (!colorpos) colorpos = 0;
  var yy = colorpos * as * 2;
  var offsetX, offsetY;

  if (this.anchors[last].x > this.anchors[secondlast].x) {	// left to right
    DirectedConnection.clipImage(theArrow.firstChild, yy+as,as,yy+as*2,0);
	offsetX = as;offsetY = as/2;
    // account for the arrowhead
    var w = parseInt(segment.style.width);
    var arrowW = halfArrow>w ? w : halfArrow;
    segment.style.width = w - arrowW;
  }	
  else if (this.anchors[last].x < this.anchors[secondlast].x) {	// right to left
    DirectedConnection.clipImage(theArrow.firstChild, yy+as,yy+as*2,as*2,as);	
	offsetX = 0;offsetY = as/2;
    // account for the arrowhead
    var w = parseInt(segment.style.width);
    var arrowW = halfArrow>w ? w : halfArrow;
    segment.style.width = w - arrowW;
	segment.style.left = parseInt(segment.style.left) + arrowW;
  }	
  else if (this.anchors[last].y > this.anchors[secondlast].y) { // top to bottom
    DirectedConnection.clipImage(theArrow.firstChild, yy,as,yy+as,0);	
	offsetX = as/2;offsetY = as; // - this.halfBrushWidth;
    // account for the arrowhead
    var h = parseInt(segment.style.height);
    var arrowH = halfArrow>h ? h : halfArrow;
    segment.style.height = h - arrowH;
  }	
  else { 
    DirectedConnection.clipImage(theArrow.firstChild, yy,as*2,yy+as,as);	
	offsetX = as/2;offsetY = 0;
    // account for the arrowhead
    var h = parseInt(segment.style.height);
    var arrowH = halfArrow>h ? h : halfArrow;
    segment.style.height = h - arrowH;
	segment.style.top = parseInt(segment.style.top) + arrowH;
  }	
 
  theArrow.style.top = this.anchors[last].y - offsetY;
  theArrow.style.left = this.anchors[last].x - offsetX;

}

DirectedConnection.prototype.createArrowHead = function() {
  //this.startArrow = document.createElement("DIV");
  this.startArrow = DOM.createElement("DIV", "startArrow");
  this.element.appendChild(this.startArrow);
  // default arrow
  var img = new Image();
  img.style.position = "absolute";
  img.src = DirectedConnection.IMAGEPATH + this.startArrowStyle + "_" + this.startArrowSize + ".gif";	
  img.style.height = this.startArrowSize * 32 + "px";
  img.style.width = this.startArrowSize * 2 + "px";
  this.startArrow.appendChild(img);
  this.startArrow.style.height=this.startArrowSize + "px";
  this.startArrow.style.width=this.startArrowSize + "px";
  this.startArrow.style.border='0px';
  this.startArrow.style.position = "absolute";
  this.startArrow.style.overflow = "hidden";
  if (this.startArrowStyle=="none") this.startArrow.style.display = "none";

  //this.endArrow = document.createElement("DIV");
  this.endArrow = DOM.createElement("DIV", "endArrow");
  this.element.appendChild(this.endArrow);
  // default arrow
  var img = new Image();
  img.style.position = "absolute";
  if (this.endArrowStyle=="none") {
    img.style.height = "0px";
    img.style.width = "0px";
  } else {
    img.src = DirectedConnection.IMAGEPATH + this.endArrowStyle + "_" + this.endArrowSize + ".gif";	
    img.style.height = this.endArrowSize * 32 + "px";
    img.style.width = this.endArrowSize * 2 + "px";
  }
  this.endArrow.appendChild(img);
  this.endArrow.style.height=this.endArrowSize + "px";
  this.endArrow.style.width=this.endArrowSize + "px";
  this.endArrow.style.border='0px';
  this.endArrow.style.position = "absolute";
  this.endArrow.style.overflow = "hidden";

}

DirectedConnection.prototype.bringToFront = function() {
  DirectedConnection.parent.prototype.bringToFront.apply(this);
  this.startArrow.style.zIndex = Jalava.diagram.baseZIndex + 100;
  this.endArrow.style.zIndex = Jalava.diagram.baseZIndex + 100;
}

DirectedConnection.prototype.sendToBack = function() {
  DirectedConnection.parent.prototype.sendToBack.apply(this);
  this.startArrow.style.zIndex = Jalava.diagram.baseZIndex - 1;
  this.endArrow.style.zIndex = Jalava.diagram.baseZIndex - 1;
}

DirectedConnection.prototype.setProperty = function(property, value) {
  if (property=="Line Style") {this.setLineStyle(value);}
  else if (property=="Start Arrow Style") {this.setStartArrowStyle(value);}
  else if (property=="Start Arrow Size") {this.setStartArrowSize(parseInt(value));}
  else if (property=="End Arrow Style") {this.setEndArrowStyle(value);}
  else if (property=="End Arrow Size") {this.setEndArrowSize(parseInt(value));}
  
  
  DirectedConnection.parent.prototype.setProperty.apply(this, arguments);
}

// Arrow style
DirectedConnection.prototype.getStartArrowStyle = function() {
  return this.startArrowStyle;
}
DirectedConnection.prototype.setStartArrowStyle = function(style) {
  this.startArrowStyle = style;
  if (this.startArrowStyle=="none") 
    this.startArrow.style.display = "none";
  else { 
    this.startArrow.style.display = "block";
    this.startArrow.firstChild.src = DirectedConnection.IMAGEPATH + this.startArrowStyle + "_" + this.startArrowSize + ".gif";	
  }
}
DirectedConnection.prototype.getEndArrowStyle = function() {
  return this.endArrowStyle;
}
DirectedConnection.prototype.setEndArrowStyle = function(style) {
  this.endArrowStyle = style;
  if (this.endArrowStyle=="none") 
    this.endArrow.style.display = "none";
  else { 
    this.endArrow.style.display = "block";
    this.endArrow.firstChild.src = DirectedConnection.IMAGEPATH + this.endArrowStyle + "_" + this.endArrowSize + ".gif";	
  }
}

// Arrow size
DirectedConnection.prototype.getStartArrowSize = function() {
  return this.startArrowSize;
}
DirectedConnection.prototype.getEndArrowSize = function() {
  return this.endArrowSize;
}
DirectedConnection.prototype.setStartArrowSize = function(size) {
  if (isNaN(size)) return;
  this.startArrowSize = size;
  this.startArrow.firstChild.src = DirectedConnection.IMAGEPATH + this.startArrowStyle + "_" + this.startArrowSize + ".gif";	
  this.startArrow.firstChild.style.height = this.startArrowSize * 32 + "px";
  this.startArrow.firstChild.style.width = this.startArrowSize * 2 + "px";
  this.startArrow.style.height=this.startArrowSize + "px";
  this.startArrow.style.width=this.startArrowSize + "px";
  this.redraw();
}
DirectedConnection.prototype.setEndArrowSize = function(size) {
  if (isNaN(size)) return;
  this.endArrowSize = size;
  this.endArrow.firstChild.src = DirectedConnection.IMAGEPATH + this.endArrowStyle + "_" + this.endArrowSize + ".gif";	
  this.endArrow.firstChild.style.height = this.endArrowSize * 32 + "px";
  this.endArrow.firstChild.style.width = this.endArrowSize * 2 + "px";
  this.endArrow.style.height=this.endArrowSize + "px";
  this.endArrow.style.width=this.endArrowSize + "px";
  this.redraw();
}

DirectedConnection.prototype.setLineWidth = function(w) {
  var wid = parseInt(w);
  if (!isNaN(wid) && wid > 6) 
  { 
    this.size = "big"; 
  }
  else
  { 
    this.size = "small"; 
  }
  DirectedConnection.parent.prototype.setLineWidth.apply(this, arguments);
}

DirectedConnection.prototype.getLineStyle = function() {
  return this.lineStyle;
}

DirectedConnection.prototype.setLineStyle = function(style) {
  this.lineStyle = style;
}


DirectedConnection.clipImage = function(div, top, right, bottom, left) {
  div.style.top = -top;
  div.style.left = -left;
}


/*
 * 201208: Persistance functionality
 */
DirectedConnection.prototype.save = function() {  
  var obj = DirectedConnection.parent.prototype.save.apply(this);
  
  obj.color = this.color;
  obj.startArrowStyle = this.startArrowStyle;
  obj.startArrowSize = this.startArrowSize;
  obj.endArrowStyle = this.endArrowStyle;
  obj.endArrowSize = this.endArrowSize;
  obj.lineStyle = this.lineStyle;
  obj.size = this.size;
  
  return obj;
}

DirectedConnection.prototype.load = function(obj) {  

  this.color = obj.color;
  this.startArrowStyle = obj.startArrowStyle;
  this.startArrowSize = obj.startArrowSize;
  this.endArrowStyle = obj.endArrowStyle;
  this.endArrowSize = obj.endArrowSize;
  this.lineStyle = obj.lineStyle;
  this.size = obj.size;
  
  // remove the current label and arrow head
  this.element.removeChild(this.label);
  this.element.removeChild(this.startArrow);
  this.element.removeChild(this.endArrow);
  
  var temporary = document.createElement("DIV");
  temporary.innerHTML = obj.segmentHtml;
  
  this.startArrow = DOM.findNodeByName(temporary, "DIV", "startArrow", false);
  this.endArrow = DOM.findNodeByName(temporary, "DIV", "endArrow", false);
  this.label = DOM.findNodeByName(temporary, "DIV", "mytextarea", false);
  
  if (this.startArrow) this.element.appendChild(this.startArrow);
  if (this.endArrow) this.element.appendChild(this.endArrow);
  if (this.label) {
  	this.element.appendChild(this.label);
    //this.label.ondblclick = function(event) {TextEdit.invoke(event);}
  }
 
  DirectedConnection.parent.prototype.load.apply(this, arguments);

}



DirectedConnection.COLOR_INDEX = new Object();
DirectedConnection.COLOR_INDEX['#000000'] = '000000';
DirectedConnection.COLOR_INDEX['#808080'] = '808080';
DirectedConnection.COLOR_INDEX['#800000'] = '800000';
DirectedConnection.COLOR_INDEX['#808000'] = '808000';
DirectedConnection.COLOR_INDEX['#008000'] = '008000';
DirectedConnection.COLOR_INDEX['#008080'] = '008080';
DirectedConnection.COLOR_INDEX['#000080'] = '000080';
DirectedConnection.COLOR_INDEX['#800080'] = '800080';
DirectedConnection.COLOR_INDEX['#c0c0c0'] = 'c0c0c0';
DirectedConnection.COLOR_INDEX['#ff0000'] = 'ff0000';
DirectedConnection.COLOR_INDEX['#ffff00'] = 'ffff00';
DirectedConnection.COLOR_INDEX['#00ff00'] = '00ff00';
DirectedConnection.COLOR_INDEX['#00ffff'] = '00ffff';
DirectedConnection.COLOR_INDEX['#0000ff'] = '0000ff';
DirectedConnection.COLOR_INDEX['#ff00ff'] = 'ff00ff';
DirectedConnection.COLOR_INDEX['#ffffff'] = 'ffffff';

DirectedConnection.COLOR_POS = new Object();
DirectedConnection.COLOR_POS['#000000'] = 0;
DirectedConnection.COLOR_POS['#808080'] = 1;
DirectedConnection.COLOR_POS['#800000'] = 2;
DirectedConnection.COLOR_POS['#808000'] = 3;
DirectedConnection.COLOR_POS['#008000'] = 4;
DirectedConnection.COLOR_POS['#008080'] = 5;
DirectedConnection.COLOR_POS['#000080'] = 6;
DirectedConnection.COLOR_POS['#800080'] = 7;
DirectedConnection.COLOR_POS['#c0c0c0'] = 8;
DirectedConnection.COLOR_POS['#ff0000'] = 9;
DirectedConnection.COLOR_POS['#ffff00'] = 10;
DirectedConnection.COLOR_POS['#00ff00'] = 11;
DirectedConnection.COLOR_POS['#00ffff'] = 12;
DirectedConnection.COLOR_POS['#0000ff'] = 13;
DirectedConnection.COLOR_POS['#ff00ff'] = 14;
DirectedConnection.COLOR_POS['#ffffff'] = 15;

Jalava._modules['DirectedConnection'] = true;
