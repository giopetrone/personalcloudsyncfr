<?php

include_once "error.php";
include_once "db.php";

function saveDiagram($user, $id, $data)
{
  $conn = db_connect();

  $query = "update ".DIAGRAMS_TABLE.
         " set DATA = ? , MODIFIED = ?".
         " where ID = ? and USERNAME = ?";
  
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("siis", 
                         $data, time(), $id, $user);
 
  $statement->execute() or db_error($conn, $statement, "DB Error - ");
  $statement->close();

  db_close($conn, true);
}

?>
