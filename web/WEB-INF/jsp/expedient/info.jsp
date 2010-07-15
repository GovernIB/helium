<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Expedient: ${expedient.identificador}</title>
	<meta name="titolcmp" content="Consultes"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.DOMWindow.js"/>"></script>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[

function confirmarAccio(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu executar aquesta acció?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="info"/>
	</c:import>

	<h3 class="titol-tab titol-info">Informació de l'expedient</h3>

	<dl class="form-info">
		<c:if test="${not empty expedient.numero}"><dt>Número</dt><dd>${expedient.numero}</dd></c:if>
		<c:if test="${not empty expedient.titol}"><dt>Títol</dt><dd>${expedient.titol}</dd></c:if>
		<c:if test="${empty expedient.numero and empty expedient.titol}"><dt>Identificació</dt><dd>${expedient.identificador}</dd></c:if>
		<dt>Tipus</dt><dd>${expedient.tipus.nom}</dd>
		<dt>Estat</dt><dd>
			<c:if test="${expedient.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
			<c:choose>
				<c:when test="${empty expedient.dataFi}">
					<c:choose><c:when test="${empty expedient.estat}">Iniciat</c:when><c:otherwise>${expedient.estat.nom}</c:otherwise></c:choose>
				</c:when>
				<c:otherwise>Finalitzat</c:otherwise>
			</c:choose>
		</dd>
		<dt>Iniciat per</dt><dd>
			<c:choose>
				<c:when test="${expedient.iniciadorTipus == 'SISTRA'}">Entrada núm. ${expedient.bantelEntradaNum}</c:when>
				<c:otherwise>${expedient.iniciadorPersona.nomSencer}</c:otherwise>
			</c:choose>
		</dd>
		<dt>Iniciat el</dt><dd><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/></dd>
		<c:if test="${not empty expedient.responsablePersona}"><dt>Persona responsable</dt><dd>${expedient.responsablePersona.nomSencer}</dd></c:if>
		<c:if test="${not empty expedient.dataFi}"><dt>Finalitzat el</dt><dd><fmt:formatDate value="${expedient.dataFi}" pattern="dd/MM/yyyy HH:mm"/></dd></c:if>
		<dt>Número identificatiu intern</dt><dd>${expedient.numeroDefault}</dd>
		<dt>Definició de procés</dt>
		<dd>
			${instanciaProces.definicioProces.idPerMostrar}&nbsp;
			<c:if test="${instanciaProces.imatgeDisponible}"><a href="#imatgeProces" class="finestraProces"><img src="<c:url value="/img/map_go.png"/>" alt="Situació actual" title="Situació actual" border="0"/></a></c:if>
		</dd>
	</dl>
	<div style="clear: both"></div>
	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
		<br/>
		<form action="<c:url value="/expedient/editar.html"/>">
			<input type="hidden" name="id" value="${param.id}"/>
			<button type="submit" class="submitButton">Modificar informació</button>
		</form>
	</security:accesscontrollist>

	<c:if test="${not empty instanciaProces.definicioProces.accions}">
		<br/><div class="missatgesGris">
			<h4 class="titol-missatge">Executar acció</h4>
			<c:set var="accionsValor"><c:forEach var="accio" items="${instanciaProces.definicioProces.accions}" varStatus="status">${accio.codi}<c:if test="${not status.last}">,</c:if></c:forEach></c:set>
			<c:set var="accionsNom"><c:forEach var="accio" items="${instanciaProces.definicioProces.accions}" varStatus="status">${accio.nom}<c:if test="${not status.last}">,</c:if></c:forEach></c:set>
			<form action="accio.html" method="post" class="uniForm" onsubmit="return confirmarAccio(event)">
				<input type="hidden" name="id" value="${instanciaProces.id}"/>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">${accionsValor}</c:param>
					<c:param name="titles">${accionsNom}</c:param>
				</c:import>
			</form>
		</div>
	</c:if>

	<c:if test="${instanciaProces.imatgeDisponible}">
		<script type="text/javascript">
			$('.finestraProces').openDOMWindow({
				eventType: 'click',
				width: 742,
				height: 500,
				loader: 1,
				loaderHeight: 32,
				loaderWidth: 32,
				windowPadding: 0,
				draggable: 1});
		</script>
		<div id="imatgeProces" style="display:none">
			<div style="position:absolute">
				<img style="position:absolute;left:0;top:0" src="<c:url value="/expedient/imatgeProces.html"><c:param name="id" value="${instanciaProces.id}"/></c:url>"/><c:forEach var="token" items="${activeTokens}"><div style="position:absolute;left:${token.nodePosX}px;top:${token.nodePosY}px;width:${token.nodeWidth - 4}px;height:${token.nodeHeight - 4}px;border: 2px solid red"></div></c:forEach>
			</div>
		</div>
	</c:if>

</body>
</html>
