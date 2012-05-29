<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}"><fmt:message key="expedient.tipus.form.crear_nou"/></c:when><c:otherwise><fmt:message key="expedient.tipus.form.crear_nou"/></c:otherwise></c:choose></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.disseny"/>"/>
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
						<c:param name="label"><fmt:message key="expedient.tipus.form.codi"/></c:param>
					</c:import>
				</c:when>
				<c:otherwise>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="codi"/>
						<c:param name="type" value="static"/>
						<c:param name="required" value="true"/>
						<c:param name="label"><fmt:message key="expedient.tipus.form.codi"/></c:param>
					</c:import>
				</c:otherwise>
			</c:choose>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.titol"/>Títol</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="teTitol"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.amb_titol"/>Amb títol</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="demanaTitol"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.demana_titol"/>Demana títol</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="teNumero"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.amb_num"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="demanaNumero"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.demana_num"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expressioNumero"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.expressio"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="sequencia"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.seq_actual"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="reiniciarCadaAny"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.seq_reiniciar"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="responsableDefecteCodi"/>
				<c:param name="type" value="suggest"/>
				<c:param name="label">Responsable per defecte</c:param>
				<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
				<c:param name="suggestText">${responsableDefecte.nomSencer}</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="restringirPerGrup"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.restringir_grup"/></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key='comuns.crear' />,<fmt:message key='comuns.cancelar' /></c:when><c:otherwise><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
