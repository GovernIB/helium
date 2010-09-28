<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Alertes</title>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmarEsborrar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquesta alerta?");
}
// ]]>
</script>
</head>
<body>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable" defaultsort="1" defaultorder="descending">
		<display:column property="dataCreacio" title="Creada el" sortable="true" format="{0,date,dd/MM/yyyy HH:mm}"/>
		<display:column title="Expedient" sortable="true">
			${registre.expedient.identificador}
		</display:column>
		<display:column title="Text" sortable="true">
			<c:choose>
				<c:when test="${not empty registre.taskInstanceId}"><a href="<c:url value="/tasca/info.html"><c:param name="id" value="${registre.taskInstanceId}"/></c:url>">${registre.text}</a></c:when>
				<c:otherwise>${registre.text}</c:otherwise>
			</c:choose>
		</display:column>
		<display:column title="Data límit" sortable="true">
			<c:if test="${not empty registre.taskInstanceId}">
				<fmt:formatDate value="${tasques[registre.taskInstanceId].dueDate}" pattern="dd/MM/yyyy HH:mm"/>
			</c:if>
		</display:column>
		<display:column>
			<c:choose>
				<c:when test="${registre.llegida}">
					<a href="<c:url value="/alerta/nollegir.html"><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/email_open.png"/>" alt="Marcar com a no llegida" title="Marcar com a no llegida" border="0"/></a>
				</c:when>
				<c:otherwise>
					<a href="<c:url value="/alerta/llegir.html"><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/email.png"/>" alt="Marcar com a llegida" title="Marcar com a llegida" border="0"/></a>
				</c:otherwise>
			</c:choose>
		</display:column>
		<display:column>
			<a href="<c:url value="/alerta/esborrar.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmarEsborrar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
