<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>

<html>
	<head>
		<title>Reassignacions</title>
		<meta name="titolcmp" content="Configuració">
		<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>		
		<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
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
		<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
			<display:column property="usuariOrigen" title="Usuari origen" sortable="true" url="/reassignar/form.html" paramId="id" paramProperty="id"/>
			<display:column property="usuariDesti" title="Usuari destí" sortable="true"/>
			<display:column title="Data inici" sortable="true">
				<fmt:formatDate value="${registre.dataInici}" pattern="dd/MM/yyyy"/>
			</display:column>
			<display:column title="Data fi" sortable="true">
				<fmt:formatDate value="${registre.dataFi}" pattern="dd/MM/yyyy"/>
			</display:column>
			<display:column title="Cancel·lar" sortable="false">
				<a href="<c:url value="/reassignar/cancelar.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Cancelar" title="Cancelar" border="0"/></a>
			</display:column>
			<display:setProperty name="paging.banner.item_name">reassignació</display:setProperty>
			<display:setProperty name="paging.banner.items_name">reassignació</display:setProperty>
		</display:table>

		<script type="text/javascript">initSelectable();</script>

		<form action="form.html">
			<button type="submit" class="submitButton">Nova reassignació</button>
		</form>
	</body>
</html>
