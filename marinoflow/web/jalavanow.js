var currentPage;
var selectedDiagram;
var id;
var loadData;
var originalMsg;

function redirectToSignin() {
  document.location = "login/index.php";
}

function logout() {
  if (confirm("All unsaved work will be lost.")) {
    document.location = "login/logout.php?sid=" + SESSION_ID;
  }
}

function switchPage(pageId){
  if (currentPage) 
    currentPage.style.display = "none";
  currentPage = document.getElementById(pageId);
  if (currentPage) 
    currentPage.style.display = "block";
}

function creat(){
  // switchPage("createDiv");

   if (confirm("Your question")) {
 // do things if OK
}
//  document.location = "jalavanow.php?sid=" + SESSION_ID + "&mode=create";
 document.location = "jalavanow.php?sid=" + "gfsdgdsfg" + "&mode=create";
}

function manage() {
  document.location = "jalavanow.php?sid=" + SESSION_ID;	
}

function load(){
  switchPage("listDiv");
  var params = new Object();
  //document.getElementById("message").style.display = "block";
  //document.getElementById("adminMenu").style.display = "none";
  originalMsg = document.getElementById("adminMsg").innerHTML;
  document.getElementById("adminMsg").innerHTML = "Please wait...";
  Ajax.doPost("php/listdiagrams_ajax.php?sid=" + SESSION_ID, params, onList);
}

function onList(){
  if (Ajax.xmlHttp.readyState == 4) {
    if (Ajax.xmlHttp.responseText=="[ER]No User") return redirectToSignin();
	
    var list = JSON.parse(Ajax.xmlHttp.responseText);
    var selectList = document.getElementById("diagramList");
    while (selectList.tBodies[0].hasChildNodes()) {
      selectList.tBodies[0].removeChild(selectList.tBodies[0].lastChild);
    }
    for (var i = 0; i < list.length; i++) {
      var row = selectList.tBodies[0].insertRow(-1);
      row.setAttribute("name", "diagramRow");
      row.id = list[i][0];
      var cell = row.insertCell(-1);
      cell.innerHTML = list[i][1];
      cell = row.insertCell(-1);
      cell.innerHTML = list[i][2];
      cell = row.insertCell(-1);
      cell.innerHTML = list[i][3];
      cell = row.insertCell(-1);
      cell.innerHTML = list[i][4];
    }
    selectedDiagram = null;
    //document.getElementById("message").style.display = "none";
    //document.getElementById("adminMenu").style.display = "block";
    document.getElementById("adminMsg").innerHTML = originalMsg;
  }
}

function doSave(func){
  var params = new Object();
  params['id'] = id;
  params['data'] = Jalava.diagram.persist();
  document.getElementById("saveMsg").innerHTML = "Please wait...";
  document.getElementById("saveMsg").style.display = "block";
  Ajax.doPost("php/savediagram_ajax.php?sid=" + SESSION_ID, params, func);
}

function save(){
  doSave(saveComplete);
}

function saveComplete(){
  if (Ajax.xmlHttp.readyState == 4) {
    if (Ajax.xmlHttp.responseText=="[ER]No User") return redirectToSignin();
    if (Ajax.xmlHttp.responseText == "[OK]") {
      document.getElementById("saveMsg").innerHTML = "Diagram saved.";
    }
    else {
      document.getElementById("saveMsg").innerHTML = "Diagram could not be saved." + Ajax.xmlHttp.responseText;
    }
  }
}

function saveAndClose(){
  doSave(saveAndCloseComplete);
}

function saveAndCloseComplete(){
  if (Ajax.xmlHttp.readyState == 4) {
    if (Ajax.xmlHttp.responseText=="[ER]No User") return redirectToSignin();
    if (Ajax.xmlHttp.responseText == "[OK]") {
      document.location = "./jalavanow.php?sid=" + SESSION_ID;
    }
    else {
      document.getElementById("saveMsg").innerHTML = "Diagram could not be saved." + Ajax.xmlHttp.responseText;
    }
  }
}

function close(){
  if (confirm("Your diagram won't be saved. Are you sure?")) {
    document.location = "./jalavanow.php?sid=" + SESSION_ID;
  }
}

function selectRow(evt){
  var row = DOM.getEventTarget(evt, "diagramRow");
  if (!row) 
    return;
  if (selectedDiagram) 
    selectedDiagram.className = "";
  selectedDiagram = row;
  row.className = "selected";
}

