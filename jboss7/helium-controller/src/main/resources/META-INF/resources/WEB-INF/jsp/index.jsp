<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title>Helium Spring Boot 2</title>
</head>
<body>
	<div>
 			<div class="col-12 app_content">
			<div class="card cardHome">
				<div class="card-body">
					<h1 class="card-title">Prova d'aplicació Spring Boot 2 publicada amb WAR BBB</h1>
					<p>Només conté aquesta pàgina d'inici</p>
					<p>${name}</p>
				</div>
			</div>
		</div>
	</div>
</body>
</html>