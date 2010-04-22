<?php

include_once "error.php";
include_once "db.php";

function loadDiagram($user, $id)
{
  $conn = db_connect();

  $query = "select DATA from ".DIAGRAMS_TABLE.
           " where USERNAME = ? and ID = ?";
  
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("ss", $user, $id);
 
  $statement->execute() or db_error($conn, $statement, "DB Error - ");

  $statement->bind_result($data);
  $statement->fetch();

  $statement->close();

  db_close($conn, true);

  if (get_magic_quotes_gpc())  return stripslashes($data);
  else return $data;
}
?>
