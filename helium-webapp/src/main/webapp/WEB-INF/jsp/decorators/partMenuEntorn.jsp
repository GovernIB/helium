<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<c:if test="${not empty entornActual}">
	<div id="page-entorn-title">
		<h2>
			<span>${entornActual.nom}</span> <fmt:message key='decorators.entorn.entorn' />
			<c:if test="${hiHaAlertesActives}">
				<a href="<c:url value="/alerta/llistat.html"/>">
					<c:choose>
						<c:when test="${hiHaAlertesNollegides}"><img src="<c:url value="/img/email_blink.gif"/>" alt="Hi ha alertes sense llegir" title="Hi ha alertes sense llegir" border="0"/></c:when>
						<c:otherwise><img src="<c:url value="/img/email.png"/>" alt="<fmt:message key='decorators.entorn.alert_activa' />" title="<fmt:message key='decorators.entorn.alert_activa' />" border="0"/></c:otherwise>
					</c:choose>
				</a>
			</c:if>
			<%--(<security:accesscontrollist domainObject="${entornActual}" hasPermission="1">R</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="2">W</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="4">C</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="8">D</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="16">A</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="32">G</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="64">O</security:accesscontrollist>)--%>
		</h2>
		<security:accesscontrollist domainObject="${entornActual}" hasPermission="16">
			<form action="<c:url value="/permisos/entorn.html"/>" class="accio-titol">
				<input name="id" type="hidden" value="${entornActual.id}"/>
				<input name="noa" type="hidden" value="noa"/>
				<button type="submit" class="submitButtonImage"><span class="entorn-permisos">&nbsp;</span><fmt:message key='decorators.entorn.permisos' /></button>
			</form>
			<form action="<c:url value="/noaentorn/form.html"/>" class="accio-titol">
				<input name="id" type="hidden" value="${entornActual.id}"/>
				<button type="submit" class="submitButtonImage"><span class="entorn-admin">&nbsp;</span><fmt:message key='comuns.modificar' /></button>
			</form>
		</security:accesscontrollist>
	</div>
	<div id="page-entorn-menu">
		<ul id="menu-entorn" class="dropdown dropdown-horizontal">
			<li class="image tasques"><a href="<c:url value="/tasca/personaLlistat.html"/>"><fmt:message key='comuns.tasques' /></a></li>
			<li class="dir image expedient-consultes">
				<a href="#" onclick="return false"><fmt:message key='comuns.consultes' /></a>
				<ul>
					<li class="image expedient-consultes"><a href="<c:url value="/expedient/consulta.html"/>"><fmt:message key='decorators.entorn.cons_general' /></a></li>
					<li class="image expedient-consultes"><a href="<c:url value="/expedient/consultaDisseny.html"/>"><fmt:message key='decorators.entorn.cons_avansades' /></a></li>
				</ul>
			</li>
			<c:if test="${hiHaTramitsPerIniciar}">
				<li class="image expedient-iniciar"><a href="<c:url value="/expedient/iniciar.html"/>"><fmt:message key='decorators.entorn.nou_exp' /></a></li>
			</c:if>
			<c:if test="${globalProperties['app.organigrama.actiu']}">
				<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,64">
					<li class="dir image organitzacio">
						<a href="#" onclick="return false"><fmt:message key='comuns.organitzacio' /></a>
						<ul>
							<li class="image area"><a href="<c:url value="/area/llistat.html"/>"><fmt:message key='comuns.arees' /></a></li>
							<li class="image area-tipus"><a href="<c:url value="/areaTipus/llistat.html"/>"><fmt:message key='decorators.entorn.tipus_area' /></a></li>
							<li class="image carrec"><a href="<c:url value="/carrec/llistat.html"/>"><fmt:message key='comuns.carrecs' /></a></li>
						</ul>
					</li>
				</security:accesscontrollist>
			</c:if>
			<c:set var="esDissenyador" value="${false}"/>
			<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,32">
				<li class="dir image disseny">
					<a href="#" onclick="return false"><fmt:message key='comuns.disseny' /></a>
					<ul>
						<li class="image desplegar"><a href="<c:url value="/definicioProces/deploy.html"/>"><fmt:message key='decorators.entorn.despl_arxiu' /></a></li>
						<li class="image defproc"><a href="<c:url value="/definicioProces/llistat.html"/>"><fmt:message key='decorators.entorn.defs_proces' /></a></li>
						<li class="image tipexp"><a href="<c:url value="/expedientTipus/llistat.html"/>"><fmt:message key='comuns.tipus_exp' /></a></li>
						<li class="image enums"><a href="<c:url value="/enumeracio/llistat.html"/>"><fmt:message key='decorators.entorn.enumeracions' /></a></li>
						<li class="image fonts"><a href="<c:url value="/domini/llistat.html"/>"><fmt:message key='decorators.entorn.dominis' /></a></li>
						<li class="image consulta"><a href="<c:url value="/consulta/llistat.html"/>"><fmt:message key='comuns.consultes' /></a></li>
					</ul>
				</li>
				<c:set var="esDissenyador" value="${true}"/>
			</security:accesscontrollist>
			<c:if test="${!esDissenyador and potDissenyarExpedientTipus}">
				<li class="dir image disseny">
					<a href="#" onclick="return false"><fmt:message key='comuns.disseny' /></a>
					<ul>
						<li class="image tipexp"><a href="<c:url value="/expedientTipus/llistat.html"/>"><fmt:message key='comuns.tipus_exp' /></a></li>
					</ul>
				</li>
			</c:if>
		</ul>
	</div>
</c:if>
