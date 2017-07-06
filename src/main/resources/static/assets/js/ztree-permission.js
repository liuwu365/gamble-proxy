var treeObj=null,tree2Obj=null,data=null;
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
        showInsertModal(treeNode);
        return false;
    });
};
function removeHoverDom(treeId, treeNode) {
    $("#addBtn_"+treeNode.tId).unbind().remove();
};
function showInsertModal(treeNode){
    $('#modal_permission')
        .find('input[type!=submit]').val('').end()
        .find('[name=pid]').val(treeNode.id).next().val(treeNode.name).end().end()
        .modal('show');
    $('#hidden-tId').val(treeNode.tId);
    $('#modal-title').html('新增资源');
    $("#form_permission").valid();
}
function showUpdateModal(treeNode){
    $('#modal_permission')
        .find('[name=id]').val(treeNode.id).end()
        .find('[name=name]').val(treeNode.name).end()
        .find('[name=url]').val(treeNode.diyUrl).end()
        .find('[name=pid]').val(treeNode.pId).next().val(treeNode.getParentNode().name).end().end()
        .modal('show');
    $('#hidden-tId').val(treeNode.tId);
    $('#modal-title').html('编辑资源');
    $("#form_permission").valid();
}