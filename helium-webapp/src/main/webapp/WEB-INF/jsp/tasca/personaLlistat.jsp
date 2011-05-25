<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
	<title>Tasques pendents</title>
	<meta name="titolcmp" content="Tasques"/>
	<c:import url="../common/formIncludes.jsp"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsTasques.jsp">
		<c:param name="tabActiu" value="persona"/>
	</c:import>

	<c:import url="../common/filtresTasques.jsp">
		<c:param name="formulari" value="personaLlistat.html"/>
		<c:param name="tasques" value="${fn:length(personaLlistat)}"/>
		<c:param name="total" value="${personaLlistatAll}"/>
	</c:import>

	<c:set var="ordre">
		<c:choose>
			<c:when test="${(command.ordre eq '1') or (command.ordre eq 1)}">descending</c:when>
			<c:otherwise>ascending</c:otherwise>
		</c:choose>
	</c:set>

	<c:set var="columna">
		<c:choose>
			<c:when test="${not empty command.ordre and command.ordre != '0'}">${command.columna+1}</c:when>
			<c:otherwise>0</c:otherwise>
		</c:choose>
	</c:set>

	<display:table name="personaLlistat" id="registre" requestURI="" class="displaytag selectable" sort="external" defaultsort="${columna}" defaultorder="${ordre}">
		<display:column title="Tasca" url="/tasca/info.html" paramId="id" paramProperty="id" sortable="true">
			${registre.titol}
		</display:column>
		<display:column sortProperty="expedientTitol" title="Expedient" sortable="true">
			<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/></c:url>">${registre.expedientTitol}</a>
		</display:column>
		<display:column property="expedientTipusNom" title="Tipus d'expedient" sortable="true"/>
		<display:column property="dataCreacio" title="Creada el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true"/>
		<display:column title="Prioritat" sortable="true">
			<c:choose>
				<c:when test="${registre.prioritat == 2}">Molt alta</c:when>
				<c:when test="${registre.prioritat == 1}">Alta</c:when>
				<c:when test="${registre.prioritat == 0}">Normal</c:when>
				<c:when test="${registre.prioritat == -1}">Baixa</c:when>
				<c:when test="${registre.prioritat == -2}">Molt baixa</c:when>
				<c:otherwise>${registre.prioritat}</c:otherwise>
			</c:choose>
		</display:column>
		<c:choose>
			<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'NORMAL'}"><c:set var="estilData">color:white;background-color:green</c:set></c:when>
			<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'AVIS'}"><c:set var="estilData">color:white;background-color:orange</c:set></c:when>
			<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'CADUCAT'}"><c:set var="estilData">color:white;background-color:red</c:set></c:when>
			<c:otherwise><c:set var="estilData"></c:set></c:otherwise>
		</c:choose>
		<display:column property="dataLimit" title="Data límit&nbsp;" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true" style="${estilData}"/>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
