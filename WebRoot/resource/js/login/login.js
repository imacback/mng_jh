$(document).ready(function(){
	$('input').bind('keydown', function (e) {
	       var key = e.which;
	       if (key == 13) {
		   		submit();
	       }
	 });
	 
	 $("#resetButton").click(function(){
	 	$("#username").val("");
		$("#password").val("");
	 });
	 
	 $("#loginButton").click(function(){
	 	submit();
	 });
}); 

submit = function() {
	var userName = $("#username").val();
	var pwd = $("#password").val();
	
	if (userName == "") {
		alert("请输入用户名！");
		$("#username").focus();
		return;
	}
	
	if (pwd == "") {
		alert("请输入密码！");
		$("#password").focus();
		return;
	}
	var form = $("#loginform");
	form.submit();
}
