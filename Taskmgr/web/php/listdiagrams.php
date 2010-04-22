<?php

include_once "error.php";
include_once "db.php";

function listDiagrams($user)
{
  $toStrip = get_magic_quotes_gpc();

  $results = Array();

  $conn = db_connect();

  $query = "select ID, NAME, DESCRIPTION, CREATED, MODIFIED from ".DIAGRAMS_TABLE.
           " where USERNAME = ? ";
  
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("s", $user);
 
  $statement->execute() or db_error($conn, $statement, "DB Error - ");
  $statement->store_result();

  $statement->bind_result($id, $name, $desc, $created, $modified);
  while ($statement->fetch()!=NULL)
  {
    $arr = Array();
    $arr[] = $id;
    $arr[] = $toStrip ? stripslashes($name) : $name;
    $arr[] = $toStrip ? stripslashes($desc) : $desc;
    $arr[] = date("d M Y H:i:s", $created);
    $arr[] = date("d M Y H:i:s", $modified);
    $results[] = $arr; 
  }
  $statement->close();

  db_close($conn, true);
  
  return $results;
}


?>
