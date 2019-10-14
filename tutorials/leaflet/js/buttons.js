$("body").css("overflow", "hidden");

$("[data-toggle='toggle']").click(function() {
	let element = $(this)[0];
	if(element.innerHTML.includes("+"))
		element.innerHTML = "-";
	else
		element.innerHTML = "+";
	let selector = $(this).data("target");
	$(selector).toggleClass('in');
});