<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${empty estatReglaCommand.id}"><c:set var="titol"><spring:message code="expedient.tipus.regla.form.titol.nou"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="expedient.tipus.regla.form.titol.modificar"/></c:set></c:otherwise>
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
			$("#quiValor").select2({
				theme: "bootstrap",
				placeholder: "<spring:message code="expedient.tipus.regla.form.camp.qui.valors"/>",
				minimumResultsForSearch: 10,
				tags: true,
				tokenSeparators: [',', ' ']
			});
			$("#queValor").select2({
				theme: "bootstrap",
				placeholder: "<spring:message code="expedient.tipus.regla.form.camp.que.valors"/>",
				minimumResultsForSearch: 10,
				tags: true,
				tokenSeparators: [',', ' ']
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
	<form:form action="" method="post" cssClass="form-horizontal" commandName="estatReglaCommand">
		<form:hidden path="id"/>
		<form:hidden path="expedientTipusId"/>
		<form:hidden path="estatId"/>

		<div class="row">
			<div class="col-xs-12">
				<hel:inputText name="nom" textKey="expedient.tipus.regla.form.camp.nom" readonly="${not empty estatReglaCommand.id}" labelSize="2" required="true"/>
			</div>
		</div>
		<hr/>
		<div class="row">
			<div class="col-xs-4">
				<hel:inputSelect name="qui" textKey="expedient.tipus.regla.form.camp.qui" emptyOption="true" optionItems="${quiOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="6" required="true"/>
			</div>
			<div class="col-xs-8">
				<div class="form-group">
					<div class="controls col-xs-12">
						<form:select path="quiValor" cssClass="form-control" id="quiValor" multiple="multiple">
							<form:options items="${estatReglaCommand.quiValor}"/>
						</form:select>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-4">
				<hel:inputSelect name="que" textKey="expedient.tipus.regla.form.camp.que" emptyOption="true" optionItems="${queOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="6" required="true"/>
			</div>
			<div class="col-xs-8">
				<div class="form-group">
					<div class="controls col-xs-12">
						<form:select path="queValor" cssClass="form-control" id="queValor" multiple="multiple">
							<form:options items="${estatReglaCommand.queValor}"/>
						</form:select>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12">
				<hel:inputSelect name="accio" textKey="expedient.tipus.regla.form.camp.accio" emptyOption="true" optionItems="${accioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="2" required="true"/>
			</div>
		</div>
		<div style="min-height: 150px;"></div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
