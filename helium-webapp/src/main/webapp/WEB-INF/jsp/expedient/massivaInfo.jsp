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
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
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
function confirmarScript(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='expedient.eines.confirm_executar_script_proces' />");
}
function confirmarAturar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='expedient.eines.confirm_aturar_tramitacio' />");
}
function confirmarModificarDocument(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='expedient.eines.confirm_modificar_document' />");
}
function confirmarModificarVariables(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='expedient.eines.confirm_modificar_variable' />");
}


function massiva(e){

	if("<%=request.getAttribute("javax.servlet.forward.request_uri")%>" == "/helium/expedient/massivaInfoTE.html" )
	{
			$("#massiva").attr("action","consultaDisseny.html");
			$("#target").val("disseny");
			$("#target2").val("disseny");

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
	<div class="uniForm">
		<div class="inlineLabels col first">
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key="expedient.massiva.actualitzar"/></h3>
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
	
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key="expedient.massiva.executarScriptMas"/></h3>
			<c:set var="tePermisExecutarScript" value="${false}"/>
			<security:accesscontrollist domainObject="${instanciaProces.expedient.tipus}" hasPermission="16,2"><c:set var="tePermisAccions" value="${true}"/></security:accesscontrollist>
			<c:if test="${tePermisAccions}">
				<form:form action="/helium/expedient/scriptMas.html" cssClass="uniForm" commandName="scriptCommandMas" onsubmit="return confirmarScript(event)">
						<div class="inlineLabels">
							<input type="hidden" name="id" value="${instanciaProces.id}"/>
							<c:import url="../common/formElement.jsp">
								<c:param name="property">script</c:param>
								<c:param name="required">${true}</c:param>
								<c:param name="type" value="textarea"/>
								<c:param name="label"><fmt:message key='expedient.eines.script' /></c:param>
							</c:import>
						</div>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">submit</c:param>
							<c:param name="titles"><fmt:message key='comuns.executar' /></c:param>
						</c:import>
					</form:form>
			</c:if>
		
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key='expedient.eines.aturar_tramitacio' /></h3>
			<form:form action="/helium/expedient/aturarMas.html" cssClass="uniForm" commandName="aturarCommandMas" onsubmit="return confirmarAturar(event)">
				<div class="inlineLabels">
					<input type="hidden" name="id" value="${instanciaProces.id}"/>
					<c:import url="../common/formElement.jsp">
						<c:param name="property">motiu</c:param>
						<c:param name="required">${true}</c:param>
						<c:param name="type" value="textarea"/>
						<c:param name="label"><fmt:message key='expedient.eines.motiu' /></c:param>
					</c:import>
				</div>
					<c:import url="../common/formElement.jsp">
						<c:param name="type" value="buttons"/>
						<c:param name="values">submit</c:param>
						<c:param name="titles"><fmt:message key='comuns.aturar' /></c:param>
					</c:import>
			</form:form>
		</div>
		<div class="inlineLabels uniForm col last">
			<c:set var="hiHaAccions" value="${false}"/>
			<c:set var="hiHaAccions" value="${fn:length(instanciaProces.definicioProces.accions) > 0}"/>
			<c:if test="${hiHaAccions}">
				<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key="expedient.massiva.accions"/></h3>
			
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
					<form:form action="massivaExecutarAccio.html" cssClass="uniForm" commandName="execucioAccioCommand" onsubmit="return confirmarExecutarAccio(event)">
						<input type="hidden" id="target2" name="target2" value="">
						<div class="inlineLabels">
							<c:set var="accions" value="${instanciaProces.definicioProces.accions}" scope="request"/>
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="accioId"/>
								<c:param name="type" value="select"/>
								<c:param name="items" value="accions"/>
								<c:param name="itemLabel" value="nom"/>
								<c:param name="itemValue" value="codi"/>
								<c:param name="label"><fmt:message key="expedient.massiva.exec_accio"/></c:param>
							</c:import>
						</div>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">submit</c:param>
							<c:param name="titles"><fmt:message key='comuns.executar' /></c:param>
						</c:import>
					</form:form>
				</c:if>
			</c:if>
			
			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key='expedient.massiva.modificar_variables' /></h3>
			<form:form action="/helium/expedient/dadaModificarMas.html" method="GET" cssClass="uniForm" commandName="modificarVariablesMasCommand" onsubmit="return confirmarModificarVariables(event)">
				<div class="inlineLabels">
					<input type="hidden" name="id" value="${instanciaProces.id}"/>
					<c:set var="variables" value="${instanciaProces.camps}" scope="request"/>
						<c:import url="../common/formElement.jsp">
							<c:param name="property">var</c:param>
							<c:param name="type" value="select"/>
							<c:param name="items" value="variables"/>
							<c:param name="itemLabel" value="codi"/>
							<c:param name="itemValue" value="codi"/>
							<c:param name="itemBuit">&lt;&lt; <fmt:message key='expedient.consulta.select.variable'/> &gt;&gt;</c:param>
							<c:param name="label"><fmt:message key="expedient.eines.modificar_variables"/></c:param>
						</c:import>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">submit</c:param>
							<c:param name="titles"><fmt:message key='comuns.modificar'/></c:param>
						</c:import>
				</div>
			</form:form>
<%-- 			<h3 class="titol-tab titol-canvi-versio mass"><fmt:message key="expedient.massiva.documents"/></h3> --%>
<%-- 			<form:form action="documentModificarMas.html" cssClass="uniForm" method="GET" commandName="execucioAccioCommand" onsubmit="return confirmarModificarDocument(event)"> --%>
<%-- 				<input type="hidden" name="id" value="${instanciaProces.id}"/> --%>
<%-- 				<c:set var="documents" value="${instanciaProces.documents}" scope="request"/> --%>
<!-- 				<div class="inlineLabels"> -->
<%-- 					<c:set var="accions" value="${instanciaProces.definicioProces.accions}" scope="request"/> --%>
<%-- 					<c:import url="../common/formElement.jsp"> --%>
<%-- 						<c:param name="property">accioId</c:param> --%>
<%-- 						<c:param name="type" value="select"/> --%>
<%-- 						<c:param name="items" value="documents"/> --%>
<%-- 						<c:param name="itemLabel" value="nom"/> --%>
<%-- 						<c:param name="itemValue" value="codi"/> --%>
<%-- 						<c:param name="itemBuit">&lt;&lt; <fmt:message key='expedient.consulta.select.variable'/> &gt;&gt;</c:param> --%>
<%-- 						<c:param name="label"><fmt:message key="expedient.massiva.documents"/></c:param> --%>
<%-- 					</c:import> --%>
<!-- 				</div> -->
<%-- 				<c:import url="../common/formElement.jsp"> --%>
<%-- 					<c:param name="type" value="buttons"/> --%>
<%-- 					<c:param name="values">submit</c:param> --%>
<%-- 					<c:param name="titles"><fmt:message key='comuns.modificar' /></c:param> --%>
<%-- 				</c:import> --%>

<%-- 			</form:form> --%>
		</div>

					
</body>


</html>
