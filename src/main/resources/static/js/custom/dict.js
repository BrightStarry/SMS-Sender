var url = {
    commonPre: "/dict",//通用前缀
    list: '/list/',//分页查询
    add: '/add',//新增
    likeSearchByRemark: '/search/remark/',//根据备注模糊查询
    query: '/query',//查询单个
    updateInfo: '/update',//修改信息
};
var dictList = {

    //分页查询
    page: function (pageNo) {
        if (!pageNo)
            pageNo = 1;
        $.get(url.commonPre + url.list + pageNo, function (result) {
            if (common.isJson(result))
                common.showMessage(result.message)
            else
                $('#tableContent').html(result);
        });
    },



    //根据备注模糊查询
    likeSearchByRemark: function () {
        $('#searchByRemarkInput').change(function () {
            var name = $(this).val();
            if (!name)
                return;
            $.get(url.commonPre + url.likeSearchByRemark + name, function (result) {
                $('#tableContent').html(result);
            });
        });
    },
    //点击搜索框，清空内容
    searchUserInputClean: function (input) {
        //当前内容
        var tempStr = input.val();
        //清空内容
        input.val("");
        //如果之前是有内容的，就重新加载第一页
        if (tempStr)
            dictList.page(1);
    },

    //弹出修改模态框
    openUpdateInfoModal: function () {
        var ids = common.getCheckedArray();
        if (ids == false) {
            common.showMessage("请选择要修改的记录");
            return;
        } else if (ids.length > 1) {
            common.showMessage("请选择要单条记录");
            //取消全选
            common.checkboxAll(false);
            return;
        }
        common.checkboxAll(false);
        $.post(url.commonPre + url.query, {id: ids[0]}, function (result) {
            if (!common.errorHandle(result)) {
                $('#updateForm :input[name="id"]').val(result.data.id);
                $('#updateForm :input[name="remark"]').attr('placeholder',result.data.remark);
                $('#updateForm :input[name="value"]').val(result.data.value);
                $('#updateModal').modal();
            }

        });
    },
    //修改信息
    updateInfo: function () {
        $.post(url.commonPre + url.updateInfo, $('#updateForm').serialize(), function (result) {
            if (!common.errorHandle(result)) {
                dictList.page();
                common.successHandle();
                common.closeModal($('#updateForm')[0], $('#updateModal'));
            }
        });
    }

};
$(function () {
    dictList.page();
    dictList.likeSearchByRemark();


});