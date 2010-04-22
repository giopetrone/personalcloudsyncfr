<?php

include "savediagram.php";
include "users.php";

if (!isset($_POST['id'])) error("No diagram ID was given.");
if (!isset($_POST['data'])) error("No diagram data was provided.");

$CURRENT_USER = user_get();
if ($CURRENT_USER==null) ajax_error("No User");

saveDiagram($CURRENT_USER, $_POST['id'], $_POST['data']);

echo "[OK]";

?>
