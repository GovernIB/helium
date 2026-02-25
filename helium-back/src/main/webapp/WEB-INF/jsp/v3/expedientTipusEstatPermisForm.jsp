<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${empty permisCommand.id}"><c:set var="titol"><spring:message code="expedient.tipus.permis.form.titol.nou"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="expedient.tipus.permis.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<meta name="title" content="${titol}"/>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
	<script>
		$(document).ready(function() {
			$(":checkbox").on('change', function () {
				if ($(this).attr('name') !== 'read' && $(this).prop("checked")) {
					$("#read").prop('checked', true);
				}
				if ($(this).attr('name') === 'read' && !$(this).prop("checked")) {
					$("#write").prop('checked', false);
					$("#dataManagement").prop('checked', false);
					$("#docManagement").prop('checked', false);
				}
			});
		});

	</script>
	<style>
		.text-left { text-align: left !important; }
		.select2-container { width: 100% !important; }
	</style>
	<hel:modalHead/>
</head>
<body>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="permisCommand">
		<form:hidden path="id"/>
		<c:if test="${not empty permisCommand.id}">
			<form:hidden path="principalTipus"/>
			<form:hidden path="principalNom"/>
		</c:if>
		<hel:inputSelect name="principalTipus" textKey="expedient.tipus.permis.form.camp.tipus" disabled="${not empty permisCommand.id}" optionItems="${principalTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="2"/>
		<hel:inputText name="principalNom" textKey="expedient.tipus.permis.form.camp.principal" disabled="${not empty permisCommand.id}" labelSize="2"/>
		<hr/>
		<hel:inputCheckbox name="read" textKey="permis.READ" labelSize="8" labelOffset="2" labelClass="text-left" info="permis.estat.READ.info" checkLabelClass="form-switch"/>
		<hel:inputCheckbox name="write" textKey="permis.WRITE" labelSize="8" labelOffset="2" labelClass="text-left" info="permis.estat.WRITE.info" checkLabelClass="form-switch"/>
		<hel:inputCheckbox name="dataManagement" textKey="permis.DATA_MANAGEMENT" labelSize="8" labelOffset="2" labelClass="text-left" info="permis.estat.DATA_MANAGEMENT.info" checkLabelClass="form-switch"/>
		<hel:inputCheckbox name="docManagement" textKey="permis.DOC_MANAGEMENT" labelSize="8" labelOffset="2" labelClass="text-left" info="permis.estat.DOC_MANAGEMENT.info" checkLabelClass="form-switch"/>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
