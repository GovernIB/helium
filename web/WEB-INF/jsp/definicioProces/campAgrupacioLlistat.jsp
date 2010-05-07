<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Definició de procés: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="Disseny">
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquesta agrupació?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="agrupacions"/>
	</c:import>

	<display:table name="agrupacions" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" title="Codi" sortable="true" url="/definicioProces/campAgrupacioForm.html?definicioProcesId=${param.definicioProcesId}" paramId="id" paramProperty="id"/>
		<display:column property="nom" title="Nom" sortable="true"/>
		<display:column>
			<a href="<c:url value="/definicioProces/campAgrupacioPujar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="Amunt" title="Amunt" border="0"/></a>
			<a href="<c:url value="/definicioProces/campAgrupacioBaixar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="Avall" title="Avall" border="0"/></a>
		</display:column>
		<display:column>
	    	<a href="<c:url value="/definicioProces/campAgrupacioDelete.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
	    </display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<form action="<c:url value="/definicioProces/campAgrupacioForm.html"/>">
		<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
		<button type="submit" class="submitButton">Nova agrupacio</button>
	</form>

</body>
</html>
