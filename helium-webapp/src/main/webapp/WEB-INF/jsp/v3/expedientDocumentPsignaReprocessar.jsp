<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code='expedient.document.info.psigna' />: ${document.documentNom}</title>
	<hel:modalHead/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<style>
		body {background-image: none;padding-top: 0px;}
		.form-group {width: 100%;}
	</style>
</head>
<body>
	<ul class="list-group">
	  	<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.id"/></strong><span class="pull-right" id="psignaDocumentId">${portasignatures.documentId}</span></li>
	  	<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.data.enviat"/></strong><span class="pull-right" id="psignaDataEnviat"><fmt:formatDate value="${portasignatures.dataEnviat}" pattern="dd/MM/yyyy HH:mm"/></span></li>
	  	
	  	<li class="list-group-item">
		  		<strong><spring:message code="common.icones.doc.psigna.estat"/></strong>
		  		
		  		<c:choose>
				<c:when test="${portasignatures.estat == 'PROCESSAT' && portasignatures.error}">
					<span class="pull-right" id="psignaEstat">REBUTJAT</span>
				</c:when>
				<c:when test="${portasignatures.estat != 'PROCESSAT' && portasignatures.error && not empty portasignatures.errorProcessant}">
					<span class="pull-right" id="psignaEstat">PENDENT</span>
				</c:when>
				<c:otherwise>
					<span class="pull-right" id="psignaEstat">${portasignatures.estat}</span>
				</c:otherwise>
				</c:choose>
		  		
		  	</li>
		
		<c:if test="${not empty portasignatures.motiuRebuig}">
			<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.motiu.rebuig"/></strong><span class="pull-right">${portasignatures.motiuRebuig}</span></li>
		</c:if>
		<c:if test="${not empty portasignatures.dataProcessamentPrimer}">
			<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.data.proces.primer"/></strong><span class="pull-right"><fmt:formatDate value="${portasignatures.dataProcessamentPrimer}" pattern="dd/MM/yyyy HH:mm"/></span></li>
		</c:if>
		<c:if test="${not empty portasignatures.dataProcessamentDarrer}">
			<li class="list-group-item"><strong><spring:message code="common.icones.doc.psigna.data.proces.darrer"/></strong><span class="pull-right"><fmt:formatDate value="${portasignatures.dataProcessamentDarrer}" pattern="dd/MM/yyyy HH:mm"/></span></li>
		</c:if>
	</ul>
	<c:if test="${portasignatures.error && not empty portasignatures.errorProcessant}">
		<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
			<div class="panel panel-default">
				<div class="panel-heading" role="tab" id="headingOne">
					<h4 class="panel-title">
						<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
							<strong><spring:message code="common.icones.doc.psigna.error.processant"/></strong>
						</a>
					</h4>
				</div>
				<div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
					<div class="panel-body panell-error">
						${portasignatures.errorProcessant}
					</div>
				</div>
			</div>
		</div>
	</c:if>
	
	<form id="modal-form" action="<c:url value='../../v3/expedient/${expedient.id}/document/${document.id}/psignaReintentar/${portasignatures.documentId}'/>" method="POST">
		<div id="modal-botons" class="well">
			<c:if test="${portasignatures.error && portasignatures.estat != 'PROCESSAT'}">
				<c:if test="${expedient.permisDocManagement}">
					<button type="submit" class="btn btn-primary">
						<i class="fa fa-file-text-o"></i> <spring:message code="common.icones.doc.psigna.reintentar"/>
					</button>
				</c:if>
			</c:if>
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
		</div>	
	</form>	
</body>
</html>
