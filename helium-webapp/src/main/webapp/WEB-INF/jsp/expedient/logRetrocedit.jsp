<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><fmt:message key="alerta.llistat.expedient"/>: ${expedient.identificadorLimitat}</title>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<display:table name="logs" id="registre" class="displaytag">
		<c:set var="cellStyle" value=""/>
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
				<c:when test="${registre.accioTipus == 'TASCA_MARCAR_FINALITZAR'}"><c:if test="${not empty registre.accioParams}"><fmt:message key="expedient.log.info.opcio"/>: ${registre.accioParams}</c:if></c:when>
				<c:when test="${registre.accioTipus == 'EXPEDIENT_ATURAR'}"><fmt:message key="expedient.log.info.missatges"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'EXPEDIENT_ACCIO'}"><fmt:message key="expedient.log.info.accio"/>: ${registre.accioParams}</c:when>
				<c:otherwise></c:otherwise>
			</c:choose>
		</display:column>
	</display:table>

</body>
</html>
