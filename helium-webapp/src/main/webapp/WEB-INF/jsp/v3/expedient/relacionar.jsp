<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
	<head>
		<title>Relacionar expediente</title>
		<c:import url="../common/formIncludes.jsp"/>
	</head>
	<body>		
<form:form id="relacionarExpediente" name="relacionarExpedient" action="relacionarExpediente" method="post" commandName="relacionarCommand" cssClass="uniForm">
	<div class="inlineLabels">
		<div class="control-group">
			<label class="control-label"><spring:message code='expedient.info.relacionar.amb'/></label>
			<div class="controls">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="expedientIdDesti"/>
					<c:param name="type" value="suggest"/>
					<c:param name="label"></c:param>
					<c:param name="suggestUrl"><c:url value="expedient/suggest"/></c:param>
				</c:import>
			</div>
		</div>
	</div>
	<div id="formButtons">
		<button type="submit" class="btn btn-primary"><spring:message code="expedient.info.relacionar"/></button>
	</div>
</form:form>
