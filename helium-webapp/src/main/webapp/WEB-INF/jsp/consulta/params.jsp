<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key="consulta.params.titol"/> ${consulta.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
	// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="consulta.params.confirmacio"/>");
}
	// ]]>
</script>
</head>
<body>
	<display:table name="llistat" id="consultaParam" requestURI="" class="displaytag selectable">
		<display:column property="campCodi" titleKey="consulta.params.camp.codi" sortable="false"/>
		<display:column property="campDescripcio" titleKey="consulta.params.camp.descripcio" sortable="false"/>
		<display:column property="paramTipus" titleKey="consulta.params.camp.tipus" sortable="false"/>
		<display:column>
			<a href="<c:url value="/consulta/paramDelete.html"><c:param name="id" value="${consulta.id}"/><c:param name="paramId" value="${consultaParam.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
	</display:table>
	<form:form action="params.html" method="post" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend><fmt:message key="consulta.params.afegir"/></legend>
			<input type="hidden" name="id" id="id" value="${param.id}" />
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="text"/>
				<c:param name="label"><fmt:message key="consulta.params.camp.codi"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="text"/>
				<c:param name="label"><fmt:message key="consulta.params.camp.descripcio"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipus"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="tipusParam"/>
				<c:param name="label"><fmt:message key="consulta.params.camp.tipus"/></c:param>
			</c:import>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key="comuns.afegir"/>,<fmt:message key="comuns.cancelar"/></c:param>
		</c:import>
	</form:form>
	<p class="aclaracio"><fmt:message key="comuns.camps_marcats"/> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>
</body>
</html>