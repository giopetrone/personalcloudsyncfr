<?php
include_once "error.php";
include_once "db.php";

function deleteDiagram($user, $id)
{
  $conn = db_connect();

  $query = "delete from ".DIAGRAMS_TABLE.
         " where USERNAME = ? and ID = ?";
  
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("si", 
                         $user, $id);
 
  $statement->execute() or db_error($conn, $statement, "DB Error - ");
  $i = $statement->affected_rows;
  $statement->close();

  db_close($conn, true);
  
  return $i;
}

?>
