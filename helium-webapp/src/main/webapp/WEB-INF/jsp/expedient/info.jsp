<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><fmt:message key='common.filtres.expedient' />: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.DOMWindow.js"/>"></script>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmarEsborrarRelacio(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='expedient.info.confirm.relacio.esborrar'/>");
}
function confirmarAccio(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='expedient.info.confirm.accio.executar'/>");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="info"/>
	</c:import>

	<h3 class="titol-tab titol-info"><fmt:message key='expedient.info.informacio' /></h3>

		<c:if test="${not empty expedient.numero}"><dt><fmt:message key='expedient.consulta.numero' /></dt><dd>${expedient.numero}</dd></c:if>
		<c:if test="${not empty expedient.titol}"><dt><fmt:message key='expedient.consulta.titol' /></dt><dd>${expedient.titol}</dd></c:if>
		<c:if test="${empty expedient.numero and empty expedient.titol}"><dt><fmt:message key='expedient.info.identificacio' /></dt><dd>${expedient.identificador}</dd></c:if>
		<dt><fmt:message key='expedient.info.tipus' /></dt><dd>${expedient.tipus.nom}</dd>
		<dt><fmt:message key='expedient.consulta.estat' /></dt><dd>
			<c:if test="${expedient.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="<fmt:message key='expedient.info.aturat' />" title="<fmt:message key='expedient.info.aturat' />" border="0"/></c:if>
			<c:if test="${expedient.anulat}"><img src="<c:url value="/img/delete.png"/>" alt="<fmt:message key='expedient.info.anulat' />" title="<fmt:message key='expedient.info.anulat' />" border="0"/></c:if>
			<c:choose>
				<c:when test="${empty expedient.dataFi}">
					<c:choose><c:when test="${empty expedient.estat}"><fmt:message key='expedient.consulta.iniciat' /></c:when><c:otherwise>${expedient.estat.nom}</c:otherwise></c:choose>
				</c:when>
				<c:otherwise><fmt:message key='expedient.consulta.finalitzat' /></c:otherwise>
			</c:choose>
		</dd>
		<c:choose>
			<c:when test="${expedient.iniciadorTipus == 'SISTRA'}"><dt><fmt:message key='expedient.info.tramit_origen' /></dt><dd>${expedient.bantelEntradaNum}</dd></c:when>
			<c:otherwise><dt><fmt:message key='expedient.info.iniciat_per' /></dt><dd>${expedient.iniciadorPersona.nomSencer}</dd></c:otherwise>
		</c:choose>
		<c:if test="${not empty expedient.registreNumero}"><dt><fmt:message key='expedient.info.numero_registre' /></dt><dd>${expedient.registreNumero}</dd></c:if>
		<c:if test="${not empty expedient.registreData}"><dt><fmt:message key='expedient.info.data_registre' /></dt><dd><fmt:formatDate value="${expedient.registreData}" pattern="dd/MM/yyyy"/></dd></c:if>
		<dt><fmt:message key='expedient.info.iniciat_el' /></dt><dd><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/></dd>
		<c:if test="${not empty expedient.responsablePersona}"><dt><fmt:message key='expedient.info.persona_responsable' /></dt><dd>${expedient.responsablePersona.nomSencer}</dd></c:if>
		<c:if test="${not empty expedient.dataFi}"><dt><fmt:message key='expedient.info.finalitzat_el' /></dt><dd><fmt:formatDate value="${expedient.dataFi}" pattern="dd/MM/yyyy HH:mm"/></dd></c:if>
		<dt><fmt:message key='expedient.info.numero_id_intern' /></dt><dd>${expedient.numeroDefault}</dd>
		<dt><fmt:message key='expedient.info.definicio_proces' /></dt>
		<dd>
			${instanciaProces.definicioProces.idPerMostrar}&nbsp;
			<!--  c:if test="${instanciaProces.imatgeDisponible}"-->
			<a href="#imatgeProces" class="finestraProces"><img src="<c:url value="/img/map_go.png"/>" alt="<fmt:message key='expedient.info.situacio_actual' />" title="<fmt:message key='expedient.info.situacio_actual' />" border="0"/></a>
			<!--/c:if-->
		</dd>
		<c:if test="${globalProperties['app.georef.actiu']}">
			<c:choose>
				<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
					<c:if test="${not empty expedient.geoReferencia}">
						<dt><fmt:message key='comuns.georeferencia' /></dt><dd>${expedient.geoReferencia}</dd>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${not empty expedient.geoPosX and not empty expedient.geoPosY}">
						<dt><fmt:message key='comuns.georeferencia' /></dt><dd>${expedient.geoPosX}, ${expedient.geoPosY}</dd>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:if>
	<div style="clear: both"></div>

	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
		<br/>
		<form action="<c:url value="/expedient/editar.html"/>">
			<input type="hidden" name="id" value="${param.id}"/>
			<button type="submit" class="submitButton"><fmt:message key='comuns.modificar_info' /></button>
		</form>
	</security:accesscontrollist>

	<c:if test="${not empty expedient.relacionats}">
		<br/><div class="missatgesGris">
			<h4 class="titol-missatge"><fmt:message key="expedient.info.relacionats"/></h4>
			<c:set var="relacionats" value="${expedient.relacionats}" scope="request"/>
			<display:table name="relacionats" id="registre" requestURI="" class="displaytag selectable">
				<c:set var="filaStyle" value=""/>
				<c:if test="${registre.anulat}"><c:set var="filaStyle" value="text-decoration:line-through"/></c:if>
				<display:column property="identificador" title="Expedient" url="/expedient/info.html" paramId="id" paramProperty="processInstanceId" style="${filaStyle}"/>
				<display:column property="dataInici" title="Iniciat el" format="{0,date,dd/MM/yyyy HH:mm}" style="${filaStyle}"/>
				<display:column property="tipus.nom" title="Tipus" style="${filaStyle}"/>
				<display:column title="Estat" style="${filaStyle}" sortProperty="estat.nom">
					<c:if test="${registre.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
					<c:choose>
						<c:when test="${empty registre.dataFi}">
							<c:choose><c:when test="${empty registre.estat}"><fmt:message key='expedient.consulta.iniciat' /></c:when><c:otherwise>${registre.estat.nom}</c:otherwise></c:choose>
						</c:when>
						<c:otherwise><fmt:message key='expedient.consulta.finalitzat' /></c:otherwise>
					</c:choose>
				</display:column>
				<display:column>
					<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,8">
						<a href="<c:url value="/expedient/relacioDelete.html"><c:param name="expedientIdOrigen" value="${expedient.id}"/><c:param name="expedientIdDesti" value="${registre.id}"/></c:url>" onclick="return confirmarEsborrarRelacio(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
					</security:accesscontrollist>
				</display:column>
			</display:table>
			<script type="text/javascript">initSelectable();</script>
			<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
				<form action="<c:url value="/expedient/relacionar.html"/>">
					<input type="hidden" name="id" value="${param.id}"/>
					<button type="submit" class="submitButton"><fmt:message key="expedient.info.relacionar.afegir"/></button>
				</form>
			</security:accesscontrollist>
		</div>
	</c:if>
	<c:if test="${empty expedient.relacionats}">
		<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
			<br/><form action="<c:url value="/expedient/relacionar.html"/>">
				<input type="hidden" name="id" value="${param.id}"/>
				<button type="submit" class="submitButton"><fmt:message key="expedient.info.relacionar.afegir"/></button>
			</form>
		</security:accesscontrollist>
	</c:if>

	<c:set var="hiHaAccions" value="${false}"/>
	<c:set var="hiHaAccionsPubliques" value="${false}"/>
	<c:forEach var="accio" items="${instanciaProces.definicioProces.accions}">
		<c:if test="${not accio.oculta}">
			<c:set var="hiHaAccions" value="${true}"/>
			<c:if test="${accio.publica}"><c:set var="hiHaAccionsPubliques" value="${true}"/></c:if>
		</c:if>
	</c:forEach>
	<c:set var="tePermisAccions" value="${false}"/>
	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2"><c:set var="tePermisAccions" value="${true}"/></security:accesscontrollist>
	<c:if test="${hiHaAccionsPubliques || (hiHaAccions && tePermisAccions)}">
		<br/><div class="missatgesGris">
			<h4 class="titol-missatge"><fmt:message key='expedient.info.executar_accio' /></h4>
			<c:set var="accionsCodi"><c:forEach var="accio" items="${accions}" varStatus="status"><c:if test="${not accio.oculta}"><c:choose><c:when test="${accio.publica}">${accio.codi}<c:if test="${not status.last}">,</c:if></c:when><c:otherwise><c:if test="${tePermisAccions}">${accio.codi}<c:if test="${not status.last}">,</c:if></c:if></c:otherwise></c:choose></c:if></c:forEach></c:set>
			<c:set var="accionsNom"><c:forEach var="accio" items="${accions}" varStatus="status"><c:if test="${not accio.oculta}"><c:choose><c:when test="${accio.publica}">${accio.nom}<c:if test="${not status.last}">,</c:if></c:when><c:otherwise><c:if test="${tePermisAccions}">${accio.nom}<c:if test="${not status.last}">,</c:if></c:if></c:otherwise></c:choose></c:if></c:forEach></c:set>
			<form action="accio.html" method="post" class="uniForm" onsubmit="return confirmarAccio(event)">
				<input type="hidden" name="id" value="${instanciaProces.id}"/>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">${accionsCodi}</c:param>
					<c:param name="titles">${accionsNom}</c:param>
				</c:import>
			</form>
		</div>
	</c:if>
	<!--c:if test="${instanciaProces.imatgeDisponible}"-->
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
	<!--/c:if-->
	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
		<script type="text/javascript">
			$('.accioRelacionar').openDOMWindow({
				eventType: 'click',
				width: 800,
				height: 200,
				loader: 1,
				loaderHeight: 32,
				loaderWidth: 32,
				windowPadding: 0,
				draggable: 1});
		</script>
		<div id="formulariRelacionar" style="display:none">
			<form:form action="relacionar.html" cssClass="uniForm" commandName="relacionarCommand" onsubmit="return confirmarReprendre(event)">
				<div class="inlineLabels">
					<input type="hidden" name="instanciaProcesId" value="${param.id}"/>
					<input type="hidden" name="expedientIdOrigen" value="${expedient.id}"/>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="expedientIdDesti"/>
						<c:param name="type" value="suggest"/>
						<c:param name="label"><fmt:message key="expedient.info.relacionar.amb"/></c:param>
						<c:param name="suggestUrl"><c:url value="/expedient/suggest.html"/></c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="type" value="buttons"/>
						<c:param name="values">submit</c:param>
						<c:param name="titles"><fmt:message key="expedient.info.relacionar"/></c:param>
					</c:import>
				</div>
			</form:form>
		</div>
	</security:accesscontrollist>

</body>
</html>
