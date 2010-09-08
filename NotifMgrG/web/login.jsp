<%-- 
    Document   : login
    Created on : May 27, 2010, 3:13:25 PM
    Author     : fabrizio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
     <%


               //    String email = request.getParameter("email");
          //      String email = "fabrizio.torretta@gmail.com";


            //    String pwd = request.getParameter("pwd");
                String sessuser =(String) session.getAttribute("email");
                String sesspwd = (String) session.getAttribute("pwd");
                if(sessuser != null)
                {
           //     String pwd = "gregorio";

    %>

    <jsp:forward page="index.jsp"/>

    <%}%>
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
        function checkid()
        {


                    try{
                           
                            var url1 = "./IdentityChecker";
                            var email = document.getElementById('email').value;
                            var pwd = document.getElementById('pwd').value
                       //     var email = "fabrizio.torretta@gmail.com";
                       //     var pwd = "gregorio";

                            objXml = new XMLHttpRequest();

                            objXml.open("POST",url1,false);

                            objXml.setRequestHeader('Content-Type', "text/plain;charset=UTF-8");


                            objXml.setRequestHeader('email',email);
                            objXml.setRequestHeader('pwd',pwd);
                            objXml.send(null);
                           str = objXml.responseText;

                            //confirm("STR: " +str);
                            if(str == "checked")  document.loginform.submit();
                            else {alert("Invalid Google Credentials. Try Again");}

                    }catch(e){alert(e.message);}


        }
</script>



        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Notification Managewr Login Page</title>
    </head>
    <body style="background-color: #add8e6">

<h1>Welcome to Notification Manager Login Page.</h1>
<p>If you are new to the Content Community, please <a href="register.html"><strong>register now</strong></a></p>

<p>Please feel free to email us at <a href="mailto:fabrizio.torretta@gmail.com">Fabrizio Torretta</a> if you experience problems or have questions.</p>

 <form name="loginform" id="loginform"  action="index.jsp" onsubmit="return validate_form(this)"method="post">
<table summary="Demonstration form">
  <tbody>

  
  <tr>
    <td><label for="email">Your email:</label></td>
    <td><input name="email" id="email" size="35" maxlength="30" type="text"></td>
  </tr>
  <tr>
  <tr>
    <td><label for="pwd">Your password</label></td>
    <td><input name="pwd" id="pwd" size="35" maxlength="25" type="password"></td>
  </tr>

  <tr>
   
    <td><input name="Submit" value="Login" type="button" onclick="checkid();"/></td>
   
  </tr>
  </tbody>
</table>

</form>


    </body>
</html>


