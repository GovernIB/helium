<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Expedient: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="Consultes"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/ui/ui.core.js"/>"></script>
	<script  type="text/javascript" src="<c:url value="/js/jquery/ui/jquery-ui-1.7.2.custom.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
function mostrarOcultar(img, objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		obj.style.display = "block";
		img.src = '<c:url value="/img/magnifier_zoom_out.png"/>';
	} else {
		obj.style.display = "none";
		img.src = '<c:url value="/img/magnifier_zoom_in.png"/>';
	}
}
function confirmarEsborrarProces(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquest document del procés?");
}
function confirmarEsborrarTasca(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquest document de la tasca?");
}
function confirmarModificar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Es podran modificar documents ja introduïts. Voleu continuar?");
}
function confirmarEsborrarSignatura(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar TOTES les signatures d'aquest document?");
}
function verificarSignatura(element) {
	var amplada = 800;
	var alcada = 600;
	$('<iframe id="verificacio" src="' + element.href + '"/>').dialog({
		title: "Verificació de signatures",
		autoOpen: true,
		modal: true,
		autoResize: true,
		width: parseInt(amplada),
		height: parseInt(alcada)
	}).width(amplada - 30).height(alcada - 30);
	return false;
}
function infoRegistre(docId) {
	var amplada = 500;
	var alcada = 200;
	$('<div>' + $("#registre_" + docId).html() + '</div>').dialog({
		title: "Informació de registre",
		autoOpen: true,
		modal: true,
		width: parseInt(amplada),
		height: parseInt(alcada)
	}).width(amplada - 30).height(alcada - 30);
	return false;
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="documents"/>
	</c:import>

	<h3 class="titol-tab titol-documents-expedient amb-lupa">
		Documents del procés
	</h3>
	<c:if test="${not empty instanciaProces.sortedDocumentKeys}">
		<display:table name="instanciaProces.sortedDocumentKeys" id="codi" class="displaytag">
			<display:column title="Document">
				<c:choose>
					<c:when test="${instanciaProces.varsDocuments[codi].adjunt}">
						${instanciaProces.varsDocuments[codi].adjuntTitol}
						<img src="<c:url value="/img/attach.png"/>" alt="Document adjunt" title="Document adjunt" border="0"/>
					</c:when>
					<c:otherwise>
						<c:forEach var="document" items="${instanciaProces.documents}">
							<c:if test="${document.codi == codi}">${document.nom}</c:if>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</display:column>
			<display:column title="Data">
				<fmt:formatDate value="${instanciaProces.varsDocuments[codi].dataDocument}" pattern="dd/MM/yyyy"/>
			</display:column>
			<display:column>
				<c:choose>
					<c:when test="${instanciaProces.varsDocuments[codi].adjunt}">
						<a href="<c:url value="/expedient/documentDescarregar.html"><c:param name="processInstanceId" value="${instanciaProces.id}"/><c:param name="docId" value="${instanciaProces.varsDocuments[codi].id}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a>
					</c:when>
					<c:otherwise>
						<c:if test="${not empty instanciaProces.varsDocuments[codi]}">
							<c:set var="documentActual" value="${instanciaProces.varsDocuments[codi]}" scope="request"/>
							<c:import url="../common/iconesConsultaDocument.jsp"/>
						</c:if>
						<c:if test="${instanciaProces.varsDocuments[codi].registrat}">
							<img src="<c:url value="/img/book_open.png"/>" alt="Registrat" title="Registrat" border="0" style="cursor:pointer" onclick="infoRegistre(${instanciaProces.varsDocuments[codi].id})"/>
						</c:if>
					</c:otherwise>
				</c:choose>
			</display:column>
			<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
				<display:column>
					<c:if test="${not instanciaProces.varsDocuments[codi].signat}">
						<a href="<c:url value="/expedient/documentModificar.html"><c:param name="id" value="${instanciaProces.id}"/><c:param name="docId" value="${instanciaProces.varsDocuments[codi].id}"/></c:url>" onclick="return confirmarModificar(event)"><img src="<c:url value="/img/page_white_edit.png"/>" alt="Editar" title="Editar" border="0"/></a>
					</c:if>
				</display:column>
				<display:column>
					<c:choose>
						<c:when test="${instanciaProces.varsDocuments[codi].signat}">
							<a href="<c:url value="/expedient/signaturaEsborrar.html"><c:param name="processInstanceId" value="${instanciaProces.id}"/><c:param name="docId" value="${instanciaProces.varsDocuments[codi].id}"/></c:url>" onclick="return confirmarEsborrarSignatura(event)"><img src="<c:url value="/img/rosette_delete.png"/>" alt="Esborrar signatura" title="Esborrar signatura" border="0"/></a>
						</c:when>
						<c:otherwise>
							<a href="<c:url value="/expedient/documentEsborrar.html"><c:param name="id" value="${instanciaProces.id}"/><c:param name="docId" value="${instanciaProces.varsDocuments[codi].id}"/></c:url>" onclick="return confirmarEsborrarProces(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar" title="Esborrar" border="0"/></a>
						</c:otherwise>
					</c:choose>
				</display:column>
			</security:accesscontrollist>
		</display:table><br/>
	</c:if>

	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
		<form action="<c:url value="/expedient/documentAdjuntForm.html"/>">
			<input type="hidden" name="id" value="${instanciaProces.id}"/>
			<button class="submitButtonImage" type="submit">
				<span class="nova-variable"></span>Adjuntar document al procés
			</button>
		</form>
	</security:accesscontrollist>

	<c:forEach var="reg" items="${instanciaProces.varsDocuments}">
		<c:set var="document" value="${reg.value}"/>
		<c:if test="${document.registrat}">
			<div id="registre_${document.id}" style="display:none">
				<dl class="form-info">
					<dt>Oficina</dt><dd>${document.registreOficinaNom}</dd>
					<dt>Data</dt><dd><fmt:formatDate value="${document.registreData}" pattern="dd/MM/yyyy HH:mm"/></dd>
					<dt>Tipus</dt><dd><c:choose><c:when test="${document.registreEntrada}">Entrada</c:when><c:otherwise>Sortida</c:otherwise></c:choose></dd>
					<dt>Número</dt><dd>${document.registreNumero}/${document.registreAny}</dd>
				</dl>
			</div>
		</c:if>
	</c:forEach>

</body>
</html>
