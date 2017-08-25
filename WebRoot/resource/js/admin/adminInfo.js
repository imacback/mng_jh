update = function() {
	var userName = $("#userName").val();
	$("#subbutton").prop("disabled",true);
	
	if (userName == "") {
		alert("请输入管理员名称！");
		$("#userName").focus();
		$("#subbutton").prop("disabled",false);
		return;
	}
	
	$("#updateAdmin").submit();
}