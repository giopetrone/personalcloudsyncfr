<?php
ini_set('display_errors',1);
error_reporting(E_ALL|E_STRICT);
include "php/loadnow.php";
$SID = isset($_GET['sid']) ? $_GET['sid'] : ""; 
$UID = isset($_COOKIE['uid']) ? $_COOKIE['uid']." | " : ""; 
?>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <title>Jalava Now!</title>
    <script language="JavaScript">
      var SESSION_ID = "<?php echo $SID?>"
     

    </script> 	
    <script type="text/javascript" src="./js/Jalava.js" > </script>
    <script type="text/javascript" src="./js/Ajax.js"></script>
    <script type="text/javascript" src="./js/json2.js"></script>
    <script type="text/javascript" src="./js/DOM.js"></script>
    <script type="text/javascript" src="./jalavanow.js"></script>
	<link rel="stylesheet" type="text/css" href="css/jalavanow.css" />
	
  </head>
  <body>
    <!--== Menu Bars =================================================-->
    <div id="adminMenu">
      <span class="menubar">
        <a href="javascript:manage()"><img src="web/manage.gif">Manage Diagrams</a>
        <a href="javascript:creat()"><img src="web/create.gif">Create New</a>
	  </span>
      <span class="menubar">&nbsp;&nbsp;&nbsp;</span>
      <span class="menubar" id="adminMsg"></span>
      <span class="useradmin"><?php echo $UID?><a href="./login/logout.php?sid=<?php echo $SID?>">Sign out</a></span>
      <span>&nbsp;</span><!--force line break-->
    </div>
	
    <div id="editMenu" style="display:none">
      <span class="menubar">
      	<a href="javascript:save()"><img src="web/save.gif">Save</a> 
		<a href="javascript:saveAndClose()"><img src="web/saveexit.gif">Save and Close</a> 
		<a href="javascript:close()"><img src="web/home.gif">Close without saving</a>
	  </span>
      <span class="menubar">&nbsp;&nbsp;&nbsp;</span>
      <span class="menubar" id="saveMsg"></span>
      <span class="useradmin"><?php echo $UID?><a href="javascript:logout()">Sign out</a></span>
      <span>&nbsp;</span><!--force line break-->
    </div>

    <div id="message" style="display:none">
      Please wait...
    </div>

    <!--== QuickStart =================================================-->
    <!--
	<div id="quickstartDiv">
      <p class="para">
        Welcome to JalavaNow! Quickstart. Select an option from the above menu bar to begin.
      </p>
    </div>
    -->
	<!--== Load =================================================-->
        include "php/loadnow.php";
<div id="listDiv">
  <p class="para">
  <table id="diagramList">
    <thead>
      <tr>
        <td width="20%">Name</td>
        <td width="40%">Description</td>
        <td width="15%">Created</td>
        <td width="15%">Modified</td>
      </tr>
    </thead>
    <tbody onclick="selectRow(event)">
    </tbody>
  </table>

  <p class="para">
    <button onclick="doLoad()">Load</button>
    <button onclick="doDelete()">Delete</button>
  </p>
</div>

    <?php
    sciopa();
ini_set('display_errors',1);
error_reporting(E_ALL|E_STRICT);
	  if (isset($_GET['mode']) && $_GET['mode']=="create") include "php/createnow.php";
	  else include "php/loadnow.php";
	?>
	
  </body>
</html>  
