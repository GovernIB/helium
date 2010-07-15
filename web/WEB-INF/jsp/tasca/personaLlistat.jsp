<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Tasques pendents</title>
	<meta name="titolcmp" content="Tasques"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsTasques.jsp">
		<c:param name="tabActiu" value="persona"/>
	</c:import>

	<display:table name="personaLlistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column title="Tasca" url="/tasca/info.html" paramId="id" paramProperty="id">
			<c:if test="${registre.delegada and not registre.delegacioOriginal}"><img src="<c:url value="/img/note_go.png"/>" title="Tasca assignada" alt="Tasca assignada"/></c:if>
			${registre.nom}
			<c:if test="${registre.delegada and registre.delegacioOriginal}"><img src="<c:url value="/img/note_go.png"/>" title="Tasca delegada" alt="Tasca delegada"/></c:if>
		</display:column>
		<display:column property="expedient.identificador" title="Expedient"/>
		<display:column property="expedient.tipus.nom" title="Tipus d'expedient"/>
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
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
