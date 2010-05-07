<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}">Crear nou entorn</c:when><c:otherwise>Modificar entorn</c:otherwise></c:choose></title>
	<meta name="titolcmp" content="Configuració">
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<form:form action="form.html" cssClass="uniForm">
		<div class="inlineLabels">
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:choose>
				<c:when test="${empty command.id}">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="codi"/>
						<c:param name="required" value="true"/>
						<c:param name="label">Codi</c:param>
					</c:import>
				</c:when>
				<c:otherwise>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="codi"/>
						<c:param name="type" value="static"/>
						<c:param name="required" value="true"/>
						<c:param name="label">Codi</c:param>
					</c:import>
				</c:otherwise>
			</c:choose>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Títol</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label">Descripció</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="actiu"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Actiu?</c:param>
			</c:import>
			<%--c:import url="../common/formElement.jsp">
				<c:param name="property" value="responsable"/>
				<c:param name="type" value="select"/>
				<c:param name="label">Responsable</c:param>
				<c:param name="items" value="persones"/>
				<c:param name="itemLabel" value="nomSencer"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit" value="--- Sense responsable ---"/>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="responsable"/>
				<c:param name="type" value="suggest"/>
				<c:param name="label">Responsable</c:param>
				<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
				<c:param name="suggestText">${command.responsable.nomSencer}</c:param>
			</c:import--%>
		</div>
		<c:choose>
			<c:when test="${empty isAdmin || isAdmin}">
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
				</c:import>
			</c:when>
			<c:otherwise>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear</c:when><c:otherwise>Modificar</c:otherwise></c:choose></c:param>
				</c:import>
			</c:otherwise>
		</c:choose>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
