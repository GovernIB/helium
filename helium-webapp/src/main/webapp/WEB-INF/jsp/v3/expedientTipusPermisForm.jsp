<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="permisCommand">
		<form:hidden path="id"/>
		<hel:inputSelect name="principalTipus" textKey="expedient.tipus.permis.form.camp.tipus" disabled="${not empty permisCommand.id}" optionItems="${principalTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="2"/>
		<hel:inputText name="principalNom" textKey="expedient.tipus.permis.form.camp.principal" disabled="${not empty permisCommand.id}" labelSize="2"/>
		<c:if test="${expedientTipus.procedimentComu}">
			<hel:inputSuggest 
					name="unitatOrganitzativaCodiNom" 
					urlConsultaInicial="/helium/v3/unitatOrganitzativa/suggestInici" 
					urlConsultaLlistat="/helium/v3/unitatOrganitzativa/suggest" 
					textKey="expedient.tipus.permis.form.camp.unitat.organitzativa" 
					placeholderKey="expedient.tipus.permis.form.camp.unitat.organitzativa"
					disabled="${!expedientTipus.procedimentComu}"
					labelSize="2"/>	
		</c:if>
		<hr/>
		<c:if test="${not empty permisCommand.id}">
			<form:hidden path="principalTipus"/>
			<form:hidden path="principalNom"/>	
		</c:if>
		<div class="row">
			<div class="col-sm-3"><hel:inputCheckbox name="read" textKey="permis.READ" labelSize="8" info="permis.READ.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="write" textKey="permis.WRITE" labelSize="8"  info="permis.WRITE.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="create" textKey="permis.CREATE" labelSize="8" info="permis.CREATE.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="delete" textKey="permis.DELETE" labelSize="8" info="permis.DELETE.info"/></div>
		</div>
		<c:if test="${expedientTipus.procedimentComu == false}">
			<div class="row">
				<div class="col-sm-3"><hel:inputCheckbox name="administration" textKey="permis.ADMINISTRATION" labelSize="8" info="permis.ADMINISTRATION.info"/></div>
				<div class="col-sm-3"></div>
				<div class="col-sm-3"></div>
			</div>
		</c:if>
		<hr/>
		<div class="row">
			<div class="col-sm-3"><hel:inputCheckbox name="cancel" textKey="permis.CANCEL" labelSize="8" info="permis.CANCEL.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="stop" textKey="permis.STOP" labelSize="8" info="permis.STOP.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="relate" textKey="permis.RELATE" labelSize="8" info="permis.RELATE.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="dataManagement" textKey="permis.DATA_MANAGEMENT" labelSize="8" info="permis.DATA_MANAGEMENT.info"/></div>
		</div>
		<div class="row">
			<div class="col-sm-3"><hel:inputCheckbox name="docManagement" textKey="permis.DOC_MANAGEMENT" labelSize="8" info="permis.DOC_MANAGEMENT.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="termManagement" textKey="permis.TERM_MANAGEMENT" labelSize="8" info="permis.TERM_MANAGEMENT.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="taskManagement" textKey="permis.TASK_MANAGEMENT" labelSize="8" info="permis.TASK_MANAGEMENT.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="taskSupervision" textKey="permis.TASK_SUPERVISION" labelSize="8" info="permis.TASK_SUPERVISION.info"/></div>
		</div>
		<div class="row">
			<div class="col-sm-3"><hel:inputCheckbox name="taskAssign" textKey="permis.TASK_ASSIGN" labelSize="8" info="permis.TASK_ASSIGN.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="logRead" textKey="permis.LOG_READ" labelSize="8" info="permis.LOG_READ.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="logManage" textKey="permis.LOG_MANAGE" labelSize="8" info="permis.LOG_MANAGE.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="tokenRead" textKey="permis.TOKEN_READ" labelSize="8" info="permis.TOKEN_READ.info"/></div>
		</div>
		<div class="row">
			<div class="col-sm-3"><hel:inputCheckbox name="tokenManage" textKey="permis.TOKEN_MANAGE" labelSize="8" info="permis.TOKEN_MANAGE.info"/></div>
			<c:if test="${expedientTipus.procedimentComu == false}">
				<div class="col-sm-3"><hel:inputCheckbox name="designAdmin" textKey="permis.DESIGN_ADMIN" labelSize="8" info="permis.DESIGN_ADMIN.info"/></div>
				<div class="col-sm-3"><hel:inputCheckbox name="designDeleg" textKey="permis.DESIGN_DELEG" labelSize="8" info="permis.DESIGN_DELEG.info"/></div>
			</c:if>
			<div class="col-sm-3"><hel:inputCheckbox name="scriptExe" textKey="permis.SCRIPT_EXE" labelSize="8" info="permis.SCRIPT_EXE.info"/></div>
		</div>
		<div class="row">
			<div class="col-sm-3"><hel:inputCheckbox name="undoEnd" textKey="permis.UNDO_END" labelSize="8" info="permis.UNDO_END.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="defprocUpdate" textKey="permis.DEFPROC_UPDATE" labelSize="8" info="permis.DEFPROC_UPDATE.info"/></div>
		</div>
		<hr/>
		
		<div class="alert alert-warning alert-dismissible fade in" role="alert">
			<strong><spring:message code="expedient.tipus.permis.permisos.descatalogats"/></strong>
		</div>
		<div class="row">
			<c:if test="${expedientTipus.procedimentComu == false}">
				<div class="col-sm-3"><hel:inputCheckbox name="design" textKey="permis.DESIGN" labelSize="8" info="permis.DESIGN.info"/></div>
			</c:if>
			<div class="col-sm-3"><hel:inputCheckbox name="supervision" textKey="permis.SUPERVISION" labelSize="8" info="permis.SUPERVISION.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="manage" textKey="permis.MANAGE" labelSize="8" info="permis.MANAGE.info"/></div>
			<div class="col-sm-3"><hel:inputCheckbox name="reassignment" textKey="permis.REASSIGNMENT" labelSize="8" info="permis.REASSIGNMENT.info"/></div>
		</div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
