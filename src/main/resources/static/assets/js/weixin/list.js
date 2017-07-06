var winprops = "height=400px,width=300px,location=no,scrollbars=no," + "menubars=no,toolbars=no,resizable=yes";
var visMnu = "";
var actMnu, url;

function flipMenu(actMnu) {
	if (visMnu == ""){
		showMenu(actMnu);
		setTab(0,0);
	}
else {
		if (visMnu == actMnu)
			hideMenu(actMnu);
		else {
			hideMenu(visMnu);
			showMenu(actMnu);
		}
	}
}

function showMenu(actMnu) {
	actMnu.style.background = "#dadada";
	actMnu.style.visibility = "visible";
	visMnu = actMnu;
}

function hideMenu(actMnu) {
	actMnu.style.background = "";
	actMnu.style.visibility = "hidden";
	visMnu = "";
}

$(function(){
	$("#confirmCar").click(function(){
		$("#confirmCar").hide();
		flipMenu(actMnu);
	});
});

$(function(){
	if ($("#processing").hasClass("active")){
		$(".check_report").addClass("widthBtn");
	}
	if ($("#finished").hasClass("active")){
		$(".check_report").removeClass("widthBtn");
	}
})

