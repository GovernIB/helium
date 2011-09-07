<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
	<title><fmt:message key='index.inici' /></title>
</head>
<body>

	<c:if test="${not empty entornActual}">
		<c:choose>
			<c:when test="${fn:length(personaLlistat) > 0 || fn:length(grupLlistat) > 0}">
				<div class="missatgesGris">
					<h4 class="titol-missatge">
						<img src="<c:url value="/img/application_form.png"/>" alt="<fmt:message key='comuns.tasques' />" title="<fmt:message key='comuns.tasques' />" border="0" style="position:relative;top:3px;"/>
						<fmt:message key='index.tasq_pendents' />
					</h4>
					<c:if test="${fn:length(personaLlistat) > 0}">
						<br/>
						<p style="padding-left: 18px">(${fn:length(personaLlistat)}) <a href="<c:url value="/tasca/personaLlistat.html"/>" title="<fmt:message key='index.anar_t_personals' />"><fmt:message key='index.tasq_personals' /></a> <fmt:message key='index.pendents' /></p>
					</c:if>
					<c:if test="${fn:length(grupLlistat) > 0}">
						<br/>
						<p style="padding-left: 18px">(${fn:length(grupLlistat)}) <a href="<c:url value="/tasca/grupLlistat.html"/>" title="<fmt:message key='index.anar_t_grup' />"><fmt:message key='index.tasq_grup' /></a> <fmt:message key='index.pendents' /></p>
					</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<div class="missatgesOk">
					<h4 class="titol-missatge"><fmt:message key='index.no_te_tasca' /></h4>
				</div>
			</c:otherwise>
		</c:choose>
		<c:if test="${fn:length(alertesLlistat) > 0}">
			<div class="missatgesGris">
				<h4 class="titol-missatge">
					<img src="<c:url value="/img/email.png"/>" alt="<fmt:message key='index.alert_sense' />" title="<fmt:message key='index.alert_sense' />" border="0" style="position:relative;top:3px;"/>
					<fmt:message key='index.alert_pendents' />
				</h4>
				<br/>
				<p style="padding-left: 18px">(${fn:length(alertesLlistat)}) <a href="<c:url value="/alerta/llistat.html"/>" title="<fmt:message key='index.veure_alert' />"><fmt:message key='index.alertes' /></a> <fmt:message key='index.pendents' /></p>
			</div>
		</c:if>
	</c:if>
	<c:if test="${empty entornActual and not empty entornsActius}">
		<c:forEach var="entorn" items="${entornsActius}">
			<c:set var="tasquesPersona" value="${tasquesPersonaEntorn[entorn]}"/>
			<c:set var="tasquesGrup" value="${tasquesGrupEntorn[entorn]}"/>
			<c:set var="alertes" value="${alertesEntorn[entorn]}"/>
			<div class="missatgesGris">
				<h4 class="titol-missatge">
					<img src="<c:url value="/img/application_form.png"/>" alt="<fmt:message key='comuns.tasques' />" title="<fmt:message key='comuns.tasques' />" border="0" style="position:relative;top:3px;"/>
					${entorn.nom}
				</h4>
				<c:if test="${fn:length(tasquesPersona) > 0}">
					<br/>
					<p style="padding-left: 18px">(${fn:length(tasquesPersona)}) <a href="<c:url value="/tasca/personaLlistat.html"><c:param name="entornCanviarAmbId" value="${entorn.id}"/></c:url>" title="<fmt:message key='index.anar_t_personals' />"><fmt:message key='index.tasq_personals' /></a> <fmt:message key='index.pendents' /></p>
				</c:if>
				<c:if test="${fn:length(tasquesGrup) > 0}">
					<br/>
					<p style="padding-left: 18px">(${fn:length(tasquesGrup)}) <a href="<c:url value="/tasca/grupLlistat.html"><c:param name="entornCanviarAmbId" value="${entorn.id}"/></c:url>" title="<fmt:message key='index.anar_t_grup' />"><fmt:message key='index.tasq_grup' /></a> <fmt:message key='index.pendents' /></p>
				</c:if>
				<c:if test="${fn:length(tasquesPersona) == 0 && fn:length(tasquesGrup) == 0}">
					<br/>
					<p style="padding-left: 18px"><img src="<c:url value="/img/tick.png"/>" alt="<fmt:message key='index.ok' />" title="<fmt:message key='index.ok' />" border="0" style="position:relative;top:3px;"/> <fmt:message key='index.cap_tasca' /></p>
				</c:if>
				<c:if test="${fn:length(alertes) > 0}">
					<br/>
					<p style="padding-left: 18px">(${fn:length(alertes)}) <a href="<c:url value="/alerta/llistat.html"><c:param name="entornCanviarAmbId" value="${entorn.id}"/></c:url>"><fmt:message key='index.hi_ha_alert' /></a></p>
				</c:if>
			</div>
		</c:forEach>
	</c:if>
	<c:if test="${empty entornActual and empty entornsActius}">
		<div class="missatgesWarn">
			<h4 class="titol-missatge"><fmt:message key='index.no_acces' /></h4>
		</div>
	</c:if>

</body>
</html>
