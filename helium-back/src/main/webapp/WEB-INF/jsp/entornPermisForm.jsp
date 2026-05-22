<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma">ca</c:set>
<c:choose>
	<c:when test="${empty permisCommand.id}"><c:set var="titol"><spring:message code="entorn.permis.form.titol.nou"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="entorn.permis.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<meta name="title" content="${titol}"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/3.4.8/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/3.4.8/dist/js/i18n/${idioma}.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<form:form action="" method="post" cssClass="form-horizontal" modelAttribute="command">
		<form:hidden path="id"/>
		<c:if test="${not empty permisCommand.id}">
			<form:hidden path="principalTipus"/>
			<form:hidden path="principalNom"/>
		</c:if>
		<hel:inputSelect name="principalTipus" textKey="entorn.permis.form.camp.tipus" disabled="${not empty permisCommand.id}" optionItems="${principalTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="2"/>
		<hel:inputText name="principalNom" textKey="entorn.permis.form.camp.principal" disabled="${not empty permisCommand.id}" labelSize="2"/>
		<hr/>
		<div class="row">
			<div class="col-sm-3"><hel:inputCheckbox name="read" textKey="permis.READ" labelSize="8"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="design" textKey="permis.DESIGN" labelSize="8"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="administration" textKey="permis.ADMINISTRATION" labelSize="8"/></div>
			<div class="col-sm-3"></div>
		</div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/expedientTipus/${expedientTipus.id}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
