var pageCount = 1;//总页数
var regExp = /_ueditor_page_break_tag_/;//根据某处字符来分页
var saveContent;//用于保存分页数据
var content, ID;//保存全局ID

//contentHtml=需要分页的html
//page=显示分页样式
UeInitialize = function (showId, contentHtml) {
    ID = showId;
    content = $(contentHtml);

    if (content != null) {
        if (regExp.test(content.html())) {
            saveContent = content.html().split(regExp);
            pageCount = saveContent.length;
        } else {
            $("#" + ID).append(content);
            return;
        }
        window.UePageContent(1);
    } else {
        $("#" + ID).append("");
        return;
    }
};

//显示分页的内容并自动生成页数
UePageContent = function (pageIndex) {
    if (pageIndex >= 1 && pageIndex <= pageCount && saveContent != null && saveContent.length >= 0) {
        if ((parseInt(pageIndex) - 1) <= saveContent.length) {
            $("#" + ID).html(saveContent[parseInt(pageIndex) - 1]);
        }

        var innHtml = "<div class='pageContent'>页数：" + pageIndex + "/" + pageCount;
        innHtml += "<a target='_self' href='javascript:UePageContent(1)'>首页</a>";
        if (pageIndex > 1) {
            innHtml += "<a target='_self' href='javascript:UePageContent(" + (parseInt(pageIndex) - 1) + ")'>上一页</a>";
        }
        if (pageIndex < pageCount) {
            innHtml += "<a target='_self' href='javascript:UePageContent(" + (parseInt(pageIndex) + 1) + ")'>下一页</a>";
        }
        innHtml += "<a target='_self' href='javascript:UePageContent(" + pageCount + ")'>末页</a></div>";
        $("#" + ID).append(innHtml);
    }
}