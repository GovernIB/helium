<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${document.adjunt}">
		<c:set var="titol"><spring:message code="expedient.document.firmaPassarela.titol.adjunt" arguments="${document.adjuntTitol}"/></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.document.firmaPassarela.titol.document" arguments="${document.documentNom}"/></c:set>
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
<script type="text/javascript">
// <![CDATA[
$(document).ready( function() {
	
}); 
// ]]>
</script>
</head>
<body>
	<c:if test="${potFirmar}">
		<c:set var="formAction">
			<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/firmaPassarela"/>
		</c:set>
		<form:form 	cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="documentExpedientFirmaPassarelaCommand">
			<div>
				<hel:inputTextarea required="true" name="motiu" textKey="expedient.document.firmaPassarela.camp.motiu"/>
			</div>
			<div id="modal-botons" class="well">
				<c:if test="${potFirmar}">
					<button type="submit" class="btn btn-success"><span class="fa fa-pencil-square-o"></span> <spring:message code="expedient.document.firmaPassarela.boto.firmar"/></button>
				</c:if>			
				<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			</div>
		</form:form>
	</c:if>
	<c:if test="${!potFirmar}">
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
		</div>
	</c:if>
</body>
</html>