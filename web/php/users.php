<?php
  
include_once "db.php";

define ("SESSION_EXPIRY", 3600);  // 1 hour

function user_getSessionId($sessionId)
{
  if (!isset($sessionId))
  {
  	if (isset($_GET['sid'])) return $_GET['sid'];
	else return null;
  }	
  return $sessionId;
}

function user_get($sessionId = null)
{  
  $sessionId = user_getSessionId($sessionId);
  if ($sessionId==null) return null;
  
  $conn = db_connect();

  $query = "select USERNAME, TOUCHED from ".USERS_TABLE.
           " where SID = ?";
  
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("s", $sessionId);
  $statement->execute() or db_error($conn, $statement, "DB Error - ");
  $statement->bind_result($user, $touched);
  $ok = $statement->fetch() != null;
  $statement->close();
 
  if (!$ok) 
  {
  	db_close($conn, false);
    return null;
  }
  
  if ($touched < time() - SESSION_EXPIRY)
  {
  	user_delete($sessionId, $conn);
	return null;
  }
  else
  {
    $query = "update ".USERS_TABLE." set TOUCHED = ? where SID = ?";
    $statement = $conn->prepare($query) or error($conn->error);
    $statement->bind_param("is", time(), $sessionId);
    $statement->execute() or db_error($conn, $statement, "DB Error - ");
    $statement->close();
  }
  
  db_close($conn, true);
  return $user;
} 

function user_delete($sessionId = null, $connection = null) 
{
  $sessionId = user_getSessionId($sessionId);
  if ($sessionId==null) return null;

  $conn = isset($connection) ? $connection : db_connect();
  
  $query = "delete from ".USERS_TABLE." where SID = ?";
  
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("s", $sessionId);
  $statement->execute() or db_error($conn, $statement, "DB Error - ");
  $statement->close();
	
  if (!isset($connection)) db_close($conn, true);
}

function user_create($user, $sessionId)
{
  $conn = db_connect();

  // first wipe all records of user
  $query = "delete from ".USERS_TABLE." where USERNAME = ? ";
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("s", $user);
  $statement->execute() or db_error($conn, $statement, "DB Error - ");
  $statement->close();

  $query = "insert into ".USERS_TABLE.
           " (USERNAME, SID, TOUCHED) ".
           " values (?,?,?)";
  
  $statement = $conn->prepare($query) or error($conn->error);
  $statement->bind_param("ssi", 
                         $user,
						 $sessionId,
						 time());
  $statement->execute() or db_error($conn, $statement, "DB Error - ");
  $statement->close();
  
  db_close($conn, true);
}


?>
