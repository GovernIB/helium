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
var comprovacio = new Array();
<c:forEach var="et" items="${expedientTipus}">
	comprovacio["${et.id}"] = new Array(
		<c:forEach var="df" items="${definicionsProces[et.id].listIdAmbEtiqueta}" varStatus="status">
			<c:choose>
				<c:when test="${df.demanaNumeroTitol or df.hasStartTask}">
					true
				</c:when>
				<c:otherwise>
					false
				</c:otherwise>
			</c:choose>
			<c:if test="${not status.last}">,</c:if>
		</c:forEach>
	);
</c:forEach>

function confirmar(e, form) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	if (!comprovacio[form.expedientTipusId.value][form.definicioProcesId.selectedIndex])
		return confirm("<spring:message code='expedient.iniciar.confirm_iniciar' />");
	else
		return true;
}

$(document).ready( function() {	
	// Botó per redirigir al formulari d'alta massiva per CSV
	$("button[name=altaMassiva]").click(function() {
		window.location.href = "<c:url value="/modal/v3/expedient/altaMassiva"></c:url>";
	});
});

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
				<c:if test="${registre.tipus == 'FLOW' }">
					<select name="definicioProcesId" id="definicioProcesId" class="span9">
						<option value="">&lt;&lt; <spring:message code='expedient.iniciar.darrera_versio' /> &gt;&gt;</option>
						<c:forEach var="df" items="${definicionsProces[registre.id].listIdAmbEtiqueta}" varStatus="status">
							<option value="${df.id}">${df.etiqueta}</option>
						</c:forEach>
					</select>
				</c:if>
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
		<c:if test="${dadesPersona.admin || potExecutarScripts}">
			<button type="button" class="btn btn-primary" name="altaMassiva" value="altaMassiva"><i class="fa fa-file-o"></i> <spring:message code="expedient.iniciar.alta.csv"></spring:message> ...</button>
		</c:if>
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
