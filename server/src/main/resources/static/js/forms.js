/* Nav Bar Script */
$('#homeBtn').click(function() {
	map.setView(new L.LatLng(39.8283, -98.5795), 5.2)
})


$('[name="paneToggle"]').click(function() {
	let selector = $(this).data("target");
	$(selector).toggleClass('in');
});


/* Bloc Analysis Script */
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


/* Gerrymander Script */
var popRange = document.getElementById("popRange");
$(function() {
    $("#slider-range").slider({
      range: true,
      min: 0,
      max: 100,
      values: [25, 75],
      slide: function(event, ui) {
		popRange.innerHTML = ui.values[0] + "% - " + ui.values[1] + "%";
      }
	});
	popRange.innerHTML = $("#slider-range").slider("values", 0) + "% - " + $("#slider-range").slider("values", 1) + "%";
});

$('#gerryBtn').click(function() {

})
