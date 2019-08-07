<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户注册</title>
</head>
<body>
<!-- 当前位置：reg.do -->
<!-- 目标位置：handle_reg.do -->
<form action="handle_reg.do" method="POST">
    <h1>用户注册</h1>
	<p>请输入用户名：</p>
	<p><input name="username" /></p>
	<p>请输入密码：</p>
	<p><input name="password" /></p>
	<p>请输入年龄：</p>
	<p><input name="age" /></p>
	<p>请输入手机号码：</p>
	<p><input name="phone" /></p>
	<p>请输入电子邮箱：</p>
	<p><input name="email" /></p>
	<p><input type="submit" value="注册" /></p>
</form>
</body>
</html>