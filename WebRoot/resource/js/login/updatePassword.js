function checkOld(){
	    var a = document.getElementById("oldPassword").value;
	    if(a==""){
		     document.getElementById("old").innerHTML="原始密码不能为空";
			 return false;
		}else if(a.length<6){
		     document.getElementById("old").innerHTML="密码最少要六位";
			 return false;
		}else {
		   document.getElementById("old").innerHTML="";
		   return true;
		}
	}
	function checkNew(){
	    var a = document.getElementById("newPassword").value;
	    if(a ==""){
		    document.getElementById("new").innerHTML="新密码不能为空";
			return false;
		}else if(a.length<6){
		     document.getElementById("new").innerHTML="密码最少要六位";
			 return false;
		}else{
		     document.getElementById("new").innerHTML="";
			 return true;
		}
	}
	
	function checkCofirm(){
	   var a = document.getElementById("newPassword").value;
	   var b = document.getElementById("confirmNewPassword").value;
	   if(a!=b){
		    document.getElementById("confirmnew").innerHTML="两次输入的新密码不相等";
			return false;
		}else{
		    document.getElementById("confirmnew").innerHTML="";
			return true;
		}
	}
	
	function clearMsg(){
	    document.getElementById("old").innerHTML="";
	    document.getElementById("new").innerHTML="";
	    document.getElementById("confirmnew").innerHTML="";
		document.getElementById("update1").innerHTML="";
	    document.getElementById("update2").innerHTML="";
	}
	
	function checkPassworld(){
	    if(checkOld()&&checkNew()&&checkCofirm()){
		    return true;
		}else{
		    return false;
		}
	}