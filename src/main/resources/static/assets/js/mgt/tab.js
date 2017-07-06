function setTab(m, n) {
	//隐藏确定按钮
	if(n==0){
		$("#confirmCar").hide();
	}
				var tli = document.getElementById("menu" + m).getElementsByTagName("li");
				var mli = document.getElementById("main" + m).getElementsByTagName("ul");
				for (i = 0; i < tli.length; i++) {
					tli[i].className = i == n ? "hover" : "";
					mli[i].style.display = i == n ? "block" : "none";
				}
			}
var kaiguan;   //控制点击车型次数，只能点击一次
function serarchSersies(item){

	kaiguan = 1;
	$("#edit-carInfor").val($(item).text()+" ");

	$.ajax({
		type: 'POST',
		data: {id:$(item).prop("id")},
		url: "/order/serarchSersiesById.json",
		success: function(data){
			$("#sersies li").remove();
			$("#exhaustVolume li").remove();
			//$("#sersies").children("li").remove();
            for(var key in data.carSeriesMap)  {
                $("#sersies").append("<li id='"+key+"' onclick="+"serarchExhaustVolume(this)"+"><span>"+data.carSeriesMap[key]+"</span></li>");
            }
			//alert($(item).text() + $(item).prop("id"));
			setTab(0,1)
		},
		dataType: 'json'
	});
}

function serarchExhaustVolume(item){
	var carInfor = $("#edit-carInfor").val();
	$("#exhaustVolume li").remove();
	carInfor = carInfor.substr(0,carInfor.indexOf(" "));

	$("#edit-carInfor").val(carInfor + " " +$(item).text());
	$.ajax({
		type: 'POST',
		data: {id:$(item).prop("id")},
		url: "/order/serarchExhaustVolumeById.json",
		success: function(data){
			for(var i=0;i<data.exhaustVolumeList.length;i++){
				$("#exhaustVolume").append("<li id='"+data.exhaustVolumeList[i].id+"' onclick="+"finshed(this)"+"><span>"+data.exhaustVolumeList[i].years+"-"+data.exhaustVolumeList[i].exhaustVolume+"-"+data.exhaustVolumeList[i].transmission+"</span></li>");
			}
			setTab(0,2);
		},
		dataType: 'json'
	});
}

function finshed(item){
	if(kaiguan == 1){
	var carInfor = $("#edit-carInfor").val();
	var lid = $(item).prop("id");
    $("#lid").val(lid);   //设置lib传进后台查询服务项目用
	//alert($("#lid").val());
	$("#edit-carInfor").val(carInfor + ' ' +$(item).text());
	//$("#confirmCar").show();
		kaiguan = 0;
	}
	//设置cookie
	//$.cookie("carInfor",carInfor + ' ' +$(item).text());
	//$.cookie("lid",lid);
	setTab(0,0);
	//hidediv(4);
	//flipMenu(actMnu);
	$('#modal_carInfo').modal('hide')

}

