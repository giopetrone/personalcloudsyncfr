/* v0.8.0beta */

function FlowChartPropertyPage(x, y, numRows) {
  FlowChartPropertyPage.parent.apply(this, arguments);
}
Jalava.copyPrototype(FlowChartPropertyPage, PropertyPage);

FlowChartPropertyPage.prototype.propertyChange = function(firer, property, value) {
   // user select a Figure	
  if (property=="select") {
  	this.table.setAttribute("id", firer.id);
  	
	var className = Jalava.getClassName(firer);

	if (className=="UrlGradientBlock") {
		
	  this.update("Backgrd Color", 
		             firer.getBackgroundColor(), 
					 this.colorPalette.createControl('Backgrd Color',firer.getBackgroundColor(), PropertyPage.prototype.onChangeHandler));	  	
	 
          this.update("Layer", 
	              firer.getLayer(),
	              this.numberInput.createControl('Layer', 4, firer.getLayer(), PropertyPage.prototype.onChangeHandler));	
				  
	}
	else if (className=="Connection") {
	  this.update("Line Color", 
	              firer.color,
	              this.colorPalette.createControl('Line Color',firer.color, FlowChartPropertyPage.prototype.onChangeHandler));	  
      this.update("Line Width", 
	              firer.halfBrushWidth*2,
	              this.numberInput.createControl('Line Width', 4, firer.halfBrushWidth*2, FlowChartPropertyPage.prototype.onChangeHandler, 2));	
	}
	else if (className=="DirectedConnection") {
	  this.update("Line Color", 
	              firer.color,
	              this.colorPalette.createControl('Line Color',firer.color, FlowChartPropertyPage.prototype.onChangeHandler));	  
      this.update("Line Width", 
	              firer.halfBrushWidth*2,
	              this.numberInput.createControl('Line Width', 4, firer.halfBrushWidth*2, FlowChartPropertyPage.prototype.onChangeHandler, 2));	

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

Jalava._modules['FlowChartPropertyPage'] = true;
