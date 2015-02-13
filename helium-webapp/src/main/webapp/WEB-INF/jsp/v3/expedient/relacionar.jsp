<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
	<head>
		<title><spring:message code="expedient.info.relacionats"/></title>
		<hel:modalHead/>
		<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
		<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
		<script src="<c:url value="/js/select2.min.js"/>"></script>
		<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
		<style>
			body {background-image: none; padding-top: 0px;}
			.col-xs-4 {width: auto;}
			.col-xs-8 {width: 100%;padding-bottom: 100px;}
			#select2-drop{left: 30px !important;}
		</style>
	</head>
	<body>		
		<form:form id="relacionarExpediente" name="relacionarExpedient" action="relacionarExpediente" method="post" commandName="relacionarCommand" cssClass="uniForm">
			<hel:inputSuggest name="expedientIdDesti" urlConsultaLlistat="expedient/suggest" urlConsultaInicial="expedient/suggest" textKey="expedient.info.relacionar.amb" placeholderKey="expedient.info.relacionar.amb"/>
			<div id="modal-botons">
				<button type="button" class="modal-tancar btn" name="submit" value="cancel">
					<spring:message code='comuns.cancelar' />
				</button>
				<button type="submit" class="btn btn-primary"><spring:message code="expedient.info.relacionar"/></button>
			</div>
		</form:form>
	</body>
</html>
