<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="tasca.info.titol.info.segonpla"/> - ${tasca.titol}</title>
	<hel:modalHead/>
</head>
<body>
	<ul class="list-group">
	<c:choose>
	<c:when test="${not empty missatges}">
		<c:forEach var="missatge" items="${missatges}">
			<li class="list-group-item">${missatge[0]}<span class="pull-right">${missatge[1]}</span></li>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<li class="list-group-item">No hi ha missatges...</li>
	</c:otherwise>
	</c:choose>
	</ul>
	<div id="modal-botons" class="well">
		<button type="button" class="modal-tancar btn btn-default" name="submit" value="cancel">
			<spring:message code='comuns.tancar' />
		</button>
	</div>
</body>
</html>