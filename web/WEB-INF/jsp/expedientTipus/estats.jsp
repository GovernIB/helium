<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="Disseny"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquest estat?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="estats"/>
	</c:import>

	<display:table name="estats" id="registre" requestURI="" class="displaytag">
		<display:column property="codi" title="Codi"/>
		<display:column property="nom" title="Nom"/>
		<display:column property="ordre" title="Ordre"/>
		<display:column>
			<a href="<c:url value="/expedientTipus/estatPujar.html"><c:param name="id" value="${registre.id}"/><c:param name="expedientTipusId" value="${param.expedientTipusId}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="Amunt" title="Amunt" border="0"/></a>
			<a href="<c:url value="/expedientTipus/estatBaixar.html"><c:param name="id" value="${registre.id}"/><c:param name="expedientTipusId" value="${param.expedientTipusId}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="Avall" title="Avall" border="0"/></a>
		</display:column>
		<display:column>
			<a href="<c:url value="/expedientTipus/estatEsborrar.html"><c:param name="id" value="${registre.id}"/><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
		</display:column>
	</display:table>

	<form:form action="estats.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend>Afegir nou estat</legend>
			<input id="expedientTipus" name="expedientTipus" value="${param.expedientTipusId}" type="hidden"/>
			<input id="expedientTipusId" name="expedientTipusId" value="${param.expedientTipusId}" type="hidden"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="${true}"/>
				<c:param name="label">Codi</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="${true}"/>
				<c:param name="label">Nom</c:param>
			</c:import>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit</c:param>
			<c:param name="titles">Afegir</c:param>
		</c:import>
	</form:form>

</body>
</html>
