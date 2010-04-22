/* v0.8.0beta */
/*
 * Is the class responsible for implementation property pages for the
 * Figures on the diagram.
 */
function PropertyPage(x, y, numRows) {

  var row, cell;  
  this.element = DOM.createElement("DIV", "propertypageDiv");
  this.element.className = "propertypage";
  this.table = DOM.createElement("TABLE", "propertypage");
  this.table.className ="propertypage";
  this.element.appendChild(this.table);
  
  // disable onclick to prevent selected figures from losing focus  
  this.element.onmousedown = function(event) {
  	if (!event) event = window.event;
  	var obj = DOM.getEventTarget(event, "propertypageDiv");
	if (!obj) return;
	Dnd.dragStart(event, obj);
	DOM.cancelBubble(event);
  }

  row = this.table.insertRow(-1);
  cell = document.createElement("TH");
  cell.setAttribute("colSpan", "2");
  row.appendChild(cell);
  cell.innerHTML = "Properties";
  
  for (var i=0; i<numRows; i++) {
  	row = this.table.insertRow(-1);
	cell = row.insertCell(-1);
	cell.className = "key";
	cell.innerHTML = "&nbsp;";
	cell = row.insertCell(-1);
	cell.className = "value";
	cell.innerHTML = "&nbsp;";
  }
  
  this.element.style.left = x;
  this.element.style.top = y;
  
  document.body.appendChild(this.element);

  var colors = new Array();
  colors[0] = new Array("#000000","#808080","#800000","#808000");
  colors[1] = new Array("#008000","#008080","#000080","#800080");
  colors[2] = new Array("#c0c0c0","#ff0000","#ffff00","#00ff00");
  colors[3] = new Array("#00ffff","#0000ff","#ff00ff","#ffffff");

  this.colorPalette = new ColorPalette(colors);
  this.numberInput = new NumberInput();  
}

PropertyPage.prototype.hide = function() {
  this.element.style.display = "none";
}

PropertyPage.prototype.show = function() {
  this.element.style.display = "block";
}

/*
 * Fired when user interacts with Figures on the diagram. Typical properties
 * are 'select', 'deselect' and 'delete'. You should always override this
 * method to provide properties specific to your Figure classes.
 */
PropertyPage.prototype.propertyChange = function(firer, property, value) {
   // user select a Figure	
  if (property=="select") {
  	this.table.setAttribute("id", firer.id);
  	
	var className = Jalava.getClassName(firer);

	if (className=="GradientBlock" || className=="Block") {
		
	  this.update("Backgrd Color", 
		             firer.getBackgroundColor(), 
					 this.colorPalette.createControl('Backgrd Color',firer.getBackgroundColor(), PropertyPage.prototype.onChangeHandler));	  	
	  this.update("Border Color", 
		             firer.getBorderColor(), 
					 this.colorPalette.createControl('Border Color',firer.getBorderColor(), PropertyPage.prototype.onChangeHandler));	  	
	 
	  var optionArray = new Array("none","dashed","solid")
	  var options = this.constructSelectBox("Border Style", optionArray, optionArray, firer.element.style.borderTopStyle);
      this.update("Border Style", firer.element.style.borderStyle, options);
      this.update("Border Width", 
	              firer.getBorder(), 
	              this.numberInput.createControl('Border Width', 4, firer.getBorder(), PropertyPage.prototype.onChangeHandler));
      this.update("Layer", 
	              firer.getLayer(),
	              this.numberInput.createControl('Layer', 4, firer.getLayer(), PropertyPage.prototype.onChangeHandler));	
				  
	}
	else if (className=="Connection") {
	  this.update("Line Color", 
	              firer.color,
	              this.colorPalette.createControl('Line Color',firer.color, PropertyPage.prototype.onChangeHandler));	  
      this.update("Line Width", 
	              firer.halfBrushWidth*2,
	              this.numberInput.createControl('Line Width', 4, firer.halfBrushWidth*2, PropertyPage.prototype.onChangeHandler, 2));	
	}
	else if (className=="DirectedConnection") {
	  this.update("Line Color", 
	              firer.color,
	              this.colorPalette.createControl('Line Color',firer.color, PropertyPage.prototype.onChangeHandler));	  
      this.update("Line Width", 
	              firer.halfBrushWidth*2,
	              this.numberInput.createControl('Line Width', 4, firer.halfBrushWidth*2, PropertyPage.prototype.onChangeHandler, 2));	

	  var optionArray = new Array("solid","dashed","dotted");
	  var options = this.constructSelectBox("Line Style", optionArray, optionArray, firer.getLineStyle());
      this.update("Line Style", firer.getLineStyle(), options);

	  optionArray = new Array("arrow","line_arrow","none");
	  options = this.constructSelectBox("Start Arrow Style", optionArray, optionArray, firer.getStartArrowStyle());
      this.update("Start Arrow Style", firer.getStartArrowStyle(), options);

	  optionArray = new Array("small","medium","big");
	  var valueArray = new Array("10","20","30");
	  options = this.constructSelectBox("Start Arrow Size", optionArray, valueArray, firer.getStartArrowSize());
      this.update("Start Arrow Size", firer.getStartArrowSize(), options);

	  optionArray = new Array("arrow","line_arrow","none");
	  options = this.constructSelectBox("End Arrow Style", optionArray, optionArray, firer.getEndArrowStyle());
      this.update("End Arrow Style", firer.getEndArrowStyle(), options);

	  optionArray = new Array("small","medium","big");
	  var valueArray = new Array("10","20","30");
	  options = this.constructSelectBox("End Arrow Size", optionArray, valueArray, firer.getEndArrowSize());
      this.update("End Arrow Size", firer.getEndArrowSize(), options);

	}
  }
  else if (property=="deselect" || property=="delete") {
  	this.table.setAttribute("id", "nil");
  	this.clear();
  } 
  else  {
  	this.updateStrictly(property, value);
  }
}


