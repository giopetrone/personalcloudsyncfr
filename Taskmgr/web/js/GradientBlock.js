/* v0.9.0beta */

// 301208: Added save/load functionality

function GradientBlock(content, x, y, width, height, anchors, type, imageId, color) {
  this.imageId = imageId;
  this.color = color;
  var imgurl = this.generateImageUrl(this.imageId, this.color, width, height);
  GradientBlock.parent.call(this, content, x, y, width, height, anchors, type, imgurl);
}

Jalava.copyPrototype(GradientBlock, Block);


GradientBlock.prototype.generateImageUrl = function(id, color, width, height) {
  return "img/" + id + "/" + id + "_" + GradientBlock.getColorIndex(color) + "_w400h200.gif";
 
}

GradientBlock.getColorIndex = function(color) {
  var i = GradientBlock.COLOR_INDEX[color];
  return i ? i : "transparent";
}

/*
 * Sets the background color of the Block
 */
GradientBlock.prototype.setBackgroundColor = function(colorCode) {
  var idx = GradientBlock.getColorIndex(colorCode);
  this.color = colorCode;
  this.setBackground(this.generateImageUrl(this.imageId, this.color, this.width, this.height));
}

GradientBlock.prototype.getBackgroundColor = function() {
  return this.color;
}

GradientBlock.prototype.save = function() {  
  var obj = GradientBlock.parent.prototype.save.apply(this);
  obj.color = this.color;
  obj.imageId = this.imageId;
  return obj;
}

GradientBlock.prototype.load = function(obj) {  
  this.color = obj.color;
  this.imageId = obj.imageId;
  GradientBlock.parent.prototype.load.apply(this, arguments);
}

GradientBlock.COLOR_INDEX = new Object();
GradientBlock.COLOR_INDEX['#000000'] = '000000';
GradientBlock.COLOR_INDEX['#808080'] = '808080';
GradientBlock.COLOR_INDEX['#800000'] = '800000';
GradientBlock.COLOR_INDEX['#808000'] = '808000';
GradientBlock.COLOR_INDEX['#008000'] = '008000';
GradientBlock.COLOR_INDEX['#008080'] = '008080';
GradientBlock.COLOR_INDEX['#000080'] = '000080';
GradientBlock.COLOR_INDEX['#800080'] = '800080';
GradientBlock.COLOR_INDEX['#c0c0c0'] = 'c0c0c0';
GradientBlock.COLOR_INDEX['#ff0000'] = 'ff0000';
GradientBlock.COLOR_INDEX['#ffff00'] = 'ffff00';
GradientBlock.COLOR_INDEX['#00ff00'] = '00ff00';
GradientBlock.COLOR_INDEX['#00ffff'] = '00ffff';
GradientBlock.COLOR_INDEX['#0000ff'] = '0000ff';
GradientBlock.COLOR_INDEX['#ff00ff'] = 'ff00ff';
GradientBlock.COLOR_INDEX['#ffffff'] = 'ffffff';
GradientBlock.COLOR_INDEX['transparent'] = 'transparent';


Jalava._modules['GradientBlock'] = true;
