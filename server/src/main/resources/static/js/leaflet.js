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
		let p0 = document.createElement("p");
		p0.appendChild(t0);
		row.insertCell(0).appendChild(p0);

		let t1 = document.createTextNode(party);
		let p1 = document.createElement("p");
		p1.appendChild(t1);
		row.insertCell(1).appendChild(p1);

		let t2 = document.createTextNode(parties[party]);
		let p2 = document.createElement("p");
		p2.appendChild(t2);
		row.insertCell(2).appendChild(p2);

		let t3 = document.createTextNode(Math.round((parties[party]/votes)*100*10)/10 + "%");
		let p3 = document.createElement("p");
		p3.appendChild(t3);
		row.insertCell(3).appendChild(p3);
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
	let p0 = document.createElement("p");
	p0.appendChild(t0);
	row.insertCell(0).appendChild(p0);
	
	let t1 = document.createTextNode(incumbent);
	let p1 = document.createElement("p");
	p1.appendChild(t1);
	row.insertCell(1).appendChild(p1);

	let t2 = document.createTextNode(party);
	let p2 = document.createElement("p");
	p2.appendChild(t2);
	row.insertCell(2).appendChild(p2);
	
	
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
		p0 = document.createElement("p");
		p0.appendChild(t0);
		row.insertCell(0).appendChild(p0);
		
		t1 = document.createTextNode(demos[demo]);
		p1 = document.createElement("p");
		p1.appendChild(t1);
		row.insertCell(1).appendChild(p1);

		t2 = document.createTextNode(Math.round((demos[demo]/pop)*100*10)/10 + "%");
		p2 = document.createElement("p");
		p2.appendChild(t2);
		row.insertCell(2).appendChild(p2);
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

function onEachFeatureDistrict(feature, layer) {
	layer.on({
		mouseover: onHoverDistrict,
		mouseout: resetDistrictHighlight//,
		// click: initState
	});
	layer.on('mouseover', function () {
			$("#district-info").toggleClass("hide");
			$("#district-demo-data").toggleClass("hide");
    });
	layer.on('mouseout', function () {
			$("#district-info").toggleClass("hide");
			$("#district-demo-data").toggleClass("hide");
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
				congressionalDistricts = L.geoJson(data, {style: districtStyle, onEachFeature:onEachFeatureDistrict});
				districtLoadedFlag = true;
			}
		}
	});
}

function onEachFeaturePrecinct(feature, layer) {
	layer.on({
		mouseover: onHoverPrecinct,
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
			$("#district-demo-data").addClass("hide");
			$("#district-info").addClass("hide");
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
					$("#district-demo-data").addClass("hide");
					$("#district-info").addClass("hide");
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
