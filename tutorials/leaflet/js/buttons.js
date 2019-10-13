$("body").css("overflow", "hidden");

$("[data-toggle='toggle']").click(function() {
    let selector = $(this).data("target");
    $(selector).toggleClass('in');
});