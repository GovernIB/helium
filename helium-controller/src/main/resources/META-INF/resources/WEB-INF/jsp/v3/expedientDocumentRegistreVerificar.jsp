<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code='expedient.document.info_registre' />: ${document.documentNom}</title>
	<hel:modalHead/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<style>
		body {background-image: none;padding-top: 0px;}
		.form-group {width: 100%;}
	</style>
</head>
<body>
	<h4 class="titol-missatge">
		<div class="form-info">
			<label><spring:message code="common.icon.oficina"/>:</label>&nbsp;<span id="registreOficinaNom">${document.registreOficinaNom}</span>
			<br/><br/>
			<label><spring:message code="common.icon.data"/>:</label>&nbsp;<span id="registreData"><fmt:formatDate value="${document.registreData}" pattern="dd/MM/yyyy HH:mm"/></span>
			<br/><br/>
			<label><spring:message code="comuns.tipus"/>:</label>&nbsp;<span id="registreEntrada"><c:choose><c:when test="${document.registreEntrada}"><spring:message code="common.icon.entrada"/></c:when><c:otherwise><spring:message code="common.icon.sortida"/></c:otherwise></c:choose></span>
			<br/><br/>
			<label><spring:message code="common.icon.numero"/>:</label>&nbsp;<span id="registreNumero">${document.registreNumero}</span>
		</div>			
	</h4>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
