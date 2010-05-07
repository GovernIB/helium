<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Tasques pendents</title>
	<meta name="titolcmp" content="Tasques">
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu fer-vos càrrec d'aquesta tasca?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsTasques.jsp">
		<c:param name="tabActiu" value="grup"/>
	</c:import>

	<display:table name="grupLlistat" id="registre" requestURI="" class="displaytag">
		<display:column property="nom" title="Tasca"/>
		<display:column property="expedient.identificador" title="Expedient"/>
		<display:column property="createTime" title="Creada el" format="{0,date,dd/MM/yyyy HH:mm}"/>
		<display:column title="Prioritat">
			<c:choose>
				<c:when test="${registre.priority == 2}">Molt alta</c:when>
				<c:when test="${registre.priority == 1}">Alta</c:when>
				<c:when test="${registre.priority == 0}">Normal</c:when>
				<c:when test="${registre.priority == -1}">Baixa</c:when>
				<c:when test="${registre.priority == -2}">Molt baixa</c:when>
				<c:otherwise>${registre.priority}</c:otherwise>
			</c:choose>
		</display:column>
		<display:column property="dueDate" title="Data límit" format="{0,date,dd/MM/yyyy HH:mm}"/>
		<display:column>
	    	<form action="agafar.html" onsubmit="return confirmar(event)">
				<input type="hidden" name="id" value="${registre.id}"/>
				<button type="submit" class="submitButton">Agafar</button>
			</form>
	    </display:column>
	</display:table>

</body>
</html>
