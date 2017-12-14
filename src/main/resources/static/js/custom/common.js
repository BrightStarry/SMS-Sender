var common = {
    //填充消息并提示
    showMessage : function (message) {
        $('#message').text(message);
        $('#messageModal').modal();
    },
    //弹出选择消息

    showErrorMessage : function(result) {
        common.showMessage("异常:" + result.message);
    },
    //清空表单并关闭窗口
    closeModal : function ($form,$modal) {
        if($form)
            $form.reset();
        if($modal)
            $modal.modal('close');
    },
    //判断是否为json对象
    isJson : function(obj){
        var tmp = typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length;
        return tmp;
    },
    //取消全选或全选-传入true或false
    checkboxAll : function (a) {
        $("input[name='list']").prop("checked",a);
    },
    //获取选中的所有复选框的value
    getCheckedArray : function () {
        var data = new Array();
        var j = 0;
        $("input[name='list']").each(function (i,item) {
            if($(item).is(':checked'))
                data[j++] = parseInt($(item).attr('value'));
        });
        return data;
    },
    successHandle : function () {
            common.showMessage("成功")
    },
    errorHandle: function(result) {
        if(result.code != '0000'){
            common.showErrorMessage(result);
            return true;
        }
        return false;
    },
    //判断是否成功
    isSuccess : function(result) {
        return result.code == '0000';
    },
    //获取当前时间加减指定分钟后的时间
    getDateByMinute : function (minute) {
        var time = new Date();
        time = time.setMinutes(time.getMinutes() + minute, time.getSeconds(), 0);
        return time = new Date(time);
    }




};
