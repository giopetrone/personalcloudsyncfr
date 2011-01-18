/* v0.9.0beta */

// 201208: constructor - parameter 'clazz' should be 'type'
// 301208: added save/load functionality

function UrlGradientBlock(content, x, y, width, height, anchors, type, imageId, color) {
 
 UrlGradientBlock.parent.call(this, content, x, y, width, height, anchors, type, imageId, color);
}

Jalava.copyPrototype(UrlGradientBlock, GradientBlock);


UrlGradientBlock.prototype.generateImageUrl = function(id, color, width, height) {
  if (color=="transparent") return "";
  color = color.replace("#", "");
 // return "./img/"+id +".gif";
 // return "/img/flowchart/" + id + "_f0f0f0_w100h50.gif" // MAR restituisci immagine corretta nonsempre rettangolo
 return "img/flowchart/" + id + "_f0f0f0_w100h50.gif";
 // return "./genimage/gen.php?id=" + id +"&c=" + color + "&w=" + width + "&h=" + height;
}

/*
 * Fired onmouseup when an anchor was previously being dragged.
 */
UrlGradientBlock.prototype.resizeEnd = function(){
  UrlGradientBlock.parent.prototype.resizeEnd.call(this);
  
  // load a new background image
  //var url = "../genimage/gen.php?id=" + this.imageId +"&c=" + idx + "&w=" + this.width + "&h=" + this.height;
  var url = this.generateImageUrl(this.imageId, this.color, this.width, this.height);
  
  this.setBackground(url);    
}

UrlGradientBlock.prototype.load = function(obj) {  
  this.width = obj.width;
  this.height = obj.height;
  this.setBackgroundColor(this.getBackgroundColor());
  UrlGradientBlock.parent.prototype.load.apply(this, arguments);
}


Jalava._modules['UrlGradientBlock'] = true;
