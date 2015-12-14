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
			<c:when test="${countPersonaLlistat > 0 || countGrupLlistat > 0}">
				<div class="missatgesGris">
					<h4 class="titol-missatge">
						<img src="<c:url value="/img/application_form.png"/>" alt="<fmt:message key='comuns.tasques' />" title="<fmt:message key='comuns.tasques' />" border="0" style="position:relative;top:3px;"/>
						<fmt:message key='index.tasq_pendents' />
					</h4>
					<c:if test="${countPersonaLlistat > 0}">
						<br/>
						<p style="padding-left: 18px">(${countPersonaLlistat}) <a href="<c:url value="/tasca/personaLlistat.html"/>" title="<fmt:message key='index.anar_t_personals' />"><fmt:message key='index.tasq_personals' /></a> <fmt:message key='index.pendents' /></p>
					</c:if>
					<c:if test="${countGrupLlistat > 0}">
						<br/>
						<p style="padding-left: 18px">(${countGrupLlistat}) <a href="<c:url value="/tasca/grupLlistat.html"/>" title="<fmt:message key='index.anar_t_grup' />"><fmt:message key='index.tasq_grup' /></a> <fmt:message key='index.pendents' /></p>
					</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<div class="missatgesOk">
					<h4 class="titol-missatge"><fmt:message key='index.no_te_tasca' /></h4>
				</div>
			</c:otherwise>
		</c:choose>
		<c:if test="${alertesCountLlistat > 0}">
			<div class="missatgesGris">
				<h4 class="titol-missatge">
					<img src="<c:url value="/img/email.png"/>" alt="<fmt:message key='index.alert_sense' />" title="<fmt:message key='index.alert_sense' />" border="0" style="position:relative;top:3px;"/>
					<fmt:message key='index.alert_pendents' />
				</h4>
				<br/>
				<p style="padding-left: 18px">(${alertesCountLlistat}) <a href="<c:url value="/alerta/llistat.html"/>" title="<fmt:message key='index.veure_alert' />"><fmt:message key='index.alertes' /></a> <fmt:message key='index.pendents' /></p>
			</div>
		</c:if>
	</c:if>
	<c:if test="${empty entornActual and not empty entornsActius}">
		<c:forEach var="entorn" items="${entornsActius}">
			<c:set var="tasquesPersona" value="${tasquesPersonaEntorn[entorn]}"/>
			<c:set var="tasquesGrup" value="${tasquesGrupEntorn[entorn]}"/>
			<c:set var="alertes" value="${alertesCountEntorn[entorn]}"/>
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
				<c:if test="${alertes > 0}">
					<br/>
					<p style="padding-left: 18px">(${alertes}) <a href="<c:url value="/alerta/llistat.html"><c:param name="entornCanviarAmbId" value="${entorn.id}"/></c:url>"><fmt:message key='index.hi_ha_alert' /></a></p>
				</c:if>
			</div>
		</c:forEach>
	</c:if>
	<c:if test="${empty entornActual and empty entornsActius}">
		<div class="missatgesWarn">
			<h4 class="titol-missatge"><fmt:message key='index.no_acces' /></h4>
		</div>
	</c:if>

	<%--c:if test="${not empty param.showDsStatus}">
<%
try {
	javax.naming.Context initCtx = new javax.naming.InitialContext();
	javax.naming.Context envCtx = (javax.naming.Context)initCtx.lookup("java:comp/env");
	pageContext.setAttribute("heliumDs", envCtx.lookup("jdbc/HeliumDS"));
} catch (Exception ex) {
	ex.printStackTrace();
}
%>
		<br/><br/>
		<div class="missatgesGris">
		<h4 class="titol-missatge">Informació del datasource</h4>
		<ul>
			<li><b>Class del datasource:</b> ${heliumDs.class.name}</li>
			<c:if test="${heliumDs.class.name == 'org.apache.tomcat.dbcp.dbcp.BasicDataSource'}">
			<li><b>Connexions actives:</b> ${heliumDs.numActive}</li>
			<li><b>Connexions en espera:</b> ${heliumDs.numIdle}</li>
			<li><b>Màxim de connexions actives:</b> ${heliumDs.maxActive}</li>
			<li><b>Màxim de connexions en espera:</b> ${heliumDs.maxIdle}</li>
			</c:if>
		</ul>
		</div>
	</c:if--%>

</body>
</html>
