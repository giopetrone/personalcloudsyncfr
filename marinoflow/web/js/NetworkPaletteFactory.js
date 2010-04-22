/* v0.8.0beta */

function NetworkPaletteFactory() {
  NetworkPaletteFactory.parent.apply(this);
}
Jalava.copyPrototype(NetworkPaletteFactory, PaletteFactory);

NetworkPaletteFactory.W = 120;
NetworkPaletteFactory.H = 50;

NetworkPaletteFactory.prototype.createConnectionObject = function(id) {
	var conn = new DirectedConnection();
	conn.setLineColor("#aaaaff");
	conn.setEndArrowStyle("none");
	conn.setLabel("");
	return conn;
}

NetworkPaletteFactory.prototype.createContent = function(objId, text) {
  
	// this is where we define our custom content
    var ele = DOM.createElement("DIV", "a");
    ele.className = "hello";
	var img = new Image();
	img.src = "./demo/network/" + objId + ".gif"
	img.style.width = "48px";
	img.style.height = "48px";
	img.style.verticalAlign = "middle";
	ele.appendChild(img);
    var span = DOM.createElement("DIV", "mytextarea");
	span.style.fontSize = "10pt";
    span.innerHTML = text;
	span.style.marginLeft = "4px";
    ele.appendChild(span);
	return ele;
}

NetworkPaletteFactory.prototype.createNode = function(objId, left, top, width, height, text) {
  
  // try and determine the drop location
  //var mousePos = diagram.getRelativeMousePos(evt);
  var x = left - Jalava.diagram.container.offsetLeft;
  var y = top - Jalava.diagram.container.offsetTop;
  
  if (x<0 || y<0) return;
  
  var anchors  = this.generateAnchors(objId, width, height);
     
  var ele = this.createContent(objId, text);
  if (!ele) return;

  //var block = new GradientBlock(ele, x+width/2, y+height/2, width, height, anchors, null, "rect", "transparent");
  var block = new Block(ele, x+width/2, y+height/2, width, height, anchors, null);
  Jalava.diagram.addFigure(block);	// 301208: manually add block to diagram
  block.setBorderStyle("none");
  if (Jalava.propertyPage)
    block.addPropertyChangeListener(Jalava.propertyPage);
  Jalava.deselect();
  Jalava.temp_var.selected = block;
  block.select();

  return block;   
}


Jalava._modules['NetworkPaletteFactory'] = true;