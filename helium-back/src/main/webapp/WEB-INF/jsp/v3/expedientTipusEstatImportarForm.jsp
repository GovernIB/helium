<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="expedient.tipus.estat.importar.form.titol"/></title>
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
	<form:form cssClass="form-horizontal" action="importar" enctype="multipart/form-data" method="post" commandName="importarDadesCommand" >
				
		<div class="form-group">
			<label class="control-label col-xs-4 obligatori" for="multipartFile"><spring:message code="expedient.tipus.enumeracio.valors.importar.arxiu"/></label>
			<div class="col-xs-8">
				<input type="file" name="multipartFile" id="multipartFile" />
				<c:set var="fileErrors"><form:errors path="multipartFile"/></c:set>
				<c:if test="${not empty fileErrors}">
					<div class="has-error">
						<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="multipartFile"/></p>
					</div>
				</c:if>
			</div>
		</div>
		<hel:inputCheckbox name="eliminarValorsAntics" textKey="expedient.tipus.enumeracio.valors.importar.eliminar" />

		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<button class="btn btn-primary right" type="submit" name="accio" value="importar">
				<span class="fa fa-sign-in"></span> <spring:message code='comu.importar' />
			</button>	
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