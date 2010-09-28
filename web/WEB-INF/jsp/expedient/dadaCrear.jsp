<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Expedient: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="Consultes"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="dades"/>
	</c:import>

	<h3 class="titol-tab titol-dades-tasca">Afegir dada</h3>

	<form:form action="dadaCrear.html" cssClass="uniForm">
		<div class="inlineLabels">
			<c:if test="${not empty param.id}">
				<form:hidden path="id"/>
			</c:if>
			<c:if test="${not empty param.taskId}">
				<form:hidden path="taskId"/>
			</c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="camp"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="camps"/>
				<c:param name="itemLabel" value="codiEtiqueta"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit" value="<< Seleccioni una variable >>"/>
				<c:param name="label">Variable</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="varCodi"/>
				<c:param name="label">Codi de la nova variable</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="modificar"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Anar a modificar la variable?</c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles">Crear,Cancel·lar</c:param>
		</c:import>
	</form:form>
	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
