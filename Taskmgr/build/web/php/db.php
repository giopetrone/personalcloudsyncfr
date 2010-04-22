<?php
error_reporting(0);

include_once "error.php";

define("DB_NAME", "JALAVA");
define("DB_USER", "jalava");
define("DB_PASSWORD", "password");

define ("DIAGRAMS_TABLE", "DIAGRAMS");
define ("USERS_TABLE", "USERS");

function db_connect()
{
  $db_connection = new mysqli('localhost', DB_USER, DB_PASSWORD, DB_NAME);
  $db_connection->autocommit(false);
  return $db_connection;
}

function db_close($db_connection, $commit)
{
  if (isset($commit) && $commit===true) $db_connection->commit();
  $db_connection->close();
}

function db_error($db_connection, $statement, $msg)
{
  if (isset($statement)) {
    $err = $statement->error;
    $statement->close();
  } else {
    $err = $db_connection->error;
  }
  $db_connection->close();
  //error($msg."(".$err.")");
  error($msg);
}


?>
