<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Definició de procés: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="Disseny"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
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
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar aquest registre?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="info"/>
	</c:import>

	<dl class="form-info">
		<dt>Id</dt><dd>${definicioProces.idPerMostrar}</dd>
		<dt>Codi</dt><dd>${definicioProces.jbpmKey}</dd>
		<dt>Nom</dt><dd>${definicioProces.jbpmName}</dd>
		<dt>Versió</dt><dd>${definicioProces.versio}</dd>
		<c:if test="${not empty definicioProces.etiqueta}"><dt>Etiqueta</dt><dd>${definicioProces.etiqueta}</dd></c:if>
		<c:if test="${not empty definicioProces.expedientTipus}"><dt>Tipus d'expedient</dt><dd>${definicioProces.expedientTipus.nom}</dd></c:if>
		<dt>Creada el</dt><dd><fmt:formatDate value="${definicioProces.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></dd>
		<c:if test="${fn:length(subDefinicionsProces) gt 0}">
			<dt>Subdefinicions de procés</dt>
			<dd>
				<c:forEach var="subdp" items="${subDefinicionsProces}" varStatus="status">${subdp.idPerMostrar}<c:if test="${not status.last}">, </c:if></c:forEach>
			</dd>
		</c:if>
	</dl><br/>

	<div class="missatgesGris">
		<h3 class="titol-tab titol-delegacio">Importació de dades <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="Mostrar/Ocultar" title="Mostrar/Ocultar" border="0" onclick="mostrarOcultar(this,'form-importar')"/></h3>
		<div id="form-importar" style="display:none">
			<form:form action="configurar.html" cssClass="uniForm" enctype="multipart/form-data">
				<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
				<div class="inlineLabels">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="arxiu"/>
						<c:param name="type" value="file"/>
						<c:param name="label">Arxiu exportat</c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="type" value="buttons"/>
						<c:param name="values">submit</c:param>
						<c:param name="titles">Importar</c:param>
					</c:import>
				</div>
			</form:form>
		</div>
	</div>

	<div class="missatgesGris">
		<form action="<c:url value="/definicioProces/export.html"/>" method="post" style="display: inline">
			<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
			<button type="submit" class="submitButton">Exportar aquesta versió</button>
		</form>
		<form action="<c:url value="/definicioProces/delete.html"/>" method="post" onsubmit="return confirmar(event)" style="display: inline">
			<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
			<button type="submit" class="submitButton">Esborrar aquesta versió</button>
		</form>
	</div>

</body>
</html>
