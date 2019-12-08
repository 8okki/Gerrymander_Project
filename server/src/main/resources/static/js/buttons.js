$(document).ready(function () {

	$("#state-dropdown").onchange = function (e) {

	};
	$("#maxSlider").change = function (e) {
		console.log("max slider change");
	}

	function updateTableBody(newTableBody, oldTableBody) {
		let tableBody = oldTableBody;
		tableBody.parentNode.replaceChild(newTableBody, tableBody);
		tableBody = newTableBody;
		tableBody.id = "bloc-tbody";
	}

	$("#updateThresh").click(async function (e) {
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
						let newTableBody = document.createElement("tbody");
						let tableBody = $("#bloc-tbody")[0];

						tableBody.parentNode.replaceChild(newTableBody, tableBody);
						tableBody = newTableBody;
						tableBody.id = "bloc-tbody";

						for (result of results) {
							let row = tableBody.insertRow(0);

                            let t0 = document.createTextNode(result.isVoteBloc)
							let p0 = document.createElement("p");
							p0.appendChild(t0);
							row.insertCell(0).appendChild(p0);

							let t1 = document.createTextNode(result.precinctName)
							let p1 = document.createElement("p");
							p1.appendChild(t1);
							row.insertCell(1).appendChild(p1);

							let t2 = document.createTextNode(result.demographic)
                            let p2 = document.createElement("p");
                            p2.appendChild(t2);
							row.insertCell(2).appendChild(p2);

							let t3 = document.createTextNode(result.winningParty.substring(0, 3))
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


});