function doCreate(){
  var params = new Object();
  params['name'] = document.forms['createForm'].elements['name'].value;
  if (params['name'] == "") 
    return alert("Please enter a name for your diagram.");
  params['desc'] = document.forms['createForm'].elements['desc'].value;
  document.getElementById("adminMenu").style.display = "none";
  document.getElementById("message").style.display = "block";
  Ajax.doPost("php/creatediagram_ajax.php?sid=" + SESSION_ID, params, createComplete);
}

function createComplete(){
  if (Ajax.xmlHttp.readyState == 4) {
  	var code = Ajax.xmlHttp.responseText.substring(0,4);	
    if (code == "[OK]") {
      id = Ajax.xmlHttp.responseText.substring(4);
      switchPage("");
      document.getElementById("message").style.display = "none";
      document.getElementById("editMenu").style.display = "block";
      Jalava.start(initJalava);
    }
    else {
	  var errormsg = (code == "[ER]") ? Ajax.xmlHttp.responseText.substring(4) : "Diagram could not be created.";
      if (errormsg=="No User") return redirectToSignin();
      document.getElementById("message").style.display = "none";
      document.getElementById("adminMenu").style.display = "block";
      document.getElementById("adminMsg").innerHTML = errormsg;
	}
  }
}

function doDelete(){
  if (!selectedDiagram) {
    return alert("Please select a diagram.");
  }
  if (confirm("Are you sure you want to delete this diagram?")) {
    var params = new Object();
    params['id'] = selectedDiagram.id;
    Ajax.doPost("php/deletediagram_ajax.php?sid=" + SESSION_ID, params, deleteComplete);
  }
}

function deleteComplete(){
  if (Ajax.xmlHttp.readyState == 4) {
    if (Ajax.xmlHttp.responseText=="[ER]No User") return redirectToSignin();
    if (Ajax.xmlHttp.responseText == "[OK]") {
      document.getElementById("adminMsg").innerHTML = "Diagram has been deleted.";
      load();
    }
    else {
      document.getElementById("adminMsg").innerHTML = "Diagram could not be deleted.";
    }
  }
}

function doLoad(){
  if (!selectedDiagram) {
    return alert("Please select a diagram.");
  }
  var params = new Object();
  params["id"] = selectedDiagram.id;
  document.getElementById("adminMenu").style.display = "none";
  document.getElementById("message").style.display = "block";
  Ajax.doPost("php/loaddiagram_ajax.php?sid=" + SESSION_ID, params, loadComplete);
}

function loadComplete(){
  if (Ajax.xmlHttp.readyState == 4) {
    if (Ajax.xmlHttp.responseText=="[ER]No User") return redirectToSignin();
    if (Ajax.xmlHttp.responseText != "") {
      id = selectedDiagram.id;
      switchPage("");
      document.getElementById("message").style.display = "none";
      document.getElementById("editMenu").style.display = "block";
      loadData = Ajax.xmlHttp.responseText;
      Jalava.start(loadJalava);
    }
  }
}

function loadJalava(){
  initJalava();
  Jalava.diagram.load(loadData);
}

function init(){
  //currentPage = document.getElementById("quickstartDiv");
  currentPage = document.getElementById("listDiv");
  if (currentPage) load();
  else currentPage = document.getElementById("createDiv");
}


// initialise Jalava here
function initJalava(){
  
  Jalava.diagram = new Diagram(220, 50, "800", "800");
  var palette = new Palette(new FlowChartPaletteFactory(), 0, 50, 200);
  palette.addItem("rounded", "Start", Palette.DRAG_TOOL, "./img/rounded.gif");
  palette.addItem("rect", "Process", Palette.DRAG_TOOL, "./img/rect.gif");
  palette.addItem("diamond", "Decision", Palette.DRAG_TOOL, "./img/diamond.gif");
  palette.addItem("parallel", "Input/Output", Palette.DRAG_TOOL, "./img/parallel.gif");
  palette.addItem("ellipse", "Connector", Palette.DRAG_TOOL, "./img/ellipse.gif");
  palette.addItem("connection", "Connection", Palette.CLICK_TOOL, "./img/line.gif");
  Jalava.propertyPage = new FlowChartPropertyPage(0, 250, 10); 
  
  Jalava.palette = palette;

}

// start Jalava
Jalava.addModule("UrlGradientBlock");
Jalava.addModule("FlowChartPropertyPage");
Jalava.addModule("FlowChartPaletteFactory");

window.onload = init;
  
