/* v0.9.0beta */

/*
 * This is used for testing purposes.
 * This PaletteFactory contains two CLICK tools which allow you to save/load
 * a diagram. 
 */

function SavePaletteFactory(saveUrl, loadUrl, listUrl) {
  SavePaletteFactory.parent.apply(this);
  this.saveUrl = saveUrl;
  this.loadUrl = loadUrl;
  this.listUrl = listUrl;
}
Jalava.copyPrototype(SavePaletteFactory, PaletteFactory);

/*
 * This method is invoked when the user clicks on a palette tool 
 * of type CLICK_TOOL
 */
SavePaletteFactory.prototype.activate = function(objId) {

 if (objId == "save") {
	Palette.prototype.deactivate();
 	this.dialog = new Dialog("Save Diagram", 300, 100, this.saveDialogCreateContent(), Dialog.OK | Dialog.CANCEL, this.saveDialogCallback);
 	this.dialog.show();
 	SavePaletteFactory.that = this;
 }
 else if (objId == "load") {
	Palette.prototype.deactivate();
 	this.dialog = new Dialog("Load Diagram", 300, 150, this.loadDialogCreateContent(), Dialog.OK | Dialog.CANCEL, this.loadDialogCallback);
 	this.dialog.show();
 	SavePaletteFactory.that = this;
 }
 else {
   SavePaletteFactory.parent.prototype.activate.apply(this, arguments);
 }
}

/*
 * Create content for the Load Diagram dialog
 */
SavePaletteFactory.prototype.loadDialogCreateContent = function(){
  var form = DOM.createElement("FORM", "loadform");
  form.innerHTML = "Loading a new diagram will erase your existing diagram!";
  var input = DOM.createElement("SELECT", "loadfile");
  form.appendChild(input);
  input.setAttribute("size", "6");
  input.style.width = "80%";
  input.style.margin = "5px";
  input.style.marginLeft = "5%";
  var option = document.createElement("OPTION");
  option.innerHTML = "Loading...";
  input.appendChild(option);
  
  var span = document.createElement("SPAN");
  span.innerHTML = "Select diagram to load, then click OK."
  form.appendChild(span);
  
  // request data from server
  var params = new Object();
  Ajax.doPost(this.listUrl, params, SavePaletteFactory.listCallback);	
  
  return form;
}

/*
 * Create content for the Save Diagram dialog
 */
SavePaletteFactory.prototype.saveDialogCreateContent = function(){
  var form = DOM.createElement("FORM", "saveform");

  var content = document.createElement("TABLE");
  content.className = "dialogContent";
  form.appendChild(content);  
  
  var cell = content.insertRow(-1).insertCell(-1);
  cell.innerHTML = "Enter name of diagram";	
  var input = DOM.createElement("INPUT", "savefile", "text");
  input.setAttribute("size", "20");
  cell = content.insertRow(-1).insertCell(-1);
  cell.appendChild(input);
  
  return form;
}

/*
 * Callback when dialog box closes
 */
SavePaletteFactory.prototype.loadDialogCallback = function(what){
  if (what==Dialog.CANCEL) {
  	SavePaletteFactory.that.dialog.close();
	delete SavePaletteFactory.that;
  }
  else if (what==Dialog.OK) {
  	var idx = document.forms['loadform'].elements['loadfile'].selectedIndex;
    if (idx<0) return;
  	var loadfile = document.forms['loadform'].elements['loadfile'][idx].value;
   	SavePaletteFactory.that.dialog.close();
	SavePaletteFactory.that.dialog.dispose();

    var params = new Object();
	params["name"] = loadfile;
	Ajax.doPost(SavePaletteFactory.that.loadUrl, params, SavePaletteFactory.loadCallback);	

	delete SavePaletteFactory.that;
  }
}

/*
 * Callback when dialog box closes
 */
SavePaletteFactory.prototype.saveDialogCallback = function(what){
  if (what==Dialog.CANCEL) {
  	SavePaletteFactory.that.dialog.close();
	delete SavePaletteFactory.that;
  }
  else if (what==Dialog.OK) {
  	var savefile = document.forms['saveform'].elements['savefile'].value;
   	SavePaletteFactory.that.dialog.close();
	SavePaletteFactory.that.dialog.dispose();

  	var str = Jalava.diagram.persist();
	var params = new Object();
	params["data"] = str;
	params["name"] = savefile;
	Ajax.doPost(SavePaletteFactory.that.saveUrl, params, SavePaletteFactory.saveCallback);	

	delete SavePaletteFactory.that;
  }
}

/*
 * AJAX callback - listUrl
 */
SavePaletteFactory.listCallback = function() {
  if (Ajax.xmlHttp.readyState == 4) {
	var list = JSON.parse(Ajax.xmlHttp.responseText);
    for (var i=0; i<list.length; i++) {
      document.forms['loadform'].elements['loadfile'].options[i] = new Option(list[i], list[i]);
	}
  }
}

/*
 * AJAX callback - saveUrl
 */
SavePaletteFactory.saveCallback = function() {
  if (Ajax.xmlHttp.readyState == 4) {
	if (Ajax.xmlHttp.responseText == "OK") 
      MessageDialog.showMessage("Save Complete", "Your diagram has been saved.");
    else {
      MessageDialog.showMessage("Save Failed", Ajax.xmlHttp.responseText);
	  //MessageDialog.showMessage("Save Failed", "Some problems encountered while saving your diagram.");
    }
  }
}

/*
 * AJAX callback - loadUrl
 */
SavePaletteFactory.loadCallback = function() {
  if (Ajax.xmlHttp.readyState == 4) {
	if (Ajax.xmlHttp.responseText != "") {
		alert(Ajax.xmlHttp.responseText);
	  Jalava.diagram.load(Ajax.xmlHttp.responseText);
    }
  }
}
	
Jalava._modules['SavePaletteFactory'] = true;