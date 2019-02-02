<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h2>登陆页面</h2>
	<form action="${pageContext.request.contextPath }/user/login" method="post">
		username : <input type="text" name="username" ><br>
		password : <input type="password" name="password"><br>
		<input type="submit" value="登陆">
	</form>
</body>
</html>