$(document).ready(function(){

	$("#search").click(function(){
		submit();
	});
	
	$("#export").click(function(){
		exportData();
	});
});

submit = function() {
	var startDay = $("#startDay").val();
	var endDay = $("#endDay").val();
	
//	if (startDay == "" || endDay == "") {
//		alert("请输入日期！");
//		return;
//	}
	
	$("#myform").prop("action", "statusQuery.do");
	$("#search").prop("disabled", true);
	$("#myform").submit();
}

exportData = function() {
	var startDay = $("#startDay").val();
	var endDay = $("#endDay").val();
	
	if (startDay == "" || endDay == "") {
		alert("请输入日期！");
		return;
	}
	
	$("#myform").prop("action", "exportStatusQuery.do");
	$("#export").prop("disabled", true);
	$("#myform").submit();
}