var url = {
    commonPre : "/platform/",//通用前缀
    platformList : "/platform/list/",//分页查询平台列表
    add : "/platform/add",//新增平台
    searchPlatformByName : "/platform/search/name/",//根据名字查询平台
    modifyNameSuf : "/modify/name",//修改平台名后缀
    pageByRootUserSuf : "/rootUser/list/",//分页查询该平台所有授权用户后缀
    getOrCancelRootSuf : "/root/",//获取或取消用户-平台间的授权后缀
    modifyStatusSuf : "/modify/status/",//修改平台状态
};
var tempPlatformId;//用作临时保存的平台id
var platformList = {
    //分页查询
    page : function(pageNo) {
        $.get(url.platformList + pageNo, function (result) {
            $('#tableContent').html(result);
        });
    },
    //新增平台
    add : function () {
        $.post(url.add, $('#addForm').serialize() ,function (result) {
            //提示
            if(result.code == '0000') {
                common.showMessage('成功');
                common.closeModal($('#addForm')[0],$('#addModal'));
                platformList.page(1);
            }else {
                common.showMessage(result.message);
            }
        });
    },
    //根据名字搜素平台
    searchPlatformByName : function (platformName) {
        if(platformName == ''){
            platformList.page(1);
            return;
        }
        $.get(url.searchPlatformByName + platformName,function (result) {
            $('#tableContent').html(result);
        });
    },
    //点击索框，清空内容
    searchUserInputClean : function (input) {
        //当前内容
        var tempStr = input.val();
        //清空内容
        input.val("");
        //如果之前是有内容的，就重新加载第一页
        if(tempStr)
            platformList.page(1);

    },
    //弹出修改平台名模态框
    openModifyNameModal : function (platformId) {
        tempPlatformId= platformId;
        $('#modifyNameModal').modal({closeViaDimmer: 0, width: 600, height: 300});
    },
    //修改平台名
    modifyName : function () {
        $.post(url.commonPre + tempPlatformId + url.modifyNameSuf,$('#modifyNameForm').serialize(),function (result) {
            //提示
            if(result.code == '0000') {
                common.closeModal($('#modifyNameForm')[0],$('#modifyNameModal'));
                common.showMessage('成功');
            }else {
                common.showMessage(result.message);
            }
            platformList.page(1);
        });
    },
    //分页查询某平台所有授权的用户
    pageByRootUser :function (platformId,pageNo) {
        var tempPath = url.commonPre + platformId + url.pageByRootUserSuf +  pageNo;
        $.get(tempPath, function (result) {
            $('#rootUserTableContent').html(result);
        });
    },
    //打开某平台的授权用户列表模态框
    openRootUserListModal : function (platformId) {
        platformList.pageByRootUser(platformId, 1);
        $('#rootUserListModal').modal();
    },

    //获取/取消某用户和某平台间的授权
    getOrCancelRoot : function (userId, platformId, status) {
        //拼接路径
        var getOrCancelRootPath = "/user/" + userId + url.getOrCancelRootSuf + platformId + "/" + status;
        $.post(getOrCancelRootPath, function (result) {
            if(result.code == '0000') {
                common.showMessage('成功');
                platformList.pageByRootUser(platformId, 1);
            }else{
                common.showMessage(result.message);
            }
        })
    },
    //修改状态,1:启用，0：停用
    modifyStatus : function (platformId,status) {
        $.post(url.commonPre + platformId + url.modifyStatusSuf + status, function (result) {
            if(result.code == '0000') {
                common.showMessage('成功');
                platformList.page(1);
            }else{
                common.showMessage(result.message);
            }
        });
    },

};