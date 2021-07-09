<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="expedient.tipus.definicioProces.incorporar.titol"/></title>
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
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusDefinicioProcesImportarCommand">
		<div>        
			<hel:inputSelect name="definicioProcesId" required="false" emptyOption="true" textKey="expedient.tipus.definicioProces.incorporar.versio" placeholderKey="expedient.tipus.definicioProces.incorporar.versio.placeholder" optionItems="${versions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputCheckbox name="sobreescriure" textKey="expedient.tipus.definicioProces.incorporar.sobreescriure" comment="expedient.tipus.definicioProces.incorporar.sobreescriure.info"/>
			<hel:inputCheckbox name="tasques" textKey="expedient.tipus.definicioProces.incorporar.tasques" disabled="${!potCanviarTasques}" comment="expedient.tipus.definicioProces.incorporar.tasques.info" />
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<button class="btn btn-primary right" type="submit" name="accio" value="incorporar">
				<span class="fa fa-download"></span> <spring:message code='expedient.tipus.definicioProces.incorporar.accio.incorporar' />
			</button>	
		</div>
		<div style="height: 200px;">
		</div>
		
	<script type="text/javascript">
		// <![CDATA[
		$(document).ready(function() {			
		});
		// ]]>
	</script>			

	</form:form>
</body>
</html>