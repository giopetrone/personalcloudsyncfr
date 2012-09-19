<%-- 
    Document   : index
    Created on : 19-set-2012, 15.28.21
    Author     : goy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
        <link rel="stylesheet" type="text/css" href="stile.css" media="screen" />
        <title>Prova Open API</title>
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false">
</script>
    </head>
    <BODY onload="initialize()">
        <h1>Hello World!</h1>
<div id="text">
  <b>Scrivi l'indirizzo da visualizzare sulla mappa:</b>
  <br /><br />
    <input id="address" type="textbox" value="Torino, Italia">
    <input type="button" value="Geocode" onclick="codeAddress()">
</div>

<div id="map"></div>

<script type="text/javascript">
  var geocoder;
  var map;
  function initialize() {
    geocoder = new google.maps.Geocoder();
    var latlng = new google.maps.LatLng(45.069285079074305,7.688970565795898);
    var myOptions = {
      zoom: 12,
      center: latlng,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    }
    map = new google.maps.Map(document.getElementById("map"), myOptions);
  }

  function codeAddress() {
    var address = document.getElementById("address").value;
    geocoder.geocode( { 'address': address}, function(results, status) {
      if (status == google.maps.GeocoderStatus.OK) {
        map.setCenter(results[0].geometry.location);
        var marker = new google.maps.Marker({
            map: map, 
            position: results[0].geometry.location
        });
      } else {
        alert("Geocode was not successful for the following reason: " + status);
      }
    });
  }
</script>
    </body>
</html>
