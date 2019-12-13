 $(document).ready(function () {

	$("#state-dropdown").onchange = function (e) {

	};
	$("#maxSlider").change = function (e) {
		console.log("max slider change");
	}

	$("#updateThresh").click(function (e) {
		if (currentState == null) {
			window.alert("Please select a state first.");
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
							let p0 = document.createElement("p");
							p0.appendChild(t0);
							row.insertCell(0).appendChild(p0);

							let t1 = document.createTextNode(summary[demographic]["blocs"]);
							let p1 = document.createElement("p");
							p1.appendChild(t1);
							row.insertCell(1).appendChild(p1);

							let t2 = document.createTextNode(summary[demographic]["voteblocs"]);
							let p2 = document.createElement("p");
							p2.appendChild(t2);
							row.insertCell(2).appendChild(p2);

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
							let p3 = document.createElement("p");
							p3.appendChild(t3);
							row.insertCell(3).appendChild(p3);
						}

					},
					"400": function (data) {
						console.log("error", data);
					}
				}
			});
		}
	});

	$("#runGerry").click(function (e) {
		if (currentState == null) {
			window.alert("Please select a state first.");
		} else {
			let demographics = ["WHITE", "ASIAN", "BLACK"];
			let demographicMinimum = 0.13;
			let demographicMaximum = 0.75;
			let targetDistrictNum = 10;

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
						let results = data.results;
						console.log(results.length);
					},
					"400": function (data) {
						console.log("error", data);
					}
				}
			});
		}
	});

    $("#runAnneal").click(function (e) {
        if (currentState == null) {
            window.alert("Please run phase 1 first.");
        } else {
            let measureWeights = {
                'GERRYMANDER_DEMOCRATIC' : 1,
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
                        console.log("Before: " + result.before);
                        console.log("After: " + result.after);
                        console.log("Diff: " + (result.after - result.before));
                    },
                    "400": function (data) {
                        console.log("error", data);
                    }
                }
            });
        }
    });

$("#runAnneal").click(function (e) {

		let newTableBody = document.createElement("tbody");
		let tableBody = $("#demo-tbody")[0];

		tableBody.parentNode.replaceChild(newTableBody, tableBody);
		tableBody = newTableBody;
		tableBody.id = "demo-tbody";


		gerryDemoCheckBoxes = $(".gerry-demo")
		
		//for every demographic that is checked
		for (button of gerryDemoCheckBoxes){
			if($(button).prop("checked") == true){
				let row = tableBody.insertRow(0);

				let t0 = document.createTextNode($(button).attr('value')); //demographic
				let p0 = document.createElement("p");
				p0.appendChild(t0);
				row.insertCell(0).appendChild(p0);

				let t1 = document.createTextNode("1"); //population
				let p1 = document.createElement("p");
				p1.appendChild(t1);
				row.insertCell(1).appendChild(p1);

				let t2 = document.createTextNode("1"); //percentage
				let p2 = document.createElement("p");
				p2.appendChild(t2);
				row.insertCell(2).appendChild(p2);
			}

	}

});

});
