<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code='signa.verif.verificacio' />: ${signatura.documentNom}</title>
	<hel:modalHead/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<style>
		body {background-image: none;padding-top: 0px;}
		.col-xs-4 {width: auto;}
		.col-xs-8 {width: 77%;}
		.form-group {width: 100%;}
	</style>
</head>
<body>
	<c:if test="${not signatura.signat or empty signatures}">
		<div id="errors" class="alert alert-danger">
			<button class="close" data-dismiss="alert">×</button>
			<p><spring:message code='signa.verif.no_valides' /></p>
		</div>
	</c:if>

	<div class="well well-small">
		<h4 class="titol-missatge">
			<spring:message code='signa.verif.doc_signat' /> &nbsp;
			<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${signatura.tokenSignatura}"/></c:url>
			<c:if test="${not empty document.tokenSignatura}">
				<a class="icon" id="downloadUrl${document.id}" href="${downloadUrl}">
					<i class="fa fa-download"></i>
				</a>
			</c:if>
		</h4>
	</div>

	<c:forEach var="signatura" items="${signatures}" varStatus="status">
		<div class="alert <c:choose><c:when test="${signatura.estatOk}">alert-success</c:when><c:otherwise>alert-danger</c:otherwise></c:choose>">
			<h4 class="titol-missatge">
				<spring:message code='signa.verif.signatari' /> ${status.index + 1}
			</h4>
			<div class="form-info">
				<label>Nom:</label>&nbsp;${signatura.dadesCertificat[0].nombreCompletoResponsable}
				<br/>
				<label>NIF:</label>&nbsp;${signatura.dadesCertificat[0].nifResponsable}
			</div>
		</div>
	</c:forEach>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
