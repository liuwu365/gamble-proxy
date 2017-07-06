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

    //var carInfor = $("#carInfor").val();
	kaiguan = 1;
	$("#carInfor").text($(item).attr("value")+" ");
	$("#imageSrc").val($(item).attr("name"));
	$.cookie("imageSrc",$(item).attr("name"));
	$.ajax({
		type: 'POST',
		data: {id:$(item).prop("id")},
		url: "/wx/order/serarchSersiesById.json",
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
	var carInfor = $("#carInfor").text();
	$("#exhaustVolume li").remove();
	carInfor = carInfor.substr(0,carInfor.indexOf(" "));
	$("#carInfor").text(carInfor + " " +$(item).text());
	$("#carInforInput1").val(carInfor + " " +$(item).text());
	$.ajax({
		type: 'POST',
		data: {id:$(item).prop("id")},
		url: "/wx/order/serarchExhaustVolumeById.json",
		success: function(data){
			for(var i=0;i<data.exhaustVolumeList.length;i++){
				$("#exhaustVolume").append("<li id='"+data.exhaustVolumeList[i].id+"' onclick="+"finshed(this)"+"><span>"+data.exhaustVolumeList[i].exhaustVolume+"-"+data.exhaustVolumeList[i].transmission+"-"+data.exhaustVolumeList[i].years+"</span></li>");
			}
			setTab(0,2);
		},
		dataType: 'json'
	});
}

function finshed(item){
	if(kaiguan == 1){
	var carInfor = $("#carInfor").text();
	var lid = $(item).prop("id");
    $("#lid").val(lid);   //设置lib传进后台查询服务项目用
	//alert($("#lid").val());
	$("#carInfor").text(carInfor + ' ' +$(item).text());
	$("#carInforInput").val(carInfor + ' ' +$(item).text());
	//$("#confirmCar").show();
		kaiguan = 0;
	}
	//设置cookie
	$.cookie("carInfor",carInfor + ' ' +$(item).text());
	$.cookie("lid",lid);
	setTab(0,0);
	hidediv(4);
	//flipMenu(actMnu);

}

//预约时间
$(function(){
	var morendate = $("#startDate").val();
	$("#presetTime").val(morendate + " 9:00:00");
});

//个人中心修改资料
$(function(){
	$("#ModifyData").click(function(){
		$('input').removeAttr("readonly");
		$('input').addClass("showBorder");
        $(this).hide();
        $("#personalConfirm").show();
		$("#personalCenter").show();
		$("#personalInformation").hide();
		//给选择车辆绑定单击事件
		$("#bindClick").attr("onclick", "showdiv(4)");
	});
});

//服务项目价格js
$(function(){
	$("ul ul li").click(function(){
		//获取配件价格
		var price = $(this).attr("name");
		var group = $(this).children("div").children("span").eq(3).attr("name");
		//品牌
		var accessoryBrandName = $(this).children("div").children("span").eq(1).text();
		//获取服务项目价格
		var projectAcount = $(this).parent("ul").children("div").children(".money").html();
        //控制div打钩变量
	    var a=0;

		if($(this).children("div").attr("class")=="off") {
			a=1;
			//实时更新应付金额
			var totallMoney = $("#payAmount").val();
			totallMoney = (totallMoney-0) - (projectAcount-0) + (price-0);
			$("#payAmount").val(totallMoney);
			$("#showProjectPayAmount").html(totallMoney>0?totallMoney:0);
			$("#showPayAmount").html(totallMoney>0?totallMoney:0);
			$("#settleAmount").val(parseFloat(totallMoney));

			//设置配件的id,name,price,custSeleFirst,genaralPrice
			var id = $(this).attr("id");
			var name = $(this).find("span").eq(4).text();
            var supplierCode=$(this).find("span").eq(5).text();
			$(this).parent("ul").find("input").eq(5).val(id);
			$(this).parent("ul").find("input").eq(6).val(name);
			$(this).parent("ul").find("input").eq(3).val(price);
			$(this).parent("ul").find("input").eq(8).val(accessoryBrandName);
			$(this).parent("ul").find("input").eq(10).val(group);
			$(this).parent("ul").find("input").eq(11).val(supplierCode);

			//设置服务项目的费用以及打钩样式
			$(this).parent().children("li").children("div").removeClass("on").addClass("off");
			$(this).children("div").removeClass("off").addClass("on");
			$(this).parent("ul").children("div").removeClass("off").addClass("on");
			$(this).parent("ul").children("div").children(".money").html((price - 0));
		}else{
			//设置样式
			$(this).children("div").attr("class","off");
			//$(this).children("div").addClass("off");
			//设置服务项目价格
			$(this).parent("ul").children("div").children(".money").html((projectAcount - 0) - (price - 0));
			//设置总金额
			var totallMoney = $("#payAmount").val();
			$("#payAmount").val(totallMoney - price);
			$("#showPayAmount").html((totallMoney - price)>0?(totallMoney - price):0);

			$("#settleAmount").val(parseFloat(totallMoney - price));
		}

		$(this).parent("ul").children("li").each(function(){
			if($(this).children("div").attr("class")=="on")
				a=1;
		});
		if (a==1){
			$(this).parent("ul").children("div").addClass("on");
			$(this).parent("ul").find("input").eq(0).val("1");         //设置 客户是否选择    1 选择
		}else{
			$(this).parent("ul").children("div").removeClass("on").addClass("off");
			$(this).parent("ul").find("input").eq(0).val("0");         //设置 客户是否选择    0 未选择
		}

		var couponPrice = $("#couponAmount").val();
		var settleAmount = $("#settleAmount").val();
        if (typeof(couponPrice) == "undefined"){
            couponPrice=0
        }
        if (typeof(settleAmount) == "undefined"){
            settleAmount=0
        }
        var final=parseFloat(settleAmount)+parseFloat(couponPrice);
		$("#settleAmount").val(parseFloat(final));
        $("#showProjectPayAmount").html(final);
		//显示客户选择服务项目名称
		/*if(a!=1){
			var childrenProjectName = $(this).parent().children("input").eq(2).val();
			var projectName = $("#projectName").html();
			if((','+projectName+',').indexOf(','+childrenProjectName+',')>0){
				projectName = (projectName+',').replace(','+ childrenProjectName +',', ",");
			}
			$("#projectName").html(projectName.substr(0, projectName.length-1));
		}else{
			var childrenProjectName = $(this).parent().children("input").eq(2).val();
			var projectName = $("#projectName").html();
			if((','+projectName+',').indexOf(','+childrenProjectName+',')>0);
			else
				$("#projectName").html(projectName+','+childrenProjectName);
		}*/
	});
});

//自有服务项目价格
$(function(){
	$("ul ul>div").click(function(){
		if($(this).attr("class")=="on" && $(this).attr("name") != "noClick"){
			//设置客户是否选择 custSeleFirst
			$(this).parent("ul").find("input").eq(0).val("0");
			//设置div同级的li class属性
			$(this).parent().children("li").children("div").removeClass("on").addClass("off");

			//设置div的class属性
			$(this).removeClass("on").addClass("off");

			//计算金额
			var totalMoney = $("#payAmount").val();
			var money = $(this).parent().children("input").eq(3).val();
			$("#payAmount").val(totalMoney-money);
			$("#showPayAmount").html((totalMoney-money)>0?(totalMoney-money):0);
			$("#showProjectPayAmount").html((totalMoney-money)>0?(totalMoney-money):0);
			$("#settleAmount").val(parseFloat(totalMoney-money));


			//设置康总服务价格
			if($(this).parent("ul").children("li").length>0){
				$(this).children("span.money").html(0);
			}
			//显示客户选择的服务项目名称
			/*var childrenProjectName = $(this).parent().children("input").eq(2).val();
			var projectName = $("#projectName").html();
			if((','+projectName+',').indexOf(','+childrenProjectName+',')>0){
				projectName = (projectName+',').replace(','+ childrenProjectName +',', ",");
				//alert(projectName);
			}
			$("#projectName").html(projectName.substr(0, projectName.length-1));*/
		}else if($(this).attr("class")=="off"){
			//设置客户是否选择 custSeleFirst
			$(this).parent("ul").find("input").eq(0).val("1");
			//设置div同级的li class属性
            var accId = $(this).parent().find("input").eq(5).val();
			$(this).parent("ul").children("li").children("div").each(function(){
				if($(this).parent("li").attr("id")==accId){
					$(this).removeClass("off").addClass("on");
				}
			});
			//设置div的class属性
			$(this).removeClass("off").addClass("on");

			//计算金额
			var totalMoney = $("#payAmount").val();
			var money = $(this).parent().children("input").eq(3).val();
			$("#payAmount").val((totalMoney-0)+(money-0));
			$("#showPayAmount").html(((totalMoney-0)+(money-0))>0?(totalMoney-0)+(money-0):0);
			$("#showProjectPayAmount").html((((totalMoney-0)+(money-0))>0?(totalMoney-0)+(money-0):0));
			$("#settleAmount").val(parseFloat((totalMoney-0) + (money-0)));

			//设置康总服务价格
			if($(this).parent("ul").children("li").length>0){
				$(this).children("span.money").html(money);
			}
			//显示客户选择的服务项目名称
			/*var childrenProjectName = $(this).parent().children("input").eq(2).val();
			var projectName = $("#projectName").html();
			$("#projectName").html(projectName+','+childrenProjectName);*/
		}else{
            return;
        }
		var couponPrice = $("#couponAmount").val();
		var settleAmount = $("#settleAmount").val();
        if (typeof(couponPrice) == "undefined"){
            couponPrice=0
        }
        if (typeof(settleAmount) == "undefined"){
            settleAmount=0
        }
		$("#settleAmount").val(parseFloat(settleAmount)+parseFloat(couponPrice));
        $("#showProjectPayAmount").html(parseFloat(settleAmount)+parseFloat(couponPrice));
	});
});

//加载页面后设置服务项目的价格
$(function(){
	var totallMoney = 0;
	//var projectName = '';
	var i =0;
	$("ul ul").each(function(){
		//设置服务项目的价格
      if($(this).children("li").length>0) {
		var money = $(this).children("li").eq(0).attr("name");
		  //品牌
		var accessoryBrandName = $(this).children("li").eq(0).children("div").children("span").eq(1).text();
		totallMoney += (money-0);
		$(this).children("div").children(".money").html(money);

		//设置配件的id,name,price,custSeleFirst
		var id = $(this).children("li").eq(0).attr("id");
		var name = $(this).children("li").eq(0).find("span").eq(4).text();
        var supplierCode=$(this).children("li").eq(0).find("span").eq(5).text();
		$(this).children("input").eq(5).val(id);
		$(this).children("input").eq(6).val(name);
		$(this).children("input").eq(0).val(1);
		$(this).children("input").eq(3).val(money);
		$(this).children("input").eq(8).val(accessoryBrandName);
		$(this).children("input").eq(11).val(supplierCode);
      }else {
         if($(this).children("div").attr("class")=="on"){
             var money = $(this).children("input").eq(3).val();
             totallMoney += (money-0);
         }
      }
	  //var childrenProjectName = $(this).children("input").eq(2).val();
		//projectName = projectName + ',' + childrenProjectName;

      $(this).children("input").each(function(){
		  var listRelMainProjectName = $(this).attr("name");
		  var childName = listRelMainProjectName.substr(listRelMainProjectName.indexOf("."));
		  //alert( 'listRelMainProject['+i+']'+childName);
		  $(this).attr("name", 'listRelMainProject['+i+']'+childName);
	  });
		i++;
	});
	$("#payAmount").val(totallMoney);
	$("#showPayAmount").html(totallMoney);
	$("#showProjectPayAmount").html(totallMoney);
	$("#settleAmount").val(parseFloat(totallMoney));
    //优惠券初始化
    var checked = $("input[name='service']:checked");
    var cusHaveCouId = checked.parent("div").next().children("input").eq(0).val();
    if(cusHaveCouId>0){
        var amount = checked.parent("div").next().children("p.amount").children("span").attr("name");
        var payAmount = $("#settleAmount").val();
        $("#cusHaveCouId").val(cusHaveCouId);
        $("#coupon").val(amount);
        $("#showCoupon").html('减 ' + amount + ' 元');
        $("#couponAmount").val(amount);
        payAmount -= amount;

        $("#payAmount").val(payAmount);
        $("#showPayAmount").html(payAmount>0?payAmount:0);
        $("#showProjectPayAmount").html(parseFloat(totallMoney));
        hidediv(5);
    }
	//$("#projectName").html(projectName.substr(1,projectName.length));
});

//显示联系方式
$(function(){
	$("#nameAndPhoneConf").click(function(){
		var ownerName = $("#ownerName").val();
		var phone = $("#phone").val();
		var address = $("#address").val();
        if(address.length >25){
		    alert("地址请输入25字以内");
		    return false;
		}
		if(ownerName != "" && phone != ""){
			$("#nameAndTel").html(ownerName + " " + phone);
		}else{
			$("#nameAndTel").html("");
		};
		if(address != "")
			$("#showAddress").html(address);
		else
		    $("#showAddress").html("");
		hidediv(2);
	});
});

//选择优惠券
$(function(){
	$("#confirmCoupon").click(function(){
		var checked = $("input[name='service']:checked");
		var cusHaveCouId = checked.parent("div").next().children("input").eq(0).val();
		if(cusHaveCouId>0){
		var amount = checked.parent("div").next().children("p.amount").children("span").attr("name");
		var payAmount = $("#settleAmount").val();
		$("#cusHaveCouId").val(cusHaveCouId);
		$("#coupon").val(amount);
		$("#showCoupon").html('减 ' + amount + ' 元');
		$("#couponAmount").val(amount);
        payAmount -= amount;

		$("#payAmount").val(payAmount);
		$("#showPayAmount").html(payAmount>0?payAmount:0);
//		$("#showProjectPayAmount").html(payAmount>0?payAmount:0);
		hidediv(5);
		}else{
			alert("请选择优惠券");
		}
		//alert(amount);
	});
	/*$("#show5").children("div").find("button").click(function(){
		var cusHaveCouId = $(this).parent("div").children("input").eq(0).val();
		var amount = $(this).parent("div").children("p.amount").attr("name");
		var payAmount = $("#settleAmount").val();
		$("#cusHaveCouId").val(cusHaveCouId);
		$("#coupon").val(amount);
		$("#showCoupon").html('减 ' + amount + ' 元');
		$("#couponAmount").val(amount);

		if((payAmount-0) < amount)
			payAmount=0;
		else
			payAmount -= amount;

		$("#payAmount").val(payAmount);
		$("#showPayAmount").html(payAmount);
		$("#showProjectPayAmount").html(payAmount);
		hidediv(5);

		var couponAmount = $("#coupon").val();
		if(couponAmount==0)
			couponPriceNum=0;
	});*/

	//不选择优惠券
	$("#bg5").click(function(){
		consCoupon();
	});
	$("#consCoupon").click(function(){
		consCoupon();
	});
});

function consCoupon(){
	var amount = $("#coupon").val();
	var settleAmount = $("#settleAmount").val();
	$("#cusHaveCouId").val(0);
	$("#showCoupon").html('请选择优惠券');
	$("#couponAmount").val(0);
	$("#payAmount").val(settleAmount);
	$("#showPayAmount").html(settleAmount>0?settleAmount:0);
//	$("#showProjectPayAmount").html(settleAmount>0?settleAmount:0);
	hidediv(5);
}




