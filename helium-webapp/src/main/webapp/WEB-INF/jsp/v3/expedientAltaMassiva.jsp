<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>


<script type="text/javascript">
// <![CDATA[         
$(document).ready(function() {
	
});
// ]]>
</script>	

<html>
<head>
	<title><spring:message code="expedient.alta.massiva.titol"/></title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	
</head>
<body>		

	<form:form cssClass="form-horizontal" action="altaMassiva" enctype="multipart/form-data" method="post" commandName="expedientAltaMassivaCommand">

		<div>
			<input type="hidden" name="id" value="${expedientTipusEnumeracioCommand.id}"/>
			<hel:inputSelect required="true" emptyOption="false" name="expedientTipusId" textKey="expedient.llistat.filtre.camp.expedient.tipus" placeholderKey="expedient.llistat.filtre.camp.expedient.tipus" optionItems="${expedientTipusPermesos}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${not empty expedientTipusActual}" inline="false"/>
			
			<hel:inputText 		required="true" name="codi"		textKey="expedient.tipus.enumeracio.form.camp.codi" />
			<hel:inputText 		required="true" name="nom"		textKey="expedient.tipus.enumeracio.form.camp.nom" />
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
		</div>

	</form:form>
	
	<script type="text/javascript">
		// <![CDATA[
		$(document).ready( function() {
			//<c:if test="${heretat}">
			webutilDisableInputs($('#expedientTipusEnumeracioCommand'));
			//</c:if>
		}); 
		// ]]>
	</script>	
</body>
</html>