<%-- 
    Document   : login
    Created on : May 27, 2010, 3:13:25 PM
    Author     : fabrizio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
         <script type="text/javascript">
         function validate_required(field,alerttxt)
            {
            with (field)
              {
              if (value==null||value=="")
                {
                alert(alerttxt);return false;
                }
              else
                {
                return true;
                }
              }
            }



        function validate_email(field,alerttxt)
        {
        with (field)
          {
          apos=value.indexOf("@");
          dotpos=value.lastIndexOf(".");
          if (apos<1||dotpos-apos<2)
            {alert(alerttxt);return false;}
          else {return true;}
          }
        }


        function validate_form(thisform)
        {
        with (thisform)
          {
          if (validate_required(email,"Email must be filled out!")==false)
              {email.focus();return false;}

          if (validate_email(email,"Not a valid e-mail address!")==false)
              {email.focus();return false;}
         
          if (validate_required(pwd,"You must insert the password!")==false)
              {pwd.focus();return false;}
          }
        }
</script>



        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Task Manager Login Page</title>
    </head>
    <body>

<h1>Welcome to Task Manager Login Page.</h1>
<p>If you are new to the Content Community, please <a href="register.html"><strong>register now</strong></a></p>

<p>Please feel free to email us at <a href="mailto:fabrizio.torretta@gmail.com">Fabrizio Torretta</a> if you experience problems or have questions.</p>

 <form name="loginform"  action="index.jsp" onsubmit="return validate_form(this)"method="get">
<table summary="Demonstration form">
  <tbody>

  
  <tr>
    <td><label for="email">Your email:</label></td>
    <td><input name="email" size="35" maxlength="30" type="text"></td>
  </tr>
  <tr>
  <tr>
    <td><label for="pwd">Your password</label></td>
    <td><input name="pwd" size="35" maxlength="25" type="password"></td>
  </tr>

  <tr>
   
    <td><input name="Submit" value="Login" type="submit" onclick="return validateFormOnSubmit(this)"/></td>
   
  </tr>
  </tbody>
</table>
</form>


    </body>
</html>


