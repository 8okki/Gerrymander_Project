$("body").css("overflow", "hidden");
$('[name="demographic"]').prop('disabled', true);

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


$("#toolBtn").click(function() {
	let selector = $(this).data("target");
	$(selector).toggleClass('in');
});

$("#updateThresh").click(function() {
	$('[name="demographic"]').prop('disabled', false);
});

$("#fullScreenBtn").click(function() {

})