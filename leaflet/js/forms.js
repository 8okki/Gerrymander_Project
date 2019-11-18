$("body").css("overflow", "hidden");

var popSlider = document.getElementById("popSlider");
var population = document.getElementById("population");
population.innerHTML = popSlider.value;

popSlider.oninput = function() {
	population.innerHTML = this.value;
}

var voteSlider = document.getElementById("voteSlider");
var vote = document.getElementById("vote");
vote.innerHTML = voteSlider.value;

voteSlider.oninput = function() {
	vote.innerHTML = this.value;
}

// var minSlider = document.getElementById("minSlider");
// var min = document.getElementById("minRange");
// min.innerHTML = minSlider.value;
//
// minSlider.oninput = function() {
// 	min.innerHTML = this.value;
// }
//
// var maxSlider = document.getElementById("maxSlider");
// var max = document.getElementById("maxRange");
// max.innerHTML = maxSlider.value;
//
// maxSlider.oninput = function() {
// 	max.innerHTML = this.value;
// }

$('[name="paneToggle"]').click(function() {
	let selector = $(this).data("target");
	$(selector).toggleClass('in');
});

$('#homeBtn').click(function() {
	map.setView(new L.LatLng(39.8283, -98.5795), 5.2)
})

$('#gerryBtn').click(function() {

})
