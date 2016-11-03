<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${not empty command.id}"><
		<c:set var="titol"><spring:message code="definicio.proces.desplegar.form.titol.definicio" arguments="${definicioProces.jbpmKey}, ${definicioProces.etiqueta}, ${definicioProces.versio}"/></c:set>
	</c:when>
	<c:when test="${not empty command.expedientTipusId}"><
		<c:set var="titol"><spring:message code="definicio.proces.desplegar.form.titol.tipus" arguments="${expedientTipus.codi}, ${expedientTipus.nom}"/></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="definicio.proces.desplegar.form.titol.entorn" arguments="${entorn.codi}, ${entorn.nom}"/></c:set>
	</c:otherwise>
</c:choose>

<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>		
	<form:form id="desplegar-form" cssClass="form-horizontal" action="desplegar" enctype="multipart/form-data" method="post" commandName="command" style="min-height: 500px;">

		<div class="inlineLabels">
		
			<script type="text/javascript">
				// <![CDATA[
				$(document).ready( function() {
					actualitzaControls();
					$('#accio').change(function() {
						actualitzaControls();
					})
				}); 	
				
				function actualitzaControls() {
					$("#actualitzarExpedientsActius,#etiqueta,#expedientTipusId").prop('disabled', $("#accio").val() != "JBPM_DESPLEGAR");
				}
				// ]]>
			</script>			
		</div>
		
		<input type="hidden" name="entornId" id="entornId" value="${command.entornId}" />
		<input type="hidden" name="id" id="id" value="${command.id}" />		
		<div class="form-group">
			<label class="control-label col-xs-4 obligatori" for="file"><spring:message code="definicio.proces.desplegar.form.file"/></label>
			<div class="col-xs-8">
				<input type="file" name="file" id="file" />
				<c:set var="fileErrors"><form:errors path="file"/></c:set>
				<c:if test="${not empty fileErrors}">
					<div class="has-error">
						<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="file"/></p>
					</div>
				</c:if>
			</div>
		</div>
		<hel:inputSelect emptyOption="false" name="accio" textKey="definicio.proces.desplegar.form.accio" optionItems="${accionsJbpm}" optionValueAttribute="codi" optionTextAttribute="valor"/>
		<hel:inputSelect emptyOption="true" name="expedientTipusId" textKey="definicio.proces.desplegar.form.tipusExpedient" placeholderKey="definicio.proces.desplegar.form.tipusExpedient.placeholder" optionItems="${expedientTipusAccessibles}" optionValueAttribute="id" optionTextAttribute="nom"/>
		<hel:inputText name="etiqueta" textKey="definicio.proces.desplegar.form.etiqueta" />
		<hel:inputCheckbox name="actualitzarExpedientsActius" textKey="definicio.proces.desplegar.form.actualitzarExpedientsActius" />
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button id="desplegarButton" type="submit" class="btn btn-success right">
				<span class="fa fa-download"></span> <spring:message code="comu.filtre.desplegar"/>
				<span class="desplegar processant" style="visibility:hidden;">
					<span class="fa fa-spinner fa-spin fa-fw" title="<spring:message code="comu.processant"/>..."></span><span class="sr-only">&hellip;</span>
				</span>			
			</button>
		</div>
	</form:form>
</body>
</html>