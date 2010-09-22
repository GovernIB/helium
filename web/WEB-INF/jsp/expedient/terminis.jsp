<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Expedient: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="Consultes"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmarIniciar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu iniciar aquest termini?");
}
function confirmarAturar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu aturar aquest termini?");
}
function confirmarContinuar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu continuar aquest termini?");
}
function confirmarCancelar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu calcel·lar aquest termini?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="terminis"/>
	</c:import>

	<h3 class="titol-tab titol-terminis">
		Terminis del procés
	</h3>
	<display:table name="terminis" id="registre" class="displaytag">
		<c:set var="iniciat" value=""/>
		<c:forEach var="ini" items="${iniciats}">
			<c:if test="${registre.id == ini.termini.id and empty ini.dataCancelacio}"><c:set var="iniciat" value="${ini}"/></c:if>
		</c:forEach>
		<display:column title="Nom" property="nom"/>
		<display:column title="Durada">
			<c:choose>
				<c:when test="${not empty iniciat}">${iniciat.durada}</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${registre.duradaPredefinida}">${registre.durada}</c:when>
						<c:otherwise>Sense especificar</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</display:column>
		<display:column title="Iniciat el">
			<c:if test="${not empty iniciat}"><fmt:formatDate value="${iniciat.dataInici}" pattern="dd/MM/yyyy"/></c:if>
		</display:column>
		<display:column title="Aturat el">
			<c:if test="${not empty iniciat and not empty iniciat.dataAturada}"><fmt:formatDate value="${iniciat.dataAturada}" pattern="dd/MM/yyyy"/></c:if>
		</display:column>
		<display:column title="Data de fi de termini">
			<c:if test="${not empty iniciat}"><fmt:formatDate value="${iniciat.dataFi}" pattern="dd/MM/yyyy"/></c:if>
		</display:column>
		<display:column title="Estat">
			<c:choose>
				<c:when test="${empty iniciat}">Pendent d'iniciar</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${not empty iniciat.dataAturada}">Aturat</c:when>
						<c:otherwise>Actiu</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</display:column>
		<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
			<display:column>
				<c:choose>
					<c:when test="${not registre.manual or not empty iniciat}">
						<c:choose>
							<c:when test="${not registre.manual or empty iniciat.dataAturada}">
								<img src="<c:url value="/img/control_play.png"/>" alt="Iniciar" title="Iniciar" border="0"/>
							</c:when>
							<c:otherwise>
								<a href="<c:url value="/expedient/terminiContinuar.html"><c:param name="id" value="${param.id}"/><c:param name="terminiId" value="${iniciat.id}"/></c:url>" onclick="return confirmarContinuar(event)"><img src="<c:url value="/img/control_play_blue.png"/>" alt="Continuar" title="Continuar" border="0"/></a>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<a href="<c:url value="/expedient/terminiIniciar.html"><c:param name="id" value="${param.id}"/><c:param name="terminiId" value="${registre.id}"/></c:url>" onclick="return confirmarIniciar(event)"><img src="<c:url value="/img/control_play_blue.png"/>" alt="Iniciar" title="Iniciar" border="0"/></a>
					</c:otherwise>
				</c:choose>
			</display:column>
			<display:column>
				<c:choose>
					<c:when test="${not registre.manual or empty iniciat or not empty iniciat.dataAturada}">
						<img src="<c:url value="/img/control_pause.png"/>" alt="Aturar" title="Aturar" border="0"/>
					</c:when>
					<c:otherwise>
						<a href="<c:url value="/expedient/terminiPausar.html"><c:param name="id" value="${param.id}"/><c:param name="terminiId" value="${iniciat.id}"/></c:url>" onclick="return confirmarAturar(event)"><img src="<c:url value="/img/control_pause_blue.png"/>" alt="Aturar" title="Aturar" border="0"/></a>
					</c:otherwise>
				</c:choose>
			</display:column>
			<display:column>
				<c:choose>
					<c:when test="${empty iniciat}">
						<img src="<c:url value="/img/control_stop.png"/>" alt="Cancel·lar" title="Cancel·lar" border="0"/>
					</c:when>
					<c:otherwise>
						<a href="<c:url value="/expedient/terminiCancelar.html"><c:param name="id" value="${param.id}"/><c:param name="terminiId" value="${iniciat.id}"/></c:url>" onclick="return confirmarCancelar(event)"><img src="<c:url value="/img/control_stop_blue.png"/>" alt="Cancel·lar" title="Cancel·lar" border="0"/></a>
					</c:otherwise>
				</c:choose>
			</display:column>
		</security:accesscontrollist>
	</display:table>

</body>
</html>
