<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<c:if test="${not empty entornActual}">
	<div id="page-entorn-title">
		<h2>
			<span>${entornActual.nom}</span> Entorn
			<%--(<security:accesscontrollist domainObject="${entornActual}" hasPermission="1">R</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="2">W</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="4">C</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="8">D</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="16">A</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="32">G</security:accesscontrollist><security:accesscontrollist domainObject="${entornActual}" hasPermission="64">O</security:accesscontrollist>)--%>
		</h2>
		<security:accesscontrollist domainObject="${entornActual}" hasPermission="16">
			<form action="<c:url value="/permisos/entorn.html"/>" class="accio-titol">
				<input name="id" type="hidden" value="${entornActual.id}"/>
				<input name="noa" type="hidden" value="noa"/>
				<button type="submit" class="submitButtonImage"><span class="entorn-permisos">&nbsp;</span>Permisos</button>
			</form>
			<form action="<c:url value="/noaentorn/form.html"/>" class="accio-titol">
				<input name="id" type="hidden" value="${entornActual.id}"/>
				<button type="submit" class="submitButtonImage"><span class="entorn-admin">&nbsp;</span>Modificar</button>
			</form>
		</security:accesscontrollist>
	</div>
	<div id="page-entorn-menu">
		<ul id="menu-entorn" class="dropdown dropdown-horizontal">
			<li class="image tasques"><a href="<c:url value="/tasca/personaLlistat.html"/>">Tasques</a></li>
			<li class="dir image expedient-consultes">
				<a href="#" onclick="return false">Consultes</a>
				<ul>
					<li class="image expedient-consultes"><a href="<c:url value="/expedient/consulta.html"/>">Consulta general</a></li>
					<li class="image expedient-consultes"><a href="<c:url value="/expedient/consultaDisseny.html"/>">Consultes avançades</a></li>
				</ul>
			</li>
			<c:if test="${hiHaTramitsPerIniciar}">
				<li class="image expedient-iniciar"><a href="<c:url value="/expedient/iniciar.html"/>">Nou expedient</a></li>
			</c:if>
			<c:if test="${globalProperties['app.organigrama.actiu']}">
				<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,64">
					<li class="dir image organitzacio">
						<a href="#" onclick="return false">Organització</a>
						<ul>
							<li class="image area"><a href="<c:url value="/area/llistat.html"/>">Àrees</a></li>
							<li class="image area-tipus"><a href="<c:url value="/areaTipus/llistat.html"/>">Tipus d'àrea</a></li>
							<li class="image carrec"><a href="<c:url value="/carrec/llistat.html"/>">Càrrecs</a></li>
						</ul>
					</li>
				</security:accesscontrollist>
			</c:if>
			<c:set var="esDissenyador" value="${false}"/>
			<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,32">
				<li class="dir image disseny">
					<a href="#" onclick="return false">Disseny</a>
					<ul>
						<li class="image desplegar"><a href="<c:url value="/definicioProces/deploy.html"/>">Desplegar arxiu</a></li>
						<li class="image defproc"><a href="<c:url value="/definicioProces/llistat.html"/>">Definicions de proces</a></li>
						<li class="image tipexp"><a href="<c:url value="/expedientTipus/llistat.html"/>">Tipus d'expedient</a></li>
						<li class="image enums"><a href="<c:url value="/enumeracio/llistat.html"/>">Enumeracions</a></li>
						<li class="image fonts"><a href="<c:url value="/domini/llistat.html"/>">Dominis</a></li>
						<li class="image consulta"><a href="<c:url value="/consulta/llistat.html"/>">Consultes</a></li>
					</ul>
				</li>
				<c:set var="esDissenyador" value="${true}"/>
			</security:accesscontrollist>
			<c:if test="${!esDissenyador and potDissenyarExpedientTipus}">
				<li class="dir image disseny">
					<a href="#" onclick="return false">Disseny</a>
					<ul>
						<li class="image tipexp"><a href="<c:url value="/expedientTipus/llistat.html"/>">Tipus d'expedient</a></li>
					</ul>
				</li>
			</c:if>
		</ul>
	</div>
</c:if>
