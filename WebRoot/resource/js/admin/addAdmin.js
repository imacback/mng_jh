add = function() {
	var userName = $("#userName").val();
	var pad = $("#pad").val();
	$("#subbutton").prop("disabled",true);
	
	if (userName == "") {
		alert("请输入管理员名称！");
		$("#userName").focus();
		$("#subbutton").prop("disabled",false);
		return;
	}
	
	if (pad == "") {
		alert("请输入管理员密码！");
		$("#pad").focus();
		$("#subbutton").prop("disabled",false);
		return;
	}
	
	$("#addAdmin").submit();
}