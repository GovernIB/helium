<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Definició de procés: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="Disseny"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
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
		<c:param name="tabActiu" value="documents"/>
	</c:import>

	<display:table name="documents" id="registre" requestURI="" defaultsort="1" class="displaytag selectable">
		<display:column property="codi" title="Codi" sortable="true" url="/definicioProces/documentForm.html?definicioProcesId=${param.definicioProcesId}" paramId="id" paramProperty="id"/>
		<display:column property="nom" title="Nom" sortable="true"/>
		<display:column title="És plantilla?">
			<c:choose><c:when test="${registre.plantilla}">Si</c:when><c:otherwise>No</c:otherwise></c:choose>
		</display:column>
		<display:column title="Arxiu">
			<c:if test="${not empty registre.arxiuNom}">
				<a href="<c:url value="/definicioProces/documentDownload.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a>
			</c:if>
		</display:column>
		<display:column property="campData.codiEtiqueta" title="Camp data" sortable="false"/>
	    <display:column>
    		<a href="<c:url value="/definicioProces/documentDelete.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
	    </display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<form action="<c:url value="/definicioProces/documentForm.html"/>">
		<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
		<button type="submit" class="submitButton">Nou document</button>
	</form>

</body>
</html>
