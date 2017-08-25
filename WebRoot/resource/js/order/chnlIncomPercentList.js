$(document).ready(function(){

	$("#search").click(function(){
		submit();
	});
});

submit = function() {
	var startDay = $("#startDay").val();
	var endDay = $("#endDay").val();
	
	if (startDay == "" || endDay == "") {
		alert("请输入日期！");
		return;
	}
	
	$("#search").prop("disabled", true);
	$("#myform").submit();
}

