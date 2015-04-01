<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code='alerta.llistat.expedient' />: ${expedientIdentificador}</title>
	<meta name="capsaleraTipus" content="llistat"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<hel:modalHead/>
	<style>
		body {background-image: none;}
		.col-xs-4 {width: auto;}
		.col-xs-8 {width: 77%;}
		.form-group {width: 100%;}
	</style>
</head>
<body>
	<form:form action="reassignar" cssClass="form-horizontal form-tasca" commandName="expedientTascaReassignarCommand">
		<form:hidden path="taskId"/>

		<div class="control-group fila_reducida">
			<hel:inputText required="true" name="expression" textKey="expedient.tasca.expresio_assignacio" placeholderKey="expedient.tasca.expresio_assignacio"/>
		</div>
		
		<div id="modal-botons">
			<button type="button" class="modal-tancar btn btn-default" name="submit" value="cancel"><spring:message code="comuns.cancelar"/></button>
			<button type="submit" class="btn btn-primary" id="submit" name="submit" value="submit"><span class="fa fa-share-square-o"></span>&nbsp;<spring:message code="comuns.reassignar"/></button>
		</div>
	</form:form>
</body>
</html>
