// global var
var geojson;
var currentState;
var congressionalDistricts;
var stateIDs = {};
var stateLoaded = {};

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

function hoverFeature(e){
	$("#voting-data").toggleClass("hide");
	$("#district-demo-data").toggleClass("hide");
}

// reset highlight
function resetHighlight(e) {
	geojson.resetStyle(e.target);
}

// zoom to feature
function initState(e) {
	let feature = e.sourceTarget.feature;
	let stateName = feature.properties.name;
    map.fitBounds(e.target.getBounds());
	let selector = $("#state-pane");
	$(selector).toggleClass('in');
	console.log("test");
    if(!stateLoaded[stateName]){
        $.ajax({
    		'type': "POST",
    		'dataType': 'json',
    		'url': "http://localhost:8080/initState",
    		'data': JSON.stringify({'stateName': stateName.toUpperCase()}),
    		'contentType': "application/json",
    		'statusCode':{
    			"200": function (data) {
    			    currentState = data;
    				stateLoaded[stateName] = true;
					initCongressionalDistricts(stateName);
    			},
    			"400": function(data){
    				console.log("error",data);
    			}
    		}
    	});
    }
}

function onEachFeature1(feature, layer) {
	layer.on({
		mouseover: highlightFeature,
		mouseout: resetHighlight//,
		// click: initState
	});
	layer.on('mouseover', function () {

    });
	layer.on('mouseout', function () {

	});
	layer._leaflet_id = feature.id;
	stateIDs[feature.properties.name] = feature.id;
	stateLoaded[feature.properties.name] = false;
}

async function initCongressionalDistricts(stateName){
	$.ajax({
		'type': "GET",
		'dataType': 'json',
		'url': "http://localhost:8080/data/" + stateName.toUpperCase() + "_DISTRICTS.json",
		'statusCode':{
			"200": function(data){
				congressionalDistricts = L.geoJson(data, {style: style, onEachFeature:onEachFeature1}).addTo(map);
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
	layer.on('mouseover', function () {
			$("#voting-data").toggleClass("hide");

			// $("#district-demo-data").toggleClass("hide");
    });
	layer.on('mouseout', function () {
			$("#voting-data").toggleClass("hide");

			// $("#district-demo-data").toggleClass("hide");
	});
	layer._leaflet_id = feature.id;
	stateIDs[feature.properties.name] = feature.id;
	stateLoaded[feature.properties.name] = false;
}

// initialize the map on the "map" div with a given center and zoom
var map = L.map('map', {
	center: [39.8283, -98.5795],
	zoomControl: false,
	zoom: 5
});

L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

// add highlighting
geojson = L.geoJson(statesData, {style: style, onEachFeature:onEachFeature}).addTo(map);

// function sleep(ms) {
//   return new Promise(resolve => setTimeout(resolve, ms));
// }
//
// async function wait1msec() {
//   await sleep(30000);
// }
//
// wait1msec();

map.on('zoomend', function() {
var zoomlevel = map.getZoom();
    if (zoomlevel < 7){
        if (map.hasLayer(congressionalDistricts)) {
            map.removeLayer(congressionalDistricts);
        }
				// else {
        //     console.log("no districts layer active");
        // }
    }
    if (zoomlevel >= 7){
        if (map.hasLayer(congressionalDistricts)){
						console.log("districts layer already added");
        }
				else {
        	map.addLayer(congressionalDistricts);
        }
    }
// console.log("Current Zoom Level =" + zoomlevel)
});

/*L.marker([51.5, -0.09]).addTo(map)
    .bindPopup('A pretty CSS3 popup.<br> Easily customizable.')
    .openPopup();*/
