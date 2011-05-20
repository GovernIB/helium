<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
	<head>
		<title>Rols de l'usuari</title>
	</head>
	<body>
		<ul>
			<c:forEach var="rol" items="${roles}">
				<li>${rol}</li>
			</c:forEach>
		</ul>
	</body>
</html>
