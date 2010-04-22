<?php

include "users.php";
include "loaddiagram.php";

if (!isset($_POST['id'])) error("No diagram ID was given.");

$CURRENT_USER = user_get();
if ($CURRENT_USER==null) ajax_error("No User");
 
echo loadDiagram($CURRENT_USER, $_POST['id']);

?>
