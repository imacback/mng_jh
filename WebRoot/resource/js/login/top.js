var displayBar = true;
function switchBar(obj) {
	if (displayBar) {
		parent.document.getElementById('frame').cols = "0,*";
		displayBar = false;
		obj.title = "打开左侧菜单";
	} else {
		parent.document.getElementById('frame').cols = "195,*";
		displayBar = true;
		obj.title = "关闭左测菜单";
	}
}
function frameclosed() {
	parent.window.location = "login.do?op=logout";
}