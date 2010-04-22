/* v0.9.0beta */

// 301208: Modified dropTarget() to add block manually to diagram.
// 301208: Override createBlockObject() to instantiate UrlGradientBlock

function FlowChartPaletteFactory() {
  FlowChartPaletteFactory.parent.apply(this);
}
Jalava.copyPrototype(FlowChartPaletteFactory, PaletteFactory);

FlowChartPaletteFactory.prototype.createContent = function(objId, real) {
  var ele = DOM.createElement("DIV", "a");
  ele.className = "hello";
  var span = DOM.createElement("SPAN", "mytextarea");
 // confirm("CREO CONTENUTO");
  span.innerHTML = "Double click to edit";
  if (real) {
    span.className = "editable";
    span.ondblclick = function(event) { TextEdit.invoke(event); }
  }
//confirm(objId);
  var img = new Image();
	img.src = "http://marinoflow.appspot.com/img/" + objId + ".gif";
       // img.src = "./demo/african/Zebra32.gif";
	img.style.width = "32px";
	img.style.height = "32px";
	img.style.verticalAlign = "middle";
	//ele.appendChild(img);

  ele.appendChild(span);
  return ele;
}

FlowChartPaletteFactory.prototype.generateTemplate = function(objId) {
  
  // check the template cache
  if (this.templates[objId]) return this.templates[objId];
  
  // default behaviour
  var ele = this.createContent(objId, false);
  if (!ele) return;
  
  this.templates[objId] = UrlGradientBlock.prototype.generateTemplate(ele, null, null, 100, 50, null, "http://marinoflow.appspot.com/img/flowchart/" + objId + "_f0f0f0_w100h50.gif"); 	
  return this.templates[objId];

}

FlowChartPaletteFactory.prototype.dropTarget = function(evt, objId, left, top) {

  // try and determine the drop location
  var x = left - Jalava.diagram.container.offsetLeft;
  var y = top - Jalava.diagram.container.offsetTop;

  if (x<0 || y<0) return;

  var anchors  = this.generateAnchors(objId, 100, 50);

  var ele = this.createContent(objId, true);
  if (!ele) return;
//confirm("sonoin dropdi flow");
  var block = new UrlGradientBlock(ele, x+50, y+25, 100, 50, anchors, null, objId, "#f0f0f0");
  Jalava.diagram.addFigure(block);	// 301208: manually add block to diagram
//Jalava.diagram.addFigure(ele);
block.setBorderStyle("none");
  if (Jalava.propertyPage)
    block.addPropertyChangeListener(Jalava.propertyPage);
  Jalava.deselect();
  Jalava.temp_var.selected = block;
  block.select();
  return block;
}

FlowChartPaletteFactory.prototype.dropTargetSAVE = function(evt, objId, left, top) {

  // try and determine the drop location
  var x = left - Jalava.diagram.container.offsetLeft;
  var y = top - Jalava.diagram.container.offsetTop;

  if (x<0 || y<0) return;

  var anchors  = this.generateAnchors(objId, 100, 50);

  var ele = this.createContent(objId, true);
  if (!ele) return;

  var block = new UrlGradientBlock(ele, x+50, y+25, 100, 50, anchors, null, objId, "#f0f0f0");
  Jalava.diagram.addFigure(block);	// 301208: manually add block to diagram
  block.setBorderStyle("none");
  if (Jalava.propertyPage)
    block.addPropertyChangeListener(Jalava.propertyPage);
  Jalava.deselect();
  Jalava.temp_var.selected = block;
  block.select();
  return block;

}

/*
 * Override PaletteFactory.prototype.createBlockObject to return a UrlGradientBlock
 */
FlowChartPaletteFactory.prototype.createBlockObject = function() {
  return new UrlGradientBlock(null, 0, 0, 10, 10, {}, null, "rect", "transparent");
}

FlowChartPaletteFactory.prototype.generateAnchors = function(objId, width, height) {
  var anchors = new Array();
  var i = 0;

  if (objId=="rect") {	
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = 0;   anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = 0;   anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 0;
  }
  else if (objId=="diamond") {	
    anchors[i] = new Object();
    anchors[i].x = 0;  anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 0 ;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = -25;
  }
  else if (objId=="parallel") {	
    anchors[i] = new Object();
    anchors[i].x = 0;  anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -40; anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -45; anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 0 ;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 40;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 45;  anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = -25;
  }  
  else {	
    anchors[i] = new Object();
    anchors[i].x = 0;  anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 0 ;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = -25;
  }  
  return anchors;
}

Jalava._modules['FlowChartPaletteFactory'] = true;
