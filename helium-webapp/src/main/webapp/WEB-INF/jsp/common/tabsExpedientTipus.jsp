<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<ul id="tabnav">
	<c:set var="tePermisGestioTipusExpedient" value="${false}"/>
	<security:accesscontrollist domainObject="${expedientTipus}" hasPermission="16,32,256">
		<c:set var="tePermisGestioTipusExpedient" value="${true}"/>
	</security:accesscontrollist>
	<c:set var="tePermisGestionar" value="${false}"/>
	<security:accesscontrollist domainObject="${expedientTipus}" hasPermission="16,32">
		<c:set var="tePermisGestionar" value="${true}"/>
	</security:accesscontrollist>
	<c:set var="tePermisDissenyTipusExpedient" value="${false}"/>
	<security:accesscontrollist domainObject="${expedientTipus}" hasPermission="16,32">
		<c:set var="tePermisDissenyTipusExpedient" value="${true}"/>
	</security:accesscontrollist>
	<c:set var="tePermisDissenyEntorn" value="${false}"/>
	<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,32">
		<c:set var="tePermisDissenyEntorn" value="${true}"/>
	</security:accesscontrollist>
	<c:if test="${tePermisGestioTipusExpedient || tePermisDissenyTipusExpedient || tePermisDissenyEntorn}">
		<li<c:if test="${param.tabActiu == 'info'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/info.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>"><fmt:message key='common.tabsexptipus.informacio' /></a></li>
	</c:if>
	<c:if test="${tePermisDissenyTipusExpedient || tePermisDissenyEntorn}">
		<li<c:if test="${param.tabActiu == 'estats'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/estats.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>"><fmt:message key='common.tabsexptipus.estats' /></a></li>
		<li<c:if test="${param.tabActiu == 'defprocs'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/definicioProcesLlistat.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>"><fmt:message key='common.tabsexptipus.definicions' /></a></li>
		<li<c:if test="${param.tabActiu == 'sistra'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/sistra.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>"><fmt:message key='common.tabsexptipus.integ_tram' /></a></li>
		<li<c:if test="${param.tabActiu == 'formext'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/formext.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>"><fmt:message key='common.tabsexptipus.integ_forms' /></a></li>
	</c:if>
	<c:if test="${tePermisGestioTipusExpedient || tePermisDissenyTipusExpedient || tePermisDissenyEntorn}">
		<li<c:if test="${param.tabActiu == 'enum'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/enumeracioLlistat.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>"><fmt:message key='common.tabsexptipus.enumeracions' /></a></li>
		<li<c:if test="${param.tabActiu == 'documents'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/documentLlistat.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>"><fmt:message key='common.tabsexptipus.documents' /></a></li>
	</c:if>
	<c:if test="${tePermisDissenyTipusExpedient || tePermisDissenyEntorn}">
		<li<c:if test="${param.tabActiu == 'dominis'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/dominiLlistat.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>"><fmt:message key='common.tabsexptipus.dominis' /></a></li>
		<li<c:if test="${param.tabActiu == 'consultes'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/consultaLlistat.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>"><fmt:message key='common.tabsexptipus.consultes' /></a></li>
	</c:if>
	<c:if test="${tePermisGestionar}">
		<li<c:if test="${param.tabActiu == 'redireccio'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/redireccioLlistat.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>"><fmt:message key='common.tabsexptipus.redireccio' /></a></li>
	</c:if>
</ul>
