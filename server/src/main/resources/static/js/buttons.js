$(document).ready(function() {

document.addEventListener("keypress", function(event) {
	if(event.key == "w"){
		console.log(event.key);
	}
});

$("#state-dropdown").onchange = function(e){

};
$("#maxSlider").change = function(e){
	console.log("max slider change");
}

function updateTableBody(newTableBody, oldTableBody){
	let tableBody = oldTableBody;
	tableBody.parentNode.replaceChild(newTableBody,tableBody);
	tableBody = newTableBody;
	tableBody.id = "bloc-tbody";
}

$("#updateThresh").click(function(e){
	if(!currentState){
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
		'data': JSON.stringify({'blocThreshold':popThreshold,
			'voteThreshold':voteThreshold}),
		'contentType': "application/json",
		'statusCode':{
                    "200": function (data) {
                            let results = data.results;
                            let newTableBody = document.createElement("tbody");
                            let tableBody = $("#bloc-tbody")[0];

                            tableBody.parentNode.replaceChild(newTableBody,tableBody);
                            tableBody = newTableBody;
                            tableBody.id = "bloc-tbody";
                            
                            console.log(data);
                            for(result of results){
                                let row = tableBody.insertRow(0);
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


});
