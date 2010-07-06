<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>${tasca.nom}</title>
	<meta name="titolcmp" content="Tasques">
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<c:import url="../common/tabsTasca.jsp">
		<c:param name="tabActiu" value="documents"/>
	</c:import>

	<c:if test="${not tasca.validada}">
		<div class="missatgesWarn">
			<p>No es podran adjuntar documents si les dades no han estat validades</p>
		</div>
	</c:if>
	<c:if test="${tasca.validada and not tasca.documentsComplet}">
		<div class="missatgesWarn">
			<p>Hi ha documents obligatoris que encara no s'han adjuntat a la tasca</p>
		</div>
	</c:if>

	<c:import url="../common/tascaReadOnly.jsp"/>

	<h3 class="titol-tab titol-documents-tasca">
		Documents de la tasca
	</h3>

	<c:forEach var="document" items="${tasca.documents}">
		<c:set var="variableDoc" value="${null}"/>
		<c:forEach var="var" items="${tasca.varsDocuments}">
			<c:if test="${document.document.codi == var.key}"><c:set var="variableDoc" value="${var.value}"/></c:if>
		</c:forEach>
		<div class="missatgesDocumentGris">
			<h4 class="titol-missatge">
				<c:if test="${document.required}"><img src="<c:url value="/img/bullet_red.png"/>" alt="Document obligatori" title="Document obligatori" border="0"/></c:if>
				${document.document.nom}&nbsp;&nbsp;
				<c:if test="${tasca.validada}">
					<c:if test="${not empty document.document.arxiuNom and not document.readOnly}">
						<a href="<c:url value="/tasca/documentGenerar.html"><c:param name="id" value="${tasca.id}"/><c:param name="documentId" value="${document.document.id}"/></c:url>"><img src="<c:url value="/img/page_white_star.png"/>" alt="Generar" title="Generar" border="0"/></a>
					</c:if>
					<c:if test="${not empty variableDoc}">
						<a href="<c:url value="/tasca/documentDescarregar.html"><c:param name="id" value="${tasca.id}"/><c:param name="codi" value="${document.document.codi}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a>
						<c:if test="${not document.readOnly}">
							<a href="<c:url value="/tasca/documentEsborrar.html"><c:param name="id" value="${tasca.id}"/><c:param name="codi" value="${document.document.codi}"/></c:url>"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
						</c:if>
					</c:if>
				</c:if>
			</h4>
			<c:if test="${tasca.validada}">
				<c:if test="${empty variableDoc}">
					<form:form action="documentGuardar.html" cssClass="uniForm" commandName="documentCommand_${document.document.codi}" enctype="multipart/form-data">
						<fieldset class="inlineLabels">
							<input type="hidden" name="id" value="${tasca.id}">
							<input type="hidden" name="codi" value="${document.document.codi}">
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="contingut"/>
								<c:param name="type" value="file"/>
								<c:param name="fileUrl"><c:url value="/definicioProces/documentDownload.html"><c:param name="definicioProcesId" value="${tasca.definicioProces.id}"/><c:param name="id" value="${document.id}"/></c:url></c:param>
								<c:param name="fileExists" value="${false}"/>
								<c:param name="label">Document</c:param>
							</c:import>
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="data"/>
								<c:param name="type" value="date"/>
								<c:param name="label">Data</c:param>
							</c:import>
						</fieldset>
						<c:import url="../common/formElement.jsp">
							<c:param name="type" value="buttons"/>
							<c:param name="values">submit</c:param>
							<c:param name="titles">Guardar</c:param>
						</c:import>
					</form:form>
				</c:if>
			</c:if>
		</div>
	</c:forEach>
	
	<p class="aclaracio">Els documents marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Document obligatori" title="Document obligatori" border="0"/> són obligatoris</p>

	<br/><c:import url="../common/tramitacioTasca.jsp">
		<c:param name="pipella" value="documents"/>
	</c:import>

</body>
</html>
