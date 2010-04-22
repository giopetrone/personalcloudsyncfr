/* v0.8.0beta */

function ProcessNode(content, x, y, width, height, anchors, clazz, imageId) {
  ProcessNode.parent.call(this, content, x, y, width, height, anchors, clazz, imageId);
}

Jalava.copyPrototype(ProcessNode, GradientBlock);

/*
 * Determine if the given connection is allowed to connect
 * to this Block
 */
ProcessNode.prototype.isConnectable = function(connection, head) {
  if (!connection) {
    return this.from.length<2;
  }
  if (head==true) {
    return this.from.length<2;
  }
  return this.to.length<2;  	
}

Jalava._modules['ProcessNode'] = true;