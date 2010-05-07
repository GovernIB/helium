<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Definicions de procés</title>
	<meta name="titolcmp" content="Disseny">
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar la darrera versió d'aquesta definició de procés?");
}
// ]]>
</script>
</head>
<body>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="jbpmName" title="Nom" sortable="true" url="/definicioProces/info.html" paramId="definicioProcesId" paramProperty="id"/>
		<display:column property="numIdsWithSameKey" title="Versions" sortable="true"/>
		<display:column property="expedientTipus.nom" title="Tipus d'expedient" sortable="true"/>
		<display:column>
			<a href="<c:url value="/definicioProces/delete.html"><c:param name="definicioProcesId" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar darrera versió" title="Esborrar darrera versió" border="0"/></a>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
