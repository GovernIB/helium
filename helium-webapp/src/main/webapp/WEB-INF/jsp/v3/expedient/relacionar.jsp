<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
	<head>
		<title><spring:message code="expedient.info.relacionats"/></title>
		<c:import url="../common/formIncludes.jsp"/>
		<hel:modalHead/>
		<style>
			.col-xs-4 {width: auto;}
			.col-xs-8 {width: 100%;padding-bottom: 100px;}
			#select2-drop{left: 15px !important;}
		</style>
	</head>
	<body>		
<form:form id="relacionarExpediente" name="relacionarExpedient" action="relacionarExpediente" method="post" commandName="relacionarCommand" cssClass="uniForm">
	<hel:inputSuggest name="expedientIdDesti" urlConsultaLlistat="expedient/suggest" urlConsultaInicial="expedient/suggest" textKey="expedient.info.relacionar.amb" placeholderKey="expedient.info.relacionar.amb"/>
	<div id="formButtons">
		<button type="button" class="modal-tancar btn" name="submit" value="cancel">
			<spring:message code='comuns.cancelar' />
		</button>
		<button type="submit" class="btn btn-primary"><spring:message code="expedient.info.relacionar"/></button>
	</div>
</form:form>
