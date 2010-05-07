<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Consultes</title>
	<meta name="titolcmp" content="Disseny">
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquesta consulta?");
}
// ]]>
</script>
</head>
<body>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" title="Codi" sortable="true" url="/consulta/form.html" paramId="id" paramProperty="id"/>
		<display:column property="nom" title="Títol" sortable="true"/>
		<display:column property="expedientTipus.nom" title="Tipus d'expedient" sortable="true"/>
		<display:column>
			<c:set var="numCampsFiltre" value="${0}"/>
	    	<c:forEach var="camp" items="${registre.camps}">
    			<c:if test="${camp.tipus=='FILTRE'}"><c:set var="numCampsFiltre" value="${numCampsFiltre+1}"/></c:if>
	    	</c:forEach>
	    	<form action="camps.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<input type="hidden" name="tipus" value="${tipusFiltre}"/>
				<button type="submit" class="submitButton">Variables del filtre&nbsp;(${numCampsFiltre})</button>
			</form>
	    </display:column>
	    <%--display:column>
	    	<c:set var="numCampsInforme" value="${0}"/>
	    	<c:forEach var="camp" items="${registre.camps}">
    			<c:if test="${camp.tipus=='INFORME'}"><c:set var="numCampsInforme" value="${numCampsInforme+1}"/></c:if>
	    	</c:forEach>
	    	<form action="camps.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<input type="hidden" name="tipus" value="${tipusInforme}"/>
				<button type="submit" class="submitButton">Variables de l'informe&nbsp;(${numCampsInforme})</button>
			</form>
	    </display:column--%>
		<display:column>
			<a href="<c:url value="/consulta/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<form action="<c:url value="/consulta/form.html"/>">
		<button type="submit" class="submitButton">Nova consulta</button>
	</form>

</body>
</html>
