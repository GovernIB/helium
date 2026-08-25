<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma">ca</c:set>

<c:set var="titol"><spring:message code="definicio.proces.form.titol.update"/></c:set>
<c:set var="formAction">update</c:set>

<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/3.4.8/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
</head>
<body>
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" modelAttribute="definicioProcesUpdateCommand">
		<div class="inlineLabels">

			<script type="text/javascript">
				// <![CDATA[
				$(document).ready( function() {
				});
				// ]]>
			</script>
			<hel:inputHidden name="id" />
			<hel:inputText required="true" name="jbpmKey" textKey="definicio.proces.form.camp.jbpmKey" disabled="true"/>
			<hel:inputText name="versio" textKey="definicio.proces.form.camp.versio" disabled="true"/>
			<hel:inputText required="false" name="etiqueta" textKey="definicio.proces.form.camp.etiqueta" />
			<hel:inputCheckbox name="hasStartTask" textKey="definicio.proces.form.camp.hasStartTask" info="definicio.proces.form.camp.hasStartTask.info"/>
		</div>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button type="submit" class="btn btn-success right">
				<span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/>
			</button>
		</div>
	</form:form>
</body>
</html>
