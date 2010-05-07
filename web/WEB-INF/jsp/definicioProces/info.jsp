<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Definició de procés: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="Disseny">
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquest registre?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="info"/>
	</c:import>

	<dl class="form-info">
		<dt>Id</dt><dd>${definicioProces.idPerMostrar}</dd>
		<dt>Codi</dt><dd>${definicioProces.jbpmKey}</dd>
		<dt>Nom</dt><dd>${definicioProces.jbpmName}</dd>
		<dt>Versió</dt><dd>${definicioProces.versio}</dd>
		<c:if test="${not empty definicioProces.etiqueta}"><dt>Etiqueta</dt><dd>${definicioProces.etiqueta}</dd></c:if>
		<c:if test="${not empty definicioProces.expedientTipus}"><dt>Tipus d'expedient</dt><dd>${definicioProces.expedientTipus.nom}</dd></c:if>
		<dt>Creada el</dt><dd><fmt:formatDate value="${definicioProces.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></dd>
		<c:if test="${fn:length(subDefinicionsProces) gt 0}">
			<dt>Subdefinicions de procés</dt>
			<dd>
				<c:forEach var="subdp" items="${subDefinicionsProces}" varStatus="status">${subdp.idPerMostrar}<c:if test="${not status.last}">, </c:if></c:forEach>
			</dd>
		</c:if>
	</dl><br/>

	<form action="<c:url value="/definicioProces/export.html"/>" method="post">
		<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
		<button type="submit" class="submitButton">Exportar aquesta versió</button>
	</form>
	<br/>
	<form action="<c:url value="/definicioProces/delete.html"/>" method="post" onsubmit="return confirmar(event)">
		<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
		<button type="submit" class="submitButton">Esborrar aquesta versió</button>
	</form>

</body>
</html>
