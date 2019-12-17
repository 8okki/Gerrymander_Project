 $(document).ready(function () {

	$("#runBlocs").click(function (e) {
		if (currentState == null) {
			$(".alert").removeClass("hide");
		} else {
			let popSlider = $("#popSlider")[0];
			let voteSlider = $("#voteSlider")[0];
			let popThreshold = popSlider.value / 100;
			let voteThreshold = voteSlider.value / 100;

			$.ajax({
				'type': "POST",
				'dataType': 'json',
				'url': "http://localhost:8080/runPhase0",
				'data': JSON.stringify({
					'blocThreshold': popThreshold,
					'voteThreshold': voteThreshold
				}),
				'contentType': "application/json",
				'statusCode': {
					"200": function (data) {
						let results = data.results;
						let summary = {};
						let newTableBody = document.createElement("tbody");
						let tableBody = $("#bloc-tbody")[0];

						tableBody.parentNode.replaceChild(newTableBody, tableBody);
						tableBody = newTableBody;
						tableBody.id = "bloc-tbody";

						for (result of results) {
							// initialize demographic if not already in table
							if(!summary[result.demographic]){
								summary[result.demographic] = {"blocs":0,"voteblocs":0,"parties":{}};
							}

							// if new party for demographic found, initialize
							if(!summary[result.demographic]["parties"][result.winningParty.substring(0,3)]){
								summary[result.demographic]["parties"][result.winningParty.substring(0,3)] = 0;
							}

							// increment demographic's winning party
							summary[result.demographic]["parties"][result.winningParty.substring(0,3)]++;

							// increment total demo block count
							summary[result.demographic]["blocs"]++;

							// increment votebloc count if votebloc
							if(result.isVoteBloc){
								summary[result.demographic]["voteblocs"]++;
							}
						}

						for (demographic of Object.keys(summary)){
							let row = tableBody.insertRow(0);
							let t0 = document.createTextNode(demographic);
							row.insertCell(0).appendChild(t0);

							let t1 = document.createTextNode(summary[demographic]["blocs"]);
							row.insertCell(1).appendChild(t1);

							let t2 = document.createTextNode(summary[demographic]["voteblocs"]);
							row.insertCell(2).appendChild(t2);

							let parties = summary[demographic]["parties"];
							let max = 0;
							let maxParty;
							for(party of Object.keys(parties)){
								if(parties[party] > max){
									maxParty = party;
									max = parties[party];
								}
							}
							let t3 = document.createTextNode(maxParty);
							row.insertCell(3).appendChild(t3);
						}

						$("#bloc-results").removeClass("hide");
					},
					"400": function (data) {
						console.log("error", data);
					}
				}
			});
		}
	});

	$("#legalGuide").click(function (e) {
		if (currentState == null) {
			$(".alert2").removeClass("hide");
		} else {
				$("#myModal")[0].style.display = "block";
		}
	});

	$(".close").click(function (e) {
		$("#myModal")[0].style.display = "none";
	});

	$(window).click(function (e) {
		if (event.target == $("#myModal")[0]) {
    	$("#myModal")[0].style.display = "none";
    }
	});

	$("#runGerry").click(function (e) {
		if (currentState == null) {
			$(".alert").removeClass("hide");
		} else {
		  let demographics = [];
			let demoCheckBoxes = $("[name='demographic']");
            for (demoCheckBox of demoCheckBoxes){
                if(demoCheckBox.checked){
                    demographics.push(demoCheckBox.value.toUpperCase());
                }
            }
			let demographicMinimum = $("#slider-range").slider("values", 0) / 100;
			let demographicMaximum = $("#slider-range").slider("values", 1) / 100;
			let targetDistrictNum = parseInt($("[aria-describedby='cong-dist']").val());

			$.ajax({
				'type': "POST",
				'dataType': 'json',
				'url': "http://localhost:8080/runPhase1",
				'data': JSON.stringify({
					'demographics': demographics,
					'demographicMinimum': demographicMinimum,
					'demographicMaximum': demographicMaximum,
					'targetDistrictNum': targetDistrictNum
				}),
				'contentType': "application/json",
				'statusCode': {
					"200": function (data) {
						if(phase1Running){
							for(district of data.districts){
								let districtGroup = districtIDs[district.id];
								districtGroup.clearLayers();
								districtGroup.population = district.districtPop;
								districtGroup.demographics = district.demoPopDist;
								for(precinct of district.precincts){
									let layer = precincts.getLayer(statePrecincts[currentState.name.toUpperCase()][precinct]);
									districtGroup.addLayer(layer);
									layer.districtGroup = districtGroup;
								}
								districtGroup.setStyle({fillColor: districtGroup.color});
							}
						}else{
							districtIDs = {};
							for(district of data.districts){
								let districtGroup = L.featureGroup();
								districtGroup.population = district.districtPop;
								districtGroup.demographics = district.demoPopDist;
								for(precinct of district.precincts){
									let layer = precincts.getLayer(statePrecincts[currentState.name.toUpperCase()][precinct]);
									districtGroup.addLayer(layer);
									layer.districtGroup = districtGroup;
								}
								districtIDs[district.id] = districtGroup;
							}
							colorPrecincts(data.districts);
						}
						// set phase 1 running flag to false as done
						phase1Running = false;
					},
					"400": function (data) {
						console.log("error", data);
					}
				}
			});
		}
	});
	
	$("#runGerryIncremental").click(function (e) {
		if (currentState == null) {
			$(".alert").removeClass("hide");
		} else {
		  let demographics = [];
			let demoCheckBoxes = $("[name='demographic']");
            for (demoCheckBox of demoCheckBoxes){
                if(demoCheckBox.checked){
                    demographics.push(demoCheckBox.value.toUpperCase());
                }
            }
			let demographicMinimum = $("#slider-range").slider("values", 0) / 100;
			let demographicMaximum = $("#slider-range").slider("values", 1) / 100;
			let targetDistrictNum = parseInt($("[aria-describedby='cong-dist']").val());

			$.ajax({
				'type': "POST",
				'dataType': 'json',
				'url': "http://localhost:8080/runPhase1Incremental",
				'data': JSON.stringify({
					'demographics': demographics,
					'demographicMinimum': demographicMinimum,
					'demographicMaximum': demographicMaximum,
					'targetDistrictNum': targetDistrictNum
				}),
				'contentType': "application/json",
				'statusCode': {
					"200": function (data) {
						if(phase1Running){
							for(district of data.districts){
								let districtGroup = districtIDs[district.id];
								districtGroup.clearLayers();
								districtGroup.population = district.districtPop;
								districtGroup.demographics = district.demoPopDist;
								for(precinct of district.precincts){
									let layer = precincts.getLayer(statePrecincts[currentState.name.toUpperCase()][precinct]);
									districtGroup.addLayer(layer);
									layer.districtGroup = districtGroup;
								}
								districtGroup.setStyle({fillColor: districtGroup.color});
							}
						}else{
							phase1Running = true;
							districtIDs = {};
							for(district of data.districts){
								let districtGroup = L.featureGroup();
								districtGroup.population = district.districtPop;
								districtGroup.demographics = district.demoPopDist;
								for(precinct of district.precincts){
									let layer = precincts.getLayer(statePrecincts[currentState.name.toUpperCase()][precinct]);
									districtGroup.addLayer(layer);
									layer.districtGroup = districtGroup;
								}
								districtIDs[district.id] = districtGroup;
							}
							colorPrecincts(data.districts);
						}
						if(data.finished){
							phase1Running = false;
						}
					},
					"400": function (data) {
						console.log("error", data);
					}
				}
			});
		}
	});

    $("#runAnneal").click(function (e) {
        if (currentState == null || !districtIDs || phase1Running) {
            $(".alert").removeClass("hide");
        } else {
            measureWeights = {};
            let measureCheckBoxes = $("[name='measure']");
            for(measureCheckBox of measureCheckBoxes){
                if(measureCheckBox.checked){
                    let weight = $("[name='" + measureCheckBox.value + "']")[0].value;
                    measureWeights[measureCheckBox.value] = parseInt(weight);
                }
            }

            $.ajax({
                'type': "POST",
                'dataType': 'json',
                'url': "http://localhost:8080/runPhase2",
                'data': JSON.stringify({
                    "measureWeights" : measureWeights
                }),
                'contentType': "application/json",
                'statusCode': {
                    "200": function (data) {
                        let result = data.result;

                        /* Number of MMs */
                        let newTableBody = document.createElement("tbody");
                        let tableBody = $("#MM-tbody")[0];
                        tableBody.parentNode.replaceChild(newTableBody, tableBody);
                        tableBody = newTableBody;
                        let row = tableBody.insertRow(0);

                        let t0 = document.createTextNode(result.mmBefore);
                        row.insertCell(0).appendChild(t0);

                        let t1 = document.createTextNode(result.mmAfter);
                        row.insertCell(1).appendChild(t1);

                        /* Objective Function Score */
                        newTableBody = document.createElement("tbody");
                        tableBody = $("#scores-tbody")[0];
                        tableBody.parentNode.replaceChild(newTableBody, tableBody);
                        tableBody = newTableBody;
                        tableBody.id = "scores-tbody";
                        row = tableBody.insertRow(0);

                        t0 = document.createTextNode('Objective Score');
                        row.insertCell(0).appendChild(t0);

                        t1 = document.createTextNode(Math.round(result.objBefore*1000000)/10000);
                        row.insertCell(1).appendChild(t1);

                        t2 = document.createTextNode(Math.round(result.objAfter*1000000)/10000);
                        row.insertCell(2).appendChild(t2);

                        let scores = result.scores;
                        let rowCount = 1;
                        for(measure in scores){
                            let scoreBefore = scores[measure][0];
                            let scoreAfter = scores[measure][1];
                            row = tableBody.insertRow(rowCount++);

                            t0 = document.createTextNode(measure);
                            row.insertCell(0).appendChild(t0);

                            t1 = document.createTextNode(Math.round(scoreBefore*10000)/10000);
                            row.insertCell(1).appendChild(t1);

                            t2 = document.createTextNode(Math.round(scoreAfter*10000)/10000);
                            row.insertCell(1).appendChild(t2);
                        }

                        $("#anneal-results").removeClass("hide");

						for(district of data.districts){
								let districtGroup = districtIDs[district.id];
								districtGroup.clearLayers();
								districtGroup.population = district.districtPop;
								districtGroup.demographics = district.demoPopDist;
								for(precinct of district.precincts){
									let layer = precincts.getLayer(statePrecincts[currentState.name.toUpperCase()][precinct]);
									districtGroup.addLayer(layer);
									layer.districtGroup = districtGroup;
								}
								districtGroup.setStyle({fillColor: districtGroup.color});
						}
                    },
                    "400": function (data) {
                        console.log("error", data);
                    }
                }
            });
        }
    });

    async function colorPrecincts(districts) {
        for(district of districts){
            let randomColor = getRandomColor();
            districtGroup = districtIDs[district.id];
            districtGroup.setStyle({ fillColor: randomColor});
			districtGroup.color = randomColor;
        }
    }

    $('input[name=electionYear]').change(
        function(){

                let newTableBody = document.createElement("tbody");
                let tableBody = $("#state-election-results")[0];

                tableBody.parentNode.replaceChild(newTableBody, tableBody);
                tableBody = newTableBody;
                tableBody.id = "state-election-results";

                electionResults = {}
                electionResults["democratic2016"] = {"party":"Democratic","votes":"5,075,040","percentage":"43.6%"}
                electionResults["republican2016"] = {"party":"Republican","votes":"6,017,880","percentage":"51.7%"}
                electionResults["democratic2012"] = {"party":"Democratic","votes":"5,901,480","percentage":"50.7%"}
                electionResults["republican2012"] = {"party":"Republican","votes":"5,552,280","percentage":"47.7%"}
                electionResults["democratic2008"] = {"party":"Democratic","votes":"5,994,600","percentage":"51.5%"}
                electionResults["republican2008"] = {"party":"Republican","votes":"5,459,160","percentage":"46.9%"}

            if ($(this).is(':checked')) {
                        for (partyYear of Object.keys(electionResults)){
                            if(partyYear.indexOf($(this).val()) >= 0 ){
                                let row = tableBody.insertRow(0);

                                let t0 = document.createTextNode(electionResults[partyYear]["party"]); //demographic
                                row.insertCell(0).appendChild(t0);

                                let t1 = document.createTextNode(electionResults[partyYear]["votes"]); //population
                                row.insertCell(1).appendChild(t1);

                                let t2 = document.createTextNode(electionResults[partyYear]["percentage"]); //percentage
                                row.insertCell(2).appendChild(t2);
                            }
                     }
            }
        });

});
