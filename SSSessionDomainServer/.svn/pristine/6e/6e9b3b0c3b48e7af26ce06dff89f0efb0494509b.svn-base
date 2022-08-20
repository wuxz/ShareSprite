<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"  />
    <title> 重置密码</title>
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" /> 
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <link href="http://www.baiku.cn/style/passport.css" type="text/css" rel="stylesheet" />
    <script src="http://www.baiku.cn/script/jquery.js" type="text/javascript"></script>
    
    <script src="http://www.baiku.cn/script/rpc.js" type="text/javascript"></script>
    <script src="http://www.baiku.cn/script/json2.js" type="text/javascript"></script>
    <script src="http://www.baiku.cn/script/common.js" type="text/javascript"></script>
    <script type="text/javascript">
    $(function(){
    	$("#newpassword").blur(function(){
			var repost= /^[a-zA-Z0-9]{6,18}$/;
			if(repost.test($("#newpassword").attr("value"))){
				$("#tips").text("");
			}else{
				$("#tips").text("密码必须为6-18位");
			}
    	});
    
		$("#submit").click(function(){
			if($("#newpassword").attr("value") != $("#newpassword2").attr("value")){
				//alert("密码和确认密码不匹配");
				$("#tips").text("密码和确认密码不匹配");
				return false;
			}
			var repost= /^[a-zA-Z0-9]{6,18}$/;
			if(!repost.test($("#newpassword").attr("value"))){
				//alert("密码必须为6-18位");
				$("#tips").text("密码必须为6-18位");
				return false;
			}
			
			//修改确认密码
	    	var req = {"user":{"domain":"userid","id":""}};
	  		req.user.id=$("#userid").attr("value");
	  		req.code = $("#code").attr("value");
	  		req.new_password = $("#newpassword").attr("value");
	    	try{
		  		call("SSSessionDomainSvc","ResetPassword","resetPasswordRequest",req,function(resp){
		  			$("#step1").css("display","none");
		  			$("#step2").css("display","inline");
		  			if(resp.response.code == 0){
						$("#result").text("密码重置成功,请使用新密码登录。");
		  			}else{
		  				$("#result").text("密码重置失败：" +resp.resetPasswordResponse.reason);
		  			}
		  		});
	    	}catch(err){
	    		  //alert("出错啦：" + err.message);
	    		  $("#tips").text("出错啦：" + err.message);
	    	}
		});

      });
	</script>    
  </head>

  <body>
  <iframe id="proxy" src="http://api.baiku.cn:8080/proxy.html" style="display:none;" ></iframe>
    <div class="wrap">
      <div class="innerbg">

        <div class="header">
          <div class="logo">百库baiku</div>
          <div class="userTools"><!--<a href="http://www.baiku.cn/index.html">百库首页</a> | <a href="http://www.baiku.cn/login.html">登录</a> | <a href="http://www.baiku.cn/register.html">注册</a> | <a href="http://www.baiku.cn/help.html">帮助</a>--></div>

        </div>

<div id="step1">
        <div class="passBlock"><div class="passBlock_clip">
          <h2 class="title title2">重置密码</h2>
          <div class="msg"><span class="ico"></span>请重新设置您的密码，这次可别忘记哦！</div>
 	<form action="#" method="POST">
		<input type="hidden" name="userid" value="${userid}" id="userid"/>
		<input type="hidden" name="code" value="${code}" id="code"/>
          <div class="forms"><label for="bindemail">设置新密码：</label><input type="password" id="newpassword" name="newpassword" class="inputText" /><span id="tips" style="color:red;"></span></div>
          <div class="forms"><label for="bindemail">确认密码：</label><input type="password" id="newpassword2" name="newpassword2" class="inputText" />
          <button class="button01" id="submit">提交</button></div>
	</form>


        </div></div>
</div>

<div id="step2" style="display:none;">
        <div class="passBlock">
		<div class="passBlock_clip">
          <h2 class="title title2">重置密码</h2>
          <div class="msg f14"><span class="ico ico2"></span><span id="result">新密码重置成功,请使用新密码 <a href="http://www.baiku.cn/login.html">登录</a> ！</span></div>

        </div>
		</div>
</div>		



        <div class="footer">
          青牛（北京）技术有限公司版权所有 　京ICP证041404号<br />
          Copyright 2011 baiku.cn Corporation, All Rights Reserved

        </div>

      </div>
    </div>

  </body>
</html>
