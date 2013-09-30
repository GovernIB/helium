<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='permisos.exp.perm_tipus_exp' /> ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.configuracio' />" />
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='permisos.exp.confirmacio' />");
}
function isValidForm(){
	if ($("#nom0").val() == "") {
		alert("<fmt:message key='error.usurol.buit' />");
		return false;
	}
	return true;
}
// ]]>
</script>
</head>
<body>

	<display:table name="acesGroupedBySid" id="registre" requestURI="" class="displaytag">
		<display:column titleKey="comuns.tipus" sortable="true">
			<c:choose>
				<c:when test="${fn:contains(registre[0].sid.class, 'Principal')}"><fmt:message key='permisos.exp.usuari' /></c:when>
				<c:otherwise><fmt:message key='permisos.exp.rol' /></c:otherwise>
			</c:choose>
		</display:column>
		<display:column titleKey="comuns.nom">
			<c:choose>
				<c:when test="${fn:contains(registre[0].sid.class, 'Principal')}">${registre[0].sid.principal}</c:when>
				<c:otherwise>${registre[0].sid.grantedAuthority}</c:otherwise>
			</c:choose>
		</display:column>
		<display:column titleKey="permisos.exp.permisos">
			<c:forEach var="ace" items="${registre}">
				<c:forEach var="permis" items="${permisos}">
					<c:if test="${permis.value == ace.permission}">+${permis.key} </c:if>
				</c:forEach>
			</c:forEach>
		</display:column>
		<display:column>
	    	<c:choose>
				<c:when test="${fn:contains(registre[0].sid.class, 'Principal')}">
		    		<a href="<c:url value="/permisos/expedientTipusEsborrar.html"><c:param name="id" value="${command.id}"/><c:param name="nom" value="${registre[0].sid.principal}"/><c:param name="usuari" value="on"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		    	</c:when>
		    	<c:otherwise>
		    		<a href="<c:url value="/permisos/expedientTipusEsborrar.html"><c:param name="id" value="${command.id}"/><c:param name="nom" value="${registre[0].sid.grantedAuthority}"/><c:param name="usuari" value="off"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		    	</c:otherwise>
		    </c:choose>
	    </display:column>
	</display:table>

	<form:form action="expedientTipus.html" cssClass="uniForm" onsubmit="return isValidForm()">
		<fieldset class="inlineLabels">
			<legend><fmt:message key='permisos.exp.afegir' /></legend>
			<form:hidden path="id"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required">true</c:param>
				<c:param name="label"><fmt:message key='permisos.exp.usu_rol' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="permisos"/>
				<c:param name="required">true</c:param>
				<c:param name="type" value="multicheck"/>
				<c:param name="label"><fmt:message key='permisos.exp.permisos' /></c:param>
				<c:param name="items" value="permisos"/>
				<c:param name="itemLabel" value="key"/>
				<c:param name="itemValue" value="key"/>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="usuari"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='permisos.exp.es_usu' /></c:param>
				<c:param name="comment"><fmt:message key='permisos.exp.sino' /></c:param>
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
