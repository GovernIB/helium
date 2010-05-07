<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
	<title>Inici</title>
</head>
<body>

	<c:if test="${not empty entornActual}">
		<c:choose>
			<c:when test="${fn:length(personaLlistat) > 0 || fn:length(grupLlistat) > 0}">
				<div class="missatgesGris">
					<h4 class="titol-missatge">
						<img src="<c:url value="/img/application_form.png"/>" alt="Tasques" title="Tasques" border="0" style="position:relative;top:3px;"/>
						Tasques pendents
					</h4>
					<c:if test="${fn:length(personaLlistat) > 0}">
						<br/>
						<p style="padding-left: 18px">(${fn:length(personaLlistat)}) <a href="<c:url value="/tasca/personaLlistat.html"/>" title="Anar a tasques personals">tasques personals</a> pendents</p>
					</c:if>
					<c:if test="${fn:length(grupLlistat) > 0}">
						<br/>
						<p style="padding-left: 18px">(${fn:length(grupLlistat)}) <a href="<c:url value="/tasca/grupLlistat.html"/>" title="Anar a tasques de grup">tasques de grup</a> pendents</p>
					</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<div class="missatgesOk">
					<h4 class="titol-missatge">No té cap tasca pendent</h4>
				</div>
			</c:otherwise>
		</c:choose>
	</c:if>
	<c:if test="${empty entornActual and not empty entornsActius}">
		<c:forEach var="entorn" items="${entornsActius}">
			<c:set var="tasquesPersona" value="${tasquesPersonaEntorn[entorn]}"/>
			<c:set var="tasquesGrup" value="${tasquesGrupEntorn[entorn]}"/>
			<div class="missatgesGris">
				<h4 class="titol-missatge">
					<img src="<c:url value="/img/application_form.png"/>" alt="Tasques" title="Tasques" border="0" style="position:relative;top:3px;"/>
					${entorn.nom}: tasques pendents
				</h4>
				<c:if test="${fn:length(tasquesPersona) > 0}">
					<br/>
					<p style="padding-left: 18px">(${fn:length(tasquesPersona)}) <a href="<c:url value="/tasca/personaLlistat.html"><c:param name="entornCanviarAmbId" value="${entorn.id}"/></c:url>" title="Anar a tasques personals">tasques personals</a> pendents</p>
				</c:if>
				<c:if test="${fn:length(tasquesGrup) > 0}">
					<br/>
					<p style="padding-left: 18px">(${fn:length(tasquesGrup)}) <a href="<c:url value="/tasca/grupLlistat.html"><c:param name="entornCanviarAmbId" value="${entorn.id}"/></c:url>" title="Anar a tasques de grup">tasques de grup</a> pendents</p>
				</c:if>
				<c:if test="${fn:length(tasquesPersona) == 0 && fn:length(tasquesGrup) == 0}">
					<br/>
					<p style="padding-left: 18px"><img src="<c:url value="/img/tick.png"/>" alt="Ok" title="Ok" border="0" style="position:relative;top:3px;"/> Cap tasca pendent</p>
				</c:if>
			</div>
		</c:forEach>
	</c:if>
	<c:if test="${empty entornActual and empty entornsActius}">
		<div class="missatgesWarn">
			<h4 class="titol-missatge">No té accés a cap entorn</h4>
		</div>
	</c:if>

</body>
</html>
