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
			
			<dt><spring:message code="notificacio.detall.camp.entorn"/></dt>
			<dd>${dto.expedient.entorn.codi} - ${dto.expedient.entorn.nom}</dd>
			
			<dt><spring:message code="notificacio.detall.camp.expedient.tipus"/></dt>
			<dd>${dto.expedient.tipus.codi} - ${dto.expedient.tipus.nom}</dd>
			
			<dt><spring:message code="notificacio.detall.camp.expedient"/></dt>
			<dd>
				<a href="<c:url value="/v3/expedient/${dto.expedient.id}"/>" 
				target="_blank">${dto.expedient.identificador}</a>
			</dd>
			
			<dt><spring:message code="notificacio.llistat.columna.concepte"/></dt>
			<dd>${dto.concepte}</dd>
			
			<dt><spring:message code="notificacio.detall.camp.data.enviament"/></dt>
			<dd><fmt:formatDate value="${dto.enviatData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>		
			
			<dt><spring:message code="notificacio.detall.camp.data.programada"/></dt>
			<dd><fmt:formatDate value="${dto.dataProgramada}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			
			<dt><spring:message code="notificacio.detall.camp.retard"/></dt>
			<dd>${dto.retard}</dd>
			
			<dt><spring:message code="notificacio.detall.camp.data.caducitat"/></dt>
			<dd><fmt:formatDate value="${dto.dataCaducitat}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			
			<dt><spring:message code="notificacio.detall.camp.codi.usuari"/></dt>
			<dd>${dto.usuariCodi}</dd>
			
			<dt><spring:message code="notificacio.detall.camp.organ.emissor"/></dt>
			<dd>${dto.organEmissorCodiAndNom}</dd>
			
			<dt><spring:message code="notificacio.detall.camp.interessat"/></dt>
			<dd>${dto.interessatFullNomNif}</dd>
			
			<dt><spring:message code="notificacio.detall.camp.destinatari"/></dt>
			<dd>${dto.destinatariFullNomNif}</dd>
			
			<dt><spring:message code="notificacio.detall.camp.document.enviat"/></dt>
			<dd>
				<a href="<c:url value="/v3/expedient/${dto.expedient.id}/proces/${dto.expedient.processInstanceId}/document/${dto.documentStoreId}/descarregar"/>"
					title="<spring:message code="expedient.notificacio.descarregar.doc"/> ${dto.arxiuNom}">
					${dto.nomDocument != null? dto.nomDocument : dto.arxiuNom}
					<span class="fa fa-download fa-lg"></span>
				</a>
			</dd>
			
			<dt><spring:message code="notificacio.detall.camp.annexos"/></dt>
			<dd></dd>
			
			<dt><spring:message code="notificacio.detall.camp.justificant"/></dt>
			<dd>
				<c:if test="${not empty dto.justificantId}">
					<a href="<c:url value="/v3/expedient/${dto.expedient.id}/proces/${dto.expedient.processInstanceId}/document/${dto.justificantId}/descarregar"/>"
						title="<spring:message code="expedient.notificacio.descarregar.doc"/> ${dto.justificantArxiuNom}">
						${dto.justificantArxiuNom}
						<span class="fa fa-download fa-lg"></span>
					</a>
				</c:if>
			</dd>
			
			<dt><spring:message code="notificacio.llistat.columna.estat"/></dt>
			<dd>
				<span title="<spring:message code="notificacio.etst.enum.${dto.estat}.info"/>">
					<c:choose>
							<c:when test="${dto.estat == 'PENDENT'}">
								<span class="fa fa-clock-o"></span>
							</c:when>
							<c:when test="${dto.estat == 'ENVIADA'}">
								<span class="fa fa-send-o"></span>
								<span class="label label-warning"></span>
							</c:when>
							<c:when test="${dto.estat == 'REGISTRADA'}">
								<span class="fa fa-file-o"></span>
							</c:when>
							<c:when test="${dto.estat == 'FINALITZADA'}">
								<span class="fa fa-check"></span>
							</c:when>							
							<c:when test="${dto.estat == 'PROCESSADA'}">
								<span class="fa fa-check-circle"></span>
							</c:when>
					</c:choose>
			<spring:message code="notificacio.etst.enum.${dto.estat}"/>
			</dd>	
			
			<dt><spring:message code="expedient.notificacio.estat.enviament"/></dt>
			<dd>
				<c:if test="${not empty dto.enviamentDatatEstat}">
								<spring:message code="notificacio.enviament.estat.enum.${dto.enviamentDatatEstat}"/>
							</c:if>
							<c:if test="${dto.enviamentDatatData != null}">
								<br/><span class="text-muted small">
										<fmt:formatDate value="${dto.enviamentDatatData}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate>
									</span>
							</c:if>
			</dd>
							
			<dt><spring:message code="notificacio.detalls.camp.intents"/></dt>
			<dd>${dto.numIntents}</dd>		
			
			<dt><spring:message code="notificacio.detalls.camp.data.ultim.intent"/></dt>
			<dd><fmt:formatDate value="${dto.intentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				
			<dt><spring:message code="notificacio.detalls.camp.data.proxim.intent"/></dt>
			<dd><fmt:formatDate value="${dto.intentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>	
			
		</dl>
		<c:if test="${error}">
			<div class="panel-body" >
				<pre style="height:300px">${dto.errorDescripcio}</pre>
			</div>
		</c:if>
	</c:if>
	<div id="modal-botons">
		<a href="<c:url value="/notificacionsNotib"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>