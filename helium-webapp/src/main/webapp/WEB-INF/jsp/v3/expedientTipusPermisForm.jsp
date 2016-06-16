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
	<hel:modalHead/>
</head>
<body>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="permisCommand">
		<form:hidden path="id"/>
		<c:if test="${not empty permisCommand.id}">
			<form:hidden path="principalTipus"/>
			<form:hidden path="principalNom"/>
		</c:if>
		<hel:inputSelect name="principalTipus" textKey="expedient.tipus.permis.form.camp.tipus" disabled="${not empty permisCommand.id}" optionItems="${principalTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		<hel:inputText name="principalNom" textKey="expedient.tipus.permis.form.camp.principal" disabled="${not empty permisCommand.id}"/>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="read" textKey="permis.READ"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="taskManagement" textKey="permis.TASK_MANAGEMENT"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="write" textKey="permis.WRITE"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="taskSupervision" textKey="permis.TASK_SUPERVISION"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="create" textKey="permis.CREATE"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="taskAssign" textKey="permis.TASK_ASSIGN"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="delete" textKey="permis.DELETE"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="goBack" textKey="permis.GO_BACK"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="administration" textKey="permis.ADMINISTRATION"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="designAdmin" textKey="permis.DESIGN_ADMIN"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="cancel" textKey="permis.CANCEL"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="designDeleg" textKey="permis.DESIGN_DELEG"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="stop" textKey="permis.STOP"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="scriptExe" textKey="permis.SCRIPT_EXE"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="relate" textKey="permis.RELATE"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="undoEnd" textKey="permis.UNDO_END"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="dataManagement" textKey="permis.DATA_MANAGEMENT"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="design" textKey="permis.ORGANIZATION"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="docManagement" textKey="permis.DOC_MANAGEMENT"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="organization" textKey="permis.DESIGN"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="termManagement" textKey="permis.TERM_MANAGEMENT"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="supervision" textKey="permis.SUPERVISION"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"><hel:inputCheckbox name="manage" textKey="permis.MANAGE"/></div>
			<div class="col-sm-6"><hel:inputCheckbox name="reassignment" textKey="permis.REASSIGNMENT"/></div>
		</div>
		<div class="row">
			<div class="col-sm-6"></div>
			<div class="col-sm-6"><hel:inputCheckbox name="defprocUpdate" textKey="permis.DEFPROC_UPDATE"/></div>
		</div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
