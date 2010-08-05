<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Modificar perfil</title>
	<meta name="titolcmp" content="El meu perfil"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<form:form action="form.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<h3>Dades personals</h3>
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="type" value="static"/>
				<c:param name="label">Codi</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Nom</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="llinatge1"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Primer llinatge</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="llinatge2"/>
				<c:param name="label">Segon llinatge</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dni"/>
				<c:param name="label">DNI</c:param>
			</c:import>
			<%--c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataNaixement"/>
				<c:param name="type" value="date"/>
				<c:param name="label">Data naixement</c:param>
			</c:import--%>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="email"/>
				<c:param name="required" value="true"/>
				<c:param name="label">A/E</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="sexe"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="sexes"/>
				<c:param name="label">Sexe</c:param>
			</c:import>
			<%--c:import url="../common/formElement.jsp">
				<c:param name="property" value="avisCorreu"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Notificar events per correu</c:param>
			</c:import--%>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles">Modificar,Cancel·lar</c:param>
		</c:import>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
