<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='comuns.def_proces' />: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
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
				<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='comuns.titol' /></c:param>
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
				<c:param name="label"><fmt:message key='defproc.tascaform.msg_info' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="missatgeWarn"/>
				<c:param name="label"><fmt:message key='defproc.tascaform.msg_alert' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nomScript"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label"><fmt:message key='defproc.tascaform.script_titol' /></c:param>
				<c:param name="comment"><fmt:message key='defproc.tascaform.si_no_buit' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="recursForm"/>
				<c:param name="label"><fmt:message key='defproc.tascaform.recurs' /></c:param>
			</c:import>
			<c:if test="${globalProperties['app.forms.actiu'] == 'true'}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="formExtern"/>
					<c:param name="label"><fmt:message key='defproc.tascaform.codi_form' /></c:param>
				</c:import>
			</c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expressioDelegacio"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="checkAsText" value="true"/>
				<c:param name="label"><fmt:message key='defproc.tascaform.delegableq' /></c:param>
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
