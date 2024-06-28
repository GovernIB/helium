<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="excepcio.detall.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
</head>
<body>
	<c:if test="${not empty excepcio}">
		<dl class="dl-horizontal">
			<dt><spring:message code="excepcio.list.columna.data"/></dt>
			<dd><fmt:formatDate value="${excepcio.data}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			<c:if test="${not empty excepcio.peticio}">
				<dt><spring:message code="excepcio.list.columna.peticio"/></dt>
				<dd>${excepcio.peticio}</dd>
			</c:if>			
			<c:if test="${not empty excepcio.params}">
				<dt><spring:message code="excepcio.list.columna.params"/></dt>
				<dd>${excepcio.params}</dd>
			</c:if>
			<c:if test="${not empty excepcio.objectClass}">
				<dt><spring:message code="excepcio.list.columna.objecte.class"/></dt>
				<dd>${excepcio.objectClass}</dd>
			</c:if>
			<c:if test="${not empty excepcio.message}">
				<dt><spring:message code="excepcio.list.columna.message"/></dt>
				<dd>${excepcio.message}</dd>
			</c:if>
		</dl>
		<c:if test="${not empty excepcio.stacktrace}">
			<div class="panel-body" >
				<pre style="height:300px">${excepcio.stacktrace}</pre>
			</div>
		</c:if>
	</c:if>
	<div id="modal-botons">
		<a href="<c:url value="/excepcio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>