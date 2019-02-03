<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<!-- 只有未登陆的游客才能看到此标签体里面的内容 -->
	<shiro:guest>
		 你好游客 ，请登陆
		 <a href="${pageContext.request.contextPath }/login.jsp">登陆页面</a>
	</shiro:guest>
	
	<!-- 只有登陆的用户才能看到此标签体里面的内容 -->
	<shiro:authenticated>
		<!-- 这个标签可以获取当前用户的  pricipal , 如果这个 subject 有多个的话，那么只显示第一个 -->
		你好，<shiro:principal /> <br>
	</shiro:authenticated>
	
	<!-- 管理员才能看得到的内容 -->
	<shiro:hasRole name="admin">
		<a href="http://www.baidu.com">管理员专属链接</a>
	</shiro:hasRole>
</body>
</html>