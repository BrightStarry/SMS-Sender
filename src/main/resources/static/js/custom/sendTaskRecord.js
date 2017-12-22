var url = {
    commonPre: "/sendTaskRecord",//通用前缀
    list: '/list/',//分页查询
    add: '/add',//新增
    delete: '/delete',//文件删除
    likeSearchByName: '/search/name/',//根据名字模糊查询
    query: '/query',//查询单个
    updateInfo: '/update',//修改信息,
    channelAll: '/channel/all',//所有通道
    numberGroupAll: '/numberGroup/list/all-group',//所有号码组
    smsContentAll: '/smsContent/list/all',//所有话术
    stop: '/stop',//中断
    pause: '/pauseTask',//暂停
    rerun: '/rerunTask',//恢复启动
    warn: '/warn',//发送任务预警
};
var sendTaskRecordList = {


    /**
     * 初始化方法
     */
    init : function() {
        sendTaskRecordList.page();
        sendTaskRecordList.likeSearchByName();
        sendTaskRecordList.channelSelect();
        sendTaskRecordList.numberGroupSelect();
        sendTaskRecordList.smsContentSelect();

        //选中分段后,限制只能选择到小时
        $('#addForm :input[name="isShard"]').change(function () {
            var time = common.getDateByMinute(-3);
            if($(this).prop('checked')){
                $('#startTime').datetimepicker('remove');
                $('#endTime').datetimepicker('remove');
                $('#startTime').datetimepicker({language: 'zh-CN', startDate: time, autoclose: true, minView:1,format:"yyyy-mm-dd hh:00:00"});
                $('#endTime').datetimepicker({language: 'zh-CN', startDate: time, autoclose: true, minView:1,format:"yyyy-mm-dd hh:00:00"});
            }
            else{
                $('#startTime').datetimepicker('remove');
                $('#endTime').datetimepicker('remove');
                $('#startTime').datetimepicker({language: 'zh-CN', startDate: time, autoclose: true, minuteStep: 3});
                $('#endTime').datetimepicker({language: 'zh-CN', startDate: time, autoclose: true, minuteStep: 3});
            }
        });
        //选中分段后,限制只能选择到小时
        $('#updateForm :input[name="isShard"]').change(function () {
            var time = common.getDateByMinute(-3);
            if($(this).prop('checked')){
                $('#updateStartTime').datetimepicker('remove');
                $('#updateEndTime').datetimepicker('remove');
                $('#updateStartTime').datetimepicker({language: 'zh-CN', startDate: time, autoclose: true, minView:1,format:"yyyy-mm-dd hh:00:00"});
                $('#updateEndTime').datetimepicker({language: 'zh-CN', startDate: time, autoclose: true, minView:1,format:"yyyy-mm-dd hh:00:00"});
            }
            else{
                $('#updateStartTime').datetimepicker('remove');
                $('#updateEndTime').datetimepicker('remove');
                $('#updateStartTime').datetimepicker({language: 'zh-CN', startDate: time, autoclose: true, minuteStep: 3});
                $('#updateEndTime').datetimepicker({language: 'zh-CN', startDate: time, autoclose: true, minuteStep: 3});
            }
        });
        //填写完结束时间

        $('#updateEndTime').change(function () {
            sendTaskRecordList.warn($('#startTime').attr('data-value'),$('#endTime').attr('data-value'));
        });
    },
    //填写完时间后,执行的预警操作
    warn : function (x,y) {
        if(x && y){
            $.post(url.commonPre + url.warn,{startTime:Number(x),endTime:Number(y)},function(result){
                if (common.isSuccess(result) && result.data){
                    $('#addWarn').show();
                    $('#updateWarn').show();
                }else
                {
                    $('#addWarn').hide();
                    $('#updateWarn').hide();
                }
            });
        }
    },



    //暂停
    pause : function() {
        var ids = common.getCheckedArray();
        if (ids == false) {
            common.showMessage("请选择记录");
            return;
        } else if (ids.length > 1) {
            common.showMessage("请选择单条记录");
            //取消全选
            common.checkboxAll(false);
            return;
        }
        common.checkboxAll(false);

        $('#pauseModal').modal({
            relatedTarget: this,
            onConfirm: function(e) {
                $.post(url.commonPre + url.pause,{id:ids[0],time:e.data},function(result){
                    //执行处理方法,如果未执行,表示成功
                    if (!common.errorHandle(result)) {
                        sendTaskRecordList.page();
                        common.successHandle();
                        //取消全选
                        common.checkboxAll(false);
                    }
                });
            },
            onCancel: function(e) {
            }
        });
    },



    //恢复启动
    rerun : function() {
        var ids = common.getCheckedArray();
        if (ids == false) {
            common.showMessage("请选择记录");
            return;
        } else if (ids.length > 1) {
            common.showMessage("请选择单条记录");
            //取消全选
            common.checkboxAll(false);
            return;
        }
        common.checkboxAll(false);

        //确认框
        $('#confirmModal').modal({
            relatedTarget: this,
            onConfirm: function (options) {
                $.post(url.commonPre + url.rerun,{id:ids[0]},function (result) {
                    //执行处理方法,如果未执行,表示成功
                    if (!common.errorHandle(result)) {
                        sendTaskRecordList.page();
                        common.successHandle();
                        //取消全选
                        common.checkboxAll(false);
                    }
                });

            },
            // closeOnConfirm: false,
            onCancel: function () {
            }
        });
    },


    //停止
    stop : function() {
        var ids = common.getCheckedArray();
        if (ids == false) {
            common.showMessage("请选择记录");
            return;
        } else if (ids.length > 1) {
            common.showMessage("请选择单条记录");
            //取消全选
            common.checkboxAll(false);
            return;
        }
        common.checkboxAll(false);

        //确认框
        $('#confirmModal').modal({
            relatedTarget: this,
            onConfirm: function (options) {
                $.post(url.commonPre + url.stop,{id:ids[0]},function (result) {
                    //执行处理方法,如果未执行,表示成功
                    if (!common.errorHandle(result)) {
                        sendTaskRecordList.page();
                        common.successHandle();
                        //取消全选
                        common.checkboxAll(false);
                    }
                });

            },
            // closeOnConfirm: false,
            onCancel: function () {
            }
        });
    },


    //下拉选择-通道
    channelSelect: function () {
        var $select = $('#channelSelect');
        //打开新增模态框时,查询所有号码组类别
        $('#openAddModalButton').one('click', function () {
            $.post(url.commonPre + url.channelAll, null, function (result) {
                if (!common.errorHandle(result)) {
                    var operators = '<option selected value=""></option>';
                    var $list = $(result.data);
                    $list.each(function (i, item) {
                        var tmp = '<option value="' + item.id + '" id="' + item.id + '" name="' + item.name + '">' + item.name + '</option>'
                        operators += tmp
                    });
                    //填充
                    $select.html(operators);
                } else {
                    common.showMessage("加载通道失败,请刷新页面重试");
                }
            });

        });
    },

    //下拉选择-号码组
    numberGroupSelect: function () {
        var $select = $('#numberGroupSelect');
        //打开新增模态框时,查询所有号码组类别
        $('#openAddModalButton').one('click', function () {
            $.post(url.numberGroupAll, null, function (result) {
                if (!common.errorHandle(result)) {
                    var operators = '<option selected value=""></option>';
                    $.each(result.data, function (key, value) {
                        operators += '<optgroup label="' + key + '">';
                        $(value).each(function (i, item) {
                            operators += '<option value="' + item.id + '" id="' + item.id + '" name="' + item.name + '">' + item.name + '</option>'
                        });
                        operators += '</optgroup>';
                    });
                    //填充
                    $select.html(operators);
                } else {
                    common.showMessage("加载号码组失败,请刷新页面重试");
                }
            });
        });
    },

    //下拉选择-话术
    smsContentSelect: function () {
        var $select = $('#smsContentSelect');
        //打开新增模态框时,查询所有号码组类别
        $('#openAddModalButton').one('click', function () {
            $.post(url.smsContentAll, null, function (result) {
                if (!common.errorHandle(result)) {
                    var operators = '<option selected value=""></option>';
                    var $list = $(result.data);
                    $list.each(function (i, item) {

                        var tmp = '<option value="' + item.id + '" id="' + item.id + '" name="' + item.name + '">' + item.name + '</option>'
                        operators += tmp
                    });
                    //填充
                    $select.html(operators);
                } else {
                    common.showMessage("加载话术失败,请刷新页面重试");
                }
            });

        });
    },

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

    //弹出新增模态框
    openAddModal: function () {
        common.closeModal($('#addForm')[0], null);
        var time = common.getDateByMinute(-3);
        $('#startTime').datetimepicker({language: 'zh-CN', startDate: time, autoclose: true, minuteStep: 3})
            .on('changeDate', function(ev){
                $('#startTime').attr('data-value',ev.date.valueOf());
                sendTaskRecordList.warn($('#startTime').attr('data-value'),$('#endTime').attr('data-value'));
            });
        $('#endTime').datetimepicker({language: 'zh-CN', startDate: time, autoclose: true, minuteStep: 3})
            .on('changeDate', function(ev){
                $('#endTime').attr('data-value',ev.date.valueOf());
                sendTaskRecordList.warn($('#startTime').attr('data-value'),$('#endTime').attr('data-value'));
            });
        $('#addWarn').hide();
        $('#addModal').modal();
    }
    ,
    //新增
    add: function () {
        $.post(url.commonPre + url.add, $('#addForm').serialize(), function (result) {
            if (!common.errorHandle(result)) {
                sendTaskRecordList.page();
                common.successHandle();
                common.closeModal($('#addForm')[0], $('#addModal'));
            }
            //让下拉框复位
            $('#channelSelect').selected('destroy');
            $('#channelSelect').selected('enable');
            $('#numberGroupSelect').selected('destroy');
            $('#numberGroupSelect').selected('enable');
            $('#smsContentSelect').selected('destroy');
            $('#smsContentSelect').selected('enable');
        });

    },

    //批量删除
    batchDelete: function () {
        //获取被选中的复选框value数组
        var array = common.getCheckedArray();
        if (array == false) {
            common.showMessage("请选择要删除的记录");
            return;
        }
        //确认框
        $('#confirmModal').modal({
            relatedTarget: this,
            onConfirm: function (options) {
                $.post(url.commonPre + url.delete, {ids: array}, function (result) {
                    //执行处理方法,如果未执行,表示成功
                    if (!common.errorHandle(result)) {
                        sendTaskRecordList.page();
                        common.successHandle();
                        //取消全选
                        common.checkboxAll(false);
                    }
                });
            },
            // closeOnConfirm: false,
            onCancel: function () {
            }
        });
    },

    //根据名字模糊查询
    likeSearchByName: function () {
        $('#searchByUsernameInput').change(function () {
            var name = $(this).val();
            if (!name)
                return;
            $.get(url.commonPre + url.likeSearchByName + name, function (result) {
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
            sendTaskRecordList.page(1);
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

        $.post(url.commonPre + url.query, {id: ids[0]}, function (result1) {
            if (!common.errorHandle(result1)) {
                $('#updateWarn').hide();
                $('#updateForm :input[name="id"]').val(result1.data.id);
                $('#updateForm :input[name="name"]').val(result1.data.name);
                $('#updateForm :input[name="remark"]').val(result1.data.remark);
                $('#updateForm :input[name="threadCount"]').val(result1.data.threadCount);
                if(result1.data.isShard == 1)
                    $('#updateForm :input[name="isShard"]').attr('checked','true');

                $('#updateStartTime').datetimepicker({language: 'zh-CN',initialDate:new Date(result1.data.expectEndTime), autoclose: true, minuteStep: 3});
                $('#updateEndTime').datetimepicker({language: 'zh-CN',initialDate:new Date(result1.data.expectEndTime), autoclose: true, minuteStep: 3});
                $('#updateStartTime').datetimepicker('update', new Date(result1.data.expectStartTime));
                $('#updateEndTime').datetimepicker('update', new Date(result1.data.expectEndTime));



                //下拉框
                var $select1 = $('#updateChannelSelect');
                $.post(url.commonPre + url.channelAll, null, function (result) {
                    if (!common.errorHandle(result)) {
                        var operators = '<option selected value=""></option>';
                        var $list = $(result.data);
                        $list.each(function (i, item) {
                            var tmp2 = "";
                            if(item.id == result1.data.channelId)
                                tmp2 = "selected";
                            var tmp = '<option value="' + item.id + '" id="' + item.id + '" '+ tmp2 +' name="' + item.name + '">' + item.name + '</option>'
                            operators += tmp
                        });
                        //填充
                        $select1.html(operators);
                    } else {
                        common.showMessage("加载通道失败,请刷新页面重试");
                    }
                });

                var $select2 = $('#updateNumberGroupSelect');
                //打开新增模态框时,查询所有号码组类别
                $.post(url.numberGroupAll, null, function (result) {
                    if (!common.errorHandle(result)) {
                        var operators = '<option selected value=""></option>';
                        $.each(result.data, function (key, value) {
                            operators += '<optgroup label="' + key + '">';

                            $(value).each(function (i, item) {
                                var tmp2 = "";
                                if(item.id == result1.data.numberGroupId)
                                    tmp2 = "selected";
                                operators += '<option value="' + item.id + '" id="' + item.id + '" '+ tmp2 +' name="' + item.name + '">' + item.name + '</option>'
                            });
                            operators += '</optgroup>';
                        });
                        //填充
                        $select2.html(operators);
                    } else {
                        common.showMessage("加载号码组失败,请刷新页面重试");
                    }
                });

                var $select3 = $('#updateSmsContentSelect');
                //打开新增模态框时,查询所有号码组类别
                $.post(url.smsContentAll, null, function (result) {
                    if (!common.errorHandle(result)) {
                        var operators = '<option selected value=""></option>';
                        var $list = $(result.data);
                        $list.each(function (i, item) {
                            var tmp2 = "";
                            if(item.id == result1.data.smsContentId)
                                tmp2 = "selected";
                            var tmp = '<option value="' + item.id + '" id="' + item.id + '" '+ tmp2 +' name="' + item.name + '">' + item.name + '</option>'
                            operators += tmp
                        });
                        //填充
                        $select3.html(operators);
                    } else {
                        common.showMessage("加载话术失败,请刷新页面重试");
                    }
                });

                $('#updateModal').modal();
            }

        });
    },
    //修改信息
    updateInfo: function () {
        $.post(url.commonPre + url.updateInfo, $('#updateForm').serialize(), function (result) {
            if (!common.errorHandle(result)) {
                sendTaskRecordList.page();
                common.successHandle();
                common.closeModal($('#updateForm')[0], $('#updateModal'));
            }
        });
    }

};
$(function () {
    sendTaskRecordList.init();
});