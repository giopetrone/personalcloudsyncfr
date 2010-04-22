/* v0.8.0beta */

function findPos(obj) {
  var curleft = curtop = 0;
  if (obj.offsetParent) {
    curleft = obj.offsetLeft
    curtop = obj.offsetTop
    while (obj = obj.offsetParent) {
      curleft += parseInt(obj.offsetLeft);
      curtop += parseInt(obj.offsetTop);
    }
  }
  return [curleft, curtop];
}

function findCenterPos(obj){
  posi = findPos(obj);
  posi[0] = posi[0] + obj.offsetWidth / 2;
  posi[1] = posi[1] + obj.offsetHeight / 2;
  return posi;
}

function getMousePos(evt){
  if (evt.pageX || evt.pageY) {
    return {
      x: parseInt(evt.pageX),
      y: parseInt(evt.pageY)
    };
  }
  return {
    x: parseInt(evt.clientX) + parseInt(document.body.scrollLeft) - parseInt(document.body.clientLeft),
    y: parseInt(evt.clientY) + parseInt(document.body.scrollTop) - parseInt(document.body.clientTop)
  };
}
function isIE(){
  return (navigator.userAgent.indexOf("MSIE") > -1);
}


function switchClass(evt, classname, elementname)
{
  if (!evt) evt = window.event;
  try {
    var tgt;
    if (elementname) tgt = DOM.getEventTarget(evt, elementname);
    else tgt = DOM.getEventTarget(evt);
    if (tgt!=null) tgt.className = classname;  	
  } catch (e) {
  	alert("switchClass: " + e.message + "\n" + evt.type + "\n" + tgt);
  }
}

Jalava._modules['Util'] = true;