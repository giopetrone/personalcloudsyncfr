<%-- 
    Document   : index
    Created on : 19-set-2012, 15.44.35
    Author     : goy
--%>

<%@page import="java.util.Iterator"%>
<%@page import="web.Evento"%>
<%@page import="java.util.ArrayList"%>
<%@page import="web.DBmgr"%>
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
        <script type="text/javascript">
            // la mappa
            var map;
            // il punto cliccato dall'utente
            var puntoUtente;
            // il servizio di calcolo dei percorsi
            var directionsService;
            // il servizio di visualizzazione dei percorsi
            var directionDisplay;

            // funzione che inizializza la pagina (crea una mappa "sensibile" al click dell'utente; 
            // crea un directionsService e un directionsDisplay)
            function initialize() {
                // creo un punto geografico corrispondente a Torino
                var torino = new google.maps.LatLng(45.069285079074305,7.688970565795898);
                var myOptions = {
                    zoom: 12,
                    center: torino,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                }
                // creo una mappa centrata su Torino
                map = new google.maps.Map(document.getElementById("map"), myOptions);
	
                //rendo la mappa "sensibile" al click dell'utente
                google.maps.event.addListener(map, 'click', function(event) {
                    //accede alle coordinate del punto cliccato (event.latLng) e posiziona li' un marker
                    var marker = new google.maps.Marker({
                        position: event.latLng,
                        map: map
                    });
                    marker.setIcon('mm_20_green.png');
                    puntoUtente = event.latLng;
                });
                // creo un direcionsService e un directionsDisplay
                directionsService = new google.maps.DirectionsService();
                directionsDisplay = new google.maps.DirectionsRenderer();
                directionsDisplay.setMap(map);
            }	

            // funzione che prende in input i dati relativi ad un evento (nome, indirizzo, immagine, descrizione) e 
            // inserisce un marker "sensibile" (cioe' che al click fa comparire una infoWindow) sulla mappa contenuta nella variabile map
            function showEvent(name, add, img, des) {
                // mi creo un geocoder
                var geocoder = new google.maps.Geocoder();	  
                //invoco il metodo geocode, passandogli l'indirizzo  
                geocoder.geocode({'address': add}, function(results, status) {
                    if (status == google.maps.GeocoderStatus.OK) {
                        // centro la mappa sul punto geografico corrispondente all'indirizzo
                        map.setCenter(results[0].geometry.location);
                        // creo un marker su quel punto
                        var marker = new google.maps.Marker({
                            map: map, 
                            position: results[0].geometry.location
                        });
                        // creo una infoWindow e la lego al marker
                        var info = "<p><b>" + name + "</b><br />" + add + "<br /><img src='img/" + img + "' /><br />" + des + "</p>";
                        //var info = "<p><b>" + name + "</b><br />" + add + "<br /><img src='img/" + img + "' /><br />" + "</p>";
                        var infowindow = new google.maps.InfoWindow({
                            content: info
                        });
                        google.maps.event.addListener(marker, 'click', function(){
                            infowindow.open(map, marker);
                        });
                    } else {
                        alert("Geocode was not successful for the following reason: " + status);
                    }
                });
            }
  
            // funzione che prende in input due punti geografici e mostra il percorso sulla mappa legata all'oggetto directionsDisplay
            function calcRoute(start, end) {
                if (start == null) {
                    alert("Devi prima cliccare sulla mappa per indicare il punto da cui vuoi partire!");
                }
                var request = {
                    origin:start, 
                    destination:end,
                    travelMode: google.maps.DirectionsTravelMode.DRIVING
                };
                directionsService.route(request, function(response, status) {
                    if (status == google.maps.DirectionsStatus.OK) {
                        directionsDisplay.setDirections(response);
                    }
                });
            }

        </script>
    </head>
    <BODY onLoad="initialize()">
        <h1 align="center">OPEN API</h1>
        <h2 align="center">Google Maps: an example (using geocoding and directions)</h2>

        <div id="text">
            <P>
                Clicca sul nome dell'evento per visualizzarlo sulla mappa
                <BR />
                Clicca sulla mappa per indicare il punto di partenza
                <BR />
                Clicca su "percorso" accanto all'evento prescelto per visualizzare il percorso per raggiungerlo
            </P>        

            <%
                ArrayList<Evento> res = DBmgr.getEvents();
                out.println("n. eventi trovati: " + res.size());
                Iterator<Evento> iterator = res.iterator();
                while (iterator.hasNext()) {
                    Evento e = iterator.next();
                    //out.println(">>> " + e.getName());
                    //out.println(">>> " + e.getAddress());
                    out.println("<P>");
                    out.println("<A HREF=\"\" onClick=\"showEvent('" + e.getName() + "', '" + e.getAddress() + "', '" + e.getImage() + "', '" + e.getDescription() + "'); return false;\">" + e.getName() + "</A>");
                    out.println("<A HREF=\"\" onClick=\"calcRoute(puntoUtente, '" + e.getAddress() + "'); return false;\">percorso</A>");
                    out.println("</P>");
                }
            %>
        </div>

        <div id="map"></div>

    </body>
</html>
