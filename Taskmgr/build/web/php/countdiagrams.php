<?php

include_once "error.php";
include_once "db.php";

function countDiagrams($user)
{
  $conn = db_connect();

  $query = "select count(ID) from ".DIAGRAMS_TABLE." where USERNAME = ? ";
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("s", $user);
 
  $statement->execute() or db_error($conn, $statement, "DB Error - ");
  $statement->bind_result($i);
  $statement->fetch();
  $statement->close();
	
  db_close($conn, false);

  return $i;
}

?>