/*
 * Helper class to clear the values in the Properties table.
 */
PropertyPage.prototype.clear = function() {
  var rows = this.table.rows;
  var n = rows.length;
  // first pass
  for (var i=1; i<n; i++) {
  	rows[i].cells[0].innerHTML = "&nbsp;";
	rows[i].cells[1].innerHTML = "&nbsp;";
  }	
}

/*
 * Updates a property if and only if it exists in the Properties sheet.
 */
PropertyPage.prototype.updateStrictly = function(property, value) {
  var rows = this.table.rows;
  var n = rows.length;
  if (value!="0" && !value) value = "none";
  // first pass
  for (var i=1; i<n; i++) {
  	if (rows[i].cells[0].innerHTML==property) {
	  var input = rows[i].cells[1].firstChild;
	  if (input.update) input.update(input, value);
	  return;
	}
  }	
}

/*
 * Updates a property. If it does not yet exist, create an entry in the 
 * Properties Sheet. An entry will be created and the input control (text box,
 * select box, color palette, etc) is provided by the options argument.
 */
PropertyPage.prototype.update = function(property, value, options) {
  var rows = this.table.rows;
  var n = rows.length;
  if (value!="0" && !value) value = "none";
  
  // first pass
  for (var i=1; i<n; i++) {
  	if (rows[i].cells[0].innerHTML==property) {
	  var input = rows[i].cells[1].firstChild;
	  if (input.update) input.update(input, value);
	  return;
	}
  }	

  // construct the input field, if not provided
  if (!options) {
    var options = DOM.createElement("INPUT", "textbox", "text");
    options.className = "value";
    options.onchange = PropertyPage.prototype.onChangeHandler;
	options.value = value;
	options.id = property;
  }


  // second pass
  for (var i=1; i<n; i++) {
  	if (rows[i].cells[0].innerHTML=="&nbsp;") {
	  rows[i].cells[0].innerHTML = property;	

	  // add the provided options element
	  while (rows[i].cells[1].hasChildNodes()) 
	    { rows[i].cells[1].removeChild(rows[i].cells[1].firstChild); }
	  rows[i].cells[1].appendChild(options);	
	 
	  return;
	}
  }	
}

/*
 * Fired when user makes a change to value in the property page
 */
PropertyPage.prototype.onChangeHandler = function(obj, property, value) {
  var table = DOM.bubbleToTarget(obj, "propertypage");
  var fig = Jalava.diagram.getFigure(table.id);
  if (fig) fig.setProperty(property, value);
}

/*
 * Helper class to instantiate a select box
 */
PropertyPage.prototype.constructSelectBox = function(name, names, values, defaultVal) {
  var input = DOM.createElement("SELECT", name);
  input.id = name;
  input.className = "value";
  input.style.zIndex =1;
  input.onchange = function(event) {
    var obj = DOM.getEventTarget(event);
    PropertyPage.prototype.onChangeHandler(obj, obj.id, obj.value);
  }
  for (var i=0; i<names.length; i++) {
    input.options[i] = new Option(names[i], values[i]);
	if (values[i]==defaultVal) input.selectedIndex = i;
  }
  
  // callback function to update this control
  input.update = function(obj, value) {
  	if (!obj.options) return;
    for (var i=0; i<obj.options.length; i++) {
	  if (obj.options[i].value==value) { obj.selectedIndex = i; return; }
    } 	
  }
 
  return input;
}

Jalava._modules['PropertyPage'] = true;