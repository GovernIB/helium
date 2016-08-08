<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${empty expedientTipusRedireccioCommand.id}"><
		<c:set var="titol"><spring:message code="expedient.tipus.redireccio.form.titol.nova"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.redireccio.form.titol.modificar"/></c:set>
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
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
</head>
<body>		
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusRedireccioCommand">
		<div style="height: 400px">        
			<input type="hidden" name="id" value="${expedientTipusRedireccioCommand.id}"/>
			<hel:inputSelect required="true" name="usuariOrigen" textKey="expedient.tipus.redireccio.form.usuariOrigen" placeholderKey="expedient.tipus.redireccio.form.usuariOrigen" optionItems="${persones}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputSelect required="true" name="usuariDesti" textKey="expedient.tipus.redireccio.form.usuariDesti" placeholderKey="expedient.tipus.redireccio.form.usuariDesti" optionItems="${persones}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputDate required="true" name="dataInici" textKey="expedient.tipus.redireccio.form.dataInici" placeholder="dd/mm/aaaa" />
			<hel:inputDate required="true" name="dataFi" textKey="expedient.tipus.redireccio.form.dataFi" />
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:choose>
				<c:when test="${empty expedientTipusRedireccioCommand.id}">
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
		
	<script type="text/javascript">
		// <![CDATA[
		$(document).ready(function() {			
		});
		// ]]>
	</script>			

	</form:form>
</body>
</html>