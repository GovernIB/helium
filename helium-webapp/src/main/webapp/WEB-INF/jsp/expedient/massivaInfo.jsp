<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<c:set var="sessionCommand" value="${sessionScope.expedientTipusConsultaDissenyCommandTE}"/>

<html>
<head>
	<title><fmt:message key="expedient.massiva.titol"/></title>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function mostrarOcultar(img, objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		obj.style.display = "block";
		img.src = '<c:url value="/img/magnifier_zoom_out.png"/>';
	} else {
		obj.style.display = "none";
		img.src = '<c:url value="/img/magnifier_zoom_in.png"/>';
	}
}
function confirmarCanviVersio(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="expedient.massiva.confirm_canviar_versio_proces"/>");
}
function confirmarExecutarAccio(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="expedient.massiva.confirm_exec_accio"/>");
}



function massiva(e){

	if("<%=request.getAttribute("javax.servlet.forward.request_uri")%>" == "/helium/expedient/massivaInfoTE.html" )
	{
			$("#massiva").attr("action","consultaDisseny.html");
			$("#target").val("disseny");		
	}
	else if("<%=request.getAttribute("javax.servlet.forward.request_uri")%>" == "/helium/expedient/massivaInfo.html"){
		$("#massiva").attr("action","consulta.html");
		$("#target").val("consulta");
	}
}


// ]]>
</script>
</head>
<body>

	<div class="missatgesBlau">
		<h3 class="titol-tab titol-massiva"><fmt:message key="expedient.massiva.info"/> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key="expedient.massiva.mos_ocul"/>" title="<fmt:message key="expedient.massiva.mos_ocul"/>" border="0" onclick="mostrarOcultar(this,'info-massiva')"/></h3>
		<div id="info-massiva" style="display:none">
			<display:table name="expedients" id="registre" requestURI="" class="displaytag selectable">
				<c:set var="filaStyle" value=""/>
				<c:if test="${registre.anulat}"><c:set var="filaStyle" value="text-decoration:line-through"/></c:if>
				<display:column property="identificador" title="Expedient" style="${filaStyle}"/>
				<display:column property="dataInici" title="Iniciat el" format="{0,date,dd/MM/yyyy HH:mm}" style="${filaStyle}"/>
				<display:column property="tipus.nom" title="Tipus" style="${filaStyle}"/>
				<display:column title="Estat" style="${filaStyle}" sortProperty="estat.nom">
					<c:if test="${registre.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
					<c:choose>
						<c:when test="${empty registre.dataFi}">
							<c:choose><c:when test="${empty registre.estat}"><fmt:message key="expedient.consulta.iniciat"/></c:when><c:otherwise>${registre.estat.nom}</c:otherwise></c:choose>
						</c:when>
						<c:otherwise><fmt:message key="expedient.consulta.finalitzat"/></c:otherwise>
					</c:choose>
				</display:column>
			</display:table>
			<form id="massiva" action="consulta.html" class="uniForm" onclick="javascript:massiva(event)">
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><fmt:message key="expedient.massiva.canvi"/></c:param>
				</c:import>
			</form>
		</div>
	</div>

	<h3 class="titol-tab titol-canvi-versio">
		<fmt:message key="expedient.massiva.actualitzar"/>
	</h3>
	<form:form action="massivaCanviVersio.html" cssClass="uniForm" commandName="canviVersioProcesCommand" onsubmit="return confirmarCanviVersio(event)" onclick="javascript:massiva(event)">
		<input type="hidden" id="target" name="target" value="">
		<div class="inlineLabels">
			<c:set var="definicionsProces" value="${definicioProces.jbpmIdsAmbDescripcio}" scope="request"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="definicioProcesId"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="definicionsProces"/>
				<c:param name="itemLabel" value="descripcio"/>
				<c:param name="itemValue" value="jbpmId"/>
				<c:param name="label"><fmt:message key="expedient.eines.canviar_versio"/></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit</c:param>
			<c:param name="titles"><fmt:message key="comuns.canviar_versio"/></c:param>
		</c:import>
	</form:form>

	<h3 class="titol-tab titol-canvi-versio">
		<fmt:message key="expedient.massiva.accions"/>
	</h3>
	<c:set var="hiHaAccions" value="${false}"/>
	<c:set var="hiHaAccionsPubliques" value="${false}"/>
	<c:forEach var="accio" items="${instanciaProces.definicioProces.accions}">
		<c:if test="${not accio.oculta}">
			<c:set var="hiHaAccions" value="${true}"/>
			<c:if test="${accio.publica}"><c:set var="hiHaAccionsPubliques" value="${true}"/></c:if>
		</c:if>
	</c:forEach>
	<c:set var="tePermisAccions" value="${false}"/>
	<security:accesscontrollist domainObject="${instanciaProces.expedient.tipus}" hasPermission="16,2"><c:set var="tePermisAccions" value="${true}"/></security:accesscontrollist>
	<c:if test="${hiHaAccionsPubliques || (hiHaAccions && tePermisAccions)}">
		<form action="massivaExecutarAccio.html" method="post" onsubmit="return confirmarExecutarAccio(event)">
			<dl class="form-info">
				<c:forEach var="accio" items="${instanciaProces.definicioProces.accions}">
					<c:if test="${not accio.oculta && (accio.publica || tePermisAccions)}">
						<dt>${accio.nom}:</dt><dd><button type="submit" class="submitButton" name="submit" value="${accio.codi}" onclick="saveAction(this, 'prova2');"><fmt:message key="expedient.massiva.exec_accio"/></button></dd>
					</c:if>
				</c:forEach>
			</dl>
		</form>
	</c:if>

</body>
</html>
