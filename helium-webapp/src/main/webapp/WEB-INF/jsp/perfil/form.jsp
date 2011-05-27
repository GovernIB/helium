<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='perfil.form.modificar' /></title>
	<meta name="titolcmp" content="<fmt:message key='perfil.form.meu_perfil' />"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<form:form action="form.html" cssClass="uniForm">
		<fieldset class="inlineLabels">
			<h3><fmt:message key='perfil.form.dades_perso' /></h3>
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="type" value="static"/>
				<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='comuns.nom' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="llinatge1"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='perfil.form.primer_llin' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="llinatge2"/>
				<c:param name="label"><fmt:message key='perfil.form.segon_llin' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dni"/>
				<c:param name="label"><fmt:message key='perfil.form.dni' /></c:param>
			</c:import>
			<%--c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataNaixement"/>
				<c:param name="type" value="date"/>
				<c:param name="label">Data naixement</c:param>
			</c:import--%>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="email"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='perfil.form.ae' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="sexe"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="sexes"/>
				<c:param name="itemLabel" value="codi"/>
				<c:param name="itemValue" value="valor"/>
				<c:param name="label"><fmt:message key='comuns.sexe' /></c:param>
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
			<c:param name="titles"><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
