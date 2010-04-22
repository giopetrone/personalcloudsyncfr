<?php
 
include_once "users.php";
include "listdiagrams.php";
 
$CURRENT_USER = user_get();
if ($CURRENT_USER==null) ajax_error("No User");

$results = listDiagrams($CURRENT_USER);

echo json_encode($results);

?>
