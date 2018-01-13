function noscroll() {
	var dheight = $('body').height();
	var cbody = $('.content').height();
	var wheight = $(window).height();
	var cheight = wheight - dheight + cbody;

	if(wheight > dheight) {
		$('body').addClass('noscroll');
	} else if(wheight <= dheight) {
		$('body').removeClass('noscroll');
	} else {
	}
}

function resize(animate) {

	var dheight = $('body').height();
	var cbody = $('.content').height();
	var wheight = $(window).height();
	var cheight = wheight - dheight + cbody;

	if(animate == false) {
		$('.content').height(cheight);
	} else {
		$('.content').animate({
			height : cheight
		}, 500);
	}
	noscroll();
}


$(document).ready(function() {

	resize(true);
	$(window).resize(function() {
		resize(false);
	});
});
