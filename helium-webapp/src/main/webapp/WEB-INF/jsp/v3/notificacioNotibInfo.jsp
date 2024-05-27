<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="notificacio.detall.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
</head>
<body>
	<c:if test="${not empty dto}">
		<dl class="dl-horizontal">
			
			<dt><spring:message code="consultes.pinbal.camp.entorn"/></dt>
			<dd>${dto.entornCodi} - ${dto.entornNom}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.tipus"/></dt>
			<dd>${dto.expedientTipusCodi} - ${dto.expedientTipusNom}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.exp"/></dt>
			<dd>${dto.expedient.identificador}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.dataPeticio"/></dt>
			<dd><fmt:formatDate value="${dto.enviatData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>		
		</dl>
		<c:if test="${error}">
			<div class="panel-body" >
				<pre style="height:300px">${dto.errorDescripcio}</pre>
			</div>
		</c:if>
	</c:if>
	<div id="modal-botons">
		<a href="<c:url value="/enviamentsPortafib"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>