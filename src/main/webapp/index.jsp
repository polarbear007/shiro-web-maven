<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<a href="${pageContext.request.contextPath }/login.jsp">登陆页面</a><br>
	<a href="${pageContext.request.contextPath }/success.jsp">登陆成功页面</a><br>
	<a href="${pageContext.request.contextPath }/unauthorized.jsp">不需要任何权限校验的页面</a><br>
	<a href="${pageContext.request.contextPath }/hello.jsp">hello.jsp</a><br>
</body>
</html>