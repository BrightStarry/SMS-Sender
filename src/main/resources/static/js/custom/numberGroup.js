var url = {
    commonPre: "/numberGroup",//通用前缀
    list: '/list/',//分页查询
    add: '/add',//新增
    delete: '/delete',//文件删除
    likeSearchByName: '/search/name/',//根据名字模糊查询
    query: '/query',//查询单个
    updateInfo: '/update',//修改信息
};
var flag = true;
var numberGroupList = {
    //下拉选择-号码组类别
    numberGroupTypeSelect: function () {
        var $select1 = $('#numberGroupTypeSelect');

        //打开新增模态框时,查询所有号码组类别
        $('#openAddModalButton').one('click', function () {
            $.post("/numberGroupType/list/all", null, function (result) {
                if (!common.errorHandle(result)) {
                    var operators = '<option selected value=""></option>';
                    var $list = $(result.data);
                    $list.each(function (i, item) {

                        var tmp = '<option value="'+ item.id +'" id="' + item.id + '" name="' + item.name + '">' + item.name + '</option>'
                        operators += tmp
                    });
                    //填充
                    $select1.html(operators);
                } else {
                    common.showMessage("加载号码组类别失败,请刷新页面重试");
                }
            });
        });

    },

    //下拉选择-号码源
    numberSourceSelect: function () {
        var $select2 = $('#numberSourceSelect');
        //打开新增模态框时,查询所有号码源
        $('#openAddModalButton').one('click', function () {
            $.post("/numberSource/list/all", {isDelete:0}, function (result) {
                if (!common.errorHandle(result)) {
                    var operators = '<option selected value=""></option>';
                    var $list = $(result.data);
                    $list.each(function (i, item) {
                        var tmp = '<option value="'+ item.id +'" id="' + item.id + '" name="' + item.name + '">' + item.name + '</option>'
                        operators += tmp
                    });
                    //填充
                    $select2.html(operators);
                } else {
                    common.showMessage("加载号码源失败,请刷新页面重试");
                }
            });
            //当选择框改变选择时,将选择的号码组id和name赋值给隐藏域
            // $select.on('change', function() {
            //     //选中的那个option
            //     var $selected = $(this).find('option:selected');
            //     $('#addForm :input[name="numberSourceName"]').val($selected.attr('name'));
            //     $('#addForm :input[name="numberSourceId"]').val($selected.attr('id'));
            // });
        });
    },

    //下拉选择-分组模式
    groupModeSelect : function() {
        var $select3 = $('#groupModeSelect');
        //当选择框改变选择时,将选择的号码组id和name赋值给隐藏域
        $select3.on('change', function() {
            //选中的那个option
            var $selected = $(this).find('option:selected');
            if($selected.val() == '2'){
                $('#phonesParent').show();
                $('#numberCountParent').hide();
            }
            else{
                $('#phonesParent').hide();
                $('#numberCountParent').show();
            }

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
        $('#addModal').modal({closeViaDimmer:false});
    },

    //新增
    add: function () {
        if(!flag)
            return;
        flag = false;
        $.post(url.commonPre + url.add, $('#addForm').serialize(), function (result) {
            if (!common.errorHandle(result)) {
                numberGroupList.page();
                common.successHandle();
                common.closeModal($('#addForm')[0], $('#addModal'));
                //让下拉框复位
                $('#numberSourceSelect').selected('destroy');
                $('#numberSourceSelect').selected('enable');
                $('#numberGroupTypeSelect').selected('destroy');
                $('#numberGroupTypeSelect').selected('enable');

            }
            flag = true;
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
                        numberGroupList.page();
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
            numberGroupList.page(1);
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
                $('#updateForm :input[name="name"]').val(result.data.name);
                $('#updateForm :input[name="remark"]').val(result.data.remark);
                //打开修改模态框时,
                var $select2 = $('#updateNumberGroupTypeSelect');
                $.post("/numberGroupType/list/all", null, function (result1) {
                        if (!common.errorHandle(result1)) {
                            var operators = '<option selected value=""></option>';
                            var $list = $(result1.data);
                            $list.each(function (i, item) {
                                var tmp2 = "";
                                if(item.id == result.data.typeId)
                                    tmp2 = "selected";
                                var tmp1 = '<option value="'+ item.id +'" id="' + item.id + '" ' + tmp2 + ' name="' + item.name + '">' + item.name + '</option>'
                                operators += tmp1
                            });
                            //填充
                            $select2.html(operators);
                        } else {
                            common.showMessage("加载号码组类别失败,请刷新页面重试");
                        }
                });
                $('#updateModal').modal({closeViaDimmer:false});
            }

        });
    },
    //修改信息
    updateInfo: function () {
        if(!flag)
            return;
        flag = false;
        $.post(url.commonPre + url.updateInfo, $('#updateForm').serialize(), function (result) {
            if (!common.errorHandle(result)) {
                numberGroupList.page();
                common.successHandle();
                common.closeModal($('#updateForm')[0], $('#updateModal'));
            }
            flag = true;
        });
        $('#updateNumberGroupTypeSelect').selected('destroy');
        $('#updateNumberGroupTypeSelect').selected('enable');
    }

};
$(function () {
    numberGroupList.page();
    numberGroupList.likeSearchByName();
    numberGroupList.numberGroupTypeSelect();
    numberGroupList.numberSourceSelect();
    numberGroupList.groupModeSelect();



});