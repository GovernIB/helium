<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<ul id="menu-superior" class="dropdown dropdown-horizontal">
	<li class="image inici"><a href="<c:url value="/"/>">Inici</a></li>
	<li class="image entorns"><a href="<c:url value="/entorn/seleccio.html"/>">Seleccionar entorn</a></li>
	<c:if test="${globalProperties['app.persones.actiu']}">
		<li class="image perfil"><a href="<c:url value="/perfil/info.html"/>">El meu perfil</a></li>
	</c:if>
	<security:authorize ifAllGranted="ROLE_ADMIN">
		<li class="dir image configuracio"><a href="#" onclick="return false">Configuració</a>
		<ul>
			<c:if test="${globalProperties['app.persones.actiu']}">
				<li class="image persones"><a href="<c:url value="/persona/llistat.html"/>">Persones</a></li>
				<li class="image rols"><a href="<c:url value="/rol/llistat.html"/>">Rols</a></li>
			</c:if>
			<li class="image entorns"><a href="<c:url value="/entorn/llistat.html"/>">Entorns</a></li>
			<c:if test="${globalProperties['app.jbpm.identity.source'] == 'jbpm'}">
				<li class="image carrec"><a href="<c:url value="/carrec/jbpmConfigurats.html"/>">Càrrecs</a></li>
			</c:if>
			<li class="image festius"><a href="<c:url value="/festiu/calendari.html"/>">Festius</a></li>
		</ul>
	</li>
	</security:authorize>
</ul>
