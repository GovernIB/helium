<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${empty interessatCommand.id}">
		<c:set var="titol"><spring:message code="interessat.form.titol.nou"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="interessat.form.titol.modificar"/></c:set>
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

<script>
	$(document).ready(function() {
		$('#tipus').on('change', function() {
	 		if (this.value == '<%=net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto.FISICA%>') {
	 			$("label[for='llinatge1']").addClass('obligatori');
	 	 	} else{
	 	 		$("label[for='llinatge1']").removeClass('obligatori');
	 	 	}
		});
	});
</script>

</head>
<body>
	<form:form cssClass="form-horizontal" action="${formAction}"  method="post" commandName="interessatCommand">
		<form:hidden id="id" path="id"/>
		<hel:inputText required="true" name="codi" textKey="interessat.form.camp.codi" />
		<hel:inputText required="true" name="nif" textKey="interessat.form.camp.nif" />
		<hel:inputText required="true" name="nom" textKey="interessat.form.camp.nom" />
		<c:choose>
			<c:when test="${interessatCommand.tipus=='FISICA'}">
				<hel:inputText name="llinatge1" textKey="interessat.form.camp.llinatge1" required="true"/>
			</c:when>
			<c:otherwise>
				<hel:inputText name="llinatge1" textKey="interessat.form.camp.llinatge1"/>			
			</c:otherwise>
		</c:choose>
		<hel:inputText name="llinatge2" textKey="interessat.form.camp.llinatge2" />
		<hel:inputText name="email" textKey="interessat.form.camp.email" />		
		<hel:inputText name="telefon" textKey="interessat.form.camp.telefon" />

		<hel:inputSelect required="true" name="tipus"
			optionItems="${interessatTipusEstats}" optionValueAttribute="valor"
			optionTextAttribute="codi" textKey="interessat.form.camp.tipus" />


		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button type="submit" class="btn btn-success right">
				<span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/>
			</button>
		</div>
	</form:form>
</body>
</html>
