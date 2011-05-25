<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="Disseny"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="info"/>
	</c:import>

	<h3 class="titol-tab titol-info">Informació del tipus d'expedient</h3>
	<dl class="form-info">
		<dt>Codi</dt><dd>${expedientTipus.codi}</dd>
		<dt>Títol</dt><dd>${expedientTipus.nom}</dd>
		<dt>Amb títol?</dt><dd><c:choose><c:when test="${expedientTipus.teTitol}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		<dt>Demana títol?</dt><dd><c:choose><c:when test="${expedientTipus.demanaTitol}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		<dt>Amb número?</dt><dd><c:choose><c:when test="${expedientTipus.teNumero}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		<dt>Demana número?</dt><dd><c:choose><c:when test="${expedientTipus.demanaNumero}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		<c:if test="${not empty expedientTipus.responsableDefecteCodi}"><dt>Responsable per defecte</dt><dd>${responsableDefecte.nomSencer}</dd></c:if>
		<c:if test="${not empty definicioProcesInicial}"><dt>Definició de procés inicial</dt><dd>${definicioProcesInicial.jbpmName}</dd></c:if>
	</dl>
	<div style="clear: both"></div>
	<br/>
	<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,32">
		<form action="<c:url value="/expedientTipus/form.html"/>">
			<input type="hidden" name="id" value="${expedientTipus.id}"/>
			<button type="submit" class="submitButton">Modificar informació</button>
		</form>
	</security:accesscontrollist>

</body>
</html>
