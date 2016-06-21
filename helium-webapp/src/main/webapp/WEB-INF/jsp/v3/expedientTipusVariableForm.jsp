<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${empty expedientTipusCampCommand.id}"><
		<c:set var="titol"><spring:message code="expedient.tipus.camp.form.titol.nou"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.camp.form.titol.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
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
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusCampCommand">
		<div>
        
			<script type="text/javascript">
				// <![CDATA[
				// ]]>
			</script>			
			<input type="hidden" name="id" value="${expedientTipusCampCommand.id}"/>
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.camp.form.camp.codi" />
			<hel:inputSelect required="true" emptyOption="true" name="tipus" textKey="expedient.tipus.camp.form.camp.tipus" placeholderKey="expedient.tipus.camp.form.camp.tipus" optionItems="${tipusCamp}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputText required="true" name="etiqueta" textKey="expedient.tipus.camp.form.camp.etiqueta" />
			<hel:inputTextarea name="observacions" textKey="expedient.tipus.camp.form.camp.observacions" />
			<hel:inputSelect required="false" emptyOption="true" name="agrupacioId" textKey="expedient.tipus.camp.form.camp.agrupacio" placeholderKey="expedient.tipus.camp.form.camp.agrupacio" optionItems="${agrupacions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputCheckbox name="multiple" textKey="expedient.tipus.camp.form.camp.multiple" />
			<hel:inputCheckbox name="ocult" textKey="expedient.tipus.camp.form.camp.ocult" />
			<hel:inputCheckbox name="ignored" textKey="expedient.tipus.camp.form.camp.ignored" />
		</div>
		
		<fieldset>
			<legend><spring:message code="expedient.tipus.camp.form.fieldset.consulta"></spring:message></legend>
		</fieldset>
		
		<fieldset>
			<legend><spring:message code="expedient.tipus.camp.form.fieldset.accio"></spring:message></legend>
		</fieldset>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:choose>
				<c:when test="${empty expedientTipusCampCommand.id}">
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
	
		</div>

	</form:form>
</body>
</html>