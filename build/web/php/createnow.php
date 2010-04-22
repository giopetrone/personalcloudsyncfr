<!--== Create =================================================-->
<div id="createDiv">
  <?php //////////////////////////////////////////////////
  include_once "./php/users.php";
  include_once "./php/countdiagrams.php";
  $CURRENT_USER = user_get();
  if (countDiagrams($CURRENT_USER) < 5)
  {
  ?>
  <p class="para">
    Enter the details for your diagram:
  </p>
  <form name="createForm">
    <p class="para">
      Name (maximum 40 characters): <input type="text" size="40" maxlength="40" name="name" />
    </p>
    <p class="para">
      Description (maximum 255 characters):
      <br/>
      <textarea cols="80" rows="6" name="desc" maxlength="255"></textarea>
    </p>
  </form>
  <button onclick="doCreate()">Create</button>
  <?php } else { /////////////////////////////////////// ?>
  <p class="para">
    Sorry, currently, you can only create up to 5 diagrams. 
  </p>
  <?php } ////////////////////////////////////////////// ?>
</div>
