<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
	<head>
		<title><fmt:message key='rol.test.rols_usu' /></title>
	</head>
	<body>
		<ul>
			<c:forEach var="rol" items="${roles}">
				<li>${rol}</li>
			</c:forEach>
		</ul>
	</body>
</html>
