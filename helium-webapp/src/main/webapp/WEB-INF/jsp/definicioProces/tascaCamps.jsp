<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='comuns.def_proces' />: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='defproc.tascacamps.confirmacio' />");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="tasques"/>
	</c:import>

	<h3 class="titol-tab titol-variables-tascadef">
		<fmt:message key='defproc.tascacamps.vars_tasca' /> ${tasca.nom}
	</h3>
	<display:table name="campsTasca" id="registre" requestURI="" class="displaytag">
		<display:column property="camp.codiEtiqueta" title="Variable"/>
		<display:column titleKey="comuns.propietats">
			<c:if test="${registre.readFrom}">rf</c:if>
			<c:if test="${registre.writeTo}">wt</c:if>
			<c:if test="${registre.required}">rq</c:if>
			<c:if test="${registre.readOnly}">ro</c:if>
		</display:column>
		<display:column property="order" titleKey="comuns.ordre"/>
		<display:column>
			<a href="<c:url value="/definicioProces/tascaCampPujar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key='comuns.amunt' />" title="<fmt:message key='comuns.amunt' />" border="0"/></a>&nbsp;<a href="<c:url value="/definicioProces/tascaCampBaixar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key='comuns.avall' />" title="<fmt:message key='comuns.avall' />" border="0"/></a>
		</display:column>
		<display:column>
			<a href="<c:url value="/definicioProces/tascaCampEsborrar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
	</display:table>

	<form:form action="tascaCamps.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend><fmt:message key='defproc.tascacamps.afegir_var' /></legend>
			<input id="definicioProcesId" name="definicioProcesId" value="${param.definicioProcesId}" type="hidden"/>
			<form:hidden path="tascaId"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="campId"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="camps"/>
				<c:param name="itemLabel" value="codiEtiqueta"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.tascacamps.selec_var' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='defproc.tascacamps.variable' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="readFrom"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.tascacamps.llegir_del' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="writeTo"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.tascacamps.escriure_cap' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="required"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.tascacamps.obligatoria' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="readOnly"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.tascacamps.nomes_lectura' /></c:param>
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
