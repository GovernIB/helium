<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='perfil.contrasenya.canviar' /></title>
	<meta name="titolcmp" content="<fmt:message key='perfil.contrasenya.meu_perfil' />"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<form:form action="contrasenya.html" cssClass="uniForm">
		<fieldset class="inlineLabels col first">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="contrasenya"/>
				<c:param name="type" value="password"/>
				<c:param name="label"><fmt:message key='perfil.contrasenya.contrasenya' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="repeticio"/>
				<c:param name="type" value="password"/>
				<c:param name="label"><fmt:message key='perfil.contrasenya.repeticio' /></c:param>
			</c:import>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:param>
		</c:import>
	</form:form>

</body>
</html>
