/**
 * Created by liuwu on 2017/6/20 0026.
 * 后台公用js
 */
//显示左侧菜单
function showMenu(obj) {
    var html = $(obj).parent().siblings('div').html();
    $(".navbar .container-fluid ul li").removeClass("active");
    $("#" + obj.id).parent("li").addClass("active");
    $(".list-unstyled").children().remove();
    $(".list-unstyled").append(html);
}
//左侧活动选项栏目
function showSide(obj) {
    $(function () {
        // $(".side .list-unstyled li").removeClass("active");
        // $(".side .list-unstyled li:nth-child(" + i + ")").addClass("active");
        $(".side .list-unstyled li").each(function () {
            if ($(this).find("c").text() == obj) {
                $(this).addClass("active");
                return;
            }
        });
    });
}

/**
 * 1.列表中的多选复选框,可实现多个勾选
 * 2.行表表格隔行换色
 */
$(document).ready(function () {
    $('body').attr('id', 'yt');
    $('[data-toggle="tooltip"]').tooltip();
    //点击上传图片按钮
    $('.up_img_btn').click(function () {
        $(this).parent().find('[type="file"]').click();
    });
    $('.up_img [type="file"]').change(function () {
        alert($(this).val());
    });

    //导入layui框架
    layui.use(['layer', 'laydate', 'form'], function () {
        var layer = layui.layer
            , laydate = layui.laydate
            , form = layui.form();
    });
    //时间插件
    if ($('.calendar').length > 0) {
        laydate({
            elem: '#yt .calendar',
            istime: true,
            format: 'YYYY-MM-DD hh:mm:ss'
        });
    }
    //导航的hover效果、二级菜单等功能，需要依赖element模块(修改密码二级菜单)
    layui.use('element', function () {
        var element = layui.element();
        //监听导航点击
        element.on('nav(demo)', function (elem) {
            layer.msg(elem.text());
        });
    });
    //修改密码
    $("#pwForm").validate({
        debug: true,
        checkStart: true,
        errorElement: 'span',
        errorClass: '_error',
        focusInvalid: false,
        ignore: "",
        rules: {
            "password": { required: true},
            "nPassword": { required: true},
            "nPassword2": { required: true, equalTo: "#nPassword"}
        },
        messages: {
            "password": { required: "请输入原密码"},
            "nPassword": { required: "请输入新密码"},
            "nPassword2": { required: "请输入确认密码", equalTo: "密码不一致"}
        },
        submitHandler: function (form) {
            $(form).ajaxSubmit(function (res) {
                if (res.code == 200) {
                    layer.msg(res.msg, {time: 3000}, function () {
                        window.location.reload();
                    });
                }
            });
        },
        highlight: function (e) {
            $(e).closest('.control-group').removeClass('info').addClass('error');
        }
    });

    //全选
    $("#select_all").click(function () {
        $("input:checkbox").prop("checked", $(this).prop("checked"));
    });
    //关联全选
    $("input:checkbox").not("#select_all").click(function () {
        if ($("input:checkbox:not(:checked)").not("#select_all").length == 0) $("#select_all").prop("checked", true);
        else $("#select_all").prop("checked", false);
    });

    //分页插件
    var options = {
        bootstrapMajorVersion: 3, //版本
        currentPage: $("[name='page']").val(),
        totalPages: $("[name='totalPage']").val(),
        alignment: "left",
        itemTexts: function (type, page, current) {
            switch (type) {
                case "first":
                    return "首页";
                case "prev":
                    return "上一页";
                case "next":
                    return "下一页";
                case "last":
                    return "末页";
                case "page":
                    return page;
            }
        },
        pageUrl: function (type, page, current) {
            if (page == current) {
                return "javascript:void(0)";
            } else {
                return "javascript:paging('" + page + "');";
            }
        }
    };
    $('#pageLimit').bootstrapPaginator(options);

    //下拉框插件(部门分组)
    $.cxSelect($('#element_id'), {
        selects: ['dept', 'group'],
        emptyStyle: 'none',
        jsonName: 'deptName',
        jsonValue: 'id',
        jsonSub: 't'
    });

    //悬浮弹框提示
    $(".alertTip").popover({
        trigger:'hover',
        placement : 'right',
        title:'<div style="text-align:left; color:gray; font-size:12px;">提示</div>',
        html: 'true',
        animation: false
    });


});

//分页搜索查询
function paging(page) {
    $("#page").val(page);
    $("#searchForm").submit();
}
//解决筛选条件时,分页从第二页起搜索无内容的错误bug;
function resetPage() {
    $("#page").val(1);
}
//取消按钮到上个页面
function cancleGoBack() {
    history.go(-1);
    location.reload();
}

/**
 * 项目公用
 */
