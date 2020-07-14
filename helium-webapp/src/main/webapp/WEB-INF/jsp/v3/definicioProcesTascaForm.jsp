<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${!heretat}">
		<c:set var="titol"><spring:message code="definicio.proces.tasca.form.titol.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="definicio.proces.tasca.form.titol.visualitzar"/></c:set>
		<c:set var="formAction">none</c:set>		
	</c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
</head>
<body>		
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="definicioProcesTascaCommand">
		<div>        
		
			<input type="hidden" name="id" value="${definicioProcesTascaCommand.id}"/>

			<hel:inputText name="jbpmName" textKey="definicio.proces.tasca.form.jbpmName" disabled="true" />
			<hel:inputText required="true" name="nom" textKey="definicio.proces.tasca.form.nom" />
			<hel:inputText name="missatgeInfo" textKey="definicio.proces.tasca.form.missatgeInfo" />
			<hel:inputText name="missatgeWarn" textKey="definicio.proces.tasca.form.missatgeWarn" />
			<hel:inputTextarea name="nomScript" textKey="definicio.proces.tasca.form.nomScript" />
			<p id="nomScriptNota" class="help-block"><spring:message code="definicio.proces.tasca.form.nomScript.nota"></spring:message></p>
			<hel:inputText name="recursForm" textKey="definicio.proces.tasca.form.recursForm" />
			<hel:inputText name="formExtern" textKey="definicio.proces.tasca.form.formExtern" />
			<hel:inputCheckbox name="expressioDelegacio" textKey="definicio.proces.tasca.form.expressioDelegacio" />
			<hel:inputCheckbox name="tramitacioMassiva" textKey="definicio.proces.tasca.form.tramitacioMassiva" />
			<hel:inputCheckbox name="finalitzacioSegonPla" textKey="definicio.proces.tasca.form.finalitzacioSegonPla" />
			<hel:inputCheckbox name="ambRepro" textKey="expedient.tipus.camp.form.camp.ambrepro" />
			<hel:inputCheckbox name="mostrarAgrupacions" textKey="expedient.tipus.camp.form.camp.mostraragrupacions" />
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:if test="${!heretat}">
				<button class="btn btn-primary right" type="submit" name="accio" value="modificar">
					<span class="fa fa-pencil"></span> <spring:message code='comu.boto.modificar' />
				</button>	
			</c:if>
		</div>
		
	<script type="text/javascript">
		// <![CDATA[
		$(document).ready(function() {
			$('#nomScriptNota').insertAfter('#nomScript');
		});	
		// ]]>
	</script>			

	</form:form>
</body>
</html>