/* v0.9.0beta */

/*
 * Persistance support
 * 
 * Persistance is achieved by requesting each Figure to implement a save() function,
 * which should return a JSON string representing all the persistable data in the
 * Figure. 
 * 
 * In addition, each Figure must implement a load() function, which does the opposite.
 * Given a JSON string, it should repopulate its data with the JSON values.
 * 
 * Access the Diagram's persistance functionality using the persist() and load() methods.
 */

/*
 * A container for Figures.
 */
function Diagram(x, y, width, height) {
    this.figures = new Object();
    this.sequence = 0;
    this.container = DOM.createElement("DIV", "container");
    this.container.style.position = "absolute";
    this.container.style.top = y;
    this.container.style.left = x;
    this.container.style.width = width;
    this.container.style.height = height;
    this.container.style.border = "3px solid #DDDDDD";
    document.body.appendChild(this.container);
    this.container.id = "container";
    initaliseAnchors(this.container);
    this.baseZIndex = 90;
}

Diagram.NUM_ANCHORS = 10;
Diagram.LEFT = 1;
Diagram.RIGHT = 2;
Diagram.TOP = 4;
Diagram.BOTTOM = 8;
Diagram.DEFAULT_ZINDEX = 90;

Diagram.prototype.hide = function() {
    this.container.style.display = "none";
}

Diagram.prototype.show = function() {
    this.container.style.display = "block";
}

Diagram.prototype.getData = function() {
    return this.container.innerHTML;
}

/*
 * Converts the objects in this diagram into a JSON string
 * for subsequent storage. The same JSON string can be read
 * and loaded back to the diagram using load().
 */
Diagram.prototype.persist = function() {
    var persisted = new Object();
    persisted.connections = new Array();
    persisted.blocks = new Array();
    for (fig in this.figures) {
        if (this.figures[fig].clazz=="Block")
            persisted.blocks.push(this.figures[fig].save());
        else if (this.figures[fig].clazz=="Connection")
            persisted.connections.push(this.figures[fig].save());
    }
    return JSON.stringify(persisted);
}

/*
 * Parses the given JSON string and instantiates the corresponding
 * objects and adds them to the diagram.
 */
Diagram.prototype.load = function(jsonString) {

    var persisted = JSON.parse(jsonString);

    for (var i=0; i<persisted.blocks.length; i++) {
        var figure = Palette.factory.generateFigure(persisted.blocks[i]);
        var seq = parseInt(figure.id.substring(6));
        if (!isNaN(seq)) this.sequence = Math.max(seq, this.sequence);
    }
    for (var j=0; j<persisted.connections.length; j++) {
        var figure = Palette.factory.generateFigure(persisted.connections[j]);
        var seq = parseInt(figure.id.substring(6));
        if (!isNaN(seq)) this.sequence = Math.max(seq, this.sequence);
    }

}

/*
 * Generate a unique ID for figures
 */
Diagram.prototype.generateId = function() {
    return "figure" + this.sequence++;
}

/*
 * Add a Block or Connection to this diagram.
 */
Diagram.prototype.addFigure = function(figure, position) {
    //var id = "figure" + this.sequence++;
    this.figures[(figure.id)] = figure;
    // DIOFFA E CHI SONO?
    if (position == "absolute") {
        figure.element.style.position = "absolute";
        figure.element.style.border = "2px solid red"; // MARINO
    }
    // da load non era settato!!!!!
    //  confirm (figure.id);
    this.container.appendChild(figure.element);
}

/*
 * Get a Block or Figure from this diagram
 */
Diagram.prototype.getFigure = function(figureId) {
    if (figureId) return this.figures[figureId];
    return null;
}

/*
 * Remove the figure from the diagram, as well as delete it from 
 * memory.
 */
Diagram.prototype.removeFigure = function(figureId) {
    if (this.figures[figureId]) {
        this.figures[figureId].dispose();
        this.container.removeChild(this.figures[figureId].element);
        delete this.figures[figureId];
    }
}

/*
 * Find the position of the given object, relative to this diagram.
 * Assumes that the object is a child of this diagram.
 */
Diagram.prototype.findPos = function(obj){
    var curleft = curtop = 0;
    if (obj.offsetParent) {
        curleft = obj.offsetLeft
        curtop = obj.offsetTop
        while (obj = obj.offsetParent) {
            if (obj==this.container) break;
            curleft += parseInt(obj.offsetLeft);
            curtop += parseInt(obj.offsetTop);
        }
    }
    return [curleft, curtop];
}

/*
 * Gets the position of the mouse cursor relative to this diagram
 */
Diagram.prototype.getRelativeMousePos = function(evt) {
    var pos = getMousePos(evt);
    return {
        x: pos.x - this.container.offsetLeft,
        y: pos.y - this.container.offsetTop
    };
}

/*
 * Fired when the user first drags an object in the diagram
 */
