$("#state-dropdown").onchange = function(e){

};
$("#maxSlider").change = function(e){
	console.log("max slider change");
}

function updateTableBody(newTableBody, oldTableBody){
	let tableBody = oldTableBody;
	tableBody.parentNode.replaceChild(newTableBody,tableBody);
	tableBody = newTableBody;
	tableBody.id = "bloc-table-body";
}

$("#updateThresh").click(function(e){
	if(!currentState){
		window.alert("Please select a state first.");
	}else{
		let popSlider = $("#popSlider")[0];
		let voteSlider = $("#voteSlider")[0];
		let popThreshold = popSlider.value / 100;
		let voteThreshold = voteSlider.value / 100;

		$.ajax({
		'type': "POST",
		'dataType': 'json',
		'url': "http://localhost:8080/runPhase0",
		'data': JSON.stringify({'blocThreshold':popThreshold,
			'voteThreshold':voteThreshold}),
		'contentType': "application/json",
		'statusCode':{
			"200": function (data) {
				let results = data.results;
				let newTableBody = document.createElement("tbody");
				let tableBody = $("#bloc-table-body")[0];

				table.parentNode.replaceChild(newTableBody,tableBody);
				tableBody = newTableBody;
				tableBody.id = "bloc-table-body";

				for(result of results){
					let row = table.insertRow(0);
					row.insertCell(0).innerHTML = result.isVoteBloc;
					row.insertCell(1).innerHTML = result.precinctName;
					row.insertCell(2).innerHTML = result.demographic;
					row.insertCell(3).innerHTML = result.winningParty.substring(0,3);
				}
			},
			"400": function(data){
				console.log("error",data);
			}
		}
            });
	}
});
