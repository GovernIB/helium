<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>${tasca.nomLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key="comuns.tasques"/>" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
	function mostrarOcultar(img, objid) {
		var obj = document.getElementById(objid);
		if (obj.style.display=="none") {
			$('#' + objid).slideDown();
			//obj.style.display = "block";
			img.src = '<c:url value="/img/magnifier_zoom_out.png"/>';
		} else {
			$('#' + objid).slideUp();
			//obj.style.display = "none";
			img.src = '<c:url value="/img/magnifier_zoom_in.png"/>';
		}
	}
	function verificarSignatura(element) {
		var amplada = 800;
		var alcada = 600;
		$('<iframe id="verificacio" src="' + element.href + '"/>').dialog({
			title: "<fmt:message key="tasca.doc.verificacio"/>",
			autoOpen: true,
			modal: true,
			autoResize: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	function infoRegistre(docId) {
		var amplada = 600;
		var alcada = 200;
		$('<div>' + $("#registre_" + docId).html() + '</div>').dialog({
			title: "<fmt:message key="tasca.doc.informacio"/>",
			autoOpen: true,
			modal: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	function generarDocumentAmbPlantilla(link, formId, adjuntarAuto) {
		var conf;
		if (adjuntarAuto)
			conf = confirm("<fmt:message key="tasca.doc.generar.auto.confirm"/>");
		else
			conf = true;
		if (conf) {
			var valData = $("#" + formId).find("input[name='data']").val();
			window.location = link.href + "&data=" + valData;
		}
		return false;
	}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsTasca.jsp">
		<c:param name="tabActiu" value="documents"/>
	</c:import>

	<c:if test="${not tasca.validada}">
		<div class="missatgesWarn">
			<p><fmt:message key="tasca.doc.no_es_podran"/></p>
		</div>
	</c:if>
	<c:if test="${tasca.validada and not tasca.documentsComplet}">
		<div class="missatgesWarn">
			<p><fmt:message key="tasca.doc.hi_ha_doc"/></p>
		</div>
	</c:if>

	<c:import url="../common/tascaReadOnly.jsp"/>

	<h3 class="titol-tab titol-documents-tasca">
		<fmt:message key="tasca.doc.docs_tasca"/>
	</h3>

	<c:forEach var="document" items="${tasca.documentsOrdenatsPerMostrarTasca}">
		<c:if test="${not document.readOnly}">
			<c:set var="variableDoc" value="${null}"/>
			<c:forEach var="var" items="${tasca.varsDocuments}">
				<c:if test="${document.document.codi == var.key}"><c:set var="variableDoc" value="${var.value}"/></c:if>
			</c:forEach>
			<div class="missatgesDocumentGris">
				<h4 class="titol-missatge">
					<c:if test="${document.required}"><img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key="tasca.doc.doc_oblig"/>" title="<fmt:message key="tasca.doc.doc_oblig"/>" border="0"/></c:if>
					${document.document.nom}&nbsp;&nbsp;
					<c:if test="${tasca.validada}">
						<c:if test="${not empty document.document.arxiuNom and not document.readOnly}">
							<c:set var="adjuntarAuto" value="${document.document.adjuntarAuto}"/>
							<a href="<c:url value="/tasca/documentGenerar.html"><c:param name="id" value="${tasca.id}"/><c:param name="documentId" value="${document.document.id}"/></c:url>" onclick="return generarDocumentAmbPlantilla(this, 'documentCommand_${document.document.codi}', ${adjuntarAuto})"><img src="<c:url value="/img/page_white_star.png"/>" alt="<fmt:message key="tasca.doc.generar"/>" title="<fmt:message key="tasca.doc.generar"/>" border="0"/></a>
						</c:if>
						<c:if test="${not empty variableDoc}">
							<a href="<c:url value="/document/arxiuMostrar.html"><c:param name="token" value="${variableDoc.tokenSignatura}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="<fmt:message key="comuns.descarregar"/>" title="<fmt:message key="comuns.descarregar"/>" border="0"/></a>
							<c:if test="${not empty seleccioMassiva}">
								<a href="<c:url value="/tasca/documentDescarregarZip.html"><c:param name="id" value="${tasca.id}"/><c:param name="codi" value="${document.document.codi}"/></c:url>"><img src="<c:url value="/img/package_green.png"/>" alt="<fmt:message key="tasca.doc.zip"/>" title="<fmt:message key="tasca.doc.zip"/>" border="0"/></a>
							</c:if>
							<c:if test="${not document.readOnly}">
								<a href="<c:url value="/tasca/documentEsborrar.html"><c:param name="id" value="${tasca.id}"/><c:param name="codi" value="${document.document.codi}"/></c:url>"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key="comuns.esborrar"/>" title="<fmt:message key="comuns.esborrar"/>" border="0"/></a>
							</c:if>
						</c:if>
					</c:if>
				</h4>
				<c:if test="${tasca.validada}">
					<c:choose>
						<c:when test="${empty variableDoc}">
							<form:form action="documentGuardar.html" cssClass="uniForm" commandName="documentCommand_${document.document.codi}" enctype="multipart/form-data">
								<fieldset class="inlineLabels">
									<input type="hidden" name="id" value="${tasca.id}">
									<input type="hidden" name="codi" value="${document.document.codi}">
									<c:import url="../common/formElement.jsp">
										<c:param name="property" value="contingut"/>
										<c:param name="type" value="file"/>
										<c:param name="fileUrl"><c:url value="/definicioProces/documentDownload.html"><c:param name="definicioProcesId" value="${tasca.definicioProces.id}"/><c:param name="id" value="${document.id}"/></c:url></c:param>
										<c:param name="fileExists" value="${false}"/>
										<c:param name="label"><fmt:message key="tasca.doc.document"/></c:param>
									</c:import>
									<c:import url="../common/formElement.jsp">
										<c:param name="property" value="data"/>
										<c:param name="type" value="date"/>
										<c:param name="label"><fmt:message key="tasca.doc.data"/></c:param>
									</c:import>
								</fieldset>
								<c:import url="../common/formElement.jsp">
									<c:param name="type" value="buttons"/>
									<c:param name="values">submit</c:param>
									<c:param name="titles"><fmt:message key="tasca.doc.guardar"/></c:param>
								</c:import>
							</form:form>
						</c:when>
						<c:otherwise>
							<form id="documentCommand_${variableDoc.documentCodi}"><input type="hidden" name="data" value="<fmt:formatDate value="${variableDoc.dataDocument}" pattern="dd/MM/yyyy"/>"/></form>
						</c:otherwise>
					</c:choose>
				</c:if>
			</div>
		</c:if>
	</c:forEach>

	<p class="aclaracio"><fmt:message key="tasca.doc.doc_marcats"/> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key="tasca.doc.doc_oblig"/>" title="<fmt:message key="tasca.doc.doc_oblig"/>" border="0"/> <fmt:message key="comuns.son_oblig"/></p>

	<br/><c:import url="../common/tramitacioTasca.jsp">
		<c:param name="pipella" value="documents"/>
	</c:import>

</body>
</html>
