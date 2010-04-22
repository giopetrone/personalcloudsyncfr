/* v0.8.0beta */

/*
 * DOM Utilities
 */
function DOM() {} 
	
DOM.hookEvent = function (obj, eventName, func){
  if (obj.attachEvent) {
    obj.attachEvent("on" + eventName, func);
  }
  else if (obj.addEventListener) {
    obj.addEventListener(eventName, func, true);
  }
}

DOM.unhookEvent = function(obj, eventName, func){
  if (obj.detachEvent) {
    obj.detachEvent("on" + eventName, func);
  }
  else if (obj.removeEventListener) {
    obj.removeEventListener(eventName, func, true);
  }
}


DOM.cancelBubble = function(event) {
  if (!event) event = window.event;
  event.cancelBubble = true;
  if (event.stopPropagation) event.stopPropagation();
}


DOM.bubbleToTarget = function(obj, name)
{
  if (!name) return obj;
  try {
    if (obj.getAttribute('name')==name) return obj;
    if (obj.tagName=="HTML") return null;
    while (obj && obj.tagName!="BODY") {
	  obj = obj.parentNode;
      if (obj.getAttribute('name')==name) return obj;
    }
  } catch (e) { alert(e.message); /*TODO*/ }
  return null;	
}


DOM.getEventTarget = function(evt, tgtName){
  if (!evt) evt = window.event;
  if (!evt) return null;
  var obj = evt.srcElement;
  if (!obj) obj = evt.target;
  if (!obj) return null;
  
  if (!tgtName) return obj;
  try {
    if (obj.getAttribute("name") == tgtName) return obj;
    
    if (obj.tagName == "HTML") return null;
    while (obj && obj.tagName != "BODY") {
      obj = obj.parentNode;
      if (obj && obj.getAttribute("name") == tgtName) return obj;
    }
  } 
  catch (e) { /*TODO*/ }
  return null;
}



DOM.createElement = function(tagName, name, type){
  var newEle;
  try {
    if (type) newEle = document.createElement("<" + tagName + " name='" + name + "' type='" + type + "'>");
    else newEle = document.createElement("<" + tagName + " name='" + name + "'>");
  } 
  catch (e) {
    newEle = document.createElement("<" + tagName + ">");
    newEle.setAttribute("name", name);
    if (type) newEle.setAttribute("type", type);
  }
  return newEle;
}



DOM.findNodeByTagName = function(chunk, tagname, recurse){
  return DOM.findNodeByIdOrName(chunk, tagname, null, recurse);
}

DOM.findNodeByName = function(chunk, tagname, idOrName, recurse){
  return DOM.findNodeByIdOrName(chunk, tagname, idOrName, recurse, "name");
}

DOM.findNodeById = function(chunk, tagname, idOrName, recurse){
  return DOM.findNodeByIdOrName(chunk, tagname, idOrName, recurse, "id");
}

DOM.findNodeByIdOrName = function(chunk, tagname, idOrName, recurse, type){
  if (chunk.childNodes == null) 
    return null;
  
  for (var i = 0; i < chunk.childNodes.length; i++) {
    if ((tagname == null || chunk.childNodes[i].tagName == tagname) &&
    (idOrName == null || chunk.childNodes[i].getAttribute(type) == idOrName)) {
      return chunk.childNodes[i];
    }
    if (recurse) {
      var rec = DOM.findNodeByIdOrName(chunk.childNodes[i], tagname, idOrName, recurse, type);
      if (rec) return rec;
    }
  }
  return null;
}

Jalava._modules['DOM'] = true;