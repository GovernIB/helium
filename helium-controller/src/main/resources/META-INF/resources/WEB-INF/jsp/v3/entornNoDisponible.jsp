<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
	<title><fmt:message key="index.inici"/></title>
</head>
<body>

	<div class="alert alert-warning" role="alert">
		<span class="fa fa-exclamation-triangle"></span>
		<strong><spring:message code="expedient.info.errors"/> </strong><fmt:message key='index.no_acces' />
	</div>

</body>
</html>
