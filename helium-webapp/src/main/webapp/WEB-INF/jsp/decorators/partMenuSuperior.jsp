<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<ul id="menu-superior" class="dropdown dropdown-horizontal">
	<li class="image inici"><a href="<c:url value="/"/>"><fmt:message key='decorators.superior.inici' /></a></li>
	<li class="image entorns"><a href="<c:url value="/entorn/seleccio.html"/>"><fmt:message key='decorators.superior.selec_entorn' /></a>
		 <ul class="llista-entorns">
		 	<c:forEach var="list" items="${entorns}">
				<li>
					<a href="<c:url value="/index.html"><c:param name="entornCanviarAmbId" value="${list.id}"/></c:url>">${list.nom}</a>
				</li>
    		</c:forEach>
    	</ul>		
   	</li>
	<c:if test="${globalProperties['app.persones.actiu']}">
		<li class="image perfil"><a href="<c:url value="/perfil/info.html"/>"><fmt:message key='decorators.superior.meu_perfil' /></a></li>
	</c:if>
	<security:authorize ifAllGranted="ROLE_ADMIN">
		<li class="dir image configuracio"><a href="#" onclick="return false"><fmt:message key='comuns.configuracio' /></a>
		<ul>
			<c:if test="${globalProperties['app.persones.actiu']}">
				<li class="image persones"><a href="<c:url value="/persona/consulta.html"/>"><fmt:message key='decorators.superior.persones' /></a></li>
				<c:if test="${empty globalProperties['app.rols.actiu'] or not globalProperties['app.rols.actiu']}">
					<li class="image rols"><a href="<c:url value="/rol/llistat.html"/>"><fmt:message key='decorators.superior.rols' /></a></li>
				</c:if>
			</c:if>
			<c:if test="${globalProperties['app.rols.actiu']}">
				<li class="image rols"><a href="<c:url value="/rol/llistat.html"/>"><fmt:message key='decorators.superior.rols' /></a></li>
			</c:if>
			<li class="image entorns"><a href="<c:url value="/entorn/llistat.html"/>"><fmt:message key='decorators.superior.entorns' /></a></li>
			<c:if test="${globalProperties['app.jbpm.identity.source'] == 'jbpm'}">
				<li class="image carrec"><a href="<c:url value="/carrec/jbpmConfigurats.html"/>"><fmt:message key='comuns.carrecs' /></a></li>
				<li class="image organitzacio"><a href="<c:url value="/area/jbpmConfigurats.html"/>"><fmt:message key='comuns.arees' /></a></li>
			</c:if>
			<li class="image festius"><a href="<c:url value="/festiu/calendari.html"/>"><fmt:message key='decorators.superior.festius' /></a></li>
			<li class="image reassignar"><a href="<c:url value="/reassignar/llistat.html"/>"><fmt:message key='decorators.superior.reassignacions' /></a></li>
		</ul>
	</li>
	</security:authorize>
</ul>
