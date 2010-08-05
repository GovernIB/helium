<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Definició de procés: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="Disseny"/>
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
	return confirm("Estau segur que voleu esborrar aquest camp de la tasca?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="tasques"/>
	</c:import>

	<h3 class="titol-tab titol-variables-tascadef">
		Variables de la tasca ${tasca.nom}
	</h3>
	<display:table name="campsTasca" id="registre" requestURI="" class="displaytag">
		<display:column property="camp.codiEtiqueta" title="Variable"/>
		<display:column title="Propietats">
			<c:if test="${registre.readFrom}">rf</c:if>
			<c:if test="${registre.writeTo}">wt</c:if>
			<c:if test="${registre.required}">rq</c:if>
			<c:if test="${registre.readOnly}">ro</c:if>
		</display:column>
		<display:column property="order" title="Ordre"/>
		<display:column>
			<a href="<c:url value="/definicioProces/tascaCampPujar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="Amunt" title="Amunt" border="0"/></a>&nbsp;<a href="<c:url value="/definicioProces/tascaCampBaixar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="Avall" title="Avall" border="0"/></a>
		</display:column>
		<display:column>
			<a href="<c:url value="/definicioProces/tascaCampEsborrar.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
		</display:column>
	</display:table>

	<form:form action="tascaCamps.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<legend>Afegir variable</legend>
			<input id="definicioProcesId" name="definicioProcesId" value="${param.definicioProcesId}" type="hidden"/>
			<form:hidden path="tascaId"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="campId"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="camps"/>
				<c:param name="itemLabel" value="codiEtiqueta"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit" value="<< Seleccioni una variable >>"/>
				<c:param name="label">Variable</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="readFrom"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Llegir del procés (rf)</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="writeTo"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Escriure cap al procés (wt)</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="required"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Obligatòria (rq)</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="readOnly"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Només lectura (ro)</c:param>
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
