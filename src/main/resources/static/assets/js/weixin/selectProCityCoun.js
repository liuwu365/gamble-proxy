/*$("#select_province").change(function(){
	selectCity("select_province","select_city");((
})
$("#select_city").change(function(){
	selectArea("select_city","select_area");
});
selectProvince("pid","cid","aid");*/

jQuery(function(){
	$("#pid").change(function(){
		var province = $("#pid").find("option:selected").text();
		$("#province").val(province);
		selectCity("pid","cid",2);
	});

	/*$("#cid").change(function(){
		var city = $("#cid").find("option:selected").text();
		$("#city").val(city);
		//alert(city);
		selectArea("cid","aid",2);
	});*/

	$("#aid").change(function(){
		var area = $("#aid").find("option:selected").text();
		var areaId = $("#aid").find("option:selected").val();
		$("#area").val(area);
		$("#areaId").val(areaId);
	});
})


function selectCity(pid,cid,type){
	var p=$("#"+pid).find("option:selected").val();
	$("#"+cid+" option:gt(0)").remove();
	$.ajax({
		url:"/wx/order/city.json",
		data:{
			id:p,
			type:type
		},
		dataType:"json",
		success:function(data) {
			var str="";

			var result = eval(data);
			//console.info(result);
			//alert(result.cityList.length);
			$.each(result.cityList,function (i,item){
				str+="<option value="+item.code+">"+item.name+"</option>";

			})
			$("#"+cid).append(str);
            $("#"+cid).find("option:first").next().attr("selected",true);
		}
	})
}

function selectArea(cid,aid,type){
	//option="";
	var p=$("#"+cid).find("option:selected").val();
	$.ajax({
		url:"/wx/order/area.json",
		data:{
			id:p,
			type:type
		},
		dataType:"json",
		success:function(data) {
			var str = "";
			var result = eval(data);
			$("#"+aid+" option:gt(0)").remove();
			$.each(result.area, function (i, item) {
				str += "<option name='area' value="+item.id+" text="+item.id+">" + item.name + "</option>";
			});
			$("#"+aid).append(str);
		}
	})
};
