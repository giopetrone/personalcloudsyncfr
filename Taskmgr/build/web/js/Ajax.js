/* v0.9.0beta */

/*
 * AJAX Utilities
 */
function Ajax(){
}

// global variable to store the current AJAX HttpRequest 
Ajax.xmlHttp = null;

// returns a XMLHttpRequest object
Ajax.getHttpRequestObj = function(){
  var httpRequest = null;
  try {
    // Firefox, Opera 8.0+, Safari
    httpRequest = new XMLHttpRequest();
  } 
  catch (e) {
    // IE
    try {
      httpRequest = new ActiveXObject("Msxml2.XMLHTTP");
    } 
    catch (e) {
      try {
        httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
      } 
      catch (e) {
        alert("Your browser does not support AJAX!");
        return false;
      }
    }
  }
  return httpRequest;
}


Ajax.doGet = function(url, func){
  try {
    if (!Ajax.xmlHttp) 
      return;
    if (Ajax.xmlHttp.readyState > 0 && Ajax.xmlHttp.readyState < 4) {
      Ajax.xmlHttp.abort();
      Ajax.xmlHttp = getHttpRequestObj();
    }
    
    Ajax.xmlHttp.open("GET", url);
    Ajax.xmlHttp.onreadystatechange = func;
    Ajax.xmlHttp.send(null);
    
  } 
  catch (e) {
    alert(e.message);
  }
}

Ajax.doPost = function(url, params, func){
  try {
  	if (!Ajax.xmlHttp) {
      Ajax.xmlHttp = Ajax.getHttpRequestObj();
	}
    else if (Ajax.xmlHttp.readyState > 0 && Ajax.xmlHttp.readyState < 4) {
      Ajax.xmlHttp.abort();
      Ajax.xmlHttp = Ajax.getHttpRequestObj();
    }
    var str = Ajax.stringify(params);

	//alert("sending request..." + url);
    Ajax.xmlHttp.open("POST", url);
	Ajax.xmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    Ajax.xmlHttp.setRequestHeader("Content-Length", str.length);
    Ajax.xmlHttp.setRequestHeader("Connection", "close");
	
    Ajax.xmlHttp.onreadystatechange = func;
    Ajax.xmlHttp.send(str);
    
  } 
  catch (e) {
    alert("error- " + e.message);
  }
}

Ajax.stringify = function(array) {
  var str = "";
  for (var key in array) {
    str += key + "=" + encodeURIComponent(array[key]) + "&";	
  }
  return str=="" ? "" : str.substring(0, str.length-1);
}

Jalava._modules['Ajax'] = true;