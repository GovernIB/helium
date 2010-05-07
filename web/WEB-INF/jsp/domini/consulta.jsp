<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Consulta del domini "${domini.nom}"</title>
	<meta name="titolcmp" content="Disseny">
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<form action="<c:url value="/domini/llistat.html"/>">
		<button type="submit" class="submitButton">Tornar</button>
	</form><br/>

	<display:table name="resultat" id="registre" requestURI="" class="displaytag selectable">
		<c:if test="${fn:length(registre.columnes) > 0}">
			<c:forEach begin="0" end="${fn:length(registre.columnes) - 1}" var="index">
				<display:column title="${registre.columnes[index].codi}">${registre.columnes[index].valor}</display:column>
			</c:forEach>
		</c:if>
	</display:table>

	<form action="<c:url value="/domini/llistat.html"/>">
		<button type="submit" class="submitButton">Tornar</button>
	</form>

</body>
</html>
