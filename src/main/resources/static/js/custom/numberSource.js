var url = {
    commonPre: "/numberSource",//通用前缀
    list: '/list/',//分页查询
    upload: '/add',//文件上传
    delete: '/delete',//文件删除
    likeSearchByName: '/search/name/',//根据名字模糊查询
    query: '/query',//查询单个
    updateInfo: '/update',//修改信息
};
var tempId;//用作临时保存的id
var numberSourceList = {
    //分页查询
    page: function (pageNo) {
        $.get(url.commonPre + url.list + pageNo, function (result) {
            if (common.isJson(result))
                common.showMessage(result.message)
            else
                $('#tableContent').html(result);
        });
    },



    //上传文件
    uploadFile: function () {
        var form = new FormData($('#numberSourceFileForm')[0]);
        $.ajax({
            url: url.commonPre + url.upload,
            type: "post",
            data: form,
            processData: false,
            contentType: false,
            success: function (result) {
                if (result.code == '0000') {
                    common.showMessage('成功');
                    $('#file-list').text('');
                    common.closeModal($('#numberSourceFileForm')[0], $('#addModal'));
                    numberSourceList.page(1);
                } else {
                    common.showErrorMessage(result);
                }
            }
        });
    },

    //批量删除
    batchDelete: function () {
        //获取被选中的复选框value数组
        var array = common.getCheckedArray();
        if(array == false){
            common.showMessage("请选择要删除的记录");
            return;
        }
        //确认框
        $('#confirmModal').modal({
            relatedTarget: this,
            onConfirm: function(options) {
                $.post(url.commonPre + url.delete,{ids:array},function (result) {
                    //执行处理方法,如果未执行,表示成功
                    if(!common.errorHandle(result)){
                        common.successHandle();
                        //取消全选
                        common.checkboxAll(false);

                        numberSourceList.page(1);
                    }
                });
            },
            // closeOnConfirm: false,
            onCancel: function() {
            }
        });
    },

    //根据名字模糊查询
    likeSearchByName : function() {
        $('#searchByUsernameInput').change(function () {
            var name = $(this).val();
            if(!name)
                return;
            $.get(url.commonPre  + url.likeSearchByName + name, function (result) {
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
            numberSourceList.page(1);
    },

    //弹出修改模态框
    openUpdateInfoModal: function () {
        var ids = common.getCheckedArray();
        if(ids == false){
            common.showMessage("请选择要修改的记录");
            return;
        }else if(ids.length > 1){
            common.showMessage("请选择要单条记录");
            //取消全选
            common.checkboxAll(false);
            return;
        }
        common.checkboxAll(false);
        $.post(url.commonPre + url.query,{id:ids[0]},function (result) {
            if(!common.errorHandle(result)){
                $('#updateInfoForm :input[name="id"]').val(result.data.id);
                $('#updateInfoForm :input[name="name"]').val(result.data.name);
                $('#updateInfoForm :input[name="remark"]').val(result.data.remark);
                $('#updateInfoModal').modal();
            }
        });
    },
    //修改信息
    updateInfo :function() {
        $.post(url.commonPre + url.updateInfo,$('#updateInfoForm').serialize(),function (result) {
            if(!common.errorHandle(result)){
                numberSourceList.page(1);
                common.successHandle();
                common.closeModal($('#updateInfoForm')[0], $('#updateInfoModal'));
            }
        } );
    },

};
$(function () {
    //分页查询
    numberSourceList.page(1);

    //文件上传
    $(function() {
        $('#numberSourceFile').on('change', function() {
            var fileNames = '';
            $.each(this.files, function() {
                fileNames += '<span class="am-badge">' + this.name + '</span> ';
            });
            $('#file-list').html(fileNames);
        });
    });
    //模糊查询
    numberSourceList.likeSearchByName();
});