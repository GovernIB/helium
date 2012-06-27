<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
	<title><fmt:message key="tasca.gllistat.tasq_pendents"/></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.tasques"/>" />
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="tasca.gllistat.confirmacio"/>");
}
// ]]>
</script>
</head>
<body>
	<c:import url="../common/tabsTasques.jsp">
		<c:param name="tabActiu" value="grup"/>
	</c:import>

	<c:import url="../common/filtresTasques.jsp">
		<c:param name="formulari" value="grupLlistat.html"/>
	</c:import>

	<display:table name="grupLlistat" id="registre" requestURI="" class="displaytag selectable" sort="external" defaultsort="4" defaultorder="descending">
		<display:column property="titol" titleKey="tasca.gllistat.tasca" sortable="true" sortName="titol"/>
		<display:column sortProperty="expedientTitol" titleKey="tasca.gllistat.expedient" sortable="true" sortName="expedientTitol">
			<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/></c:url>">${registre.expedientTitol}</a>
		</display:column>
		<display:column property="expedientTipusNom" titleKey="comuns.tipus_exp" sortable="true" sortName="expedientTipusNom"/>
		<display:column property="dataCreacio" titleKey="tasca.gllistat.creada_el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true" sortName="dataCreacio"/>
		<display:column titleKey="tasca.gllistat.prioritat" sortable="true" sortName="prioritat">
			<c:choose>
				<c:when test="${registre.prioritat == 2}"><fmt:message key="tasca.gllistat.m_alta"/></c:when>
				<c:when test="${registre.prioritat == 1}"><fmt:message key="tasca.gllistat.alta"/></c:when>
				<c:when test="${registre.prioritat == 0}"><fmt:message key="tasca.gllistat.normal"/></c:when>
				<c:when test="${registre.prioritat == -1}"><fmt:message key="tasca.gllistat.baixa"/></c:when>
				<c:when test="${registre.prioritat == -2}"><fmt:message key="tasca.gllistat.m_baixa"/></c:when>
				<c:otherwise>${registre.prioritat}</c:otherwise>
			</c:choose>
		</display:column>
		<c:choose>
			<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'NORMAL'}"><c:set var="estilData">color:white;background-color:green</c:set></c:when>
			<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'AVIS'}"><c:set var="estilData">color:white;background-color:orange</c:set></c:when>
			<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'CADUCAT'}"><c:set var="estilData">color:white;background-color:red</c:set></c:when>
			<c:otherwise><c:set var="estilData"></c:set></c:otherwise>
		</c:choose>
		<display:column property="dataLimit" titleKey="tasca.gllistat.data_limit" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true" sortName="dataLimit" style="${estilData}"/>
		<display:column>
	    	<form action="agafar.html" onsubmit="return confirmar(event)">
				<input type="hidden" name="id" value="${registre.id}"/>
				<button type="submit" class="submitButton"><fmt:message key="tasca.gllistat.agafar"/></button>
			</form>
	    </display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
