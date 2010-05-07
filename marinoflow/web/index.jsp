<%-- 
    Document   : index
    Created on : Mar 17, 2010, 1:29:46 PM
    Author     : marino
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">


<html> <!--</html> xmlns="http://www.w3.org/1999/xhtml">-->
<head>
<title>Edit Text</title>
<link type="text/css" rel="stylesheet" href="./css/dhtmlgoodies_calendar.css?random=20051112" media="screen"></link>
<script type="text/javascript" src="./js/dhtmlgoodies_calendar.js?random=20060118"></script>

<script type="text/javascript">
/*
 * Retrieve all contacts
 */

tinyMCE.init({
    mode : "textareas",
    theme : "advanced",
    theme_advanced_buttons1 : "fontselect,fontsizeselect,forecolor,separator,bold,italic,underline,justifyleft,justifycenter,justifyright,justifyfull",
    theme_advanced_buttons2 : "",
    theme_advanced_buttons3 : "",
    theme_advanced_toolbar_location : "top",
    theme_advanced_toolbar_align : "left",
    theme_advanced_more_colors : 0,setup : function(ed) {
    //ed.onInit.add(init);
    }
});

function init()
{
   try
   {
    if (window.parent.TextEdit)
    {
        data = window.parent.TextEdit.getCurrentContent();

    }
   }
  catch (e) {
    alert(e.message);
  }


}

function initForm(oForm, element_name) {

frmElement = oForm.elements[element_name];
frmElement.value = window.parent.TextEdit.getCurrentContent();

}

function initStatus(oForm, element_name) {

frmElement = oForm.elements[element_name];
frmElement.value = window.parent.TextEdit.getCurrentStatus();

}

function initDate(oForm, element_name) {

frmElement = oForm.elements[element_name];
frmElement.value = window.parent.TextEdit.getCurrentDate();

}

</script>

</head>
<body onload="initStatus(document.forms[0], 'status');initForm(document.forms[0], 'nome');initDate(document.forms[0], 'theDate');">
<div id="basic_container"></div>
<form name='myform'>
	<!-- Gets replaced with TinyMCE, remember HTML in a textarea should be encoded -->
	<label>Nome: <input id ="nome" name="nome" type="text" ></label>
	<br />
	<label>Stato del Task:
		<select name="status" id="status" >

            <option value="Task Status"> </option>
	    <option value="In progress">In progress</option>
	    <option value="Not Started yet">Not Started yet</option>
	    <option value="Done">Done</option>


	    </select>
	</label>
        <br />
	
        <label>Date: <input type="text" value="dd/mm/yy" id="theDate" name="theDate" onclick="displayCalendar(document.forms[0].theDate,'yyyy/mm/dd',this)"></label>
        
        <br/>
        <input type="button" name="reset" value="Cancella" onclick="window.parent.TextEdit.cancel()"/>
	<input type="button" name="ok" value="Ok"      onclick="window.parent.TextEdit.setContent(document.myform.nome.value,document.myform.status.value,document.myform.theDate.value);"/>

</form>

    <div id="basic_container"></div>

</body>
</html>
