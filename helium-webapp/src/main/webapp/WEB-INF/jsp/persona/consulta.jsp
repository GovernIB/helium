<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Persones</title>
	<meta name="titolcmp" content="Configuració"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
     <c:import url="../common/formIncludes.jsp"/>
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

	<form:form action="consulta.html" cssClass="uniForm">
		<div class="inlineLabels col first">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="label">Codi</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="label">Nom</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="email"/>
				<c:param name="label">Email</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit,clean</c:param>
				<c:param name="titles">Consultar,Netejar</c:param>
			</c:import>
		</div>
	</form:form><br/>

	<c:if test="${not empty sessionScope.consultaPersonesCommand}">
		<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
			<display:column property="codi" title="Codi" sortable="true" url="/persona/form.html" paramId="id" paramProperty="id"/>
			<display:column property="nomSencer" title="Nom" sortable="true"/>
			<display:column property="email" title="A/E" sortable="true"/>
			<c:if test="${globalProperties['app.persones.readonly'] != 'true'}">
				<display:column title="Accés?" sortable="false">
					<c:choose><c:when test="${registre.login}">Si</c:when><c:otherwise>No</c:otherwise></c:choose>
				</display:column>
				<display:column>
					<a href="<c:url value="/persona/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
				</display:column>
			</c:if>
			<display:setProperty name="paging.banner.item_name">persona</display:setProperty>
			<display:setProperty name="paging.banner.items_name">persona</display:setProperty>
		</display:table>
		<script type="text/javascript">initSelectable();</script>
	</c:if>

	<c:if test="${globalProperties['app.persones.readonly'] != 'true'}">
		<form action="form.html">
			<button type="submit" class="submitButton">Nova persona</button>
		</form>
	</c:if>

</body>
</html>
