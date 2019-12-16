// global var
var geojson;
var currentState;
var congressionalDistricts;
var precincts;
var statePrecincts = {"OHIO":{}};
var stateIDs = {};
var stateLoadedFlags = {};
var districtLoadedFlag = false;
var precinctLoadedFlag = false;

// get color function
function getColor(state) {
	return state == "Alabama" ? '#800026' :
	       state == "Oregon"  ? '#1100BB' :
	       state == "Ohio"  ? '#9CFF88' :
	                  '#FFEDA0';
}

function getRandomColor() {
  var letters = '0123456789ABCDEF';
  var color = '#';
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
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

function districtStyle(feature) {
	let color;
	let party = feature.properties.PARTY;
	if(party == "DEM"){
		color = "#1E90FF"
	}else if(party == "REP"){
		color = "#FF0000"
	}else{
		color = "#FFEDA0";
	}
	return {
		fillColor: color,
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

function onHoverPrecinct(e){
	let layer = e.target;
	let feature = layer.feature;
	let properties = feature.properties;
	let tableBody = $("#precinct-votes-table")[0];
	let newTableBody = document.createElement("tbody");

	tableBody.parentNode.replaceChild(newTableBody, tableBody);
	tableBody = newTableBody;
	tableBody.id = "precinct-votes-table";

	let code = properties["PRECODE"];
	let rep = properties["PRES16R"];
	let dem = properties["PRES16D"];
	let votes = properties["TOTVOTE16"];
	let parties = {"DEM":dem,"REP":rep};

	for(party of Object.keys(parties)){
		let row = tableBody.insertRow(0);

		let t0 = document.createTextNode(code);
		row.insertCell(0).appendChild(t0);

		let t1 = document.createTextNode(party);
		row.insertCell(1).appendChild(t1);

		let t2 = document.createTextNode(parties[party]);
		row.insertCell(2).appendChild(t2);

		let t3 = document.createTextNode(Math.round((parties[party]/votes)*100*10)/10 + "%");
		row.insertCell(3).appendChild(t3);
	}

	if(layer.districtGroup){
		layer.districtGroup.setStyle(
			{
				opacity: 0,
				fillOpacity: 1.0
			}
		);
	}else{
		layer.setStyle(
			{
				weight: 5,
				color:"#666",
				dashArray: '',
				fillOpacity: 0.7
			}
		);
	}
}

function onHoverDistrict(e){
	let layer = e.target;
	let feature = layer.feature;
	let properties = feature.properties;

	// first for incumbent and general district info
	let tableBody = $("#district-info-table")[0];
	let newTableBody = document.createElement("tbody");

	tableBody.parentNode.replaceChild(newTableBody, tableBody);
	tableBody = newTableBody;
	tableBody.id = "district-info-table";

	let district = properties["NAMELSAD"].replace("Congressional ","");
	let incumbent = properties["INCUB"];
	let party = properties["PARTY"];

	let row = tableBody.insertRow(0);

	let t0 = document.createTextNode(district);
	row.insertCell(0).appendChild(t0);

	let t1 = document.createTextNode(incumbent);
	row.insertCell(1).appendChild(t1);

	let t2 = document.createTextNode(party);
	row.insertCell(2).appendChild(t2);


	// then district demographic info
	tableBody = $("#district-demo-table")[0];
	newTableBody = document.createElement("tbody");
	tableBody.parentNode.replaceChild(newTableBody, tableBody);
	tableBody = newTableBody;
	tableBody.id = "district-demo-table";
	let pop = properties["TOTPOP"];
	let white = properties["NH_WHITE"];
	let black = properties["NH_BLACK"];
	let asian = properties["NH_ASIAN"];
	let hispanic = properties["HISPANIC"];

	let demos = {"White":white,"Black":black,"Asian":asian,"Hispanic":hispanic};

	for(demo of Object.keys(demos)){
		row = tableBody.insertRow(0);

		t0 = document.createTextNode(demo);
		row.insertCell(0).appendChild(t0);

		t1 = document.createTextNode(demos[demo]);
		row.insertCell(1).appendChild(t1);

		t2 = document.createTextNode(Math.round((demos[demo]/pop)*100*10)/10 + "%");
		row.insertCell(2).appendChild(t2);
	}

	layer.setStyle(
		{
			weight: 5,
			color:"#666",
			dashArray: '',
			fillOpacity: 0.7
		}
	);

	//demographic data for gerrymander pane
	tableBody.id = "demo-tbody";
	let pop = properties["TOTPOP"];
	let white = properties["NH_WHITE"];
	let black = properties["NH_BLACK"];
	let asian = properties["NH_ASIAN"];
	let hispanic = properties["HISPANIC"];

	let demos = {"White":white,"Black":black,"Asian":asian,"Hispanic":hispanic};

	for(demo of Object.keys(demos)){
		row = tableBody.insertRow(0);

		t0 = document.createTextNode(demo);
		row.insertCell(0).appendChild(t0);

		t1 = document.createTextNode(demos[demo]);
		row.insertCell(1).appendChild(t1);

		t2 = document.createTextNode(Math.round((demos[demo]/pop)*100*10)/10 + "%");
		row.insertCell(2).appendChild(t2);
	}

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

function resetPrecinctHighlight(e) {
	let layer = e.target;
	if(layer.districtGroup){
		layer.districtGroup.setStyle(
			{
				weight: 2,
				color: 'white',
				opacity: 1,
				dashArray: '3',
				fillOpacity: 0.5
			}
		);
	}else{
		layer.setStyle({
			weight: 2,
			opacity: 1,
			color: 'white',
			dashArray: '3',
			fillOpacity: 0.5
		});
	}
}

function resetDistrictHighlight(e) {
	let layer = e.target;
	let feature = layer.feature;
	let color;
	let party = feature.properties.PARTY;
	if(party == "DEM"){
		color = "#1E90FF"
	}else if(party == "REP"){
		color = "#FF0000"
	}else{
		color = "#FFEDA0";
	}
	layer.setStyle({
		fillColor: color,
		weight: 2,
		opacity: 1,
		color: 'white',
		dashArray: '3',
		fillOpacity: 0.5
	});
}

// zoom to feature
function initState(e) {
	let feature = e.sourceTarget.feature;
	let stateName = feature.properties.name;
    map.fitBounds(e.target.getBounds());
	let selector = $("#state-pane");
	$(selector).toggleClass('in');
    if(!stateLoadedFlags[stateName]){
        console.log('Init state sent');
        $.ajax({
    		'type': "POST",
    		'dataType': 'json',
    		'url': "http://localhost:8080/initState",
    		'data': JSON.stringify({'stateName': stateName.toUpperCase()}),
    		'contentType': "application/json",
    		'statusCode':{
    			"200": function (data) {
						$(".alert").addClass("hide");
    			  currentState = data;
    				stateLoadedFlags[stateName] = true;
					initCongressionalDistricts(stateName);
					console.log('state loaded');
					initNeighbors();
					initPrecincts();
    			},
    			"400": function(data){
    				console.log("error",data);
    			}
    		}
    	});
    }
}

function initNeighbors() {
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
                initGeometry();
            },
            "400": function(data){
                console.log("error: failed to load neighbors");
            }
        }
    });
}

