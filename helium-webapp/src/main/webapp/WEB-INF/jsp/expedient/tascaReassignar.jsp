<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='alerta.llistat.expedient' />: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="tasques"/>
	</c:import>
	<h3 class="titol-tab titol-dades-tasca"><fmt:message key='expedient.tasca.reassignar' /> "${tasca.nom}"</h3>

	<form:form action="tascaReassignar.html" cssClass="uniForm">
		<div class="inlineLabels">
			<input type="hidden" name="id" value="${param.id}"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="taskId"/>
				<c:param name="type" value="hidden"/>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expression"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='expedient.tasca.expresio_assignacio' /></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key='comuns.reassignar' />,<fmt:message key='comuns.cancelar' /></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
