/* v0.9.0beta */

// 301208: Modified dropTarget() to add block manually to diagram.
// 301208: Override createBlockObject() to instantiate UrlGradientBlock

function FlowChartPaletteFactory() {
  FlowChartPaletteFactory.parent.apply(this);
}
Jalava.copyPrototype(FlowChartPaletteFactory, PaletteFactory);

FlowChartPaletteFactory.prototype.createContent = function(objId, real) {

  var ele = DOM.createElement("DIV", "a");
  ele.className = "hello";
  ele.setAttribute("id","ele");
 
 
  var span = DOM.createElement("SPAN", "mytextarea");
  span.setAttribute("id","span");
 
   var nome = DOM.createElement("SPAN","nome");
   var status = DOM.createElement("INPUT","status");
   var date = DOM.createElement("INPUT","date");
   var owner = DOM.createElement("INPUT","owner");
   var assign = DOM.createElement("INPUT","assign");
   var elm1 = DOM.createElement("INPUT","elm1");
   var shared = DOM.createElement("INPUT","shared");
   var writers = DOM.createElement("INPUT","writers");
   var approved = DOM.createElement("SPAN","approved");
   var link = DOM.createElement("A","link");
 //  link.setAttribute("href","");
   link.setAttribute("target",'_blank');
  // link.innerHTML = "\nLink";
   approved.setAttribute("id","approved");
   shared.setAttribute("type","hidden");
   shared.setAttribute("id","shared");
   writers.setAttribute("id","writers");
   writers.setAttribute("type","hidden");
   nome.setAttribute("id","nome");
   status.setAttribute("id","status");
   status.setAttribute("type","hidden");
   date.setAttribute("id","date");
   date.setAttribute("type","hidden");
   owner.setAttribute("id","owner");
   owner.setAttribute("type","hidden");
   assign.setAttribute("id","assign");
   assign.setAttribute("type","hidden");
   elm1.setAttribute("id","desc");
   elm1.setAttribute("type","hidden");
   span.setAttribute("id","span");
   nome.innerHTML = "Click to edit";
 //  nome.style.color = "green";
   approved.innerHTML ="";
   approved.style.fontSize ="10px";
   span.appendChild(nome);
 //  span.appendChild(status);
 //  span.appendChild(date);
   span.appendChild(owner);
   span.appendChild(assign);
//   span.appendChild(elm1);
   span.appendChild(shared);
   span.appendChild(writers);
   span.appendChild(approved);
   span.appendChild(link);
   
  if (real) {
    span.className = "editable";
     if (objId=="rect") {
    
    var own = document.getElementById("owner").value;
     
    // {alert("owner uguali");span.className = "editable";}
   // else alert("owner diversi");
   // if(own == null) {span.className = "editable";alert("dentroIf")}
   
    span.ondblclick = function(event) {
        
        TextEdit.invoke(event);//Editable.tryedit();
    }
     }
     if(objId=="diamond") 
     {
         nome.innerHTML = "Decision";
         span.ondblclick = function(event) 
         {
            //alert(span.parentNode.parentNode.shared);
            TextEdit.invokeAndor(event);
        }

     }
     if(objId == "ellipse")
     {
         nome.innerHTML = "And?Or?";
         span.ondblclick = function(event) 
         {
         
            
            TextEdit.invokeAndor(event);
        }

     }
     if(objId == "rounded")
     {
         nome.innerHTML = "Start?End?";
         span.ondblclick = function(event)
         {


            TextEdit.invokeAndor(event);
        }

     }




  }
//confirm(objId);
  var img = new Image();
	img.src = "/img/" + objId + ".gif";
       // img.src = "./demo/african/Zebra32.gif";
	img.style.width = "32px";
	img.style.height = "32px";
	img.style.verticalAlign = "middle";
	//ele.appendChild(img);

  ele.appendChild(span);

  return ele;
}


