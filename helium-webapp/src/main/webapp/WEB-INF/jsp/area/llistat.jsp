<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Àrees</title>
	<meta name="titolcmp" content="Organització"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquest registre?. També s'esborraràn els seus càrrecs.");
}
// ]]>
</script>
</head>
<body>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" title="Codi" sortable="true" url="/area/form.html" paramId="id" paramProperty="id"/>
		<display:column property="nom" title="Nom" sortable="true"/>
		<display:column property="tipus.nom" title="Tipus"/>
		<display:column property="pare.nom" title="Pare"/>
		<display:column>
	    	<form action="membres.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<button type="submit" class="submitButton">Membres</button>
			</form>
	    </display:column>
		<display:column>
			<a href="<c:url value="/area/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<form action="<c:url value="/area/form.html"/>">
		<button type="submit" class="submitButton">Nova àrea</button>
	</form>

</body>
</html>
