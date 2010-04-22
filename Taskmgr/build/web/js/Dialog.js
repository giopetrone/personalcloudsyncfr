/* v0.9.0beta */

/*
 * Generic Dialog box 
 */
function Dialog(title, width, height, content, buttons, func) {
  this.title = title;
  this.height = height + 50;  // for the buttons
  this.width = width;
  this.content = content;
  this.buttons = buttons;
  this.callback = func;
}

Dialog.OK = 1;
Dialog.CANCEL = 2;
Dialog.APPLY = 4;
Dialog.CLOSE = 8;
Dialog.YES = 16;
Dialog.NO = 32;

Dialog.callback;

Dialog.prototype.show = function() {
  this.cover();
  this.dialog = document.createElement("DIV");
  this.dialog.style.width = this.width;
  this.dialog.style.height = this.height;
  this.dialog.style.position = "absolute";
  this.dialog.style.display = "none";
  this.dialog.style.border = "1px solid #888888";
  
  // centre the frame
  var h = document.body.clientHeight;
  var w = document.body.clientWidth;
  var clientH = parseInt(this.dialog.style.height);
  var clientW = parseInt(this.dialog.style.width);
  var top = (h - clientH) / 2;
  var left = (w - clientW) / 2;
  this.dialog.style.top = top > 0 ? top : 0;
  this.dialog.style.left = left > 0 ? left : 0;

  this.dialog.style.display = 'block';
  this.dialog.style.zIndex = 400;

  // create the title bar
  var table = document.createElement("TABLE");
  this.dialog.appendChild(table);
  table.className = "dialog";
  table.style.height = this.height;

  var header = table.createTHead().insertRow(-1).insertCell(-1);  
  header.innerHTML = this.title;
  
  var tbody = document.createElement("TBODY");
  table.appendChild(tbody);
  var body = tbody.insertRow(-1).insertCell(-1);
  
  this.createContent(body);
  
  var tfoot = table.createTFoot().insertRow(-1).insertCell(-1);

  this.createButtons(tfoot);
 
  this.mousedownHandler = Jalava.mousedownHandler;
  this.disableHighlight = Jalava.disableHighlight;
  Jalava.mousedownHandler = Jalava.returnfalse;
  Jalava.disableHighlight = Jalava.returntrue;

  Dialog.that = this;  
  
  document.body.appendChild(this.dialog); 
}

Dialog.prototype.createContent = function(parent) {
  parent.appendChild(this.content);
}

Dialog.prototype.createButtons = function(parent) {
  if (this.buttons & Dialog.OK) {
  	var btn = DOM.createElement("BUTTON", "ok");
	btn.innerHTML = "OK";
	btn.onclick = this.ok;
	parent.appendChild(btn);
  }	  
  if (this.buttons & Dialog.YES) {
  	var btn = DOM.createElement("BUTTON", "yes");
	btn.innerHTML = "Yes";
	btn.onclick = this.yes;
	parent.appendChild(btn);
  }	
  if (this.buttons & Dialog.NO) {
  	var btn = DOM.createElement("BUTTON", "no");
	btn.innerHTML = "No";
	btn.onclick = this.no;
	parent.appendChild(btn);
  }	
  if (this.buttons & Dialog.CANCEL) {
  	var btn = DOM.createElement("BUTTON", "cancel");
	btn.innerHTML = "Cancel";
	btn.onclick = this.cancel;
	parent.appendChild(btn);
  }	
  if (this.buttons & Dialog.APPLY) {
  	var btn = DOM.createElement("BUTTON", "apply");
	btn.innerHTML = "Apply";
	btn.onclick = this.cancel;
	parent.appendChild(btn);
  }	
  if (this.buttons & Dialog.CLOSE) {
  	var btn = DOM.createElement("BUTTON", "close");
	btn.innerHTML = "Close";
	btn.onclick = this.closeAndDispose;
	parent.appendChild(btn);
  }	
}

Dialog.prototype.ok = function() {
  Dialog.that.callback(Dialog.OK);
}

Dialog.prototype.yes = function() {
  Dialog.that.callback(Dialog.YES);
}

Dialog.prototype.no = function() {
  Dialog.that.callback(Dialog.NO);
}

Dialog.prototype.cancel = function() {
  Dialog.that.callback(Dialog.CANCEL);
}

Dialog.prototype.apply = function() {
  Dialog.that.callback(Dialog.APPLY);
}

Dialog.prototype.closeAndDispose = function() {
  Dialog.that.close();
  Dialog.that.dispose();
  delete Dialog.that;
}

Dialog.prototype.close = function() {
  document.body.removeChild(this.dialog);
  this.uncover();  
  Jalava.mousedownHandler = this.mousedownHandler;
  Jalava.disableHighlight = this.disableHighlight;
}

Dialog.prototype.dispose = function() {
  delete this.dialog;
  delete this.content;
  delete this.callback;
}

Dialog.prototype.cover = function() {
  // add a translucent cover to prevent user access
  this.opaque = document.createElement("DIV");
  this.opaque.style.width = "100%";
  this.opaque.style.height = "100%";
  this.opaque.style.position = "absolute";
  this.opaque.style.top = "0px";
  this.opaque.style.left = "0px";
  this.opaque.style.zIndex = 399;
  this.opaque.className = "blanket"
  document.body.appendChild(this.opaque);
}

Dialog.prototype.uncover = function() {
  document.body.removeChild(this.opaque);
  delete this.opaque;
}

Jalava._modules['Dialog'] = true;