FlowChartPaletteFactory.invoke = function(event) {
try
{
  FlowChartPaletteFactory.target = DOM.getEventTarget(event, "mytextarea");


 } catch (e) {
  	alert(e.message);
  }

}


FlowChartPaletteFactory.getCurrentContent = function() {
  var content = FlowChartPaletteFactory.target.innerHTML;
  if (content=="&nbsp;") content = "";
  return content;
}

FlowChartPaletteFactory.prototype.generateTemplate = function(objId) {

  // check the template cache
  if (this.templates[objId]) return this.templates[objId];

  // default behaviour
  var ele = this.createContent(objId, false);
  if (!ele) return;

  this.templates[objId] = UrlGradientBlock.prototype.generateTemplate(ele, null, null, 100, 50, null, "/img/flowchart/" + objId + "_f0f0f0_w100h50.gif");
  return this.templates[objId];

}

FlowChartPaletteFactory.prototype.dropTarget = function(evt, objId, left, top) {

  // try and determine the drop location
  var x = left - Jalava.diagram.container.offsetLeft;
  var y = top - Jalava.diagram.container.offsetTop;

  if (x<0 || y<0) return;

  var anchors  = this.generateAnchors(objId, 100, 50);

  var ele = this.createContent(objId, true);
  if (!ele) return;
//confirm("sonoin dropdi flow");
  var block = new UrlGradientBlock(ele, x+50, y+25, 100, 50, anchors, null, objId, "#f0f0f0");
  Jalava.diagram.addFigure(block);	// 301208: manually add block to diagram
//Jalava.diagram.addFigure(ele);
block.setBorderStyle("none");
  if (Jalava.propertyPage)
    block.addPropertyChangeListener(Jalava.propertyPage);
  Jalava.deselect();
  Jalava.temp_var.selected = block;
  block.select();
  return block;
}

FlowChartPaletteFactory.prototype.dropTargetSAVE = function(evt, objId, left, top) {

  // try and determine the drop location
  var x = left - Jalava.diagram.container.offsetLeft;
  var y = top - Jalava.diagram.container.offsetTop;

  if (x<0 || y<0) return;

  var anchors  = this.generateAnchors(objId, 100, 50);

  var ele = this.createContent(objId, true);
  if (!ele) return;

  var block = new UrlGradientBlock(ele, x+50, y+25, 100, 50, anchors, null, objId, "#f0f0f0");
  Jalava.diagram.addFigure(block);	// 301208: manually add block to diagram
  block.setBorderStyle("none");
  if (Jalava.propertyPage)
    block.addPropertyChangeListener(Jalava.propertyPage);
  Jalava.deselect();
  Jalava.temp_var.selected = block;
  block.select();
  return block;

}

/*
 * Override PaletteFactory.prototype.createBlockObject to return a UrlGradientBlock
 */
FlowChartPaletteFactory.prototype.createBlockObject = function() {
  return new UrlGradientBlock(null, 0, 0, 10, 10, {}, null, "rect", "transparent");
}

FlowChartPaletteFactory.prototype.generateAnchors = function(objId, width, height) {
  var anchors = new Array();
  var i = 0;

  if (objId=="rect") {
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = 0;   anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = 0;   anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 0;
  }
  else if (objId=="diamond") {
    anchors[i] = new Object();
    anchors[i].x = 0;  anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 0 ;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = -25;
  }
  else if (objId=="parallel") {
    anchors[i] = new Object();
    anchors[i].x = 0;  anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -40; anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -45; anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 0 ;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 40;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 45;  anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = -25;
  }
  else {
    anchors[i] = new Object();
    anchors[i].x = 0;  anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = -25;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = -50; anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 0 ;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 25;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = 0;
    anchors[i] = new Object();
    anchors[i].x = 50;  anchors[i++].y = -25;
  }
  return anchors;
}




Jalava._modules['FlowChartPaletteFactory'] = true;
