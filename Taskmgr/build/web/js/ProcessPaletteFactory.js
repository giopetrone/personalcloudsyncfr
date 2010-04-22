/* v0.8.0beta */

function ProcessPaletteFactory() {
  ProcessPaletteFactory.parent.apply(this);
}
Jalava.copyPrototype(ProcessPaletteFactory, PaletteFactory);

ProcessPaletteFactory.W = 120;
ProcessPaletteFactory.H = 30;

ProcessPaletteFactory.prototype.createConnectionObject = function(id) {
	var conn = new DirectedConnection();
	conn.setLineColor("#c0c0c0");
	return conn;
}

ProcessPaletteFactory.prototype.generateTemplate = function(objId) {
  
  // check the template cache
  if (this.templates[objId]) return this.templates[objId];
  
  // default behaviour
  var ele = this.createContent(objId, false);
  if (!ele) return;
  
  this.templates[objId] = ProcessNode.prototype.generateTemplate(ele, null, null, ProcessPaletteFactory.W, ProcessPaletteFactory.H, null, "./img/rect/rect_template.gif"); 	
  return this.templates[objId];

}

ProcessPaletteFactory.prototype.createContent = function(objId, real) {
  var title;
  if (objId=="sequence") title = "Sequence";
  else if (objId=="receive") title = "Receive";
  else if (objId=="reply") title = "Reply";
  else if (objId=="invoke") title = "Invoke";
  else return;  
  
	// this is where we define our custom content
    var ele = DOM.createElement("DIV", "a");
    ele.className = "hello";
	var img = new Image();
	img.src = "./demo/process/" + objId + ".gif"
	img.style.width = "20px";
	img.style.height = "20px";
	img.style.verticalAlign = "middle";
	ele.appendChild(img);
    var span = DOM.createElement("SPAN", "mytextarea");
	span.style.fontSize = "10pt";
    span.innerHTML = title;
	span.style.marginLeft = "4px";
	if (real) {
      span.className = "editableLine";
      span.ondblclick = function(event) { Editable.startedit(event); }
	}
    ele.appendChild(span);
	return ele;
}

ProcessPaletteFactory.prototype.dropTarget = function(evt, objId, left, top) {
  
  // try and determine the drop location
  //var mousePos = diagram.getRelativeMousePos(evt);
  var x = left - Jalava.diagram.container.offsetLeft;
  var y = top - Jalava.diagram.container.offsetTop;
  
  if (x<0 || y<0) return;
  
  var anchors  = this.generateAnchors(objId, ProcessPaletteFactory.W, ProcessPaletteFactory.H);
     
  var ele = this.createContent(objId, true);
  if (!ele) return;
  try {
  var block = new ProcessNode(ele, x+ProcessPaletteFactory.W/2, y+ProcessPaletteFactory.H/2, ProcessPaletteFactory.W, ProcessPaletteFactory.H, anchors, null, "rect", "transparent");
  block.setBorderStyle("solid");
  block.setBorderColor("#c0c0c0");
  block.setBorder(1);
  block.setBackgroundColor("#c0c0c0");
  if (Jalava.propertyPage)
    block.addPropertyChangeListener(Jalava.propertyPage);
  Jalava.deselect();
  Jalava.temp_var.selected = block;
  block.select();
  } catch (e) { alert(e.message); }
  return block;   
}

Jalava._modules['ProcessPaletteFactory'] = true;