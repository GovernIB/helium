<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Expedient: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
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

	<h3 class="titol-tab titol-terminis"><fmt:message key="expedient.termini.terminis"/></h3>
	<display:table name="terminis" id="registre" class="displaytag">
		<c:set var="iniciat" value=""/>
		<c:forEach var="ini" items="${iniciats}">
			<c:if test="${registre.id == ini.termini.id and empty ini.dataCancelacio}"><c:set var="iniciat" value="${ini}"/></c:if>
		</c:forEach>
		<display:column titleKey="expedient.termini.nom" property="nom"/>
		<display:column titleKey="expedient.termini.durada">
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
		<display:column titleKey="expedient.termini.iniciat.el">
			<c:if test="${not empty iniciat}"><fmt:formatDate value="${iniciat.dataInici}" pattern="dd/MM/yyyy"/></c:if>
		</display:column>
		<display:column titleKey="expedient.termini.aturat.el">
			<c:if test="${not empty iniciat and not empty iniciat.dataAturada}"><fmt:formatDate value="${iniciat.dataAturada}" pattern="dd/MM/yyyy"/></c:if>
		</display:column>
		<display:column titleKey="expedient.termini.data.de.fi">
			<c:if test="${not empty iniciat}"><fmt:formatDate value="${iniciat.dataFi}" pattern="dd/MM/yyyy"/></c:if>
		</display:column>
		<display:column titleKey="expedient.termini.estat">
			<c:choose>
				<c:when test="${empty iniciat}">
					<c:set var="trobat" value="${false}"/>
					<c:forEach var="ini" items="${iniciats}">
						<c:if test="${registre.id == ini.termini.id and not empty ini.dataCancelacio}">
							<fmt:message key="expedient.termini.estat.cancelat"/>
							<c:set var="trobat" value="${true}"/>
						</c:if>
					</c:forEach>
					<c:if test="${not trobat}"><fmt:message key="expedient.termini.estat.pendent"/></c:if>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${not empty iniciat.dataAturada}"><fmt:message key="expedient.termini.estat.aturat"/></c:when>
						<c:otherwise><fmt:message key="expedient.termini.estat.actiu"/></c:otherwise>
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
								<img src="<c:url value="/img/control_play.png"/>" alt="<fmt:message key="expedient.termini.accio.iniciar"/>" title="<fmt:message key="expedient.termini.accio.iniciar"/>" border="0"/>
							</c:when>
							<c:otherwise>
								<a href="<c:url value="/expedient/terminiContinuar.html"><c:param name="id" value="${param.id}"/><c:param name="terminiId" value="${iniciat.id}"/></c:url>" onclick="return confirmarContinuar(event)"><img src="<c:url value="/img/control_play_blue.png"/>" alt="<fmt:message key="expedient.termini.accio.continuar"/>" title="<fmt:message key="expedient.termini.accio.continuar"/>" border="0"/></a>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<a href="<c:url value="/expedient/terminiIniciar.html"><c:param name="id" value="${param.id}"/><c:param name="terminiId" value="${registre.id}"/></c:url>" onclick="return confirmarIniciar(event)"><img src="<c:url value="/img/control_play_blue.png"/>" alt="<fmt:message key="expedient.termini.accio.iniciar"/>" title="<fmt:message key="expedient.termini.accio.iniciar"/>" border="0"/></a>
					</c:otherwise>
				</c:choose>
			</display:column>
			<display:column>
				<c:choose>
					<c:when test="${not registre.manual or empty iniciat or not empty iniciat.dataAturada}">
						<img src="<c:url value="/img/control_pause.png"/>" alt="<fmt:message key="expedient.termini.accio.aturar"/>" title="<fmt:message key="expedient.termini.accio.aturar"/>" border="0"/>
					</c:when>
					<c:otherwise>
						<a href="<c:url value="/expedient/terminiPausar.html"><c:param name="id" value="${param.id}"/><c:param name="terminiId" value="${iniciat.id}"/></c:url>" onclick="return confirmarAturar(event)"><img src="<c:url value="/img/control_pause_blue.png"/>" alt="<fmt:message key="expedient.termini.accio.aturar"/>" title="<fmt:message key="expedient.termini.accio.aturar"/>" border="0"/></a>
					</c:otherwise>
				</c:choose>
			</display:column>
			<display:column>
				<c:choose>
					<c:when test="${empty iniciat}">
						<img src="<c:url value="/img/control_stop.png"/>" alt="<fmt:message key="expedient.termini.accio.cancelar"/>" title="<fmt:message key="expedient.termini.accio.cancelar"/>" border="0"/>
					</c:when>
					<c:otherwise>
						<a href="<c:url value="/expedient/terminiCancelar.html"><c:param name="id" value="${param.id}"/><c:param name="terminiId" value="${iniciat.id}"/></c:url>" onclick="return confirmarCancelar(event)"><img src="<c:url value="/img/control_stop_blue.png"/>" alt="<fmt:message key="expedient.termini.accio.cancelar"/>" title="<fmt:message key="expedient.termini.accio.cancelar"/>" border="0"/></a>
					</c:otherwise>
				</c:choose>
			</display:column>
			<display:column>
				<c:if test="${not empty iniciat}">
					<a href="<c:url value="/expedient/terminiModificar.html"><c:param name="id" value="${param.id}"/><c:param name="terminiId" value="${iniciat.id}"/></c:url>"><img src="<c:url value="/img/page_white_edit.png"/>" alt="<fmt:message key="expedient.termini.accio.modificar"/>" title="<fmt:message key="expedient.termini.accio.modificar"/>" border="0"/></a>
				</c:if>
			</display:column>
		</security:accesscontrollist>
	</display:table>

</body>
</html>
