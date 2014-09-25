<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="expedient.iniciar.disponibles"/></title>
	<hel:modalHead/>
		<style>
			body {background-image: none; padding-top: 0px;}
		</style>
	<meta name="capsaleraTipus" content="llistat"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
var comprovacio = new Array();<c:forEach var="et" items="${expedientTipus}">
comprovacio["${et.id}"] = new Array(<c:forEach var="id" items="${definicionsProces[et.id].idsWithSameKey}" varStatus="status"><c:choose><c:when test="${(et.teNumero and et.demanaNumero) or (et.teTitol and et.demanaTitol) or definicionsProces[et.id].hasStartTaskWithSameKey[status.index]}">true</c:when><c:otherwise>false</c:otherwise></c:choose><c:if test="${not status.last}">,</c:if></c:forEach>);</c:forEach>
function confirmar(e, form) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	if (!comprovacio[form.expedientTipusId.value][form.definicioProcesId.selectedIndex])
		return confirm("<spring:message code='expedient.iniciar.confirm_iniciar' />");
	else
		return true;
}
// ]]>
</script>
</head>
<body>
	<display:table name="expedientTipus" id="registre" requestURI="" class="table table-striped table-bordered">
		<display:column property="codi" title="Codi"/>
		<display:column property="nom" title="Nom"/>
		<display:column>
			<form class="form-init-exedient" action="<c:url value="/modal/v3/expedient/iniciar"/>" method="post" onsubmit="return confirmar(event, this)">
				<input type="hidden" name="expedientTipusId" value="${registre.id}"/>
				<select name="definicioProcesId" id="definicioProcesId" class="span9">
					<option value="${definicionsProces.id}">&lt;&lt; <spring:message code='expedient.iniciar.darrera_versio' /> &gt;&gt;</option>
					<c:forEach var="id" items="${definicionsProces[registre.id].idsWithSameKey}" varStatus="status">
						<option value="${id}">${definicionsProces[registre.id].idsMostrarWithSameKey[status.index]}</option>
					</c:forEach>
				</select>
				<button type="submit" class="btn btn-primary pull-right"><spring:message code='comuns.iniciar' /></button>
			</form>
		</display:column>
	</display:table>
	<script>
		$("select").select2({
		    width: 'calc(100% - 71px)',
		    allowClear: true
		});
	</script>
	<div id="modal-botons" class="well">
		<button type="button" class="modal-tancar btn btn-default" name="submit" value="cancel">
			<spring:message code='comuns.cancelar' />
		</button>
	</div>
</body>
</html>
