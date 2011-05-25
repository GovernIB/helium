<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Definició de procés: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="Disseny"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="recursos"/>
	</c:import>

	<display:table name="recursos" id="registre" requestURI="" class="displaytag selectable">
		<display:column title="Nom">${registre}</display:column>
		<display:column>
			<a href="<c:url value="/definicioProces/recursDescarregar.html"><c:param name="definicioProcesId" value="${definicioProces.id}"/><c:param name="resourceName" value="${registre}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a>
		</display:column>
	</display:table>

</body>
</html>
