<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Expedient: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmarSuspendre(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu suspendre aquesta tasca?");
}
function confirmarReprendre(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu reprendre aquesta tasca?");
}
function confirmarCancelar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu cancel·lar aquesta tasca? Aquesta acció no es podrà desfer.");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="tasques"/>
	</c:import>

	<h3 class="titol-tab titol-dades-tasques">
		Tasques del procés
	</h3>
	<c:set var="cont" value="0"/>
	<display:table name="tasques" id="registre" class="displaytag">
		<display:column property="id" title="Id"/>
		<display:column title="Títol">
			<c:if test="${registre.delegada and not registre.delegacioOriginal}"><img src="<c:url value="/img/note_go.png"/>" title="Tasca assignada" alt="Tasca assignada"/></c:if>
			${registre.nom}
			<c:if test="${registre.delegada and registre.delegacioOriginal}"><img src="<c:url value="/img/note_go.png"/>" title="Tasca delegada" alt="Tasca delegada"/></c:if>
		</display:column>
		<display:column property="createTime" title="Creada el" format="{0,date,dd/MM/yyyy HH:mm}"/>
		<display:column property="startTime" title="Iniciada el" format="{0,date,dd/MM/yyyy HH:mm}"/>
		<display:column property="endTime" title="Finalitzada el" format="{0,date,dd/MM/yyyy HH:mm}"/>
		<display:column title="Prioritat">
			<c:choose>
				<c:when test="${registre.priority == 2}">Molt alta</c:when>
				<c:when test="${registre.priority == 1}">Alta</c:when>
				<c:when test="${registre.priority == 0}">Normal</c:when>
				<c:when test="${registre.priority == -1}">Baixa</c:when>
				<c:when test="${registre.priority == -2}">Molt baixa</c:when>
				<c:otherwise>${registre.priority}</c:otherwise>
			</c:choose>
		</display:column>
		<display:column property="dueDate" title="Data límit" format="{0,date,dd/MM/yyyy HH:mm}"/>
		<display:column title="Responsable">
			<c:choose>
				<c:when test="${empty registre.assignee}">
					<c:forEach var="actor" items="${registre.pooledActors}" varStatus="status">
						${registre.personesMap[actor].nomSencer}<c:if test="${not status.last}">, </c:if>
					</c:forEach>
				</c:when>
				<c:otherwise>${registre.personesMap[registre.assignee].nomSencer}</c:otherwise>
			</c:choose>
		</display:column>
		<display:column title="Flags">
			<c:if test="${registre.cancelled}">C</c:if>
			<c:if test="${registre.suspended}">S</c:if>
			<c:if test="${expedientLogIds[cont][1] eq 'RETROCEDIT_TASQUES'}">R</c:if>
		</display:column>
		<display:column>
			<c:if test="${registre.open}">
				<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
					<a href="<c:url value="/expedient/tascaReassignar.html"><c:param name="id" value="${param.id}"/><c:param name="taskId" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/user_go.png"/>" alt="Reassignar" title="Reassignar" border="0"/></a>
				</security:accesscontrollist>
			</c:if>
		</display:column>
		<display:column>
			<c:if test="${registre.open}">
				<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
					<c:choose>
						<c:when test="${not registre.suspended}">
							<a href="<c:url value="/expedient/tascaSuspendre.html"><c:param name="id" value="${param.id}"/><c:param name="taskId" value="${registre.id}"/></c:url>" onclick="return confirmarSuspendre(event)"><img src="<c:url value="/img/control_pause_blue.png"/>" alt="Suspendre" title="Suspendre" border="0"/></a>
						</c:when>
						<c:otherwise>
							<a href="<c:url value="/expedient/tascaReprendre.html"><c:param name="id" value="${param.id}"/><c:param name="taskId" value="${registre.id}"/></c:url>" onclick="return confirmarReprendre(event)"><img src="<c:url value="/img/control_play_blue.png"/>" alt="Reprendre" title="Reprendre" border="0"/></a>
						</c:otherwise>
					</c:choose>
				</security:accesscontrollist>
			</c:if>
		</display:column>
		<display:column>
			<c:if test="${registre.open}">
				<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
					<a href="<c:url value="/expedient/tascaCancelar.html"><c:param name="id" value="${param.id}"/><c:param name="taskId" value="${registre.id}"/></c:url>" onclick="return confirmarCancelar(event)"><img src="<c:url value="/img/stop.png"/>" alt="Cancel·lar" title="Cancel·lar" border="0"/></a>
				</security:accesscontrollist>
			</c:if>
		</display:column>
		<display:column>
			<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="128">
				<c:if test="${((not registre.cancelled) and (not registre.suspended) and ( expedientLogIds[cont][1] ne 'RETROCEDIT_TASQUES') and expedientLogIds[cont][0] !=null)}"> 
						<a href="<c:url value="/expedient/retrocedir.html"><c:param name="id" value="${param.id}"/><c:param name="logId" value="${expedientLogIds[cont][0]}"/><c:param name="tipus_retroces" value="${param.tipus_retroces}"/><c:param name="retorn" value="t"/></c:url>" onclick="return confirmarRetrocedir(event)" class="retroces"><img src="<c:url value="/img/arrow_undo.png"/>" alt="<fmt:message key="expedient.log.retrocedir"/>" title="<fmt:message key="expedient.log.retrocedir"/>" border="0"/></a>
				</c:if>
			</security:accesscontrollist>
		</display:column>
		<c:set var="cont" value="${cont + 1}"/>
	</display:table>

	<p align="right" class="aclaracio">Llegenda dels flags: [C] Tasca cancel·lada, [S] Tasca suspesa, [R] Tasca retrocedida</p>

</body>
</html>
