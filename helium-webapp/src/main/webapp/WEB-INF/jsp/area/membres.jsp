<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='area.membres.membres_area' /> ${area.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.configuracio' />" />
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='area.membres.confirmacio' />");
}
// ]]>
</script>
</head>
<body>

	<display:table name="membres" id="registre" requestURI="" class="displaytag">
	    <display:column property="nomSencer" titleKey="area.membres.nom_sencer"/>
	    <display:column titleKey="comuns.carrecs">
	    	<c:forEach var="carrec" items="${carrecsMembres[registre.codi]}" varStatus="status">
	    		<c:if test="${carrec.area == area}">${carrec.nomHomeDona}<c:if test="${!status.last}">, </c:if></c:if>
	    	</c:forEach>
	    </display:column>
	    <display:column>
    		<a href="<c:url value="/area/membreEsborrar.html"><c:param name="id" value="${command.id}"/><c:param name="persona" value="${registre.codi}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
	    </display:column>
	</display:table>

	<form:form action="membres.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend><fmt:message key='area.membres.afegir' /></legend>
			<form:hidden path="id"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="persona"/>
				<c:param name="type" value="suggest"/>
				<c:param name="label"><fmt:message key='comuns.persona' /></c:param>
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
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='area.membres.selec_carrec' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='comuns.carrec' /></c:param>
			</c:import>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key='comuns.afegir' />,<fmt:message key='comuns.tornar' /></c:param>
		</c:import>
	</form:form>

</body>
</html>
