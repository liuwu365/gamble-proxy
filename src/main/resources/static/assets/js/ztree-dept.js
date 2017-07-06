var treeObj=null;
function addHoverDom(treeId, treeNode) {
    var sObj = $("#" + treeNode.tId + "_span");
    if(!treeNode.getParentNode()){
        sObj.siblings('.edit,.remove').remove();
    }
    if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0||!treeNode.isParent) return;
    var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
        + "' title='添加子节点' onfocus='this.blur();'></span>";
    sObj.after(addStr);
    var btn = $("#addBtn_"+treeNode.tId);
    if (btn) btn.bind("click", function(){
        var newNode=treeObj.addNodes(treeNode, {id:0, pId:treeNode.id, name:"新部门"})[0];
        treeObj.editName(newNode);
        return false;
    });
};
function removeHoverDom(treeId, treeNode) {
    $("#addBtn_"+treeNode.tId).unbind().remove();
};
