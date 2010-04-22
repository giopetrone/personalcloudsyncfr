<?
function error($msg)
{
  echo "Some error occurred - ".$msg."<br>";
  die();
}

function ajax_error($msg)
{
  echo "[ER]".$msg;
  die();
}

?>
