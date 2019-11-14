// global var
var geojson;

// get color function
function getColor(state) {
	return state == "Alabama" ? '#800026' :
	       state == "Oregon"  ? '#1100BB' :
	       state == "Ohio"  ? '#9CFF88' :
	                  '#FFEDA0';
}
// style function
function style(feature) {
	return {
		fillColor: getColor(feature.properties.name),
		weight: 2,
		opacity: 1,
		color: 'white',
		dashArray: '3',
		fillOpacity: 0.5
	};
}
// highlight feature
function highlightFeature(e){
	let layer = e.target;
	layer.setStyle(
		{
			weight: 5,
			color:"#666",
			dashArray: '',
			fillOpacity: 0.7
		}
	);
}
// reset highlight
function resetHighlight(e) {
	geojson.resetStyle(e.target);
}
// zoom to feature
function initState(e) {
	let feature = e.sourceTarget.feature;
	let stateName = feature.properties.name;
	
	$.ajax({
		'type': "POST",
		'dataType': 'json',
		'url': "http://localhost:8080/initState",
		'data': JSON.stringify({'stateName': stateName.toUpperCase()}),
		'contentType': "application/json",
		'statusCode':{
			"200": function (data) {
				console.log("res:",data)
				map.fitBounds(e.target.getBounds());
				$("#toolBtn").click();
			},
			"400": function(data){
				console.log("error:",data.responseJSON.error);
			}
		}
	});
}
// listeners
function onEachFeature(feature, layer) {
	layer.on({
		mouseover: highlightFeature,
		mouseout: resetHighlight,
		click: initState
	});
}
// initialize the map on the "map" div with a given center and zoom
var map = L.map('map', {
	center: [39.8283, -98.5795],
	zoomControl: false,
	zoom: 5.2
});
L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

// add highlighting	
geojson = L.geoJson(statesData, {style: style, onEachFeature:onEachFeature}).addTo(map);




/*L.marker([51.5, -0.09]).addTo(map)
    .bindPopup('A pretty CSS3 popup.<br> Easily customizable.')
    .openPopup();*/