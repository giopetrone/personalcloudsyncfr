/* v0.8.0beta */

function MyPaletteFactory() {
  MyPaletteFactory.parent.apply(this);
}
Jalava.copyPrototype(MyPaletteFactory, PaletteFactory);

MyPaletteFactory.prototype.createContent = function(objId, real) {
  if (objId=="Zebra" || objId=="Elephant") {    
	// this is where we define our custom content
    var ele = DOM.createElement("DIV", "a");
    ele.className = "hello";
	var img = new Image();
	img.src = "./demo/african/" + objId + "32.gif"
	img.style.width = "32px";
	img.style.height = "32px";
	img.style.verticalAlign = "middle";
	ele.appendChild(img);
    var span = DOM.createElement("SPAN", "mytextarea");
    span.innerHTML = objId;
	span.style.marginLeft = "4px";
	if (real) {
      span.className = "editableLine";
      span.ondblclick = function(event) { Editable.startedit(event); }
	}
    ele.appendChild(span);
	return ele;
  }
  else return;
}


Jalava._modules['MyPaletteFactory'] = true;