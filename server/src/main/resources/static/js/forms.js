/* Nav Bar Script */
$('#homeBtn').click(function () {
	map.setView(new L.LatLng(39.8283, -98.5795), 5.2)
})

$('[name="paneToggle"]').click(function () {
	let selector = $(this).data("target");
	$(selector).toggleClass('in');
});

$('[name="stateToggle"]').click(function () {
	var id = stateIDs[$(this).text()];
	var layer = geojson.getLayer(id);
	layer.fireEvent('click');
})


/* Bloc Analysis Script */
var popSlider = document.getElementById("popSlider");
var population = document.getElementById("population");
population.innerHTML = popSlider.value;
popSlider.oninput = function () {
	population.innerHTML = this.value;
}

var voteSlider = document.getElementById("voteSlider");
var vote = document.getElementById("vote");
vote.innerHTML = voteSlider.value;
voteSlider.oninput = function () {
	vote.innerHTML = this.value;
}


/* Gerrymander Script */
var popRange = document.getElementById("popRange");
$(function () {
	$("#slider-range").slider({
		range: true,
		min: 0,
		max: 100,
		values: [25, 75],
		slide: function (event, ui) {
			popRange.innerHTML = ui.values[0] + "% - " + ui.values[1] + "%";
		}
	});
	popRange.innerHTML = $("#slider-range").slider("values", 0) + "% - " + $("#slider-range").slider("values", 1) + "%";
});

/* Annealing Sliders */
var weightSlider = document.getElementById("weightSlider");
var weight = document.getElementById("weight");
weight.innerHTML = weightSlider.value;
weightSlider.oninput = function () {
	weight.innerHTML = this.value;
}

var weightSlider2 = document.getElementById("weightSlider2");
var weight2 = document.getElementById("weight2");
weight2.innerHTML = weightSlider2.value;
weightSlider2.oninput = function () {
	weight2.innerHTML = this.value;
}

var weightSlider3 = document.getElementById("weightSlider3");
var weight3 = document.getElementById("weight3");
weight3.innerHTML = weightSlider3.value;
weightSlider3.oninput = function () {
	weight3.innerHTML = this.value;
}

var weightSlider4 = document.getElementById("weightSlider4");
var weight4 = document.getElementById("weight4");
weight4.innerHTML = weightSlider4.value;
weightSlider4.oninput = function () {
	weight4.innerHTML = this.value;
}

var weightSlider5 = document.getElementById("weightSlider5");
var weight5 = document.getElementById("weight5");
weight5.innerHTML = weightSlider5.value;
weightSlider5.oninput = function () {
	weight5.innerHTML = this.value;
}

var weightSlider6 = document.getElementById("weightSlider6");
var weight6 = document.getElementById("weight6");
weight6.innerHTML = weightSlider6.value;
weightSlider6.oninput = function () {
	weight6.innerHTML = this.value;
}

var weightSlider7 = document.getElementById("weightSlider7");
var weight7 = document.getElementById("weight7");
weight7.innerHTML = weightSlider7.value;
weightSlider7.oninput = function () {
	weight7.innerHTML = this.value;
}

var weightSlider8 = document.getElementById("weightSlider8");
var weight8 = document.getElementById("weight8");
weight8.innerHTML = weightSlider8.value;
weightSlider8.oninput = function () {
	weight8.innerHTML = this.value;
}

var weightSlider9 = document.getElementById("weightSlider9");
var weight9 = document.getElementById("weight9");
weight9.innerHTML = weightSlider9.value;
weightSlider9.oninput = function () {
	weight9.innerHTML = this.value;
}

var weightSlider10 = document.getElementById("weightSlider10");
var weight10 = document.getElementById("weight10");
weight10.innerHTML = weightSlider10.value;
weightSlider10.oninput = function () {
	weight10.innerHTML = this.value;
}
