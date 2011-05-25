<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Permisos per al tipus d'expedient ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="Configuració"/>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquests permisos?");
}
// ]]>
</script>
</head>
<body>

	<display:table name="acesGroupedBySid" id="registre" requestURI="" class="displaytag">
		<display:column title="Tipus" sortable="true">
			<c:choose>
				<c:when test="${fn:contains(registre[0].sid.class, 'Principal')}">Usuari</c:when>
				<c:otherwise>Rol</c:otherwise>
			</c:choose>
		</display:column>
		<display:column title="Nom">
			<c:choose>
				<c:when test="${fn:contains(registre[0].sid.class, 'Principal')}">${registre[0].sid.principal}</c:when>
				<c:otherwise>${registre[0].sid.grantedAuthority}</c:otherwise>
			</c:choose>
		</display:column>
		<display:column title="Permisos">
			<c:forEach var="ace" items="${registre}">
				<c:forEach var="permis" items="${permisos}">
					<c:if test="${permis.value == ace.permission}">+${permis.key} </c:if>
				</c:forEach>
			</c:forEach>
		</display:column>
		<display:column>
	    	<c:choose>
				<c:when test="${fn:contains(registre[0].sid.class, 'Principal')}">
		    		<a href="<c:url value="/permisos/expedientTipusEsborrar.html"><c:param name="id" value="${command.id}"/><c:param name="nom" value="${registre[0].sid.principal}"/><c:param name="usuari" value="on"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
		    	</c:when>
		    	<c:otherwise>
		    		<a href="<c:url value="/permisos/expedientTipusEsborrar.html"><c:param name="id" value="${command.id}"/><c:param name="nom" value="${registre[0].sid.grantedAuthority}"/><c:param name="usuari" value="off"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
		    	</c:otherwise>
		    </c:choose>
	    </display:column>
	</display:table>

	<form:form action="expedientTipus.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend>Afegir permisos</legend>
			<form:hidden path="id"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required">true</c:param>
				<c:param name="label">Usuari/Rol</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="permisos"/>
				<c:param name="required">true</c:param>
				<c:param name="type" value="multicheck"/>
				<c:param name="label">Permisos</c:param>
				<c:param name="items" value="permisos"/>
				<c:param name="itemLabel" value="key"/>
				<c:param name="itemValue" value="key"/>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="usuari"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Es usuari?</c:param>
				<c:param name="comment">Si no és usuari serà un rol</c:param>
			</c:import>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles">Afegir,Tornar</c:param>
		</c:import>
	</form:form>

</body>
</html>
