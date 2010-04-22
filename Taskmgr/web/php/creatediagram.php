<?php

include_once "error.php";
include_once "db.php";


function createDiagram($user, $name, $desc)
{
  $conn = db_connect();

  // first check that the name is not already in use
  $query = "select NAME from ".DIAGRAMS_TABLE.
           " where USERNAME = ? and NAME = ? ";
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("ss", 
                         $user, $name);
 
  $statement->execute() or db_error($conn, $statement, "DB Error - ");
  $statement->store_result();
  $i = $statement->num_rows;
  $statement->close();
	
  if ($i > 0) {
    db_close($conn, false);
    return -1; // name exists	   
  }
  
  $now = time();
  
  $dummy['blocks'] = Array();
  $dummy['connections'] = Array();  
  $data = json_encode($dummy);
  
  $query = "insert into ".DIAGRAMS_TABLE.
           " (USERNAME, NAME, DESCRIPTION, DATA, CREATED, MODIFIED) ".
           " values (?,?,?,?,?,?)";
  
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("ssssii", 
                       $user, $name, $desc, $data, $now, $now);
 
  $statement->execute() or db_error($conn, $statement, "DB Error - ");
  $i = $statement->insert_id;
  $statement->close();

  db_close($conn, true);
  
  return $i;
}


?>
