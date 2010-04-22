<?php

include_once "users.php";
include_once "creatediagram.php";

if (!isset($_POST['name'])) error("No diagram name provided");
$desc =  (isset($_POST['desc'])) ? $_POST['desc'] : "";

$CURRENT_USER = user_get();

$i = createDiagram($CURRENT_USER, $_POST['name'], $desc);

if ($i > 0)
{
  echo "[OK]".$i;
}
else if ($i==-1)
{
  echo "[ER]Name already exists.";
}
else
{
  echo "[ER]Could not create diagram";
}

?>
