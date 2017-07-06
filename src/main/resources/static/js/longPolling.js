/**
 * @author Created by 王亚平 on 2017/6/26.
 * @description 消息格式 {"code":200,"errorMsg":"",
* "result":[{"msgType":2,"from":51,"to":56,"ts":1498529455562,"msgContent":"你有新的订单，快去处理..."}]}
 */

$(function () {

    function longPolling() {
        $.ajax({
            url: "/async/comet.json",
            success: function (data, textStatus) {
                if (textStatus === "success") { // 请求成功
                    // console.log("success"+ parse);
                    try {
                        var parse = JSON.parse(data);
                        if (parse.code === 200 && parse.errorMsg === '') {
                            console.log(parse.result[0]);
                            addMsg(parse.result[0].msgContent);
                        }
                    } catch (e){
                        //request is html view
                        // console.log("data is not json");
                        return;
                    }

                    longPolling();
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("[jqXHR.status: " + jqXHR.status + "[jqXHR.readyState: " + jqXHR.readyState
                    + "[textStatus: " + textStatus + ", error: " + errorThrown + " ]");
                if (textStatus === "timeout") { // 请求超时
                    longPolling();
                }
                if (textStatus === 'error' && jqXHR.status===0 && jqXHR.readyState ===0 ) {
                    // console.log("服务器异常");
                    //  longPolling(); // 递归调用
                }
                else {
                    // 其他错误，如网络错误等
                    longPolling();
                }
                // longPolling();
            }
        });

    }

    function addMsgCount() {
        var $admin = $("#admin-notify a span");
        var oldCount = $admin.text();
        $admin.text(parseInt(oldCount) + 1)
    }

    function addMsg(msg) {
        addMsgCount();
        $("#admin-notify ul").append($("<li></li>").append($("<a></a>").addClass("glyphicon").text(msg)));
    }

    function delMsg() {

    }

    longPolling();
});
