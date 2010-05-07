<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Expedient: ${expedient.identificador}</title>
	<meta name="titolcmp" content="Consultes">
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="dades"/>
	</c:import>

	<h3 class="titol-tab titol-dades-tasca">Modificar dada</h3>

	<form:form action="dadaModificar.html" cssClass="uniForm">
		<div class="inlineLabels">
			<c:if test="${not empty param.id}">
				<form:hidden path="id"/>
			</c:if>
			<c:if test="${not empty param.taskId}">
				<form:hidden path="taskId"/>
			</c:if>
			<form:hidden path="var"/>
			<c:if test="${not empty tasca.camps}">
				<c:forEach var="camp" items="${tasca.camps}">
					<c:set var="campTascaActual" value="${camp}" scope="request"/>
					<c:import url="../common/campTasca.jsp"/>
				</c:forEach>
			</c:if>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles">Modificar,Cancel·lar</c:param>
		</c:import>
	</form:form>

</body>
</html>
