<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Canviar contrasenya</title>
	<meta name="titolcmp" content="El meu perfil">
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<form:form action="contrasenya.html" cssClass="uniForm">
		<fieldset class="inlineLabels col first">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="contrasenya"/>
				<c:param name="type" value="password"/>
				<c:param name="label">Contrasenya</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="repeticio"/>
				<c:param name="type" value="password"/>
				<c:param name="label">Repetició contrasenya</c:param>
			</c:import>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles">Modificar,Cancel·lar</c:param>
		</c:import>
	</form:form>

</body>
</html>
