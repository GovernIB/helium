<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='common.filtres.expedient' />: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="documents"/>
	</c:import>

	<h3 class="titol-tab titol-dades-tasca"><c:choose><c:when test="${empty param.adjuntId}"><fmt:message key='expedient.document.adjuntar' /></c:when><c:otherwise><fmt:message key='expedient.document.modificar_adjunt' /></c:otherwise></c:choose></h3>

	<form:form action="documentAdjuntForm.html" cssClass="uniForm" enctype="multipart/form-data">
		<div class="inlineLabels">
			<input type="hidden" name="id" value="${param.id}"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='expedient.document.titol' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="contingut"/>
				<c:param name="type" value="file"/>
				<c:param name="required" value="true"/>
				<c:param name="fileUrl">${downloadUrl}</c:param>
				<c:param name="fileExists" value="${not empty command.nom}"/>
				<c:param name="label"><fmt:message key='expedient.document.arxiu' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="data"/>
				<c:param name="type" value="date"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='expedient.document.data' /></c:param>
			</c:import>
		</div>
		<c:choose>
			<c:when test="${empty param.adjuntId}">
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><fmt:message key='comuns.adjuntar' />,<fmt:message key='comuns.cancelar' /></c:param>
				</c:import>
			</c:when>
			<c:otherwise>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:param>
				</c:import>
			</c:otherwise>
		</c:choose>
	</form:form>

</body>
</html>
