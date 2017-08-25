add = function() {
	$("#subbutton").prop("disabled",true);
	
	var name = $("#name").val();
	var description = $("#description").val();

	var falg = 0; 
	$("input[type='checkBox']").each(function () { 
		if ($(this).attr("checked")) { 
			falg =1; 
		} 
	})
	
	if (name == "") {
		alert("请输入角色名！");
		$("#name").focus();
		$("#subbutton").prop("disabled",false);
		return;
	}
	
	if (description == "") {
		alert("请输入角色描述！");
		$("#description").focus();
		$("#subbutton").prop("disabled",false);
		return;
	}
	if (falg == 0) {
		alert("请选择角色操作!");
		$("#subbutton").prop("disabled",false);
		return;
	}
	
	$("#addRole").submit();
}

$(function () {
    // 全选
    $(".comphead").bind("click", function () {
    	var id = $(this).attr("id");
        if ($("#"+id).prop("checked")) {
			$("input[type='checkBox'][class='"+id+"']").prop("checked", true);
		} else {
			$("input[type='checkBox'][class='"+id+"']").prop("checked", false);
		}
    });
})

