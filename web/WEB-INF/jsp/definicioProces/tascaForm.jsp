<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Definició de procés: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="Disseny"/>
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="tasques"/>
	</c:import>

	<form:form action="tascaForm.html" cssClass="uniForm">
		<div class="inlineLabels">
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:if test="${not empty param.definicioProcesId}"><input type="hidden" name="definicioProcesId" value="${param.definicioProcesId}"/></c:if>
			<form:hidden path="definicioProces"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="jbpmName"/>
				<c:param name="type" value="static"/>
				<c:param name="label">Codi</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Títol</c:param>
			</c:import>
			<%--c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipus"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="tipusTasca"/>
				<c:param name="itemBuit" value="<< Seleccioni un tipus >>"/>
				<c:param name="label">Tipus</c:param>
			</c:import--%>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="missatgeInfo"/>
				<c:param name="label">Missatge d'informació</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="missatgeWarn"/>
				<c:param name="label">Missatge d'alerta</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nomScript"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label">Script pel títol</c:param>
				<c:param name="comment">Si no està buit s'evaluarà aquest script per obtenir el títol de la tasca</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="recursForm"/>
				<c:param name="label">Recurs amb el formulari</c:param>
			</c:import>
			<c:if test="${globalProperties['app.forms.actiu'] == 'true'}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="formExtern"/>
					<c:param name="label">Codi del formulari extern</c:param>
				</c:import>
			</c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expressioDelegacio"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="checkAsText" value="true"/>
				<c:param name="label">Delegable?</c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
