<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><fmt:message key="alerta.llistat.expedient"/>: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key="comuns.consultes"/>" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/ui/ui.core.js"/>"></script>
	<script  type="text/javascript" src="<c:url value="/js/jquery/ui/jquery-ui-1.7.2.custom.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
function mostrarLogsRetrocedits(element) {
	var amplada = 960;
	var alcada = 400;
	$('<iframe id="verificacio" src="' + element.href +'"/>').dialog({
		title: "<fmt:message key='expedient.log.retroces.llistat' />",
		autoOpen: true,
		modal: true,
		autoResize: true,
		width: parseInt(amplada),
		height: parseInt(alcada)
	}).width(amplada - 30).height(alcada - 30);
}
function confirmarRetrocedir(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="expedient.log.confirm.retrocedir"/>");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="registre"/>
	</c:import>

	<h3 class="titol-tab titol-registre">
		<fmt:message key="expedient.log"/>
	</h3>
	<c:set var="numBloquejos" value="${0}"/>
	<c:forEach var="log" items="${logs}">
		<c:if test="${log.estat == 'BLOCAR'}"><c:set var="numBloquejos" value="${numBloquejos + 1}"/></c:if>
	</c:forEach>
	<display:table name="logs" id="registre" class="displaytag">
		<c:set var="cellStyle" value=""/>
		<c:if test="${registre.estat == 'RETROCEDIT'}"><c:set var="cellStyle" value="text-decoration:line-through"/></c:if>
		<display:column property="data" titleKey="expedient.document.data" format="{0,date,dd/MM/yyyy'&nbsp;'HH:mm:ss}" style="${cellStyle}"/>
		<display:column property="usuari" titleKey="expedient.editar.responsable" style="${cellStyle}"/>
		<display:column titleKey="expedient.log.objecte" style="${cellStyle}">
			<c:choose>
				<c:when test="${registre.targetTasca}"><fmt:message key="expedient.log.objecte.TASCA"/>: ${tasques[registre.targetId].nom}</c:when>
				<c:when test="${registre.targetProces}"><fmt:message key="expedient.log.objecte.PROCES"/>: ${registre.targetId}</c:when>
				<c:when test="${registre.targetExpedient}"><fmt:message key="expedient.log.objecte.EXPEDIENT"/></c:when>
				<c:otherwise>???: ${registre.targetId}</c:otherwise>
			</c:choose>
		</display:column>
		<display:column titleKey="expedient.log.accio" style="${cellStyle}">
			<fmt:message key="expedient.log.accio.${registre.accioTipus}"/>
		</display:column>
		<display:column titleKey="expedient.log.info" style="${cellStyle}">
			<c:choose>
				<c:when test="${registre.accioTipus == 'PROCES_VARIABLE_CREAR'}"><fmt:message key="expedient.log.info.variable"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_VARIABLE_MODIFICAR'}"><fmt:message key="expedient.log.info.variable"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_VARIABLE_ESBORRAR'}"><fmt:message key="expedient.log.info.variable"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_DOCUMENT_AFEGIR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_DOCUMENT_MODIFICAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_DOCUMENT_ESBORRAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_DOCUMENT_ADJUNTAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_SCRIPT_EXECUTAR'}"><fmt:message key="expedient.log.info.accio"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_REASSIGNAR'}"><fmt:message key="expedient.log.info.abans"/>: ${fn:split(registre.accioParams, "::")[0]}, <fmt:message key="expedient.log.info.despres"/>: ${fn:split(registre.accioParams, "::")[1]}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_ACCIO_EXECUTAR'}"><fmt:message key="expedient.log.info.accio"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_DOCUMENT_AFEGIR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_DOCUMENT_MODIFICAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_DOCUMENT_ESBORRAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_DOCUMENT_SIGNAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_COMPLETAR'}"><c:if test="${not empty registre.accioParams}"><fmt:message key="expedient.log.info.opcio"/>: ${registre.accioParams}</c:if></c:when>
				<c:when test="${registre.accioTipus == 'EXPEDIENT_ATURAR'}"><fmt:message key="expedient.log.info.missatge"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'EXPEDIENT_ACCIO'}"><fmt:message key="expedient.log.info.accio"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'EXPEDIENT_RETROCEDIR'}">
					<a href="<c:url value="/expedient/logRetrocedit.html"><c:param name="id" value="${param.id}"/><c:param name="logId" value="${registre.id}"/></c:url>" onclick="mostrarLogsRetrocedits(this); return false"><img src="<c:url value="/img/magnifier.png"/>" alt="<fmt:message key="expedient.log.retrocedits"/>" title="<fmt:message key="expedient.log.retrocedits"/>" border="0"/></a>
				</c:when>
				<c:otherwise></c:otherwise>
			</c:choose>
		</display:column>
		<display:column>
			<c:if test="${registre.estat == 'NORMAL' && numBloquejos == 0}">
				<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
					<a href="<c:url value="/expedient/retrocedir.html"><c:param name="id" value="${param.id}"/><c:param name="logId" value="${registre.id}"/></c:url>" onclick="return confirmarRetrocedir(event)"><img src="<c:url value="/img/arrow_undo.png"/>" alt="<fmt:message key="expedient.log.retrocedir"/>" title="<fmt:message key="expedient.log.retrocedir"/>" border="0"/></a>
				</security:accesscontrollist>
			</c:if>
			<c:if test="${numBloquejos gt 0}">B</c:if>
		</display:column>
		<c:if test="${registre.estat == 'BLOCAR'}">Hola<c:set var="numBloquejos" value="${numBloquejos - 1}"/></c:if>
	</display:table>

</body>
</html>
