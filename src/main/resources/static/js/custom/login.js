//路径对象
var url ={
    login : "/user/login",//登录
};
var login = {
    init : function () {
        /**
         * 单击登录按钮
         */
        $('#loginButton').click(function(){
            //账号校验
            //密码校验{username:$('#username').val(), password:$('#password').val()}
            //ajax提交
            $.post(url.login, $('#loginForm').serialize(), function (result) {
                //登录成功
                if(result.code == '0000'){
                    window.location.href="/numberSource/list";
                    return;
                }
                //登录失败
                $('#warnMessage').text(result.message);
                $('#warnDiv').show();
            });
        });

        /**
         * 单击回车
         */
        $(document).keydown(function (event) {
            if (event.keyCode == 13) {
                $('#loginButton').click();
            }
        });
    }

};


