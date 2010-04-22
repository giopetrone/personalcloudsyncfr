/* v0.9.0beta */

function MessageDialog() {
	
}

MessageDialog.showMessage = function(title, message) {

  var content = document.createElement("DIV");
  content.className = "dialogContent";
  content.innerHTML = message;	
	
  var d = new Dialog(title, 250, 50, content, Dialog.CLOSE, null);
  
  d.show();

}



Jalava._modules['MessageDialog'] = true;