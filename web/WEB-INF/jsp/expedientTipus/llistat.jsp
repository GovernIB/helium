<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Tipus d'expedient</title>
	<meta name="titolcmp" content="Disseny">
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquest tipus d'expedient?");
}
// ]]>
</script>
</head>
<body>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" title="Codi" sortable="true" url="/expedientTipus/info.html" paramId="expedientTipusId" paramProperty="id"/>
		<display:column property="nom" title="Títol" sortable="true"/>
		<display:column title="Amb títol?" sortable="false">
			<c:choose><c:when test="${registre.teTitol}">Si</c:when><c:otherwise>No</c:otherwise></c:choose>
		</display:column>
		<display:column title="Demana títol?" sortable="false">
			<c:choose><c:when test="${registre.demanaTitol}">Si</c:when><c:otherwise>No</c:otherwise></c:choose>
		</display:column>
		<display:column title="Amb número?" sortable="false">
			<c:choose><c:when test="${registre.teNumero}">Si</c:when><c:otherwise>No</c:otherwise></c:choose>
		</display:column>
		<display:column title="Demana número?" sortable="false">
			<c:choose><c:when test="${registre.demanaNumero}">Si</c:when><c:otherwise>No</c:otherwise></c:choose>
		</display:column>
		<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,32">
			<display:column>
		    	<form action="<c:url value="/permisos/expedientTipus.html"/>">
					<input type="hidden" name="id" value="${registre.id}"/>
					<button type="submit" class="submitButton">Permisos</button>
				</form>
		    </display:column>
			<display:column>
				<a href="<c:url value="/expedientTipus/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
			</display:column>
		</security:accesscontrollist>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,32">
		<form action="<c:url value="/expedientTipus/form.html"/>">
			<button type="submit" class="submitButton">Nou tipus d'expedient</button>
		</form>
	</security:accesscontrollist>

</body>
</html>
