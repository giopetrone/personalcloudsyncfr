<?php

include "users.php";
include "countdiagrams.php";

$CURRENT_USER = user_get();

echo countDiagrams($CURRENT_USER);

?>
