<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Expedient: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="Consultes"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="documents"/>
	</c:import>

	<h3 class="titol-tab titol-dades-tasca">Modificar document</h3>

	<c:url value="/expedient/documentDescarregar.html" var="downloadUrl"><c:param name="processInstanceId" value="${param.id}"/><c:param name="docId" value="${command.docId}"/></c:url>

	<div class="missatgesDocumentGris">
		<h4 class="titol-missatge">
			<c:choose><c:when test="${not document.adjunt}">${command.nom}</c:when><c:otherwise>Document adjunt</c:otherwise></c:choose>
			<c:if test="${documentDisseny.plantilla}">
				<a href="<c:url value="/expedient/documentGenerar.html"><c:param name="id" value="${param.id}"/><c:param name="docId" value="${command.docId}"/></c:url>"><img src="<c:url value="/img/page_white_star.png"/>" alt="Generar" title="Generar" border="0"/></a>
			</c:if>
		</h4>
		<form:form action="documentModificar.html" cssClass="uniForm" enctype="multipart/form-data">
		<div class="inlineLabels">
			<input type="hidden" name="id" value="${param.id}"/>
			<form:hidden path="docId"/>
			<c:choose>
				<c:when test="${document.adjunt}">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="nom"/>
						<c:param name="required" value="true"/>
						<c:param name="label">Títol</c:param>
					</c:import>
				</c:when>
				<c:otherwise>
					<form:hidden path="nom"/>
				</c:otherwise>
			</c:choose>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="contingut"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="file"/>
				<c:param name="fileUrl">${downloadUrl}</c:param>
				<c:param name="fileExists" value="${not empty command.data}"/>
				<c:param name="label">Document</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="data"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="date"/>
				<c:param name="label">Data</c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles">Modificar,Cancel·lar</c:param>
		</c:import>
	</form:form>
	</div>


</body>
</html>
