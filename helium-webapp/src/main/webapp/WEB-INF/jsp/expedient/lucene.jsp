<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Expedient: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
	</c:import>

	<c:if test="${not empty dadesExpedient}">
		<h3 class="titol-tab titol-dades-tasques">
			Dades expedient
		</h3>
		<display:table name="dadesExpedient" id="registre" class="displaytag">
			<display:column property="campCodi" title="Dada"/>
			<display:column property="valorIndex" title="Valor indexat"/>
			<display:column property="valorMostrar" title="Valor mostrar"/>
		</display:table>
	</c:if>
	<c:if test="${not empty dadesCamps}">
		<h3 class="titol-tab titol-dades-tasques">
			Dades camps
		</h3>
		<display:table name="dadesCamps" id="registre" requestURI="" class="displaytag">
			<display:column property="reportFieldName" title="Camp codi" sortable="true"/>
			<display:column property="etiqueta" title="Camp"/>
			<display:column title="Valor">
				${registre}
			</display:column>
		</display:table>
	</c:if>

	<form action="<c:url value="/expedient/reindexar.html"/>">
		<input type="hidden" name="id" value="${param.id}" />
		<button type="submit" class="submitButton">Reindexar</button>
	</form>

</body>
</html>
