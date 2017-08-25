function next() {
	var f = $("#pagedform");
	var pi = parseInt($("#pageId").val());
	if (pi < $("#pageNum").val()) {
		$("#pageId").val(pi + 1);
		f.submit();
	}
}

function prev() {
	var f = $("#pagedform");
	var pi = parseInt($("#pageId").val());
	if (pi > 1) {
		$("#pageId").val(pi - 1);
		f.submit();
	}
}

function _submitPaged(pageid) {
	$("#pageId").val(pageid);
	$("#pagedform").submit();
}

function confirmDelete() {
	return window.confirm('确实要删除该条记录吗？');
}

/**
 * 时间对象的格式化
 */
Date.prototype.format = function(format) {
	/*
	 * format="yyyy-MM-dd hh:mm:ss";
	 */
	var o = {
		"M+" : this.getMonth() + 1,
		"d+" : this.getDate(),
		"h+" : this.getHours(),
		"m+" : this.getMinutes(),
		"s+" : this.getSeconds(),
		"q+" : Math.floor((this.getMonth() + 3) / 3),
		"S" : this.getMilliseconds()
	}

	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4
						- RegExp.$1.length));
	}

	for (var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1
							? o[k]
							: ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
}

function accDiv(arg1,arg2){   
	var t1=0,t2=0,r1,r2;   
	try{t1=arg1.toString().split(".")[1].length}catch(e){}   
	try{t2=arg2.toString().split(".")[1].length}catch(e){}   
	with(Math){   
	r1=Number(arg1.toString().replace(".",""))   
	r2=Number(arg2.toString().replace(".",""))   
	return (r1/r2)*pow(10,t2-t1);   
	}   
} 

/**
*乘法函数，用来得到精确的乘法结果
*
8
*/
function accMul(arg1,arg2)   
{   
	var m=0,s1=arg1.toString(),s2=arg2.toString();   
	try{m+=s1.split(".")[1].length}catch(e){}   
	try{m+=s2.split(".")[1].length}catch(e){}   
	return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
}   

//加法函数，用来得到精确的加法结果 

function accAdd(arg1,arg2){   
	var r1,r2,m;   
	try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}   
	try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}   
	m=Math.pow(10,Math.max(r1,r2))   
	return (arg1*m+arg2*m)/m  ; 
}  

//减法函数  
function Subtr(arg1,arg2){  
     var r1,r2,m,n;  
     try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}  
     try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}  
     m=Math.pow(10,Math.max(r1,r2));  
     //last modify by deeka  
     //动态控制精度长度  
     n=(r1>=r2)?r1:r2;  
     return ((arg1*m-arg2*m)/m).toFixed(n);  
} 

//时间比较
function checkTime(startTime, endTime) {
	var regS = new RegExp("-","gi");
	startTime = startTime.replace(regS,"/");
	endTime = endTime.replace(regS,"/");
			
	var bd = new Date(Date.parse(startTime)); 
	var ed = new Date(Date.parse(endTime));
	
	if (bd > ed) {
		return false;
	} else {
		return true;
	}
}

// ----------------------------------------------------------------------
// <summary>
// 限制只能输入数字
// </summary>
// ----------------------------------------------------------------------
$.fn.onlyNum = function () {
    $(this).keypress(function (event) {
        var eventObj = event || e;
        var keyCode = eventObj.keyCode || eventObj.which;
        if ((keyCode >= 48 && keyCode <= 57))
            return true;
        else
            return false;
    }).focus(function () {
    //禁用输入法
        this.style.imeMode = 'disabled';
    }).bind("paste", function () {
    //获取剪切板的内容
        var clipboard = window.clipboardData.getData("Text");
        if (/^\d+$/.test(clipboard))
            return true;
        else
            return false;
    });
};

// ----------------------------------------------------------------------
// <summary>
// 限制只能输入字母
// </summary>
// ----------------------------------------------------------------------
$.fn.onlyAlpha = function () {
    $(this).keypress(function (event) {
        var eventObj = event || e;
        var keyCode = eventObj.keyCode || eventObj.which;
        if ((keyCode >= 65 && keyCode <= 90) || (keyCode >= 97 && keyCode <= 122))
            return true;
        else
            return false;
    }).focus(function () {
        this.style.imeMode = 'disabled';
    }).bind("paste", function () {
        var clipboard = window.clipboardData.getData("Text");
        if (/^[a-zA-Z]+$/.test(clipboard))
            return true;
        else
            return false;
    });
};

// ----------------------------------------------------------------------
// <summary>
// 限制只能输入数字和字母
// </summary>
// ----------------------------------------------------------------------
$.fn.onlyNumAlpha = function () {
    $(this).keypress(function (event) {
        var eventObj = event || e;
        var keyCode = eventObj.keyCode || eventObj.which;
        if ((keyCode >= 48 && keyCode <= 57) || (keyCode >= 65 && keyCode <= 90) || (keyCode >= 97 && keyCode <= 122))
            return true;
        else
            return false;
    }).focus(function () {
        this.style.imeMode = 'disabled';
    }).bind("paste", function () {
        var clipboard = window.clipboardData.getData("Text");
        if (/^(\d|[a-zA-Z])+$/.test(clipboard))
            return true;
        else
            return false;
    });
};