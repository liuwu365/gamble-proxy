/**
 * Created by liuwu on 2016/7/26 0026.
 * 加载下拉框
 */
var _LoadSelectBox = {
    /**
     * 加载文章类型下拉框(栏目列表)
     */
    getFolder: function () {
        $.ajax({
            type: "get",
            url: "/news/folder/findFolderAll/",
            dataType: "json",
            success: function (data) {
                if (data != null && data != '') {
                    var folderid = $("input[name='loadFolderid']").val();
                    for (var i in data) {
                        if (folderid == data[i].folderid) {
                            $(".folderidBox").append("<option selected=\"true\" value=\"" + data[i].folderid + "\">" + data[i].name + "</option>");
                        } else {
                            $(".folderidBox").append("<option value=\"" + data[i].folderid + "\">" + data[i].name + "</option>");
                        }
                    }
                }
            }
        });
    },
    /**
     * 加载父级栏目
     */
    getFatherFolder: function () {
        $.ajax({
            type: "get",
            url: "/news/folder/loadFatherFolder/",
            dataType: "json",
            success: function (data) {
                if (data != null && data != null) {
                    for (var i in data) {
                        $(".parentFolder").append("<option readonly=\"readonly\" value=\"" + data[i].folderid + "\">" + data[i].name + "</option>");
                    }
                }
            }
        });
    },
    /**
     * 加载菜单栏
     */
    getParendMenu: function () {
        $.ajax({
            type: "get",
            url: "/base/resources/findParentMenuAll?parentId=0",
            dataType: "json",
            success: function (res) {
                if (res != null && res.code == 200) {
                    var menuId = $("input[name='menuId']").val();
                    var data = res.t;
                    for (var i in data) {
                        if (menuId == data[i].id) {
                            $(".parentMenuBox").append("<option selected=\"true\" value=\"" + data[i].id + "\">" + data[i].name + "</option>");
                        } else {
                            $(".parentMenuBox").append("<option value=\"" + data[i].id + "\">" + data[i].name + "</option>");
                        }
                    }
                }
            }
        });
    }


};