var _CommonJS = {
    /**
     * 特殊字符过滤[把特殊字符踢掉]
     * @param {Object} s
     */
    checkCharFilter: function (s) {
        //var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？+%]")
        var pattern = new RegExp("[`~!@#$^*()=|{}:;,\\[\\]<>/?~！@#￥……*（）——|{}【】‘；：”“。，、？+%]");
        var rs = "";
        for (var i = 0; i < s.length; i++) {
            rs = rs + s.substr(i, 1).replace(pattern, '');
        }
        return rs;
    },
    /**
     * 把英文单词的首字母大写
     */
    replaceFirstUper: function (str) {
        if (str.length <= 0 || str == "") {
            return;
        }
        str = str.toLowerCase();
        return str.replace(/\b(\w)|\s(\w)/g, function (m) {
            return m.toUpperCase();
        });
    },
    /**
     * 转换成数组，去掉重复，再组合好。
     * 需 jquery支持
     */
    removeRepeat: function (str) {
        var strArr = str.split(",");
        strArr.sort(); //排序
        var result = $.unique(strArr);
        return result;
    },
    /**
     * 如果字符串是null返回空字符,否则返回空str
     */
    checkNull: function (str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    },
    /**
     * 验证输入内容是否为数字
     */
    isDigit: function (value) {
        console.log(value);
        var patrn = /^[0-9]*$/;
        if (!patrn.test(value) || value == "" || value == null || value == "null") {
            return false;
        } else {
            return true;
        }
    },
    /**
     * 验证输入内容是否为双精度
     */
    isDouble: function (value) {
        if (value.length != 0) {
            var reg = /^[-\+]?\d+(\.\d+)?$/;
            if (reg.test(value)) {
                return true;
            } else {
                return false;
            }
        }
    },
    /**
     * 比较时间大小
     * 0:data1>data2
     * 1:data1<data2
     * 2:data1=data2
     */
    dateCompare: function (data1, data2) {
        var d1 = new Date(data1.replace(/\-/g, '/'));
        var d2 = new Date(data2.replace(/\-/g, '/'));
        if (d1 > d2) {
            return 0;
        } else if (d1 < d2) {
            return 1;
        } else if (d1 - d2 == 0) {
            return 2;
        }
    },
    /**
     * 获取当前时间,格式[2015-08-18 12:00:00]
     */
    currentTime: function () {
        var d = new Date();
        return d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + " " + d.getHours() + ':' + d.getMinutes() + ":" + d.getSeconds();
    },
    /**
     * 提取字符串中的所有数字
     */
    getNum: function (text) {
        var spaceInputStr = text.replace(/\s+/g, ""); //先去掉空格
        var filterStr = spaceInputStr.replace(/[^0-9]/ig, " ");  //提取数字,并以空格分割
        var value = filterStr.replace(/\s+/g, ' '); //剔除多余的空格[多个空格转为1个空格]
        return value;
    },
    /**
     * 分割标题得到标签,小于1个字符的将被过滤掉
     */
    splitStrTolabel: function (str) {
        if (str == null || str == "") {
            return "";
        } else {
            var spaceStr = str.replace(/\s+/g, ' '); //剔除多余的空格[多个空格转为1个空格]
            var arrayStr = spaceStr.split(" ");
            var newStr = "";
            for (var i = 0; i < arrayStr.length; i++) {
                if (arrayStr[i].length < 2) {
                    continue;
                }
                newStr.append(arrayStr[i])
            }
            return newStr;
        }
    },
    /**
     * 验证网址
     */
    checkUrl: function (str) {
        if (str == null || str == "") {
            return "";
        } else {
            var RegUrl = new RegExp();
            RegUrl.compile("^[A-Za-z]+://[A-Za-z0-9-_]+\\.[A-Za-z0-9-_%&\?\/.=]+$");
            if (!RegUrl.test(str)) {
                alert("网址格式错误");
            }
        }
    },
    /**
     * 验证邮箱
     */
    checkEmail: function (str) {
        var temp = document.getElementById("text1");
        var myreg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
        if (!myreg.test(temp.value)) {
            alert('提示\n\n请输入有效的E_mail！');
            myreg.focus();
            return false;
        }
    },
    /*
     * 鼠标悬浮显示大图
     */
    showLargeImg: function () {
        var x = 3;
        var y = 5; //设置提示框相对于偏移位置，避免遮挡鼠标
        $(".prompt").hover(function (e) {  //鼠标移上事件
            this.myTitle = this.title; //把title的赋给自定义属性 myTilte ，屏蔽自带提示
            this.title = "";
            var tooltipHtml = "<div id='tooltip'><img src='" + this.myTitle + "' /></div>"; //创建提示框
            $("body").append(tooltipHtml); //添加到页面中
            $("#tooltip").css({
                "top": (e.pageY + y) + "px",
                "left": (e.pageX + x) + "px"
            }).show("fast"); //设置提示框的坐标，并表现
        }, function () {  //鼠标移出事件
            this.title = this.myTitle;  //重新设置title
            $("#tooltip").remove();  //移除弹出框
        }).mousemove(function (e) {   //跟随鼠标挪动事件
            $("#tooltip").css({
                "top": (e.pageY + y) + "px",
                "left": (e.pageX + x) + "px"
            });
        });
    },
    /**
     * 鼠标悬浮显示信息
     */
    showHoverInfo: function () {
        var x = 3;
        var y = 5; //设置提示框相对于偏移位置，避免遮挡鼠标
        $(".prompt").hover(function (e) {  //鼠标移上事件
            alert(11);
            this.myTitle = this.title; //把title的赋给自定义属性 myTilte ，屏蔽自带提示
            this.title = "";
            var tooltipHtml = "<div id='tooltip2'>" + this.myTitle + "</div>"; //创建提示框
            console.log(tooltipHtml);
            $("body").append(tooltipHtml); //添加到页面中
            $("#tooltip2").css({
                "top": (e.pageY + y) + "px",
                "left": (e.pageX + x) + "px"
            }).show("fast"); //设置提示框的坐标，并表现
        }, function () {  //鼠标移出事件
            this.title = this.myTitle;  //重新设置title
            $("#tooltip2").remove();  //移除弹出框
        }).mousemove(function (e) {   //跟随鼠标挪动事件
            $("#tooltip2").css({
                "top": (e.pageY + y) + "px",
                "left": (e.pageX + x) + "px"
            });
        });
    },
    /**
     * 显示Div
     */
    showDiv: function (divID) {
        if (document.getElementById(divID).style.display != "block") {
            document.getElementById(divID).style.display = "";
        }
        else {
            document.getElementById(divID).style.display = "none";
        }
    },
    /**
     * 隐藏Div
     */
    closeDiv: function (divID) {
        document.getElementById(divID).style.display = "none";
    },
    /**
     * 图片上传
     * id: file标签的id
     * url: control控制类方法
     */
    upload: function (id, url) {
        $.ajaxFileUpload({
            url: url,
            secureuri: false,
            type: "post",
            fileElementId: id,//file标签的id
            dataType: 'json',//返回数据的类型
            success: function (data) {
                //把图片替换
                if (data != undefined || data != '') {
                    if (data == '1') {
                        layer.msg("文件格式不对：", {time: 6000});
                        return;
                    } else if (data == '2') {
                        layer.msg("文件是空的：", {time: 6000});
                        return;
                    } else if (data == '3') {
                        layer.msg("文件大于2MB：", {time: 6000});
                        return;
                    }
                    var news_prefix = $("#news_prefix").val();
                    $("#index_pic").attr("src", news_prefix + data + "?t=" + Math.random());
                    $("#index_image").val(data);
                } else {
                    layer.msg("上传处理失败：", {time: 6000});
                }
            },
            error: function (data, status, e) {
                console.log("data:" + JSON.stringify(data));
                console.log("status:" + status);
                console.log("e:" + e);
                layer.msg("上传失败")
            }
        });
    },
    /**
     * 批量上传图片
     */
    uploadBatch: function (id, url) {
        $.ajaxFileUpload({
            url: url,
            secureuri: false,
            type: "post",
            fileElementId: id,//file标签的id
            dataType: 'json',//返回数据的类型
            success: function (data) {
                layer.msg("上传中...");
                //把图片替换
                if (data != undefined || data != '') {
                    if (data == '1') {
                        layer.msg("文件格式不对：", {time: 6000});
                        return;
                    } else if (data == '2') {
                        layer.msg("文件是空的：", {time: 6000});
                        return;
                    } else if (data == '3') {
                        layer.msg("文件大于2MB：", {time: 6000});
                        return;
                    }
                    var news_prefix = $("#news_prefix").val();
                    $("#index_pic").attr("src", news_prefix + data + "?t=" + Math.random());
                    $("#index_image").val(data);
                    layer.msg("上传成功：", {time: 2000});
                } else {
                    layer.msg("上传处理失败：", {time: 6000});
                }
            },
            error: function (data, status, e) {
                console.log("data:" + JSON.stringify(data));
                console.log("status:" + status);
                console.log("e:" + e);
                layer.msg("上传失败")
            }
        });
    },
    /**
     * 获取选中的复选框(根据name)
     */
    getChks: function (inputName) {
        var chk_value = "";
        $('input[name="' + inputName + '"]:checked').each(function () {
            if (chk_value == "") {
                chk_value = $(this).val();
            } else {
                chk_value += "," + $(this).val();
            }
        });
        return chk_value;
    },
    getChkTexts: function (inputName) {
        var chk_value = "";
        $('input[name="' + inputName + '"]:checked').each(function () {
            if (chk_value == "") {
                chk_value = $(this).data().title;
            } else {
                chk_value += "," + $(this).data().title;
            }
        });
        return chk_value;
    }
}

