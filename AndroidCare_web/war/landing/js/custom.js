/*----------------------------------------------------*/
/*	Scrolling Nav Menu
/*----------------------------------------------------*/

/* Document Start */
$(document).ready(function() {

	$('.menu').onePageNav({
		begin: function() {

		},
		end: function() {

		},
		scrollOffset: 63
	});

/*----------------------------------------------------*/
/*	Revolution Sldier Settings
/*----------------------------------------------------*/
	if ($.fn.cssOriginal != undefined) {
		$.fn.css = $.fn.cssOriginal;
	}

	$('.fullwidthbanner').revolution(
		{
			delay: 9000,
			startwidth: 960,
			startheight: 520,
			onHoverStop: "off", // Stop Banner Timet at Hover on Slide on/off
			navigationType: "none", //bullet, none
			navigationArrows: "verticalcentered", //nexttobullets, verticalcentered, none
			navigationStyle: "none", //round, square, navbar, none
			touchenabled: "on", // Enable Swipe Function : on/off
			navOffsetHorizontal: 0,
			navOffsetVertical: 20,
			stopAtSlide: -1, // Stop Timer if Slide "x" has been Reached. If stopAfterLoops set to 0, then it stops already in the first Loop at slide X which defined. -1 means do not stop at any slide. stopAfterLoops has no sinn in this case.
			stopAfterLoops: -1, // Stop Timer if All slides has been played "x" times. IT will stop at THe slide which is defined via stopAtSlide:x, if set to -1 slide never stop automatic
			fullWidth: "on",
		});

});
/* Document End */