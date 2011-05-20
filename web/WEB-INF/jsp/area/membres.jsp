<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Membres de l'àrea ${area.nom}</title>
	<meta name="titolcmp" content="Configuració"/>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu treure aquest membre de l'àrea?");
}
// ]]>
</script>
</head>
<body>

	<display:table name="membres" id="registre" requestURI="" class="displaytag">
	    <display:column property="nomSencer" title="NomSencer"/>
	    <display:column title="Càrrecs">
	    	<c:forEach var="carrec" items="${carrecsMembres[registre.codi]}" varStatus="status">
	    		<c:if test="${carrec.area == area}">${carrec.nomHomeDona}<c:if test="${!status.last}">, </c:if></c:if>
	    	</c:forEach>
	    </display:column>
	    <display:column>
    		<a href="<c:url value="/area/membreEsborrar.html"><c:param name="id" value="${command.id}"/><c:param name="persona" value="${registre.codi}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
	    </display:column>
	</display:table>

	<form:form action="membres.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend>Afegir un nou membre</legend>
			<form:hidden path="id"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="persona"/>
				<c:param name="type" value="suggest"/>
				<c:param name="label">Persona</c:param>
				<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
				<c:param name="suggestText">${command.persona.nomSencer}</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="carrec"/>
				<c:param name="required" value="false"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="carrecs"/>
				<c:param name="itemLabel" value="nomHomeDona"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit" value="<< Seleccioni un càrrec >>"/>
				<c:param name="label">Càrrec</c:param>
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
