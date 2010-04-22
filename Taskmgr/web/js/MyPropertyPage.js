/* v0.8.0beta */

function MyPropertyPage(x, y, numRows) {
  MyPropertyPage.parent.apply(this, arguments);
}
Jalava.copyPrototype(MyPropertyPage, PropertyPage);

MyPropertyPage.prototype.propertyChange = function(firer, property, value) {
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
      this.update("Layer", 
	              firer.getLayer(),
	              this.numberInput.createControl('Layer', 4, firer.getLayer(), PropertyPage.prototype.onChangeHandler));	
				  
	}
	else if (className=="DirectedConnection") {
	  this.update("Line Color", 
	              firer.color,
	              this.colorPalette.createControl('Line Color',firer.color, PropertyPage.prototype.onChangeHandler));	  
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


Jalava._modules['MyPropertyPage'] = true;