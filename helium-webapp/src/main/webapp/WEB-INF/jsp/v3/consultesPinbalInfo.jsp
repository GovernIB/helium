<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="consultes.pinbal.detall.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
</head>
<body>
	<c:if test="${not empty peticioPinbalDto}">
		<dl class="dl-horizontal">
			
			<dt><spring:message code="consultes.pinbal.camp.entorn"/></dt>
			<dd>${peticioPinbalDto.entorn.nom}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.tipus"/></dt>
			<dd>${peticioPinbalDto.tipus.nom}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.exp"/></dt>
			<dd>${peticioPinbalDto.expedient.identificador}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.dataPeticio"/></dt>
			<dd><fmt:formatDate value="${peticioPinbalDto.dataPeticio}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			
			<dt><spring:message code="consultes.pinbal.camp.usuari"/></dt>
			<dd>${peticioPinbalDto.usuari}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.procediment"/></dt>
			<dd>${peticioPinbalDto.procediment}</dd>			
			
			<dt><spring:message code="consultes.pinbal.camp.pinbal"/></dt>
			<dd>${peticioPinbalDto.pinbalId}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.document"/></dt>
			<dd>${peticioPinbalDto.document.arxiuNom}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.estat"/></dt>
			<dd>${peticioPinbalDto.estat}</dd>			
			
			<dt><spring:message code="consultes.pinbal.camp.asincrona"/></dt>
			<dd>${peticioPinbalDto.asincrona}</dd>
			
			<c:if test="${peticioPinbalDto.asincrona}">
					<dt><spring:message code="consultes.pinbal.camp.dataPrevista"/></dt>
					<dd><fmt:formatDate value="${peticioPinbalDto.dataPrevista}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
					
					<dt><spring:message code="consultes.pinbal.camp.dataProcessamentDarrer"/></dt>
					<dd><fmt:formatDate value="${peticioPinbalDto.dataProcessamentDarrer}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			</c:if>
			
			<c:if test="${not empty peticioPinbalDto.errorMsg}">
				<dt><spring:message code="consultes.pinbal.camp.error"/></dt>
				<dd>${peticioPinbalDto.errorMsg}</dd>
			</c:if>
			
		</dl>
		<c:if test="${not empty peticioPinbalDto.errorProcessament}">
			<div class="panel-body" >
				<pre style="height:300px">${peticioPinbalDto.errorProcessament}</pre>
			</div>
		</c:if>
	</c:if>
	<div id="modal-botons">
		<a href="<c:url value="/consultesPinbal"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>