function initGeometry(){
    console.log('Init geometry sent');
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

function onEachFeatureDistrict(feature, layer) {
	layer.on({
		mouseover: onHoverDistrict,
		mouseout: resetDistrictHighlight
	});
	layer.on('mouseover', function () {
			$("#district-info").toggleClass("hide");
			$("#district-demo-data").toggleClass("hide");
			$("#demo-results").addClass("hide");
    });
	layer.on('mouseout', function () {
			$("#district-info").toggleClass("hide");
			$("#district-demo-data").toggleClass("hide");
			$("#demo-results").removeClass("hide");
	});
	layer._leaflet_id = feature.id;
	stateIDs[feature.properties.name] = feature.id;
	stateLoadedFlags[feature.properties.name] = false;
}

async function initCongressionalDistricts(stateName){
	$.ajax({
		'type': "GET",
		'dataType': 'json',
		'url': "http://localhost:8080/data/" + stateName.toUpperCase() + "_DISTRICTS.json",
		'statusCode':{
			"200": function(data){
				congressionalDistricts = L.geoJson(data, {style: districtStyle, onEachFeature:onEachFeatureDistrict});
				districtLoadedFlag = true;
			}
		}
	});
}

function onEachFeaturePrecinct(feature, layer) {
	layer.on({
		mouseover: onHoverPrecinct,
		mouseout: resetPrecinctHighlight//,
	});
	layer.on('mouseover', function () {
			$("#precinct-voting-data").toggleClass("hide");
    });
	layer.on('mouseout', function () {
			$("#precinct-voting-data").toggleClass("hide");
	});
	layer._leaflet_id = L.Util.stamp(layer);
	statePrecincts[currentState.name.toUpperCase()][feature.properties.PRECODE] = layer._leaflet_id;
	stateLoadedFlags[feature.properties.name] = false;
}

// listeners
function onEachFeature(feature, layer) {
	layer.on({
		mouseover: highlightFeature,
		mouseout: resetHighlight,
		click: initState
	});
	layer._leaflet_id = feature.id;
	stateIDs[feature.properties.name] = feature.id;
	stateLoadedFlags[feature.properties.name] = false;
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
			$("#district-demo-data").addClass("hide");
			$("#district-info").addClass("hide");
        }
    } else if (zoomlevel >= 8 && precinctLoadedFlag){
			map.addLayer(precincts);
            map.removeLayer(congressionalDistricts);
            $("#district-demo-data").addClass("hide");
            $("#district-info").addClass("hide");
    } else if (zoomlevel >= 6 && zoomlevel < 8){
        if(districtLoadedFlag){
        	map.addLayer(congressionalDistricts);
        }
	    if (map.hasLayer(precincts)) {
            map.removeLayer(precincts);
            $("#precinct-voting-data").addClass("hide");
        }
    }
});

/*L.marker([51.5, -0.09]).addTo(map)
    .bindPopup('A pretty CSS3 popup.<br> Easily customizable.')
    .openPopup();*/
