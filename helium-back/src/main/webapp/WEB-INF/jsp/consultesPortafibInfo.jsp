<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="consultes.potafib.detall.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
</head>
<body>
	<c:if test="${not empty dto}">
		<dl class="dl-horizontal">
			
			<dt><spring:message code="consultes.pinbal.camp.entorn"/></dt>
			<dd>${dto.entornNom}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.tipus"/></dt>
			<dd>${dto.tipusExpedientNom}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.exp"/></dt>
			<dd>${dto.expedientIdentificador}</dd>
			
			<dt><spring:message code="consultes.potafib.camp.nomDoc"/></dt>
			<dd>${dto.documentNom}</dd>
			
			<dt><spring:message code="anotacio.annex.detalls.camp.arxiu.uuid"/></dt>
			<dd>${dto.documentUUID}</dd>
			
			<dt><spring:message code="consultes.pinbal.camp.dataPeticio"/></dt>
			<dd><fmt:formatDate value="${dto.dataEnviat}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			
			<dt><spring:message code="consultes.pinbal.camp.estat"/></dt>
			<dd>${dto.estat}</dd>
			
			<c:if test="${dto.estat=='REBUTJAT'}">			
				<dt><spring:message code="consultes.potafib.camp.dataRebuig"/></dt>
				<dd><fmt:formatDate value="${dto.dataSignatRebutjat}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							
				<dt><spring:message code="consultes.potafib.camp.motiuRebuig"/></dt>
				<dd>${dto.motiuRebuig}</dd>
			</c:if>
			
			<c:if test="${dto.estat!='REBUTJAT'}">
				<dt><spring:message code="consultes.potafib.camp.dataFirmat"/></dt>
				<dd><fmt:formatDate value="${dto.dataSignatRebutjat}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			</c:if>
			
			<dt><spring:message code="consultes.potafib.camp.dataProcessamentPrimer"/></dt>
			<dd><fmt:formatDate value="${dto.dataProcessamentPrimer}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			
			<dt><spring:message code="consultes.potafib.camp.dataProcessamentDarrer"/></dt>
			<dd><fmt:formatDate value="${dto.dataProcessamentDarrer}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			
		</dl>
		<c:if test="${not empty dto.errorProcessant}">
			<div class="panel-body" >
				<pre style="height:300px; white-space: pre-wrap; word-break: keep-all;">
					<c:out value="${dto.errorProcessant}" escapeXml="true"/>
				</pre>
			</div>
		</c:if>
	</c:if>
	<div id="modal-botons">
		<a href="<c:url value="/enviamentsPortafib"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>