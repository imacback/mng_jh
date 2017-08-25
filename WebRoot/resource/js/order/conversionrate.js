$(document).ready(function(){

	$("#search").click(function(){
		submit();
	});
	
	$("#gameInfoId").change(function(){
		changeGameId();
	});
});

submit = function() {
	var startDay = $("#startDay").val();
	var endDay = $("#endDay").val();
	
	if (startDay == "" || endDay == "") {
		alert("请输入日期！");
		return;
	}
	
	var checkedResult = false;
	$("input[type='checkBox'][name='groupCondition']").each(function(){
		if ($(this).prop("checked")) {
			checkedResult = true;
			return false;
		}
	});
	
	if (!checkedResult) {
		alert("请选择分组条件！");
		return ;
	}
	
	$("#search").prop("disabled", true);
	$("#myform").submit();
}

changeGameId = function() {
	var gameId = $("#gameInfoId").val();
	
	if (gameId == "") {
		$("#consumeId").html("<option value=\"\">--请选择道具--</option>");
	} else {
		var options = "<option value=\"\">--请选择道具--</option>";
		$.getJSON($("#getConsumeUrl").val(), {gameId:gameId}, function(myJSON) {
			if (myJSON != "" && myJSON.length > 0) {
				for(var i=0;i<myJSON.length;i++){ 
					options+="<option value="+myJSON[i].id+">"+myJSON[i].consumeName+"</option>"; 
				} 
				$("#consumeId").html("");
				$("#consumeId").html(options);
			}
		})
	}
}