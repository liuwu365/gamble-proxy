$(function(){
	recommendService();
	//页面加载计算应付，合计价格
	countTotalPrice();
	$("div.check").click(function(){
		var hc = $(this).hasClass("on");
		if(hc){
			$(this).removeClass("on").addClass("off");
		    //计算价格
			var price = $(this).children("span").eq(2).attr("value");
			var a = 0;
			countPrice(price, a);
		}
		else{
			$(this).removeClass("off").addClass("on");
		    //计算价格
		    var price = $(this).children("span").eq(2).attr("value");
			var a =1;
			countPrice(price, a);
		}
		recommendService();
	});
});

//设置默认推荐项目的id 拼接成字符串
function recommendService(){
	var str = '';
	$("div.on").each(function(){
		var childrenStr = $(this).parent("dd").attr("id");
		if(str=='')
			str = childrenStr.substring(3);
		else str = str + ',' +childrenStr.substring(3);
	});
	$("#checkResultItemId").val(str);
}

//计算价格
function countPrice(price, a){
	var orderTotalAmount = $("#orderTotalAmount").val();
	var payAmount = $("#payAmount").val();
	if(a==0){
		orderTotalAmount = orderTotalAmount - price;
		payAmount = payAmount - price;
	}else {
		orderTotalAmount = (orderTotalAmount-0) + (price-0);
		payAmount = (payAmount-0) + (price-0);
	}
	//设置input 框 价格
	$("#orderTotalAmount").val(orderTotalAmount);
	$("#payAmount").val(payAmount);
	//设置显示价格
	$(".order-total").children("span:first").html(orderTotalAmount +' 元');
	$(".amount-payable").children("span:first").html(payAmount +' 元');
}

function countTotalPrice(){
	var orderTotalAmount = $("#orderTotalAmount").val();
	var payAmount = $("#payAmount").val();
	$("div.on").each(function(){
		var childPrice = $(this).children().eq(2).attr("value");
		orderTotalAmount = (orderTotalAmount - 0) + (childPrice - 0);
		payAmount = (payAmount - 0) + (childPrice - 0);
	});
	//设置input 框 价格
	$("#orderTotalAmount").val(orderTotalAmount);
	$("#payAmount").val(payAmount);
	//设置显示价格
	$(".order-total").children("span:first").html(orderTotalAmount +' 元');
	$(".amount-payable").children("span:first").html(payAmount +' 元');
}