$(document).ready(function(){
	$("#search").click(function(){
		var startTime = $("#starttime").val();
		var endTime = $("#endtime").val();
		
		if(startTime == ""||endTime == ""){
		//开始或结束时间为空
		}else{
			var reg = new RegExp("-","gi");
			startTime = startTime.replace(reg,"/");
			endTime = endTime.replace(reg,"/");
			var bt = new Date(Date.parse(startTime));
			var et = new Date(Date.parse(endTime));
			if(bt > et){
				alert('开始时间大于结束时间，请检查');
				return false;
			}
		}
		$("#myform").submit();
	});
	$("#add").click(function(){
		window.location = "preAddRole.do";
	});
	
});
delRoleList = function(id){
	if(confirm("确定删除此条记录吗？")){
		window.location = "deleteRole.do?id=" + id;
	}
}