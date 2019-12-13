// global var
var geojson;
var currentState;
var congressionalDistricts;
var precincts;
var stateIDs = {};
var stateLoaded = {};
var districtLoadedFlag = false;
var precinctLoadedFlag = false;

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
    if(!stateLoaded[stateName]){
        console.log('Init state sent');
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
					console.log('state loaded');
					console.log('Init neighbor sent');
					$.ajax({
                        'type': "POST",
                        'dataType': 'json',
                        'url': "http://localhost:8080/initNeighbors",
                        'data': JSON.stringify({}),
                        'contentType': "application/json",
                        'statusCode':{
                            "200": function (data) {
                                console.log("neighbors loaded");
                                console.log('Init geometry sent');
                                initGeometry();
                            },
                            "400": function(data){
                                console.log("error: failed to load neighbors");
                            }
                        }
                    });
					initPrecincts();
    			},
    			"400": function(data){
    				console.log("error",data);
    			}
    		}
    	});
    }
}

function initGeometry(){
    $.ajax({
        'type': "POST",
        'dataType': 'json',
        'url': "http://localhost:8080/initGeometry",
        'data': JSON.stringify({}),
        'contentType': "application/json",
        'statusCode':{
            "200": function (data) {
                console.log("geometry loaded");
            },
            "400": function(data){
                console.log("error: failed to load geometry");
            }
        }
    });
}

function runPhase1(){
    let demographics = ['WHITE', 'BLACK'];
    let demographicMinimum = 0.25;
    let demographicMaximum = 0.75;
    let targetDistrictNum = 16;

    $.ajax({
            'type': "POST",
            'dataType': 'json',
            'url': "http://localhost:8080/runPhase1",
            'data': JSON.stringify({'demographics' : demographics,
                                    'demographicMinimum' : demographicMinimum,
                                    'demographicMaximum' : demographicMaximum,
                                    'targetDistrictNum' : targetDistrictNum}),
            'contentType': "application/json",
            'statusCode':{
                "200": function (data) {
                    console.log(data.results);
                },
                "400": function(data){
                    console.log("error: failed to run phase1");
                }
            }
        });
}

function onEachFeatureDistrict(feature, layer) {
	layer.on({
		mouseover: highlightFeature,
		mouseout: resetHighlight//,
		// click: initState
	});
	layer.on('mouseover', function () {
			$("#district-election-results").toggleClass("hide");
    });
	layer.on('mouseout', function () {
			$("#district-election-results").toggleClass("hide");
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
				congressionalDistricts = L.geoJson(data, {style: style, onEachFeature:onEachFeatureDistrict});
				districtLoadedFlag = true;
			}
		}
	});
}

function onEachFeaturePrecinct(feature, layer) {
	layer.on({
		mouseover: highlightFeature,
		mouseout: resetHighlight//,
		// click: initState
	});
	layer.on('mouseover', function () {
			$("#precinct-voting-data").toggleClass("hide");
    });
	layer.on('mouseout', function () {
			$("#precinct-voting-data").toggleClass("hide");
	});
	layer._leaflet_id = feature.id;
	stateIDs[feature.properties.name] = feature.id;
	stateLoaded[feature.properties.name] = false;
}

async function initPrecincts(stateName){
	$.ajax({
		'type': "GET",
		'dataType': 'json',
		'url': "http://localhost:8080/data/" + currentState["name"].toUpperCase() + "_PRECINCTS.json",
		'statusCode':{
			"200": function(data){
				precincts = L.geoJson(data, {style: style, onEachFeature:onEachFeaturePrecinct});
				precinctLoadedFlag = true;
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

map.on('zoomend', function() {
var zoomlevel = map.getZoom();
    if (zoomlevel < 7){
        if (map.hasLayer(congressionalDistricts)) {
            map.removeLayer(congressionalDistricts);
						$("#district-election-results").addClass("hide");
        }
				// else {
        //     console.log("no districts layer active");
        // }
    }
		else if (zoomlevel >= 9){
        if (map.hasLayer(precincts)){
						// console.log("districts layer already added");
        }
				else if(precinctLoadedFlag == true){
					map.addLayer(precincts);
        	map.removeLayer(congressionalDistricts);
					$("#district-election-results").addClass("hide");
        }
    }
    else if (zoomlevel >= 7 && zoomlevel < 9){
        if (map.hasLayer(congressionalDistricts)){
						// console.log("districts layer already added");
        }
				else if(districtLoadedFlag == true){
        	map.addLayer(congressionalDistricts);
        }
				if (map.hasLayer(precincts)) {
            map.removeLayer(precincts);
						$("#precinct-voting-data").addClass("hide");
        }
    }

// console.log("Current Zoom Level =" + zoomlevel)
});

/*L.marker([51.5, -0.09]).addTo(map)
    .bindPopup('A pretty CSS3 popup.<br> Easily customizable.')
    .openPopup();*/