Diagram.prototype.dragStart = function(evt){
    var test = DOM.getEventTarget(evt);
    if (test && test.tagName=="TEXTAREA") return true;
  
    Jalava.temp_var.grabbed = DOM.getEventTarget(evt, "figure");
  
    if (Jalava.temp_var.grabbed == null) {
        var grabbed = DOM.getEventTarget(evt, "anchor");
        if (grabbed == null) return Jalava.deselect(evt);
        return Jalava.resizeStart(evt, grabbed);
    }
	
    var fig = Jalava.diagram.getFigure(Jalava.temp_var.grabbed.id);
    if (!fig) return false; // no figure!?
 
    if (fig.clazz=="Connection") {
        delete Jalava.temp_var.grabbed;
        return false;
    }
  
    var posi = this.findPos(Jalava.temp_var.grabbed);
    var mousePos = getMousePos(evt);
    Jalava.temp_var.grabbedX = mousePos.x - posi[0];
    Jalava.temp_var.grabbedY = mousePos.y - posi[1];
    Jalava.temp_var.container = this.container;
  
    Jalava.mousemoveHandler = this.drag;
    Jalava.mouseupHandler = this.dragEnd;
	
    return false;
}

/*
 * Note that this drag function has no access to instance variables ('this')
 * as it is assigned to an external reference
 */
Diagram.prototype.drag = function(evt){
    try {
        if (isIE() && evt.button == 0) return Jalava.mouseupHandler(evt);
    
        var mousePos = getMousePos(evt);
    
        var top = (mousePos.y - Jalava.temp_var.grabbedY);
        var left = (mousePos.x - Jalava.temp_var.grabbedX);

        // prevent dragging out of the bounds of this diagram
        var blockW = parseInt(Jalava.temp_var.grabbed.offsetWidth);
        var blockH = parseInt(Jalava.temp_var.grabbed.offsetHeight);
        if (top < 0) {		// top boundary
            Jalava.temp_var.grabbed.style.top = 0;
            top = 0;
        }					// bottom boundary
        else if (top > Jalava.temp_var.container.offsetHeight - blockH) {
            top = Jalava.temp_var.container.offsetHeight - blockH;
            Jalava.temp_var.grabbed.style.top = top;
        }
        if (left < 0) {		// left boundary
            Jalava.temp_var.grabbed.style.left = 0;
            left = 0;
        }					// right boundary
        else if (left > Jalava.temp_var.container.offsetWidth - blockW) {
            left = Jalava.temp_var.container.offsetWidth - blockW;
            Jalava.temp_var.grabbed.style.left = left;
        }
    
        var x = left + parseInt(Jalava.temp_var.grabbed.offsetWidth) / 2;
        var y = top + parseInt(Jalava.temp_var.grabbed.offsetHeight) / 2;
    
        Jalava.diagram.getFigure(Jalava.temp_var.grabbed.id).setPosition(x, y);
    }
    catch (e) {
        alert(e.message); /* TODO */
    }
    return false;
}

/* 
 * Fired when the user releases the mouse button after a drag
 */
Diagram.prototype.dragEnd = function(evt){
    Jalava.mouseupHandler = Jalava.donothing;
    Jalava.mousemoveHandler = Jalava.returnfalse;
  
    Jalava.temp_var.grabbed = null;
    Jalava.temp_var.grabbedX = null;
    Jalava.temp_var.grabbedY = null;
}

/*
 * Returns the bounded value for a given side (TOP, BOTTOM, LEFT, RIGHT)
 * If the given value falls outside the boundary, it will be clipped
 * to the boundary value and returned.
 */
Diagram.prototype.bound = function(targetVal, side) {
    var val = parseInt(targetVal);
    if (side&Diagram.TOP) {
        val = Math.max(val, 0);
    }
    if (side&Diagram.BOTTOM) {
        val = Math.min(val, this.container.offsetHeight);
    }
    if (side&Diagram.LEFT) {
        val = Math.max(val, 0);
    }
    if (side&Diagram.RIGHT) {
        val = Math.min(val, this.container.offsetWidth);
    }
    return val;
}


Diagram.prototype.click = function(targetVal, side) {
    }

/*
 * Utility function to create the DOM elements used to represent
 * anchors.
 */
function initaliseAnchors(container) {
    for (var i=0; i<Diagram.NUM_ANCHORS; i++) {
        var anchor = DOM.createElement("DIV", "anchor");
        container.appendChild(anchor);
        anchor.id = "anchor" + i;
        anchor.className = "anchor";
        var img = DOM.createElement("img", "image");
        anchor.appendChild(img);
        img.src = "http://marinoflow.appspot.com/img/nothing.gif";
    }
    for (var i=0; i<Diagram.NUM_ANCHORS; i++) {
        var hover = DOM.createElement("DIV", "hover");
        container.appendChild(hover);
        hover.id = "hover" + i;
        hover.className = "hover";
        var img = DOM.createElement("img", "image");
        hover.appendChild(img);
        img.src = "http://marinoflow.appspot.com/img/nothing.gif";
    }
}

Jalava._modules['Diagram'] = true;
