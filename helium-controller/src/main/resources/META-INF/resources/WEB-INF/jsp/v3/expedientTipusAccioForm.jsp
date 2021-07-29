<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${!heretat}">
		<c:choose>
			<c:when test="${empty expedientTipusAccioCommand.id}"><
				<c:set var="titol"><spring:message code="expedient.tipus.accio.form.titol.nova"/></c:set>
				<c:set var="formAction">new</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="titol"><spring:message code="expedient.tipus.accio.form.titol.modificar"/></c:set>
				<c:set var="formAction">update</c:set>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.accio.form.titol.visualitzar"/></c:set>
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
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" modelAttribute="expedientTipusAccioCommand">
		<div>        
			<input type="hidden" name="id" value="${expedientTipusAccioCommand.id}"/>
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.accio.form.accio.codi" />
			<hel:inputText required="true" name="nom" textKey="expedient.tipus.accio.form.accio.nom" />
			<c:if test="${not empty expedientTipusAccioCommand.expedientTipusId}">
				<hel:inputSelect required="true" name="defprocJbpmKey" textKey="expedient.tipus.accio.form.accio.defprocJbpmKey" emptyOption="true" placeholderKey="expedient.tipus.accio.form.accio.defprocJbpmKey.placeholder" optionItems="${definicionsProces}" />
				<hel:inputText required="true" name="jbpmAction" textKey="expedient.tipus.accio.form.accio.jbpmAction" />
			</c:if>
			<c:if test="${not empty expedientTipusAccioCommand.definicioProcesId}">
				<input type="hidden" name="defprocJbpmKey" value="${expedientTipusAccioCommand.defprocJbpmKey}" />
				<hel:inputSelect required="true" name="jbpmAction" textKey="expedient.tipus.accio.form.accio.jbpmAction" optionItems="${handlers}" />
			</c:if>
			<hel:inputTextarea name="descripcio" textKey="expedient.tipus.accio.form.accio.descripcio" />
			<hel:inputCheckbox name="publica" textKey="expedient.tipus.accio.form.accio.publica" />
			<hel:inputCheckbox name="oculta" textKey="expedient.tipus.accio.form.accio.oculta" />
			<hel:inputText name="rols" textKey="expedient.tipus.accio.form.accio.rols" />
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:if test="${!heretat}">
				<c:choose>
					<c:when test="${empty expedientTipusAccioCommand.id}">
						<button class="btn btn-primary right" type="submit" name="accio" value="crear">
							<span class="fa fa-plus"></span> <spring:message code='comu.boto.crear' />
						</button>
					</c:when>
					<c:otherwise>
						<button class="btn btn-primary right" type="submit" name="accio" value="modificar">
							<span class="fa fa-pencil"></span> <spring:message code='comu.boto.modificar' />
						</button>
					</c:otherwise>
				</c:choose>
			</c:if>
		</div>
		
	<script type="text/javascript">
		// <![CDATA[
		$(document).ready(function() {			
   			//<c:if test="${heretat}">
			webutilDisableInputs($('#expedientTipusAccioCommand'));
			//</c:if>
		});
		// ]]>
	</script>			

	</form:form>
</body>
</html>