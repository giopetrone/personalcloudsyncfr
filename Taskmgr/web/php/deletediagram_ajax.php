<?php
include "users.php";
include "deletediagram.php";

if (!isset($_POST['id'])) error("Diagram ID not provided.");

$CURRENT_USER = user_get();
if ($CURRENT_USER==null) ajax_error("No User");

$i = deleteDiagram($CURRENT_USER, $_POST['id']);

if ($i>0) echo "[OK]";
else echo "[ER]No diagram found.";

?>
